package com.keyking.util;

import com.keyking.http.HttpServer;
import com.keyking.um.Controler;
import com.keyking.um.dao.DBManager;
import com.keyking.um.dao.PrimaryKey;

public interface Instances {
	
	public static final Controler CTRL = Controler.getInstance();
	
	public static final SMSUtil SMS    = SMSUtil.getInstance();
	
	public static final TokenUtil TOKEN = TokenUtil.getInstance();
	
	public static final DBManager DB = DBManager.getInstance();
	
	public static final PrimaryKey PK = PrimaryKey.getInstance();
	
	public static final HttpServer HTTP = HttpServer.getInstance();
	
}
 
