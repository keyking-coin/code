package com.joymeng.slg.net.handler.impl;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDeal;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class ClearTransactionCD extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt());// 城市id
		params.put(in.getLong());// 建筑数据库主键
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("ClearTransactionCD getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		int cityId = params.get(0);// 城市ID
		long id = params.get(1);// 建筑id
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.getBuild();
		if (build == null) {
			NewLogManager.misTakeLog("ClearTransactionCD getRoleBuild is null where uid = " + info.getUid());
			resp.fail();
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_BUILD_NOT_FIND, id);
			return resp;
		}
		BuildComponentDeal deal = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEAL);
		if (deal == null || !build.clearTranCd(role)) {
			resp.fail();
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_CHG_TRAN_CD, id);
			return resp;
		}
		return resp;
	}

}
