package com.keyking.coin.service.domain.bourse;

public class BourseInfo {
	String name;
	String url;
	byte type;//1只在文交所导航,2文交所导航加下拉列表,3文交所导航加下拉列表加热门文交所
	int pos;//位置
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
}
