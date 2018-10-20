package com.springhibernate.dao.impl;

import com.springhibernate.bean.User;
import com.springhibernate.dao.itfc.UserDaoItfc;
import com.springhibernate.hibernateUtils.HibernateBaseDao;

/**
 * A data access object (DAO) providing persistence and search support for
 * EchoMessage entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see .User
 * @author MyEclipse Persistence Tools
 */

public class UserDaoImpl extends HibernateBaseDao<User> implements UserDaoItfc
{

}