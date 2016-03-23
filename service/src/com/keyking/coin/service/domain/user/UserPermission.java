package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.StringUtil;

public class UserPermission {
	PermissionType type = PermissionType.buyer;
	String lastPayTime = "null";//上次支付时间
	String endTime  = "2017-01-01 00:00:00";//会员到期日期
	String safeCode = "uu_admin";//管理员的密码保护
	
	public UserPermission(){
		
	}

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

	public boolean admin(){
		return type.ordinal() == PermissionType.admin.ordinal();
	}
	
	public boolean seller(){
		return type.ordinal() == PermissionType.seller.ordinal();
	}
	
	public boolean buyer(){
		return type.ordinal() == PermissionType.buyer.ordinal();
	}
	
	public String serialize(){
		return lastPayTime + "," + endTime + "," + type.toString();
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		String[] ss = str.split(",");
		lastPayTime = ss[0];
		endTime     = ss[1];
		if (ss[2].equals("seller")){
			type = PermissionType.seller;
		}else if (ss[2].equals("buyer")){
			type = PermissionType.buyer;
		}else if (ss[2].equals("admin")){
			type = PermissionType.admin;
		}
	}

	public void serialize(DataBuffer buffer) {
		buffer.putUTF(endTime == null ? "" : endTime);
		buffer.putInt(type.ordinal());
	}
}
