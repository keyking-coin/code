package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Radar implements DataKey {

	String id;
	List<String> marchfunction;
	List<String> scoutfunction;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getMarchfunction() {
		return marchfunction;
	}

	public void setMarchfunction(List<String> marchfunction) {
		this.marchfunction = marchfunction;
	}

	public List<String> getScoutfunction() {
		return scoutfunction;
	}

	public void setScoutfunction(List<String> scoutfunction) {
		this.scoutfunction = scoutfunction;
	}

	@Override
	public Object key() {
		return id;
	}

}
