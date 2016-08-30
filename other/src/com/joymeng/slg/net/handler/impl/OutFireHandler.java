package com.joymeng.slg.net.handler.impl;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class OutFireHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城池id
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception{
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		RoleCityAgent agent = role.getCity(cityId);
		if( agent == null || !agent.outFireState(role)){
			resp.fail();
			return resp;
		}
		return resp;
	}
}
