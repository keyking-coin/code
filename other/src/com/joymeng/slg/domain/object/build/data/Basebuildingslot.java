package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Basebuildingslot implements DataKey{
	
	String id;
	List<String> buildLimitation;
	String unlockCondition;
	int unlockPrice;
	List<String> unlockOperation;
	String initBuilding;
	int initBuildingLevel;

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<String> getBuildLimitation() {
		return buildLimitation;
	}


	public void setBuildLimitation(List<String> buildLimitation) {
		this.buildLimitation = buildLimitation;
	}


	public String getUnlockCondition() {
		return unlockCondition;
	}


	public void setUnlockCondition(String unlockCondition) {
		this.unlockCondition = unlockCondition;
	}


	public int getUnlockPrice() {
		return unlockPrice;
	}



	public void setUnlockPrice(int unlockPrice) {
		this.unlockPrice = unlockPrice;
	}



	public List<String> getUnlockOperation() {
		return unlockOperation;
	}



	public void setUnlockOperation(List<String> unlockOperation) {
		this.unlockOperation = unlockOperation;
	}



	public String getInitBuilding() {
		return initBuilding;
	}



	public void setInitBuilding(String initBuilding) {
		this.initBuilding = initBuilding;
	}



	public int getInitBuildingLevel() {
		return initBuildingLevel;
	}


	public void setInitBuildingLevel(int initBuildingLevel) {
		this.initBuildingLevel = initBuildingLevel;
	}


	@Override
	public Object key() {
		return id;
	}
}
