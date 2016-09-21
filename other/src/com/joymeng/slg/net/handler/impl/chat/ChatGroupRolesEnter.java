package com.joymeng.slg.net.handler.impl.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ChatGroupRolesEnter extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());	// 聊天组ID
		int size = in.getInt();
		params.put(size);			// 成员列表大小
		for (int i = 0; i < size; i++) {
			params.put(in.getLong());	// 成员id
		}
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
		long groupId = params.get(0);
		int size = params.get(1);
		List<Long> rolesList = new ArrayList<Long>();
		for (int i = 0; i < size; i++) {
			rolesList.add(Long.parseLong(params.get(i + 2).toString()));
		}
		if (!chatMgr.inviteRolesTojoinGroup(role, groupId, rolesList)) {
			resp.fail();
			return resp;
		}
		return resp;
	}

}
