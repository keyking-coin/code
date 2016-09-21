package com.joymeng.slg.net.handler.impl.build;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentGem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildRemoveGemHandler extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in,ParametersEntity params) {
		params.put(in.getInt());//城市id
		params.put(in.getLong());//建筑id
//		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//物品id
		params.put(in.getInt());//物品索引
	}
	
	@Override
	public JoyProtocol handle(UserInfo info,ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			NewLogManager.misTakeLog("BuildRemoveGemHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId   = params.get(0);
		long buildId = params.get(1);
		String itemId = "";//params.get(2);
		int index = params.get(2);
		
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);
		if (build == null){
			resp.fail();
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_BUILD_NOT_FIND,buildId);
			return resp;
		}
		BuildComponentGem comGem = build.getComponent(BuildComponentType.BUILD_COMPONENT_GEM);
		if(comGem == null || !comGem.removeProductionGems(role, cityId, buildId, itemId, index)){
			resp.fail();
		}
		return resp;
	}
}
