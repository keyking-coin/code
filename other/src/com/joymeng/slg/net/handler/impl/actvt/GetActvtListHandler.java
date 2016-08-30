package com.joymeng.slg.net.handler.impl.actvt;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.actvt.ActvtManager;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GetActvtListHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception 
	{
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null)
		{
			resp.fail();
			return resp;
		}
		
		ActvtManager.getInstance().sendActvtListReq(role);
		return resp;
	}
}
