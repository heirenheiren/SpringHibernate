package com.springhibernate.hibernateUtils;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
public class HibernateConfig
{
	@Bean
	public SessionFactory getSessionFactory()
	{
		SessionFactory sessionFactory = new org.hibernate.cfg.Configuration().configure("classpath:hibernate.cfg.xml").buildSessionFactory();
		
		return sessionFactory;
	}
	
	/*@Bean
	public SessionFactory getLocalSessionFactory()
	{
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setConfigLocations("classpath:hibernate.cfg.xml");
		return localSessionFactoryBean.getObject();
	}*/
}
