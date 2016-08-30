package com.joymeng.push;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.role.Role;

public class PushModuleData implements Instances,Comparable<PushModuleData>{
	
	public enum PushPlatform{
		Android,
		IOS;
	}
	long uid;
	String title;
	String content;
	String language;
	long sendTime;
	PushPlatform platform = PushPlatform.Android;
	String targetId;
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public boolean push(long time){
		if (sendTime > 0 && time < sendTime){
			return false;
		}
		return true;
//		if (targetId == null){
//			if (platform == PushPlatform.Android){
//				return push.sendAndroidBroadcast(title,content);
//			}else{
//				return push.sendIOSBroadcast(title,content);
//			}
//		}else{
//			if (platform == PushPlatform.Android){
//				return push.sendAndroidUnicast(targetId,title,content);
//			}else{
//				return push.sendIOSUnicast(targetId,title,content);
//			}
//		}
	}
	
	public boolean checkOnline(){
		Role role = world.getOnlineRole(uid);
		return role == null;
	}

	@Override
	public int compareTo(PushModuleData data) {
		return Long.compare(sendTime, data.sendTime);
	}
}
