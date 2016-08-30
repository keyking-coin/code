package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class OpenBagHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception{
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null){
			resp.fail();
			return resp;
		}
		RoleBagAgent bagAgent = role.getBagAgent();
		if(bagAgent == null){
			resp.fail();
		}
		RespModuleSet rms = new RespModuleSet();
		bagAgent.sendBagToClient(rms);
		MessageSendUtil.sendModule(rms,info);
		return resp;
	}
}
