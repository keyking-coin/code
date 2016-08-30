package com.joymeng.slg.domain.chat.chatdata;

import java.lang.reflect.Field;
import java.util.Properties;

import com.joymeng.slg.world.GameConfig;

public class RedisConfig {
	int maxActive;
	int maxIdle;
	long maxWait;
	boolean testOnBorrow;
	String ip;
	int port;
	int timeOut;
	int useIndex;
	
	public static RedisConfig load(Properties properties) throws Exception{
		RedisConfig config = new RedisConfig();
		Field[] fields = RedisConfig.class.getDeclaredFields();
		for (int i = 0 ; i < fields.length ; i++){
			Field field = fields[i];
			String str = properties.getProperty(field.getName());
			if (str == null) {
				continue;
			}
			field.setAccessible(true);
			GameConfig.loadOneProperty(field,str,config);
		}
		return config;
	}
	
	public int getMaxActive() {
		return maxActive;
	}
	
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	
	public int getMaxIdle() {
		return maxIdle;
	}
	
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	
	public long getMaxWait() {
		return maxWait;
	}
	
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}
	
	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}
	
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getTimeOut() {
		return timeOut;
	}
	
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public int getUseIndex() {
		return useIndex;
	}

	public void setUseIndex(int useIndex) {
		this.useIndex = useIndex;
	}
}
