package com.joymeng.slg.union.impl;

import java.util.Map;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.world.World;

/**
 * 邀请列表信息
 * @author tanyong
 *
 */
public class UnionInviteInfo implements SerializeEntity{
	long uid;
	long unionId;
	String name;
	int fight;
	int language;
	RoleIcon icon = new RoleIcon();//头像
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(uid);//long 编号
		out.putLong(unionId);
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);//string 名称
		out.putInt(language);//int 语言编号
		out.putInt(fight);//int 战斗力
		icon.serialize(out);
	}
	
	public void init(Role role){
		uid = role.getId();
		unionId = role.getUnionId();
		name = role.getName();
		fight = role.getFightPower();
		icon.copy(role.getIcon());
	}

	public void load(Map<String, Object> data) {
		uid = Long.parseLong(data.get(DaoData.RED_ALERT_ROLE_ID).toString());
		Role role  = World.getInstance().getRole(uid);
		unionId = role.getUnionId();
		name = role.getName();
		fight = role.getFightPower();
		icon.copy(role.getIcon());
//		
//		name  = data.get(DaoData.RED_ALERT_GENERAL_NAME).toString();
//		icon.setIconId(Byte.parseByte(data.get(DaoData.RED_ALERT_ROLE_ICON_ID).toString()));
//		icon.setIconType(Byte.parseByte(data.get(DaoData.RED_ALERT_ROLE_ICON_TYPE).toString()));
//		Object o = data.get(DaoData.RED_ALERT_ROLE_ICON_NAME);
//		if(o != null){
//			icon.setIconName(data.get(DaoData.RED_ALERT_ROLE_ICON_NAME).toString());
//		}
	}
}
