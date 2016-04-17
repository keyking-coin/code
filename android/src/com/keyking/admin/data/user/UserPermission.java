package com.keyking.admin.data.user;


public class UserPermission {
	PermissionType type = PermissionType.buyer;
	String lastPayTime  = "null";//上次支付时间
	String endTime      = "2017-01-01 00:00:00";//会员到期日期
	String safeCode     = "uu_admin";//管理员的密码保护
	
	public PermissionType getType() {
		return type;
	}
	public void setType(PermissionType type) {
		this.type = type;
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
	public void copy(UserPermission perission) {
		type = perission.type;
		lastPayTime = perission.lastPayTime;
		endTime = perission.endTime;
		safeCode = perission.safeCode;
	}
}
