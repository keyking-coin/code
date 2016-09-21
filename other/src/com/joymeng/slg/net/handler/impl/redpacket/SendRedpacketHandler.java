package com.joymeng.slg.net.handler.impl.redpacket;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatRole;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgType;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.redpacket.Redpacket;
import com.joymeng.slg.domain.object.redpacket.RedpacketMsg;
import com.joymeng.slg.domain.object.redpacket.RedpacketState;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.world.GameConfig;

public class SendRedpacketHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 红包道具的id String
		params.put(in.get());// 聊天频道  byte
		params.put(in.getLong());// 讨论组ID 预留着
		params.put(in.getInt());// 金币数 int
		params.put(in.getInt());// 红包数量 int
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 祝福语 String
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
		String itemId = params.get(0);// 红包道具的id
		byte chatType = params.get(1);// 聊天频道
		@SuppressWarnings("unused")
		long groupId = params.get(2);// 讨论组ID
		int money = params.get(3);// 金币数
		int num = params.get(4);// 红包数量
		String greetings = params.get(5);// 祝福语
		ChannelType channelType = ChannelType.valueOf(chatType);
		// 对应条件的检测
		// 主城等级的检查
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
		// 金额的检测
		if (money < GameConfig.ROLE_REDPACKET_GOLD_MIN || money > GameConfig.ROLE_REDPACKET_GOLD_MAX) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_GOLD_BEYOND_LIMITE);
			resp.fail();
			return resp;
		}
		// 用户拥有的金币检测
		if (role.getMoney() < money) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NO_MONEY, money);
			resp.fail();
			return resp;
		}
		//红包个数超出最大限制
		if (num > money / 10) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_BEYOND_MAX_NUM);
			resp.fail();
			return resp;
		}
		// 红包个数的检测
		if (channelType.ordinal() == ChannelType.WORLD.ordinal()) {// 世界频道
			if (num < GameConfig.ROLE_REDPACKET_NUM_WORLD_MIN) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_NUM_WORLD_LITTE,
						GameConfig.ROLE_REDPACKET_NUM_WORLD_MIN);
				resp.fail();
				return resp;
			}
		} else if (channelType.ordinal() == ChannelType.GUILD.ordinal()) {// 联盟频道
			if (role.getUnionId() == 0 || unionManager.search(role.getUnionId()) == null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_NO_JOIN_UNION);
				resp.fail();
				return resp;
			}
			if (num < GameConfig.ROLE_REDPACKET_NUM_UNION_MIN) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_NUM_UNION_LITTE,
						GameConfig.ROLE_REDPACKET_NUM_UNION_MIN);
				resp.fail();
				return resp;
			}
		} else {// 错误频道
			GameLog.error("client send channelType is error!");
			resp.fail();
			return resp;
		}
		// 祝福语的合法性(长度)
		if (StringUtils.countStringLength(greetings) < 1
				|| StringUtils.countStringLength(greetings) > GameConfig.ROLE_REDPACKET_GREETING_LENGTH) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_GREETING_LENGTH_ILLEGAL);
			resp.fail();
			return resp;
		}
		// 祝福语的合法性(敏感字)
		if (!nameManager.isNameLegal(greetings)) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_REDPACKET_GREETING_SENSITION_ILLEGAL);
			resp.fail();
			return resp;
		}
		// 道具的个数检查
		RoleBagAgent bagAgent = role.getBagAgent();
		ItemCell item = bagAgent.getItemFromBag(itemId);
		if (item == null || item.getNum() < 1) {
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_HORN_INSUFFICIENT);
			resp.fail();
			return resp;
		}
		// 扣除道具
		if (!bagAgent.removeItems(itemId, 1)) {
			GameLog.error("remove " + itemId + " is fail");
			resp.fail();
			return resp;
		}
		RespModuleSet rms = new RespModuleSet();
		bagAgent.sendItemsToClient(rms, item);
		// 扣除金币
		if (!role.redRoleMoney(money)) {
			GameLog.error("remove money is fail");
			resp.fail();
			return resp;
		}
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		// 记录用户红包
		Redpacket redpacket = rpManager.createRedpacket(role, itemId, RedpacketState.NORMAL, chatType, role.getUnionId(),
				greetings, money, num);
		role.getRoleRedpackets().addRoleSendRedpacket(redpacket);
		role.sendRoleRedpackets();
		// 创建红包信息体并发送到对应的频道
		RedpacketMsg redpacketMsg = new RedpacketMsg(itemId, redpacket.getId());
		String chatContent = JsonUtil.ObjectToJsonString(redpacketMsg);
		ChatRole chatRole = new ChatRole(role);
		ChatMsg msg = new ChatMsg(chatContent, MsgTextColorType.COLOR_BLACK, channelType, MsgType.TYPE_REDPACKET,
				ReportType.TYPE_DEFAULT, chatRole, null);
		if (channelType.ordinal() == ChannelType.WORLD.ordinal()) {// 世界频道
			// 发送信息
			chatMgr.addWorldChat(msg);
			chatMgr.sendWorldChatMsgsUpdate(msg);
			role.getChatAgent().setLastWorldChatMsgDate(TimeUtils.nowLong());
		} else if (channelType.ordinal() == ChannelType.GUILD.ordinal()) {// 联盟频道
			UnionBody unionBody = unionManager.search(role.getUnionId());
			if (unionBody == null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_NO_JOIN_UNION);
				resp.fail();
				return resp;
			}
			chatMgr.addUnionChat(unionBody.getId(), msg);
			chatMgr.sendUnionChatMsgsUpdate(unionBody, msg);
			role.getChatAgent().setLastWorldChatMsgDate(TimeUtils.nowLong());
		} else {// 错误频道
			GameLog.error("client send channelType is error!");
			resp.fail();
			return resp;
		}
		return resp;
	}

}
