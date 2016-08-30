package com.joymeng.slg.net.handler.impl.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class GetMsgAnnex extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		// TODO Auto-generated method stub
		
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
		List<BriefItem> items = new ArrayList<>();
		for (int i = 0 ; i < items.size() ; i++){
			BriefItem briefItem = items.get(i);
			role.getBagAgent().addOther(briefItem.getItemId(), briefItem.getNum());
		}
		return null;
	}

}
