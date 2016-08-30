package com.joymeng.slg.domain.object.role;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.mod.AbstractClientModule;

public class RoleIcon {
	
	byte iconType = 0;//头像类型0系统，1自定义
	byte iconId = 0;//头像
	String iconName = "";//自定义头像名字
	
	
	public RoleIcon() {
	}

	public RoleIcon(byte iconType, byte iconId, String iconName) {
		this.iconType = iconType;
		this.iconId = iconId;
		this.iconName = iconName;
	}
	
	public byte getIconType() {
		return iconType;
	}
	
	public void setIconType(byte iconType) {
		this.iconType = iconType;
	}
	
	public byte getIconId() {
		return iconId;
	}
	
	public void setIconId(byte iconId) {
		this.iconId = iconId;
	}
	
	public String getIconName() {
		return iconName;
	}
	
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	
	public void sendToClient(AbstractClientModule module){
		module.add(iconType);
		module.add(iconType == 0 ? iconId : iconName);
	}
	
	public void serialize(JoyBuffer out) {
		out.put(iconType);//byte 头像类型
		if (iconType == 0){
			out.put(iconId);//byte 系统头像类型
		}else{
			out.putPrefixedString(iconName,JoyBuffer.STRING_TYPE_SHORT);//string 自定义头像url
		}
	}
	
	public void copy(RoleIcon icon){
		iconType = icon.iconType;
		iconId   = icon.iconId;
		iconName = icon.iconName;
	}
	
	public void clear() {
		iconType = 0;
		iconId   = 0;
		iconName = "";
	}
}
