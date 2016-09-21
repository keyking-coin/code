package com.joymeng.slg.net.handler.impl;

import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class MoneySecKillHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());// 城市id
		params.put(in.getLong());// 建筑数据库主键
		params.put(in.getInt());// 是否消耗金币 0-免费   1-金币加速
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("MoneySecKillHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);//
		long id = params.get(1);// 建筑id
		int money = params.get(2);// 是否消耗金币
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null) {
			NewLogManager.misTakeLog("MoneySecKillHandler getRoleCityAgent is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(id);
		if (build == null) {
			NewLogManager.misTakeLog("MoneySecKillHandler getRoleBuild is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		if (!build.secondKill(role, money)) {
			resp.fail();
			return resp;
		}
		return resp;
	}
}
