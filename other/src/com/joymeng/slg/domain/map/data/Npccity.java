package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

/**
 * 城市固化表
 * @author tanyong
 *
 */
public class Npccity implements DataKey {
	String id;
	int level;
	String cityname;
	List<String> litemonster;
	List<String> citymonster;
	List<String> reward;
	List<String> hurtreward;
	List<String> Architecture;
	String buildingmodelID;
	String worldbuildingid; 
	String viewrange;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}

	public List<String> getLitemonster() {
		return litemonster;
	}

	public void setLitemonster(List<String> litemonster) {
		this.litemonster = litemonster;
	}

	public List<String> getCitymonster() {
		return citymonster;
	}

	public void setCitymonster(List<String> citymonster) {
		this.citymonster = citymonster;
	}

	public List<String> getReward() {
		return reward;
	}

	public void setReward(List<String> reward) {
		this.reward = reward;
	}

	public List<String> getHurtreward() {
		return hurtreward;
	}

	public void setHurtreward(List<String> hurtreward) {
		this.hurtreward = hurtreward;
	}

	public List<String> getArchitecture() {
		return Architecture;
	}

	public void setArchitecture(List<String> architecture) {
		Architecture = architecture;
	}

	public String getBuildingmodelID() {
		return buildingmodelID;
	}

	public void setBuildingmodelID(String buildingmodelID) {
		this.buildingmodelID = buildingmodelID;
	}

	public String getWorldbuildingid() {
		return worldbuildingid;
	}

	public void setWorldbuildingid(String worldbuildingid) {
		this.worldbuildingid = worldbuildingid;
	}

	public String getViewrange() {
		return viewrange;
	}

	public void setViewrange(String viewrange) {
		this.viewrange = viewrange;
	}

	@Override
	public Object key() {
		return id;
	}

}
