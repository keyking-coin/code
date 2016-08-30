package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentProduction;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class HarvestResourceHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put( in.getInt() );//城池Id
		params.put( in.getLong() );//建筑数据库主键
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp( info );
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);//城池id, int
		long id = params.get(1);//建筑id, long
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(id);
		if (build == null){
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,id);
			return resp;
		}
		BuildComponentProduction comProduction = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
		if(comProduction == null){
			resp.fail();
		}
		long collectNum = comProduction.collectResource(role,build);
		if (collectNum == 0){
			resp.fail();
			return resp;
		}
		resp.add(id);
		resp.add(collectNum);
		return resp;
	}

}
