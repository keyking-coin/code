package com.joymeng.slg.net.handler.impl.rank;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RankHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); //rankId
		params.put(in.getInt()); //rankShowCount
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int rankId = params.get(0);
		@SuppressWarnings("unused")
		int rankShowCount = params.get(1);
		if (!rankManager.getRankResultByRankId(role,rankId)) {
			resp.fail();
			return resp;
		}
		return resp;
	}

}
