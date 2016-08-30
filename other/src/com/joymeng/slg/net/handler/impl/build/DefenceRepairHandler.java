package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class DefenceRepairHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id int
		params.put(in.getLong());//建筑id long 
		params.put(in.getInt());//是否金币直接修复 int
		params.put(in.get());//修理类型0-城墙，1-防御建筑
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
		long buildId = params.get(1);
		int money = params.get(2);
		byte type = params.get(3);
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null){
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(buildId);
		if(build == null){
			resp.fail();
		}else{
			if(type == 0){
				BuildComponentWall comDefense = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
				if(comDefense == null || !comDefense.repairDefense(role, cityId, buildId, money)){
					resp.fail();
				}
			}else{
				BuildComponentDefense comDefense = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
				if(comDefense == null || !comDefense.repairDefenseArmys(role, cityId, buildId, money)){
					resp.fail();
				}
			}
		}
		return resp;
	}
}
