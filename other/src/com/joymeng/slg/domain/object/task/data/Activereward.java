package com.joymeng.slg.domain.object.task.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Activereward implements DataKey{
	String id;
	int points;
	List<String> reward;
	String icon;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public List<String> getReward() {
		return reward;
	}
	public void setReward(List<String> reward) {
		this.reward = reward;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	@Override
	public Object key() {
		return id;
	}
	

}
