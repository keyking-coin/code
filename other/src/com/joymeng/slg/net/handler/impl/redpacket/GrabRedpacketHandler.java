package com.joymeng.slg.net.handler.impl.redpacket;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ChatSystemContent;
import com.joymeng.slg.domain.chat.ChatSystemContentType;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.redpacket.Redpacket;
import com.joymeng.slg.domain.object.redpacket.RedpacketMsg;
import com.joymeng.slg.domain.object.redpacket.RedpacketState;
import com.joymeng.slg.domain.object.redpacket.RoleRedpacket;
import com.joymeng.slg.domain.object.redpacket.SmallRedpacket;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class GrabRedpacketHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());// redpacketId long
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			NewLogManager.misTakeLog("Userinfo : " + info, "uid : " + info.getUid(),
					"className : " + this.getClass().getName(), "params : " + params);
			resp.fail();
			return resp;
		}
		long redpacketId = params.get(0);
		resp.add(redpacketId);//2 , 红包ID
		byte isGotOver = 1;
		resp.add(isGotOver);//3 , 是被领取完的标示
		//抢红包
		RespModuleSet rms = new RespModuleSet();
		RoleRedpacket roleRedpacket = role.getRoleRedpackets();
		Redpacket redpacket = rpManager.searchRedPacketById(redpacketId);
		if (redpacket == null) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_IS_INVALID);
			resp.fail();
			return resp;
		}
		synchronized (redpacket) {
			// 城市等级判断
			RoleCityAgent agent = role.getCity(0);
			if (agent == null) {
				GameLog.error("getCity" + 0 + "is null where uid = " + role.getId());
				resp.fail();
				return resp;
			}
			if (agent.getCityCenterLevel() < GameConfig.ROLE_REDPACKET_CITY_LV_LIMITE) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_CENTER_CITY_LV_INF,
						GameConfig.ROLE_REDPACKET_CITY_LV_LIMITE);
				resp.fail();
				return resp;
			}
			// 如果是联盟红包,判断同一个联盟
			if (redpacket.getType() == 2 && redpacket.getUnionId() != 0L && role.getUnionId() != redpacket.getUnionId()) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_IS_NULL);
				resp.fail();
				return resp;
			}
			// 红包是否已经退还(失效)
			if (redpacket.getState() == RedpacketState.INVALID) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_IS_INVALID);
				resp.fail();
				return resp;
			}
			// 是否已超出每日领取上限
			if (roleRedpacket.getCumulativeGotGold() >= GameConfig.ROLE_REDPACKET_GOT_DAILY_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_BEYOND_GOT_MAX);
				resp.fail();
				return resp;
			}
			// 是否已经领取过
			if (redpacket.containRole(role.getId())) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_HAVED_GOT);
				resp.fail();
				return resp;
			}
			// 红包是否已被领取完
			if (redpacket.isGotOver()) { // 不用给出提示,客户端自己做界面跳转
				resp.add(3, (byte) 0);
				resp.fail();
				return resp;
			}
			// 领取奖励
			int gotMoney = redpacket.computeRedpacketRoleGold();
			role.addRoleMoney(gotMoney);
			role.sendRoleToClient(rms);
			// 添加领取的用户
			SmallRedpacket gotRole = new SmallRedpacket(role.getId(), role.getName(), TimeUtils.nowLong(),
					redpacket.getUid(), redpacketId, gotMoney);
			redpacket.addGotRole(gotRole);
			// 设置领取相关数值
			RedpacketMsg redpacketMsg = new RedpacketMsg(redpacket.getItemId(), redpacket.getId());
			roleRedpacket.addGotRedpacketRole(redpacketMsg);
			roleRedpacket.addDayAndCumulativeGotGold(gotMoney);
			roleRedpacket.addDayAndCumulativeGotNum();
			// 下发数据
			roleRedpacket.sendClient(rms);
			MessageSendUtil.sendModule(rms, role);
			// 发送领取消息
			String systemChatPara = role.getName() + "|" + redpacket.getName();
			ChatSystemContent systemContent = new ChatSystemContent(
					ChatSystemContentType.CONTENT_TYPE_REDPACKET_GOT_INFO, systemChatPara);
			String chatContent = JsonUtil.ObjectToJsonString(systemContent);
			if (redpacket.getType() == 1) { // 世界频道
				chatMgr.generateOneMsgsToWorldAndSend(chatContent);
			} else if (redpacket.getType() == 2) {// 联盟频道
				chatMgr.generateOneMsgsToUnionAndSend(redpacket.getUnionId(), chatContent);
			}
		return resp;
		}
	}
}
