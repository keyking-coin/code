package com.joymeng.slg.net.handler.impl;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RoleSignHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // byte 0-七天签到  1-30天签到
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int signType = params.get(0);
		if (signType == 0) {
			if (!role.getSevenSignIn().signIn(role)) {
				resp.fail();
			}
		} else if (signType == 1) {
			if (!role.getThirtySignIn().signIn(role)) {
				resp.fail();
			}
		}

		return resp;
	}

}
