package com.keyking.coin.service.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.keyking.coin.service.dao.impl.AccountApplyDAO;
import com.keyking.coin.service.dao.impl.AdDAO;
import com.keyking.coin.service.dao.impl.BourseDAO;
import com.keyking.coin.service.dao.impl.BrokerDAO;
import com.keyking.coin.service.dao.impl.DealDAO;
import com.keyking.coin.service.dao.impl.DealOrderDAO;
import com.keyking.coin.service.dao.impl.EmailDAO;
import com.keyking.coin.service.dao.impl.FeedBackDAO;
import com.keyking.coin.service.dao.impl.FriendDAO;
import com.keyking.coin.service.dao.impl.MessageDAO;
import com.keyking.coin.service.dao.impl.NoticeDAO;
import com.keyking.coin.service.dao.impl.OtherDAO;
import com.keyking.coin.service.dao.impl.RevertDAO;
import com.keyking.coin.service.dao.impl.TimeLineDAO;
import com.keyking.coin.service.dao.impl.UserBrokerDAO;
import com.keyking.coin.service.dao.impl.UserDAO;

public class DBManager {
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
	BrokerDAO brokerDao = null;
	UserBrokerDAO userBrokerDao = null;
	FeedBackDAO feedBackDao = null;
	NoticeDAO noticeDao = null;
	AdDAO adDao = null;
	BourseDAO bourseDao = null;
	
	private static DBManager instance = new DBManager();
	
	public static DBManager getInstance(){
		return instance;
	}

	public void init() {
		ApplicationContext context = new FileSystemXmlApplicationContext("conf/DB.xml");
		userDao         = (UserDAO)context.getBean("userDao");
		dealDao         = (DealDAO)context.getBean("dealDao");
		revertDao       = (RevertDAO)context.getBean("revertDao");
		dealOrderDao    = (DealOrderDAO)context.getBean("dealOrderDao");
		accountApplyDao = (AccountApplyDAO)context.getBean("accountApplyDao");
		emailDao        = (EmailDAO)context.getBean("emailDao");
		timeDao         = (TimeLineDAO)context.getBean("timeDao");
		friendDao       = (FriendDAO)context.getBean("friendDao");
		messageDao      = (MessageDAO)context.getBean("messageDao");
		otherDao        = (OtherDAO)context.getBean("otherDao");
		noticeDao       = (NoticeDAO)context.getBean("noticeDao");
		brokerDao       = (BrokerDAO)context.getBean("brokerDao");
		userBrokerDao   = (UserBrokerDAO)context.getBean("userBrokerDao");
		feedBackDao     = (FeedBackDAO)context.getBean("feedBackDao");
		adDao           = (AdDAO)context.getBean("adDao");
		bourseDao       = (BourseDAO)context.getBean("bourseDao");
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

	public NoticeDAO getNoticeDao() {
		return noticeDao;
	}

	public BrokerDAO getBrokerDao() {
		return brokerDao;
	}

	public UserBrokerDAO getUserBrokerDao() {
		return userBrokerDao;
	}

	public FeedBackDAO getFeedBackDao() {
		return feedBackDao;
	}

	public AdDAO getAdDao() {
		return adDao;
	}

	public BourseDAO getBourseDao() {
		return bourseDao;
	}
}
 
 
 
