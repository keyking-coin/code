package com.keyking.coin.service.dao;

public enum TableName {
	TABLE_NAME_DEAL("deal"),
	TABLE_NAME_ORDER("deal_order"),
	TABLE_NAME_REVERT("deal_revert"),
	TABLE_NAME_EMAIL("email"),
	TABLE_NAME_MESSAGE("message"),
	TABLE_NAME_USER("users"),
	TABLE_NAME_BROKER("brokers"),
	TABLE_NAME_TIME_LINE("timeline"),
	TABLE_NAME_AD("ad"),
	;
	
	private TableName(String table){
		this.table = table;
	}
	
	private String table;
	
	public String getTable() {
		return table;
	}
	
}
