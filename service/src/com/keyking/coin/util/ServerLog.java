package com.keyking.coin.util;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏日志
 * 
 */
public class ServerLog {
	
	private static Logger logger = null;
	
	public static void init(){
		PropertyConfigurator.configureAndWatch("conf/log4j.properties");
		logger = LoggerFactory.getLogger(ServerLog.class);
	}
	
	public static void error(String str, Throwable throwable) {
		logger.error(str, throwable);
	}	
	
	public static void error(String str) {
		logger.error(str);
	}
	
	public static void info(String info) {
		logger.info(info);
	}
	
	public static void debug(String str) {
		logger.debug(str);
	}
}
 
 
