package com.keyking.chat;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.keyking.chat.db.DBManager;

public class StartInit implements ServletContextListener {
	
	public static ServletContext context = null;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			context = null;
		} catch (Exception e) {
			
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			context = arg0.getServletContext();
			DBManager.getInstance().init(context);
		} catch (Exception e) {
			
		}
	}  
}
 
 
 
