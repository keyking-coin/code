package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 世界地图建筑等级表
 * @author tanyong
 *
 */
public class Worldbuildinglevel implements DataKey {
	String id; 
	String buildingID; 
	List<String> buildCostList; 
	List<String> needBuildingIDList; 
	List<String> paramList; 
	int attackForce; 
	int power; 
	int time; 
	String buildingIconID; 
	String buildingmodelID; 
	String buildingAudioID; 
	int freetime; 
	int monsterCD; 
	List<String> monster; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBuildingID() {
		return buildingID;
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

	public String getBuildingIconID() {
		return buildingIconID;
	}

	public void setBuildingIconID(String buildingIconID) {
		this.buildingIconID = buildingIconID;
	}

	public String getBuildingmodelID() {
		return buildingmodelID;
	}

	public void setBuildingmodelID(String buildingmodelID) {
		this.buildingmodelID = buildingmodelID;
	}

	public String getBuildingAudioID() {
		return buildingAudioID;
	}

	public void setBuildingAudioID(String buildingAudioID) {
		this.buildingAudioID = buildingAudioID;
	}

	public int getFreetime() {
		return freetime;
	}

	public void setFreetime(int freetime) {
		this.freetime = freetime;
	}

	public int getMonsterCD() {
		return monsterCD;
	}

	public void setMonsterCD(int monsterCD) {
		this.monsterCD = monsterCD;
	}

	public List<String> getMonster() {
		return monster;
	}

	public void setMonster(List<String> monster) {
		this.monster = monster;
	}

	@Override
	public Object key() {
		return id;
	}
}
