package com.joymeng.slg.domain.object.role;

public class RoleOnlineTime {
	long login;// 登录时间
	long SignOut;// 退出时间
	long online;// 本次在线时长
	long Total;// 总在线时长

	public long getLogin() {
		return login;
	}

	public void setLogin(long login) {
		this.login = login;
	}

	public long getSignOut() {
		return SignOut;
	}

	public void setSignOut(long signOut) {
		SignOut = signOut;
	}

	public long getOnline() {
		return online;
	}

	public void setOnline(long online) {
		this.online = online;
	}

	public long getTotal() {
		return Total;
	}

	public void setTotal(long total) {
		Total = total;
	}

	public RoleOnlineTime() {
		login = 0;
		SignOut = 0;
		online = 0;
		Total = 0;
	}
}
