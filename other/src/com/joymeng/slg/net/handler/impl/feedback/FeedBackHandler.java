package com.joymeng.slg.net.handler.impl.feedback;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class FeedBackHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 反馈来源
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 反馈内容	
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
		String   content = params.get(0);
		String come = params.get(1);
		FeedBackManager.getInstance().record(role.getId(), role.getChannelId(),come, content);
		FeedBackManager.getInstance().postFeedback(role.getId(), role.getChannelId(), come, content);
		return resp;
	}

}
