package com.joymeng.slg.net.handler.impl.actvt;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ReceiveActvtRewardHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());
		params.put(in.getInt());	
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		
		int actvtId = params.get(0);
		int index = params.get(1);
		if (!ActvtManager.getInstance().receiveActvtReward(role, actvtId, index)) {
			resp.fail();
		}
		return resp;
	}

}
