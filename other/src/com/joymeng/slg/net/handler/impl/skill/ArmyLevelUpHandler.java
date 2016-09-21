package com.joymeng.slg.net.handler.impl.skill;

import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ArmyLevelUpHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int cityId
		params.put(in.getLong());//long buildId
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//armyId
//		params.put(in.getInt());//level
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		long buildId = params.get(1);
		String armyId = params.get(2);
		int level = 1;
		RoleCityAgent agent = role.getCity(cityId);
		if(agent == null){
			GameLog.error("getCity" + cityId + "is null where uid = " + role.getId());
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(buildId);
		if(build == null || build.getPointsAgent() == null){
			GameLog.error("agent.searchBuildById is null buildId = " + buildId);
			resp.fail();
			return resp;
		}
		if(!build.getPointsAgent().armyLevelup(role, armyId, level)){
			resp.fail();
		}
		return resp;
	}
}
