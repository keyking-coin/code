package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Building implements DataKey {
	
	String id;
	int buildingType;
	String buildingName;
	String buildingDescription;
	String buildingAudioID;
	List<String> moduleList;
	int maxLevel;
	int initializeLevel;
	int isUpgrade;
	List<String> levelDataList;
	String buildingIconID;
	String buildingmodelID;
	int maxBuildCount;
	List<String> paramNameList;
	List<String> buildingComponent;
	String name;
	
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

	public String getBuildingAudioID() {
		return buildingAudioID;
	}

	public void setBuildingAudioID(String buildingAudioID) {
		this.buildingAudioID = buildingAudioID;
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

	public int getInitializeLevel() {
		return initializeLevel;
	}

	public void setInitializeLevel(int initializeLevel) {
		this.initializeLevel = initializeLevel;
	}

	public int getIsUpgrade() {
		return isUpgrade;
	}

	public void setIsUpgrade(int isUpgrade) {
		this.isUpgrade = isUpgrade;
	}

	public List<String> getLevelDataList() {
		return levelDataList;
	}

	public void setLevelDataList(List<String> levelDataList) {
		this.levelDataList = levelDataList;
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

	public int getMaxBuildCount() {
		return maxBuildCount;
	}

	public void setMaxBuildCount(int maxBuildCount) {
		this.maxBuildCount = maxBuildCount;
	}
	
	public List<String> getBuildingComponent() {
		return buildingComponent;
	}

	public void setBuildingComponent(List<String> buildingComponent) {
		this.buildingComponent = buildingComponent;
	}

	public List<String> getParamNameList() {
		return paramNameList;
	}

	public void setParamNameList(List<String> paramNameList) {
		this.paramNameList = paramNameList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object key() {
		return id;
	}

}
