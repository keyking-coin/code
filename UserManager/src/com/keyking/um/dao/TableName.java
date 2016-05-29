package com.keyking.um.dao;

public enum TableName {
	TABLE_NAME_USER("users"),
	TABLE_NAME_BROKER("brokers"),
	TABLE_NAME_UB("ubs");
	
	private String key;
	
	public String getKey() {
		return key;
	}

	private TableName(String key){
		this.key = key;
	}
}
