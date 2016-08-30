package com.joymeng.slg.domain.object.role.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Checkreward implements DataKey{
	String id;
	byte type;
	byte data;
	List<String> commanderlv;
	List<String> Reward;
	byte vip;
	byte multiple;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}

	public List<String> getCommanderlv() {
		return commanderlv;
	}

	public void setCommanderlv(List<String> commanderlv) {
		this.commanderlv = commanderlv;
	}

	public List<String> getReward() {
		return Reward;
	}

	public void setReward(List<String> reward) {
		Reward = reward;
	}

	public byte getVip() {
		return vip;
	}

	public void setVip(byte vip) {
		this.vip = vip;
	}

	public byte getMultiple() {
		return multiple;
	}

	public void setMultiple(byte multiple) {
		this.multiple = multiple;
	}

	@Override
	public Object key() {
		return id;
	}

}
