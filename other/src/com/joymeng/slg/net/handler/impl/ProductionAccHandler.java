package com.joymeng.slg.net.handler.impl;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ProductionAccHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//建筑id(数据库主键)
		params.put(in.getInt());//是否金币建造
		params.put(in.getInt());//道具Id
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
//		int cityId = params.get(0);//城池id, int
//		long id = params.get(1);//建筑id, long
//		int state = params.get(2);//0-金币加速，1-道具加速
//		int itemId = params.get(3);//道具id
//		RoleCityAgent agent = role.getCity(cityId);
//		RoleBuild build = agent.searchBuildById(id);
//		if (build == null){
//			resp.fail();
//			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,id);
//			return resp;
//		}
//		BuildComponentProduction comProduction = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
//		if(!comProduction.productionAccelerate(role, cityId, id, state, itemId)){
//			resp.fail();
//		}
//		resp.add(id);
		return resp;
	}
}
