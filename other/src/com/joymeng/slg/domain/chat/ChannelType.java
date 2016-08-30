package com.joymeng.slg.domain.chat;

import com.joymeng.common.util.TextUtils;
public enum ChannelType {
	GENERAL("CHANNEL_TYPE_GENERAL",TextUtils.WHITE),
	WORLD("CHANNEL_TYPE_WORLD",TextUtils.WHITE),
	GUILD("CHANNEL_TYPE_ALLY",TextUtils.WHITE),
	GROUP("CHANNEL_TYPE_GROUP",TextUtils.WHITE),
	SYSTEM_REPORT("CHANNEL_TYPE_REPORT", TextUtils.WHITE),
	MAIL_SYSTEM("CHANNEL_TYPE_MAIL", TextUtils.WHITE),
	SYSTEM_ACTIVE("CHANNEL_TYPE_ACTIVE", TextUtils.WHITE),
	;
	
	String showName;	//显示名字
	String color;		//字体类型
	
	public static int LENGTH = ChannelType.values().length;
	
	private ChannelType(String n,String c) {
		this.showName = n;
		this.color = c;
	}
	
	public String getShowName() {
		return showName;
	}
	
	public static ChannelType valueOf(byte ordinal) {
		if (ordinal >= LENGTH) {
			ordinal = 0;
		}
		return values()[ordinal];
	}
	
}

