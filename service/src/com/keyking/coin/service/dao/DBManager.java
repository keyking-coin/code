package com.keyking.coin.service.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.keyking.coin.service.dao.impl.AccountApplyDAO;
import com.keyking.coin.service.dao.impl.DealDAO;
import com.keyking.coin.service.dao.impl.DealOrderDAO;
import com.keyking.coin.service.dao.impl.EmailDAO;
import com.keyking.coin.service.dao.impl.FriendDAO;
import com.keyking.coin.service.dao.impl.MessageDAO;
import com.keyking.coin.service.dao.impl.OtherDAO;
import com.keyking.coin.service.dao.impl.RevertDAO;
import com.keyking.coin.service.dao.impl.TimeLineDAO;
import com.keyking.coin.service.dao.impl.UserDAO;

public class DBManager {
	
	private static DBManager instance = new DBManager();
	
	public static DBManager getInstance(){
		return instance;
	}
	
	ApplicationContext context = null;
	
	UserDAO userDao = null;
	
	DealDAO dealDao = null;
	
	RevertDAO revertDao = null;
	
	DealOrderDAO dealOrderDao = null;
	
	AccountApplyDAO accountApplyDao = null;
	
	EmailDAO emailDao = null;
	
	TimeLineDAO timeDao = null;
	
	FriendDAO friendDao = null;
	
	MessageDAO messageDao = null;
	
	OtherDAO otherDao = null;
	
	public void init() {
		context         = new FileSystemXmlApplicationContext("conf/DB.xml");
		userDao         = (UserDAO)context.getBean("userDao");
		dealDao         = (DealDAO)context.getBean("dealDao");
		revertDao       = (RevertDAO)context.getBean("revertDao");
		dealOrderDao    = (DealOrderDAO)context.getBean("dealOrderDao");
		accountApplyDao = (AccountApplyDAO)context.getBean("accountApplyDao");
		emailDao        = (EmailDAO)context.getBean("emailDao");
		timeDao         = (TimeLineDAO)context.getBean("timeDao");
		friendDao       = (FriendDAO)context.getBean("friendDao");
		messageDao      = (MessageDAO)context.getBean("messageDao");
		otherDao      = (OtherDAO)context.getBean("otherDao");
	}
	
	public UserDAO getUserDao() {
		return userDao;
	}

	public DealDAO getDealDao() {
		return dealDao;
	}

	public RevertDAO getRevertDao() {
		return revertDao;
	}

	public DealOrderDAO getDealOrderDao() {
		return dealOrderDao;
	}

	public AccountApplyDAO getAccountApplyDao() {
		return accountApplyDao;
	}

	public EmailDAO getEmailDao() {
		return emailDao;
	}

	public TimeLineDAO getTimeDao() {
		return timeDao;
	}

	public FriendDAO getFriendDao() {
		return friendDao;
	}

	public MessageDAO getMessageDao() {
		return messageDao;
	}

	public OtherDAO getOtherDao() {
		return otherDao;
	}
}
 
 
 
