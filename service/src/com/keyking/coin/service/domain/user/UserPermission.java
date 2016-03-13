package com.keyking.coin.service.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.util.StringUtil;

public class UserPermission {
	List<PermissionType> permissions = new ArrayList<PermissionType>();
	String lastPayTime;//上次支付时间
	String endTime;//会员到期日期
	String safeCode = "uu_admin";//管理员的密码保护
	
	public UserPermission(){
		
	}
	
	public List<PermissionType> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionType> permissions) {
		this.permissions = permissions;
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
		for (PermissionType type : permissions){
			if (type.ordinal() == PermissionType.admin.ordinal()){
				return true;
			}
		}
		return false;
	}
	
	public String serialize(){
		return null;
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		
	}
}
