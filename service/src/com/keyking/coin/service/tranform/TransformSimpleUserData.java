package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.domain.user.UserPermission;

public class TransformSimpleUserData {
	String nikeName= "";//昵称
	String title = "普通营销员";//称号
	UserPermission perission;//用户权限
	int emailNum;//站内信数量
	
	public TransformSimpleUserData(UserCharacter user){
		nikeName = user.getNikeName();
		title = user.getTitle();
		perission = user.getPermission();
		emailNum = user.getEmails().size();
	}
	
	public String getNikeName() {
		return nikeName;
	}
	
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public UserPermission getPerission() {
		return perission;
	}
	
	public void setPerission(UserPermission perission) {
		this.perission = perission;
	}

	public int getEmailNum() {
		return emailNum;
	}

	public void setEmailNum(int emailNum) {
		this.emailNum = emailNum;
	}
	
}
