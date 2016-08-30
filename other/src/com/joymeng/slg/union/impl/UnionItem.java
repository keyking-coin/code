package com.joymeng.slg.union.impl;

public class UnionItem {
	String id;
	Byte type; // 0-Item表 1-是AllianceShop表
	int num;

	public UnionItem() {

	}

	public UnionItem(String id, byte type, int num) {
		this.id = id;
		this.type = type;
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

}
