package com.joymeng.slg.domain.map.impl.still.union;

import com.joymeng.services.core.buffer.JoyBuffer;


public class AttackerDamage implements Comparable<AttackerDamage>{
	long uid;
	String name = "";
	int num;
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	@Override
	public int compareTo(AttackerDamage o) {
		return Integer.compare(o.num,num);
	}
	
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(num);
	}
	
	public void deserialize(JoyBuffer buffer){
		name = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		num  = buffer.getInt();
	}
}
