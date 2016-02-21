package com.keyking.coin.service.domain.time;

import com.keyking.coin.service.net.buffer.DataBuffer;

public class TimeContent {
	
	byte type;
	
	String value;
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.put(type);
		buffer.putUTF(value);
	}
}
