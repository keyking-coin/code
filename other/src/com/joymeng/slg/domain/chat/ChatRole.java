package com.joymeng.slg.domain.chat;

import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.domain.object.role.VipInfo;

public class ChatRole {
	long uid;
	long unionId;//联盟ID
	VipInfo vipInfo = new VipInfo();//vip等级
	String name = "";//玩家姓名
	RoleIcon icon = new RoleIcon();
	public ChatRole(){
		
	}
	
	public ChatRole(Role role){
		uid = role.getId();
		unionId = role.getUnionId();
		vipInfo = role.getVipInfo() == null ? new VipInfo() : role.getVipInfo();
		name = role.getName();
		icon = role.getIcon();
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUnionId() {
		return unionId;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public VipInfo getVipInfo() {
		return vipInfo;
	}

	public void setVipInfo(VipInfo vipInfo) {
		this.vipInfo = vipInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	public void update(Role role) {
		unionId = role.getUnionId();
		vipInfo = role.getVipInfo();
		name = role.getName();
		icon = role.getIcon();
	}
}

