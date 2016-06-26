package com.keyking.coin.util;

import cn.jpush.api.JPushClient;

public class PushUtil implements Instances{
	private static PushUtil instance = new PushUtil();
	
	JPushClient pushClient;
	
	public static PushUtil getInstance(){
		return instance;
	}
	
	public void init(){
		pushClient = new JPushClient(" fe714706cb46fc30db0c4757 ", "8c6c63ca61aa276290efbbf5");
	}
	
	
}
