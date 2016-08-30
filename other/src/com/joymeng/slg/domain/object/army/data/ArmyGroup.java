package com.joymeng.slg.domain.object.army.data;

import java.util.ArrayList;
import java.util.List;

public class ArmyGroup {
	ArmyGroupID armyGroupId;
	List<ArmyBriefInfo> armys = new ArrayList<ArmyBriefInfo>();

	public ArmyGroup() {
	}

	public ArmyGroup(int groupId, int cityId) {
		ArmyGroupID armyGroupID = new ArmyGroupID(groupId, cityId);
		this.armyGroupId = armyGroupID;
		this.armys = new ArrayList<>();
	}

	public void addArmyToArmtGroup(ArmyBriefInfo armyBriefInfo) {
		if (armyBriefInfo == null) {
			return;
		}
		boolean t = false;
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyBriefInfo temp = armys.get(i);
			if (temp.getArmyPos() == armyBriefInfo.getArmyPos()) {
				temp.setArmyId(armyBriefInfo.getArmyId());
				temp.setArmyNum(armyBriefInfo.getArmyNum());
				t = true;
			}
		}
		if (!t) {
			armys.add(armyBriefInfo);
		}
	}

	public ArmyGroupID getArmyGroupId() {
		return armyGroupId;
	}

	public void setArmyGroupId(ArmyGroupID armyGroupId) {
		this.armyGroupId = armyGroupId;
	}

	public List<ArmyBriefInfo> getArmys() {
		return armys;
	}

	public void setArmys(List<ArmyBriefInfo> armys) {
		this.armys = armys;
	}

}
