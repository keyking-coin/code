package com.joymeng.slg.domain.turntable;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Lotterypool implements DataKey {
	String id;
	List<String> itemlist;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getItemlist() {
		return itemlist;
	}

	public void setItemlist(List<String> itemlist) {
		this.itemlist = itemlist;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
