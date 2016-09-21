package com.joymeng.slg.net.handler.impl.role;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class ChangeRoleNameHandler extends ServiceHandler implements Instances {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getInt()); // 类型 0-检测合法性 1-修改名称
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 用户修改的名称
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params) throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null) {
			resp.fail();
			return resp;
		}
		int handlerType = params.get(0);
		String newName = params.get(1);
		resp.add(handlerType);
		if (handlerType == 0) {
			if (StringUtils.countStringLength(newName) < GameConfig.ROLE_NAME_MIN || StringUtils.countStringLength(newName) > GameConfig.ROLE_NAME_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL_LENGTH, newName);
				resp.fail();
				return resp;
			}
			if (!nameManager.isNameCharLegal(newName, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(newName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL_SENSITIVE, newName);
				resp.fail();
				return resp;
			}
			if (nameManager.check(newName) == 0 || role.getName().equals(newName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_REPEAT, newName);
				resp.fail();
				return resp;
			}
		} else if (handlerType == 1) {
			if (StringUtils.countStringLength(newName) < GameConfig.ROLE_NAME_MIN || StringUtils.countStringLength(newName) > GameConfig.ROLE_NAME_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL_LENGTH, newName);
				resp.fail();
				return resp;
			}
			if (!nameManager.isNameCharLegal(newName, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(newName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_ILLEGAL, newName);
				resp.fail();
				return resp;
			}
			if (nameManager.check(newName) == 0 || role.getName().equals(newName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NAME_REPEAT, newName);
				resp.fail();
				return resp;
			}
			RoleBagAgent bagAgent = role.getBagAgent();
			if (bagAgent == null) {
				GameLog.error("role.getBagAgent() is null role.uid = " + role.getId());
				resp.fail();
				return resp;
			}
			String itemId = "modify_userName";
			byte type = bagAgent.getItemNumFromBag(itemId) > 0 ? (byte) 0 : (byte) 1;
			Item itemdata = dataManager.serach(Item.class, itemId);
			if (itemdata == null) {
				GameLog.error("item: " + itemId + "static data not found.");
				resp.fail();
				return resp;
			}
			if (!bagAgent.useItem(role, itemId, 1, type, 0)) { // 扣除资源
				resp.fail();
				return resp;
			}
			if (!role.changeRoleName(newName)) { // 修改名字
				resp.fail();
				return resp;
			}
			try {
				if(type==0){
					NewLogManager.baseEventLog(role, "commander_rename",itemId);
				}else{
					Shop shop = dataManager.serach(Shop.class, new SearchFilter<Shop>(){
						@Override
						public boolean filter(Shop data) {
							return data.getItemid().equals("modify_userName");
						}
					});
					NewLogManager.baseEventLog(role, "commander_rename",shop.getSaleSprice());
				}			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
		}
		return resp;
	}

}
