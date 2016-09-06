package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;

public class FriendListInfo implements Instances{
	long fid;//好友编号
	String nikeName;//好友昵称
	String faceIcon;//头像
	String tel;//电话
	byte pass;//0等待通过的好友申请,1已通过的好友。
	String time;//申请时间或者是通过时间
	String other;//验证信息
	
	public FriendListInfo(Friend friend){
		fid = friend.getFid();
		UserCharacter user = CTRL.search(fid);
		if (user != null){
			nikeName = user.getNikeName();
			faceIcon = user.getAppFace();
			tel      = user.getAccount();
		}
		pass  = friend.getPass();
		time  = friend.getTime();
		other = friend.getOther();
	}
	public long getFid() {
		return fid;
	}
	
	public void setFid(long fid) {
		this.fid = fid;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
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
	
	public byte getPass() {
		return pass;
	}
	
	public void setPass(byte pass) {
		this.pass = pass;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getOther() {
		return other;
	}
	
	public void setOther(String other) {
		this.other = other;
	}
}
