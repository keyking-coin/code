package com.keyking.coin.service.domain.other;

import java.util.ArrayList;
import java.util.List;

public class AboutInfo {
	List<String> QQ = new ArrayList<String>();
	List<String> TEL = new ArrayList<String>();
	String WX;
	
	public List<String> getQQ() {
		return QQ;
	}
	public void setQQ(List<String> qQ) {
		QQ = qQ;
	}
	public List<String> getTEL() {
		return TEL;
	}
	public void setTEL(List<String> tEL) {
		TEL = tEL;
	}
	public String getWX() {
		return WX;
	}
	public void setWX(String wX) {
		WX = wX;
	}
}
