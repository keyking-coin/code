package com.joymeng.slg.domain.object.task.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Activeextrainfo implements DataKey{
	String id;
	int buildinglevel;
	int stage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getBuildinglevel() {
		return buildinglevel;
	}

	public void setBuildinglevel(int buildinglevel) {
		this.buildinglevel = buildinglevel;
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	@Override
	public Object key() {
		return id;
	}

}
