package com.joymeng.slg.domain.object.armyPoints;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.armyPoints.data.Soldierstt;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class ArmyPoints implements Instances {
	long uid;
	int cityId;
	String buildId;
	int techTreeId;
	int leavePoints = 0;
	Map<Integer, Integer> branchsMap = new HashMap<Integer, Integer>();// 分支信息
	Map<String, UnlockInfo> armysFreeMap = new HashMap<String, UnlockInfo>();// 兵种升级信息

	public ArmyPoints() {

	}

	public void init(long uid, int cityId, String buildId) {
		this.uid = uid;
		this.cityId = cityId;
		this.buildId = buildId;
	}

	public void init(long uid, int cityId, String buildId, int level) {
		this.uid = uid;
		this.cityId = cityId;
		this.buildId = buildId;
		techTreeId = changeBuildIdToTreeId(buildId);
		init(buildId, level);
	}

	public void init(String buildId, int level) {
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildId, level);
		if (buildLevel == null) {
			return;
		}
		final int id = techTreeId;
		if (id == 0) {
			return;
		}
		List<Soldierstt> soldierLst = dataManager.serachList(Soldierstt.class, new SearchFilter<Soldierstt>() {
			@Override
			public boolean filter(Soldierstt data) {
				if (data.getTechTreeID() == id) {
					return true;
				}
				return false;
			}
		});
		if (soldierLst == null) {
			return;
		}
		for (int i = 0 ; i < soldierLst.size() ; i++){
			Soldierstt stt = soldierLst.get(i);
			if (stt.getPrecedingTech().equals("ture") && checkLimitation(stt.getLimitation(), level)) {
				UnlockInfo uInfo = UnlockInfo.create(stt.getId(), stt.getTechTreeID(), stt.getBranchID());
				armysFreeMap.put(stt.getId(), uInfo);
			}
			if (branchsMap.get(stt.getBranchID()) == null) {
				branchsMap.put(stt.getBranchID(), 0);
			}
		}
	}

	public void addTechTreePoints(int level) {
		if (buildId == null) {
			return;
		}
		List<Soldierstt> soldierLst = dataManager.serachList(Soldierstt.class, new SearchFilter<Soldierstt>() {
			@Override
			public boolean filter(Soldierstt data) {
				if (data.getTechTreeID() == techTreeId && data.getPrecedingTech().equals("ture")) {
					return true;
				}
				return false;
			}
		});
		if (soldierLst == null) {
			return;
		}
		for (int i = 0 ; i < soldierLst.size() ; i++){
			Soldierstt stt = soldierLst.get(i);
			if (checkLimitation(stt.getLimitation(), level)) {
				UnlockInfo uInfo = UnlockInfo.create(stt.getId(), stt.getTechTreeID(), stt.getBranchID());
				armysFreeMap.put(stt.getId(), uInfo);
			}
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildId, level);
		if (buildLevel == null || buildLevel.getArmyID() == null) {
			return;
		}
		try {
			int points = Integer.parseInt(buildLevel.getArmyID());
			if (points > 0) {
				leavePoints += points;
			}
			Role role = world.getOnlineRole(uid);
			if (role != null) {
				RespModuleSet rms = new RespModuleSet();
				sendPointsToClient(rms);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		} catch (Exception e) {
			GameLog.error("addTechTreePoints error, buildId=" + buildId);
		}
	}

	public void sendPointsToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ARMY_POINTS;
			}
		};
		module.add(techTreeId);// int treeId
		module.add(leavePoints);// int 剩余技能点数
		module.add(branchsMap.size());// 分支数量
		for (Map.Entry<Integer, Integer> entry : branchsMap.entrySet()) {
			module.add(entry.getKey());// int branchId
			module.add(entry.getValue());// int 已加点数
		}
		module.add(armysFreeMap.size());// int
		for (UnlockInfo info : armysFreeMap.values()) {
			module.add(info.getArmyId());// String
			module.add(info.getLevel());// int
			// module.add(info.getState());//int 1-当前索引，0-其他
		}
		rms.addModule(module);
	}

	/**
	 * 士兵解锁
	 * 
	 * @param role
	 * @param armyId
	 * @param level
	 * @return
	 */
	public boolean armyLevelup(Role role, String armyId, int level) {
		Soldierstt stt = dataManager.serach(Soldierstt.class, armyId);
		if (stt == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_SKILL_ERROR);
			return false;
		}
		if (armysFreeMap.get(stt.getPrecedingTech()) == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_SKILL_NO_PRECEDING,
					stt.getPrecedingTech());
			return false;
		}
		int buildLevel = 0;
		List<RoleBuild> builds = role.getCity(cityId).searchBuildByBuildId(buildId);
		if (builds == null) {
			GameLog.error("searchBuildByBuildId is null cityId = " + cityId + "buildId = " + buildId);
			return false;
		}
		for (int i = 0 ; i < builds.size() ; i++){
			RoleBuild build = builds.get(i);
			buildLevel = build.getLevel();
		}
		if (!checkLimitation(stt.getLimitation(), buildLevel)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_SKILL_NO_PRECEDING, buildId);
			return false;
		}
		if (leavePoints < level) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_SKILL_NO_POINTS);
			return false;
		}
		UnlockInfo info = armysFreeMap.get(armyId);
		if (info != null && info.getLevel() + level > stt.getMaxPoints()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SKILL_LEVELUP_MAX);
			return false;
		}
		// 扣技能点,加小分支技能点
		if (branchsMap.get(stt.getBranchID()) == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ARMY_SKILL_ERROR);
			return false;
		}
		leavePoints -= level;
		int points = branchsMap.get(stt.getBranchID());
		branchsMap.put(stt.getBranchID(), points + level);
		// 兵种新增
		UnlockInfo armyUInfo = UnlockInfo.create(stt.getId(), stt.getTechTreeID(), stt.getBranchID());
		armysFreeMap.put(stt.getId(), armyUInfo);

		RespModuleSet rms = new RespModuleSet();
		sendPointsToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "study_arms",armyId);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_SOLD_UNLOK, armyId);
		return true;
	}

	public boolean resetArmysPoints(Role role, int type) {
		final String itemId = "userArmy_resetting";
		Shop item = dataManager.serach(Shop.class, new SearchFilter<Shop>() {
			@Override
			public boolean filter(Shop data) {
				return data.getItemid().equals(itemId);
			}
		});
		if (item == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, 1);
			return false;
		}
		RespModuleSet rms = new RespModuleSet();
		if (type == 0) {// 消耗道具
			RoleBagAgent bagAgent = role.getBagAgent();
			ItemCell itemcell = bagAgent.getItemFromBag(itemId);
			if (itemcell == null || itemcell.getNum() == 0) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH, itemId, 1);
				return false;
			}
			bagAgent.removeItems(itemId, 1);
			bagAgent.sendItemsToClient(rms, itemcell);
		} else {// 消耗金币
			if (role.getMoney() < item.getNormalPrice()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,
						item.getNormalPrice());
				return false;
			}
			role.redRoleMoney(item.getNormalPrice());
			LogManager.goldConsumeLog(role, item.getNormalPrice(), EventName.resetArmysPoints.getName());
			role.sendRoleToClient(rms);
		}
		//
		for (int points : branchsMap.values()) {
			leavePoints += points;
		}
		branchsMap.clear();
		armysFreeMap.clear();
		List<RoleBuild> builds = role.getCity(0).searchBuildByBuildId(buildId);
		for (int i = 0 ; i < builds.size() ; i++){
			RoleBuild build = builds.get(i);
			init(buildId, build.getLevel());
		}
		sendPointsToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "reset_arms_study_point",item.getNormalPrice());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public void deserialize(String str) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<Integer, Object> map = (Map<Integer, Object>) JSON.parse(str);
		techTreeId = (int) map.get("1");
		leavePoints = (int) map.get("2");
		Object obj = map.get("3");
		branchsMap = JSON.parseObject(obj.toString(), new TypeReference<Map<Integer, Integer>>() {
		});
		obj = map.get("4");
		armysFreeMap = JSON.parseObject(obj.toString(), new TypeReference<Map<String, UnlockInfo>>() {
		});
	}

	public String serialize() {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, techTreeId);
		map.put(2, leavePoints);
		map.put(3, JsonUtil.ObjectToJsonString(branchsMap));
		map.put(4, JsonUtil.ObjectToJsonString(armysFreeMap));
		String str = JsonUtil.ObjectToJsonString(map);
		return str;
	}

	public void deserializeEntiy(JoyBuffer out) {
		techTreeId = out.getInt();
		leavePoints = out.getInt();
		int bSize = out.getInt();
		for (int i = 0; i < bSize; i++) {
			int key = out.getInt();
			int value = out.getInt();
			branchsMap.put(key, value);
		}
		int aSize = out.getInt();
		for (int j = 0; j < aSize; j++) {
			String armyId = out.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			int level = out.getInt();
			int branchId = out.getInt();
			int techTreeId = out.getInt();
			UnlockInfo info = UnlockInfo.create(armyId, techTreeId, branchId);
			info.setLevel(level);
			armysFreeMap.put(armyId, info);
		}
	}

	public void serializeEntiy(JoyBuffer buffer) {
		buffer.putInt(techTreeId);
		buffer.putInt(leavePoints);
		buffer.putInt(branchsMap.size());
		for (Map.Entry<Integer, Integer> entry : branchsMap.entrySet()) {
			buffer.putInt(entry.getKey());
			buffer.putInt(entry.getValue());
		}
		buffer.putInt(armysFreeMap.size());
		for (UnlockInfo info : armysFreeMap.values()) {
			buffer.putPrefixedString(info.getArmyId(),JoyBuffer.STRING_TYPE_SHORT);
			buffer.putInt(info.getLevel());
			buffer.putInt(info.getBranchId());
			buffer.putInt(info.getTechTreeId());
		}
	}

	private boolean checkLimitation(List<String> limitations, int level) {
		boolean bSuc = false;
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			return false;
		}
		for (int i = 0 ; i < limitations.size() ; i++){
			String limition = limitations.get(i);
			bSuc = false;
			String[] lims = limition.split(":");
			int iparam = Integer.parseInt(lims[1]);
			if (level >= iparam) {
				bSuc = true;
			}
		}
		return bSuc;
	}

	private int changeBuildIdToTreeId(String buildId) {
		int techTreeId = 0;
		BuildName type = BuildName.search(buildId);
		if (type != null) {
			switch (type) {
			case SOLDIERS_CAMP:
				techTreeId = 1;
				break;
			case WAR_FACT:
				techTreeId = 2;
				break;
			case ARMORED_FACT:
				techTreeId = 3;
				break;
			case AIR_COM:
				techTreeId = 4;
				break;
			default:
				break;
			}
		} else {
			GameLog.error("cannot find build by buildId = " + buildId);
		}
		return techTreeId;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public int getTechTreeId() {
		return techTreeId;
	}

	public void setTechTreeId(int techTreeId) {
		this.techTreeId = techTreeId;
	}

	public int getLeavePoints() {
		return leavePoints;
	}

	public void setLeavePoints(int leavePoints) {
		this.leavePoints = leavePoints;
	}

	public Map<Integer, Integer> getBranchsMap() {
		return branchsMap;
	}

	public void setBranchsMap(Map<Integer, Integer> branchsMap) {
		this.branchsMap = branchsMap;
	}

	public Map<String, UnlockInfo> getArmysFreeMap() {
		return armysFreeMap;
	}

	public void setArmysFreeMap(Map<String, UnlockInfo> armysFreeMap) {
		this.armysFreeMap = armysFreeMap;
	}

	public int getArmyLvl(String soldId) {
		UnlockInfo info = armysFreeMap.get(soldId);
		if (info == null || info.getLevel() == 0) {
			return 0;
		}
		return info.getLevel();
	}

}
