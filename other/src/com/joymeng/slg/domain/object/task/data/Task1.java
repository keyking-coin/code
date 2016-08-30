package com.joymeng.slg.domain.object.task.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Task1 implements DataKey{
	String id;
	String branchID;
	String typeName;
	String Icon;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBranchID() {
		return branchID;
	}
	public void setBranchID(String branchID) {
		this.branchID = branchID;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getIcon() {
		return Icon;
	}
	public void setIcon(String icon) {
		Icon = icon;
	}
	@Override
	public Object key() {
		return id;
	}

}
