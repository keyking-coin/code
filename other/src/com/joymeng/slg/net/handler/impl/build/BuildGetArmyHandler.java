package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentArmyTrain;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildGetArmyHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//建筑数据库主键
	}

	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);//城池id, int
		long id = params.get(1);//建筑id, long
		//GameLog.info("handle BuildGetArmy where build="+id);
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(id);
		if (build == null){
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,id);
			return resp;
		}
		BuildComponentArmyTrain comArmyTrain = build.getComponent(BuildComponentType.BUILD_COMPONENT_ARMYTRAIN);
		if(!comArmyTrain.getTrainArmy(role, agent, build)){
			resp.fail();
		}
		resp.add(id);
		return resp;
	}
}

