package com.joymeng.slg.net.handler.impl.chat;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;

public class ChatMsgsGetOneSuit extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); 	//请求的类型 1-世界 2-联盟 3-群组
		params.put(in.getLong()); 	//开始的MsgId 
		params.put(in.getInt()); 	//msg的个数
		params.put(in.getLong()); 	//是世界-0 联盟-联盟的Id 群组-群组Id
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		// TODO Auto-generated method stub
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int type = params.get(0);	// 请求的类型
		switch (type) {
		case 1: {	//世界
			long msgsMaxId = params.get(1);
			int msgsCount = params.get(2);
			if (!chatMgr.SendOneSuitWorldMsgs(role,msgsMaxId,msgsCount)) {
				resp.fail();
				return resp;
			}
			break;
		}
		case 2: {	//联盟
			long msgsMaxId = params.get(1);
			int msgsCount = params.get(2);
			long unionId = params.get(3);
			UnionBody unionBody = unionManager.search(unionId);
			if (unionBody==null) {
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_UNION_NOT_FIND);
				resp.fail();
				return resp;
			}
			if (!chatMgr.SendOneSuitUnionMsgs(role, unionId, msgsMaxId,msgsCount)) {
				resp.fail();
				return resp;
			}
			break;
		}
		case 3: {	//群组Id

			break;
		}
		}
		
		return resp;
	}

}
