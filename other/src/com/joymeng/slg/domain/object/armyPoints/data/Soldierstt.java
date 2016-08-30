package com.joymeng.slg.domain.object.armyPoints.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Soldierstt implements DataKey{
	String id;
	int techTreeID;
	int branchID;
	String branchname;
	String armyName;
	String armydes;
	String precedingTech;
	int maxPoints;
	List<String> limitation;
	String iconId;
	List<String> skillID;
	int ranknum;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTechTreeID() {
		return techTreeID;
	}
	public void setTechTreeID(int techTreeID) {
		this.techTreeID = techTreeID;
	}
	public int getBranchID() {
		return branchID;
	}
	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}
	public String getBranchname() {
		return branchname;
	}
	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}
	public String getArmyName() {
		return armyName;
	}
	public void setArmyName(String armyName) {
		this.armyName = armyName;
	}
	public String getArmydes() {
		return armydes;
	}
	public void setArmydes(String armydes) {
		this.armydes = armydes;
	}
	public String getPrecedingTech() {
		return precedingTech;
	}
	public void setPrecedingTech(String precedingTech) {
		this.precedingTech = precedingTech;
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
	public List<String> getSkillID() {
		return skillID;
	}
	public void setSkillID(List<String> skillID) {
		this.skillID = skillID;
	}
	public int getRanknum() {
		return ranknum;
	}
	public void setRanknum(int ranknum) {
		this.ranknum = ranknum;
	}
	@Override
	public Object key() {
		return id;
	}

}
