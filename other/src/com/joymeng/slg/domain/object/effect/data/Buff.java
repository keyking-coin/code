package com.joymeng.slg.domain.object.effect.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Buff implements DataKey{
	String id;
	String buffName;
	int buffdatatype;
	String buffTarget;
	int buffobject;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBuffName() {
		return buffName;
	}
	public void setBuffName(String buffName) {
		this.buffName = buffName;
	}
	public int getBuffdatatype() {
		return buffdatatype;
	}
	public void setBuffdatatype(int buffdatatype) {
		this.buffdatatype = buffdatatype;
	}
	public String getBuffTarget() {
		return buffTarget;
	}
	public void setBuffTarget(String buffTarget) {
		this.buffTarget = buffTarget;
	}
	public int getBuffobject() {
		return buffobject;
	}
	public void setBuffobject(int buffobject) {
		this.buffobject = buffobject;
	}
	@Override
	public Object key() {
		return id;
	}
	
}
