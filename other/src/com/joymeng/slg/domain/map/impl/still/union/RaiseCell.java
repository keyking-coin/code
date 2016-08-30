package com.joymeng.slg.domain.map.impl.still.union;

import com.joymeng.services.core.buffer.JoyBuffer;

public class RaiseCell {
	String key;
	int num;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public void addNum(int num){
		this.num += num;
	}
	
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(key,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(num);
	}

	public void deserialize(JoyBuffer buffer) {
		key = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		num = buffer.getInt();
	}
}

