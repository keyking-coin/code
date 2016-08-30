package com.joymeng.slg.domain.object.technology.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Tech implements DataKey{
	//科技Id
	String id;
	//所属科技树的Id
	String techTreeID;
	//所属科技树的分支ID
	byte branchID;
	//科技描述
	String description;
	//名称
	String name;
	//前置条件
	List<String> precedingTechList;
	//最大等级
	int maxPoints;
	//研究条件，json表达式
	List<String> limitation;
	//图片Id
	String iconId;
	//cd时间1-被动技能无cd，0-主动技能，
	long cdTime;
	//技能持续时间
	long lastTime;
	//技能类型
	byte skillType;
	List<String> datatype;
	int number;
	int ranknumber;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTechTreeID() {
		return techTreeID;
	}
	public void setTechTreeID(String techTreeID) {
		this.techTreeID = techTreeID;
	}
	public byte getBranchID() {
		return branchID;
	}
	public void setBranchID(byte branchID) {
		this.branchID = branchID;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<String> getPrecedingTechList() {
		return precedingTechList;
	}
	public void setPrecedingTechList(List<String> precedingTechList) {
		this.precedingTechList = precedingTechList;
	}
	public int getMaxPoints() {
		return maxPoints;
	}
	public void setMaxPoints(int maxPoints) {
		this.maxPoints = maxPoints;
	}
	public List<String> getLimitation() {
		return limitation;
	}
	public void setLimitation(List<String> limitation) {
		this.limitation = limitation;
	}
	public String getIconId() {
		return iconId;
	}
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getCdTime() {
		return cdTime;
	}
	public void setCdTime(long cdTime) {
		this.cdTime = cdTime;
	}
	public long getLastTime() {
		return lastTime;
	}
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
	public byte getSkillType() {
		return skillType;
	}
	public void setSkillType(byte skillType) {
		this.skillType = skillType;
	}
	
	public List<String> getDatatype() {
		return datatype;
	}
	public void setDatatype(List<String> datatype) {
		this.datatype = datatype;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getRanknumber() {
		return ranknumber;
	}
	public void setRanknumber(int ranknumber) {
		this.ranknumber = ranknumber;
	}
	@Override
	public Object key() {
		return id;
	}
	
}
