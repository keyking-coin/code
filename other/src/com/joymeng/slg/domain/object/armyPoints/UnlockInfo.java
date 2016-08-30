package com.joymeng.slg.domain.object.armyPoints;

public class UnlockInfo {
	String armyId;
	int techTreeId;
	int branchId;
	int level=1;
//	int state;
	public UnlockInfo(){
		
	}
	public static UnlockInfo create(String armyId, int techTreeId, int branchId){
		UnlockInfo info = new UnlockInfo();
		info.armyId = armyId;
		info.techTreeId = techTreeId;
		info.branchId = branchId;
		info.level = 1;
		return info;
	}
	public String getArmyId() {
		return armyId;
	}
	public void setArmyId(String armyId) {
		this.armyId = armyId;
	}
	public int getTechTreeId() {
		return techTreeId;
	}
	public void setTechTreeId(int techTreeId) {
		this.techTreeId = techTreeId;
	}
	public int getBranchId() {
		return branchId;
	}
	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
//	public int getState() {
//		return state;
//	}
//	public void setState(int state) {
//		this.state = state;
//	}
	
}
