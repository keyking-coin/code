package com.joymeng.slg.domain.market.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Blackshopcost implements DataKey {
	public String id; 
	public int cost; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public Object key() {
		return id;
	}

}
