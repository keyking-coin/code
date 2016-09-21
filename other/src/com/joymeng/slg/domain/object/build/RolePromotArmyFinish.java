package com.joymeng.slg.domain.object.build;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RolePromotArmyFinish implements Instances,TimerOver{

	long uid;
	int cityId;
	ArmyInfo bArmy;
	ArmyInfo aArmy;

	public RolePromotArmyFinish(long uid, int cityId, ArmyInfo bArmy, ArmyInfo aArmy) {
		this.uid = uid;
		this.cityId = cityId;
		this.bArmy = bArmy;
		this.aArmy = aArmy;
	}
	@Override
	public void finish() {
		RespModuleSet rms = new RespModuleSet();
		Role role = world.getObject(Role.class,uid);
		RoleCityAgent agent = role.getCity(cityId);
		RoleArmyAgent roleArmyAgent = agent.getCityArmys();
		roleArmyAgent.updateArmysPromot(role, ArmyState.ARMY_IN_NORMAL.getValue(), bArmy, aArmy);
		roleArmyAgent.sendToClient(rms, agent);
		MessageSendUtil.sendModule(rms, role);
	}

}
