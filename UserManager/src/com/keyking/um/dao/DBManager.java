package com.keyking.um.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.keyking.um.dao.impl.BrokerDAO;
import com.keyking.um.dao.impl.UserBrokerDAO;
import com.keyking.um.dao.impl.UserDAO;

public class DBManager {
	
	private static DBManager instance = new DBManager();
	
	public static DBManager getInstance(){
		return instance;
	}
	
	ApplicationContext context = null;
	
	UserDAO userDao = null;
	BrokerDAO brokerDao = null;
	UserBrokerDAO userBrokerDao = null;
	
	public void init() {
		context         = new FileSystemXmlApplicationContext("./conf/DB.xml");
		userDao         = (UserDAO)context.getBean("userDao");
		brokerDao       = (BrokerDAO)context.getBean("brokerDao");
		userBrokerDao   = (UserBrokerDAO)context.getBean("userBrokerDao");
	}
	
	public UserDAO getUserDao() {
		return userDao;
	}

	public BrokerDAO getBrokerDao() {
		return brokerDao;
	}

	public UserBrokerDAO getUserBrokerDao() {
		return userBrokerDao;
	}
}
 
 
 
