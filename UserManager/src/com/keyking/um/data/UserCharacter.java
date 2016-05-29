package com.keyking.um.data;

import java.util.ArrayList;
import java.util.List;

import com.keyking.util.Instances;


public class UserCharacter implements Instances{
	long id;
	long fid;//我的上司
	String account;
	String pwd  = "666666";
	String name = "";//姓名
	String registTime = "";
	long lastCheck;//上次验证时间
	List<UserBroker> brokers = new ArrayList<UserBroker>();
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFid() {
		return fid;
	}

	public void setFid(long fid) {
		this.fid = fid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public boolean check(){
		return false;
	}
	
	public void save() {
		DB.getUserDao().save(this);
	}
	
	public void load(){
		
	}
}
 
