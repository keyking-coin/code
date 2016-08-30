package com.joymeng.slg.domain.actvt.data;

import java.util.List;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_rushsevenday implements DataKey 
{
	String id;
	List<Integer> accTimes;
	List<Integer> accScores;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Integer> getAccTimes() {
		return accTimes;
	}

	public void setAccTimes(List<Integer> accTimes) {
		this.accTimes = accTimes;
	}

	public List<Integer> getAccScores() {
		return accScores;
	}

	public void setAccScores(List<Integer> accScores) {
		this.accScores = accScores;
	}

	@Override
	public Object key() {
		return id;
	}
}
