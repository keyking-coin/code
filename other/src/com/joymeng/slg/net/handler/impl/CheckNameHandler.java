package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.CheckNameType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class CheckNameHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.get()); // checktype byte 1:要塞的名称
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 内容
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		byte type = params.get(0);
		String data = params.get(1);
		switch (type) {
		case CheckNameType.FORTRESS_NAME_TYPE:
			if (!nameManager.isNameLegal(data)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_DATA_NAME_ILLEGAL);
				resp.fail();
				return resp;
			}
			if (data.length() > GameConfig.FORTRESS_NAME_MAX || data.length() < GameConfig.FORTRESS_NAME_MIN) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_DATA_LENGTH_ILLEGAL);
				resp.fail();
				return resp;
			}
			break;
		case CheckNameType.UNION_TITLE_NAME_TYPE:
			if (!nameManager.isNameLegal(data)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_DATA_NAME_ILLEGAL);
				resp.fail();
				return resp;
			}
			break;
		default:
			break;
		}
		return resp;
	}
}
