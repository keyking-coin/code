package com.joymeng.slg.union.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Alliancemembers implements DataKey {
	String id;
	int type;
	String name;
	List <String> persContr;
	List<String> jurisdiction;
	int rank;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPersContr() {
		return persContr;
	}

	public void setPersContr(List<String> persContr) {
		this.persContr = persContr;
	}

	public List<String> getJurisdiction() {
		return jurisdiction;
	}

	public void setJurisdiction(List<String> jurisdiction) {
		this.jurisdiction = jurisdiction;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public Object key() {
		return id;
	}

}
