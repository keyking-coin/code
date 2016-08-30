package com.joymeng.slg.domain.object.army.data;

public class ArmyBriefInfo {
	String armyId;
	int armyNum;
	int armyPos;//1前，2中，3后, 4空中
	public ArmyBriefInfo() {

	}

	public ArmyBriefInfo(String armyId, int armyNum,int armyPos) {
		this.armyId = armyId;
		this.armyNum = armyNum;
		this.armyPos = armyPos;
	}

	public String getArmyId() {
		return armyId;
	}

	public void setArmyId(String armyId) {
		this.armyId = armyId;
	}

	public int getArmyNum() {
		return armyNum;
	}

	public void setArmyNum(int armyNum) {
		this.armyNum = armyNum;
	}

	public int getArmyPos() {
		return armyPos;
	}

	public void setArmyPos(int armyPos) {
		this.armyPos = armyPos;
	}

}
