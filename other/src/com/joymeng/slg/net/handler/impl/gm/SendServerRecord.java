package com.joymeng.slg.net.handler.impl.gm;

import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;

public class SendServerRecord extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal()) {
			params.put(in.getInt());//从哪个服务器来的
			params.put(in.getLong());//玩家uid
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//服务器实例号（传多了）
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal()) {
			long uid = params.get(2);
			String serverId  = params.get(3);
			serverManager.record(uid,serverId);
		}
		return null;
	}

}
