package com.springhibernate.hibernateUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@SuppressWarnings("deprecation")
@Repository
public abstract class HibernateBaseDao<T> implements IHibernateBaseDao<T>
{
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Class<T> entityClass;

	@Resource
	protected SessionFactory sessionFactory = HibernateSessionFactory.getSessionFactory();

	@SuppressWarnings("unchecked")
	public HibernateBaseDao()
	{
		this.entityClass = getSuperClassGenricType(this.getClass(), 0);
	}

	public static Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException
	{
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType))
		{
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0)
		{
			return Object.class;
		}
		if (!(params[index] instanceof Class))
		{
			return Object.class;
		}
		return (Class) params[index];
	}

	protected void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession()
	{
		return this.sessionFactory.getCurrentSession();
	}
	
	@Override
	public T load(Serializable id)
	{
		Assert.notNull(id, "id is required");
		return (T) this.getSession().load(this.entityClass, id);
	}

	@Override
	public T get(Serializable id)
	{
		Assert.notNull(id, "id is required");
		return (T) this.getSession().get(this.entityClass, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> get(Serializable[] ids)
	{
		Assert.notEmpty(ids, "ids must not be empty");
		String hql = "from " + this.entityClass.getName() + " as model where model.id in(:ids)";
		return this.getSession().createQuery(hql).setParameterList("ids", ids).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(String propertyName, Object value)
	{
		// Assert.hasText(propertyName, "propertyName must not be empty");
		// Assert.notNull(value, "value is required");
		if (value == null)
		{
			return null;
		}
		String hql = "from " + this.entityClass.getName() + " as model where model." + propertyName + " = ?";
		return (T) this.getSession().createQuery(hql).setParameter(0, value).uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getList(String propertyName, Object value)
	{
		Assert.hasText(propertyName, "propertyName must not be empty");
		Assert.notNull(value, "value is required");
		String hql = "from " + this.entityClass.getName() + " as model where model." + propertyName + " = ?";
		return this.getSession().createQuery(hql).setParameter(0, value).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getList(String propertyName, Object[] values)
	{
		Assert.hasText(propertyName, "propertyName must not be empty");
		Assert.notNull(values, "values is required");
		String hql = "from " + this.entityClass.getName() + " as model where model." + propertyName + " in(:values)";
		return this.getSession().createQuery(hql).setParameterList("values", values).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getAll()
	{
		String hql = "from " + this.entityClass.getName();
		return this.getSession().createQuery(hql).list();
	}

	@Override
	public Long getTotalCount()
	{
		String hql = "select count(*) from " + this.entityClass.getName();
		return (Long) this.getSession().createQuery(hql).uniqueResult();
	}

	@Override
	public boolean isUnique(String propertyName, Object oldValue, Object newValue)
	{
		Assert.hasText(propertyName, "propertyName must not be empty");
		Assert.notNull(newValue, "newValue is required");
		if (newValue == oldValue || newValue.equals(oldValue))
		{
			return true;
		}
		if (newValue instanceof String)
		{
			if (oldValue != null && StringUtils.equalsIgnoreCase((String) oldValue, (String) newValue))
			{
				return true;
			}
		}
		T object = this.get(propertyName, newValue);
		return object == null;
	}

	@Override
	public boolean isExist(String propertyName, Object value)
	{
		Assert.hasText(propertyName, "propertyName must not be empty");
		Assert.notNull(value, "value is required");
		T object = this.get(propertyName, value);
		return object != null;
	}

	@Override
	public Serializable save(T entity)
	{
		Assert.notNull(entity, "entity is required");
		return this.getSession().save(entity);
	}

	@Override
	public void update(T entity)
	{
		Assert.notNull(entity, "entity is required");
		this.getSession().update(entity);
	}

	@Override
	public T saveOrUpdate(T o)
	{
		this.getSession().saveOrUpdate(o);
		return o;
	}

	@Override
	public void delete(T entity)
	{
		Assert.notNull(entity, "entity is required");
		this.getSession().delete(entity);
	}

	@Override
	public void delete(Serializable id)
	{
		Assert.notNull(id, "id is required");
		T entity = this.load(id);
		this.getSession().delete(entity);
	}

	@Override
	public void delete(Serializable[] ids)
	{
		Assert.notEmpty(ids, "ids must not be empty");
		for (Serializable id : ids)
		{
			T entity = this.load(id);
			this.getSession().delete(entity);
		}
	}

	@Override
	public void delete(String propertyName, Object value)
	{
		Assert.notNull(propertyName, "propertyName is required");
		Assert.notNull(value, "value is required");
		String hql = "delete from " + this.entityClass.getName() + " as model where model." + propertyName + " = ?";
		this.getSession().createQuery(hql).setParameter(0, value).executeUpdate();
	}

	@Override
	public int delete(Map<String, Object> conditions) throws Exception
	{
		if (null == conditions || conditions.isEmpty())
		{
			throw new Exception("No conditions!");
		}

		StringBuffer hql = new StringBuffer("delete from " + this.entityClass.getName() + " as model ");
		if (null != conditions && conditions.size() > 0)
		{
			hql.append(" where ");

			int i = 1;
			Set<String> keySet = conditions.keySet();
			for (String key : keySet)
			{
				Object value = conditions.get(key);
				if (i > 1)
				{
					hql.append(" AND ");
				}
				if (value instanceof Collection<?> || value instanceof Object[])
				{
					hql.append(" model." + key + " IN(:" + key + ") ");
				}
				else
				{
					hql.append(" model." + key + " = :" + key + " ");
				}
				++i;
			}
		}

		Query createQuery = this.getSession().createQuery(hql.toString());
		createQuery = this.setParameter(createQuery, conditions);
		return createQuery.executeUpdate();
	}

	@Override
	public void evict(Object object)
	{
		Assert.notNull(object, "object is required");
		this.getSession().evict(object);
	}

	@Override
	public void flush()
	{
		this.getSession().flush();
	}

	@Override
	public void clear()
	{
		this.getSession().clear();
	}

	@Override
	@SuppressWarnings("deprecation")
	public Criteria createCriteria(Criterion... criterions)
	{
		Criteria criteria = this.getSession().createCriteria(this.entityClass);
		for (Criterion c : criterions)
		{
			criteria.add(c);
		}
		return criteria;
	}

	@Override
	public Criteria createCriteria(String orderBy, boolean isAsc, Criterion... criterions)
	{
		Criteria criteria = this.createCriteria(criterions);
		if (isAsc)
		{
			criteria.addOrder(Order.asc(orderBy));
		}
		else
		{
			criteria.addOrder(Order.desc(orderBy));
		}

		return criteria;
	}

	@Override
	public List<T> getAllByOrder(String orderBy, boolean isAsc, boolean useCache)
	{
		return this.getLimitByOrder(orderBy, isAsc, -1, useCache);
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation" })
	public List<T> getLimitByOrder(String orderBy, boolean isAsc, int limit, boolean useCache)
	{
		Assert.hasText(orderBy);

		Order order = isAsc ? Order.asc(orderBy) : Order.desc(orderBy);
		Criteria criteria = this.createCriteria();
		if (limit > 0)
		{
			criteria.setMaxResults(limit);
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).addOrder(order).setCacheable(useCache);
		return criteria.list();
	}

	@Override
	public int getRowCount(Criteria criteria)
	{
		criteria.setProjection(Projections.rowCount());
		Long totalRows = (Long) criteria.uniqueResult();
		return totalRows.intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getListByCriteria(Criteria criteria)
	{
		criteria = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getListByCriteria(Criteria criteria, int fistRow, int rowNum, boolean useCache)
	{
		criteria = criteria.setFirstResult(fistRow).setMaxResults(rowNum)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setCacheable(useCache);
		return criteria.list();
	}

	@Override
	public PageFinder<T> pagedByCriteria(Criteria criteria, int pageNo, int pageSize)
	{
		int totalRows = this.getRowCount(criteria);
		criteria.setProjection(null);
		if (totalRows < 1)
		{
			PageFinder<T> finder = new PageFinder<T>(pageNo, pageSize, totalRows);
			finder.setData(new ArrayList<T>());
			return finder;
		}
		else
		{
			PageFinder<T> finder = new PageFinder<T>(pageNo, pageSize, totalRows);
			List<T> list = this.getListByCriteria(criteria, finder.getStartOfPage(), finder.getPageSize(), false);
			finder.setData(list);
			return finder;
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public Query createQuery(String hql, Object... values)
	{
		Assert.hasText(hql, "sql 不能为空");
		Query query = this.getSession().createQuery(hql);
		if (values != null)
		{
			for (int i = 0; i < values.length; i++)
			{
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	@Override
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public Query createQuery(String hql, Map<String, ?> values)
	{
		Assert.hasText(hql, "sql 不能为空");
		Query query = this.createQuery(hql);
		if (values != null)
		{
			query = this.setParameter(query, values);
		}
		return query;
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public T getObjectByHql(String hql, Map<String, Object> values)
	{
		Query query = this.createQuery(hql, values);
		return (T) query.uniqueResult();
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public List<T> getListByHql(String hql, Map<String, Object> values)
	{
		Query query = this.createQuery(hql);
		query = this.setParameter(query, values);
		return query.list();
	}

	@Override
	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	public List<T> getListByHql(String hql, int firstRow, int maxNum, Map<String, Object> values)
	{
		Query query = this.createQuery(hql);
		query = this.setParameter(query, values);
		query.setFirstResult(firstRow);
		query.setMaxResults(maxNum);
		return query.list();
	}

	@Override
	public PageFinder<T> pagedByHQL(String hql, int toPage, int pageSize, Map<String, Object> values)
	{
		String countQueryString = " select count(*) " + this.removeSelect(this.removeOrders(hql));
		List<T> countlist = this.getListByHql(countQueryString, values);
		Long totalCount = (Long) countlist.get(0);

		if (totalCount.intValue() < 1)
		{
			return new PageFinder<T>(toPage, pageSize, totalCount.intValue());
		}
		else
		{
			final PageFinder<T> finder = new PageFinder<T>(toPage, pageSize, totalCount.intValue());
			List<T> list = this.getListByHql(hql, finder.getStartOfPage(), finder.getPageSize(), values);
			finder.setData(list);
			return finder;
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public List getListByHQL(String datasql, Map<String, Object> values)
	{
		Query dataQuery = this.createQuery(datasql, values);
		return dataQuery.list();
	}

	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public List getListByHQL(String datasql, int firstRow, int maxNum, Map<String, Object> values)
	{
		Query dataQuery = this.createQuery(datasql, values);
		dataQuery.setFirstResult(firstRow);
		dataQuery.setMaxResults(maxNum);
		return dataQuery.list();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public PageFinder<Object> pagedObjectByHQL(String countHql, String hql, int toPage, int pageSize,
			Map<String, Object> values)
	{
		Query query = this.createQuery(countHql, values);
		Long totalCount = (Long) query.uniqueResult();
		if (totalCount.intValue() < 1)
		{
			return new PageFinder<Object>(toPage, pageSize, totalCount.intValue());
		}
		else
		{
			PageFinder<Object> finder = new PageFinder<Object>(toPage, pageSize, totalCount.intValue());
			List<Object> list = this.getListByHQL(hql, finder.getStartOfPage(), finder.getPageSize(), values);
			finder.setData(list);
			return finder;
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public T getObjectByHql(String hql, Object... values)
	{
		Query query = this.createQuery(hql, values);
		List<T> list = query.list();
		if (null != list && list.size() > 0)
		{
			T first = list.get(0);
			for (int i = 1; i < list.size(); i++)
			{
				if (list.get(i) != first)
				{
					throw new NonUniqueResultException(list.size());
				}
			}
			return first;
		}
		return null;
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public List<T> getListByHql(String hql, Object... values)
	{
		Query dataQuery = this.createQuery(hql, values);
		return dataQuery.list();
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public List<T> getListByHql(String hql, int firstRow, int maxNum, Object... values)
	{
		Query query = this.createQuery(hql, values);
		query.setFirstResult(firstRow);
		query.setMaxResults(maxNum);
		return query.list();
	}

	@Override
	public PageFinder<T> pagedByHQL(String hql, int toPage, int pageSize, Object... values)
	{
		String countQueryString = " select count(*) " + this.removeSelect(this.removeOrders(hql));
		List<T> countlist = this.getListByHql(countQueryString, values);
		Long totalCount = (Long) countlist.get(0);

		if (totalCount.intValue() < 1)
		{
			return new PageFinder<T>(toPage, pageSize, totalCount.intValue());
		}
		else
		{
			final PageFinder<T> finder = new PageFinder<T>(toPage, pageSize, totalCount.intValue());
			List<T> list = this.getListByHql(hql, finder.getStartOfPage(), finder.getPageSize(), values);
			finder.setData(list);
			return finder;
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public SQLQuery createSQLQuery(String sql, Object... values)
	{
		Assert.hasText(sql, "sql 不能为空");
		SQLQuery query = this.getSession().createSQLQuery(sql);
		if (values != null)
		{
			for (int i = 0; i < values.length; i++)
			{
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public SQLQuery createSQLQuery(String sql, Map<String, ?> values)
	{
		Assert.hasText(sql, "sql 不能为空");
		Query query = this.createSQLQuery(sql);
		if (values != null)
		{
			query = this.setParameter(query, values);
		}
		return (SQLQuery) query;
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public List<Object> getListBySQL(String datasql, Map<String, Object> values)
	{
		SQLQuery dataQuery = this.createSQLQuery(datasql, values);
		return dataQuery.list();
	}

	@Override
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
	public List<Object> getListBySQL(String datasql, int firstRow, int maxNum, Map<String, Object> values)
	{
		SQLQuery dataQuery = this.createSQLQuery(datasql, values);
		dataQuery.setFirstResult(firstRow);
		dataQuery.setMaxResults(maxNum);
		return dataQuery.list();
	}

	@Override
	@SuppressWarnings({ "deprecation", "rawtypes" })
	public PageFinder<Object> pagedObjectBySQL(String countsql, String datasql, int toPage, int pageSize,
			Map<String, Object> values)
	{
		SQLQuery query = this.createSQLQuery(countsql, values);
		Long totalCount = Long.parseLong(query.uniqueResult().toString());
		if (totalCount.intValue() < 1)
		{
			return new PageFinder<Object>(toPage, pageSize, totalCount.intValue());
		}
		else
		{
			PageFinder<Object> finder = new PageFinder<Object>(toPage, pageSize, totalCount.intValue());
			List<Object> list = this.getListBySQL(datasql, finder.getStartOfPage(), finder.getPageSize(), values);
			finder.setData(list);
			return finder;
		}
	}

	/**
	 * 取得对象的主键值,辅助函数.
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	private Serializable getId(Object entity)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		Assert.notNull(entity);
		return (Serializable) PropertyUtils.getProperty(entity, this.getIdName());
	}

	/**
	 * 取得对象的主键名,辅助函数.
	 */
	@SuppressWarnings("deprecation")
	private String getIdName()
	{
		ClassMetadata meta = this.sessionFactory.getClassMetadata(this.entityClass);
		Assert.notNull(meta, "Class " + this.entityClass + " not define in hibernate session factory.");
		String idName = meta.getIdentifierPropertyName();
		Assert.hasText(idName, this.entityClass.getSimpleName() + " has no identifier property define.");
		return idName;
	}

	/**
	 * hql 设置参数
	 * 
	 * @Title: setParameter
	 * @Description: TODO
	 * @param query
	 * @param map
	 * @return
	 * @throws @author:
	 *             yong
	 * @date: 2012-12-17下午05:56:15
	 */
	@SuppressWarnings("deprecation")
	private Query setParameter(Query query, Map<String, ?> map)
	{
		if (map != null && !map.isEmpty())
		{
			Set<String> keySet = map.keySet();
			for (String string : keySet)
			{
				Object obj = map.get(string);
				// 这里考虑传入的参数是什么类型，不同类型使用的方法不同
				if (obj instanceof Collection<?>)
				{
					query.setParameterList(string, (Collection<?>) obj);
				}
				else if (obj instanceof Object[])
				{
					query.setParameterList(string, (Object[]) obj);
				}
				else
				{
					query.setParameter(string, obj);
				}
			}
		}
		return query;
	}

	/**
	 * 去除hql的select 子句，未考虑union的情况用于pagedQuery.
	 * 
	 * @param hql
	 * @return
	 */
	private String removeSelect(String hql)
	{
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 * 
	 * @param hql
	 * @return
	 */
	private String removeOrders(String hql)
	{
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find())
		{
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
}
