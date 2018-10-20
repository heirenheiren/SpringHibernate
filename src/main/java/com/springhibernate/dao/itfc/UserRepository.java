package com.springhibernate.dao.itfc;

import org.springframework.data.repository.CrudRepository;

import com.springhibernate.bean.User;

public interface UserRepository extends CrudRepository<User,Integer>
{

}
