package com.joymeng.slg.domain.actvt.data;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_soldierscore implements DataKey 
{
	String id;
	int score;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public Object key() {
		return id;
	}
}
