package com.joymeng.slg.domain.object.effect;

import com.joymeng.Instances;

public class BuffObject implements Instances {
	String key;
	byte valueType;// 0-float，1-int,2-int+float
	int value;
	float rate;
	String buildId;

	public BuffObject() {
	}

	public BuffObject(String buildId, String key, byte type, int val, float rate) {
		this.buildId = buildId;
		this.key = key;
		this.valueType = type;
		if (valueType == 0) {
			this.rate = rate;
			this.value = 0;
		} else if (valueType == 1) {
			this.value = val;
			this.rate = 0;
		} else {
			this.rate = rate;
			this.value = val;
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public byte getValueType() {
		return valueType;
	}

	public void setValueType(byte valueType) {
		this.valueType = valueType;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public void updateValues(byte type, int val, float rate) {
		if (type == 0) {
			this.rate += rate;
		} else {
			this.value += val;
		}
		if (type != valueType) {
			valueType = 2;
		}
	}

	// public void serilize(JoyBuffer in){
	//
	// }
	//
	// public void deserilize(JoyBuffer out){
	//
	// }
}
