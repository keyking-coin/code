package com.joymeng.slg.net.handler.impl.code;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.RoleChatMail;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class MailItemsHandler extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getLong());//msgId long
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = world.getRole(info.getUid());
		if (role == null) {
			NewLogManager.misTakeLog("MailItemsHandler getRole is null where uid = " + info.getUid());
			resp.fail();
			return resp;
		}
		long msgId = params.get(0);
		resp.add(msgId);
		RoleChatMail roleMailBox = chatMgr.getRoleMail().get(role.getId());
		if (roleMailBox == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_MSG_ANNEXES_HAS_GOT);
			resp.fail();
			return resp;
		}
		ChatMsg msg = roleMailBox.getRoleChatMails().get(msgId);
		if (msg == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_MSG_ANNEXES_HAS_GOT);
			resp.fail();
			return resp;
		}

		List<BriefItem> msgAnnexes = msg.getMsgAnnex();
		if (msgAnnexes == null || msgAnnexes.size() < 1) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_MSG_ANNEXES_HAS_GOT);
			resp.fail();
			return resp;
		}
		List<ItemCell> items = new ArrayList<ItemCell>();
		for (int i = 0; i < msgAnnexes.size(); i++) {
			BriefItem briefItem = msgAnnexes.get(i);
			if (briefItem == null) {
				continue;
			}
			String itemId = briefItem.getItemId();
			String type = briefItem.getItemType();
			int number = briefItem.getNum();
			if (type.equalsIgnoreCase("Equip")) { // 装备
				items.addAll(role.getBagAgent().addEquip(itemId, number));
			} else if (type.equalsIgnoreCase("Item") || type.equalsIgnoreCase("test")) {//物品
				Item it = dataManager.serach(Item.class, itemId);
				if (it == null) {
					GameLog.error("email error itemId = " + itemId);
					continue;
				}
				if (it.getMaterialType() != 0) {
					items.addAll(role.getBagAgent().addOther(itemId, number));
				} else {
					items.addAll(role.getBagAgent().addGoods(itemId, number));
				}
			} else if(type.equalsIgnoreCase("Resources")){ // 资源
				ResourceTypeConst resource = ResourceTypeConst.search(itemId);
	            if (resource ==null){
	            	GameLog.error("email error resType = " + itemId);
	            	continue;
	            }
				boolean sendRole = false;
				switch (resource) {
				case RESOURCE_TYPE_USEREXP:
					role.addExp(number);
					sendRole = true;
					break;
				case RESOURCE_TYPE_FOOD:
				case RESOURCE_TYPE_METAL:
				case RESOURCE_TYPE_OIL:
				case RESOURCE_TYPE_ALLOY:
					for (RoleCityAgent city : role.getCityAgents()) {
						RoleCityAgent agent = role.getCity(city.getId());
						agent.addResource(resource, number,role);
						role.sendResourceToClient(null,city.getId(),resource,number);
					}
					break;
				case RESOURCE_TYPE_UNION_SCORE:
					UnionBody union = unionManager.search(role.getUnionId());
					if (union == null) {
						break;
					}
					long value = Math.max(0, union.getScore() + number);
					union.setScore(value);
					union.sendMeToAllMembers(0);
					break;
				case RESOURCE_TYPE_COIN:
					role.addRoleCopper(number);
					sendRole = true;
					break;
				case RESOURCE_TYPE_UNION_MEMBER_SCORE:
					UnionBody body = unionManager.search(role.getUnionId());
					if (body == null) {
						break;
					}
					UnionMember member = body.searchMember(role.getId());
					if (member != null) {
						long ns = Math.max(0, member.getScore() + number);
						member.setScore(ns);
						member.setScoreDaily(ns);
						member.setScoreWeekly(ns);
						member.setScoreRecord(ns);		
						body.sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_UPDATE);
					}
					break;
				case RESOURCE_TYPE_GOLD:
					role.addRoleMoney(number);
					sendRole = true;
					break;
				case RESOURCE_TYPE_GEM:
					role.addRoleGem(number);
					sendRole = true;
					break;
				case RESOURCE_TYPE_SILVER:
					role.addRoleSilver(number);
					sendRole = true;
					break;
				case RESOURCE_TYPE_KRYPTON:
					role.addRoleKrypton(number);
					sendRole = true;
					break;
				default:
					break;
				}
				if(sendRole){
					RespModuleSet rmsp = new RespModuleSet();
					role.sendRoleToClient(rmsp);
					MessageSendUtil.sendModule(rmsp, role.getUserInfo());
				}
			}else if(type.equalsIgnoreCase("Armys")){ //部队   
				List<RoleCityAgent> agents = role.getCityAgents();
				for (int m = 0; m < agents.size(); m++) {
					RoleCityAgent cityAgent = agents.get(m);
					RoleArmyAgent armyAgent = cityAgent.getCityArmys();
					List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
					ArmyInfo army = armyAgent.createArmy(itemId, number, ArmyState.ARMY_IN_NORMAL.getValue());
					armys.add(army);
					armyAgent.addClassArmys(armys);
					RespModuleSet rms = new RespModuleSet();
					armyAgent.sendToClient(rms, cityAgent);// 下发城里士兵状态
					MessageSendUtil.sendModule(rms, role.getUserInfo());
				}
			} else {
				GameLog.info("email error type:" + type + "cannot find");
			}
			LogManager.itemOutputLog(role, number, EventName.MailItemsHandler.getName(), itemId);
		}
		RespModuleSet rms = new RespModuleSet();
		ItemCell[] itemArray = items.toArray(new ItemCell[items.size()]);
		role.getBagAgent().sendItemsToClient(rms, itemArray);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_GET_MAIL_ITEMS_SUCC);
		return resp;
	}

}
