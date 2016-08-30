package com.joymeng.slg.net.handler.impl.task;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GetHonorAwardHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//荣誉任务ID
		params.put(in.getInt());//要领取哪个子任务奖励 ID
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null){
			resp.fail();
			return resp;
		}
		String honorMId = params.get(0);
        int  count=params.get(1);
		
		if(honorMId==null || count<=0||count>5){
			resp.fail();
			return resp;
		}
		if(!role.getHonorAgent().getHonorReward(role,honorMId,count)){
			resp.fail();
		}
		return resp;
	}
	
}
