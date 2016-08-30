package com.joymeng.slg.union.impl;

public class UnionHelper {
	long uid;
	String roleName;

	public UnionHelper() {

	}

	public UnionHelper(long uid, String roleName) {
		this.uid = uid;
		this.roleName = roleName;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
