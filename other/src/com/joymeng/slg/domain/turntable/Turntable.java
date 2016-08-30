package com.joymeng.slg.domain.turntable;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Turntable implements DataKey {
	String id;
	List<String> buildingLevel;
	int resetNumber;
	int weight;
	List<String> itemList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getBuildingLevel() {
		return buildingLevel;
	}

	public void setBuildingLevel(List<String> buildingLevel) {
		this.buildingLevel = buildingLevel;
	}

	public int getResetNumber() {
		return resetNumber;
	}

	public void setResetNumber(int resetNumber) {
		this.resetNumber = resetNumber;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public List<String> getItemList() {
		return itemList;
	}

	public void setItemList(List<String> itemList) {
		this.itemList = itemList;
	}

	@Override
	public Object key() {
		return id;
	}

}
