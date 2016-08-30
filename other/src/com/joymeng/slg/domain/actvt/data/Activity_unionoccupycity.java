package com.joymeng.slg.domain.actvt.data;

import java.util.List;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_unionoccupycity implements DataKey
{
	String id;
	int rankNum;
	List<Integer> cityScores;
	List<Integer> cityLevels;
	List<String> rewards;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRankNum() {
		return rankNum;
	}

	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}

	public List<Integer> getCityScores() {
		return cityScores;
	}

	public void setCityScores(List<Integer> cityScores) {
		this.cityScores = cityScores;
	}

	public List<Integer> getCityLevels() {
		return cityLevels;
	}

	public void setCityLevels(List<Integer> cityLevels) {
		this.cityLevels = cityLevels;
	}

	public List<String> getRewards() {
		return rewards;
	}

	public void setRewards(List<String> rewards) {
		this.rewards = rewards;
	}

	@Override
	public Object key() {
		return id;
	}
}
