package com.joymeng.slg.net.handler.impl.build;

import java.util.ArrayList;
import java.util.List;

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
import com.joymeng.slg.domain.object.build.impl.BuildComponentCure;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildCureHandler extends ServiceHandler {
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());	//城市id
		params.put(in.getLong());	//建筑固化id
		params.put(in.getInt());	//是否金币建造
		//伤兵列表
		int size = in.getInt();
		params.put(size);
		for(int i=0; i< size; i++){
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//兵种ID
			params.put(in.getInt());										//兵的数量
		}
	}	
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			NewLogManager.misTakeLog("BuildCureHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId   = params.get(0);
		Long buildId = params.get(1);
		int money = params.get(2);
		//伤兵列表5
		int size = params.get(3);
		List<String> strArmys = new ArrayList<String>();
		for(int index=4,i=0; i < size; i++){
			strArmys.add(String.valueOf(params.get(index++)));
			strArmys.add(params.get(index++).toString());
		}
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null){
			GameLog.error("getCity" + cityId + "is null where uid = " + role.getId());
			resp.fail();
			return resp;
		}
		RoleBuild build = agent.searchBuildById(buildId);
		if(build == null){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,buildId);
			resp.fail();
			return resp;
		}
		BuildComponentCure comArmyCure = build.getComponent(BuildComponentType.BUILD_COMPONENT_CURE);
		if(comArmyCure == null || !comArmyCure.cureArmys(role, cityId, buildId, strArmys, money)){
			resp.fail();
		}
		resp.add(buildId);
		return resp;
	}
}
