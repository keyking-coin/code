package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Cityinitialize implements DataKey {
	String id;
	List<String> resourcescount;
	int foodConsumption;
	int idlePower;
	int buildQueue;
	int researchQueue;
	int maxBuildQueue;
	int maxResearchQueue;
	int bornmoney; 
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<String> getResourcescount() {
		return resourcescount;
	}


	public void setResourcescount(List<String> resourcescount) {
		this.resourcescount = resourcescount;
	}


	public int getFoodConsumption() {
		return foodConsumption;
	}


	public void setFoodConsumption(int foodConsumption) {
		this.foodConsumption = foodConsumption;
	}


	public int getIdlePower() {
		return idlePower;
	}


	public void setIdlePower(int idlePower) {
		this.idlePower = idlePower;
	}


	public int getBuildQueue() {
		return buildQueue;
	}


	public void setBuildQueue(int buildQueue) {
		this.buildQueue = buildQueue;
	}


	public int getResearchQueue() {
		return researchQueue;
	}


	public void setResearchQueue(int researchQueue) {
		this.researchQueue = researchQueue;
	}


	public int getMaxBuildQueue() {
		return maxBuildQueue;
	}


	public void setMaxBuildQueue(int maxBuildQueue) {
		this.maxBuildQueue = maxBuildQueue;
	}


	public int getMaxResearchQueue() {
		return maxResearchQueue;
	}


	public void setMaxResearchQueue(int maxResearchQueue) {
		this.maxResearchQueue = maxResearchQueue;
	}
	
	public int getBornmoney() {
		return bornmoney;
	}

	public void setBornmoney(int bornmoney) {
		this.bornmoney = bornmoney;
	}

	@Override
	public Object key() {
		return id;
	}
}
