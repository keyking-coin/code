package com.joymeng.slg.union.data;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Allianceresearch implements DataKey{

	int id;
	String name;
	List<String> resources = new ArrayList<>();
	int persContr;
	int allianceContr;
	int weightValue;
	int resType;
	int roleLevel;
	int resExp;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<String> getResources() {
		return resources;
	}


	public void setResources(List<String> resources) {
		this.resources = resources;
	}


	public int getPersContr() {
		return persContr;
	}


	public void setPersContr(int persContr) {
		this.persContr = persContr;
	}


	public int getAllianceContr() {
		return allianceContr;
	}


	public void setAllianceContr(int allianceContr) {
		this.allianceContr = allianceContr;
	}


	public int getWeightValue() {
		return weightValue;
	}


	public void setWeightValue(int weightValue) {
		this.weightValue = weightValue;
	}


	public int getResType() {
		return resType;
	}


	public void setResType(int resType) {
		this.resType = resType;
	}


	public int getRoleLevel() {
		return roleLevel;
	}


	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}


	public int getResExp() {
		return resExp;
	}


	public void setResExp(int resExp) {
		this.resExp = resExp;
	}


	@Override
	public Object key() {
		return id;
	}

}
