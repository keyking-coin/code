package com.joymeng.slg.domain.object.army.data;

public class ArmyGroupID {
	int armyGroupId;
	int cityId;

	public ArmyGroupID() {
	}

	public ArmyGroupID(int armyGroupId, int cityId) {
		this.armyGroupId = armyGroupId;
		this.cityId = cityId;
	}

	public int getArmyGroupId() {
		return armyGroupId;
	}

	public void setArmyGroupId(int armyGroupId) {
		this.armyGroupId = armyGroupId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

}
