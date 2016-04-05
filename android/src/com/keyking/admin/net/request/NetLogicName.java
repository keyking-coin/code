package com.keyking.admin.net.request;

public enum NetLogicName {
	admin_login("AdminLogin");
	;
	private String key;
	private NetLogicName(String key){
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}
