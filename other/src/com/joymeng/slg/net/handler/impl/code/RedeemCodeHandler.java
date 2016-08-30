package com.joymeng.slg.net.handler.impl.code;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.code.CodeManager;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RedeemCodeHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 兑换码

	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null) {
			resp.fail();
			return resp;
		}
		String code = params.get(0);
		role.getCode();
        String message = CodeManager.getCodeByPost(role.getChannelId(), code, role.getId());
	//  String message = CodeManager.getCodeByPost("9999998", code,role.getId());//(仅测试用，上线需调整)
		if (StringUtils.isNull(message)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_USE_CODE_FAIL);
			resp.fail();
			return resp;
		}
		if (!role.getCode().sendMail(role,message)) {
			resp.fail();
		}
		return resp;
	}

}
