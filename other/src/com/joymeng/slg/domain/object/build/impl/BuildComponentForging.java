package com.joymeng.slg.domain.object.build.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.bag.impl.OtherItem;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 锻造组件
 * @author tanyong
 */
public class BuildComponentForging implements BuildComponent, Instances {
	private BuildComponentType buildComType;
	long srcEId;// key
	byte state = 0; // 建筑组件的状态: 0:未使用 1:升级装备中 2:升级完成 3:成功 4:失败
	long uid;
	int cityId;
	long buildId;

	public BuildComponentForging() {
		buildComType = BuildComponentType.BUILD_COMPONENT_FORGING;
	}

	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityID;
		this.buildId = buildId;
	}

	/**
	 * @param 装备升级
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param equipId
	 * @param costMaterialLst
	 * @return
	 */
	public boolean upgradeEquipment(Role role, int cityId, long buildId, long equipId, List<String> costMaterialLst) {
		srcEId = equipId;
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);
		if (build.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		if (build.getState() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_STATE_WRONG, build.getId());
			return false;
		}
		// 检查装备升级条件
		RoleBagAgent bagAgent = role.getBagAgent();
		EquipItem item = bagAgent.getEquipById(equipId);
		if (item == null || item.getState() != 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED, equipId);
			return false;
		}
		// 检测物品
		Equip eq = dataManager.serach(Equip.class, item.getKey());
		if (eq.getUpgradeEquipID().equals("0")) { // 检测装备的等级是否为最高等级
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUPGRADE, equipId);
			return false;
		}
		List<String> costLst = eq.getUpgradeCost();
		for (int i = 0 ; i < costLst.size() ; i++){
			String cost = costLst.get(i);
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find upgrade equip costs where equipId = " + item.getKey());
				return false;
			}
			String resourceId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			//buff 
			float resourceBuff = 0.0F;
			List<RoleBuild> hBuilds = agent.searchBuildByBuildId(BuildName.EQUIP_LAB.getKey());
			if (hBuilds.size() > 0) {
				Buildinglevel hb = hBuilds.get(0).getBuildingLevel();
				if (hb != null) {
					resourceBuff = Float.valueOf(hb.getParamList().get(1));
				}
			}
			num = (int) (num * (1.0f - resourceBuff));
			if (!resourceId.equals("krypton")) {
				GameLog.error("equip base data krypton is error");
				return false;
			}
			if (num > role.getKrypton()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_KRYPTON, num);
				return false;
			}
		}
		// 检测材料
		List<String> baseMaterials = eq.getUpgradeMaterial();
		if (baseMaterials.size() != costMaterialLst.size()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT);
			return false;
		}
		for (int index = 0; index < baseMaterials.size(); index++) {
			String cost = costMaterialLst.get(index);
			String[] costMaterialArray = cost.split("\\|");
			if (costMaterialArray.length < 2) {
				GameLog.error("cannot find costMaterialArray where equipId =" + item.getKey());
				return false;
			}
			String itemId = costMaterialArray[0];
			// int num = Integer.parseInt(costMaterialArray[1]);
			String need = baseMaterials.get(index);
			String[] needMaterialLst = need.split(":");
			if (needMaterialLst.length < 2) {
				GameLog.error("cannot find needMaterialLst where equipId =" + item.getKey());
				return false;
			}
			int baseMaterialType = Integer.parseInt(needMaterialLst[0]);
			int baseNum = Integer.parseInt(needMaterialLst[1]);
			Item material = dataManager.serach(Item.class, itemId);
			if (material == null) {
				GameLog.error("cannot find material from item base data!");
				return false;
			}
			if (material.getMaterialType() != baseMaterialType) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_TYPE_NOT_SAME);
				return false;
			}
			if (!bagAgent.checkItemFromBag(itemId, baseNum)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT,
						material.getId());
				return false;
			}
		}
		//扣除金币
		for (int i = 0 ; i < costLst.size() ; i++){
			String cost = costLst.get(i);
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find upgrade equip costs where equipId =" + item.getKey());
				return false;
			}
			int num = Integer.parseInt(costArray[1]);
			//resourceBuff 值
			float resourceBuff = 0.0F;
			List<RoleBuild> hBuilds = agent.searchBuildByBuildId(BuildName.EQUIP_LAB.getKey());
			if (hBuilds.size() > 0) {
				Buildinglevel hb = hBuilds.get(0).getBuildingLevel();
				if (hb != null) {
					resourceBuff = Float.valueOf(hb.getParamList().get(1));
				}
			}
			num = (int) (num * (1.0f - resourceBuff));
			if (role.redRoleKrypton(num) == false) {
				GameLog.error("krypton is insufficient!");
				return false;
			}
			String event = "upgradeEquipment";
			String items ="krypton";
			LogManager.itemConsumeLog(role, num, event, items);
		}
		// 删除消耗-材料
		ItemCell[] materials = new ItemCell[costMaterialLst.size()];
		for (int i = 0; i < costMaterialLst.size(); ++i) {
			String[] costArray = costMaterialLst.get(i).split("\\|");
			String itemId = costArray[0];
			int num = Integer.parseInt(baseMaterials.get(i).split(":")[1]);
			ItemCell cell = bagAgent.getItemFromBag(itemId);
			bagAgent.removeItems(itemId, num);
			LogManager.itemConsumeLog(role, num, "upgradeEquipment", itemId);
			ItemCell tempCell = bagAgent.getItemFromBag(itemId);
			if (tempCell == null) {
				tempCell = cell;
				tempCell.setNum(0);
			}
			materials[i] = tempCell;
		}
		item.setUpgradeMaterialLists(costMaterialLst);// 记录升级用的材料列表

		long upgradetime = eq.getUpgradeTime();
		//add buff时间
		float timeBuff = agent.getRoleBuildBaseBuffValue(BuildName.EQUIP_LAB.getKey());
		upgradetime = (long) (upgradetime * (1.0f - timeBuff));
		TimerLast timer = build.addBuildTimer((long) upgradetime, TimerLastType.TIME_UP_EQUIP);
		timer.registTimeOver(this);
		state = 1;
		item.setEquipState((byte) 2); // 设置装备状态正在升级中
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		build.sendToClient(rms);
		bagAgent.sendItemsToClient(rms, item);
		bagAgent.sendItemsToClient(rms, materials);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		LogManager.equipLog(role, eq.getEquipType(), eq.getBeizhuname(), "强化装备");
		
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < costMaterialLst.size(); i++) {
				String cost = costMaterialLst.get(i);
				sb.append(cost);
				sb.append(GameLog.SPLIT_CHAR);
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.buildLog(role, "equip_upgrade", eq.getId(),newStr);
			} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_LVLUP);
		return true;
	}

	/**
	 * @param 装备升级结束
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @return
	 */
	public boolean EquipUpgradeOver(Role role, int cityId, long buildId) {
		RespModuleSet rms = new RespModuleSet();
		RoleBagAgent agent = role.getBagAgent();
		List<EquipItem> equipLst = agent.getEquipByEquipState((byte) 2); // 获取正在升级的装备
		if (equipLst == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_EQUIP_UNUSED);
			//强行把建筑的状态值为正常   老袁的锅
			state = 0;
			RoleCityAgent cityAgent = role.getCity(cityId);
			RoleBuild build = cityAgent.searchBuildById(buildId);
			build.sendToClient(rms);
			return false;
		}
		EquipItem equipItem = equipLst.get(0);// 一次只能升级一个装备
		String keyId = equipItem.getKey();
		Equip equip = dataManager.serach(Equip.class, keyId);
		if (equip == null) {
			GameLog.error("cannot find this equip base information where keyId=" + keyId);
			return false;
		}
		if (upgradeSuccessRate(equip, equipItem.getUpgradeMaterialLists())) {
			// 升级成功
			EquipItem oldEquip = equipItem.clone();
			equipItem.setKey(equip.getUpgradeEquipID());
			List<String> afterEquipBuff = equipItem.getEquipBuffIdLists();
			List<String> equipBuffIdList = equipItem.randomBuffIdList(equip.getUpgradeEquipID());
			if (equipBuffIdList == null) {
				GameLog.error("add buffIdList fail");
				return false;
			}
			if (equipBuffIdList.size() <= afterEquipBuff.size()) { // 随机出buff的个数比原来少
				equipBuffIdList = afterEquipBuff;
			} else {
				for (int i = 0; i < afterEquipBuff.size(); i += 2) {
					String buffId = afterEquipBuff.get(i);
					if (StringUtils.isNull(buffId)) {
						continue;
					}
					if (!equipBuffIdList.contains(buffId)) {
						equipBuffIdList.set(i, buffId);
					}
				}
			}
			equipBuffIdList = equipItem.randomBuffValueList(equipBuffIdList, equip.getUpgradeEquipID()); // 更新buff的值
			state = 3;
			equipItem.setEquipBuffIdLists(equipBuffIdList);
			equipItem.setForgeAfterBuffIdLists(null);
			equipItem.setEquipState((byte) 0);
			agent.sendShowItemsToClient(rms,oldEquip, equipItem);
			agent.sendItemsToClient(rms, equipItem);
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_GET_EQUIP, equipItem.getKey(), 1);
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_EQUIP_LVLUP);
		} else {
			// 升级失败
			OtherItem otherItem = new OtherItem();
			String materialId = "";
			if (equipItem.getUpgradeMaterialLists() == null || equipItem.getUpgradeMaterialLists().size() < 1
					|| equipItem.getUpgradeMaterialLists().get(0).split("\\|").length < 1) {
				GameLog.error("get drawing is fail from getUpgradeMaterialLists");
				return false;
			}
			materialId = equipItem.getUpgradeMaterialLists().get(0).split("\\|")[0];
			state = 4;
			agent.addOther(materialId, 1);
			String event = "EquipUpgradeOver";
			String itemst =materialId;
			LogManager.itemOutputLog(role, 1, event,itemst);
			otherItem.init(uid, materialId, 1);
			equipItem.setEquipState((byte) 0);
			//agent.sendShowItemsToClient(rms, equipItem, otherItem);
			ItemCell temp = role.getBagAgent().getItemFromBag(materialId);
			agent.sendItemsToClient(rms, equipItem, temp);
		}
		equipItem.setUpgradeMaterialLists(null);
		RoleCityAgent cityAgent = role.getCity(cityId);
		RoleBuild build = cityAgent.searchBuildById(buildId);
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		state = 0;
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.REINFORCE);
		return true;
	}
	
	/**
	 * 装备升级加速
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @return
	 */
	public boolean EquipUpgradeSpeed(Role role, int cityId, long buildId){
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);
		TimerLast timer = build.getBuildTimer();
		if (timer == null) {
			return false;
		}
		int money = role.timeChgMoney(timer.getLast(), (byte) 0);
		if (money > role.getMoney()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, money);
			return false;
		}
		if (role.redRoleMoney(money) == false) {
			GameLog.error("momey is insufficient!");
			return false;
		}
		boolean suc = build.redBuildTimer(role, timer.getLast(),TimerLastType.TIME_UP_EQUIP);
		RespModuleSet rms = new RespModuleSet();
		build.sendToClient(rms);
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		EquipItem  equipItem = role.getBagAgent().getEquipById(srcEId);
		Equip equip = dataManager.serach(Equip.class, equipItem.getKey());
		try {
			NewLogManager.buildLog(role, "equip_upgrade_accelerate",equip.getId(),money);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return suc;
	}

	@Override
	public void tick(Role role,RoleBuild build,long now) {
	}

	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		srcEId = Long.parseLong(map.get("srcEId"));
		TimerLast timer = build.searchTimer(TimerLastType.TIME_UP_EQUIP);
		if (timer != null) {
			timer.registTimeOver(this);
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		map.put("srcEId", String.valueOf(srcEId));
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());
		params.put(state);// byte
		params.put(srcEId);// long
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	/**
	 * @param equip
	 * @param list
	 * @param 计算升级成功率
	 * @return
	 */
	public boolean upgradeSuccessRate(Equip equip, List<String> list) {
		// TODO 成功率计算公式待定
		double numerator = 0.0D;
		double denominator = 0.0D;
		for (int index = 0; index < list.size(); index ++) {
			String tempStr = list.get(index);
			if (tempStr == null) {
				continue;
			}
			String[] strings = tempStr.split("\\|");
			String materialId = strings[0];
			Item material = dataManager.serach(Item.class, materialId);
			if (material == null) {
				GameLog.error("read material base data is fail!");
				return false;
			}
			if (index == 0) {
				numerator += Math.pow(2.5, (material.getItemLevel()));
				continue;
			}
			numerator += Math.pow(1.8, material.getItemLevel());
		}
		numerator = Math.pow(numerator, 0.85);
		denominator = 10 * Math.pow(equip.getEquipQuality(), 1.5);
		if (numerator / denominator > 1) {
			return true;
		} else {
			double sucNum = numerator / denominator;
			int rand = MathUtils.random(1, 100);
			if (rand / 100.0 <= sucNum) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void finish() {
		Role role = world.getRole(uid);
		state = 2; // 完成后设置建筑的属性为2 即为升级完成
		RespModuleSet rms = new RespModuleSet();
		RoleCityAgent cityAgent = role.getCity(cityId);
		RoleBuild build = cityAgent.searchBuildById(buildId);
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role);
	}

	@Override
	public void setBuildParams(RoleBuild build) {
	}

}
