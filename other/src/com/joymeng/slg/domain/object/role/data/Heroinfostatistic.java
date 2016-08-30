package com.joymeng.slg.domain.object.role.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Heroinfostatistic implements DataKey{
	String id;
	String statisticName;
	String type;
	byte vlType;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatisticName() {
		return statisticName;
	}
	public void setStatisticName(String statisticName) {
		this.statisticName = statisticName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public byte getVlType() {
		return vlType;
	}
	public void setVlType(byte vlType) {
		this.vlType = vlType;
	}
	@Override
	public Object key() {
		return id;
	}
	
}
