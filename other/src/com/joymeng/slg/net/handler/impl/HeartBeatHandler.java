package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class HeartBeatHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		Role role = world.getOnlineRole(info.getUid());
		if (role != null){
			role.heartBeat();
		}
		//GameLog.info(info.getUid() + " >>>>>>> do HeartBeat");
		CommunicateResp resp = newResp(info);
		long time = TimeUtils.nowLong() / 1000;
		resp.add(time);
		return resp;
	}

}
