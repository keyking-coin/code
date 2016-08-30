package com.joymeng.slg.net.handler.impl.role;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class AddRoleBlacklistHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong()); //uid long
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		long otherUid = params.get(0);
		Role other = world.getRole(otherUid);
		if (other == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_INEXISTENCE);
			resp.fail();
			return resp;
		}
		if(role.getBlacklist().contains(otherUid)){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_HAS_BEEN_IN_BLACKLIST);
			resp.fail();
			return resp;
		}
		if(!role.addRoleToBlacklist(otherUid)){
			resp.fail();
			return resp;
		}
		MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ADD_ROLE_BLACKLIST_SUC);
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleBlacklist(rms);
		MessageSendUtil.sendModule(rms, role);
		return resp;
	}
}
