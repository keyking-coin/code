package com.keyking.chat.db;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DBManager {
	
	private static DBManager instance = new DBManager();
	
	public static DBManager getInstance(){
		return instance;
	}
	
	UserDao userDao = null;
	
	public void init(ServletContext context) throws Exception{
		context.log("try to connect mysql db");
		String path = context.getRealPath("conf");
		ApplicationContext application  = new ClassPathXmlApplicationContext(path);
		userDao = (UserDao) application.getBean("userDao");
	}
	
	public UserDao getUserDao() {
		return userDao;
	}
}
