package com.joymeng.slg.domain.market.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Blackshop implements DataKey {
	public String id; 
	public String itemid; 
	public int discount; 
	public int rate; 
	public List<String> cost; 
	public int randomtype; 
	public String degree; 
	public String baselv; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}


	public int getDiscount() {
		return discount;
	}


	public void setDiscount(int discount) {
		this.discount = discount;
	}


	public int getRate() {
		return rate;
	}


	public void setRate(int rate) {
		this.rate = rate;
	}


	public List<String> getCost() {
		return cost;
	}


	public void setCost(List<String> cost) {
		this.cost = cost;
	}


	public int getRandomtype() {
		return randomtype;
	}


	public void setRandomtype(int randomtype) {
		this.randomtype = randomtype;
	}


	public String getDegree() {
		return degree;
	}


	public void setDegree(String degree) {
		this.degree = degree;
	}


	public String getBaselv() {
		return baselv;
	}


	public void setBaselv(String baselv) {
		this.baselv = baselv;
	}


	@Override
	public Object key() {
		return id;
	}

}
