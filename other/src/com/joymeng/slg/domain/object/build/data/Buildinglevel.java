package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Buildinglevel implements DataKey {
	
	String id;
	String buildingID;
	String description;
	List<String> buildCostList;
	List<String> needBuildingIDList;
	List<String> paramList;
	int attackForce;
	int power;
	int time;
	List<String> nextLevelTextList;
	int number;
	String armyID;
	List<String> needitem;
	
	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBuildingID() {
		return buildingID;
	}

	public List<String> getNeeditem() {
		return needitem;
	}

	public void setNeeditem(List<String> needitem) {
		this.needitem = needitem;
	}

	public void setBuildingID(String buildingID) {
		this.buildingID = buildingID;
	}

	public List<String> getBuildCostList() {
		return buildCostList;
	}

	public void setBuildCostList(List<String> buildCostList) {
		this.buildCostList = buildCostList;
	}

	public List<String> getNeedBuildingIDList() {
		return needBuildingIDList;
	}

	public void setNeedBuildingIDList(List<String> needBuildingIDList) {
		this.needBuildingIDList = needBuildingIDList;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	public int getAttackForce() {
		return attackForce;
	}

	public void setAttackForce(int attackForce) {
		this.attackForce = attackForce;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<String> getNextLevelTextList() {
		return nextLevelTextList;
	}

	public void setNextLevelTextList(List<String> nextLevelTextList) {
		this.nextLevelTextList = nextLevelTextList;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getArmyID() {
		return armyID;
	}

	public void setArmyID(String armyID) {
		this.armyID = armyID;
	}

	@Override
	public Object key() {
		return id;
	}

}
