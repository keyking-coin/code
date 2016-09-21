package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Resourcerefresh implements DataKey {
	String id;
	String name;
	byte type;
	int centerX;
	int centerY;
	int rangeX;
	int rangeY;
	int count;
	List<String> needDistribution;
	List<String> needProbavility;
	int refreshTime; 
	int survivalTime; 
	int deathRefresh;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getRangeX() {
		return rangeX;
	}

	public void setRangeX(int rangeX) {
		this.rangeX = rangeX;
	}

	public int getRangeY() {
		return rangeY;
	}

	public void setRangeY(int rangeY) {
		this.rangeY = rangeY;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getNeedDistribution() {
		return needDistribution;
	}

	public void setNeedDistribution(List<String> needDistribution) {
		this.needDistribution = needDistribution;
	}

	public List<String> getNeedProbavility() {
		return needProbavility;
	}

	public void setNeedProbavility(List<String> needProbavility) {
		this.needProbavility = needProbavility;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public int getSurvivalTime() {
		return survivalTime;
	}

	public void setSurvivalTime(int survivalTime) {
		this.survivalTime = survivalTime;
	}

	public int getDeathRefresh() {
		return deathRefresh;
	}

	public void setDeathRefresh(int deathRefresh) {
		this.deathRefresh = deathRefresh;
	}

	@Override
	public Object key() {
		return id;
	}
}
