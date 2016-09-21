package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentResearch;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class TechLevelupHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put( in.getInt() );//城池Id
		params.put( in.getLong() );//建筑数据库主键
		params.put( in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT) );//科技Id
		params.put( in.getInt());//money
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp( info );
		Role role = getRole(info);
		if (role == null){
			NewLogManager.misTakeLog("TechLevelupHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);//城池id, int
		long id = params.get(1);//建筑id, long
		String techId = params.get(2);//科技Id，String
		int money = params.get(3);//是否金币直接升级
		RoleCityAgent agent = role.getCity(cityId);
		if(agent == null){
			GameLog.error("getCity" + cityId + "is null where uid = " + role.getId());
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(id);
		if (build == null){
			NewLogManager.misTakeLog("TechLevelupHandler getRoleBuild is null where uid = " + info.getUid());
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,id);
			return resp;
		}
		BuildComponentResearch comProduction = build.getComponent(BuildComponentType.BUILD_COMPONENT_RESEARCH);
		if(!comProduction.upgradeTech(role, cityId, id, techId, money)){
			resp.fail();
		}
		return resp;
	}
}
