package com.joymeng.slg.domain.object.task.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Taskreward implements DataKey{
	String id;
	List<String> RewardList;
	int Count;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getRewardList() {
		return RewardList;
	}
	public void setRewardList(List<String> rewardList) {
		RewardList = rewardList;
	}
	public int getCount() {
		return Count;
	}
	public void setCount(int count) {
		Count = count;
	}
	@Override
	public Object key() {
		return id;
	}
	
	
}
