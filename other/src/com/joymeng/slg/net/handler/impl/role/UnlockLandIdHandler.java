package com.joymeng.slg.net.handler.impl.role;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Baseland;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;

public class UnlockLandIdHandler extends ServiceHandler{

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//解锁地块的ID
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		RespModuleSet rms = new RespModuleSet();
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		String landId = params.get(0);
		RoleCityAgent cityAgent = role.getCity(0);
		if (cityAgent == null) {
			resp.fail();
			return resp;
		}
		Baseland baseland = dataManager.serach(Baseland.class, landId);
		if (baseland == null) {
			GameLog.error("read baseland is fail");
			resp.fail();
			return resp;
		}
		//检测等级
		if (baseland.getUnlockCondition() > cityAgent.getCityCenterLevel()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNLOCK_LAND_NEED_ROLE_LEVEL,baseland.getUnlockCondition());
			resp.fail();
			return resp;
		}
		//检测金币
		if (role.getMoney() < baseland.getUnlockPrice()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,baseland.getUnlockPrice());
			resp.fail();
			return resp;
		}
		//扣除金币
		if(!role.redRoleMoney(baseland.getUnlockPrice())){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,baseland.getUnlockPrice());
			resp.fail();
			return resp;
		}
		LogManager.goldConsumeLog(role, baseland.getUnlockPrice(), EventName.UnlockLandIdHandler.getName());
		try {
			NewLogManager.buildLog(role, "unlock_field",landId,baseland.getUnlockPrice());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		//解锁
		if (cityAgent.getLandIds().contains(landId)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_UNLOCKED);
			resp.fail();
			return resp;
		}
		cityAgent.addLand(landId);
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_UNLOK_FIELD);
		//下发数据 
		role.sendRoleToClient(rms);
		cityAgent.sendToClient(rms, false);
		MessageSendUtil.sendModule(rms, role);
		return resp;
	}

}
