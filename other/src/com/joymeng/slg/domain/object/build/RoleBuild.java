package com.joymeng.slg.domain.object.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.BuildOperation;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.armyPoints.ArmyPoints;
import com.joymeng.slg.domain.object.build.data.Basebuildingslot;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.data.RoleBuildState;
import com.joymeng.slg.domain.object.build.impl.BuildComponentArmyTrain;
import com.joymeng.slg.domain.object.build.impl.BuildComponentCure;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDeal;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.build.impl.BuildComponentElectrical;
import com.joymeng.slg.domain.object.build.impl.BuildComponentForging;
import com.joymeng.slg.domain.object.build.impl.BuildComponentGem;
import com.joymeng.slg.domain.object.build.impl.BuildComponentHelp;
import com.joymeng.slg.domain.object.build.impl.BuildComponentIntelligence;
import com.joymeng.slg.domain.object.build.impl.BuildComponentProduction;
import com.joymeng.slg.domain.object.build.impl.BuildComponentResearch;
import com.joymeng.slg.domain.object.build.impl.BuildComponentStorage;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWar;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.exp.NoBuildComponentError;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.MemberAssistance;

/**
 * 功能组件
 * 
 * @author tanyong
 *
 */
public class RoleBuild implements Instances {
	protected long id;// 数据库主键
	protected long uid;// 玩家编号
	protected String buildId;// 建筑固化key
	protected String name;// 名字
	protected byte level;// 等级
	protected String slotID;// 建筑槽key
	protected int cityId;// 城市编号
	protected byte state;// 建筑状态0-正常，1-升级中，2-升级免费中，6-医院/维修厂工作中,7-贸易中心工作中,100-已删除建筑
	protected List<BuildComponent> components = new ArrayList<BuildComponent>();// 功能组件
	protected List<TimerLast> timers = new ArrayList<TimerLast>();// 倒计时
	protected BuildLinkQueue linkQueue;
	protected ArmyPoints pointsAgent = new ArmyPoints();
	protected boolean hasArmyPoints = false;

	/**
	 * 创建建筑
	 * 
	 * @param uid
	 *            玩家编号
	 * @param cityId
	 *            城市编号
	 * @param initializeLevel
	 * @param building
	 * @param bbs
	 * @return
	 */
	public static RoleBuild create(long uid, int cityId, int initializeLevel, Building building, Basebuildingslot bbs) {
		RoleBuild build = new RoleBuild();
		build.uid = uid;
		build.cityId = cityId;
		build.level = (byte) initializeLevel;
		build.init(building, bbs);
		build.initPointsAgent();
		return build;
	}

	private void init(Building building, Basebuildingslot bbs) {
		buildId = building.getId();
		name = building.getBuildingName();
		slotID = bbs.getId();
		state = RoleBuildState.COND_NORMAL.getKey();
		List<String> lis = building.getBuildingComponent();
		if (lis != null) {
			for (int i = 0; i < lis.size(); i++) {
				String componentKey = lis.get(i);
				BuildComponent component = createComponent(componentKey);
				if (component != null) {
					components.add(component);
				}
			}
		}
	}
	
	/**
	 * 
	* @Title: initPowerRatio 
	* @Description: 初始化所有组件电力调控数据
	* 
	* @return void
	* @param agent
	* @param building
	 */
	public void initPowerRatio(RoleCityAgent agent) {
		if (components != null) {
			for (int i = 0; i < components.size(); i++) {
				BuildComponent component = components.get(i);
				if (component != null) {
					if(ElectricalAdjustAgent.isPowerType(component)){
						agent.geteAgent().addElectricalComponents(agent.geteAgent().createElectricalComponent(this.id, component.getBuildComponentType()));
					}
				}
			}
		}
	}
	

	private void initPointsAgent() {
		BuildName name = BuildName.search(buildId);
		if (name == null) {
			return;
		}
		switch (name) {
		case SOLDIERS_CAMP:
		case WAR_FACT:
		case ARMORED_FACT:
		case AIR_COM: {
			pointsAgent.init(uid, cityId, buildId, level);
			hasArmyPoints = true;
			break;
		}
		default:
			break;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getBuildId() {
		return buildId;
	}

	public void setBuildId(String buildId) {
		this.buildId = buildId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public String getSlotID() {
		return slotID;
	}

	public void setSlotID(String slotID) {
		this.slotID = slotID;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
		BuildComponentProduction com = this.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
		if (com != null) {
			com.setStopProduction(state);
		}
	}

	public BuildLinkQueue getLinkQueue() {
		return linkQueue;
	}

	public void setLinkQueue(BuildLinkQueue linkQueue) {
		this.linkQueue = linkQueue;
	}

	public int getTimerSize() {
		return timers.size();
	}

	public List<TimerLast> getTimers() {
		return timers;
	}

	public List<BuildComponent> getComponents() {
		return components;
	}

	public void setComponents(List<BuildComponent> components) {
		this.components = components;
	}

	public boolean isHasArmyPoints() {
		return hasArmyPoints;
	}

	public void setHasArmyPoints(boolean hasArmyPoints) {
		this.hasArmyPoints = hasArmyPoints;
	}

	public ArmyPoints getPointsAgent() {
		return pointsAgent;
	}

	public boolean checkTimerType(TimerLastType type) {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast time = timers.get(i);
			String key = time.getType().getKey();
			if (key.equals(type.getKey())) {
				return false;
			}
		}
		return true;
	}

	// 初始化所有组件
	public void initComponents() {
		for (int i = 0; i < components.size(); i++) {
			BuildComponent com = components.get(i);
			com.init(uid, cityId, id, buildId);
		}
	}

	public TimerLast getBuildTimer() {

		if (timers.size() > 1) { // 资源交易倒计时
			for (int i = 0; i < timers.size(); i++) {
				TimerLast time = timers.get(i);
				if (!time.getType().equals(TimerLastType.TIME_CITY_TRADE_CD)) {
					return time;
				}
			}
		}
		if (timers.size() > 0) {
			return timers.get(0);
		}
		return null;
	}

	/**
	 * 用金币秒升级时间和剩余5分钟免费升级
	 * 
	 * @param money
	 * @return
	 */
	public boolean secondKill(Role role, int money) {
		if (timers.size() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_OVER);
			return false;
		}
		TimerLast timer = getBuildTimer();
		if (timer == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_OVER);
			return false;
		}
		// 检查金币是否足够
		long remainTime = timer.getLast() - (TimeUtils.nowLong() / 1000 - timer.getStart());// 剩余时间
		long cost = 0;
		if (money == 0) {// 免费加速
			if (timer.getType().ordinal() != TimerLastType.TIME_CREATE.ordinal()
					&& timer.getType().ordinal() != TimerLastType.TIME_LEVEL_UP.ordinal()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TIMER_TYPE_NOT_RIGHT);
				return false;
			}
		} else {
			int costMoney = 0;
			if (timer.getType().ordinal() == TimerLastType.TIME_CREATE.ordinal()
					|| timer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal()) {
				costMoney = role.timeChgMoney(remainTime, (byte) 1);
			} else if (timer.getType().ordinal() == TimerLastType.TIME_PD_GEM.ordinal()) {
				BuildComponentGem buildCom = getComponent(BuildComponentType.BUILD_COMPONENT_GEM);
				costMoney = buildCom.tickUpdateExpirationTime(false);
				GameLog.info("[secondKill]uid=" + role.getJoy_id() + "|cost=" + costMoney);
			} else {
				costMoney = role.timeChgMoney(remainTime, (byte) 0);
			}
			if (!role.redRoleMoney(costMoney)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return false;
			}
			if (timer.getType().ordinal() == TimerLastType.TIME_PD_GEM.ordinal()) {
				BuildComponentGem buildCom = getComponent(BuildComponentType.BUILD_COMPONENT_GEM);
				buildCom.buyAccelerate();// 次数++
				GameLog.info("[secondKill]uid=" + role.getJoy_id() + "|buyAccelerate,cost="
						+ buildCom.tickUpdateExpirationTime(false));
			}
			cost = costMoney;
			RespModuleSet rms = new RespModuleSet();
			role.sendRoleToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			LogManager.goldConsumeLog(role, costMoney, EventName.secondKill.getName());
			// 任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD,
					timer.getType());
		}
		role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.ACCELERATE, remainTime);

		try {
			switch (timer.getType()) {
			case TIME_CREATE:
				NewLogManager.buildLog(role, "build_accelerate", buildId, cost);
				if (cost == 0) {
					NewLogManager.buildLog(role, "building_free_complete", buildId);
				}
				break;
			case TIME_LEVEL_UP:
				NewLogManager.buildLog(role, "upgrade_accelerate", buildId, cost);
				break;
			case TIME_TRAIN:
				NewLogManager.buildLog(role, "train_accelerate", buildId, cost);
				break;
			case TIME_RESEARCH:
				NewLogManager.buildLog(role, "study_accelerate", buildId, cost);
				break;
			case TIME_CURE:
				NewLogManager.buildLog(role, "cure_accelerate", buildId, cost);
				break;
			case TIME_PD_GEM:
				NewLogManager.buildLog(role, "mining_accelerate", cost);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return killTimer(role, timer);
	}

	private synchronized boolean killTimer(Role role, TimerLast timer) {
		try {
			timer.die();
		} catch (Exception e) {
			GameLog.error("build timer over error : " + timer.getType(), e);
		}
		linkQueue.removeQueue(id);
		timers.remove(timer);
		role.handleEvent(GameEvent.ROLE_BUILD_TIME_ROVER, this, timer);
		RespModuleSet rms = new RespModuleSet();
		if (timer.getType() == TimerLastType.TIME_PD_GEM) {
			BuildComponentGem buildCom = getComponent(BuildComponentType.BUILD_COMPONENT_GEM);
			buildCom.addLastItems();
		}
		// 建造或升级时下发电力和buildQueue更新,城市状态更新
		if (timer.getType() == TimerLastType.TIME_CREATE || timer.getType() == TimerLastType.TIME_LEVEL_UP
				|| timer.getType() == TimerLastType.TIME_REMOVE || timer.getType() == TimerLastType.TIME_CURE) {
			RoleCityAgent agent = role.getCity(cityId);
			agent.sendToClient(rms, false);
		}
		sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		GameLog.info("build run timer finish timer type=" + timer.getType().getKey());
		return true;
	}

	/**
	 * 清除交易CD
	 */
	public boolean clearTranCd(Role role) {
		BuildComponentDeal deal = getComponent(BuildComponentType.BUILD_COMPONENT_DEAL);
		if (deal == null) {
			return false;
		}
		TimerLast timer = getCDTime();
		if (timer == null) {
			return false;
		}
		int count = deal.getCount();
		// 检查金币是否足够
		int costMoney = 0;
		costMoney = deal.getMoney(count);
		if (!role.redRoleMoney(costMoney)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
			return false;
		}
		if (!killTimer(role, timer)) {
			return false;
		}
		deal.addCount();
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}
	
	public TimerLast getCDTime() {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast time = timers.get(i);
			if (time.getType().equals(TimerLastType.TIME_CITY_TRADE_CD)) {
				return time;
			}
		}
		return null;
	}
	
	
	public synchronized void runTimers(RoleCityAgent city,Role role,long now) {
		for (int i = 0; i < timers.size();) {
			TimerLast timer = timers.get(i);
			if (timer != null && timer.over(now)) {
				killTimer(role, timer);
				if (timer.getType() == TimerLastType.TIME_CITY_FIRE) {
					if (role.isOnline()) {
						RespModuleSet rms = new RespModuleSet();
						city.sendToClient(rms, false);
						MessageSendUtil.sendModule(rms, role);
					}
				}
			} else {
				// 最后5分钟免费加速提示
				long timeLeave = now / 1000 - timer.getStart();
				if (state != RoleBuildState.COND_UPGRADEFREE.getKey()
						&& timer.getLast() <= role.getFreeTime() + timeLeave) {
					if (timer.getType() == TimerLastType.TIME_CREATE
							|| timer.getType() == TimerLastType.TIME_LEVEL_UP) {
						state = RoleBuildState.COND_UPGRADEFREE.getKey();// 可以免费完成剩余时间
						if (role.isOnline()) {
							RespModuleSet rms = new RespModuleSet();
							sendToClient(rms);// 下发建筑
							MessageSendUtil.sendModule(rms, role.getUserInfo());
						}
					}
				}
				++i;
			}
		}
	}

	public void tick(RoleCityAgent city, Role role, long now) {
		runTimers(city, role, now);
		for (int i = 0; i < components.size(); i++) {
			BuildComponent component = components.get(i);
			component.tick(role, this, now);
		}
	}

	public void initComponentInfo(Role role, boolean isLoad) {
		for (int i = 0; i < components.size(); i++) {
			BuildComponent com = components.get(i);
			if (com.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_PRODUCTION && isLoad) {
				BuildComponentProduction pCom = (BuildComponentProduction) com;
				pCom.calcOffLineRes(role, this);
			}
			if (com.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_WALL && !isLoad) {
				BuildComponentWall pCom = (BuildComponentWall) com;
				pCom.initWallStatus(role, this);
				pCom.redDefence(30); // 熊大 让初始的是不满的
				pCom.setState((byte) 2);
			}
		}
	}

	public void serialize(JoyBuffer out) {
		out.putLong(id);
		out.putLong(uid);
		out.putPrefixedString(buildId, JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(name, JoyBuffer.STRING_TYPE_SHORT);
		out.put(level);
		out.putPrefixedString(slotID, JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(cityId);
		out.put(state);
		String str = JsonUtil.ObjectToJsonString(timers);
		out.putPrefixedString(str, JoyBuffer.STRING_TYPE_SHORT);
		str = saveComponents();
		out.putPrefixedString(str, JoyBuffer.STRING_TYPE_SHORT);
		if (hasArmyPoints) {
			out.putPrefixedString("ArmyPoints", JoyBuffer.STRING_TYPE_SHORT);
			pointsAgent.serializeEntiy(out);
		} else {
			out.putPrefixedString("null", JoyBuffer.STRING_TYPE_SHORT);
		}
	}

	public void deserialize(JoyBuffer buffer) {
		id = buffer.getLong();
		uid = buffer.getLong();
		buildId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		name = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		level = buffer.get();
		slotID = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		cityId = buffer.getInt();
		state = buffer.get();
		String str1 = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		timers = JsonUtil.JsonToObjectList(str1, TimerLast.class);
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			switch (timer.getType()) {
			case TIME_CREATE:
				if (timer.getStart() + timer.getLast() <= TimeUtils.nowLong() / 1000) {
					state = RoleBuildState.COND_NORMAL.getKey();
				}
				timer.registTimeOver(new RoleBuildCreateFinish(this));
				break;
			case TIME_LEVEL_UP:
				if (timer.getStart() + timer.getLast() <= TimeUtils.nowLong() / 1000) {
					state = RoleBuildState.COND_NORMAL.getKey();
				}
				timer.registTimeOver(new RoleBuildLevelUpFinish(this));
				break;
			case TIME_REMOVE:
				if (timer.getStart() + timer.getLast() <= TimeUtils.nowLong() / 1000) {
					state = RoleBuildState.COND_NORMAL.getKey();
				}
				timer.registTimeOver(new RoleBuildRemoveFinish(this));
				break;
			default:
				break;
			}
		}
		String str2 = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		loadComponents(str2);
		// 士兵解锁
		String flag = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		if (flag.equals("ArmyPoints")) {
			if (buffer.hasRemaining()) {
				pointsAgent.init(uid, cityId, buildId);
				pointsAgent.deserializeEntiy(buffer);
				hasArmyPoints = true;
			} else {
				initPointsAgent();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadComponents(String str) {
		if (StringUtils.isNull(str)) {
			return;
		}
		components.clear();
		try {
			Map<String, List<String>> map = JsonUtil.JsonToObjectMap_List(str, String.class, String.class);
			for (String key : map.keySet()) {
				Class<? extends BuildComponent> clazz = (Class<? extends BuildComponent>) Class.forName(key);
				List<String> lis = map.get(key);
				for (int i = 0; i < lis.size(); i++) {
					String bcs = lis.get(i);
					BuildComponent component = clazz.newInstance();
					component.init(uid, cityId, id, buildId);
					component.deserialize(bcs, this);
					components.add(component);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String saveComponents() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (int i = 0; i < components.size(); i++) {
			BuildComponent component = components.get(i);
			String key = component.getClass().getName();
			List<String> lis = map.get(key);
			if (lis == null) {
				lis = new ArrayList<String>();
				map.put(key, lis);
			}
			lis.add(component.serialize(this));
		}
		String str = JsonUtil.ObjectToJsonString(map);
		return str;
	}

	public void sendToClient() {
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			RespModuleSet rms = new RespModuleSet();
			sendToClient(rms);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	public void sendToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_BUILD;
			}
		};
		module.add(cityId);// In 城市编号
		module.add(id);// long 数据库唯一主键
		module.add(uid);// long 用户编号
		module.add(buildId);// string 建筑固化编号
		module.add(level);// byte 当前等级
		module.add(slotID);// string 固化数据BaseBuildingSlot的编号
		module.add(state);// byte 当前建筑状态
		BuildComponentProduction com = getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
		if (com != null) {
			TimerLast specialTimer = com.getSpecialTimer();
			if (specialTimer != null) {
				module.add(timers.size() + 1);// 当前建筑的倒计时列表
				for (int i = 0; i < timers.size(); i++) {
					TimerLast timer = timers.get(i);
					timer.sendToClient(module.getParams());
				}
				specialTimer.sendToClient(module.getParams());
			} else {
				// 下发倒计时
				module.add(timers.size());// 当前建筑的倒计时列表
				for (int i = 0; i < timers.size(); i++) {
					TimerLast timer = timers.get(i);
					timer.sendToClient(module.getParams());
				}
			}
		} else {
			// 下发倒计时
			module.add(timers.size());// 当前建筑的倒计时列表
			for (int i = 0; i < timers.size(); i++) {
				TimerLast timer = timers.get(i);
				timer.sendToClient(module.getParams());
			}
		}
		module.add(components.size());// 功能组件个数
		for (int i = 0; i < components.size(); i++) {
			BuildComponent component = components.get(i);
			component.sendToClient(module.getParams());// 各个功能模块的数据
		}
		rms.addModule(module);
		if (hasArmyPoints) {
			pointsAgent.sendPointsToClient(rms);
		}
	}

	public boolean isOnly() {
		Building building = dataManager.serach(Building.class, buildId);
		if (building == null) {
			GameLog.error("check build static data error where buildId=" + buildId);
			return true;
		}
		return building.getMaxBuildCount() == 1;
	}

	public boolean updateUnionHelperList(Role role, TimerLast timer, long time) {
		UnionBody union = unionManager.search(role.getUnionId());
		if (union == null) {
			GameLog.error("unionManager.search is fail!");
			return false;
		}
		List<MemberAssistance> assistances = union.getAssistances();
		for (int i = 0; i < assistances.size(); i++) {
			MemberAssistance assistance = assistances.get(i);
			if (assistance == null) {
				continue;
			}
			if (role.getId() == assistance.getUid() && timer.getStart() == assistance.getStartTime()
					&& id == assistance.getBuildId()) {
				assistance.setStartTime(timer.getStart() - time);
			}
		}
		return true;
	}

	public synchronized TimerLast addBuildTimer(long last, TimerLastType type) {
		TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, last, type);
		timers.add(timer);
		if (type == TimerLastType.TIME_CREATE || type == TimerLastType.TIME_LEVEL_UP
				|| type == TimerLastType.TIME_REMOVE) {
			linkQueue.addBuildQueue(id);
		} else if (type == TimerLastType.TIME_RESEARCH) {
			linkQueue.addResearchQueue(id);
		}
		return timer;
	}

	public synchronized TimerLast searchTimer(TimerLastType type) {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			if (timer.getType().equals(type)) {
				return timer;
			}
		}
		return null;
	}

	public synchronized void removeTimer(TimerLastType type) {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			if (timer.getType().equals(type)) {
				timers.remove(timer);
				break;
			}
		}
	}

	/**
	 * 同步治疗建筑的时间
	 * 
	 * @param role
	 */
	public void synCureBuildTimer(Role role) {
		RoleCityAgent agent = role.getCity(cityId);
		if (agent == null) {
			GameLog.error("getCity is error");
			return;
		}
		List<RoleBuild> buildList = agent.searchBuildByBuildId(buildId);
		TimerLast timer = null;
		for (int i = 0; i < timers.size(); i++) {
			if (timers.get(i) == null) {
				continue;
			}
			if (timers.get(i).getType() == TimerLastType.TIME_CURE) {
				timer = timers.get(i);
				break;
			}
		}
		if (timer == null) {
			GameLog.error("get cureBuild timer 0 is error , please tell houshanping!");
			return;
		}
		RespModuleSet rms = new RespModuleSet();
		for (int i = 0; i < buildList.size(); i++) {
			RoleBuild tempBuild = buildList.get(i);
			if (tempBuild != null && tempBuild.getState() != 1 && tempBuild.getId() != id) {
				if (tempBuild.getTimers().size() < 1) {
					GameLog.error("get cureBuild timer 1 is error, please tell houshanping!");
					continue;
				}
				for (int j = 0; j < tempBuild.getTimers().size(); j++) {
					TimerLast tLast = tempBuild.getTimers().get(j);
					if (tLast == null) {
						continue;
					}
					if (tLast.getType() == TimerLastType.TIME_CURE) {
						tLast.setStart(timer.getStart());
					}
				}
				tempBuild.sendToClient(rms);
			}
		}
		MessageSendUtil.sendModule(rms, role);
	}

	// 时间加速
	public synchronized boolean redBuildTimer(Role role, long time, TimerLastType type) {
		boolean bSuc = false;
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			if (type == null) {
				if (timer.getType() == TimerLastType.TIME_CREATE || timer.getType() == TimerLastType.TIME_TRAIN
						|| timer.getType() == TimerLastType.TIME_LEVEL_UP
						|| timer.getType() == TimerLastType.TIME_REMOVE
						|| timer.getType() == TimerLastType.TIME_RESEARCH || timer.getType() == TimerLastType.TIME_CURE
						|| timer.getType() == TimerLastType.TIME_UP_EQUIP) {
					long lastTime = timer.getLast() - time;
					if (lastTime > 0) {
						if (role.getUnionId() != 0) {
							if (updateUnionHelperList(role, timer, time)) {
								timer.setStart(timer.getStart() - time);
							}
						} else {
							timer.setStart(timer.getStart() - time);
						}
					} else {
						timer.setLast(0);
					}
					type = timer.getType();
					bSuc = true;
				}
			} else if (timer.getType() == type) {
				long lastTime = timer.getLast() - time;
				if (lastTime > 0) {
					if (role.getUnionId() != 0) {
						if (updateUnionHelperList(role, timer, time)) {
							timer.setStart(timer.getStart() - time);
						}
					} else {
						timer.setStart(timer.getStart() - time);
					}
				} else {
					timer.setLast(0);
				}
				bSuc = true;
			}
		}
		if (bSuc) {
			// 任务事件
			if (role != null) {
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD, type);
				role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.ACCELERATE, time);
			}
			// 同步治疗建筑的时间
			if (type == TimerLastType.TIME_CURE) {
				synCureBuildTimer(role);
			}
		}
		return bSuc;
	}

	/**
	 * 加速或取消定时操作
	 * 
	 * @param time
	 *            改变的时间
	 * @param type
	 * @param bCancel
	 * @return
	 */
	public synchronized boolean modifyTimer(long time, TimerLastType type, boolean bCancel) {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			if (timer.getType() == type) {
				if (bCancel) {
					timers.remove(timer);
					linkQueue.removeQueue(id);
				} else {
					long lastTime = timer.getStart() + timer.getLast() - TimeUtils.nowLong() - time;
					timer.setLast(lastTime > 0 ? lastTime : 0);
				}
				return true;
			}
		}
		return false;
	}

	public static Buildinglevel getBuildinglevelByCondition(final String buildKey, int level) {
		if (level < 0 || level > 30) {
			return null;
		}
		Building building = dataManager.serach(Building.class, buildKey);
		List<String> Buildinglevels = building.getLevelDataList();
		final String BuildinglevelId = Buildinglevels.get(level - 1);
		Buildinglevel buildLevel = dataManager.serach(Buildinglevel.class, new SearchFilter<Buildinglevel>() {
			@Override
			public boolean filter(Buildinglevel data) {
				if (data.getBuildingID().equals(buildKey) && data.getId().equals(BuildinglevelId)) {
					return true;
				}
				return false;
			}
		});
		if (buildLevel == null) {
			GameLog.error("Can't find Buildinglevel when buildId = " + buildKey + " and id " + BuildinglevelId);
		}
		return buildLevel;
	}

	public Buildinglevel getBuildingLevel() {
		return getBuildingLevel(level);
	}

	public Buildinglevel getBuildingLevel(int level) {
		return getBuildinglevelByCondition(buildId, level);
	}

	/**
	 * 建筑完成可能要重新计算战斗力还有一些其他操作
	 */
	public void createFinish(boolean bSend) {
		Role role = world.getObject(Role.class, uid);
		state = RoleBuildState.COND_NORMAL.getKey();
		LogManager.buildLog(role, slotID, buildId, level, BuildOperation.createFinish.getKey());
		for (int i = 0; i < components.size(); i++) {
			BuildComponent bCom = components.get(i);
			bCom.setBuildParams(this);
			if (bCom.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_PRODUCTION) {
				BuildComponentProduction com = (BuildComponentProduction) bCom;
				com.initProductEffect(role);
			}
		}
		// 同步建造完成的医院状态
		if (buildId.equals(BuildName.HOSPITAL.getKey()) || buildId.equals(BuildName.REPAIRER.getKey())) {
			syncHospitalOrRepairer(role);
		}
		// 城墙初始化设置
		// if(buildId.equals(BuildName.FENCE.getKey())){
		// MapCity mc = mapWorld.searchMapCity(uid, cityId);
		// if(mc != null){
		// mc.getWall().updateWall(this);
		// }
		// }
		// 建筑建造完成事件添加
		if (role != null) {
			// Task Event
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_BUILD, cityId,
					buildId, level);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UPGRADE_BUILD, buildId, (int) level);
		}
		// 下发这个建筑
		if (bSend) {
			sendToClient();
		}
		if (hasArmyPoints) {
			pointsAgent.addTechTreePoints(level);
		}
		// 重置该建筑的联盟被帮助的次数为0
		role.clearBuildHelpers(0, id);
	}

	public ResourceTypeConst getBuildResType() {
		ResourceTypeConst type = null;
		BuildName name = BuildName.search(buildId);
		if (name != null) {
			switch (name) {
			case FOOD_FACT:
				type = ResourceTypeConst.RESOURCE_TYPE_FOOD;
				break;
			case SMELTER:
				type = ResourceTypeConst.RESOURCE_TYPE_METAL;
				break;
			case REFINERY:
				type = ResourceTypeConst.RESOURCE_TYPE_OIL;
				break;
			case TITANIUM_PLANT:
				type = ResourceTypeConst.RESOURCE_TYPE_ALLOY;
				break;
			default:
				break;
			}
		} else {
			GameLog.error("cannot find build by buildId = " + buildId);
		}
		return type;
	}

	/**
	 * 建筑升级完成后的操作和数据下发
	 */
	public void leveupFinish(boolean bSend) {
		level += 1;
		state = RoleBuildState.COND_NORMAL.getKey();
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			GameLog.error("leveupFinish  getRole is fail ");
			return;
		}
		LogManager.buildLog(role, slotID, buildId, level, BuildOperation.upLevelFinish.getKey());
		// 同步建造完成的医院维修厂的状态
		if (buildId.equals(BuildName.HOSPITAL.getKey()) || buildId.equals(BuildName.REPAIRER.getKey())) {
			syncHospitalOrRepairer(role);
			sendToClient();
		}
		for (int i = 0; i < components.size(); i++) {
			BuildComponent bCom = components.get(i);
			bCom.setBuildParams(this);
		}
		if (role != null) {
			// 建筑升级完成事件添加
			// Task Event
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_BUILD, cityId,
					buildId, level);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UPGRADE_BUILD, buildId, (int) level);

			if (buildId.equals(BuildName.CITY_CENTER.getKey())) {
				role.handleEvent(GameEvent.CENTER_LEVE_UP, level);
				role.handleEvent(GameEvent.RANK_ROLECITYLEVEL_CHANGE, new TaskEventDelay());
			}
			if (buildId.equals(BuildName.RADAR.getKey())) {// 下发视野变化
				role.sendViews(new RespModuleSet(), true);
			}
		}
		// 下发这个建筑
		if (bSend) {
			sendToClient();
		}
		if (hasArmyPoints) {
			pointsAgent.addTechTreePoints(level);
		}
		// 重置该建筑的联盟被帮助的列表
		role.clearBuildHelpers(0, id);
	}

	public void tradeCDFinish() {
		for (int i = 0; i < timers.size(); i++) {
			TimerLast time = timers.get(i);
			if (time.getType().equals(TimerLastType.TIME_LEVEL_UP)) {
				state = RoleBuildState.COND_UPGRADE.getKey();
				return;
			}
		}	
		state = RoleBuildState.COND_NORMAL.getKey();
	}

	/**
	 * 同步医院或者维修站的状态的具体的操作
	 */
	private void syncHospitalOrRepairer(Role role) {
		RoleCityAgent agent = role.getCity(cityId);
		List<RoleBuild> builds = agent.searchBuildByBuildId(buildId);
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.state == RoleBuildState.COND_WORKING.getKey()) {
				state = build.state;
				timers.add(build.getBuildTimer());
				break;
			}
		}
	}

	/**
	 * 建筑移除计时器结束后
	 */
	public void removeFinish() {
		state = RoleBuildState.COND_DELETED.getKey();// 删除flag
		Role role = world.getObject(Role.class, uid);
		LogManager.buildLog(role, slotID, buildId, level, BuildOperation.removeFinish.getKey());
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_BUILD, cityId, buildId,
				(byte) 0);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UPGRADE_BUILD, buildId, (int) level);
	}

	/**
	 * 根据componentKey创建建筑功能组件
	 * 
	 * @param componentKey
	 */
	public BuildComponent createComponent(String componentKey) {
		BuildComponent component = null;
		BuildComponentType type = BuildComponentType.search(componentKey);
		if (type == BuildComponentType.BUILD_COMPONENT_ARMYTRAIN) {
			component = new BuildComponentArmyTrain();
		} else if (type == BuildComponentType.BUILD_COMPONENT_PRODUCTION) {
			component = new BuildComponentProduction();
		} else if (type == BuildComponentType.BUILD_COMPONENT_ELECTRICAL) {
			component = new BuildComponentElectrical();
		} else if (type == BuildComponentType.BUILD_COMPONENT_RESEARCH) {
			component = new BuildComponentResearch();
		} else if (type == BuildComponentType.BUILD_COMPONENT_FORGING) {
			component = new BuildComponentForging();
		} else if (type == BuildComponentType.BUILD_COMPONENT_HELP) {
			component = new BuildComponentHelp();
		} else if (type == BuildComponentType.BUILD_COMPONENT_INTELLIGENCE) {
			component = new BuildComponentIntelligence();
		} else if (type == BuildComponentType.BUILD_COMPONENT_DEFENSE) {
			component = new BuildComponentDefense();
		} else if (type == BuildComponentType.BUILD_COMPONENT_CURE) {
			component = new BuildComponentCure();
		} else if (type == BuildComponentType.BUILD_COMPONENT_STORAGE) {
			component = new BuildComponentStorage();
		} else if (type == BuildComponentType.BUILD_COMPONENT_DEAL) {
			component = new BuildComponentDeal();
			System.out.println("交易组件！！！！！！！");
		} else if (type == BuildComponentType.BUILD_COMPONENT_WALL) {
			component = new BuildComponentWall();
		} else if (type == BuildComponentType.BUILD_COMPONENT_WAR) {
			component = new BuildComponentWar();
		} else if (type == BuildComponentType.BUILD_COMPONENT_GEM) {
			component = new BuildComponentGem();
		} else {
			NoBuildComponentError error = new NoBuildComponentError(componentKey);
			GameLog.error(error.getMessage(), error);
		}
		return component;
	}

	/**
	 * 获取组件类型
	 */
	@SuppressWarnings("unchecked")
	public <T extends BuildComponent> T getComponent(BuildComponentType type) {
		for (int i = 0; i < components.size(); i++) {
			BuildComponent bCom = components.get(i);
			if (bCom.getBuildComponentType() == type) {
				return (T) bCom;
			}
		}
		return null;
	}
	
	

	/**
	 * 返回建筑中文说明
	 * 
	 * @return
	 */
	public static String getStatebyte(byte key) {
		String state = null;
		switch (key) {
		case 0:
			state = "正常状态";
			break;
		case 1:
			state = "升级状态";
			break;
		case 2:
			state = "免费状态";
			break;
		case 5:
			state = "拆除状态";
			break;
		case 6:
			state = "医院/维修厂工作中";
			break;
		case 100:
			state = "已删除建筑";
			break;
		default:
			state = "未知状态";
			break;
		}
		return state;
	}

	/**
	 * 建筑消耗电力
	 * 
	 * @return
	 */
	public int getCostPower(RoleCityAgent agent) {
		boolean isBuilding = false; // 是否处于建造状态
		for (int i = 0; i < timers.size(); i++) {
			TimerLast time = timers.get(i);
			if (time.getType().equals(TimerLastType.TIME_CREATE)) {
				isBuilding = true;
				break;
			}
		}
		Buildinglevel buildLevel = null;
		if (isBuilding) {
			buildLevel = getBuildingLevel(level);
		} else {
			boolean isLevelUpIng = state == 1 || state == 2; // 升级和免费升级状态
			buildLevel = getBuildingLevel(isLevelUpIng ? level + 1 : level);
		}
		if (buildLevel != null) {// 电力条件比例
			float value = 0;
			for (BuildComponent com : components) {
				if(com.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ELECTRICAL)
					continue;
				value += buildLevel.getPower() * agent.geteAgent().searchPoweRatio(this.id, com.getBuildComponentType().getKey());
			}
			return (int) value;
		}
		return 0;
	}
	
	public float getCostPowerRatio() {
		float powerRatio = 0;
		RoleCityAgent agent = null;
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			agent = role.getCity(cityId);
		}
		for (BuildComponent com : components) {
			if(com.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ELECTRICAL)
				continue;
			if(agent != null)
				powerRatio +=  agent.geteAgent().searchPoweRatio(this.id, com.getBuildComponentType().getKey());
			else
				powerRatio +=  Const.DEFAULT_CONSUMPTION;
		}
		return powerRatio;
	}
	
	public int getCostPowerModify(RoleCityAgent agent,String key,float powerRatio) {
		boolean isBuilding = false; // 是否处于建造状态
		for (int i = 0; i < timers.size(); i++) {
			TimerLast time = timers.get(i);
			if (time.getType().equals(TimerLastType.TIME_CREATE)) {
				isBuilding = true;
				break;
			}
		}
		Buildinglevel buildLevel = null;
		if (isBuilding) {
			buildLevel = getBuildingLevel(level);
		} else {
			boolean isLevelUpIng = state == 1 || state == 2; // 升级和免费升级状态
			buildLevel = getBuildingLevel(isLevelUpIng ? level + 1 : level);
		}
		if (buildLevel != null) {// 电力条件比例
			float value = 0;
			for (BuildComponent com : components) {
				if(com.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ELECTRICAL)
					continue;
				if(key.equals(com.getBuildComponentType().getKey())){
					value += buildLevel.getPower() * powerRatio;
					continue;
				}
				value += buildLevel.getPower() * agent.geteAgent().searchPoweRatio(this.id, com.getBuildComponentType().getKey());
			}
			return (int) value;
		}
		return 0;
	}
	
	/**
	 * 建筑产生电力
	 * 
	 * @return
	 */
	public int getProductePower() {
		boolean flag = false;
		for (int i = 0; i < components.size(); i++) {
			BuildComponent component = components.get(i);
			if (component.getBuildComponentType() == BuildComponentType.BUILD_COMPONENT_ELECTRICAL) {
				flag = true;
				break;
			}
		}
		if (flag) {
			Buildinglevel buildLevel = getBuildingLevel();
			if (buildLevel != null) {
				List<String> params = buildLevel.getParamList();
				if (params.size() == 0) {
					return 0;
				}
				int power = Integer.parseInt(params.get(0));
				return power;
			}
		}
		return 0;
	}
}
