package com.joymeng.slg.domain.object.skill;

public class BranchInfo {
	private String branchId;
	private int branchNum;
	
	public BranchInfo(String branchId, int num){
		this.branchId = branchId;
		this.branchNum = num;
	}
	
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public int getBranchNum() {
		return branchNum;
	}
	public void setBranchNum(int branchNum) {
		this.branchNum = branchNum;
	}
	
}
