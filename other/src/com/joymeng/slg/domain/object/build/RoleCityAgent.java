package com.joymeng.slg.domain.object.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.event.impl.EffectEvent;
import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.build.data.Basebuildingslot;
import com.joymeng.slg.domain.object.build.data.Baseland;
import com.joymeng.slg.domain.object.build.data.Building;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.data.Cityinitialize;
import com.joymeng.slg.domain.object.build.data.RoleBuildState;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDeal;
import com.joymeng.slg.domain.object.build.impl.BuildComponentProduction;
import com.joymeng.slg.domain.object.build.impl.BuildComponentStorage;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.build.queue.TimeQueue;
import com.joymeng.slg.domain.object.effect.BuffObject;
import com.joymeng.slg.domain.object.effect.BuffTypeConst;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleArmyAttr;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.technology.RoleTechAgent;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.MemberAssistance;
import com.joymeng.slg.union.impl.UnionHelper;
import com.joymeng.slg.world.GameConfig;

/**
 * 玩家城市
 * 
 * @author tanyong
 * 
 */
public class RoleCityAgent implements DaoData, Instances, BuildLinkQueue {
	public static final String CITY_CENTER_KEY = "CityCenter";
	public static final float RESOURCE_PROTECT_LIMIT = 0.3f;// 资源保护上限
	int id;// 城市编号
	long uid;// 玩家编号
	int position;// 城市在大地图位置信息
	long fortVision;// 城池视野
	Map<ResourceTypeConst, Long> resources = new HashMap<ResourceTypeConst, Long>();// 城市资源
	List<RoleBuild> builds = new CopyOnWriteArrayList<RoleBuild>();// 玩家城市中所有的建筑
	List<TimeQueue> buildQueue = new ArrayList<TimeQueue>();// 可以使用的建造队列
	List<TimeQueue> researchQueue = new ArrayList<TimeQueue>();// 可以使用的研究队列
	RoleArmyAgent armyAgent = new RoleArmyAgent();// 部队管理
	RoleTechAgent techAgent = new RoleTechAgent();// 城池科技
	ElectricalAdjustAgent eAgent  = new ElectricalAdjustAgent();
	byte state;
	long resSyncTime;// 士兵消耗资源更新时间
	long lessFood; // 士兵消耗不足时亏欠的粮食
	RoleCityAttr cityAttr = new RoleCityAttr();
	List<String> landIds = new ArrayList<>(); // 已解锁的块
	long maxBuildKeyId = 1000;//
	boolean savIng = false;
	Map<Long, List<UnionHelper>> unionHelpers = new HashMap<Long, List<UnionHelper>>();// 建筑请求联盟帮助次数的记录
																						// <建筑ID,已被帮助的用户uid列表>

	public RoleCityAgent(int id) {
		this.id = id;
		initLandIds();
	}

	public RoleCityAgent() {

	}

	public long getUid() {
		return uid;
	}

	public List<RoleBuild> getBuilds() {
		List<RoleBuild> rbs = new ArrayList<RoleBuild>();
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			rbs.add(build);
		}
		return rbs;
	}

	public RoleBuild getBuild() {
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			BuildComponentDeal deal = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEAL);
			if (deal != null) {
				return build;
			}
		}
		return null;
	}
	
	public List<TimeQueue> getBuildQueue() {
		return buildQueue;
	}

	public void setBuildQueue(List<TimeQueue> buildQueue) {
		this.buildQueue = buildQueue;
	}

	public void setBuilds(List<RoleBuild> builds) {
		this.builds = builds;
	}

	public void setTechAgent(RoleTechAgent techAgent) {
		this.techAgent = techAgent;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Map<ResourceTypeConst, Long> getResources() {
		return resources;
	}

	public void setResources(Map<ResourceTypeConst, Long> resources) {
		this.resources = resources;
	}

	public RoleArmyAgent getArmyAgent() {
		return armyAgent;
	}

	public void setArmyAgent(RoleArmyAgent armyAgent) {
		this.armyAgent = armyAgent;
	}

	public long getResSyncTime() {
		return resSyncTime;
	}

	public List<String> getLandIds() {
		return landIds;
	}

	public Map<Long, List<UnionHelper>> getUnionHelpers() {
		return unionHelpers;
	}

	public void setUnionHelpers(Map<Long, List<UnionHelper>> unionHelpers) {
		this.unionHelpers = unionHelpers;
	}

	public void setLandIds(List<String> landIds) {
		this.landIds = landIds;
	}

	public RoleCityAttr getCityAttr() {
		return cityAttr;
	}

	public void setCityAttr(RoleCityAttr cityAttr) {
		this.cityAttr = cityAttr;
	}
	
	

	public ElectricalAdjustAgent geteAgent() {
		return eAgent;
	}

	public void seteAgent(ElectricalAdjustAgent eAgent) {
		this.eAgent = eAgent;
	}

	/**
	 * 
	 * @Title: getCityBuffs
	 * @Description: 得到所有的citybuffer
	 * 
	 * @return Map<String,Map<String,BuffObject>>
	 * @return
	 */
	public Map<String, Map<String, BuffObject>> getCityBuffs() {
		Map<String, Map<String, BuffObject>> cityBuffs = new HashMap<String, Map<String, BuffObject>>();// 建筑ID,<buffId,buff效果>
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			GameLog.error("[getCityBuffs] uid=" + uid + ",role is null");
		} else {
			TargetType[] targetTypes = new TargetType[] { 
					BuffTypeConst.TargetType.G_B_IMP_RT, //增加资源掠夺保护比例上限
					BuffTypeConst.TargetType.T_B_ADD_HC, //医院伤兵数量上限提升
					BuffTypeConst.TargetType.T_B_ADD_RC,//增加维修厂的受损机械容量
					BuffTypeConst.TargetType.T_B_ADD_SPL, //单次训练的士兵数量增加
					BuffTypeConst.TargetType.T_B_ADD_FHP,//城防值增加
					BuffTypeConst.TargetType.T_B_ADD_FS, //城防空间增加
					BuffTypeConst.TargetType.T_B_ADD_PP,//发电厂的电力产量提升
					BuffTypeConst.TargetType.G_B_ADD_SDN, //发电厂的电力产量提升
					BuffTypeConst.TargetType.T_B_ADD_WL,////增加战争大厅的部队队伍上限
					BuffTypeConst.TargetType.T_B_RED_HT, 	//减少医院的伤兵的治疗时间
					BuffTypeConst.TargetType.T_B_RED_RT,//治疗伤兵的资源降低, 维修机械的资源降低
					BuffTypeConst.TargetType.T_A_RED_RRHR, //治疗伤兵的资源降低, 维修机械的资源降低
					BuffTypeConst.TargetType.T_A_RED_DR, //提升伤兵率
					BuffTypeConst.TargetType.T_A_RED_SPT, //需要兵种
					BuffTypeConst.TargetType.G_C_IMP_RS, //提升研究速度
					BuffTypeConst.TargetType.T_A_IMP_SW, // 部队负重提升
					BuffTypeConst.TargetType.T_A_IMP_SD, // 兵种防御提升
					BuffTypeConst.TargetType.T_A_IMP_SA, // 兵种攻击提升
					BuffTypeConst.TargetType.T_A_IMP_DMG, // 提升部队的伤害
					BuffTypeConst.TargetType.T_A_IMP_AHP, // 部队生命值提升
					BuffTypeConst.TargetType.T_A_IMP_ICR, // 部队的暴击值
					BuffTypeConst.TargetType.T_A_IMP_IAR, // 部队的命中值
					BuffTypeConst.TargetType.T_A_IMP_IER, // 部队的闪避值
					BuffTypeConst.TargetType.C_A_RED_BDMG, // 降低部队承受的伤害
					BuffTypeConst.TargetType.C_A_RED_MB, // 降低部队机动力
					BuffTypeConst.TargetType.C_A_RED_ATK, // 降低部队的火力
					BuffTypeConst.TargetType.C_A_RED_DEF, // 降低部队的防御力
					BuffTypeConst.TargetType.C_A_RED_HP, // 降低部队的生命值
					BuffTypeConst.TargetType.C_A_RED_CRT, // 降低部队的暴击值
					BuffTypeConst.TargetType.C_A_RED_ATR, // 降低部队的命中值
					BuffTypeConst.TargetType.C_A_RED_EDR, // 降低部队的闪避值
					BuffTypeConst.TargetType.C_A_RED_DMG, // 降低部队的伤害
					BuffTypeConst.TargetType.C_A_RED_BDMG_ALL, // 降低部队承受的伤害
					BuffTypeConst.TargetType.C_RED_ALL_DG,// 降低自己的所有部队攻击力
			};
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(targetTypes);
			for (Effect ef : list) {
				EffectEvent.effectInRoleCity(role, cityBuffs, ef);
			}
		}
		GameLog.info("[getCityBuffs] uid=" + uid + ",cityBuffs=" + cityBuffs.size());
		return cityBuffs;
	}

	/**
	 * 
	 * @Title: addBuffInMap
	 * @Description: 添加城市buff效果
	 * 
	 * @return void
	 * @param add
	 * @param cityBuffs
	 */
	public void addBuffInMap(BuffObject add, Map<String, Map<String, BuffObject>> cityBuffs) {
		Map<String, BuffObject> buildBuff = cityBuffs.get(add.getBuildId());
		if (buildBuff == null) {
			buildBuff = new HashMap<String, BuffObject>();
		}
		BuffObject buff = buildBuff.get(add.getKey());
		if (buff == null) {
			buildBuff.put(add.getKey(), add);
			cityBuffs.put(add.getBuildId(), buildBuff);
		} else {
			buff.updateValues(add.getValueType(), add.getValue(), add.getRate());
		}
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			return;
		}
	}

	// public void addBuffInMap(String buildId, String key, byte type, int
	// value, float rate) {
	// Map<String, BuffObject> buildBuff = cityBuffs.get(buildId);
	// if (buildBuff == null) {
	// buildBuff = new HashMap<String, BuffObject>();
	// }
	// BuffObject buff = buildBuff.get(key);
	// if (buff == null) {
	// buff = new BuffObject(buildId, key, type, value, rate);
	// buildBuff.put(key, buff);
	// cityBuffs.put(buildId, buildBuff);
	// } else {
	// buff.updateValues(false, type, value, rate);
	// }
	// Role role = world.getOnlineRole(this.uid);
	// if (role == null) {
	// return;
	// }
	// RespModuleSet rms = new RespModuleSet();
	// this.sendCityBuffToClient(rms);
	// MessageSendUtil.sendModule(rms, role.getUserInfo());
	// }

	// public void removeBuffInMap(String buildId, String key, byte type, int
	// value, float rate){
	// Map<String, BuffObject> buildBuff = cityBuffs.get(buildId);
	// if(buildBuff == null){
	// return;
	// }
	// BuffObject buff = buildBuff.get(key);
	// if(buff == null){
	// return;
	// }
	// buff.updateValues(true, type, value, rate);
	//
	// Role role = world.getOnlineRole(this.uid);
	// if(role == null){
	// return;
	// }
	// RespModuleSet rms = new RespModuleSet();
	// this.sendCityBuffToClient(rms);
	// MessageSendUtil.sendModule(rms, role.getUserInfo());
	// }

	public void buffToClient() {
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			return;
		}
		RespModuleSet rms = new RespModuleSet();
		this.sendCityBuffToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}

	public int getLandsByCondition() {
		int num = 0;
		for (String landId : landIds) {
			Baseland data = dataManager.serach(Baseland.class, landId);
			if (data != null) {
				if (data.getUnlockCondition() > 1) {
					num++;
				}
			}
		}
		return num;
	}

	/**
	 * 获取已被使用的槽List
	 */

	public List<String> getUsedSlots() {
		List<RoleBuild> builds = getBuilds();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild rb = builds.get(i);
			list.add(rb.getSlotID());
		}
		return list;
	}

	/**
	 * 获取已经解锁的地块
	 */

	public List<String> getUnlockSlots() {
		List<String> list = new ArrayList<String>();
		for (String str : landIds) {
			Baseland land = dataManager.serach(Baseland.class, str);
			List<String> containSlots = land.getContainSlots();
			list.addAll(containSlots);
		}
		return list;
	}

	/**
	 * 获取未被使用但是已经解锁的槽List
	 */
	public List<String> getUnusedSlots() {
		List<String> list = new ArrayList<String>();
		List<String> used = getUsedSlots();
		List<String> unlock = getUnlockSlots();
		for (String str : unlock) {
			if (!used.contains(str)) {
				list.add(str);
			}
		}
		return list;
	}

	/**
	 * 根据建筑槽Id获取建筑
	 */
	public RoleBuild getBuildBySlot(String slotID) {
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild rbuild = builds.get(i);
			if (rbuild.getSlotID().equals(slotID)) {
				return rbuild;
			}
		}
		return null;
	}

	/**
	 * 根据训练的兵种获取建筑
	 */
	public RoleBuild getBuildByArmyType(String ArmyId) {
		Army army = dataManager.serach(Army.class, ArmyId);
		byte armyType = army.getArmyType();
		String buildId = "";
		switch (armyType) {
		case 1:
			buildId = "SoldiersCamp";
			break;
		case 2:
			buildId = "WarFact";
			break;
		case 3:
			buildId = "TankFact";
			break;
		case 4:
			buildId = "AirCom";
			break;
		default:
			break;
		}
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild rbuild = builds.get(i);
			if (rbuild.getBuildId().equals(buildId)) {
				return rbuild;
			}
		}
		return null;
	}
	
	/**
	 * 获取建筑的最大等级
	 */

	public int getBuildMaxLevel(RoleBuild roleBuild) {
		Building bd = dataManager.serach(Building.class, roleBuild.getBuildId());
		return bd.getMaxLevel();
	}

	/**
	 * 获取建筑的建筑个数
	 * 
	 * @param buildName
	 *            建筑名
	 */
	public int getBuildCount(String buildName) {
		int count = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild rbuild = builds.get(i);
			if (rbuild.getBuildId().equals(buildName)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 获取建筑最多建筑个数
	 * 
	 * @param buildName
	 *            建筑名
	 */
	public int getBuildMaxCount(String buildName) {
		int maxCount = 0;
		Building bd = dataManager.serach(Building.class, buildName);
		maxCount = bd.getMaxBuildCount();
		return maxCount;
	}

	/**
	 * 加资源
	 * 
	 * @param type
	 * @param value
	 */
	public void addResource(ResourceTypeConst type, long value, Role role) {
		if (resources.containsKey(type)) {
			long pre = resources.get(type).longValue();
			long now = pre + value >= Long.MAX_VALUE ? Long.MAX_VALUE : pre + value;
			resources.put(type, now);
		}
		// 降低部队攻击力的buff
		if (type == ResourceTypeConst.RESOURCE_TYPE_FOOD) {
			if (resources.get(type) >= lessFood) {
				resources.put(type, resources.get(type) - lessFood);
				lessFood = 0;
				// 移除 Debuff
				role.getEffectAgent().removeTechBuff(role, buffName);
				RespModuleSet rms = new RespModuleSet();
				AbstractClientModule module = new AbstractClientModule() {
					@Override
					public short getModuleType() {
						return NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD;
					}
				};
				module.add(2);// 1表示不正常,2表示恢复正常
				rms.addModule(module);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
				resSyncTime = TimeUtils.nowLong();
			} else {
				lessFood = lessFood - resources.get(type);
				resources.put(type, 0L);
			}
		}
	}

	public void init(Role role) {
		setUid(role.getId());
		// 初始化资源
		Cityinitialize initialize = dataManager.serach(Cityinitialize.class, GameConfig.CITYINITIALIZE_ID);
		role.addRoleMoney(initialize.getBornmoney());
		List<String> resourceStrs = initialize.getResourcescount();
		for (int j = 0; j < resourceStrs.size(); j++) {
			String res = resourceStrs.get(j);
			String[] rss = res.split(":");
			Resourcestype rt = dataManager.serach(Resourcestype.class, rss[0]);
			ResourceTypeConst type = ResourceTypeConst.search(rss[0]);
			if (rt == null || type == null) {
				GameLog.error("Can't find initialize resource type when id is " + rss[0]);
				continue;
			}
			long num = Long.parseLong(rss[1]);
			resources.put(type, num);
		}
		// 初始化队列
		TimerLast forever = new TimerLast(0, 0, TimerLastType.TIME_FOREVER);
		for (int i = 0; i < initialize.getBuildQueue(); i++) {
			TimeQueue timeQueue = new TimeQueue();
			timeQueue.setTimer(forever);
			buildQueue.add(timeQueue);
		}
		for (int i = 0; i < initialize.getResearchQueue(); i++) {
			TimeQueue timeQueue = new TimeQueue();
			timeQueue.setTimer(forever);
			researchQueue.add(timeQueue);
		}
		// 初始化建筑
		List<Baseland> baselands = dataManager.serachList(Baseland.class);
		if (baselands != null) {
			for (int i = 0; i < baselands.size(); i++) {
				Baseland baseland = baselands.get(i);
				if (!baseland.isInitUnlock()) {// 初始未解锁
					continue;
				}
				List<String> baselandStrs = baseland.getContainSlots();
				for (int j = 0; j < baselandStrs.size(); j++) {
					String baselandStr = baselandStrs.get(j);
					Basebuildingslot bbs = dataManager.serach(Basebuildingslot.class, baselandStr);
					String initBuildingStr = bbs.getInitBuilding();
					if (initBuildingStr.equals("NULL") || initBuildingStr.equals("null")) {
						continue;
					}
					Building building = dataManager.serach(Building.class, initBuildingStr);
					if (building == null) {
						GameLog.error("can not search Building where id is " + initBuildingStr);
						continue;
					}
					RoleBuild build = RoleBuild.create(role.getId(), id, bbs.getInitBuildingLevel(), building, bbs);
					if (build != null) {
						build.setId(maxBuildKeyId);
						maxBuildKeyId += 1;
						addBuild(build);
						build.initComponents();
						build.initPowerRatio(this);
						build.initComponentInfo(role, false);
						GameLog.info("role initialize build name is " + building.getBuildingName());
					}
				}
			}
		}
		// 初始化部队信息
		armyAgent.init(uid, id);
		// 城池科技信息
		techAgent.init(uid, id);
		// buff init
		cityAttr.init(uid, id);
	}

	/**
	 * 建筑建筑
	 * @param bbsId
	 * @param buildingId
	 * @param role
	 * @return
	 */
	public synchronized byte createBuild(String bbsId,String buildingId,Role role, int money){
		//检查槽的id是否被占用
		if( searchBuildBySoltId(bbsId) != null ){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_SLOT_UNUSED, bbsId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		Basebuildingslot bbs = dataManager.serach(Basebuildingslot.class,bbsId);
		if (bbs == null){
			GameLog.error("Can't find BaseBuildingSlot where id=" + bbsId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		
		final Building building = dataManager.serach(Building.class,buildingId);
		if (building == null){
			GameLog.error("Can't find Building where id=" + buildingId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		int initializeLevel  = building.getInitializeLevel();//初始化等级
		if(initializeLevel == 0){//有些建筑初始等级被设成了0，导致报错
			initializeLevel += 1;
		}
		if (!checkPower(buildingId,initializeLevel,Const.DEFAULT_CONSUMPTION)){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_NO_POWER);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		List<RoleBuild> buildLst = searchBuildByBuildId(buildingId);
		if(buildLst != null && buildLst.size() >= building.getMaxBuildCount()){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_MUST_ONLY,buildingId,building.getMaxBuildCount());
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildingId,initializeLevel);
		if (buildLevel == null){
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 时间队列检查,秒时间的情况下只要有建筑队列空闲即可
		if (money == 1) {
			if(!buildQueueNotWorking()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_BUILD_QUEUE);
				return ErrorCodeConst.ERR_QUEUE_LMT.getKey();	
			}
		}else{
			int costTime = (int) (buildLevel.getTime() * (1 - getCityAttr().getImpBuildSpeed()));
			if (!couldUseBuildQueue(costTime)) {
				if (checkTimeQueueState() && checkBuildQueueTime(costTime)) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_BUILD_QUEUE);
					return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
				} else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TIME_BUILD_QUEUE);
					return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
				}
			}
		}
		//判断建筑需要条件
		if( !checkBuildConditions(role, buildLevel)){
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		//add buff
		// 检查资源条件，使用金币  在减去RoleArmyAttr.getEffValV2NoAll(role,TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 5))
		float armyAttrBuff = 0;
		if(checkDefense(buildingId))
			armyAttrBuff = RoleArmyAttr.getEffValV2NoAll(role,TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 5);
		long time = (long) (buildLevel.getTime() * (1 - cityAttr.getImpBuildSpeed()-armyAttrBuff));
		GameLog.info("[BuildLevelup]uid="+role.getJoy_id()+"|buildId="+buildingId+"|time="+buildLevel.getTime()+"|buff="+cityAttr.getImpBuildSpeed()+"|armyAttrBuff="+armyAttrBuff+"|time="+time);
		int costMoney = 0;
		if(money > 0){
			if(money == 2){
				costMoney = getCostMoney(role, buildLevel.getBuildCostList(),buildLevel.getNeeditem(), 0, (byte)1);
			}else{
				if(time > role.getFreeTime()){
					costMoney = getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(),time, (byte)1);
				}else{
					costMoney = getCostMoney(role, buildLevel.getBuildCostList(),buildLevel.getNeeditem(), 0, (byte)1);
				}
			}
			if(costMoney > role.getMoney()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return ErrorCodeConst.ERR_NORMAL.getKey();
			}
		}else if( !checkResConditions(role, buildLevel.getBuildCostList())){
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		RoleBuild build  = RoleBuild.create(role.getId(),id,initializeLevel,building,bbs);
		if (build == null){
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		if (addBuild(build)){
			maxBuildKeyId += 1;
			build.setId(maxBuildKeyId);
			build.setState(RoleBuildState.COND_UPGRADE.getKey());//创建中
			build.initComponents();
			build.initPowerRatio(this);
			//下发数据
			RespModuleSet rms = new RespModuleSet();
			if( money == 1){
				role.redRoleMoney(costMoney);
				build.createFinish(false);
				role.sendRoleToClient(rms);
				//任务事件
				if(time > role.getFreeTime()){
					role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), 
										ConditionType.C_ACC_BUILD, TimerLastType.TIME_CREATE);
				}
			}else{
				 if(money == 2){
					role.redRoleMoney(costMoney);
					role.sendRoleToClient(rms);
				}
				// 添加建筑时间
				//低于5分钟免费
				if (time < role.getFreeTime()){
					build.setState(RoleBuildState.COND_UPGRADEFREE.getKey());
				}
				TimerLast timer = build.addBuildTimer(time, TimerLastType.TIME_CREATE);
				timer.registTimeOver(new RoleBuildCreateFinish(build));
				LogManager.buildLog(role, build.getSlotID(),build.getBuildId(), build.getLevel(),BuildOperation.creatBuild.getKey());
			}
			LogManager.goldConsumeLog(role, costMoney, EventName.createBuild.getName());
			sendToClient(rms, false);//下发城市
			build.sendToClient(rms);//下发建筑
			List<Object> costRes = redCostResource(buildLevel.getBuildCostList(), costMoney,EventName.createBuild.getName());
			role.sendResourceToClient(false,rms,id,costRes.toArray());
			redNeedItems(role, buildLevel.getNeeditem());
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			try {
				if(money==1){
					NewLogManager.buildLog(role, "put_building",buildingId,true,costMoney);
				}else if(money ==0||money==2){
					List<String>  res = buildLevel.getBuildCostList();
					StringBuffer sb = new StringBuffer();
					if(money==2){
						sb.append(true);
						sb.append(GameLog.SPLIT_CHAR);
						sb.append(costMoney);
						sb.append(GameLog.SPLIT_CHAR);
					}
					for(int i=0;i<res.size();i++){
						String resource = res.get(i);
						String[] params = resource.split(":");
						for (int j = 0 ; j < params.length ; j++){
							Object obj = params[j];
							sb.append(obj.toString());
							sb.append(GameLog.SPLIT_CHAR);
						}
					}
					String newStr = sb.toString().substring(0, sb.toString().length() - 1);
					if(money==2){
						NewLogManager.buildLog(role, "put_building",buildingId,newStr);
					}else{
						NewLogManager.buildLog(role, "put_building",buildingId,false,0,newStr);
					}
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			return ErrorCodeConst.SUC_RETURN.getKey();
		}else{
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_MUST_ONLY,building.getBuildingName(),building.getMaxBuildCount());
		}
		return ErrorCodeConst.ERR_NORMAL.getKey();
	}

	/**
	 * 检查建筑条件
	 * 
	 * @param role
	 * @param buildLevel
	 * @return
	 */
	public boolean checkBuildConditions(Role role, Buildinglevel buildLevel) {
		return checkBuildConditions(role, buildLevel.getNeedBuildingIDList());
	}

	/**
	 * 检查建筑条件
	 * 
	 * @param role
	 * @param needBuildingIDList
	 * @return
	 */
	public boolean checkBuildConditions(Role role, List<String> needBuildingIDList) {
		for (int i = 0; i < needBuildingIDList.size(); i++) {
			String needKey = needBuildingIDList.get(i);
			Buildinglevel needBuildLevel = dataManager.serach(Buildinglevel.class, needKey);
			String buildKey = needBuildLevel.getBuildingID();// 建筑固化编号
			Building needBuilding = dataManager.serach(Building.class, buildKey);
			int needLevelNum = needBuilding.getLevelDataList().indexOf(needKey) + 1;// 等级
			List<RoleBuild> buildLst = searchBuildByBuildId(buildKey);
			boolean flag = false;
			boolean isBuild = true;
			for (int j = 0; j < buildLst.size(); j++) {
				RoleBuild build = buildLst.get(j);
				List<TimerLast> timers = build.getTimers();
				for (int k = 0; k < timers.size(); k++) {
					TimerLast timer = timers.get(k);
					if (timer.getType() == TimerLastType.TIME_CREATE) { // 正在建造的排除
						isBuild = false;
						break;
					}
				}
				if (build.getLevel() >= needLevelNum && isBuild) {// 建筑中有一个已经满足条件
					flag = true;
					break;
				}
			}
			if (!flag) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NEED_BUILD, needLevelNum,
						needBuilding.getBuildingName());
				return false;
			}
		}
		return true;
	}

	/**
	 * 检查资源数量
	 * 
	 * @param role
	 * @param buildCostList
	 * @return
	 */
	public boolean checkResConditions(Role role, List<String> buildCostList) {
		// 检查资源
		for (int i = 0; i < buildCostList.size(); i++) {
			String cost = buildCostList.get(i);
			String[] cs = cost.split(":");
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			if (type == null) {
				GameLog.error("Can't find resource type when id is " + cs[0]);
				return false;
			}
			long need = Long.parseLong(cs[1]);
			if (getResource(type) < need) {
				Resourcestype rt = dataManager.serach(Resourcestype.class, type.getKey());
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_RESOURCE, need, rt.getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取金币时间花费
	 * 
	 * @param role
	 * @param resCostList
	 * @param time
	 * @param isTime
	 *            0-无免费时间，1-有免费时间
	 * @return
	 */
	public int getCostMoney(Role role, List<String> resCostList, List<String> needItems, long time, byte isTime) {
		int costMoney = 0;
		for (String cost : resCostList) {
			String[] cs = cost.split(":");
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			if (type != null) {
				long need = Long.parseLong(cs[1]);
				if (getResource(type) < need) {
					int temp = role.resourceChgMoney(type, need - getResource(type));
					costMoney += temp;
				}
			} else {
				GameLog.error("Can't find resource type when id is " + cs[0]);
				return 0;
			}
		}
		if (needItems != null && needItems.size() != 0) {
			for (int i = 0; i < needItems.size(); i++) {
				String item = needItems.get(i);
				String[] it = item.split(":");
				Item tm = dataManager.serach(Item.class, it[0]);
				Shop sp = dataManager.serach(Shop.class, tm.getGoldPrice());
				if (item != null) {
					long count = role.getBagAgent().getItemNumFromBag(it[0]);
					long num = Long.valueOf(it[1]);
					if (count < num) {
						costMoney += sp.getSaleSprice() * (num - count);
					}
				}
			}
		}
		if (time > 0) {
			int timeMoney = role.timeChgMoney(time, isTime);
			costMoney += timeMoney;
		}
		return costMoney;
	}

	/**
	 * 扣资源
	 * 
	 * @param buildCostList
	 */
	public List<Object> redCostResource(List<String> buildCostList, int costMoney, String event) {
		// 扣除消耗
		List<Object> resLst = new ArrayList<Object>();
		for (int i = 0; i < buildCostList.size(); i++) {
			String cost = buildCostList.get(i);
			String[] cs = cost.split(":");
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			long need = Long.parseLong(cs[1]);
			if (costMoney > 0) {
				if (need > getResource(type)) {
					need = getResource(type);
				}
			}
			redResource(type, need);
			if (need > 0) {
				Role role = world.getRole(uid);
				LogManager.itemConsumeLog(role, need, event, type.getKey());
				resLst.add(type);
				resLst.add(need * -1);
			}
		}
		return resLst;
	}

	/**
	 * 扣物品(钢筋)
	 * 
	 * @param needItems
	 */
	public void redNeedItems(Role role, List<String> needItems) {
		List<ItemCell> aList = new ArrayList<>();
		for (int i = 0; i < needItems.size(); i++) {
			String item = needItems.get(i);
			String[] it = item.split(":");
			if (item != null) {
				long count = role.getBagAgent().getItemNumFromBag(it[0]);// 拥有物品数量
				long num = Long.valueOf(it[1]); // 需要物品数量
				ItemCell itemCell = role.getBagAgent().getItemFromBag(it[0]);
				role.getBagAgent().removeItems(it[0], count >= num ? num : count);
				if (itemCell == null) {
					continue;
				}
				aList.add(itemCell);
			}
		}
		if (role.isOnline()) {
			RespModuleSet rms = new RespModuleSet();
			role.getBagAgent().sendItemsToClient(rms, aList);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}

	}

	/**
	 * 升级建筑
	 * 
	 * @param buildId
	 * @param role
	 */
	public synchronized byte BuildLevelup(Role role, long buildId, int money) {
		RoleBuild roleBuild = this.searchBuildById(buildId);
		if (roleBuild == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 倒计时检查，一个建筑同时只能有一个倒计时
		if (!roleBuild.checkTimerType(TimerLastType.TIME_LEVEL_UP)
				|| roleBuild.getState() == RoleBuildState.COND_WORKING.getKey()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, roleBuild.getId());
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 升级条件检查
		final Building building = dataManager.serach(Building.class, roleBuild.getBuildId());
		if (building == null) {
			GameLog.error("Can't find Building where id=" + roleBuild.getBuildId());
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 最大等级检查
		if (building.getMaxLevel() <= roleBuild.getLevel()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_LEVEL_MAX, buildId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		int newLevel = roleBuild.getLevel() + 1;
		float powerRation = roleBuild.getCostPowerRatio();
		// 检查建筑等级对应的升级条件
		if (!checkPower(roleBuild.getBuildId(), newLevel,powerRation)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NO_POWER);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(roleBuild.getBuildId(), newLevel);
		if (buildLevel == null) {
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 时间队列检查,秒时间的情况下只要有建筑队列空闲即可
		if (money == 1) {
			if (!buildQueueNotWorking()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_BUILD_QUEUE);
				return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
			}
		} else {
			int costTime = (int) (buildLevel.getTime() * (1 - getCityAttr().getImpBuildSpeed()));
			if (!couldUseBuildQueue(costTime)) {
				if (checkTimeQueueState() && checkBuildQueueTime(costTime)) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_BUILD_QUEUE);
					return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
				} else {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TIME_BUILD_QUEUE);
					return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
				}
			}
		}
		// 判断建筑需要条件
		if (!checkBuildConditions(role, buildLevel)) {
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		float armyAttrBuff = 0;
		if(checkDefense(roleBuild.getBuildId()))
			armyAttrBuff = RoleArmyAttr.getEffValV2NoAll(role,TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 5);
		// add buff
		long time = (long) (buildLevel.getTime() * (1.0f - cityAttr.getImpBuildSpeed() - armyAttrBuff));
		BuildComponentStorage storage = roleBuild.getComponent(BuildComponentType.BUILD_COMPONENT_STORAGE);
		if(storage != null ){
			time = (long) (time*1.0/roleBuild.getCostPowerRatio());
			GameLog.info("[BuildLevelup]uid=" + role.getJoy_id() + "|buildId=" + buildId + "|time=" + buildLevel.getTime()
			+ "|buff=" + cityAttr.getImpBuildSpeed() + "|armyAttrBuff="+armyAttrBuff+"|time=" + time +"|is  BUILD_COMPONENT_STORAGE=");
		}
		GameLog.info("[BuildLevelup]uid=" + role.getJoy_id() + "|buildId=" + buildId + "|time=" + buildLevel.getTime()
				+ "|buff=" + cityAttr.getImpBuildSpeed() + "|armyAttrBuff="+armyAttrBuff+"|time=" + time);
		BuildComponentProduction producter = roleBuild.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
		if (producter != null) {// 生成类型的建筑，自动收已生产的资源
			producter.collectResource(role, roleBuild);
		}
		// 检查资源条件，使用金币
		int costMoney = 0;
		if (money > 0) {
			if (money == 2) {
				costMoney = getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(), 0, (byte) 1);
			} else {
				if (buildLevel.getTime() > role.getFreeTime()) {
					costMoney = getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(), time,
							(byte) 1);
				} else {
					costMoney = getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(), 0,
							(byte) 1);
				}
			}
			if (costMoney > role.getMoney()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return ErrorCodeConst.ERR_NORMAL.getKey();
			}
		} else if (!checkResConditions(role, buildLevel.getBuildCostList())
				|| !checkItemConditions(role, buildLevel.getNeeditem())) {
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		LogManager.buildLog(role, roleBuild.getSlotID(), roleBuild.getBuildId(), roleBuild.getLevel(),
				BuildOperation.uplevelBuild.getKey());
		roleBuild.setState(RoleBuildState.COND_UPGRADE.getKey());
		// 下发数据
		RespModuleSet rms = new RespModuleSet();
		if (money == 1) {
			role.redRoleMoney(costMoney);
			role.sendRoleToClient(rms);
			roleBuild.leveupFinish(true);
			if (costMoney > 0) {
				// 任务事件
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD,
						TimerLastType.TIME_LEVEL_UP);
				role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.ACCELERATE, time);
			}
		} else {
			if (money == 2) {
				role.redRoleMoney(costMoney);
				role.sendRoleToClient(rms);
			}
			// 添加建筑时间
			// 低于5分钟免费
			if (buildLevel.getTime() <= role.getFreeTime()) {
				roleBuild.setState(RoleBuildState.COND_UPGRADEFREE.getKey());
			}
			TimerLast timer = roleBuild.addBuildTimer(time, TimerLastType.TIME_LEVEL_UP);
			timer.registTimeOver(new RoleBuildLevelUpFinish(roleBuild));
		}
		LogManager.goldConsumeLog(role, costMoney, EventName.BuildLevelup.getName());
		sendToClient(rms, false);// 发城市
		List<Object> costRes = redCostResource(buildLevel.getBuildCostList(), costMoney,
				EventName.BuildLevelup.getName());
		role.sendResourceToClient(false, rms, id, costRes.toArray());
		roleBuild.sendToClient(rms);// 升级完成后下发建筑
		redNeedItems(role, buildLevel.getNeeditem());
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			if (money == 1) {
				NewLogManager.buildLog(role, "building_upgrade", buildId, true, costMoney);
			} else if (money == 0 || money == 2) {
				List<String> res = buildLevel.getBuildCostList();
				StringBuffer sb = new StringBuffer();
				if (money == 2) {
					sb.append(true);
					sb.append(GameLog.SPLIT_CHAR);
					sb.append(costMoney);
					sb.append(GameLog.SPLIT_CHAR);
				}
				for (int i = 0; i < res.size(); i++) {
					String resource = res.get(i);
					String[] params = resource.split(":");
					for (int j = 0; j < params.length; j++) {
						Object obj = params[j];
						sb.append(obj.toString());
						sb.append(GameLog.SPLIT_CHAR);
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				if (money == 2) {
					NewLogManager.buildLog(role, "building_upgrade", buildId, newStr);
				} else {
					NewLogManager.buildLog(role, "building_upgrade", buildId, false, 0, newStr);
				}
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}

		return ErrorCodeConst.SUC_RETURN.getKey();
	}

	/**
	 * 检查升级所需的道具
	 * 
	 * @param role
	 * @param needitem
	 * @return
	 */
	private boolean checkItemConditions(Role role, List<String> needItem) {
		if (needItem.size() < 1) {
			return true;
		}
		boolean result = true;
		for (int i = 0; i < needItem.size(); i++) {
			String temp = needItem.get(i);
			if (StringUtils.isNull(temp)) {
				continue;
			}
			String[] params = temp.split(":");
			if (params.length < 2) {
				continue;
			}
			if (role.getBagAgent().getItemNumFromBag(params[0]) < Long.valueOf(params[1])) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_UP_ITEM_INSUFFICIENT);
				result = false;
			}
		}
		return result;
	}

	/**
	 * 取消建造
	 * 
	 * @param role
	 * @param buildId
	 * @return
	 */
	public boolean cancelCreateBuild(Role role, long buildId) {
		RoleBuild roleBuild = this.searchBuildById(buildId);
		if (roleBuild == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		if (roleBuild.getTimerSize() == 0 || roleBuild.getState() != RoleBuildState.COND_UPGRADE.getKey()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		boolean bCancel = roleBuild.modifyTimer(0, TimerLastType.TIME_CREATE, true);
		if (!bCancel) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(roleBuild.getBuildId(), roleBuild.getLevel());
		List<String> buildCostList = buildLevel.getBuildCostList();
		// 返还消耗的资源50%
		List<Object> costRes = new ArrayList<Object>();
		for (int i = 0; i < buildCostList.size(); i++) {
			String cost = buildCostList.get(i);
			String[] cs = cost.split(":");
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			long need = Long.parseLong(cs[1]);
			need = (long) (need * Const.RES_CANCEL_RETURN_RATE);
			costRes.add(type);
			costRes.add(need);
		}
		List<String> items = buildLevel.getNeeditem();
		List<ItemCell> aList = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			String itemD = items.get(i);
			if (StringUtils.isNull(itemD)) {
				continue;
			}
			String[] params = itemD.split(":");
			if (params.length < 2) {
				continue;
			}
			List<ItemCell> temp = role.getBagAgent().addGoods(params[0], Integer.valueOf(params[1]));
			aList.addAll(temp);
			LogManager.itemOutputLog(role, Integer.valueOf(params[1]), EventName.cancelCreateBuild.getName(),
					params[0]);
		}
		LogManager.buildLog(role, roleBuild.getSlotID(), roleBuild.getBuildId(), roleBuild.getLevel(),
				BuildOperation.cancleCreBuild.getKey());
		roleBuild.setState(RoleBuildState.COND_STAYBY.getKey());
		roleBuild.sendToClient();
		roleBuild.setState(RoleBuildState.COND_DELETED.getKey());
		RespModuleSet rms = new RespModuleSet();
		role.getBagAgent().sendItemsToClient(rms, aList);
		Object[] costObjs = costRes.toArray();
		role.addResourcesToCity(rms, id, costObjs);
		for (int i = 0; i < costObjs.length; i += 2) {
			ResourceTypeConst type = (ResourceTypeConst) costObjs[i];
			long value = ((Number) costRes.toArray()[i + 1]).longValue();
			if (value < 0) {
				GameLog.error(" add resource value must > 0");
				continue;
			}
			String item = type.getKey();
			if (value != 0) {
				LogManager.itemOutputLog(role, value, EventName.cancelCreateBuild.getName(), item);
			}
		}
		// 清空联盟帮助的消息
		removeRoleUnionHelp(role, buildId);
		// finish下发
		RespModuleSet rms1 = new RespModuleSet();
		sendToClient(rms1, false);
		MessageSendUtil.sendModule(rms1, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "cancel_build", roleBuild.getBuildId());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * 清空联盟帮助的消息
	 * 
	 * @param role
	 * @param buildId
	 */
	private void removeRoleUnionHelp(Role role, long buildId) {
		// 清除个人是已被帮助的列表
		unionHelpers.remove(buildId);
		// 清出联盟帮助列表的内容
		MemberAssistance clrAss = null;
		if (role.getUnionId() != 0) {
			UnionBody unionBody = unionManager.search(role.getUnionId());
			if (unionBody != null && unionBody.getAssistances() != null && unionBody.getAssistances().size() > 0) {
				for (int i = 0; i < unionBody.getAssistances().size(); i++) {
					MemberAssistance ma = unionBody.getAssistances().get(i);
					if (ma == null) {
						continue;
					}
					if (ma.getUid() == role.getId() && ma.getBuildId() == buildId) {
						clrAss = ma;
						break;
					}
				}
			}
			if (clrAss != null) {
				unionBody.getAssistances().remove(clrAss);
				unionBody.sendHelperInfoToAllMembers(role, clrAss, ClientModule.DATA_TRANS_TYPE_DEL);
			}
		}
	}

	/**
	 * 取消建筑升级
	 * 
	 * @param buildId
	 * @param role
	 * @return
	 */
	public boolean cancelBuildLevelup(Role role, long buildId) {
		final RoleBuild roleBuild = this.searchBuildById(buildId);
		if (roleBuild == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		// 检查升级队列是否存在
		boolean bCancel = roleBuild.modifyTimer(0, TimerLastType.TIME_LEVEL_UP, true);
		if (bCancel) {
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(roleBuild.getBuildId(),
					roleBuild.getLevel() + 1);
			if (buildLevel == null) {
				GameLog.error("read buildLevel base is fail");
				return false;
			}
			List<String> buildCostList = buildLevel.getBuildCostList();
			// 返还消耗的资源
			List<Object> costRes = new ArrayList<Object>();
			for (int i = 0; i < buildCostList.size(); i++) {
				String cost = buildCostList.get(i);
				String[] cs = cost.split(":");
				ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
				long need = Long.parseLong(cs[1]);
				need = (long) (need * Const.RES_CANCEL_RETURN_RATE);
				costRes.add(type);
				costRes.add(need);
			}
			List<String> items = buildLevel.getNeeditem();
			List<ItemCell> aList = new ArrayList<>();
			for (int i = 0; i < items.size(); i++) {
				String itemD = items.get(i);
				if (StringUtils.isNull(itemD)) {
					continue;
				}
				String[] params = itemD.split(":");
				if (params.length < 2) {
					continue;
				}
				List<ItemCell> temp = role.getBagAgent().addGoods(params[0], Integer.valueOf(params[1]));
				aList.addAll(temp);
				LogManager.itemOutputLog(role, Integer.valueOf(params[1]), EventName.cancelBuildLevelup.getName(),
						params[0]);
			}
			LogManager.buildLog(role, roleBuild.getSlotID(), roleBuild.getBuildId(), roleBuild.getLevel(),
					BuildOperation.cancleUpBuild.getKey());
			roleBuild.setState(RoleBuildState.COND_NORMAL.getKey());
			// 下发数据
			RespModuleSet rms = new RespModuleSet();
			sendToClient(rms, false);// 发城市
			role.getBagAgent().sendItemsToClient(rms, aList);// 下发背包
			roleBuild.sendToClient(rms);// 下发建筑
			Object[] costObjs = costRes.toArray();
			role.addResourcesToCity(rms, id, costObjs);
			for (int i = 0; i < costObjs.length; i += 2) {
				ResourceTypeConst type = (ResourceTypeConst) costObjs[i];
				long value = ((Number) costRes.toArray()[i + 1]).longValue();
				if (value < 0) {
					GameLog.error(" add resource value must > 0");
					continue;
				}
				String item = type.getKey();
				if (value != 0) {
					LogManager.itemOutputLog(role, value, EventName.cancelBuildLevelup.getName(), item);
				}
			}

		} else {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		// 清空联盟帮助的消息
		try {
			NewLogManager.buildLog(role, "cancel_upgrade", roleBuild.getBuildId(), roleBuild.getLevel());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		removeRoleUnionHelp(role, buildId);
		return false;
	}

	/**
	 * 取消拆除建筑
	 * 
	 * @param role
	 * @param buildId
	 * @return
	 */
	public boolean cancelRemoveBuild(Role role, long buildId) {
		final RoleBuild roleBuild = this.searchBuildById(buildId);
		if (roleBuild == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		// 检查升级队列是否存在
		boolean bCancel = roleBuild.modifyTimer(0, TimerLastType.TIME_REMOVE, true);
		if (bCancel) {
			roleBuild.setState((byte) 0);
			// 下发数据
			RespModuleSet rms = new RespModuleSet();
			sendToClient(rms, false);// 发城市
			roleBuild.sendToClient(rms);// 下发建筑
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			LogManager.buildLog(role, roleBuild.getSlotID(), roleBuild.getBuildId(), roleBuild.getLevel(),
					BuildOperation.cancleReBuild.getKey());
		} else {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		return true;
	}

	/**
	 * 拆除建筑
	 * 
	 * @param role
	 * @param buildId
	 * @return
	 */
	public byte removeBuild(Role role, long buildId, int money) {
		final RoleBuild roleBuild = this.searchBuildById(buildId);
		if (roleBuild == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		if (roleBuild.getState() != RoleBuildState.COND_NORMAL.getKey() || roleBuild.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_STATE_WRONG, buildId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		// 检查该建筑是否可拆除
		if (roleBuild.isOnly()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_CANNOT_REMOVE, buildId);
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		if (roleBuild.getBuildId().equals(BuildName.POWER_PLANT.getKey())) {
			if (getAllPower() - roleBuild.getProductePower() < getUsePower()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_POWERPLANT_CANNOT_REMOVE, buildId);
				return ErrorCodeConst.ERR_NORMAL.getKey();
			}
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(roleBuild.getBuildId(), roleBuild.getLevel());
		if (buildLevel == null) {
			return ErrorCodeConst.ERR_NORMAL.getKey();
		}
		long time = buildLevel.getTime() / 2;
		// 时间队列检查
		if (!couldUseBuildQueue(time)) {
			if (checkTimeQueueState() && !checkBuildQueueTime(time)) { // 空闲
																		// 时间不足
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TIME_BUILD_QUEUE);
				return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
			} else if (checkBuildQueueTime(time)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_BUILD_QUEUE);
				return ErrorCodeConst.ERR_QUEUE_LMT.getKey();
			} else {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MAX_BUILD_QUEUE);
				return ErrorCodeConst.ERR_NORMAL.getKey();
			}
		}
		int costMoney = 0;
		if (money > 0) {
			costMoney = getCostMoney(role, buildLevel.getBuildCostList(), buildLevel.getNeeditem(),
					buildLevel.getTime(), (byte) 0);
			if (costMoney > role.getMoney()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return ErrorCodeConst.ERR_NORMAL.getKey();
			}
		}
		LogManager.buildLog(role, roleBuild.getSlotID(), roleBuild.getBuildId(), roleBuild.getLevel(),
				BuildOperation.removeBuild.getKey());
		roleBuild.setState(RoleBuildState.COND_DISMANTLE.getKey());// 设置为拆除状态
		// 下发数据
		RespModuleSet rms = new RespModuleSet();

		if (money > 0) {
			role.redRoleMoney(costMoney);
			LogManager.goldConsumeLog(role, costMoney, EventName.removeBuild.getName());
			roleBuild.removeFinish();
			role.sendRoleToClient(rms);
			sendToClient(rms, false);// 发城市
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		} else {
			TimerLast timer = roleBuild.addBuildTimer(time, TimerLastType.TIME_REMOVE);
			timer.registTimeOver(new RoleBuildRemoveFinish(roleBuild));
			sendToClient(rms, false);// 发城市 城市信息和建筑信息的下发顺序会影响客户端的倒计时展示
			roleBuild.sendToClient(rms);// 升级完成后下发建筑
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
		return ErrorCodeConst.SUC_RETURN.getKey();
	}

	/**
	 * 移动建筑
	 * 
	 * @param role
	 * @param slotId1
	 * @param slotId2
	 * @return
	 */
	public boolean changeBuildsSlot(Role role, String slotId1, String slotId2) {
		Basebuildingslot bbs1 = dataManager.serach(Basebuildingslot.class, slotId1);
		Basebuildingslot bbs2 = dataManager.serach(Basebuildingslot.class, slotId2);
		RoleBuild build1 = searchBuildBySoltId(slotId1);
		RoleBuild build2 = searchBuildBySoltId(slotId2);
		if (build1 != null) {
			// 检查槽2是否可以存放建筑1
			if (!bbs2.getBuildLimitation().contains(build1.getBuildId())) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, slotId1);
				return false;
			}
		}
		if (build2 != null) {
			// 检查槽1是否可以存放建筑2
			if (!bbs1.getBuildLimitation().contains(build2.getBuildId())) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, slotId2);
				return false;
			}
		}
		if (build1 != null || build2 != null) {
			if (build1 != null) {
				build1.setSlotID(slotId2);
				LogManager.buildLog(role, build1.getSlotID(), build1.getBuildId(), build1.getLevel(),
						BuildOperation.moveBuild.getKey());
			}
			if (build2 != null) {
				build2.setSlotID(slotId1);
				LogManager.buildLog(role, build2.getSlotID(), build2.getBuildId(), build2.getLevel(),
						BuildOperation.moveBuild.getKey());
			}

			try {
				if (build1 != null && build2 == null) {
					NewLogManager.buildLog(role, "move_building", build1.getBuildId());
				} else if (build1 == null && build2 != null) {
					NewLogManager.buildLog(role, "move_building", build2.getBuildId());
				} else if (build1 != null && build2 != null) {
					NewLogManager.buildLog(role, "move_building", build1.getBuildId(), build2.getBuildId());
				}
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}

		} else {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, slotId2);
			return false;
		}
		// 下发建筑
		RespModuleSet rms = new RespModuleSet();
		if (build1 != null)
			build1.sendToClient(rms);
		if (build2 != null)
			build2.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	/**
	 * 灭火处理 TODO
	 * 
	 * @return
	 */
	public boolean outFireState(Role role) {
		MapCity mapCity = mapWorld.searchMapCity(uid, id);
		if (!mapCity.getCityState().isFire()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_CITY_STATE_NOT_RIGHT, id);
			return false;
		}
		int costMoney = Const.CITY_OUT_FIRE_MONEY;
		if (role.getMoney() < costMoney) {
			return false;
		}
		role.redRoleMoney(costMoney);
		mapCity.getCityState().updateTimer(TimerLastType.TIME_CITY_FIRE);
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.FENCE.getKey());
		for (RoleBuild build : builds) {
			BuildComponentWall wall = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
			if (wall != null) {
				wall.cancelFireState();
			}
		}
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		sendCityStateToClient(role, rms);
		sendToClient(rms, false);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "wall_extinguish", costMoney);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * 扣资源,资源不能被扣成负数
	 * 
	 * @param type
	 * @param value
	 */
	public boolean redResource(ResourceTypeConst type, long value) {
		if (resources.containsKey(type)) {
			long pre = resources.get(type).longValue();
			long now = pre - value < 0 ? 0 : pre - value;
			resources.put(type, now);
			return true;
		}
		return false;
	}

	/**
	 * 购买建造队列
	 * 
	 * @param lastTime
	 * @param type
	 *            0:使用道具 1:未使用
	 * @return
	 */
	public boolean addCityNewQueue(long lastTime, int type) {
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			return false;
		}
		if (type != 0) {
			if (role.getMoney() < GameConfig.BUY_QUEUE_COST_MONEY) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,
						GameConfig.BUY_QUEUE_COST_MONEY);
				return false;
			}
			role.redRoleMoney(GameConfig.BUY_QUEUE_COST_MONEY);
		}
		long startTime = TimeUtils.nowLong() / 1000;
		if (lastTime == 0) {
			lastTime = GameConfig.BUY_QUEUE_GET_TIME * (Const.HOUR / 1000);
		}
		boolean bSuc = false;
		for (TimeQueue queue : buildQueue) {
			if (queue.getTimer().getType() == TimerLastType.TIME_QUEUE) {
				queue.getTimer().setLast(queue.getTimer().getLast() + lastTime);
				bSuc = true;
				break;
			}
		}
		if (!bSuc) {
			addNewBuildQueue(startTime, lastTime, TimerLastType.TIME_QUEUE);
		}
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms, false);
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "use_extrabuildqueue");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * 获取资源类型
	 * 
	 * @param type
	 * @return
	 */
	public long getResource(ResourceTypeConst type) {
		if (type != null && resources.containsKey(type)) {
			return resources.get(type).longValue();
		}
		return 0;
	}

	/**
	 * 获取初始电力
	 * 
	 * @return
	 */
	public int getIdlePower() {
		Cityinitialize initialize = dataManager.serach(Cityinitialize.class, GameConfig.CITYINITIALIZE_ID);
		if (initialize != null) {
			return initialize.getIdlePower();
		}
		return 0;
	}

	/**
	 * 获取最大建筑队列数
	 * 
	 * @return
	 */
	public int getMaxBuildQueue() {
		Cityinitialize initialize = dataManager.serach(Cityinitialize.class, GameConfig.CITYINITIALIZE_ID);
		if (initialize != null) {
			return initialize.getMaxBuildQueue();
		}
		return 0;
	}

	/**
	 * 获取最大研究队列数
	 * 
	 * @return
	 */
	public int getMaxResearchQueue() {
		Cityinitialize initialize = dataManager.serach(Cityinitialize.class, GameConfig.CITYINITIALIZE_ID);
		if (initialize != null) {
			return initialize.getMaxResearchQueue();
		}
		return 0;
	}

	/**
	 * 可以使用的建造队列数量
	 * 
	 * @return
	 */
	public int getBuildQueueNum() {
		return buildQueue.size();
	}

	/**
	 * 可以使用的研究队列数量
	 * 
	 * @return
	 */
	public int getResearchQueueNum() {
		return researchQueue.size();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private void initLandIds() {
		landIds.add("1");
		landIds.add("2");
		landIds.add("3");
	}

	public void initBuildComponent(Role role) {
		for (RoleBuild build : builds) {
			build.initComponentInfo(role, true);
		}
	}

	@Override
	public void loadFromData(SqlData data) {
		id = data.getInt(DaoData.RED_ALERT_GENERAL_ID);
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		position = data.getInt(DaoData.RED_ALERT_GENERAL_POSITION);
		String str = data.getString(DaoData.RED_ALERT_CITY_BUILD_QUEUE);
		buildQueue = JsonUtil.JsonToObjectList(str, TimeQueue.class);
		str = data.getString(DaoData.RED_ALERT_CITY_RESEARCH_QUEUE);
		researchQueue = JsonUtil.JsonToObjectList(str, TimeQueue.class);
		str = data.getString(DaoData.RED_ALERT_CITY_RESOURCES);
		resources = JSON.parseObject(str, new TypeReference<Map<ResourceTypeConst, Long>>() {
		});
		state = data.getByte(DaoData.RED_ALERT_CITY_STATE);
		resSyncTime = data.getLong(DaoData.RED_ALERT_CITY_RESSYNCTIME);
		maxBuildKeyId = data.getLong(DaoData.RED_ALERT_CITY_MAXBUILDKEY);
		// 解锁的地块
		String landIdData = data.getString(DaoData.RED_ALERT_CITY_LANDIDS);
		if (!StringUtils.isNull(landIdData)) {
			landIds = JsonUtil.JsonToObjectList(landIdData, String.class);
		} else {
			initLandIds();
		}
		// 初始化部队和科技信息
		armyAgent.init(uid, id);
		techAgent.init(uid, id);
		// buff init
		cityAttr.init(uid, id);
		armyAgent.deserialize(data.get(DaoData.RED_ALERT_CITY_ARMYS));
		techAgent.deserialize(data.get(DaoData.RED_ALERT_CITY_TECHDATAS));
		//电力调节数据
		String powerRatio = data.getString(DaoData.RED_CITY_POWER_RATIO);
		eAgent.deserialize(powerRatio);
		// 所有建筑
		JoyBuffer buildData = JoyBuffer.wrap((byte[]) data.get(RED_ALERT_CITY_BUILDS));
		int size = buildData.getInt();
		for (int i = 0; i < size; i++) {
			RoleBuild build = new RoleBuild();
			build.deserialize(buildData);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}		
			if (TimeUtils.isSameDay(resSyncTime, TimeUtils.nowLong())) {
				initCDCount();
			}		
			addBuild(build);
		}
		String unionHelperData = data.getString(DaoData.RED_ALERT_CITY_HELPER_NUM);
		if (!StringUtils.isNull(unionHelperData)) {
			unionHelpers = JSON.parseObject(unionHelperData, new TypeReference<Map<Long, List<UnionHelper>>>() {
			});
		} else {
			unionHelpers = new HashMap<Long, List<UnionHelper>>();
		}
	}

	public void initCDCount() {
		RoleBuild build = getBuild();
		if (build != null) {
			BuildComponentDeal deal = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEAL);
			deal.initCount();
		}
	}
	
	@Override
	public void saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_GENERAL_ID, id);
		data.put(DaoData.RED_ALERT_GENERAL_UID, uid);
		data.put(DaoData.RED_ALERT_GENERAL_POSITION, position);
		String str = JsonUtil.ObjectToJsonString(buildQueue);
		data.put(DaoData.RED_ALERT_CITY_BUILD_QUEUE, str);
		str = JsonUtil.ObjectToJsonString(researchQueue);
		data.put(DaoData.RED_ALERT_CITY_RESEARCH_QUEUE, str);
		// 资源类型
		str = JsonUtil.ObjectToJsonString(resources);
		data.put(DaoData.RED_ALERT_CITY_RESOURCES, str);
		data.put(DaoData.RED_ALERT_CITY_STATE, state);
		data.put(DaoData.RED_ALERT_CITY_MAXBUILDKEY, maxBuildKeyId);
		long outLineTime = TimeUtils.nowLong();
		data.put(DaoData.RED_ALERT_CITY_RESSYNCTIME, outLineTime);
		data.put(DaoData.RED_ALERT_CITY_LANDIDS, JsonUtil.ObjectToJsonString(landIds));
		armyAgent.serialize(data);
		techAgent.serialize(data);
		//
		JoyBuffer out = JoyBuffer.allocate(8192);
		out.putInt(builds.size());
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			build.serialize(out);
			if (build.getBuildId().equals(BuildName.RADAR.getKey())) {
				data.put(DaoData.RED_ALERT_CITY_RADARLVL, build.getLevel());
			}
			if (build.getBuildId().equals(BuildName.CITY_CENTER.getKey())) {
				data.put(DaoData.RED_ALERT_CITY_LEVEL, build.getLevel());
			}
		}
		data.put(DaoData.RED_ALERT_CITY_BUILDS, out.arrayToPosition());
		data.put(DaoData.RED_ALERT_CITY_HELPER_NUM, JsonUtil.ObjectToJsonString(unionHelpers));
		//电力调节数据
		data.put(DaoData.RED_CITY_POWER_RATIO,eAgent.serialize());
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public boolean delete() {
		return false;
	}

	public void tick(Role role, long now) {
		for (int i = 0; i < buildQueue.size();) {
			TimeQueue queue = buildQueue.get(i);
			if (queue.getTimer().over(now)) {
				buildQueue.remove(i);
				if (role != null) {
					RespModuleSet rms = new RespModuleSet();
					sendToClient(rms, false);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
				}
			} else {
				i++;
			}
		}
		for (int i = 0; i < researchQueue.size();) {
			TimeQueue queue = researchQueue.get(i);
			if (queue.getTimer().over(now)) {
				researchQueue.remove(i);
			} else {
				i++;
			}
		}
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			build.tick(this, role, now);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				builds.remove(i);
			}
		}
		armyAgent.tick(now);
		if (resSyncTime > 0) {
			long time = TimeUtils.nowLong() - resSyncTime;
			if (time >= Const.ARMY_COST_FRESH_TIME) {
				grainConsumption(role, true); // 士兵资源消耗
			}
		}
	}

	@Override
	public String table() {
		return DaoData.TABLE_RED_ALERT_CITY;
	}

	@Override
	public String[] wheres() {
		String[] result = new String[2];
		result[0] = DaoData.RED_ALERT_GENERAL_ID;
		result[1] = DaoData.RED_ALERT_GENERAL_UID;
		return result;
	}

	@Override
	public void save() {
		if (savIng) {
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}

	public RoleBuild searchBuildById(long id) {
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			if (build.getId() == id) {
				return build;
			}
		}
		return null;
	}

	public List<RoleBuild> searchBuildByBuildId(String buildKey) {
		List<RoleBuild> results = new ArrayList<RoleBuild>();
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			if (build.getBuildId().equals(buildKey)) {
				results.add(build);
			}
		}
		return results;
	}

	public RoleBuild searchBuildBySoltId(String slotId) {
		for (RoleBuild build : builds) {
			if (build.getSlotID().equals(slotId)) {
				if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
					continue;
				}
				return build;
			}
		}
		return null;
	}

	public List<RoleBuild> searchBuildByCompomemt(BuildComponentType type) {
		List<RoleBuild> results = new ArrayList<RoleBuild>();
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getComponent(type) != null) {
				results.add(build);
			}
		}
		return results;
	}

	public int checkBuildLevelByBuildId(String buildId) {
		int level = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			if (build.getBuildId().equals(buildId)) {
				if (build.getLevel() > level) {
					level = build.getLevel();
				}
			}
		}
		return level;
	}

	public boolean addBuild(RoleBuild build) {
		if (build.isOnly()) {
			for (int i = 0; i < builds.size(); i++) {
				RoleBuild rb = builds.get(i);
				if (rb.getBuildId().equals(build.getBuildId())) {
					return false;
				}
			}
		}
		build.setLinkQueue(this);
		builds.add(build);
		return true;
	}

	public void sendToClient(RespModuleSet rms, boolean needSendBuilds) {
		// 城市的模块
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_CITY;
			}
		};
		module.add(id);// int城市编号
		module.add(uid);// long 玩家编号
		int landSize = landIds.size();
		module.add(landSize); // int
		for (int i = 0; i < landSize; i++) {
			module.add(landIds.get(i));// String 地块Id
		}
		module.add(position);// int 城市在大地图的编号
		module.add(getAllPower());// int 城池所有电力
		module.add(getUsePower());// int 获取已使用电力
		// 资源类型
		module.add(resources.size());// int 资源列表长度
		for (ResourceTypeConst type : resources.keySet()) {
			String str = type.getKey();
			module.add(str);// string 资源编号
			long value = resources.get(type);
			module.add(value);// long 资源数量
		}
		module.add(getMaxBuildQueue());// int 建筑队列的最大上限
		module.add(buildQueue.size());// int 可以使用的建造队列长度
		for (int i = 0; i < buildQueue.size(); i++) {
			TimeQueue queue = buildQueue.get(i);
			TimerLast timer = queue.getTimer();
			timer.sendToClient(module.getParams());// 这个建造队列可以用的时间倒计时
			module.add(queue.getBuild());// long 这个建造队列被那个建筑使用(数据库主键key)
		}
		module.add(getMaxResearchQueue());//// int 研究队列的最大上限
		module.add(researchQueue.size());// int 可以使用的研究队列长度
		for (int i = 0; i < researchQueue.size(); i++) {
			TimeQueue queue = researchQueue.get(i);
			TimerLast timer = queue.getTimer();
			timer.sendToClient(module.getParams());// 这个研究队列可以用的时间倒计时
			module.add(queue.getBuild());// long 这个研究队列被那个建筑使用(数据库主键key)
		}
		// 军校，指挥所
		module.add(getMaxOutBattleAllNum());// int 指挥所
		module.add(getMaxTrainNum(BuildName.AIR_COM));// int 飞机
		module.add(getMaxTrainNum(BuildName.ARMORED_FACT));// int 坦克
		module.add(getMaxTrainNum(BuildName.WAR_FACT));// int 战车
		module.add(getMaxTrainNum(BuildName.SOLDIERS_CAMP));// 步兵
		module.add(getMaxTrainNum(BuildName.MILITARY_FACT));// 军工厂
		module.add((byte) (getMapCityState().isFire() ? 1 : 0));// 城池失火状态
		rms.addModule(module);
		if (needSendBuilds) {
			sendBuildsToClient(rms);
			// 发送城池部队信息
			armyAgent.sendToClient(rms, this);
			// 科技树
			techAgent.sendToClient(rms);
		}
	}

	public CityState getMapCityState() {
		if (uid == 0) {
			return null;
		}
		MapCity mapCity = mapWorld.searchMapCity(uid, id);
		if (mapCity != null) {
			return mapCity.getCityState();
		}
		return null;
	}

	public void sendBuildsToClient(RespModuleSet rms) {
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			build.sendToClient(rms);
		}
	}

	public void sendCityStateToClient(Role role, RespModuleSet rms) {
		if (rms == null) {
			rms = new RespModuleSet();
		}
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_CITY_STATE;
			}
		};
		List<String> alltimers = new ArrayList<String>();
		alltimers.add(TimerLastType.TIME_CITY_NOWAR.getKey());
		alltimers.add(TimerLastType.TIME_CITY_NOSPY.getKey());
		alltimers.add(TimerLastType.TIME_CITY_DBSPY.getKey());
		alltimers.add(TimerLastType.TIME_ITEM_RED_FOOD.getKey());
		alltimers.add(TimerLastType.TIME_ITEM_TROOPS_LIMIT.getKey());
		alltimers.add(TimerLastType.TIME_ITEM_IMP_DEF.getKey());
		alltimers.add(TimerLastType.TIME_ITEM_IMP_ATK.getKey());
		alltimers.add(TimerLastType.TIME_ITEM_IMP_COLL.getKey());
		alltimers.add(TimerLastType.TIME_CITY_FIRE.getKey());

		List<TimerLast> extimers = new ArrayList<TimerLast>();
		List<TimerLast> timers = role.getEffectAgent().getItemBuffTimer();
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			alltimers.remove(timer.getType().getKey());
			extimers.add(timer);
		}

		MapCity mapCity = mapWorld.searchMapCity(uid, id);
		if (mapCity == null) {
			return;
		}

		timers = mapCity.getCityState().searchItemTimer();
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			alltimers.remove(timer.getType().getKey());
			extimers.add(timer);
		}
		module.add(extimers.size());// int 有buff的内容
		if (extimers.size() > 0) {
			for (TimerLast timer : extimers) {
				module.add(timer.getType().getKey());// String timerId
				timer.sendToClient(module.getParams());// 倒计时
			}
		}
		module.add(alltimers.size());
		if (alltimers.size() > 0) {
			for (String key : alltimers) {
				module.add(key);// String timerId
			}
		}
		rms.addModule(module);
		eAgent.sendToClient(rms, this);
	}

	public void sendCityBuffToClient(RespModuleSet rms) {
		if (rms == null) {
			rms = new RespModuleSet();
		}
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_CITY_BUFF;
			}
		};

		Map<String, Map<String, BuffObject>> cityBuffs = getCityBuffs();
		int size = cityBuffs.size();//
		module.add(size); // int
		for (Map.Entry<String, Map<String, BuffObject>> entry : cityBuffs.entrySet()) {
			module.add(entry.getKey());// String BuildId
			module.add(entry.getValue().size());// int 建筑的buff数量
			for (Map.Entry<String, BuffObject> buffEntry : entry.getValue().entrySet()) {
				module.add(buffEntry.getValue().getKey());// String buffId
				byte type = buffEntry.getValue().getValueType();
				module.add(type);// byte buff类型
				String value = String.valueOf(buffEntry.getValue().getRate());
				if (type == 0) {// 值的类型为百分比
					module.add(value); // String
				} else if (type == 1) {// 值的类型为值类型
					module.add(buffEntry.getValue().getValue());// int
				} else {// 值的类型为百分比和值类型
					module.add(value); // 同时存在
					module.add(buffEntry.getValue().getValue());
				}
			}
		}
		// RoleCityAttr buff
		// module.add(String.valueOf(cityAttr.getAddSoldLimit())); //float
		// 单支部队的兵力数量上限
		rms.addModule(module);
		GameLog.info("BuffObject=uid=" + uid + "*****" + JsonUtil.ObjectToJsonString(cityBuffs));
	}

	/*
	 * 判断是否有建筑队列处于空闲中
	 */

	public boolean buildQueueNotWorking() {
		for (int i = 0; i < buildQueue.size(); i++) {
			TimeQueue queue = buildQueue.get(i);
			if (queue.empty()) {
				return true;
			}
		}
		return false;
	}

	public boolean couldUseBuildQueue(long time) {
		for (int i = 0; i < buildQueue.size(); i++) {
			TimeQueue queue = buildQueue.get(i);
			if (queue.couldUse(time)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkTimeQueueState() {
		for (TimeQueue queue : buildQueue) {
			if (queue.getTimer().getType() == TimerLastType.TIME_QUEUE) {
				if (!queue.empty()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkBuildQueueTime(long time) {
		for (TimeQueue queue : buildQueue) {
			if (queue.getTimer().getType() == TimerLastType.TIME_QUEUE) {
				if (!queue.couldUse(time)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void addBuildQueue(long buildId) {
		for (int i = 0; i < buildQueue.size(); i++) {
			TimeQueue queue = buildQueue.get(i);
			if (queue.empty()) {
				queue.setBuild(buildId);
				return;
			}
		}
	}

	@Override
	public void addResearchQueue(long buildId) {
		for (int i = 0; i < researchQueue.size(); i++) {
			TimeQueue queue = researchQueue.get(i);
			if (queue.empty()) {
				queue.setBuild(buildId);
			}
		}
	}

	@Override
	public void removeQueue(long buildId) {
		for (int i = 0; i < buildQueue.size(); i++) {
			TimeQueue queue = buildQueue.get(i);
			if (!queue.empty() && queue.getBuild() == buildId) {
				queue.setBuild(0);
				return;
			}
		}
		for (int i = 0; i < researchQueue.size(); i++) {
			TimeQueue queue = researchQueue.get(i);
			if (!queue.empty() && queue.getBuild() == buildId) {
				queue.setBuild(0);
			}
		}
	}

	/**
	 * 获取市政府的等级
	 * 
	 * @return
	 */
	public byte getCityCenterLevel() {
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.CITY_CENTER.getKey());
		if (builds.size() > 0) {
			return builds.get(0).getLevel();
		}
		GameLog.error("Can't find build whoes key is : " + BuildName.CITY_CENTER.getKey());
		return 1;
	}

	public RoleBuild getCiytCenter() {
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.CITY_CENTER.getKey());
		if (builds.size() > 0) {
			return builds.get(0);
		}
		return null;
	}

	/**
	 * 获取城池部队信息
	 * 
	 * @return
	 */
	public RoleArmyAgent getCityArmys() {
		return armyAgent;
	}

	/**
	 * 获取城池科技
	 * 
	 * @return
	 */
	public RoleTechAgent getTechAgent() {
		return techAgent;
	}

	public int getCityBattleEffec() {
		int value = 0;
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			Buildinglevel buildlevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
			if (buildlevel != null) {
				value += buildlevel.getAttackForce();
			}
		}
		return value;
	}

	public boolean checkDefense(String buildKey) {
		Building building = dataManager.serach(Building.class, buildKey);
		if (building != null && (building.getBuildingComponent()
				.contains(BuildComponentType.BUILD_COMPONENT_ELECTRICAL.getKey())
				|| building.getBuildingComponent().contains(BuildComponentType.BUILD_COMPONENT_WALL.getKey()))) {
			return true;
		}
		return false;
	}

	/**
	 * 检查升级或者建筑某个建筑电力是否充足
	 * 
	 * @param buildKey
	 * @param level
	 * @return
	 */
	public boolean checkPower(String buildKey, int level,float powerRatio) {
		Building building = dataManager.serach(Building.class, buildKey);
		if (building != null
				&& building.getBuildingComponent().contains(BuildComponentType.BUILD_COMPONENT_ELECTRICAL.getKey())) {
			return true;
		}
		int have = 0, use = 0, need = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			use += build.getCostPower(this);
		}
		have = getAllPower();
		if (level > 1) {
			Buildinglevel buildLevel1 = RoleBuild.getBuildinglevelByCondition(buildKey, level - 1);
			Buildinglevel buildLevel2 = RoleBuild.getBuildinglevelByCondition(buildKey, level);
			if (buildLevel1 != null && buildLevel2 != null) {
				need = buildLevel2.getPower() - buildLevel1.getPower();
			}
		} else {
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildKey, level);
			if (buildLevel != null) {
				need = buildLevel.getPower();
			}
		}
		return have >= use + (int)(need*powerRatio);
	}

	public int getAllPower() {
		int value = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			value += build.getProductePower();
		}
		value += value * cityAttr.getAddPowerProd();
		return value + getIdlePower();
	}
	
	public int getUserPower(RoleBuild... withoutBuild) {
		int use = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			use += build.getCostPower(this);
		}
		for (int i = 0; i < withoutBuild.length; i++) {
			RoleBuild build = withoutBuild[i];
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			use -= build.getCostPower(this);
		}
		return use;
	}

	public int getUsePower() {
		int value = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			value += build.getCostPower(this);
		}
		return value;
	}

	/**
	 * 获取资源的基础产量
	 * 
	 * @param resName
	 * @return
	 */
	public int getResourceProduction(String resName) {
		ResourceTypeConst type = ResourceTypeConst.search(resName);
		int production = 0;
		List<RoleBuild> builds = null;
		switch (type) {
		case RESOURCE_TYPE_FOOD:
			builds = searchBuildByBuildId(BuildName.FOOD_FACT.getKey());//
			break;
		case RESOURCE_TYPE_METAL:
			builds = searchBuildByBuildId(BuildName.SMELTER.getKey());//
			break;
		case RESOURCE_TYPE_OIL:
			builds = searchBuildByBuildId(BuildName.REFINERY.getKey());//
			break;
		case RESOURCE_TYPE_ALLOY:
			builds = searchBuildByBuildId(BuildName.TITANIUM_PLANT.getKey());//
			break;
		default:
			break;
		}
		if (builds != null) {
			for (int i = 0; i < builds.size(); i++) {
				RoleBuild build = builds.get(i);
				BuildComponentProduction com = (BuildComponentProduction)build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
				if(com != null){
					production += com.calcResourceNum();
				}
			}
		}
		return production;
	}
	/**
	 * 获取城市资源的指定时间产量
	 * 
	 * @return
	 * times  秒
	 */
	public Map<ResourceTypeConst, Long> getCityTimesRes(int times) {
		Map<ResourceTypeConst, Long> resMap = new HashMap<ResourceTypeConst, Long>();
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			BuildName name = BuildName.search(build.getBuildId());
			if (name == null) {
				GameLog.error("cannot find build by buildId = " + build.getBuildId());
				return resMap;
			}
			BuildComponentProduction com = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
			long production = 0;
			switch (name) {
			case FOOD_FACT:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_FOOD) == null) {
					production = com.calcResourceNumTimes(times);
				} else {
					production = com.calcResourceNumTimes(times) + resMap.get(ResourceTypeConst.RESOURCE_TYPE_FOOD);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_FOOD, production);
				break;
			case SMELTER:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_METAL) == null) {
					production = com.calcResourceNumTimes(times);
				} else {
					production = com.calcResourceNumTimes(times) + resMap.get(ResourceTypeConst.RESOURCE_TYPE_METAL);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_METAL, production);
				break;
			case REFINERY:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_OIL) == null) {
					production = com.calcResourceNumTimes(times);
				} else {
					production = com.calcResourceNumTimes(times) + resMap.get(ResourceTypeConst.RESOURCE_TYPE_OIL);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_OIL, production);
				break;
			case TITANIUM_PLANT:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_ALLOY) == null) {
					production = com.calcResourceNumTimes(times);
				} else {
					production = com.calcResourceNumTimes(times) + resMap.get(ResourceTypeConst.RESOURCE_TYPE_ALLOY);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_ALLOY, production);
				break;
			default:
				break;
			}
		}
		return resMap;
	}
	/**
	 * 获取城市当前的资源产量
	 * 
	 * @return
	 */
	public Map<ResourceTypeConst, Long> getCityCurrentRes() {
		Map<ResourceTypeConst, Long> resMap = new HashMap<ResourceTypeConst, Long>();
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			BuildName name = BuildName.search(build.getBuildId());
			if (name == null) {
				GameLog.error("cannot find build by buildId = " + build.getBuildId());
				return resMap;
			}
			BuildComponentProduction com = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
			long production = 0;
			switch (name) {
			case FOOD_FACT:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_FOOD) == null) {
					production = com.calcResourceNum();
				} else {
					production = com.calcResourceNum() + resMap.get(ResourceTypeConst.RESOURCE_TYPE_FOOD);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_FOOD, production);
				break;
			case SMELTER:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_METAL) == null) {
					production = com.calcResourceNum();
				} else {
					production = com.calcResourceNum() + resMap.get(ResourceTypeConst.RESOURCE_TYPE_METAL);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_METAL, production);
				break;
			case REFINERY:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_OIL) == null) {
					production = com.calcResourceNum();
				} else {
					production = com.calcResourceNum() + resMap.get(ResourceTypeConst.RESOURCE_TYPE_OIL);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_OIL, production);
				break;
			case TITANIUM_PLANT:
				if (resMap == null || resMap.get(ResourceTypeConst.RESOURCE_TYPE_ALLOY) == null) {
					production = com.calcResourceNum();
				} else {
					production = com.calcResourceNum() + resMap.get(ResourceTypeConst.RESOURCE_TYPE_ALLOY);
				}
				resMap.put(ResourceTypeConst.RESOURCE_TYPE_ALLOY, production);
				break;
			default:
				break;
			}
		}
		return resMap;
	}

	public void addNewBuildQueue(long start, long last, TimerLastType type) {
		TimerLast newTimer = new TimerLast(start, last, type);
		newTimer.registTimeOver(new RoleBuildQueueFinish());
		TimeQueue timeQueue = new TimeQueue();
		timeQueue.setBuild(0);
		timeQueue.setTimer(newTimer);
		buildQueue.add(timeQueue);
	}

	public void addNewResearchQueue(long start, long last, TimerLastType type) {
		TimerLast newTimer = new TimerLast(start, last, type);
		TimeQueue timeQueue = new TimeQueue();
		timeQueue.setTimer(newTimer);
		researchQueue.add(timeQueue);
	}

	// 单支出征部队最大数量
	public int getMaxOutBattleAllNum() {
		return (int) (getMaxOutBattleBaseNum() * (int) (cityAttr.getAddSoldLimit() * 1000) / 1000)
				+ getMaxOutBattleBaseNum();
	}

	// 出征部队的基础量
	public int getMaxOutBattleBaseNum() {
		int maxGoOutNum = GameConfig.EXPEDITE_SOLDIER_NUM;
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			String buildId = build.getBuildId();
			if (buildId.equals(BuildName.COMMAND_POST.getKey())) {
				Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(buildId, build.getLevel());
				if (buildLevel == null) {
					GameLog.error("cannnot get static data,getMaxOutBattleNum error, buildId=" + build.getBuildId());
					return 0;
				}
				List<String> params = buildLevel.getParamList();
				maxGoOutNum += Integer.parseInt(params.get(0));
			}
		}
		return maxGoOutNum;
	}

	// 一次训练士兵的最大数量
	public int getMaxTrainNum(BuildName name) {
		int maxTrainNum = 0;
		if (BuildName.MILITARY_FACT == name) {
			List<RoleBuild> buildsCamp = this.searchBuildByBuildId(name.getKey());
			if (buildsCamp.size() == 0) {
				return maxTrainNum;
			}
			RoleBuild buildCamp = buildsCamp.get(0);
			Buildinglevel buildCampLevel = RoleBuild.getBuildinglevelByCondition(name.getKey(), buildCamp.getLevel());
			if (buildCampLevel == null) {
				return maxTrainNum;
			}
			List<String> campParams = buildCampLevel.getParamList();
			if (campParams.size() == 0) {
				return maxTrainNum;
			}
			maxTrainNum = Integer.parseInt(campParams.get(0));
			return maxTrainNum;// (int) (maxTrainNum +
								// maxTrainNum*cityAttr.getAddSProdLimit());
		}
		maxTrainNum = GameConfig.BASE_TRAIN_SPACE;
		// 军校加成
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			if (build.getBuildId().equals(BuildName.MILITARY_SCHOOL.getKey())) {
				Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
				if (buildLevel == null) {
					GameLog.error("cannnot get static data,getMaxTrainNum error,buildId=" + build.getBuildId());
					return 0;
				}
				List<String> params = buildLevel.getParamList();
				maxTrainNum += Integer.parseInt(params.get(0));
			}
		}
		switch (name) {
		case AIR_COM:
			maxTrainNum += cityAttr.getAddSProdLimit_4();
			break;
		case ARMORED_FACT:
			maxTrainNum += cityAttr.getAddSProdLimit_3();
			break;
		case WAR_FACT:
			maxTrainNum += cityAttr.getAddSProdLimit_2();
			break;
		case SOLDIERS_CAMP:
			maxTrainNum += cityAttr.getAddSProdLimit_1();
			break;
		default:
			break;
		}
		return maxTrainNum;
	}

	/**
	 * 获取可以被掠夺的最大资源数量
	 * 
	 * @param type
	 * @return
	 */
	public Map<String, Integer> getMaxLootResource() {
		Map<String, Integer> resLootMap = new HashMap<String, Integer>();
		MapCity mc = mapWorld.searchMapCity(uid, id);
		if (mc == null) {
			return resLootMap;
		}
		// 保护状态下不能抢夺资源
		if (mc.getCityState().isResprotect()) {
			return resLootMap;
		}
		List<RoleBuild> builds = this.searchBuildByBuildId(BuildName.LOGISTICS_CENTER.getKey());
		if (builds.size() > 0) {
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(),
					builds.get(0).getLevel());
			List<String> strParam = buildLevel.getParamList();
			if (buildLevel != null) {
				int start = ResourceTypeConst.RESOURCE_TYPE_FOOD.ordinal();
				int end = ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal();
				for (int i = start, j = 0; i <= end; i++, j++) {
					ResourceTypeConst rt = ResourceTypeConst.search(i);
					if (strParam.size() > j) {
						int resProtectNum = (int) (Integer.parseInt(strParam.get(j)) * (1 + cityAttr.getImpProtect()));
						long have = getResource(rt);
						int num = (int) Math.max(0, have - resProtectNum);
						resLootMap.put(rt.getKey(), (int) num);
						GameLog.info("[MapCity]uid=" + uid + ",mcid=" + id + ",resProtectNum=" + resProtectNum
								+ "|掠夺num=" + num);
					} else {
						resLootMap.put(rt.getKey(), 0);
					}
				}
			}
		}
		return resLootMap;
	}

	// 可以被掠夺的最大资源数量
	public long getMaxLootResource(ResourceTypeConst type) {
		long maxResource = 0;
		List<RoleBuild> builds = this.searchBuildByBuildId(BuildName.LOGISTICS_CENTER.getKey());
		if (builds.size() > 0) {
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(),
					builds.get(0).getLevel());
			List<String> strParam = buildLevel.getParamList();
			if (buildLevel != null && strParam.size() == 4) {
				switch (type) {
				case RESOURCE_TYPE_FOOD:
					maxResource = Long.valueOf(strParam.get(0));
					break;
				case RESOURCE_TYPE_METAL:
					maxResource = Long.valueOf(strParam.get(1));
					break;
				case RESOURCE_TYPE_OIL:
					maxResource = Long.valueOf(strParam.get(2));
					break;
				case RESOURCE_TYPE_ALLOY:
					maxResource = Long.valueOf(strParam.get(3));
					break;
				default:
					break;
				}
			}
		}
		// add buff
		if (maxResource > 0) {
			long resProtectNum = (long) (maxResource * (1 + cityAttr
					.getImpProtect()/* + cityAttr.getAddStorageLimit() */));
			return (getResource(type) - resProtectNum) > 0 ? (getResource(type) - resProtectNum) : 0;
		}
		return maxResource;
	}

	/**
	 * 获取医院或维修厂的最大存伤兵上限
	 * 
	 * @param type
	 * @return
	 */
	public int getRepairerHospital(String buildId) {
		int value = 0;
		List<RoleBuild> builds = searchBuildByBuildId(buildId);
		for (RoleBuild build : builds) {
			if (build.getState() == RoleBuildState.COND_DELETED.getKey()) {
				continue;
			}
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
			if (buildLevel == null) {
				GameLog.error("cann't get LogisticsCenter buff where buildId=" + build.getId());
				return 0;
			}
			List<String> paramLst = buildLevel.getParamList();
			if (paramLst.size() < 2) {
				GameLog.error("cann't get logistics buff params error where params=" + paramLst.size());
				return 0;
			}
			value += Integer.parseInt(paramLst.get(1));
		}
		// add buff
		if (buildId.equals(BuildName.HOSPITAL.getKey()))
			value += cityAttr.getAddHospCapa();
		else if (buildId.equals(BuildName.REPAIRER.getKey()))
			value += cityAttr.getAddRepaCapa();
		return value;
	}

	/***
	 * 围墙现有的陷阱数量
	 * 
	 * @return
	 */
	public int getFenceCurTrip() {
		int count = 0;
		List<ArmyInfo> armys = armyAgent.getAllCityArmy();
		if (armys == null) {
			return count;
		}
		for (int i = 0; i < armys.size(); i++) {
			ArmyInfo army = armys.get(i);
			if (army.getArmyBase().getArmyType() == ArmyType.HOOK.ordinal()
					&& army.getState() == ArmyState.ARMY_IN_NORMAL.ordinal()) {
				count += army.getArmyNum() * army.getArmyBase().getSpace();
			}
		}
		return count;
	}

	/**
	 * 围墙陷阱容量
	 * 
	 * @return
	 */
	public int getFenceMaxTrip() {
		int value = 0;
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.FENCE.getKey());// 围墙
		if (builds.size() == 0 || builds.size() > 1) {
			return value;
		}
		RoleBuild build = builds.get(0);
		int level = build.getLevel();
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), level);
		if (buildLevel == null) {
			GameLog.error("cann't get build level buff where buildId=" + build.getBuildId());
			return value;
		}
		List<String> paramLst = buildLevel.getParamList();
		if (paramLst.size() < 2) {
			GameLog.error("cann't get build level build buff where buildId=" + build.getBuildId());
			return value;
		}
		value = Integer.parseInt(paramLst.get(0));
		// add buff
		value += cityAttr.getAddFenceSpace();
		return value;
	}

	/**
	 * 粮食消耗计算 离线粮食消耗和在线消耗 resSyncTime
	 * 
	 * @param isOnline
	 */
	public void grainConsumption(Role role, boolean isOnline) {
		if (resSyncTime > 0) {
			ResourceTypeConst type = ResourceTypeConst.RESOURCE_TYPE_FOOD;
			long timer = TimeUtils.nowLong() - resSyncTime;
			int n = (int) (timer / Const.ARMY_COST_FRESH_TIME); // 需要扣几次粮食
			if (n <= 0) {
				return;
			}
			long grain = resources.get(type).longValue(); // 获取到当前角色粮食总量
			List<ArmyInfo> armyLst = armyAgent.getCityArmy();// 部队List
			long num = (long) Math.ceil(getCostGrain(armyLst) / 6);
			long deducGrain = num * n;
			if (deducGrain != 0) { // 部队需要消耗粮食才下发
				RespModuleSet rms = new RespModuleSet();
				if (grain == 0) {
					lessFood = deducGrain - grain;
					role.getEffectAgent().removeTechBuff(role, buffName);
					role.getEffectAgent().addTechBuff(role, "315", "0.2", buffName);
					AbstractClientModule module = new AbstractClientModule() {
						@Override
						public short getModuleType() {
							return NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD;
						}
					};
					module.add(1);// 1表示不正常,2表示恢复正常
					rms.addModule(module);
					MessageSendUtil.tipModule(rms, MessageSendUtil.TIP_TYPE_NORMAL,
							I18nGreeting.MSG_FOOD_SHORTAGE_FORCE_ARMY);
				} else if (grain - deducGrain < 0) {
					lessFood = deducGrain - grain;
					role.getEffectAgent().removeTechBuff(role, buffName);
					role.getEffectAgent().addTechBuff(role, "315", "0.2", buffName);
					MessageSendUtil.tipModule(rms, MessageSendUtil.TIP_TYPE_NORMAL,
							I18nGreeting.MSG_FOOD_SHORTAGE_FORCE_ARMY);
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MAINTAIN_UNIT_CONSUM, grain);
					AbstractClientModule module = new AbstractClientModule() {
						@Override
						public short getModuleType() {
							return NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD;
						}
					};
					module.add(1);// 1表示不正常,2表示恢复正常
					rms.addModule(module);
					String item = type.getKey();
					if (isOnline) {
						LogManager.itemConsumeLog(role, grain, EventName.grainConsumption.getName(), item);
					} else {
						LogManager.itemConsumeLog(role, grain, EventName.getOutlineConsumption.getName(), item);
					}
					try {
						NewLogManager.baseEventLog(role, "arms_consume", item, grain);
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
					role.redResourcesFromCity(rms, id, type, grain); // 下发并改变粮食总量
				} else {
					lessFood = 0;
					AbstractClientModule module = new AbstractClientModule() {
						@Override
						public short getModuleType() {
							return NTC_DTCD_FIGHT_REDUCE_BY_NO_FOOD;
						}
					};
					module.add(2);// 1表示不正常,2表示恢复正常
					rms.addModule(module);
					role.getEffectAgent().removeTechBuff(role, buffName);
					String item = type.getKey();
					if (isOnline) {
						LogManager.itemConsumeLog(role, (long) deducGrain, EventName.grainConsumption.getName(), item);
					} else {
						LogManager.itemConsumeLog(role, (long) deducGrain, EventName.getOutlineConsumption.getName(),
								item);
					}
					try {
						NewLogManager.baseEventLog(role, "arms_consume", item, grain);
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
					role.redResourcesFromCity(rms, id, type, deducGrain);// 下发并改变粮食总量
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MAINTAIN_UNIT_CONSUM,
							deducGrain);
				}
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
		resSyncTime = TimeUtils.nowLong();
	}

	public static final String buffName = "SpecialEffect";

	/**
	 * 粮食消耗计算
	 */
	public double getCostGrain(List<ArmyInfo> armyLst) {
		double costGrain = 0;
		if (armyLst != null) {
			// 遍历List，得到粮食消耗总量
			for (ArmyInfo armyInfo : armyLst) {
				Army armyBase = dataManager.serach(Army.class, armyInfo.getArmyId());
				List<String> costLst = armyBase.getRiceCost();
				for (String cost : costLst) {
					String[] costArray = cost.split(":");
					long B = armyInfo.getArmyNum();
					float A = Float.parseFloat(costArray[1]);
					// add buff
					float buff = 0;
					Role role = world.getOnlineRole(uid);
					if (role != null) {
						buff = RoleArmyAttr.getEffValV2(role, TargetType.T_A_RED_SC, ExtendsType.EXTEND_ARMY,
								armyBase.getArmyType());
					}
					double num = A * B * (1 - buff);
					costGrain += num;
					if (buff > 1) {
						GameLog.error("Buff系数计算错误~");
					}
					GameLog.info("[getCostGrain]uid="+uid+"|armyType"+armyBase.getArmyType()+"|buff="+buff);
				}
			}
		}
		if (costGrain < 0) {
			GameLog.error("消耗计算错误，消耗粮食数值不能为负数~");
		}
		
		return costGrain;
	}

	private long changeResResult(byte rate, long sourceNum, ResourceTypeConst targetType) {
		long value = 0;
		switch (targetType) {
		case RESOURCE_TYPE_FOOD:
			value = (long) (sourceNum * rate);
			break;
		case RESOURCE_TYPE_METAL:
			value = (long) (sourceNum * rate);
			break;
		case RESOURCE_TYPE_OIL:
			value = (long) (sourceNum * rate / 4);
			break;
		case RESOURCE_TYPE_ALLOY:
			value = (long) (sourceNum * rate / 16);
			break;
		default:
			break;
		}
		return value;
	}

	/**
	 * 资源互换
	 * 
	 * @param role
	 * @param ress
	 * @return
	 */
	public boolean roleTradeCityResource(Role role, String type1, long num1, String type2, long num2) {
		List<RoleBuild> builds = this.searchBuildByBuildId(BuildName.TRADE_CENTER.getKey());
		if (builds.size() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		RoleBuild tradeBuild = builds.get(0);
		if (tradeBuild.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		ResourceTypeConst resType1 = ResourceTypeConst.search(type1);
		if (num1 > this.getResource(resType1)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		ResourceTypeConst resType2 = ResourceTypeConst.search(type2);
		long value2 = 0;
		switch (resType1) {
		case RESOURCE_TYPE_FOOD:
			value2 = changeResResult((byte) 1, num1, resType2);
			break;
		case RESOURCE_TYPE_METAL:
			value2 = changeResResult((byte) 1, num1, resType2);
			break;
		case RESOURCE_TYPE_OIL:
			value2 = changeResResult((byte) 4, num1, resType2);
			break;
		case RESOURCE_TYPE_ALLOY:
			value2 = changeResResult((byte) 16, num1, resType2);
			break;
		default:
			break;
		}
		if (Math.abs(value2 - num2) > 5) {
			num2 = value2;
		}
		// 扣税
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(tradeBuild.getBuildId(),
				tradeBuild.getLevel());
		if (buildLevel == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		List<String> paramList = buildLevel.getParamList();
		if (paramList.size() != 1) {
			GameLog.error("策划的buildinglevel表配错了！");
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		long cdTime = GameConfig.BUILD_TRAND_CD;
		long maxTradeNum = Long.parseLong(paramList.get(0));
		if (num1 > maxTradeNum) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_CHG_RES_ERROR);
			return false;
		}
		resources.put(resType1, getResource(resType1) - num1);
		resources.put(resType2, getResource(resType2) + num2);
		LogManager.itemConsumeLog(role, num1, EventName.roleTradeCityResource.getName(), resType1.getKey());
		LogManager.itemOutputLog(role, num2, EventName.roleTradeCityResource.getName(), resType2.getKey());
		try {
			NewLogManager.buildLog(role, "resource_exchange", resType1.getKey(), num1, resType2.getKey(), num2);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		RespModuleSet rms = new RespModuleSet();
		TimerLast timer = tradeBuild.addBuildTimer(cdTime, TimerLastType.TIME_CITY_TRADE_CD);
		timer.registTimeOver(new CityTradeCDFinish(tradeBuild));
		tradeBuild.sendToClient(rms);
		this.sendToClient(rms, false);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	/**
	 * 检查科技升级条件
	 * 
	 * @param limitions
	 * @return
	 */
	public boolean checkTechLimition(List<String> limitions) {
		boolean bSuc = false;
		for (String limition : limitions) {
			bSuc = false;
			String[] lims = limition.split(":");
			String sparam = lims[0];
			int iparam = Integer.parseInt(lims[1]);
			if (BuildName.search(sparam) != null) {
				List<RoleBuild> builds = searchBuildByBuildId(sparam);
				for (RoleBuild build : builds) {
					if (build.getLevel() >= iparam) {
						bSuc = true;
						break;
					}
				}
				if (!bSuc) {
					return bSuc;
				}
			} else if (sparam.equals("RoleLevel")) {
				Role role = world.getOnlineRole(uid);
				if (role != null && role.getLevel() >= iparam) {
					bSuc = true;
				} else {
					return false;
				}
			} else if (dataManager.serach(Tech.class, sparam) != null) {// 科技条件只要有一个达成就允许升级
				if (iparam <= techAgent.getTechLevel(sparam)) {
					return true;
				}
			}
		}
		return bSuc;
	}

	/**
	 * 下发城墙数据
	 * 
	 * @Title: setWallBuff
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @return void
	 */
	public void setWallBuff() {
		List<RoleBuild> builds = this.searchBuildByBuildId(BuildName.FENCE.getKey());
		if (builds == null || builds.size() == 0) {
			GameLog.error("uid=" + this.uid + "|城墙建筑找不到了。。。");
			return;
		}
		RoleBuild build = builds.get(0);
		BuildComponentWall com = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
		if (com != null) {
			com.updateWallHP();
		}
	}

	/**
	 * 下发客户端更新
	 * 
	 * @param e
	 * @param isRemove
	 * @param buildId
	 */
	public void updateProductionBuff(String buildId) {
		if (buildId != null) {
			List<RoleBuild> buildLst = searchBuildByBuildId(buildId);
			for (int j = 0; j < buildLst.size(); j++) {
				RoleBuild build = buildLst.get(j);
				BuildComponentProduction component = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
				if (component != null) {
					// component.updateResBuffRate(isRemove, e);
					component.updateResBuffRate();
				}
			}
		} else {
			for (int i = 0; i < builds.size(); i++) {
				RoleBuild build = builds.get(i);
				BuildComponentProduction component = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
				if (component != null) {
					// component.updateResBuffRate(isRemove, e);
					component.updateResBuffRate();
				}
			}
		}
	}

	/**
	 * 当前城池粮食消耗
	 * 
	 * @return
	 */
	public long getCityArmyConsume() {
		if (armyAgent == null) {
			return 0;
		}
		long curConsume = 0;
		List<ArmyInfo> curArmys = armyAgent.getCityArmy();
		curConsume = (long) Math.ceil(getCostGrain(curArmys));
		return curConsume;
	}

	private String changeResTypeToBuildName(ResourceTypeConst type) {
		BuildName name = null;
		switch (type) {
		case RESOURCE_TYPE_FOOD:
			name = BuildName.FOOD_FACT;
			break;
		case RESOURCE_TYPE_METAL:
			name = BuildName.SMELTER;
			break;
		case RESOURCE_TYPE_OIL:
			name = BuildName.REFINERY;
			break;
		case RESOURCE_TYPE_ALLOY:
			name = BuildName.TITANIUM_PLANT;
			break;
		default:
			break;
		}
		if (name == null) {
			return null;
		}
		return name.getKey();
	}

	public long getProductionNumBuff(ResourceTypeConst type) {
		long production = 0;
		List<RoleBuild> builds = this.searchBuildByBuildId(changeResTypeToBuildName(type));
		for (RoleBuild build : builds) {
			BuildComponentProduction com = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
			long num[] = com.getAllNumBuff();
			production += (num[0] + num[1] + num[2] + num[3] + num[4] + num[5]);
			break;
		}
		return production;
	}

	public float getProductionRateBuff(ResourceTypeConst type) {
		float rate = 0;
		List<RoleBuild> builds = this.searchBuildByBuildId(changeResTypeToBuildName(type));
		for (RoleBuild build : builds) {
			BuildComponentProduction com = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
			float[] num = com.getAllRateBuff();
			rate += (num[0] + num[1] + num[3] + num[4] + num[5]);// 不计算道具类
			break;
		}
		return rate;
	}

	/**
	 * 城池资源增益详情
	 * 
	 * @return
	 */
	public boolean getAllResDetails(Role role) {
		List<ResourceTypeConst> list = new ArrayList<ResourceTypeConst>(); // 用来记录已经遍历过的资源生产组件
		Map<ResourceTypeConst, Long[]> resMap = new HashMap<ResourceTypeConst, Long[]>();
		Long[] resBuff1 = new Long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		resMap.put(ResourceTypeConst.RESOURCE_TYPE_FOOD, resBuff1);
		Long[] resBuff2 = new Long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		resMap.put(ResourceTypeConst.RESOURCE_TYPE_METAL, resBuff2);
		Long[] resBuff3 = new Long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		resMap.put(ResourceTypeConst.RESOURCE_TYPE_OIL, resBuff3);
		Long[] resBuff4 = new Long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
		resMap.put(ResourceTypeConst.RESOURCE_TYPE_ALLOY, resBuff4);
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			BuildName name = BuildName.search(build.getBuildId());
			if (name == null) {
				GameLog.error("cannot find build by buildId = " + build.getBuildId());
				return false;
			}
			BuildComponentProduction com = build.getComponent(BuildComponentType.BUILD_COMPONENT_PRODUCTION);
			ResourceTypeConst type = null;
			boolean bSuc = true;
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
				bSuc = false;
				break;
			}
			if (bSuc) {
				Long[] resBuffDet = resMap.get(type);
				if (resBuffDet == null) {
					resBuffDet = new Long[] { 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
				}
				if (com.getSpecialTimer() == null) {
					resBuffDet[0] += com.calcResourceNum();//当前产出
					resBuffDet[1] += com.getBaseOutput(); //基础产出
					resBuffDet[2] += com.getItemBuffOutput() + com.getTechBuffOutput() + com.getSkillBuffOutput()
							+ com.getVipBuffOutput() + com.getEquipBuffOutput() + com.getUnionCitysBuffOutput() ; // 总资源加成
					resBuffDet[4] += com.getEquipBuffOutput(); // 装备加成
					if (!list.contains(type)) {
						list.add(type);
						resBuffDet[3] += com.getTechBuffOutput(); // 科技加成
						resBuffDet[5] += com.getSkillBuffOutput(); // 技能加成
						resBuffDet[6] += com.getVipBuffOutput(); // vip加成
						resBuffDet[7] += com.getUnionCitysBuffOutput(); //联盟城市加成
					}
				} else {
					resBuffDet[0] += com.calcResourceNum();
					resBuffDet[1] += com.getBaseOutput();
					resBuffDet[2] += com.getItemBuffOutput() + com.getTechBuffOutput() + com.getSkillBuffOutput()
							+ com.getVipBuffOutput() + com.getEquipBuffOutput() + com.getUnionCitysBuffOutput();
					resBuffDet[4] += com.getEquipBuffOutput();
					if (!list.contains(type)) {
						list.add(type);
						resBuffDet[3] += com.getTechBuffOutput() / 2;
						resBuffDet[5] += com.getSkillBuffOutput() / 2;
						resBuffDet[6] += com.getVipBuffOutput() / 2;
						resBuffDet[7] += com.getUnionCitysBuffOutput() / 2;
					}
				}
				resMap.put(type, resBuffDet);
			}
		}
		if (resMap.size() == 0) {
			return false;
		}
		RespModuleSet rms = new RespModuleSet();
		sendAllResourceInfoToClient(rms, resMap);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	public void sendAllResourceInfoToClient(RespModuleSet rms, Map<ResourceTypeConst, Long[]> resMap) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_CITY_RESBUFF;
			}
		};
		module.add(getCityArmyConsume());// long 粮食消耗
		module.add(resMap.size());// int 循环大小
		for (Map.Entry<ResourceTypeConst, Long[]> entry : resMap.entrySet()) {
			String key = entry.getKey().getKey();
			module.add(key); // String 资源类型
			module.add(entry.getValue()[0]);// long 总数量
			module.add(entry.getValue()[1]);// long 基础产量
			module.add(entry.getValue()[2]);// long buff总产量
			module.add(entry.getValue()[3]);// long 科技产量
			module.add(entry.getValue()[4]);// long 物品产量
			module.add(entry.getValue()[5]);// long 技能产量
			module.add(entry.getValue()[6]);// long vip产量
		}
		rms.addModule(module);
	}

	public int getRadarLevel() {
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.RADAR.getKey());
		return builds.get(0).getLevel();
	}

	public int getBuildsNumByLevel(int level) {
		int num = 0;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getLevel() >= level) {
				num++;
			}
		}
		return num;
	}

	public void addLand(String landId) {
		landIds.add(landId);
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng;
	}

	/**
	 * 获取建筑的最大等级
	 * 
	 * @param key
	 * @return
	 */
	public int getBuildMaxLevel(String key) {
		List<RoleBuild> builds = searchBuildByBuildId(key);
		if (builds == null || builds.size() < 1) {
			return 0;
		}
		byte max = 1;
		for (int i = 0; i < builds.size(); i++) {
			RoleBuild build = builds.get(i);
			if (build.getLevel() > max) {
				max = build.getLevel();
			}
		}
		return max;
	}

	/**
	 * 获取城墙当前最大生命值
	 */
	public int getWallMaxValue() {
		int value = 0;
		List<RoleBuild> builds = searchBuildByBuildId(BuildName.FENCE.getKey());
		for (RoleBuild build : builds) {
			BuildComponentWall wall = build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
			if (wall != null) {
				value += wall.getDefenseMaxValue();
			}
		}
		return value;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + uid;
	}

	/**
	 * 获取建筑提供的基础buff值 rate ..PS:医院维修厂之类的所有的等级提供的是一样的 , 没有取最高等级
	 * 
	 * @param key
	 * @return
	 */
	public float getRoleBuildBaseBuffValue(String key) {
		float base = 0.0F;
		List<RoleBuild> hBuilds = searchBuildByBuildId(key);
		if (hBuilds.size() > 0) {
			Buildinglevel hb = hBuilds.get(0).getBuildingLevel();
			if (hb != null) {
				base = Float.valueOf(hb.getParamList().get(0));
			}
		}
		return base;
	}
}
