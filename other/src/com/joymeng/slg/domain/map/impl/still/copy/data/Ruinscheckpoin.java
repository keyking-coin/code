package com.joymeng.slg.domain.map.impl.still.copy.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Ruinscheckpoin implements DataKey {
	String id;
	List<String> monster;
	float DieProbability;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getMonster() {
		return monster;
	}

	public void setMonster(List<String> monsters) {
		this.monster = monsters;
	}

	public float getDieProbability() {
		return DieProbability;
	}

	public void setDieProbability(float dieProbability) {
		DieProbability = dieProbability;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
