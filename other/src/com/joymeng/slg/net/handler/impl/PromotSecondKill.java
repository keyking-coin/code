package com.joymeng.slg.net.handler.impl;

import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class PromotSecondKill extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type); // 加速类型 0 道具 1 金币
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 需要加速的兵种Id
		if (type == 0) {
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 物品Id
			params.put(in.getInt()); // 数量
		}
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("PromotSecondKill getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int type = params.get(0);
		String armyId= params.get(1);
		RoleCityAgent agent = role.getCity(0);
		if (agent == null) {
			NewLogManager.misTakeLog("PromotSecondKill getRoleCityAgent is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		if (type == 0) {
			String itemId = params.get(2);
			int number = params.get(3);
			if (!agent.getArmyAgent().speedUp(role, armyId,itemId,number)) {
				resp.fail();
				return resp;
			}
		} else {
			if (!agent.getArmyAgent().secondKill(role, armyId)) {
				resp.fail();
				return resp;
			}
		}
		return resp;
	}

}
