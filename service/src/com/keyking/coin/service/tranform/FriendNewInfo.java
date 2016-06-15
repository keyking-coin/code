package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.user.UserCharacter;

public class FriendNewInfo {
	long uid;//编号
	String nikeName;//昵称
	String faceIcon;//头像
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public String getNikeName() {
		return nikeName;
	}
	
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	
	public String getFaceIcon() {
		return faceIcon;
	}
	
	public void setFaceIcon(String faceIcon) {
		this.faceIcon = faceIcon;
	}
	
	public void copy(UserCharacter user){
		uid = user.getId();
		nikeName = user.getNikeName();
		faceIcon = user.getFace();
	}
}
