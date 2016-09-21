package com.joymeng.slg.net.handler.impl.union;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.expression.ProtoExpression;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.data.Alliance;
import com.joymeng.slg.world.GameConfig;

public class UnionCreateHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.get());//类型 0 检测名字是否合法;1检查简称是否合法; 2创建联盟
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//名称
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));	//简称
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		byte  type         = params.get(0);
		String name        = params.get(1);
		String shortName   = params.get(2);
		resp.add(type);		
		if (type <= 1) {
			if (type == 0) {
				if (StringUtils.countStringLength(name) < GameConfig.UNION_NAME_MIN
						|| StringUtils.countStringLength(name) > GameConfig.UNION_NAME_MAX) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_ILLEGALITY_LENGTH);
					resp.fail();
					return resp;
				}
				if (!nameManager.isNameCharLegal(name, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
						|| !nameManager.isNameLegal(name)) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
					resp.fail();
				}
				UnionBody union = unionManager.search(name);
				if (union != null) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_NAME_HAVE_USED, name);
					resp.fail();
					return resp;
				}
			}
			if (type == 1) {
				if (StringUtils.countStringLength(shortName) != GameConfig.UNION_SHORTNAME_LIMIT) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_SHORTNAME_ILLEGALITY_LENGTH);
					resp.fail();
					return resp;
				}
				if (!nameManager.isNameCharLegal(shortName, GameConfig.REGEX_UPPER_LETTER_NUMBER)
						|| !nameManager.isNameLegal(shortName)) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
					resp.fail();
				}
				UnionBody union = unionManager.searchShortName(shortName);
				if (union != null) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_SHORT_NAME_HAVE_USED, shortName);
					resp.fail();
					return resp;
				}
			}
		} else {
			if (StringUtils.countStringLength(name) < GameConfig.UNION_NAME_MIN
					|| StringUtils.countStringLength(name) > GameConfig.UNION_NAME_MAX) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_ILLEGALITY_LENGTH);
				resp.fail();
				return resp;
			}
			if (StringUtils.countStringLength(shortName) != GameConfig.UNION_SHORTNAME_LIMIT) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_SHORTNAME_ILLEGALITY_LENGTH);
				resp.fail();
				return resp;
			}
			if (!nameManager.isNameCharLegal(name, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(name)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
				resp.fail();
			}
			if (!nameManager.isNameCharLegal(shortName, GameConfig.REGEX_UPPER_LETTER_NUMBER)
					|| !nameManager.isNameLegal(shortName)) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_NAME_OR_SHORTNAME_ILLEGALITY_SENSITIVE);
				resp.fail();
			}
			UnionBody union = unionManager.search(name);
			if (union != null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_NAME_HAVE_USED, name);
				resp.fail();
				return resp;
			}
			union = unionManager.searchShortName(shortName);
			if (union != null) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_SHORT_NAME_HAVE_USED, shortName);
				resp.fail();
				return resp;
			}
			if (role.getLevel() < 1) {
				MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_ROLE_LEVEL_NO);
				resp.fail();
				return resp;
			}
			if (role.getUnionId() != 0) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_JOINED_IN_UNION); // 提示创建成功
				resp.fail();
				return resp;
			}
			RespModuleSet rms = new RespModuleSet();
			String createUnionItemId = "allianceLicence";
			if (role.getBagAgent().getItemNumFromBag(createUnionItemId) > 0) { // 有创建联盟的道具
																				// 优先使用道具创建联盟
				ItemCell itemCell = role.getBagAgent().getItemFromBag(createUnionItemId);
				if (!role.getBagAgent().removeItems(createUnionItemId, 1)) {
					MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ITEM_NOT_ENOUGH, createUnionItemId, 1);
					resp.fail();
					return resp;
				}
				role.getBagAgent().sendItemsToClient(rms, itemCell);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			} else {
				Alliance alliance = dataManager.serach(Alliance.class, new SearchFilter<Alliance>() {
					@Override
					public boolean filter(Alliance data) {
						return data.getLevel() == 1;
					}
				});
				String needStr = alliance.getNeed();
				int needMoney = Integer.parseInt(needStr.split(">=")[1]);
				String str = needStr.replaceAll("uid", String.valueOf(role.getId()));
				try {
					Object result = ProtoExpression.ExecuteExpression(str);
					boolean flag = Boolean.parseBoolean(result.toString());
					if (!flag) {// 不满足创建条件
						resp.fail();
						MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_ROLE_NO_MONEY, needMoney);
						return resp;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				role.redRoleMoney(needMoney);
				role.sendRoleToClient(rms);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
				LogManager.goldConsumeLog(role, needMoney, EventName.UnionCreateHandler.getName());
				NewLogManager.unionLog(role, "create_alliance",needMoney);
			}
			union = unionManager.create(role, name, shortName);
			if (union == null) {
				GameLog.error("unionManager create union is null");
				resp.fail();
				return resp;
			}
			MessageSendUtil.sendNormalTip(info, I18nGreeting.MSG_UNION_CREATE_SUCCESS); // 提示创建成功
		}
		return resp;
	}
}
