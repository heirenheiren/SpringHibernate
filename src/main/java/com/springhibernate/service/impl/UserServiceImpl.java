package com.springhibernate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.springhibernate.bean.User;
import com.springhibernate.dao.itfc.UserDaoItfc;
import com.springhibernate.dao.itfc.UserRepository;
import com.springhibernate.service.itfc.UserServiceItfc;

public class UserServiceImpl implements UserServiceItfc
{

	@Autowired
	private UserDaoItfc userDao;
	
	@Autowired
    private UserRepository userRepository;
	
	@Override
	public User get(int id)
	{
		return userDao.get(id);
	}

}
