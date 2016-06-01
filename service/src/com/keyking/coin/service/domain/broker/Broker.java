package com.keyking.coin.service.domain.broker;

import com.keyking.coin.util.Instances;

public class Broker implements Instances{
	long id;
	String name;
	String des;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDes() {
		return des;
	}
	
	public void setDes(String des) {
		this.des = des;
	}

	public void save() {
		DB.getBrokerDao().save(this);
	}
	
}
