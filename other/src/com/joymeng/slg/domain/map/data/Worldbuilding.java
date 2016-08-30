package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

/**
 * 世界地图建筑表
 * @author tanyong
 *
 */
public class Worldbuilding implements DataKey {
	String id;
	int buildingType;
	String buildingName;
	String buildingDescription;
	List<String> moduleList;
	int maxLevel;
	int isUpgrade;
	List<String> maxBuildCount;
	List<String> levelDataList;
	int initializeLevel;
	List<String> paramNameList;
	List<String> buildingComponent;
	String nextLevelType;
	int size;
	int physical;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(int buildingType) {
		this.buildingType = buildingType;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getBuildingDescription() {
		return buildingDescription;
	}

	public void setBuildingDescription(String buildingDescription) {
		this.buildingDescription = buildingDescription;
	}

	public List<String> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<String> moduleList) {
		this.moduleList = moduleList;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getIsUpgrade() {
		return isUpgrade;
	}

	public void setIsUpgrade(int isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	public List<String> getMaxBuildCount() {
		return maxBuildCount;
	}

	public void setMaxBuildCount(List<String> maxBuildCount) {
		this.maxBuildCount = maxBuildCount;
	}

	public List<String> getLevelDataList() {
		return levelDataList;
	}

	public void setLevelDataList(List<String> levelDataList) {
		this.levelDataList = levelDataList;
	}

	public int getInitializeLevel() {
		return initializeLevel;
	}

	public void setInitializeLevel(int initializeLevel) {
		this.initializeLevel = initializeLevel;
	}

	public List<String> getParamNameList() {
		return paramNameList;
	}

	public void setParamNameList(List<String> paramNameList) {
		this.paramNameList = paramNameList;
	}

	public List<String> getBuildingComponent() {
		return buildingComponent;
	}

	public void setBuildingComponent(List<String> buildingComponent) {
		this.buildingComponent = buildingComponent;
	}

	public String getNextLevelType() {
		return nextLevelType;
	}

	public void setNextLevelType(String nextLevelType) {
		this.nextLevelType = nextLevelType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	@Override
	public Object key() {
		return id;
	}

}
