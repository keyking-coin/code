package com.keyking.admin.data.user;

public class Account {
	String account;//银行账号
	String name;//银行名称
	String openAddress;//开户行地址
	String openName;//开户人姓名
	String addTime;//添加时间
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenAddress() {
		return openAddress;
	}
	public void setOpenAddress(String openAddress) {
		this.openAddress = openAddress;
	}
	public String getOpenName() {
		return openName;
	}
	public void setOpenName(String openName) {
		this.openName = openName;
	}
	public String getAddTime() {
		return addTime;
	}
	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}
}
