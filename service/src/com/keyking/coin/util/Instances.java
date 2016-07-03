package com.keyking.coin.util;

import com.keyking.coin.service.dao.DBManager;
import com.keyking.coin.service.dao.PrimaryKey;
import com.keyking.coin.service.domain.Controler;
import com.keyking.coin.service.http.HttpServer;
import com.keyking.coin.service.net.handler.ServiceHandler;
import com.keyking.coin.service.system.SystemContext;

public interface Instances {
	
	public static final Controler CTRL = Controler.getInstance();
	
	public static final SMSUtil SMS    = SMSUtil.getInstance();
	
	public static final TokenUtil TOKEN = TokenUtil.getInstance();
	
	public static final ServiceHandler NET = ServiceHandler.getInstance();
	
	public static final DBManager DB = DBManager.getInstance();
	
	public static final PrimaryKey PK = PrimaryKey.getInstance();
	
	public static final HttpServer HTTP = HttpServer.getInstance();
	
	public static final SystemContext SYS_CONTEXT = SystemContext.getInstance();
	
	public static final PushUtil PUSH = PushUtil.getInstance();
	
}
 
