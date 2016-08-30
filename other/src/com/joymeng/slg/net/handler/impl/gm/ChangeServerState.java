package com.joymeng.slg.net.handler.impl.gm;

import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;

public class ChangeServerState extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal()) {
			params.put(in.getInt());//从哪个服务器来的
			params.put(in.get());//服务器状态变化
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal()) {
			int closeId = params.get(1);
			byte state  = params.get(2);
			serverManager.setState(closeId,state);
		}
		return null;
	}

}
