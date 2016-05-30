package com.keyking.coin.service.domain.broker;

public class UserBroker {
	long uid;
	long bid;
	String account;
	String tel;//电话
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public long getBid() {
		return bid;
	}
	
	public void setBid(long bid) {
		this.bid = bid;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getTel() {
		return tel;
	}
	
	public void setTel(String tel) {
		this.tel = tel;
	}
}
