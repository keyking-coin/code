package com.joymeng.slg.net.handler.impl.equip;

import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentForging;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class EquipUpgradeOverHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());	//城市id
		params.put(in.getLong());	//建筑id
		params.put(in.getLong());	//装备数据库Id
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		long buildId = params.get(1);
		// long equipId=params.get(2);
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null) {
			GameLog.error("getCity" + cityId + "is null where uid = " + role.getId());
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(buildId);
		if (build == null) {
			GameLog.error("agent.searchBuildById is null buildId = " + buildId);
			resp.fail();
			return resp;
		}
		BuildComponentForging comEquipForge = build.getComponent(BuildComponentType.BUILD_COMPONENT_FORGING);
		if (comEquipForge == null) {
			GameLog.error("build.getComponent(BuildComponentType.BUILD_COMPONENT_FORGING) is null , buildId = " + buildId); 
			resp.fail();
			return resp;
		}
		if (!comEquipForge.EquipUpgradeOver(role, cityId, buildId)) {
			resp.fail();
			return resp;
		}
		return resp;
	}

}
