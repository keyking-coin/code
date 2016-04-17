package com.keyking.admin.net.request;

public enum NetLogicName {
	admin_login("AdminLogin"),
	user_search("AdminUserSearch"),
	user_commit("AdminUserCommit"),
	agency_commit("AdminDealOrderUpdate"),
	;
	private String key;
	private NetLogicName(String key){
		this.key = key;
	}
	public String getKey() {
		return key;
	}
}
