package com.joymeng.slg.domain.object.technology.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Techtree implements DataKey{
	//科技树ID
	String id;
	//科技树名称
	String techTreeName;
	//科技树分支列表
	List<String> branchList;
	//图片类型
	int iconType;
	//图片ID
	String iconId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTechTreeName() {
		return techTreeName;
	}
	public void setTechTreeName(String techTreeName) {
		this.techTreeName = techTreeName;
	}
	public List<String> getBranchList() {
		return branchList;
	}
	public void setBranchList(List<String> branchList) {
		this.branchList = branchList;
	}
	public int getIconType() {
		return iconType;
	}
	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
	public String getIconId() {
		return iconId;
	}
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	
	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}
	

}
