package com.joymeng.http.handler.impl.gm;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Const;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.log.LogManager;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.data.ItemType;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.bag.impl.GoodsItem;
import com.joymeng.slg.domain.object.bag.impl.OtherItem;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.Guide;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class HttpGmRoleMgr extends HttpHandler {

	@Override
	public boolean handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		if (ServiceApp.FREEZE){
			message(response,"服务器已关闭");
			return false;
		}
		String action = request.getParameter("action");
		switch (action){
			case "addGold":{
				String uStr = request.getParameter("gold_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String nStr = request.getParameter("gold_num");
				if (StringUtils.isNull(nStr)){
					message(response,"金币数量不能为空");
					return false;
				}
				int money = Integer.parseInt(nStr);
				role.addRoleMoney(money);
				String event= "HttpGmRoleMgr";
				LogManager.goldOutputLog(role, money, event);
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"添加金币成功");
				break;
			}
			case "clearGold":{
				String uStr = request.getParameter("gold_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				role.redRoleMoney(role.getMoney());
				String event = "HttpGmRoleMgr";
				LogManager.goldConsumeLog(role, role.getMoney(), event);
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"金币清理成功");
				break;
			}
			case "addItem":{
				String uStr = request.getParameter("item_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String key  = request.getParameter("item_name");
				String nStr = request.getParameter("item_num");
				if (StringUtils.isNull(nStr)){
					message(response,"道具数量不能为空");
					return false;
				}
				int num = Integer.parseInt(nStr);
				RespModuleSet rms = new RespModuleSet();
				List<ItemCell> cells = new ArrayList<ItemCell>();
				String event = "HttpGmRoleMgr";
				if (key.equals("all_items")){
					List<Item> items = dataManager.serachList(Item.class);
					for (int i = 0 ; i < items.size() ; i++){
						Item item = items.get(i);
						if (item.getMaterialType() > 0 ||
							item.getItemType() == ItemType.TYPE_TURNTABLE_BOX ||
							item.getItemType() == ItemType.TYPE_SUDOKU_MULTI){
							continue;
						}
						List<ItemCell> temp = role.getBagAgent().addGoods(item.getId(),num);
						LogManager.itemOutputLog(role, num, event, item.getId());
						cells.addAll(temp);
					}
				}else{
					LogManager.itemOutputLog(role,num,event, key);
					cells.addAll(role.getBagAgent().addGoods(key,num));
				}
				if (cells.size() == 0){
					message(response,"添加道具失败");
					return false;
				}
				if (role.isOnline()){
					role.getBagAgent().sendItemsToClient(rms,cells);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"添加道具成功");
				break;
			}
			case "addEquip":{
				String uStr = request.getParameter("equip_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String key  = request.getParameter("equip_name");
				String nStr = request.getParameter("equip_num");
				if (StringUtils.isNull(nStr)){
					message(response,"装备数量不能为空");
					return false;
				}
				RespModuleSet rms = new RespModuleSet();
				int num = Integer.parseInt(nStr);
				List<ItemCell> cells = new ArrayList<ItemCell>();
				String event = "HttpGmRoleMgr";
				if (key.equals("all_equips")){
					List<Equip> equips = dataManager.serachList(Equip.class);
					for (int i = 0 ; i < equips.size() ; i++){
						Equip equip = equips.get(i);
						List<ItemCell> temp = role.getBagAgent().addEquip(equip.getId(),num);
						LogManager.itemOutputLog(role, num, event,equip.getId());
						LogManager.equipLog(role,equip.getEquipType(),equip.getBeizhuname(),"后台添加");
						cells.addAll(temp);
					}
				}else{
					LogManager.itemOutputLog(role,num,event,key);
					cells.addAll(role.getBagAgent().addEquip(key,num));
				}
				if (cells.size() == 0){
					message(response,"添加装备失败");
					return false;
				}
				if (role.isOnline()){
					role.getBagAgent().sendItemsToClient(rms,cells);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"添加装备成功");
				break;
			}
			case "addStone":{
				String uStr = request.getParameter("stone_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String key  = request.getParameter("stone_name");
				String nStr = request.getParameter("stone_num");
				if (StringUtils.isNull(nStr)){
					message(response,"材料数量不能为空");
					return false;
				}
				int num = Integer.parseInt(nStr);
				RespModuleSet rms = new RespModuleSet();
				List<ItemCell> cells = new ArrayList<ItemCell>();
				String event = "HttpGmRoleMgr";
				if (key.equals("all_stones")){
					List<Item> items = dataManager.serachList(Item.class);
					for (int i = 0 ; i < items.size() ; i++){
						Item item = items.get(i);
						if (item.getMaterialType() == 0){
							continue;
						}
						List<ItemCell> temp = role.getBagAgent().addOther(item.getId(),num);
						LogManager.itemOutputLog(role, num, event,item.getId());
						cells.addAll(temp);
					}
				}else{
					LogManager.itemOutputLog(role,num,event,key);
					cells.addAll(role.getBagAgent().addOther(key,num));
				}
				if (cells.size() == 0){
					message(response,"添加材料失败");
					return false;
				}
				if (role.isOnline()){
					role.getBagAgent().sendItemsToClient(rms,cells);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"添加材料成功");
				break;
			}
			case "addRes":{
				String uStr = request.getParameter("res_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String key  = request.getParameter("res_name");
				String nStr = request.getParameter("res_num");
				String op   = request.getParameter("res_operation");
				if (StringUtils.isNull(nStr)){
					message(response,"资源数量不能为空");
					return false;
				}
				int num = Integer.parseInt(nStr);
				if (op.equals("red")){
					num *= -1;
				}
				boolean clear = op.equals("clear");
				ResourceTypeConst type = ResourceTypeConst.search(key);
				if (type != null){
					if (type.ordinal() > ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal()){
						boolean sendRole = false;
						switch (type){
							case RESOURCE_TYPE_KRYPTON:{
								if (clear){
									num = -role.getKrypton();
								}
								role.addRoleKrypton(num);
								sendRole = true;
								break;
							}
							case RESOURCE_TYPE_COIN:{
								if (clear){
									num = -role.getCopper();
								}
								role.addRoleCopper(num);
								sendRole = true;
								break;
							}
							case RESOURCE_TYPE_GEM:{
								if (clear){
									num = -role.getGem();
								}
								role.addRoleGem(num);
								sendRole = true;
								break;
							}
							case RESOURCE_TYPE_SILVER:{
								if (clear){
									num = -role.getSilver();
								}
								role.addRoleSilver(num);
								sendRole = true;
								break;
							}
							case RESOURCE_TYPE_UNION_MEMBER_SCORE:{
								UnionBody union = unionManager.search(role.getUnionId());
								if (union == null){
									message(response,"未加入联盟，无法添加");
									return false;
								}
								UnionMember member = union.searchMember(uid);
								if (member != null){
									if (clear){
										member.setScore(0);
									}else{
										long ns = Math.max(0,member.getScore()+num);
										long rs = Math.max(0,member.getScoreRecord() + num);
										if (rs > member.getScoreRecord()){
											member.setScoreRecord(rs);
										}
										member.setScore(ns);
									}
									RespModuleSet rms = member.sendToClient(ClientModule.DATA_TRANS_TYPE_UPDATE);
									MessageSendUtil.sendModule(rms,role.getUserInfo());
								}else{
									message(response,"修改失败");
									return false;
								}
								break;
							}
							case RESOURCE_TYPE_UNION_SCORE:{
								UnionBody union = unionManager.search(role.getUnionId());
								if (union == null){
									message(response,"找不到联盟");
									return false;
								}
								if (clear){
									union.setScore(0);
								}else{
									long value = Math.max(0,union.getScore() + num);
									union.setScore(value);
								}
								union.sendMeToAllMembers(0);
								break;
							}
							case RESOURCE_TYPE_STAMINA:{
								if (num > Const.MAXSTAMINA){
									message(response,"体力的数值不能大于100");
									return false;
								}
								if (clear){
									num = -role.getRoleStamina().getCurStamina();
								}
								role.getRoleStamina().updateCurStamina(num);
								break;
							}
							case RESOURCE_TYPE_USEREXP:{
								if (num <= 0){
									message(response,"经验不能为负数");
									return false;
								}
								if (clear){
									message(response,"经验不支持清除操作");
									return false;
								}
								role.addExp(num);
								sendRole = true;
								break;
							}
							default :{
								break;
							}
						}
						if (sendRole){
							RespModuleSet rms = new RespModuleSet();
							role.sendRoleToClient(rms);
							MessageSendUtil.sendModule(rms,role.getUserInfo());
						}
					}else{
						String sCity  = request.getParameter("res_cityId");
						if (StringUtils.isNull(sCity)){
							message(response,"城市编号不能为空");
							return false;
						}
						int cityId = Integer.parseInt(sCity);
						RoleCityAgent city = role.getCity(cityId);
						if (city == null){
							message(response,"错误的城市编号");
							return false;
						}
						if (clear){
							num = (int)city.getResource(type);
							city.redResource(type,num);
							num *= -1;
						}else{
							city.addResource(type,num,role);
						}
						role.sendResourceToClient(null,cityId,type,num);
					}
					String event = "HttpGmRoleMgr";
					String item  = type.getKey();
					LogManager.itemOutputLog(role, num, event, item);
					message(response,"修改成功");
				}else{
					message(response,"修改失败");
				}
				break;
			}
			case "kickRole":{
				String uStr = request.getParameter("kick_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				if (world.kick(uid)){
					message(response,"成功");
				}else{
					message(response,"失败");
				}
				break;
			}
			case "buildUp":{
				String uStr = request.getParameter("build_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String sCity  = request.getParameter("build_cityId");
				if (StringUtils.isNull(sCity)){
					message(response,"城市编号不能为空");
					return false;
				}
				int cityId = Integer.parseInt(sCity);
				RoleCityAgent agent = role.getCity(cityId);
				if (agent == null){
					message(response,"城市编号不对");
					return false;
				}
				String nStr = request.getParameter("build_num");
				if (StringUtils.isNull(nStr)){
					message(response,"建筑等级不能为空");
					return false;
				}
				String operate = request.getParameter("build_operation");
				byte num = Byte.parseByte(nStr);
				List<RoleBuild> builds = null;
				if (operate.equals("AllBuild")){
					builds = agent.getBuilds();
				}else{
					Building building = dataManager.serach(Building.class,operate);
					if (building.getMaxLevel() < num){
						message(response,"建筑已经满级");
						return false;
					}
					builds = agent.searchBuildByBuildId(operate);
				}
				for(RoleBuild build : builds){
					Building building = dataManager.serach(Building.class,build.getBuildId());
					if(building.getMaxLevel() == 1 || building.getMaxLevel() < num){
						continue;
					}
					for(int i = build.getLevel() ; i < num; i++){
						build.leveupFinish(false);
					}
				}
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					agent.sendToClient(rms, true);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"升级成功");
				break;
			}
			case "addSkill":{
				String uStr = request.getParameter("skill_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String nStr = request.getParameter("skill_num");
				if (StringUtils.isNull(nStr)){
					message(response,"技能点不能为空");
					return false;
				}
				int num = Integer.parseInt(nStr);
				role.addSkillPoints(num);
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"修改技能点成功");
				break;
			}
			case "changeVip": {
				String uStr = request.getParameter("vip_uid");
				if (StringUtils.isNull(uStr)) {
					message(response, "用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null) {
					message(response, "错误的用户编号");
					return false;
				}
				String nStr = request.getParameter("vip_num");
				if (StringUtils.isNull(nStr)) {
					message(response, "VIP时间不能为空");
					return false;
				}
				int num = Integer.parseInt(nStr);
				TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, num, TimerLastType.TIME_VIP);
				role.getVipInfo().setTimer(timer);
				role.getVipInfo().setActive(true);
				timer.registTimeOver(role.getVipInfo());
				if (role.isOnline()) {
					RespModuleSet rms = new RespModuleSet();
					role.getVipInfo().sendVipToClient(rms);// 下发VIP消息
					MessageSendUtil.sendModule(rms, role.getUserInfo());
				}
				message(response, "修改VIP时间成功");
				break;
			}
			case "clcGuides": {
				String uStr = request.getParameter("guide_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				List<Guide> allGuides = dataManager.serachList(Guide.class);
				List<String> gs = new ArrayList<>();
				for (int i = 0; i < allGuides.size(); i++) {
					Guide temp = allGuides.get(i);
					if (temp != null) {
						gs.add(temp.getId());
					}
				}
				role.getGuideIdList().clear();
				role.getGuideIdList().addAll(gs);
				if (role.isOnline()) {
					RespModuleSet rms = new RespModuleSet();
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				message(response,"清除成功");
				break;
			}
			case "troopsChange":{
				String uStr = request.getParameter("troops_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				String cityStr = request.getParameter("troops_cityId");
				String key  = request.getParameter("troops_id");
				String lStr = request.getParameter("troops_level");
				String nStr = request.getParameter("troops_num");
				String op   = request.getParameter("troops_operation");
				if (StringUtils.isNull(nStr)){
					message(response,"部队数量不能为空");
					return false;
				}
				if (StringUtils.isNull(cityStr)){
					message(response,"请输入城市编号");
					return false;
				}
				int cityId = Integer.parseInt(cityStr);
				int num = Integer.parseInt(nStr);
				RoleCityAgent city = role.getCity(cityId);
				if (city == null){
					message(response,"错误的城市编号");
					return false;
				}
				RoleArmyAgent armyAgent = city.getArmyAgent();
				List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
				if (key.equals("allTroops")){
					List<Army> datas = dataManager.serachList(Army.class,new SearchFilter<Army>(){
						@Override
						public boolean filter(Army data) {
							return data.getArmyType() < ArmyType.HOOK.ordinal() && data.getArmycamp() == 0;
						}
					});
					for (int i = 0 ; i < datas.size() ; i++){
						Army army = datas.get(i);
						ArmyInfo ai = armyAgent.createArmy(army.getId(),num,ArmyState.ARMY_IN_NORMAL.getValue());
						armys.add(ai);
					}
				}else if (key.equals("allHooks")){
					List<Army> datas = dataManager.serachList(Army.class,new SearchFilter<Army>(){
						@Override
						public boolean filter(Army data) {
							return data.getArmyType() == ArmyType.HOOK.ordinal();
						}
					});
					for (int i = 0 ; i < datas.size() ; i++){
						Army army = datas.get(i);
						ArmyInfo ai = armyAgent.createArmy(army.getId(),num,ArmyState.ARMY_IN_NORMAL.getValue());
						armys.add(ai);
					}
				}else{
					ArmyInfo ai = armyAgent.createArmy(key + "_" + lStr,num,ArmyState.ARMY_IN_NORMAL.getValue());
					armys.add(ai);
				}
				if (op.equals("red")){//减少
					armyAgent.removeClassArmys(armys);
				}else{//增加
					armyAgent.addClassArmys(armys);
				}
				RespModuleSet rms = new RespModuleSet();
				armyAgent.sendToClient(rms,city);//下发城里士兵状态
				MessageSendUtil.sendModule(rms,role.getUserInfo());
				message(response,"修改成功");
				break;
			}
			case "bag_clear":{
				String uStr = request.getParameter("bag_clear_uid");
				if (StringUtils.isNull(uStr)){
					message(response,"用户编号不能为空");
					return false;
				}
				long uid = Long.parseLong(uStr);
				Role role = world.getRole(uid);
				if (role == null){
					message(response,"错误的用户编号");
					return false;
				}
				RoleBagAgent bag = role.getBagAgent();
				String type  = request.getParameter("bag_clear_type");
				List<ItemCell> ics = null;
				switch(type){
					case "item":{
						ics = bag.removeColumn(GoodsItem.class);
						break;
					}
					case "equip":{
						ics = bag.removeColumn(EquipItem.class);
						break;
					}
					case "material":{
						ics = bag.removeColumn(OtherItem.class);
						break;
					}
				}
				if (ics.size() > 0){
					RespModuleSet rms = new RespModuleSet();
					bag.sendItemsToClient(rms,ics);
					MessageSendUtil.sendModule(rms,role);
					message(response,"清理成功");
				}else{
					message(response,"清理失败");
				}
				break;
			}
		}
		return false;
	}
}
