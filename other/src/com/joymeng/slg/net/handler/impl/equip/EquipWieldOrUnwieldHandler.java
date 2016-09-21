package com.joymeng.slg.net.handler.impl.equip;

import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class EquipWieldOrUnwieldHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());										//正在操作的装备数据库Id Long
		params.put(in.getInt()); 										//操作类型0-装备 1-卸下
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		long equipId = params.get(0);
		int operationState = params.get(1);
		RoleBagAgent roleBagAgent = role.getBagAgent();
		if (roleBagAgent == null) {
			GameLog.error("getBagAgent is error role.uid = " + role.getId());
			resp.fail();
			return resp;
		}
		if (operationState == 0) { // 给角色装备上装备
			if (roleBagAgent == null || !roleBagAgent.equipWield(role, equipId)) {
				resp.fail();
				return resp;
			}
		} else if (operationState == 1) { // 卸下装备
			if (roleBagAgent == null || !roleBagAgent.equipUnwield(role, equipId)) {
				resp.fail();
				return resp;
			}
		}
		return resp;
	}

}
