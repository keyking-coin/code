package com.joymeng.slg.union.impl;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.net.SerializeEntity;

/***
 * 联盟入盟申请
 * @author tanyong
 *
 */
public class UnionApply implements SerializeEntity{
	long uid;
	String name;
	RoleIcon icon = new RoleIcon();
	int fight;//战斗力
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
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

	public int getFight() {
		return fight;
	}
	
	public void setFight(int fight) {
		this.fight = fight;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(uid);//long 
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);//string 成员名称
		icon.serialize(out);
		out.putInt(fight);//int 战斗力
	}
}
