package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.buffer.DataBuffer;

public class RechargeOrder {
	String time;//充值时间
	float value;//充值数据
	byte type;//充值途径 0 银联、1支付宝、2微信。
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public void _serialize(DataBuffer buffer) {
		buffer.put(type);
		buffer.putUTF("" + value);
		buffer.putUTF(time);
	}
}
