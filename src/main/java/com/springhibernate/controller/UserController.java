package com.springhibernate.controller;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.springhibernate.bean.Role;
import com.springhibernate.bean.User;
import com.springhibernate.hibernateUtils.HibernateSessionFactory;

public class UserController
{
	// Restrictions.eq equal 等于 ==
	// Restrictions.allEq 参数为Map对象,使用key/value进行多个等于的对比,相当于多个Restrictions.eq的效果
	// Restrictions.gt great than 大于 >
	// Restrictions.ge great equal 大于等于 >=
	// Restrictions.lt less than 小于 <
	// Restrictions.le less equal 小于等于 <=
	// Restrictions.between 对应SQL的between子句
	// Restrictions.like 对应SQL的like子句
	// Restrictions.in 对应SQL的in子句
	// Restrictions.and and 关系
	// Restrictions.or or 关系
	// Restrictions.isNull 判断属性是否为null,为空则返回true
	// Restrictions.sqlRestriction SQL限定的查询
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		onePkOne();
		// oneToMany();
		// manyToOne();
		// secondCache();
		// get();
		// load();
		// save();
		// update();
		// delete();//字段要设置为可空
		// query("test 1");
		// criteria("test 1","1");
		// otherCriteria();
		// order("test 1", 1);
		// matchMode("9999");
	}

	/**
	 * 
	 */

	@SuppressWarnings("unchecked")
	private static void matchMode(String key)
	{
		Session session = HibernateSessionFactory.getSession();
		// Session session = HibernateUtil.currentSession();
		Criteria criteria = session.createCriteria(User.class);
		try
		{
			List<User> list = criteria.add(Restrictions.or(Restrictions.like("username", key, MatchMode.ANYWHERE),
					(Restrictions.like("password", key, MatchMode.ANYWHERE)))).list();

			System.out.println("查询结果：");
			System.out.println("id\t" + "username\t" + "password\t" + "age");
			for (User user : list)
			{
				System.out.println(user.getUserId() + "\t" + user.getUsername() + "\t\t" + user.getPassword() + "\t\t"
						+ user.getAge());
			}
		}
		catch (HibernateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private static void order(String name, int age)
	{
		Session session = null;
		try
		{
			session = HibernateSessionFactory.getSession();
			Criteria c = session.createCriteria(User.class);
			if (name != null)
			{
				c.add(Restrictions.like("username", name, MatchMode.ANYWHERE));
			}
			if (age != 0)
			{
				c.add(Restrictions.eq("age", new Integer(age)));
			}
			// c.addOrder(Order.asc("id"));//根据名字升序排列
			c.addOrder(Order.desc("id"));// 根据id降序排列
			List<User> list = c.list();
			System.out.println("查询结果：");
			System.out.println("id\t" + "username\t" + "password\t" + "age");
			for (User user : list)
			{
				System.out.println(user.getUserId() + "\t" + user.getUsername() + "\t\t" + user.getPassword() + "\t\t"
						+ user.getAge());
			}
		}
		catch (HibernateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static void otherCriteria()
	{
		// TODO Auto-generated method stub
		Session session = null;
		try
		{
			session = HibernateSessionFactory.getSession();

			// 1.查询年龄在20-30岁之间的所有用户对象
			// List<User> list =
			// session.createCriteria(User.class).add(Restrictions.between("age",
			// new Integer(20), new Integer(30))).list();

			// 2.查询学生姓名在AAA,BBB,CCC之间的学生对象
			// String[] names = {"slkd","yaozh","liangjh"};
			// List<User> list =
			// session.createCriteria(User.class).add(Restrictions.in("username",names)).list();

			// 3.查询年龄为空的用户对象
			// List<User> list =
			// session.createCriteria(User.class).add(Restrictions.isNull("age")).list();

			// 4.查询年龄等于20或者年龄为空的学生对象
			// List<User> list =
			// session.createCriteria(User.class).add(Restrictions.or(Restrictions.eq("age",new
			// Integer(12)),Restrictions.isNull("age"))).list();

			// 5.查询某字段包含某字符的用户对象
			List<User> list = session.createCriteria(User.class).add(Restrictions.like("username", "%999%")).list();

			System.out.println("查询结果：");
			System.out.println("id\t" + "username\t" + "password\t" + "age");
			for (User user : list)
			{
				System.out.println(user.getUserId() + "\t" + user.getUsername() + "\t\t" + user.getPassword() + "\t\t"
						+ user.getAge());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static void criteria(String name, String password)
	{
		Session session = null;
		try
		{
			session = HibernateSessionFactory.getSession();

			Criteria c = session.createCriteria(User.class);
			c.add(Restrictions.eq("username", name));// eq是等于，gt是大于，lt是小于,or是或
			c.add(Restrictions.eq("password", password));

			List<User> list = c.list();
			System.out.println("查询结果：");
			System.out.println("id\t" + "username\t" + "password\t" + "age");
			for (User user : list)
			{
				System.out.println(user.getUserId() + "\t" + user.getUsername() + "\t\t" + user.getPassword() + "\t\t"
						+ user.getAge());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private static void query(String name)
	{
		Session session = null;
		try
		{
			session = HibernateSessionFactory.getSession();

			// from后面是对象，不是表名
			String hql = "from User as user where user.username=:name";// 使用命名参数，推荐使用，易读。
			Query query = session.createQuery(hql);
			query.setString("name", name);

			List<User> list = query.list();
			if (list.size() > 0)
			{
				for (User user : list)
				{
					System.out.println("查询结果：");
					System.out.println("id\t" + "username\t" + "password\t" + "age");
					System.out.println(user.getUserId() + "\t" + user.getUsername() + "\t\t" + user.getPassword()
							+ "\t\t" + user.getAge());
				}
			}
			else
			{
				System.out.println(name + "不存在");
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings("unused")
	private static void delete()
	{
		// TODO Auto-generated method stub
		Session session = HibernateSessionFactory.getSession();
		Transaction transaction = session.beginTransaction();
		// User user = new User();
		// user.setAge(1);//id号要存在，否则报错。
		try
		{
			// 1.非批量删除处理
			// session.delete(user);
			// session.flush();
			// 2.批量删除处理
			String hqlDelete = "delete User where age = :age";
			int deletedEntities = session.createQuery(hqlDelete).setInteger("age", 1).executeUpdate();
			System.out.println(deletedEntities);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
	}

	@SuppressWarnings("unused")
	private static void update()
	{
		// TODO Auto-generated method stub
		Session session = HibernateSessionFactory.getSession();
		Transaction transaction = session.beginTransaction();
		User user = new User();
		user.setUsername("asdf");
		user.setPassword("654321");
		user.setAge(12);
		user.setUserId(1);
		try
		{
			session.update(user);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
	}

	@SuppressWarnings("unused")
	private static void save()
	{
		// TODO Auto-generated method stub
		Session session = HibernateSessionFactory.getSession();
		Transaction transaction = session.beginTransaction();
		try
		{
			for (int i = 0; i < 100000; i++)
			{
				User user = new User();
				user.setUsername("test " + i);
				user.setAge((int) (Math.random() * 100) + 1);
				user.setPassword(i + "");
				session.save(user);
				if (i % 20 == 0)
				{// 批量添加的方法
					session.flush();
					session.clear();
				}
			}
			transaction.commit();
		}
		catch (HibernateException e)
		{
			transaction.rollback();
			e.printStackTrace();
		}
		finally
		{
			session.close();
		}
	}

	private static void get()
	{
		Session session = HibernateSessionFactory.getSession();
		try
		{
			User user1 = (User) session.get(User.class, new Integer("1"));
			System.out.println("用户ID：" + user1.getUserId() + "用户名：" + user1.getUsername());
			// 证明一级缓存，user直接读取数据库获取，user1读取缓存获取
			User user2 = (User) session.get(User.class, new Integer("1"));
			System.out.println("用户ID：" + user2.getUserId() + "用户名：" + user2.getUsername());
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			HibernateSessionFactory.closeSession();
		}
	}

	/**
	 * 区别get方法，load涉及延迟加载，load返回对象在被调用时hibernate才会发出SQL语句去查询对象。
	 */
	private static void load()
	{
		Session session = HibernateSessionFactory.getSession();
		try
		{
			User user = (User) session.load(User.class, new Integer("1"));
			System.out.println("用户ID：" + user.getUserId() + "用户名：" + user.getUsername());
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			HibernateSessionFactory.closeSession();
		}
	}

	private static void secondCache()
	{
		Session session1 = HibernateSessionFactory.getSession();
		Session session2 = HibernateSessionFactory.getSession();

		try
		{
			User user1 = (User) session1.load(User.class, new Integer("1"));
			System.out.println("用户ID：" + user1.getUserId() + "用户名：" + user1.getUsername());
			User user2 = (User) session2.load(User.class, new Integer("1"));
			System.out.println("用户ID：" + user2.getUserId() + "用户名：" + user2.getUsername());
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
		finally
		{
			HibernateSessionFactory.closeSession();
		}
	}

	private static void manyToOne()
	{
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		try
		{
			User user = (User) session.get(User.class, new Integer("1"));
			System.out.println("用户ID：" + user.getUserId() + "用户名：" + user.getUsername());
			System.out.println("用户角色：" + user.getRole().getRoleName());
			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		finally
		{
			session.close();
		}
	}

	private static void oneToMany()
	{
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		try
		{
			Role role = (Role) session.get(Role.class, 1);
			System.out.println("角色名称：" + role.getRoleName() + "该角色用户数量" + role.getUsers().size());// 被调用时hibernate才会发出SQL语句去查询对象
			for (Iterator<User> iterator = role.getUsers().iterator(); iterator.hasNext();)
			{
				User user = (User) iterator.next();
				System.out.println("用户名称：" + user.getUsername());
			}
			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		finally
		{
			session.close();
		}
	}

	private static void onePkOne()
	{
		Session session = HibernateSessionFactory.getSession();
		session.beginTransaction();
		try
		{
			User user = (User) session.get(User.class, 1);
			System.out.println("用户ID：" + user.getUserId() + "用户名：" + user.getUsername() + "身份证："
					+ user.getIdcard().getIdcardCode());
			session.getTransaction().commit();
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			session.getTransaction().rollback();
		}
		finally
		{
			session.close();
		}
	}
}
