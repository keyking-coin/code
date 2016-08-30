package com.joymeng.slg.domain.object.role.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Vipbufftype implements DataKey{
	String id;
	String vipBufftype;
	List<String> buffTarget;
	String buffID;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVipBufftype() {
		return vipBufftype;
	}

	public void setVipBufftype(String vipBufftype) {
		this.vipBufftype = vipBufftype;
	}

	public List<String> getBuffTarget() {
		return buffTarget;
	}

	public void setBuffTarget(List<String> buffTarget) {
		this.buffTarget = buffTarget;
	}

	public String getBuffID() {
		return buffID;
	}

	public void setBuffID(String buffID) {
		this.buffID = buffID;
	}

	@Override
	public Object key() {
		return id;
	}

}
