package com.joymeng.slg.net.handler.impl.build;

import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;

public class BuildPromotHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 晋级前兵种Id
		params.put(in.getInt());// 晋级数量
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 晋级后兵种Id
		params.put(in.getInt());// 是否金币秒

	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("BuildPromotHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		String beforeArmy = params.get(0);// 晋级前兵种Id
		int number = params.get(1);// 晋级数量
		String afterArmy = params.get(2);// 晋级后兵种Id
		int money = params.get(3);// 金币秒
		Army before = dataManager.serach(Army.class, beforeArmy);
		if (before == null) {
			GameLog.error("固化表错误 army is null ,error armyId = " + beforeArmy);
			resp.fail();
			return resp;
		}
		Army after = dataManager.serach(Army.class, afterArmy);
		if (after == null) {
			GameLog.error("固化表错误 army is null ,error armyId = " + afterArmy);
			resp.fail();
			return resp;
		}
		if (before.getSoldiersType() != after.getSoldiersType()) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_PROMOT_ERROR_TYPE);
			resp.fail();
			return resp;
		}
		List<RoleCityAgent> roleCity = role.getCityAgents();
		for (int i = 0; i < roleCity.size(); i++) {
			RoleCityAgent city = roleCity.get(i);
			RoleArmyAgent armyAgent = city.getArmyAgent();
			if (!armyAgent.promotArmy(role, beforeArmy, number, afterArmy, money)) {
				resp.fail();
				return resp;
			}
		}
		return resp;
	}

}
