package com.joymeng.slg.net.handler.impl.chat;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class ChatGroupNameChange extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // 类型 0-检测合法性 1-修改名称
		params.put(in.getLong()); // 组Id
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 修改后的名字
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
		int handlerType = params.get(0); // 协议类型
		long groupId = params.get(1); // 讨论组ID
		String groupName = params.get(2); // 修改后的名字
		groupName = groupName.trim();
		resp.add(handlerType);
		if (handlerType == 0) {
			if (StringUtils.countStringLength(groupName) < GameConfig.GROUP_NAME_MIN
					|| StringUtils.countStringLength(groupName) > GameConfig.GROUP_NAME_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL_LENGTH, groupName);
				resp.fail();
				return resp;
			}
			if (!nameManager.isNameCharLegal(groupName, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(groupName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_GROUP_NAME_ILLEGALITY_SENSITIVE);
				resp.fail();
				return resp;
			}
			if (groupName.length() < 1 || groupName.length() > 16) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_GROUP_NAME_ILLEGALITY_LENGTH);
				resp.fail();
				return resp;
			}
		} else {
			if (StringUtils.countStringLength(groupName) < GameConfig.GROUP_NAME_MIN
					|| StringUtils.countStringLength(groupName) > GameConfig.GROUP_NAME_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL_LENGTH, groupName);
				resp.fail();
				return resp;
			}
			if (!nameManager.isNameCharLegal(groupName, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(groupName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_GROUP_NAME_ILLEGALITY_SENSITIVE);
				resp.fail();
				return resp;
			}
			if (groupName.length() < 1 || groupName.length() > 16) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.CHAT_GROUP_NAME_ILLEGALITY_LENGTH);
				resp.fail();
				return resp;
			}
			if (!chatMgr.changeGroupName(role, groupId, groupName)) {
				resp.fail();
				return resp;
			}
		}
		return resp;
	}
}
