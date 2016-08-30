package com.joymeng.slg.domain.map.impl;

import com.joymeng.Instances;
import com.joymeng.common.util.StringUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.role.RoleIcon;
import com.joymeng.slg.union.UnionBody;

public class MapRoleInfo implements Instances{
	long uid;//玩家编号
	long unionId;
	int cityId;
	int level = 1;//主城等级
	String name = "未知目标";
	RoleIcon icon = new RoleIcon();
	int position;// 坐标

	public void MapUserInfo() {
	}

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
	
	public int getCityId() {
		return cityId;
	}
	
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	public long getUnionId() {
		return unionId;
	}
	
	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}
	
	public String getUnionName(){
		UnionBody union = unionManager.search(unionId);
		if (union != null){
			return union.getName();
		}
		return "";
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void serialize(JoyBuffer out) {
		out.putLong(uid);//long 玩家编号编号
		out.putInt(cityId);//int 玩家城市编号
		out.putLong(unionId);//long 玩家联盟编号
		out.putPrefixedString(name == null ? "" : name, JoyBuffer.STRING_TYPE_SHORT);//string 玩家名字
		out.putInt(level);//int 玩家城堡等级
		out.putInt(position);//int 坐标
		icon.serialize(out);//头像
		unionManager.serializeSimple(unionId,out);
	}
	
	public void loadFromData(SqlData data,int position) {
		uid       = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		unionId   = data.getLong(DaoData.RED_ALERT_GENERAL_UNION_ID);
		name      = data.getString(DaoData.RED_ALERT_GENERAL_NAME);
		if (StringUtils.isNull(name)){
			name = "新兵" + uid;
		}
		level     = data.getInt(DaoData.RED_ALERT_GENERAL_LEVEL);
		cityId    = data.getInt(DaoData.RED_ALERT_GENERAL_ID);
		icon.setIconId(data.getByte(DaoData.RED_ALERT_ROLE_ICON_ID));
		icon.setIconType(data.getByte(DaoData.RED_ALERT_ROLE_ICON_TYPE));
		icon.setIconName(data.getString(DaoData.RED_ALERT_ROLE_ICON_NAME));
		this.position = position;
	}
	
	public void copy(MapRoleInfo info){
		uid       = info.uid;
		unionId   = info.unionId;
		name      = info.name;
		level     = info.level;
		cityId    = info.cityId;
		position  = info.position;
		icon.copy(info.icon);
	}

	public void clear() {
		uid       = 0;
		unionId   = 0;
		name      = null;
		level     = 0;
		cityId    = 0;
		position  = 0;
		icon.clear();
	}
}
