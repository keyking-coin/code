package com.joymeng.list;


public enum ServerStatus {
	SERVER_STATUS_CLOSE, // 0-服务器关服状态
	SERVER_STATUS_NEW, // 1-服务器新服状态
	SERVER_STATUS_MAINTEN, // 2-服务器维护状态
	SERVER_STATUS_NORMAL, // 3-服务器正常状态
	SERVER_STATUS_BUSY, // 4-服务器繁忙状态
	SERVER_STATUS_FULL,//5-服务器爆满状态
	SERVER_STATUS_FOROLD,//6-服务器爆满状态，老玩家可进
	SERVER_STATUS_ONREADY//7-服务器准备状态
   ;
  
	public byte getKey() {
		return (byte)ordinal();
	}
	
	public static ServerStatus valueof(byte key){
		ServerStatus[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ServerStatus serStatus = datas[i];
			if(serStatus.ordinal() == key){
				return serStatus;
			}
		}
		return null;
	}
}
