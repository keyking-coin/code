package com.joymeng.slg.domain.object.bag.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Equipeffect implements DataKey {

	String id;
	String effectDesc;
	String buffId;
	List<String> buffTarget;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEffectDesc() {
		return effectDesc;
	}

	public void setEffectDesc(String effectDesc) {
		this.effectDesc = effectDesc;
	}

	public String getBuffId() {
		return buffId;
	}

	public void setBuffId(String buffId) {
		this.buffId = buffId;
	}

	public List<String> getBuffTarget() {
		return buffTarget;
	}

	public void setBuffTarget(List<String> buffTarget) {
		this.buffTarget = buffTarget;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
