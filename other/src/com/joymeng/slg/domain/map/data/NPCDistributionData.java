package com.joymeng.slg.domain.map.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 城市刷新固化表
 * @author tanyong
 *
 */
public class NPCDistributionData implements DataKey {
	String id;
	String name;
	byte type;
	int centerX;
	int centerY;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	@Override
	public Object key() {
		return id;
	}

}
