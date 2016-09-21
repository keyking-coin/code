package com.joymeng.slg.net.handler.impl;

import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class FireArmyHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//兵种id
		params.put(in.getInt());//解雇数量
		params.put(in.get());//兵种的状态
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("FireArmyHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);// 城市ID
		String armyId = params.get(1);// 兵种id
		int num = params.get(2);// 解雇数量
		byte armyState = params.get(3);// 兵种的状态
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null) {
			GameLog.error("getCity cityid = " + cityId + "is null");
			resp.fail();
			return resp;
		}
		RoleArmyAgent armyAgent = agent.getCityArmys();
		if (armyAgent == null) {
			GameLog.error("getCityArmys cityid = " + cityId + "is null");
			resp.fail();
			return resp;
		}
		if (!armyAgent.disMissArmy(role, armyId, num, armyState)) {
			resp.fail();
			return resp;
		}
		return resp;
	}
}
