package com.joymeng.slg.domain.map.impl.still.copy.data;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Ruins implements DataKey {
	String Id;
	String RuinsName;
	int Type;
	List<String> Checkpoin = new ArrayList<>();
	List<String> numberweight = new ArrayList<>();
	List<String> Reward = new ArrayList<>();
	String Prefab;
	String ServerName;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getRuinsName() {
		return RuinsName;
	}

	public void setRuinsName(String ruinsName) {
		RuinsName = ruinsName;
	}

	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public List<String> getCheckpoin() {
		return Checkpoin;
	}

	public void setCheckpoin(List<String> checkpoin) {
		Checkpoin = checkpoin;
	}

	public List<String> getNumberweight() {
		return numberweight;
	}

	public void setNumberweight(List<String> numberweight) {
		this.numberweight = numberweight;
	}

	public List<String> getReward() {
		return Reward;
	}

	public void setReward(List<String> reward) {
		Reward = reward;
	}

	public String getPrefab() {
		return Prefab;
	}

	public void setPrefab(String prefab) {
		Prefab = prefab;
	}
	
	public String getServerName() {
		return ServerName;
	}

	public void setServerName(String serverName) {
		ServerName = serverName;
	}

	@Override
	public Object key() {
		return Id;
	}

}
