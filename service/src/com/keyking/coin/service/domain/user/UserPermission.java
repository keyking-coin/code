package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;

public class UserPermission {
	PermissionType permission = PermissionType.buyer;
	String lastPayTime = "null";//上次支付时间
	String endTime  = "2017-01-01 00:00:00";//会员到期日期
	String safeCode = "uu_admin";//管理员的密码保护
	
	public UserPermission(){
		
	}
	
	public PermissionType getPermission() {
		return permission;
	}

	public void setPermission(PermissionType permission) {
		this.permission = permission;
	}



	public String getLastPayTime() {
		return lastPayTime;
	}

	public void setLastPayTime(String lastPayTime) {
		this.lastPayTime = lastPayTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getSafeCode() {
		return safeCode;
	}

	public void setSafeCode(String safeCode) {
		this.safeCode = safeCode;
	}

	public boolean isAdmin(){
		return permission.ordinal() == PermissionType.admin.ordinal();
	}
	
	public String serialize(){
		String str = JsonUtil.ObjectToJsonString(permission);
		return lastPayTime + "|" + endTime + "|" + str;
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		String[] ss = str.split("\\|");
		lastPayTime = ss[0];
		endTime     = ss[1];
		permission = JsonUtil.JsonToObject(ss[2],PermissionType.class);
	}

	public void serialize(DataBuffer buffer) {
		buffer.putUTF(endTime==null?"":endTime);
		buffer.putInt(permission.ordinal());
	}
}
