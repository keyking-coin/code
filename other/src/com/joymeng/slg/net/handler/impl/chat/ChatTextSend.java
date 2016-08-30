package com.joymeng.slg.net.handler.impl.chat;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatGroup;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatRole;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgType;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.world.GameConfig;

public class ChatTextSend extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.get());// 聊天频道
		params.put(in.get());// 内容格式
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));// 聊天内容
		params.put(in.getLong());// 讨论组ID
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		byte chatType = params.get(0); // 聊天频道
		byte msgType = params.get(1); // 内容格式 0:普通文字聊天 1:语音消息 2:系统 3:喇叭消息
										// 4:联盟全体邮件
		String chatContent = params.get(2); // 聊天内容  头标志:0:普通消息/公告消息  1:战报分享  2:侦查报告分享
		long groupId = params.get(3); // 讨论组ID
		ChannelType channelType = ChannelType.valueOf(chatType);
		if (chatContent.length() < 1) {
			resp.fail();
			return resp;
		}
		String tString = chatContent.substring(0, 1);
		// 聊天字屏蔽
		if (tString.equals("0")) {
			chatContent = nameManager.chatWordsValid(chatContent);
		}
		ChatRole chatRole = new ChatRole(role);
		ChatMsg msg = new ChatMsg(chatContent, MsgTextColorType.COLOR_BLACK, channelType, msgType, (byte) 0, chatRole,
				null);
		switch (channelType) {
		case WORLD: { // 世界频道
			// 禁言
			// for(Forbid forbid : forbidMgr.getForbidList()){
			// if(forbid.getPlayerId()==role.getId()){
			// MessageSendUtil.sendNormalTip(info,
			// I18nGreeting.CHAT_WORLD_FAIL_IS_FORBID);
			// resp.fail();
			// return resp;
			// }
			// }
			// 发送间隔检查
			// if (System.currentTimeMillis()-
			// role.getChatAgent().getLast_world_chat_msg_date() <=
			// GameConfig.CHAT_INTERVAL_SECOND* Const.SECOND) {
			// MessageSendUtil.sendNormalTip(info,I18nGreeting.CHAT_WORLD_TOO_FAST);
			// resp.fail();
			// return resp;
			// }
			String groupType = "world"; // 群组类型
			// 发送消息的最小玩家等级需求
			if (role.getLevel() < GameConfig.CHAT_MIN_LEVEL) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_WORLD_MIN_LEVEL, GameConfig.CHAT_MIN_LEVEL);
				resp.fail();
				return resp;
			}
			if (msgType == MsgType.TYPE_HORN) { // 喇叭消息
				String itemId = "notice_horn";
				RoleBagAgent bagAgent = role.getBagAgent();
				ItemCell item = bagAgent.getItemFromBag(itemId);
				if (item == null || item.getNum() < 1) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_HORN_INSUFFICIENT);
					resp.fail();
					return resp;
				}
				if (!bagAgent.removeItems(itemId, 1)) {
					GameLog.error("remove notice_horn is fail");
					break;
				}
				chatMgr.addWorldNotice(msg);
				//发送背包变化
				RespModuleSet rms = new RespModuleSet();
				role.getBagAgent().sendBagToClient(rms);
				MessageSendUtil.sendModule(rms, role);
			}
			if (msgType == MsgType.TYPE_UNIONINVITE) { // 联盟公告邀请
				if (!role.redRoleMoney(GameConfig.NOTICE_INVITE_PRICE)) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NO_MONEY, GameConfig.NOTICE_INVITE_PRICE);
					resp.fail();
					return resp;
				}
				msg.setMsgType(MsgType.TYPE_HORN);
				chatMgr.addWorldNotice(msg);
				RespModuleSet rms = new RespModuleSet();
				role.sendRoleToClient(rms);
				MessageSendUtil.sendModule(rms, role);
			}
			// 发送信息
			chatMgr.addWorldChat(msg);
			chatMgr.sendWorldChatMsgsUpdate(msg);
			LogManager.chatLog(role, groupType, msg.getMsgType(), msg.getMsg());
			role.getChatAgent().setLastWorldChatMsgDate(TimeUtils.nowLong());
			// 任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_CHAT_WORLD, 0);
			break;
		}
		case GUILD: {
			// Guild guild = player.getGuildAgent().getGuild();
			// if (guild == null) {
			// MessageSendUtil.sendNormalTip(info,
			// I18nGreeting.CHAT_GUILD_NEED_JION);
			// resp.fail();
			// return resp;
			// }
			// guild.sendChatTextMsg(msg);
			// GameLog.logEvent(player, LogEvent.CHAT_SEND,
			// logBuffer.add(guild.getId()));
			// 发送间隔检查
			// if (System.currentTimeMillis()-
			// role.getChatAgent().getLast_world_chat_msg_date() <=
			// GameConfig.CHAT_INTERVAL_SECOND* Const.SECOND) {
			// MessageSendUtil.sendNormalTip(info,I18nGreeting.CHAT_WORLD_TOO_FAST);
			// resp.fail();
			// return resp;
			// }
			String groupType = "guild"; // 群组类型
			// 发送消息的最小玩家等级需求
			if (role.getLevel() < GameConfig.CHAT_MIN_LEVEL) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_WORLD_MIN_LEVEL, GameConfig.CHAT_MIN_LEVEL);
				resp.fail();
				return resp;
			}
			UnionBody unionBody = unionManager.search(role.getUnionId());
			if (unionBody == null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_NO_JOIN_UNION);
				resp.fail();
				return resp;
			}
			if (msgType == MsgType.TYPE_UNIONNOTICE) { // 联盟全体邮件
				chatMgr.sendUnionNotice(role, msg.getMsg());
			} else { // 发送信息
				chatMgr.addUnionChat(unionBody.getId(), msg);
				chatMgr.sendUnionChatMsgsUpdate(unionBody, msg);
				String message = "";
				byte type = 99 ;
				if (msg.getMsg().startsWith("1")) {
					message = "0战斗报告分享";
				} else if (msg.getMsg().startsWith("2")) {
					message = "0侦查报告分享";
				} else {
					message = msg.getMsg();
					type=msg.getMsgType();
				}
				LogManager.chatLog(role, groupType, type, message);
				role.getChatAgent().setLastWorldChatMsgDate(System.currentTimeMillis());
			}
			break;
		}
		case GROUP: { // 讨论组判定到 // 发送信息
			String groupType = "group"; // 群组类型
			ChatGroup group = chatMgr.getChatGroupByGroupId(groupId);
			if (group == null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_GROUP_NOT_EXIST);
				resp.fail();
				return resp;
			}
			msg.setGroupId(groupId);
			chatMgr.sendGroupChatMsgsUpdate(group, msg);
			LogManager.chatLog(role, groupType, msg.getMsgType(), msg.getMsg());
			role.getChatAgent().setLastWorldChatMsgDate(System.currentTimeMillis());
			break;
		}

		default: {
			GameLog.error(role + " ChatSend chattype [" + channelType + "] not exist, is error");
			MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_CHANNEL_CAN_NOT_SEND,
					channelType == null ? "" : channelType.getShowName());
			resp.fail();
		}
		}
		return resp;
	}

}
