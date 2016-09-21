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

public class ResetArmyPointsHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//int cityId
		params.put(in.getLong());//long buildId
		params.put(in.getInt());//int 道具使用类型0-消耗道具，1-消耗金币
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
		int type = params.get(2);
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);
		if(build == null || build.getPointsAgent() == null){
			GameLog.error("agent.searchBuildById is null buildId = " + buildId);
			resp.fail();
			return resp;
		}
		if(!build.getPointsAgent().resetArmysPoints(role, type)){
			resp.fail();
			return resp;
		}
		return resp;
	}
	

}
