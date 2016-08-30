package com.joymeng.http.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.joymeng.http.dao.impl.WebArmyDAO;


public class WebDB {
	
	private static WebDB instance = new WebDB();
	
	public static WebDB getInstance(){
		return instance;
	}
	
	WebArmyDAO armyDao;
	
	public void init() {
		ApplicationContext context = new FileSystemXmlApplicationContext("web/WebContext.xml");
		armyDao = (WebArmyDAO)context.getBean("webDao");
		armyDao.initFields();
	}

	public WebArmyDAO getArmyDao() {
		return armyDao;
	}
	
	
}
