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

public class RemoveBuildHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//建筑数据库主键
		params.put(in.getInt());//是否金币升级
	}

	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			NewLogManager.misTakeLog("RemoveBuildHandler getRole is null where uid = "+info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);
		long id = params.get(1);
		int money = params.get(2);
		RoleCityAgent agent = role.getCity(cityId);
		byte code = agent.removeBuild(role, id, money);
		if(code != 0){
			resp.fail();
			resp.add(code);
		}
		return resp;
	}

}
