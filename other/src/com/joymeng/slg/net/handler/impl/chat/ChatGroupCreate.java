package com.joymeng.slg.net.handler.impl.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ChatGroupCreate extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int size = in.getInt();
		params.put(size); // size
		for (int i = 0; i < size; i++) {
			params.put(in.getLong());
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("Userinfo : " + info, "uid : " + info.getUid(),
					"className : " + this.getClass().getName(), "params : " + params);
			resp.fail();
			return resp;
		}
		int size = params.get(0);
		List<Long> uids = new ArrayList<Long>();
		for (int i = 0; i < size; i++) {
			uids.add(Long.parseLong(params.get(i + 1).toString()));
		}
		if (!chatMgr.newGroupChat(role, uids)) {
			resp.fail();
			return resp;
		}
		try {
			NewLogManager.baseEventLog(role, "create_conversation");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return resp;
	}

}
