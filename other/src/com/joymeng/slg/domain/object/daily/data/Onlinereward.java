package com.joymeng.slg.domain.object.daily.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Onlinereward implements DataKey {
	String id;
	long Time;
	List<String> Reward;
	String nextid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTime() {
		return Time;
	}

	public void setTime(long time) {
		Time = time;
	}

	public List<String> getReward() {
		return Reward;
	}

	public void setReward(List<String> reward) {
		Reward = reward;
	}

	public String getNextid() {
		return nextid;
	}

	public void setNextid(String nextid) {
		this.nextid = nextid;
	}

	@Override
	public Object key() {
		return id;
	}

}
