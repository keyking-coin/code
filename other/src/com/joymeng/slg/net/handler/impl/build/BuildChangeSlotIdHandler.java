package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildChangeSlotIdHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//建筑1的BaseBuildingSlot的编号
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//建筑2的BaseBuildingSlot的编号
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId   = params.get(0);
		String slotId1 = params.get(1);
		String slotId2 = params.get(2);
		
		RoleCityAgent agent = role.getCity(cityId);
		if(!agent.changeBuildsSlot(role, slotId1, slotId2)){
			resp.fail();
		}

		return resp;
	}
}
