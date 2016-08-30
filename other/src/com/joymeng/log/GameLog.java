package com.joymeng.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gejing
 * 
 */
public class GameLog {
	public static final char SPLIT_CHAR = '|';

	private final static Logger logger = LoggerFactory.getLogger(GameLog.class);

	public static void error(String error, Throwable throwable) {
		logger.error(error, throwable);
	}

	public static void error(Throwable throwable) {
		logger.error("error throwable \t" + throwable.getMessage(), throwable);
	}
	
	public static void error(String error) {
		logger.error(error);
	}
	
	public static void info(String info) {
		logger.info(info);
	}
	
	public static void debug(String deb) {
		logger.debug(deb);
	}

	public static void warn(String warn) {
		logger.warn(warn);
	}
}
