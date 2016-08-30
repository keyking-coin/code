package com.joymeng.slg.net.handler.impl.chat;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.impl.UnionInviteInfo;

public class ChatSearchRole extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		String name = params.get(0);
		if (name.trim().length() < 1) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_GROUP_SEARCH_ROLE_NAME_ISNT_NULL);
			resp.fail();
			return resp;
		}
		List<UnionInviteInfo> infos = world.fuzzySearchRole(role,name);
		if(infos.size() < 1){
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_DONT_SEARCH_ROLES);
		}
		resp.add(infos);
		return resp;
	}

}
