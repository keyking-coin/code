package com.joymeng.slg.domain.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.TimeUtils;

/**
 * 聊天组
 * @author houshanping
 */
public class ChatGroup {
	public static byte MAX_NUM = 100;
	long id;			// 聊天组的ID
	String name = "讨论组";// 名字
	List<ChatRole> roles = new ArrayList<ChatRole>();// 聊天人员
	long creatorUid;// 创始人ID
	long creatDate = TimeUtils.nowLong() / 1000;// 创建时间
	
	public ChatGroup() {		
	}
	/**
	 * 创建一个组
	 * @param id 组ID
	 * @param roles 成员列表
	 * @param creatorUid 创建者的ID
	 */
	public ChatGroup(long id,List<ChatRole> roles,long creatorUid){
		this.id = id;
		this.name = "讨论组";
		this.roles = roles;
		this.creatorUid = creatorUid;
		this.creatDate =  TimeUtils.nowLong() / 1000;
	}
	
	public static byte getMAX_NUM() {
		return MAX_NUM;
	}

	public static void setMAX_NUM(byte mAX_NUM) {
		MAX_NUM = mAX_NUM;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<ChatRole> getRoles() {
		return roles;
	}

	public void setRoles(List<ChatRole> roles) {
		this.roles = roles;
	}

	public long getCreatorUid() {
		return creatorUid;
	}
	public void setCreatorUid(long creatorUid) {
		this.creatorUid = creatorUid;
	}
	public long getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(long creatDate) {
		this.creatDate = creatDate;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
