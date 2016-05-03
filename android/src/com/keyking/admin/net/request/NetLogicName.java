package com.keyking.admin.net.request;

public enum NetLogicName {
	system_module("Module"),
	system_connect("AdminConnect"),
	admin_login("AdminLogin"),
	user_search("AdminUserSearch"),
	user_commit("AdminUserCommit"),
	agency_commit("AdminDealOrderUpdate"),
	deal_search("AdminDealSearch"),
	order_search("AdminOrderSearch"),
	deal_lock("AdminLockDeal"),
	order_lock("AdminLockOrder"),
	app_login("AppLogin")
	;
	private String key;
	private NetLogicName(String key){
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}
