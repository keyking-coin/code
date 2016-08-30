package com.joymeng.slg.net.handler.impl.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RoleMailLocalizeReslut extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int size = in.getInt();
		params.put(size); //本地化成功的邮箱消息 size
		for (int i = 0; i < size; i++) {
			params.put(in.getLong());	//本地化成功的邮箱消息 ID
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		// TODO Auto-generated method stub
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
		}
		int size = params.get(0);
		List<Long> mailMsgs = new ArrayList<Long>();
		for (int i = 0; i < size; i++) {
			mailMsgs.add(Long.parseLong(params.get(i + 1).toString()));
		}
		if (!chatMgr.removeRoleMailMsgs(role, mailMsgs)) {
			resp.fail();
		}
		return resp;
	}
	
}
