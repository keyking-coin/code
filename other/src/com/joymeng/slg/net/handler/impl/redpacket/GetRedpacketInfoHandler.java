package com.joymeng.slg.net.handler.impl.redpacket;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.redpacket.Redpacket;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GetRedpacketInfoHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());// 红包的Id long
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("Userinfo : " + info, "uid : " + info.getUid(),
					"className : " + this.getClass().getName(), "params : " + params);
			resp.fail();
			return resp;
		}
		long redpacketId = params.get(0);
		Redpacket redpacket = rpManager.searchRedPacketById(redpacketId);
		if (redpacket == null) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_IS_INVALID);
			resp.fail();
			return resp;
		}
		resp.add(redpacket);
		return resp;
	}
}
