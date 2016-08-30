package com.joymeng.slg.net.handler.impl.build;

import java.util.List;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class RandomMoveCity extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());//城市id int
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		int cityId   = params.get(0);
		MapCity mapCity = mapWorld.searchMapCity(role.getId(), cityId);
		mapCity.moveByRandom();
		RoleCityAgent cityAgent = role.getCity(cityId);
		List<RoleBuild> builds = cityAgent.searchBuildByBuildId(BuildName.FENCE.getKey());
		RespModuleSet rms = new RespModuleSet();
		for (RoleBuild build : builds){
			BuildComponentWall com = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
			if (com != null){
				com.reinforceWall(role,build);
				build.sendToClient(rms);
			}
		}
		MessageSendUtil.sendModule(rms, info);
		return resp;
	}

}
