package com.joymeng.slg.domain.object.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.chat.ChatAgent;
import com.joymeng.slg.domain.code.CodeManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.event.impl.EffectEvent;
import com.joymeng.slg.domain.event.impl.HonorEvent;
import com.joymeng.slg.domain.event.impl.RemoveRoleEvent;
import com.joymeng.slg.domain.event.impl.RoleBagEvent;
import com.joymeng.slg.domain.event.impl.RoleBuildEvent;
import com.joymeng.slg.domain.event.impl.RoleEvent;
import com.joymeng.slg.domain.event.impl.RoleHeartEvent;
import com.joymeng.slg.domain.event.impl.RoleMapEvent;
import com.joymeng.slg.domain.event.impl.RoleRankEvent;
import com.joymeng.slg.domain.event.impl.TaskEvent;
import com.joymeng.slg.domain.event.impl.UnionEvent;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.copy.Relic;
import com.joymeng.slg.domain.map.impl.still.copy.RoleRelic;
import com.joymeng.slg.domain.map.impl.still.copy.Scene;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruins;
import com.joymeng.slg.domain.map.impl.still.res.EffectListener;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.market.RoleBlackMarketAgent;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.object.army.data.ArmyBriefInfo;
import com.joymeng.slg.domain.object.army.data.ArmyGroup;
import com.joymeng.slg.domain.object.armyPoints.data.Soldierstt;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.daily.OnlineAgent;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.effect.EffectAgent;
import com.joymeng.slg.domain.object.rank.RoleRank;
import com.joymeng.slg.domain.object.redpacket.RoleRedpacket;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.data.Heroicon;
import com.joymeng.slg.domain.object.role.data.Userlevel;
import com.joymeng.slg.domain.object.role.imp.RoleStaticData;
import com.joymeng.slg.domain.object.role.imp.RoleStatisticInfo;
import com.joymeng.slg.domain.object.role.signin.RoleSevenSignIn;
import com.joymeng.slg.domain.object.role.signin.RoleThirtySignIn;
import com.joymeng.slg.domain.object.skill.RoleSkillAgent;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.DailyTaskAgent;
import com.joymeng.slg.domain.object.task.HonorMissionAgent;
import com.joymeng.slg.domain.object.task.MissionManager;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.shop.RoleShopAgent;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionHelper;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.GameConfig;
import com.joymeng.slg.world.thread.OnlineRunnable;

public class Role extends AbstractObject implements Instances{
	
	UserInfo userInfo;//通讯对象
	
	long joy_id;//主键编号
	
	String name;//姓名
	
	int countryId;//隶属国家编号
	
	byte sex;//性别 0男性，1女性
	
	byte level = 1;//等级
	
	long exp;//升级经验
	
	RoleIcon icon = new RoleIcon();//头像对象
	
	int money;//金币
	
	int krypton;//氪晶
		
	int gem;//宝石
	
	int copper=0;//铜币
	
	int silver=0;//银币
	
	long unionId;//联盟id
	
	TimerLast joinTimer = null;//加入联盟所需的倒计时
	
	List<RoleCityAgent> cityAgents = new ArrayList<RoleCityAgent>();//拥有的建筑群
	
	long lastSaveTime;//上一次保存时间

	RoleStamina stamina = new RoleStamina();
	
	long heartTime;//上传心跳时间
	
	boolean isInMap = false;//是否在大地图
	
	List<PositionInfo> posFavorites = new ArrayList<PositionInfo>();//收藏的坐标

	int skillPoints = 0;//技能点数
	
	List<String> guideIdList = new ArrayList<String>(); //已使用的引导的Id列表
	
	RoleBagAgent bagAgent = new RoleBagAgent();
	
	VipInfo vipInfo = new VipInfo();//vip信息管理
	
	RoleSkillAgent skillAgent = new RoleSkillAgent();
	
	long lastJoin = 0;//最近一次加入联盟的时间
	
	EffectAgent effectAgent = new EffectAgent();//buff池
	
	long freeTime;//建筑建造或升级时的免费加速时间
	
	ChatAgent chatAgent = new ChatAgent(joy_id);
	
	String taskState;//任务领取的进度任务信息
	
	MissionManager taskAgent = new MissionManager();

	RoleSevenSignIn sevenSignIn = new RoleSevenSignIn(); // 七日签到

	RoleThirtySignIn thirtySignIn = new RoleThirtySignIn(); // 30日签到

	OnlineAgent onlineAgent = new OnlineAgent();
	
//	RoleArmyAttr armyAttr = new RoleArmyAttr(); //军队相关的buff
	
	HonorMissionAgent honorAgent = new HonorMissionAgent();//数据库荣誉榜
	
	RoleStatisticInfo statictisInfo = new RoleStatisticInfo();
	
	long lastLoginTime;//上次登陆的时间
	
	String lastLoginIp;//上次登陆的ip地址
	
	String uidRegisTime;//玩家uid注册时间
	
	String uuidRegisTime; // 玩家uuid注册时间
	
	String uuid;//设备唯一uuid
	
	String channelId = "0000000";//渠道编号
	
	String country;// 国家

	String language;// 语言
	
	String model; //机型
	
	String version; //版本
	
	String resolution; //分辨率
	
	int memory; //内存
	
	String  registrationId; //友盟推送注册号

	String openId; // 微信号   binding 是否绑定的标识   0-未绑定 (微信号为空)  1-绑定

	int signIn = 0;// 是否签到   0-为签到  1-签到
	
	Map<Integer, ArmyGroup> armyGroups = new HashMap<Integer, ArmyGroup>();	//军队分组信息
	
	TurntableBody turntableBody = new TurntableBody(joy_id); // 大转盘
	
	boolean tickFlag = false;
	
	RoleShopAgent shopAgent = new RoleShopAgent();
	
	List<Long> blacklist = new ArrayList<>(); // 黑名单
	
	RoleSetting roleSetting = new RoleSetting();//用户设置
	
	DailyTaskAgent dailyTaskAgent = new DailyTaskAgent();//每日任务
	
	RoleBlackMarketAgent blackMarketAgent = new RoleBlackMarketAgent();//玩家黑市
	
	RoleAntiAddiction roleAnti = new RoleAntiAddiction();//防沉迷系统
	
	Map<Integer, RoleRelic> roleCopys = new HashMap<Integer, RoleRelic>();//用户副本进度
	
	RoleRedpacket roleRedpackets = new RoleRedpacket(joy_id);
	
	CodeManager code  = new CodeManager();//兑换码

	int gmFortressCreateTime = 0;//大地图建造建筑调试时间
	int gmFortressLevelUpTime = 0;//大地图建筑升级调试时间
	int gmFortressDropTime = 0;//大地图建筑放弃调试时间
	int gmCityMoveTime = 0;//大地图迁城点调试时间
	
	int chargeSuccessNum = 0; // 充值成功的次数
	
	MapEctype nearestEctype;
	public MapEctype getNearestEctype() {
		return nearestEctype;
	}
	
	public void setNearestEctype() {
		MapEctype ectype = mapWorld.getNearestEctype(this);
		nearestEctype = ectype;
	}
	
	public long getJoy_id() {
		return joy_id;
	}


	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public int getSignIn() {
		return signIn;
	}

	public void setSignIn(int signIn) {
		this.signIn = signIn;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public int getMemory() {
		return memory;
	}

	public void setMemory(int memory) {
		this.memory = memory;
	}

	public String getUidRegisTime() {
		return uidRegisTime;
	}

	public void setUidRegisTime(String uidRegisTime) {
		this.uidRegisTime = uidRegisTime;
	}

	public String getUuidRegisTime() {
		return uuidRegisTime;
	}

	public void setUuidRegisTime(String uuidRegisTime) {
		this.uuidRegisTime = uuidRegisTime;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public TimerLast getJoinTimer() {
		return joinTimer;
	}

	public void setJoinTimer(TimerLast joinTimer) {
		this.joinTimer = joinTimer;
	}

	public Map<Integer, RoleRelic> getRoleCopys() {
		return roleCopys;
	}

	public void setRoleCopys(Map<Integer, RoleRelic> roleCopys) {
		this.roleCopys = roleCopys;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Map<Integer, ArmyGroup> getArmyGroups() {
		return armyGroups;
	}

	public List<Long> getBlacklist() {
		return blacklist;
	}

	public void setBlacklist(List<Long> blacklist) {
		this.blacklist = blacklist;
	}

	public TurntableBody getTurntableBody() {
		return turntableBody;
	}

	public void setTurntableBody(TurntableBody turntableBody) {
		this.turntableBody = turntableBody;
	}

	public RoleRedpacket getRoleRedpackets() {
		return roleRedpackets;
	}

	public void setRoleRedpackets(RoleRedpacket roleRedpackets) {
		this.roleRedpackets = roleRedpackets;
	}

	public void setArmyGroups(Map<Integer, ArmyGroup> armyGroups) {
		this.armyGroups = armyGroups;
	}

	public HonorMissionAgent getHonorAgent() {
		return honorAgent;
	}

	public boolean addRoleToBlacklist(long otherUid) {
		return blacklist.add(otherUid);
	}

	public RoleAntiAddiction getRoleAnti() {
		return roleAnti;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getChannelId() {
		return channelId;
	}

	public int getGmFortressCreateTime() {
		return gmFortressCreateTime;
	}

	public void setGmFortressCreateTime(int gmFortressCreateTime) {
		this.gmFortressCreateTime = gmFortressCreateTime;
	}

	public int getGmFortressLevelUpTime() {
		return gmFortressLevelUpTime;
	}

	public void setGmFortressLevelUpTime(int gmFortressLevelUpTime) {
		this.gmFortressLevelUpTime = gmFortressLevelUpTime;
	}

	public int getGmFortressDropTime() {
		return gmFortressDropTime;
	}

	public void setGmFortressDropTime(int gmFortressDropTime) {
		this.gmFortressDropTime = gmFortressDropTime;
	}
	
	public int getGmCityMoveTime() {
		return gmCityMoveTime;
	}

	public void setGmCityMoveTime(int gmCityMoveTime) {
		this.gmCityMoveTime = gmCityMoveTime;
	}

	public boolean removeRoleFromBlacklist(long otherUid) {
		if (blacklist.size() < 1) {
			return false;
		}
		return blacklist.remove(otherUid);
	}
	
	public Role(){
		lastSaveTime = TimeUtils.nowLong();
		freeTime     = Const.FIVE_MINUTE;
	}

	public ChatAgent getChatAgent() {
		return chatAgent;
	}

	public int getKrypton() {
		return krypton;
	}
	public void setKrypton(int krypton) {
		this.krypton = krypton;
	}
	public int getGem() {
		return gem;
	}
	public void setGem(int gem) {
		this.gem = gem;
	}
	public int getCopper() {
		return copper;
	}
	public void setCopper(int copper) {
		this.copper = copper;
	}
	public int getSilver() {
		return silver;
	}
	public void setSilver(int silver) {
		this.silver = silver;
	}

	public RoleSevenSignIn getSevenSignIn() {
		return sevenSignIn;
	}

	public void setSevenSignIn(RoleSevenSignIn sevenSignIn) {
		this.sevenSignIn = sevenSignIn;
	}

	public RoleThirtySignIn getThirtySignIn() {
		return thirtySignIn;
	}

	public void setThirtySignIn(RoleThirtySignIn thirtySignIn) {
		this.thirtySignIn = thirtySignIn;
	}

	public void setChatAgent(ChatAgent chatAgent) {
		this.chatAgent = chatAgent;
	}

	public List<String> getGuideIdList() {
		return guideIdList;
	}

	public void setGuideIdList(List<String> guideIdList) {
		this.guideIdList = guideIdList;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public RoleSetting getRoleSetting() {
		return roleSetting;
	}

	public void setRoleSetting(RoleSetting roleSetting) {
		this.roleSetting = roleSetting;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		tickFlag = false;
	}

	public RoleShopAgent getShopAgent() {
		return shopAgent;
	}

	public void setShopAgent(RoleShopAgent shopAgent) {
		this.shopAgent = shopAgent;
	}
	
	public DailyTaskAgent getDailyTaskAgent() {
		return dailyTaskAgent;
	}

	public void setDailyTaskAgent(DailyTaskAgent dailyTaskAgent) {
		this.dailyTaskAgent = dailyTaskAgent;
	}

	public RoleBlackMarketAgent getBlackMarketAgent() {
		return blackMarketAgent;
	}
	
	public CodeManager getCode() {
		return code;
	}

	public void setCode(CodeManager code) {
		this.code = code;
	}

	@Override
	public long getId() {
		return joy_id;
	}

	public void setId(long joy_id) {
		this.joy_id = joy_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public byte getSex() {
		return sex;
	}

	public void setSex(byte sex) {
		this.sex = sex;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public RoleStamina getRoleStamina() {
		return stamina;
	}

	public int getMoney() {
		return money;
	}

	public RoleIcon getIcon() {
		return icon;
	}

	public void setIcon(RoleIcon icon) {
		this.icon = icon;
	}

	public List<RoleCityAgent> getCityAgents() {
		return cityAgents;
	}

	public int getSkillPoints(){
		return skillPoints;
	}
	
	public void setSkillPoints(int value){
		skillPoints = value;
	}
	
	public void addSkillPoints(int value){
		skillPoints += value;
		skillPoints = Math.max(0,skillPoints);
	}
	
	public void useSkillPoints(int value){
		skillPoints -= value;
	}
	
	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public VipInfo getVipInfo() {
		return vipInfo;
	}
	
	public RoleBagAgent getBagAgent() {
		return bagAgent;
	}
	
	public RoleSkillAgent getSkillAgent(){
		return skillAgent;
	}
	
	public EffectAgent getEffectAgent(){
		return effectAgent;
	}
		
	public MissionManager getTaskAgent(){
		return taskAgent;
	}
	
	public OnlineAgent getDailyAgent(){
		return onlineAgent;
	}
	
	public RoleStatisticInfo getRoleStatisticInfo(){
		return statictisInfo;
	}
	public long getLastJoin() {
		return lastJoin;
	}

	public void setLastJoin(long lastJoin) {
		this.lastJoin = lastJoin;
	}
	
	public long getFreeTime(){
		List<Effect> effets = effectAgent.searchBuffByTargetType(TargetType.G_C_REDU_BT);
		int num = 0;
		for(Effect ef:effets){
			num += ef.getNum();
		}
		//GameLog.info("[getFreeTime]uid="+joy_id+"|freeTime="+freeTime+"|buff="+num);
		return freeTime + num;
	}

	public void heartBeat() {
		heartTime = TimeUtils.nowLong();
	}

	public long getUnionId() {
		return unionId;
	}
	
	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public boolean isInMap() {
		return isInMap;
	}

	public void setInMap(boolean isInMap) {
		this.isInMap = isInMap;
	}

	public List<PositionInfo> getPosFavorites() {
		return posFavorites;
	}

	public List<RoleBuild> getBuildsByBuildId(int index,String buildKey){
		RoleCityAgent agent = cityAgents.get(index);
		if (agent != null){
			return agent.searchBuildByBuildId(buildKey);
		}
		return null;
	}
	
	public RoleCityAgent getCity(int index){
		for (int i = 0 ; i < cityAgents.size() ; i++){
			RoleCityAgent agent = cityAgents.get(i);
			if (agent.getId() == index){
				return agent;
			}
		}
		return null;
	}
	
	public void addCity(RoleCityAgent buildAgent){
		for (int i = 0 ; i < cityAgents.size() ; i++){
			RoleCityAgent agent = cityAgents.get(i);
			if (agent.getId() == buildAgent.getId()){
				return;
			}
		}
		cityAgents.add(buildAgent);
	}
	
	public boolean addResourcesToCity(boolean send , RespModuleSet rms ,int cityId,Object... resources){
		RoleCityAgent city = getCity(cityId);
		if (city != null){
			if (resources == null || resources.length % 2 != 0){
				GameLog.error(" resources length must be divisible by 2 ");
				return false;
			}
			Object[] result = new Object[resources.length];
			for (int i = 0; i < resources.length; i += 2) {
				ResourceTypeConst type = (ResourceTypeConst) resources[i];
				result[i] = type;
				long value = ((Number) resources[i + 1]).longValue();
				if (value < 0) {
					GameLog.error(" add resource value must > 0");
					return false;
				}
				long first = city.getResource(type);
				city.addResource(type, value,this);
				long last = city.getResource(type);
				result[i + 1] = last - first;
			}
			if (send) {
				sendResourceToClient(rms,cityId,result);
			}
			return true;
		}else{
			GameLog.error("Can't find city where id is " + cityId);
		}
		return false;
	}
	
	/**
	 * 给cityId的城市加资源,档rms不为null时最好放在前面所以模块都加完了最后掉，他会自动下发整个set
	 * @param rms
	 * @param cityId
	 * @param resources
	 * @return
	 */
	public boolean addResourcesToCity(RespModuleSet rms ,int cityId,Object... resources){
		return addResourcesToCity(true,rms,cityId,resources);
	}
	
	/**
	 * 给cityId的城市加资源,档rms不为null时最好放在前面所以模块都加完了最后掉，他会自动下发整个set
	 * @param cityId
	 * @param resources
	 * @return
	 */
	public boolean addResourcesToCity(int cityId,Object... resources){
		return addResourcesToCity(null,cityId,resources);
	}
	
	/**
	 * 给cityId的城市扣资源,档rms不为null时最好放在前面所以模块都加完了最后掉，他会自动下发整个set
	 * @param rms
	 * @param cityId
	 * @param resources
	 * @return
	 */
	public boolean redResourcesFromCity(RespModuleSet rms ,int cityId,Object... resources){
		RoleCityAgent city = getCity(cityId);
		if (city != null) {
			if (resources == null || resources.length % 2 != 0) {
				GameLog.error(" resources length must be divisible by 2 ");
				return false;
			}
			ResourceTypeConst type = null;
			long value = 0;
			List<Object> ress = new ArrayList<Object>();
			for (int i = 0; i < resources.length; i += 2) {
				type = (ResourceTypeConst) resources[i];
				value = ((Number) resources[i + 1]).longValue();
				if (value < 0) {
					GameLog.error(" add resource value must < 0");
					continue;
				}
				city.redResource(type, value);
				ress.add(type);
				ress.add(value*(-1));
				handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.USE_RESOURCE,type.getKey(),(int)value);
			}
			sendResourceToClient(rms,cityId,ress.toArray());
			return true;
		} else {
			GameLog.error("Can't find city where id is " + cityId);
		}
		return false;
	}
	
	/**
	 * 给cityId的城市扣资源,档rms不为null时最好放在前面所以模块都加完了最后掉，他会自动下发整个set
	 * @param cityId
	 * @param resources
	 * @return
	 */
	public boolean redResourcesFromCity(int cityId,Object... resources){
		return redResourcesFromCity(null,cityId,resources);
	}
	
	public void sendResourceToClient(boolean send,RespModuleSet rms,int cityId,Object... resources){
		if (resources == null || resources.length % 2 != 0){
			GameLog.error(" resources length must be divisible by 2 ");
			return;
		}
		if (rms == null){
			rms = new RespModuleSet();
		}
    	AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_RESSOURCE_CAHNGE;
			}
		};
		module.add(cityId);//此次是那个城市需要资源变更
		module.add(resources.length/2);//此次有几个资源需要变更
		for (int i =0 ; i < resources.length ; i += 2){
			ResourceTypeConst type = (ResourceTypeConst)resources[i];
			long value = Long.valueOf(String.valueOf(resources[i+1]));
			module.add(type.getKey());//string 资源变更编号
			module.add(value);//long 资源变更数量 >0加,<0扣
		}
		rms.addModule(module);
		if (send){
			MessageSendUtil.sendModule(rms,userInfo);
		}
	}
	

	public void sendResourceToClient(RespModuleSet rms,int cityId,Object... resources){
		sendResourceToClient(true,rms,cityId,resources);
	}
	
    public RespModuleSet sendToClient(int type){
    	RespModuleSet rms = new RespModuleSet();
    	if (type == 1 || type == 3){
    		sendRoleToClient(rms);
    		for (int i = 0 ; i < cityAgents.size() ; i++){
    			RoleCityAgent agent = cityAgents.get(i);
        		agent.sendToClient(rms,true);
        		agent.sendCityStateToClient(this, rms);//下发城池状态
        	}
    		sendRoleSetting(rms);//游戏设置
    		taskAgent.sendAllToClient(rms);
    	}
    	if (type == 2 || type == 3){
    		vipInfo.sendVipToClient(rms);//下发VIP消息
    		sendRoleBlacklist(rms);//下发用户黑名单
    		AbstractClientModule module = new AbstractClientModule(){
    			@Override
    			public short getModuleType() {
    				return NTC_DTCD_MAP_FAVORITE_POSITION;
    			}
    		};
    		module.add(posFavorites);
    		rms.addModule(module);
        	sevenSignIn.sendSignInDataToClient(this,rms);	//七天签到
        	thirtySignIn.sendSignInDataToClient(rms);	//30天签到
        	bagAgent.sendBagToClient(rms);
        	skillAgent.sendToClient(rms);
        	onlineAgent.sendToClient(rms);
        	dailyTaskAgent.sendMissionsToClient(rms);
        	sendArmyGroup(rms);//发送用户编队信息
        	turntableBody.sendTurntableToClient(rms);
        	stamina.sendToClient(rms);//体力模块
        	for (int i = 0; i < cityAgents.size(); i++) {
    			RoleCityAgent one = cityAgents.get(i);
    			one.sendCityBuffToClient(rms);
    		}
    	}
    	return rms;
    }

	private void sendRoleSetting(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_SETTING;
			}
		};
		module.add(roleSetting.isSoundeffect() ? 1 : 0);// 音效 boolen
		module.add(roleSetting.isMusic() ? 1 : 0); // 音乐 booblen
		module.add(roleSetting.getLanguage());// 语言 String
		int size = roleSetting.getMegNotice().size();
		module.add(size);
		for (int i = 0 ; i < size ; i++){
			String str = roleSetting.getMegNotice().get(i);
			module.add(str);
		}
		rms.addModule(module);
	}

	public void sendArmyGroup(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ARMY_GROUP;
			}
		};
		int size = armyGroups.size();
		module.add(size);		//分组个数 int
		if (size > 0) {
			for (int armyGroupId : armyGroups.keySet()) {
				ArmyGroup armyGroup = armyGroups.get(armyGroupId);
				module.add(armyGroup.getArmyGroupId().getArmyGroupId());	//分组的ID int
				module.add(armyGroup.getArmyGroupId().getCityId());			//分组的CityID int
				int aSize = armyGroup.getArmys().size();
				module.add(aSize);											//分组的军队数量 int
				for (ArmyBriefInfo armyBriefInfo : armyGroup.getArmys()) {
					module.add(armyBriefInfo.getArmyId());					//军队的ID String
					module.add(armyBriefInfo.getArmyNum());					//军队的Num int
					module.add(armyBriefInfo.getArmyPos());					//军队的位置
				}
			}
		}
		rms.addModule(module);
	}


	public void sendGameConfigToClient(RespModuleSet rms){
    	AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_GAME_CONFIG;
			}
		};
		module.add(GameConfig.UNION_NAME_MIN);	//联盟名字的最小值
		module.add(GameConfig.UNION_NAME_MAX);	//联盟名字的最大值
		module.add(GameConfig.UNION_SHORTNAME_LIMIT);//联盟缩写的限制字符
		module.add(GameConfig.UNION_CHANGE_FLAG_PRICE); //修改旗帜的价格
		module.add(GameConfig.UNION_TITLE_MIN);	//联盟称谓的最小值
		module.add(GameConfig.UNION_TITLE_MAX);//联盟称谓的最大值
		module.add(String.valueOf(GameConfig.MAP_SPEED_SLOW));//减速层减速因子
		module.add(String.valueOf(GameConfig.EXPEDITE_SPEED_EFFECT));//行军速度因子
		module.add(GameConfig.FORTRESS_LEVEL_MAX);//要塞的最高等级★
		module.add(GameConfig.EXPEDITE_SPY_COST);//侦查所需的金币(因子)★
		module.add(GameConfig.BUY_QUEUE_COST_MONEY);//购买队列消耗的金币int
		module.add(GameConfig.BUY_QUEUE_GET_TIME);//购买队列的持续时间(天数)int
		module.add(GameConfig.NOTICE_INVITE_PRICE);//公告邀请的价格int
		module.add(GameConfig.EXPEDITE_MONSTER_NEED_STAMINA);//去打怪需要体力
		module.add(GameConfig.EXPEDITE_ECTYPE_NEED_STAMINA);//去副本需要体力
		module.add(GameConfig.CHANGE_UNION_NAME_PRICE);//修改联盟名称价格
		module.add(GameConfig.CHANGE_UNION_SHORTNAME_PRICE);//修改联盟简称价格
		module.add(GameConfig.FORTRESS_NAME_MIN);//要塞名字的最小值
		module.add(GameConfig.FORTRESS_NAME_MAX);//要塞名字的最大值
		module.add(GameConfig.CHARGE_SHOP_TIP);//充值商店是否开启的提示
//		RoleStatisticInfo sInfo = getRoleStatisticInfo();
//		module.add(sInfo.getBuildFortNum());
		rms.addModule(module);
    }
    
    public void sendRoleToClient(RespModuleSet rms){
    	AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_USER_INFO;
			}
		};
		module.add(joy_id);
		module.add(unionId);//联盟编号 0没加入联盟
		//TODO 加入联盟的倒计时
		if (joinTimer != null) {
			module.add(1);
			joinTimer.sendToClient(module.getParams());
		} else {
			module.add(0);
		}
		module.add(countryId);
		module.add(name);
		module.add(sex);
		icon.sendToClient(module);
		module.add(level);
		module.add(exp);
		module.add(stamina.getCurStamina());
		module.add(money);
		module.add(krypton);
		module.add(gem);
		module.add(copper);
		module.add(silver);
		module.add(skillPoints);
		module.add(guideIdList.size());
		for (int i = 0; i < guideIdList.size(); i++) {
			module.add(guideIdList.get(i));
		}
		int addFlag  = lastJoin > 0 ? 1 : 0;
		module.add(addFlag);//是否加入过联盟
		module.add(getExpediteMaxNum(0));//获取玩家最大可出征队伍数量 int
    	rms.addModule(module);
    	System.out.println(module.getParams().toString());
    }
    
	@Override
	public void loadFromData(SqlData data) {
		joy_id     = data.getLong(RED_ALERT_ROLE_ID);
		name       = data.getString(RED_ALERT_GENERAL_NAME);
		country    = data.getString(RED_ALERT_ROLE_INCOUNTRY);
		language   = data.getString(RED_ALERT_ROLE_LANGUAGE);
		if (StringUtils.isNull(name)){
			name = "新兵" + joy_id;
		}
		if (StringUtils.isNull(country)){
			country = "Chinese";
		}
		if (StringUtils.isNull(language)){
			language = "zh";
		}
		unionId    = data.getLong(RED_ALERT_GENERAL_UNION_ID);
		String joinStr = data.getString(RED_ALERT_JOIN_UNION_TIMER);
		if (StringUtils.isNull(joinStr)) {
			joinTimer = null;
		} else {
			joinTimer = JsonUtil.JsonToObject(joinStr, TimerLast.class);
			if (joinTimer != null && joinTimer.over(TimeUtils.nowLong())) {
				joinTimer.die();
			}
			joinTimer.registTimeOver(new JoinUnionTimerFinish(joy_id));
		}
		channelId  = data.getString(RED_ALERT_ROLE_CHANNELID);
		countryId  = data.getInt(RED_ALERT_ROLE_COUNTRY);
		sex        = data.getByte(RED_ALERT_ROLE_SEX);
		level      = data.getByte(RED_ALERT_GENERAL_LEVEL);
		exp        = data.getLong(RED_ALERT_ROLE_EXP);
		money  	   = data.getInt(RED_ALERT_ROLE_MONEY);
		krypton    = data.getInt(RED_ALERT_ROLE_KRYPTON);
		gem  	   = data.getInt(RED_ALERT_ROLE_GEM);
		copper     = data.getInt(RED_ALERT_ROLE_COPPER);
		silver	   = data.getInt(RED_ALERT_ROLE_SLIVER);
		icon.setIconId(data.getByte(RED_ALERT_ROLE_ICON_ID));
		icon.setIconType(data.getByte(RED_ALERT_ROLE_ICON_TYPE));
		icon.setIconName(data.getString(RED_ALERT_ROLE_ICON_NAME));
		effectAgent.setUid(joy_id);
		vipInfo.setUid(joy_id);
		String strVip = data.getString(RED_ALERT_ROLE_VIPINFO);
		vipInfo.deserialize(strVip);
		String staminaData    = data.getString(RED_ALERT_ROLE_STAMINA); //必要保证VIPInfo的读取在Stamina的后面
		if(StringUtils.isNull(staminaData)){
			stamina.initStamina(this);
		}else{
			stamina.deserialize(this, level, staminaData);
		}
		String str = data.getString(RED_ALERT_ROLE_POSF);
		posFavorites = JsonUtil.JsonToObjectList(str,PositionInfo.class);
		String guideString = data.getString(rED_ALERT_ROLE_GUIDEIDLIST);
		if (!StringUtils.isNull(guideString)) {
			guideIdList = JsonUtil.JsonToObjectList(guideString, String.class);
		}
		skillPoints = data.getInt(RED_ALERT_ROLE_POINTS);
		lastJoin  = data.getLong(RED_ALERT_ROLE_LAST_JOIN);
		lastLoginTime=data.getLong(RED_ALERT_ROLE_LAST_LOGIN);
		uidRegisTime = data.getString(RED_ALERT_ROLE_UID_REGIS);
		uuid=data.getString(RED_ALERT_ROLE_UUID);
		uuidRegisTime = data.getString(RED_ALERT_ROLE_UID_REGIS);
		model =data.getString(RED_ALERT_ROLE_MODEL);
		version =data.getString(RED_ALERT_ROLE_VERSION);
		resolution =data.getString(RED_ALERT_ROLE_RESOLUTION);
		memory =data.getInt(RED_ALERT_ROLE_MEMORY);
		openId= data.getString(RED_ALERT_ROLE_OPENID);
		signIn =data.getInt(RED_ALERT_ROLE_SIGNIN);
		registrationId = data.getString(RED_ALERT_ROLE_TEGSITID);
		lastLoginIp = data.getString(RED_ALERT_ROLE_LAST_LOGIN_IP);
		String chatGroupsData = data.getString(RED_ALERT_ROLE_CHATGROUPS);
		chatAgent.deserialize(chatGroupsData);
		taskState = data.getString(RED_ALERT_ROLE_TASKSTATE);
		String sevenSignInData = data.getString(RED_ALERT_ROLE_SEVEN_SIGNIN);
		sevenSignIn.deserialize(sevenSignInData);
		String thirtySignInData = data.getString(RED_ALERT_ROLE_THIRTY_SIGNIN);
		thirtySignIn.deserialize(thirtySignInData);
		String onlineRewardData = data.getString(RED_ALERT_ROLE_ONLINE_REWARD);
		onlineAgent.deserialize(onlineRewardData);
		String effectData = data.getString(RED_ALERT_ROLE_EFFECTDATA);
		effectAgent.deserialize(effectData);
		String armyGroupData = data.getString(RED_ALERT_ROLE_ARMY_GROUP);
		if (!StringUtils.isNull(armyGroupData)) {
			armyGroups = JSON.parseObject(armyGroupData,new TypeReference<Map<Integer,ArmyGroup>>(){});
		}
		String turntableData = data.getString(RED_ALERT_ROLE_TURNTABLE);
		if (!StringUtils.isNull(turntableData)) {
			turntableBody = JsonUtil.JsonToObject(turntableData, TurntableBody.class);
			if (!TimeUtils.isSameDay(turntableBody.getSaveTime(), TimeUtils.nowLong())) {
				turntableBody.setTurnSum(0);
			}
		} else {
			GameLog.error("turntableData is null -fail ");//会在建筑加载完成后重置大转盘的对象
			turntableBody = null;
		}
		String shopStr = data.getString(RED_ALERT_ROLE_SHOP);
		shopAgent.deserialize(shopStr);
		String blacklistData = data.getString(RED_ALERT_BLACKLIST);
		blacklist = JsonUtil.JsonToObjectList(blacklistData, Long.class);
		String roleSettingData = data.getString(RED_ALERT_ROEL_SETTING);
		if(!StringUtils.isNull(roleSettingData)){
			roleSetting = JsonUtil.JsonToObject(roleSettingData, RoleSetting.class);
		}
		String marketStr = data.getString(RED_ALERT_ROEL_MARKET);
		blackMarketAgent.deserialize(marketStr,joy_id);
		taskAgent.setUid(joy_id, taskState);
		taskAgent.deserialize(data.get(RED_ALERT_ROEL_MISSIONS));
		honorAgent.deserialize(data.get(RED_ALERT_ROEL_HONORS));
		bagAgent.setUid(joy_id);
		bagAgent.deserialize(data.get(RED_ALERT_ROLE_BAGDATAS));
		skillAgent.setUid(joy_id);
		skillAgent.deserialize(data.get(RED_ALERT_ROLE_SKILLDATAS));
		roleAnti.deserialize((byte[])data.get(RED_ALERT_ROLE_ANTI));
		roleCopyDeserialize(data.get(RED_ALERT_ROLE_COPYS));
		String roleRedpacketData = data.getString(RED_ALERT_ROLE_REDPACKET);
		if(!StringUtils.isNull(roleRedpacketData)){
			roleRedpackets = JsonUtil.JsonToObject(roleRedpacketData, RoleRedpacket.class);
			if (!TimeUtils.isSameDay(roleRedpackets.getSaveTime(), TimeUtils.nowLong())) {
				roleRedpackets.resetDailyData();
			}
		}
		tickFlag = false;
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_ROLE_ID,joy_id);
		data.put(RED_ALERT_GENERAL_NAME,name);
		data.put(RED_ALERT_GENERAL_UNION_ID,unionId);
		data.put(RED_ALERT_JOIN_UNION_TIMER, JsonUtil.ObjectToJsonString(joinTimer));
		data.put(RED_ALERT_ROLE_CHANNELID,channelId);
		data.put(RED_ALERT_ROLE_COUNTRY,countryId);
		data.put(RED_ALERT_ROLE_SEX,sex);
		data.put(RED_ALERT_ROLE_EXP,exp);
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_ROLE_STAMINA,stamina.serialize());
		data.put(RED_ALERT_ROLE_MONEY, money);
		data.put(RED_ALERT_ROLE_KRYPTON, krypton);
		data.put(RED_ALERT_ROLE_GEM, gem);
		data.put(RED_ALERT_ROLE_COPPER, copper);
		data.put(RED_ALERT_ROLE_SLIVER, silver);
		data.put(RED_ALERT_ROLE_ICON_TYPE,icon.getIconType());
		data.put(RED_ALERT_ROLE_ICON_ID,icon.getIconId());
		data.put(RED_ALERT_ROLE_ICON_NAME,icon.getIconName());
		data.put(RED_ALERT_ROLE_VIPINFO, vipInfo.serialize());
		data.put(RED_ALERT_ROLE_POSF,JsonUtil.ObjectToJsonString(posFavorites));
		data.put(rED_ALERT_ROLE_GUIDEIDLIST, JsonUtil.ObjectToJsonString(guideIdList));
		data.put(RED_ALERT_ROLE_POINTS, skillPoints);
		data.put(RED_ALERT_ROLE_LAST_JOIN,lastJoin);
		data.put(RED_ALERT_ROLE_LAST_LOGIN, lastLoginTime);
		data.put(RED_ALERT_ROLE_UID_REGIS, uidRegisTime);
		data.put(RED_ALERT_ROLE_UUID, uuid);
		data.put(RED_ALERT_ROLE_UUID_REGIS, uuidRegisTime);
		data.put(RED_ALERT_ROLE_INCOUNTRY, country);
		data.put(RED_ALERT_ROLE_LANGUAGE, language);
		data.put(RED_ALERT_ROLE_MODEL, model);
		data.put(RED_ALERT_ROLE_VERSION, version);
		data.put(RED_ALERT_ROLE_RESOLUTION, resolution);
		data.put(RED_ALERT_ROLE_MEMORY, memory);
		data.put(RED_ALERT_ROLE_OPENID, openId);
		data.put(RED_ALERT_ROLE_SIGNIN, signIn);
		data.put(RED_ALERT_ROLE_TEGSITID, registrationId);
		data.put(RED_ALERT_ROLE_LAST_LOGIN_IP,lastLoginIp);
		data.put(RED_ALERT_ROLE_CHATGROUPS, chatAgent.serialize());
		data.put(RED_ALERT_ROLE_TASKSTATE, getTaskState());
		data.put(RED_ALERT_ROLE_SEVEN_SIGNIN, sevenSignIn.serialize());
		data.put(RED_ALERT_ROLE_THIRTY_SIGNIN, thirtySignIn.serialize());
		data.put(RED_ALERT_ROLE_ONLINE_REWARD, onlineAgent.serialize());
		data.put(RED_ALERT_ROLE_EFFECTDATA, effectAgent.serialize());
		data.put(RED_ALERT_ROLE_ARMY_GROUP, JsonUtil.ObjectToJsonString(armyGroups));
		data.put(RED_ALERT_ROLE_TURNTABLE, JsonUtil.ObjectToJsonString(turntableBody));
		turntableBody.setSaveTime(TimeUtils.nowLong());//设置保存时间
		data.put(RED_ALERT_ROLE_SHOP,shopAgent.serialize());
		data.put(RED_ALERT_BLACKLIST, JsonUtil.ObjectToJsonString(blacklist));
		data.put(RED_ALERT_ROEL_SETTING,JsonUtil.ObjectToJsonString(roleSetting));
		data.put(RED_ALERT_ROEL_MARKET,blackMarketAgent.serialize());
		RoleRank rankInfo = rankManager.getRoleRankByRoleUid(joy_id);
		if (rankInfo == null) {
			rankInfo = new RoleRank(this);
		}
		data.put(RED_ALERT_ROLE_RANK_INFO, JsonUtil.ObjectToJsonString(rankInfo));
		taskAgent.serialize(data);
		honorAgent.serialize(data);
		bagAgent.serialize(data);
		skillAgent.serialize(data);
		roleAnti.serialize(data);
		roleCopySerialize(data);
		data.put(RED_ALERT_ROLE_REDPACKET, JsonUtil.ObjectToJsonString(roleRedpackets));
		roleRedpackets.setSaveTime(TimeUtils.nowLong());//设置保存时间
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}
	public long timeNow = 0l;
	@Override
	public void _tick(long now) {
		if (joinTimer != null && joinTimer.over(now)) {
			joinTimer.die();
		}
		if (now > lastSaveTime + Const.MINUTE * 15){
			//15分钟自动保存
			GameLog.info("role id = " +joy_id + " auto save ");
			save();
			lastSaveTime = now;
		}
		if (now > heartTime + Const.MINUTE * 5 && !tickFlag){//5分钟还没收到心跳就删除
			kick();
		}
		//每秒运行一次
		if (!tickFlag && now - timeNow >= Const.SECOND){
			timeNow = now;
			stamina.tick(now);
			for (int i = 0 ; i < cityAgents.size() ; i++){
				RoleCityAgent agent = cityAgents.get(i);
				agent.tick(this,now);
			}
			if(vipInfo != null){
				//更新玩家最大体力
				vipInfo.tick(this,now);
			}
			if(skillAgent != null){
				//技能效果
				skillAgent.tick(this);
			}
			onlineAgent.tick(now);
			blackMarketAgent.tick(now);
			effectAgent.tick(this,now);
			roleAnti.tick(this,now);
			if (unionId != 0) {
				UnionBody body = unionManager.search(unionId);
				if (body != null) {
					UnionMember unionMember = body.searchMember(joy_id);
					if (unionMember != null) {
						unionMember.tick(now);
					}
				}
			}
		}
	}
	
	@Override
	public void registerAll() {
		RoleBuildEvent buildEvent = new RoleBuildEvent();
		registerEventHandler(buildEvent,GameEvent.LOAD_FROM_DB);
		registerEventHandler(buildEvent,GameEvent.ROLE_CREATE);
		registerEventHandler(buildEvent,GameEvent.ROLE_BUILD_TIME_ROVER);
		registerEventHandler(buildEvent,GameEvent.ARMY_FACT_LEVEL_UP);
		registerEventHandler(buildEvent,GameEvent.ARMY_FACT_CREATE);
		
		RoleMapEvent mapEvent = new RoleMapEvent();
		registerEventHandler(mapEvent,GameEvent.ROLE_CREATE);
		registerEventHandler(mapEvent,GameEvent.CENTER_LEVE_UP);
		registerEventHandler(mapEvent,GameEvent.LOAD_FROM_DB);
		registerEventHandler(mapEvent,GameEvent.TROOPS_SEND);
		registerEventHandler(mapEvent,GameEvent.UNION_FIGHT_CHANGE);
		registerEventHandler(mapEvent, GameEvent.ROlE_CHANGE_BASE_INFO);
		registerEventHandler(mapEvent, GameEvent.ROLE_RES_BUFF_CHANGE);
		registerEventHandler(mapEvent, GameEvent.ROLE_RES_BUFF_CHANGE_1);
		
		RoleBagEvent bagEvent = new RoleBagEvent();
		registerEventHandler(bagEvent,GameEvent.ROLE_CREATE);
		registerEventHandler(bagEvent,GameEvent.LOAD_FROM_DB);
		registerEventHandler(new RoleHeartEvent(),GameEvent.ROLE_HEART);
		

		RoleEvent roleEvent = new RoleEvent();
		registerEventHandler(roleEvent, GameEvent.ROLE_CREATE);
		registerEventHandler(roleEvent, GameEvent.LOAD_FROM_DB);
		registerEventHandler(roleEvent, GameEvent.RANK_ROLE_FIGHT_CHANGE);
		
		UnionEvent unionEvent = new UnionEvent();
		registerEventHandler(unionEvent,GameEvent.ROlE_CHANGE_BASE_INFO);
		registerEventHandler(unionEvent,GameEvent.UNION_JOIN);
		registerEventHandler(unionEvent,GameEvent.UNION_EXIT);
		registerEventHandler(unionEvent,GameEvent.UNION_WAR_RECORD);//联盟战争记录
		
		//靠后位置，添加新event时请在这之前插入
		TaskEvent taskEvent = new TaskEvent();
		registerEventHandler(taskEvent,GameEvent.ROLE_CREATE);
		registerEventHandler(taskEvent,GameEvent.LOAD_FROM_DB);
		registerEventHandler(taskEvent,GameEvent.TASK_CHECK_EVENT);
		
		HonorEvent honorEvent = new HonorEvent();   //荣誉任务事件
		registerEventHandler(honorEvent,GameEvent.ROLE_CREATE);
		registerEventHandler(honorEvent,GameEvent.LOAD_FROM_DB);
		registerEventHandler(honorEvent,GameEvent.TASK_CHECK_STATE_EVENT);
		
		EffectEvent effectEvent = new EffectEvent();//buff事件
		registerEventHandler(effectEvent, GameEvent.EFFECT_UPDATE);
		
		RoleRankEvent rankEvent = new RoleRankEvent();// 排行榜事件
		registerEventHandler(rankEvent, GameEvent.RANK_ROLE_FIGHT_CHANGE);
		registerEventHandler(rankEvent, GameEvent.RANK_ROLEKILLENEMY_CHANGE);
		registerEventHandler(rankEvent, GameEvent.RANK_ROLECITYLEVEL_CHANGE);
		registerEventHandler(rankEvent, GameEvent.RANK_ROLEHEROLEVEL_CHANGE);
//		registerEventHandler(rankEvent, GameEvent.RANK_ROLEOCCUPYLAND_CHANGE);
//		registerEventHandler(rankEvent, GameEvent.RANK_ROLECOLLECTIONRES_CHANGE);
//		registerEventHandler(rankEvent, GameEvent.RANK_ROLEASSISTANCE_CHANGE);
//		registerEventHandler(rankEvent, GameEvent.RANK_ROLEDUNGEONSPEED_CHANGE);
		
		ActvtEvent actvtEvent = new ActvtEvent();
		registerEventHandler(actvtEvent,GameEvent.ACTIVITY_EVENTS);
		
		RemoveRoleEvent removeRole = new RemoveRoleEvent();
		registerEventHandler(removeRole,GameEvent.REMOVE_ROLE);

	}
	
	public void kick() {
		GameLog.info("system kick <" + joy_id + ">");
		tickFlag = true;
		if (isOnline()) {
			LogManager.leaveLog(this);
			OnlineRunnable.recordTime(this, (byte) 2);
		}
		save();
		//把玩家踢下线，返回登录界面
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_KICK_ROLE;
			}
		};
		rms.addModule(module);
		MessageSendUtil.sendModule(rms, userInfo);
	}
	
	private int getMaxLevel(){
		List<Userlevel> result = dataManager.serachList(Userlevel.class);
		Collections.sort(result);
		return result.get(0).getLevel();
	}
	
	private boolean levelUp(){
		int max = getMaxLevel();
		if (level >= max){
			return false;
		}
		Userlevel ul = dataManager.serach(Userlevel.class,(int)level);
		if (ul == null) {
			GameLog.error("找不到固化数据了,roleLevel=" + level);
			return false;
		}
		int preLevl = level;
		if (exp >= ul.getExperience()){
			exp -= ul.getExperience();
			level++;
			skillPoints += ul.getSkillpoints();
			levelUp();
		}
		return level > preLevl;
	}
	
	public void addExp(int exp){
		this.exp += exp;
		this.exp = this.exp > maxExp() ? maxExp() : this.exp;
		if (levelUp()){
			handleEvent(GameEvent.RANK_ROLEHEROLEVEL_CHANGE, new TaskEventDelay());
			handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_LEVEL, level);
		}
	}
	
	private int maxExp() {
		int max = getMaxLevel();
		Userlevel ul = dataManager.serach(Userlevel.class, max);
		if (ul == null) {
			GameLog.error("找不到固化数据了,roleLevel=" + level);
			return Integer.MAX_VALUE;
		}
		return ul.getExperience();
	}
	
	public boolean isOnline(){
		return userInfo != null;
	}

	@Override
	public String table() {
		return DaoData.TABLE_RED_ALERT_ROLE;
	}

	@Override
	public String[] wheres() {
		return new String[]{DaoData.RED_ALERT_ROLE_ID};
	}

	@Override
	public void save(){
		for (int i = 0 ; i < cityAgents.size() ; i++){
			RoleCityAgent agent = cityAgents.get(i);
			agent.save();
		}
		statictisInfo.save();
		dailyTaskAgent.save();
		super.save();
	}

	/**
	 * 
	 * @param addMoney
	 * @return
	 */
	public boolean addRoleMoney(int addMoney) {
		money += addMoney;
		money = Math.max(0,money);
		return true;
	}

	public boolean addRoleKrypton(int addKrypton) {
		krypton += addKrypton;
		krypton = Math.max(0,krypton);
		return true;
	}

	public boolean addRoleGem(int addGem) {
		gem += addGem;
		gem = Math.max(0,gem);
		return true;
	}
	
	public boolean addRoleCopper(int addCopper){
		copper += addCopper;
		copper = Math.max(0,copper);
		return true;
	}
	
	public boolean addRoleSilver(int addSilver){
		silver += addSilver;
		silver = Math.max(0,silver);
		return true;
	}

	/**
	 * 
	 * @param redMoney
	 * @return
	 */
	public boolean redRoleMoney(int redMoney) {
		if (money - redMoney < 0) {
			return false;
		}
		money -= redMoney;
		money  = Math.max(0,money);
		return true;
	}

	public boolean redRoleKrypton(int redKrypton) {
		if (krypton - redKrypton < 0) {
			return false;
		}
		krypton -= redKrypton;
		krypton  = Math.max(0,krypton);
		return true;
	}

	public boolean redRoleGem(int redGem) {
		if (gem - redGem < 0) {
			return false;
		}
		gem -= redGem;
		gem  = Math.max(0,gem);
		return true;
	}
	
	public boolean redRoleCopper(int redCopper){
		copper -= redCopper;
		copper  = Math.max(0,copper);
		return true;
	}
	
	public boolean redRoleSilver(int redSilver){
		silver -= redSilver;
		silver  = Math.max(0,silver);
		return true;
	}
	
	/**
	 * 资源与金币换算公式
	 */
	public int resourceChgMoney(ResourceTypeConst type, long num) {
		Resourcestype resType = dataManager.serach(Resourcestype.class,type.getKey());
		int money = 0;
		num *= resType.getWeight();

		if (num <= 500) {
			money = 1;
		} else if (num <= 10000) {
			money = (int) Math.round(1 + 0.002 * (num - 500));
		} else if (num <= 50000) {
			money = (int) Math.round(20 + 0.00175 * (num - 10000));
		} else if (num <= 250000) {
			money = (int) Math.round(90 + 0.00155 * (num - 50000));
		} else if (num <= 1000000) {
			money = (int) Math.round(400 + 0.00134 * (num - 250000));
		} else {
			money = (int) Math.round(1400 + 0.00115 * (num - 1000000));
		}
		return money;
	}
	
	/**
	 * 时间与金币换算公式
	 */
	public int timeChgMoney(long time, byte type) {
		int money = 0;
		if (type == 1) { // 建筑建造和升级时，最后5分钟(+buff加成)时间免费
			time -= getFreeTime();
		}
		if (time <= 0){
			money = 0;
		}else if (time <= Const.MINUTE / 1000) {
			money = 2;
		} else if (time <= Const.HOUR / 1000) {
			money = Math.round(2 + 0.0306f * (time - 60));
		} else if (time <= Const.DAY / 1000) {
			money = Math.round(110 + 0.0277f * (time - 3600));
		} else if (time <= 7 * Const.DAY / 1000) {
			money = Math.round(2400 + 0.0243f * (time - 86400));
		} else {
			money = Math.round(15000 + 0.0202f * (time - 604800));
		}
		return money;
	}

	
	/**
	 * 获取玩家战斗力
	 */
	public int getRoleBattleEffec(){
		int value = 0;
		//获取所有城池战斗力
		for (int i = 0 ; i < cityAgents.size() ; i++){
			RoleCityAgent agent = cityAgents.get(i);
			//获取建筑战斗力
			value += agent.getCityBattleEffec();
			//获取部队战斗力
			value += agent.getCityArmys().getArmysBattleEffec();
			//各种陷阱或其他防御道具的战斗力
		}
		//获取所有科技战斗力, 暂无
		return value;
	}
	
	/**
	 * 获取玩家最大可出征数量
	 * @return
	 */
	public int getExpediteMaxNum(int cityId){
		RoleCityAgent agent = getCity(cityId);
		int buff = agent.getCityAttr().getAddTroopsLimit();
		return GameConfig.EXPEDITE_TROOPS_NUM + buff;
	}
	
	/**
	 * 获取当前玩家已派出的部队数量
	 * @return
	 */
	public int getExpediteCurNum(){
		int num = 0;
		List<ExpediteTroops> troopses = mapWorld.getMyRoleExpedites(joy_id);
		num += troopses.size();
		List<GarrisonTroops> garrisons = mapWorld.getRelevanceGarrisons(joy_id);
		num += garrisons.size();
		return num;
	}
	
	/**
	 * 获取行军速度加成
	 * @return
	 */
	public float getExpediteSpeedEffect(int cityId, String armyId){
		float value = RoleArmyAttr.getEffVal(this,TargetType.T_A_IMP_SS, armyId);
		return value;
	}
	
	/**
	 * 获取资源采集速度加成
	 * @return
	 */
	public float getResourceCollectEffect(int cityId, String armyId, String resId){
		RoleCityAgent agent = getCity(cityId);
		String buildId = BuildName.CITY_CENTER.getKey();
		ResourceTypeConst type = ResourceTypeConst.search(resId);
		List<RoleBuild> buildLst = agent.searchBuildByBuildId(buildId);
		float buildBuff = 0;
		if(buildLst != null){
			int level = buildLst.get(0).getLevel();
			Buildinglevel data = RoleBuild.getBuildinglevelByCondition(buildId, level);
			switch(type){
			case RESOURCE_TYPE_FOOD:
				buildBuff = Float.parseFloat(data.getParamList().get(0));
				break;
			case RESOURCE_TYPE_METAL:
				buildBuff = Float.parseFloat(data.getParamList().get(1));
				break;
			default:
				break;
			}
		}
		float buffSold = RoleArmyAttr.getEffVal(this,TargetType.T_A_ADD_IC, armyId);
		float buffRes = agent.getCityAttr().getImpCollSpeed(ResourceTypeConst.search(resId));
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_GATHER_SPEED)/100.0f;
		return buffSold + buffRes + buildBuff + newServerBuff;
	}
	
	/**
	 * 有倒计时的资源采集速度加成
	 */
	public List<EffectListener> getResourceCollectEffect(int cityId){
		List<EffectListener> effList = new ArrayList<EffectListener>();
		this.effectAgent.searchCollEffs(TargetType.T_A_ADD_IC, effList);
		return effList;
	}
	
	/***
	 * 采集时间缩短加成,目前没用
	 * @return
	 */
	public float getResourceTimeEffect(int cityId, String keyType){
		ResourceTypeConst type = ResourceTypeConst.search(keyType);
		RoleCityAgent agent = getCity(cityId);
		float buff = 0;
		switch(type){
		case RESOURCE_TYPE_FOOD:
			buff += agent.getCityAttr().getReduFoodCollTime();
			break;
		case RESOURCE_TYPE_METAL:
			buff += agent.getCityAttr().getReduMetalCollTime();
			break;
		case RESOURCE_TYPE_OIL:
			buff += agent.getCityAttr().getReduOilCollTime();
			break;
		case RESOURCE_TYPE_ALLOY:
			buff += agent.getCityAttr().getReduAlloyCollTime();
			break;
		default:
			break;
		}
		return buff;
	}
	
	/**
	 * 战斗对象buff加成
	 * @param attributes
	 */
	public void effectFightTroops(List<FightTroops> team ,List<FightTroops> emenys ,int cityId){
		TargetType[][] keys = new TargetType[][]{
			{
				TargetType.T_A_IMP_SA,
				TargetType.C_A_RED_ATK,
				TargetType.C_RED_ALL_DG
			},//火力buff部分
			{
				 TargetType.T_A_IMP_SD,
				 TargetType.C_A_RED_DEF,
			},//防御buff部分
			{
				TargetType.T_A_IMP_AHP,
				TargetType.C_A_RED_HP
			},//生命buff部分
			{
				TargetType.T_A_IMP_IAR,
				TargetType.C_A_RED_ATR
			},//命中buff部分
			{
				TargetType.T_A_IMP_ICR,
				TargetType.C_A_RED_CRT
			},//暴击buff部分
			{
				TargetType.T_A_IMP_IER,
				TargetType.C_A_RED_EDR
			},
			{
				TargetType.T_A_IMP_DMG,
				TargetType.C_A_RED_DMG
			},
			{
				TargetType.C_A_RED_BDMG_ALL,
				TargetType.C_A_RED_BDMG
			},
			{
				TargetType.T_A_IMP_SS,
				TargetType.C_A_RED_MB
			}
		};
		FightBuffType[] bts = new FightBuffType[]{
			FightBuffType.ATTACK_EFF,
			FightBuffType.DEFENDE_EFF,
			FightBuffType.HP_EFF,
			FightBuffType.HIT_EFF,
			FightBuffType.CRIT_EFF,
			FightBuffType.EVADE_EFF,
			FightBuffType.VULNERABILITY_EFF,
			FightBuffType.MITIGATION_EFF,
			FightBuffType.SPEED_EFF
		};
		for (int i = 0 ; i < keys.length ; i++){
			for (int j = 0 ; j < keys[i].length ; j++){
				TargetType key = keys[i][j];
				List<FightTroops> lis = null;
				if (key.getSymbol() <= 2){
					lis = team;
				}else if (key.getSymbol() <= 4){
					lis = emenys;
				}else if (key.getSymbol() <= 6){
					lis = new ArrayList<FightTroops>();
					lis.addAll(team);
					lis.addAll(emenys);
				}
				if (lis != null){
					for (int k = 0 ; k < lis.size() ; k++){
						FightTroops troops = lis.get(k);
						String armyId = troops.getAttribute().getName();
						float value = RoleArmyAttr.getEffVal(this,key,armyId);
						int symbol = key.getSymbol() % 2 == 0 ? -1 : 1;
						if (value > 0){
							troops.addBuff(bts[i],value * symbol);
						}
					}
				}
			}
		}
	}
	
	/***
	 * 获取伤兵率
	 * @param cityId 
	 * @param troopsType 部队类型 1碳基生物类型，2机械类型
	 * @return
	 */
	public float getFightInjurieRate(int cityId , int armyType){
		RoleCityAgent city = getCity(cityId);
		List<RoleBuild> builds = null;
		if (armyType == 1){
			builds = city.searchBuildByBuildId(BuildName.HOSPITAL.getKey());
		}else{
			builds = city.searchBuildByBuildId(BuildName.REPAIRER.getKey());
		}
		float result = 0;
		for (int i = 0 ; i < builds.size() ; i++){
			RoleBuild  build = builds.get(i);
			List<String> str = build.getBuildingLevel().getParamList();
			float temp = Float.parseFloat(str.get(0));
			result = Math.max(result, temp);
		}
		float buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_RED_DR, ExtendsType.EXTEND_ARMY, armyType);
		//战斗受伤率
		GameLog.info("[getFightInjurieRate]roleuid="+this.getJoy_id()+"|city="+cityId+"|armyType="+armyType+"|buildcount="+result+"|buff="+buff);
		//add buff
		result += buff;

		return result;
	}
	
	/**
	 * 获取战斗力
	 * @return
	 */
	public int getFightPower() {
		return statictisInfo.getRoleFight();
	}
	
	/**
	 * 获取战争大厅的集结最大数量
	 */
	public int getRoleMaxMassNum(int cityId){
		List<RoleBuild> builds = getCity(cityId).searchBuildByBuildId(BuildName.WAR_LOBBY.getKey());
		if(builds.size() > 0){
			Buildinglevel buildlevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(), builds.get(0).getLevel());
			if(buildlevel != null){
				List<String> paramList = buildlevel.getParamList();
				if(paramList.size() > 0){
					int num = Integer.parseInt(paramList.get(0));
					//add Buff
					int buff = getCity(cityId).getCityAttr().getAddSoldNum();
					return (num + buff);
				}
			}
		}
		return 0;
	}
	/**
	 * 获取战争大厅的集结部队的最大数量
	 */
	public int getRoleMaxMassArmyNum(int cityId){
		List<RoleBuild> builds = getCity(cityId).searchBuildByBuildId(BuildName.WAR_LOBBY.getKey());
		if (builds.size() > 0){
			Buildinglevel buildlevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(),builds.get(0).getLevel());
			if(buildlevel != null){
				List<String> paramList = buildlevel.getParamList();
				if(paramList.size() > 1){
					return Integer.parseInt(paramList.get(1)) + getCity(cityId).getCityAttr().getAddWarLimit();
				}
			}
		}
		return 0;
	}
	
	/**
	 * 获取玩家的建筑被帮助的最大次数
	 * @return
	 */
	public int getBuildAssistanceNum(int cityId) {
		List<RoleBuild> builds = getCity(cityId).searchBuildByBuildId(BuildName.EMBASSY.getKey());
		if(builds.size() > 0){
			Buildinglevel buildlevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(), builds.get(0).getLevel());
			if(buildlevel != null){
				List<String> paramList = buildlevel.getParamList();
				if(paramList.size() == 2){
					return Integer.parseInt(paramList.get(0));
				}
			}
		}
		return GameConfig.BUILD_ASSISTANCE_MAX_NUM;
	}
	
	/**
	 * 获取玩家的建筑被帮助减少的时间
	 * @return
	 */
	public int getBuildAssistanceEffect(int cityId) {
		List<RoleBuild> builds = getCity(cityId).searchBuildByBuildId(BuildName.EMBASSY.getKey());
		if(builds.size() > 0){
			Buildinglevel buildlevel = RoleBuild.getBuildinglevelByCondition(builds.get(0).getBuildId(), builds.get(0).getLevel());
			if(buildlevel != null){
				List<String> paramList = buildlevel.getParamList();
				if(paramList.size() == 2){
					return Integer.parseInt(paramList.get(1));
				}
			}
		}
		return GameConfig.BUILD_ASSISTANCE_EFFECT;
	}
	
	/**
	 * 获取仓库被掠夺率
	 * @return
	 */
	public int getCouldLogistics(int cityId,ResourceTypeConst rtc){
		return 0;
	}
	
	public void sendRoleDetailToClient(Role role, int cityId){
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_DETAILS_INFO;
			}
		};
		List<RoleStaticData> infoList = RoleStaticData.getDetailList(role, cityId);
		module.add(infoList.size()); //列表大小
		for(RoleStaticData info : infoList){
			module.add(info.getHfss().getId());//String id
			module.add(info.getHfss().getStatisticName());//String 名字
			module.add(info.getHfss().getType());//String 所属类
			module.add(info.getHfss().getVlType());//byte 值类型，0-整数，1-小数
			module.add(String.valueOf(info.getNum()));//String
		}
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(module);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
	
	public void sendArmyMobiBuff(RespModuleSet rms){
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_ARMY_MOVEBUFF;
			}
		};
		float buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 1);
		module.add(String.valueOf(buff));//string, 步兵速度加成 
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 2);
		module.add(String.valueOf(buff));//string, 战车速度加成 
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 3);
		module.add(String.valueOf(buff));//string, 坦克速度加成 
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 4);
		module.add(String.valueOf(buff));//string, 飞机速度加成 
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ALL, 0);
		module.add(String.valueOf(buff));//string, 所有部队负重 加成百分比
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 1);
		module.add(String.valueOf(buff));//string, 提升步兵的负重量	加成百分比
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 2);
		module.add(String.valueOf(buff));//string, 提升战车的负重量	加成百分比
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 3);
		module.add(String.valueOf(buff));//string, 提升坦克的负重量	加成百分比
		buff = RoleArmyAttr.getEffValV2(this,TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 4);
		module.add(String.valueOf(buff));//string, 提升战机的负重量	加成百分比
		module.add(getCity(0).getMaxOutBattleBaseNum());//int 出征部队空间的基础量
		module.add(String.valueOf(getCity(0).getCityAttr().getAddSoldLimit())); //string  出征部队空间的buff增加量
		module.add(String.valueOf(getCity(0).getCityAttr().getImpBuildSpeed()));// 提高建造速度 ★
		module.add(getCity(0).getCityAttr().getFortNum());//int  要塞的增加数量
		rms.addModule(module);
	}

	/**
	 * 初始化对应的Rolerank对象
	 */
	public void initRoleRank() {
		RoleRank roleRank = new RoleRank(this);
		rankManager.addRoleRank(roleRank);
	}
	
	public boolean changeRoleName(String newName) {
		RespModuleSet rms = new RespModuleSet();
		nameManager.change(joy_id,name,newName);
		setName(newName);
		handleEvent(GameEvent.ROlE_CHANGE_BASE_INFO);//更新用户基础数据(姓名\等级)
		sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms,this);
		return true;
	}

	public boolean changeRoleImage(int type, String iconName) {
		RespModuleSet rms = new RespModuleSet();
		if (type == 0) { // 修改为系统的头像
			icon.setIconId(Byte.parseByte(iconName));
		} else {// 修改为自定义的头像
			icon.setIconName(iconName);
		}
		handleEvent(GameEvent.ROlE_CHANGE_BASE_INFO);
		sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, this);
//		save();
		return true;
	}
	
	public boolean checkSkillLimition(List<String> limitions){
		boolean bSuc = false;
		for(String limition : limitions){
			bSuc = false;
			String[] lims = limition.split(":");
			String sparam = lims[0];
			int iparam = Integer.parseInt(lims[1]);
			if(sparam.equals("RoleLevel")){
				if(level >= iparam){
					bSuc = true;
				}else{
					return false;
				}
			}
		}
		return bSuc;
	}


	/**
	 * 下发用户战斗力
	 */
	public void sendFrequentVariables() {
		sendFrequentVariables(true,new RespModuleSet());
	}
	
	public void sendFrequentVariables(boolean send , RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_FREQUENT_VARIABLES;
			}
		};
		module.add(statictisInfo.getRoleFight());//用户战斗力 int
		rms.addModule(module);
		if (send){
			MessageSendUtil.sendModule(rms, this);
		}
	}
	
	/**
	 * 玩家指挥官详情中的部分字段,主动推送
	 */
	public void sendCommanderInfo(){
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_COMMANDER_INFO;
			}
		};
		float compRate = 0;
		if(honorAgent.getMedalCount(true) != 0){
			compRate = (float)honorAgent.getMedalCount(false)/honorAgent.getMedalCount(true);
		}
		module.add(statictisInfo.getKillSoldsNum());//int 杀敌数统计
		module.add(String.valueOf(compRate));	//string 成就完成度
		module.add(honorAgent.getMedalCount(false));//int 勋章收集数量
		rms.addModule(module);
		MessageSendUtil.sendModule(rms, this);
	}
	
	/**
	 * 用户黑名单
	 * @param rms
	 */
	public void sendRoleBlacklist(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_BLACKLIST;
			}
		};
		int size = blacklist.size(); // ★黑名单
		module.add(size);
		for (int i = 0 ; i < blacklist.size() ; i++){
			Long  val = blacklist.get(i);
			Role role = world.getRole(val);
			if (role == null) {
				continue;
			}
			module.add(val); // long uid
			module.add(role.getName());// string name
			role.getIcon().sendToClient(module); // 头像
			module.add(role.getUnionId()); // long unionID 0:未加入联盟
			if (role.getUnionId() != 0L) {
				UnionBody body = unionManager.search(role.getUnionId());
				module.add(body == null ? "0" : body.getName());
			}
		}
		rms.addModule(module);
	}
	
	/**
	 * 获取视野
	 * @param flag
	 * @return
	 */
	public List<Integer> getViews(){
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0 ; i < cityAgents.size() ; i++){
			RoleCityAgent city = cityAgents.get(i);
			MapCity mc = mapWorld.searchMapCity(joy_id,city.getId());
			mc.getViews(result,this);
		}
		List<MapFortress> fortresses = mapWorld.getAllFortresses(joy_id);
		for (int i = 0 ; i < fortresses.size() ; i++){
			MapFortress fortress = fortresses.get(i);
			fortress.getViews(result);
		}
		UnionBody union = unionManager.search(unionId);
		if (union != null){
			union.getViews(result,this);
		}
		return result;
	}
	
	/**
	 * 获取兵种是否解锁
	 */
	public int getSoldUnlockLvl(String soldId){
		int level = 0;
		List<RoleBuild> builds = null;
		Soldierstt stt = dataManager.serach(Soldierstt.class, soldId);
		switch(stt.getTechTreeID()){
		case 1:
			for (int i = 0 ; i < cityAgents.size() ; i++){
				RoleCityAgent city = cityAgents.get(i);
				builds = city.searchBuildByBuildId(BuildName.SOLDIERS_CAMP.getKey());
			}
			break;
		case 2:
			for (int i = 0 ; i < cityAgents.size() ; i++){
				RoleCityAgent city = cityAgents.get(i);
				builds = city.searchBuildByBuildId(BuildName.SOLDIERS_CAMP.getKey());
			}
			break;
		case 3:
			for (int i = 0 ; i < cityAgents.size() ; i++){
				RoleCityAgent city = cityAgents.get(i);
				builds = city.searchBuildByBuildId(BuildName.SOLDIERS_CAMP.getKey());
			}
			break;
		case 4:
			for (int i = 0 ; i < cityAgents.size() ; i++){
				RoleCityAgent city = cityAgents.get(i);
				builds = city.searchBuildByBuildId(BuildName.SOLDIERS_CAMP.getKey());
			}
			break;
		}
		if(builds == null || builds.size() == 0){
			return level;
		}
		for(RoleBuild build : builds){
			level = build.getPointsAgent().getArmyLvl(soldId);
		}
		return level;
	}


	public int getRadarlevel(int cityId) {
		RoleCityAgent city = cityAgents.get(cityId);
		if (city != null){
			return city.getRadarLevel();
		}
		return 1;
	}


	public void sendViews(RespModuleSet rms,boolean send) {
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_VIEWS;
			}
		};
		List<Integer> views = getViews();
		module.add(views);	//玩家视野范围坐标
		rms.addModule(module);
		if (send){
			MessageSendUtil.sendModule(rms,userInfo);
		}
	}

	@Override
	public void over() {
		savIng = false;
		if (tickFlag){
			remove();
		}
	}

	public void updateTurntableTurnSum(int turnSum) {
		turntableBody.setTurnSum(turnSum);
	}

	public int getBuildLevel(int cityId, String key) {
		RoleCityAgent city = getCity(cityId);
		if (city != null){
			return city.getBuildMaxLevel(key);
		}
		return 0;
	}

	@Override
	public String toString() {
		return "role_" + joy_id;
	}
	
	/**
	 * 下发用户副本信息
	 */
	public void sendRoleCopysToClient(RespModuleSet rms, boolean send) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_COPYS;
			}
		};
		if (roleCopys.size() < 1) {
			module.add(0);		//没有副本 int
		}else{
			module.add(1);		//有副本 int
			module.add(roleCopys.size());
			for (int type : roleCopys.keySet()) {
				module.add(type);// 副本的类型 int
				RoleRelic roleRelic = roleCopys.get(type);
				if (roleRelic.getRelicArmys().size() < 1) {
					module.add(0);
				} else {
					module.add(1);
					module.add(roleRelic.getRelicArmys().size());//int 用户副本的个数
					for (int pos : roleRelic.getRelicArmys().keySet()) {
						module.add(pos); // int 副本位置
						Relic relic = roleRelic.getRelicArmys().get(pos);
						module.add(relic.getId());// 副本的ID String
						module.add(relic.getPosition());// int 副本的位置
						module.add(relic.getArmyState());// int 副本的军队的状态 暂定0:不在副本 1:在副本
						module.add(relic.getTroopId());// long 副本的军队编号
					}
					module.add(roleRelic.getIsGotReward());// 时候领取过了 1:通关领过 0:通关未领取过 -1:未通关
					module.add(roleRelic.getType());// int 副本的类型
					module.add(roleRelic.getScenes().size());// int 已通过的关卡
					for (int i = 0; i < roleRelic.getScenes().size(); i++) {
						Scene scene = roleRelic.getScenes().get(i);
						module.add(scene.getSceneId());// 关卡ID String
						module.add(scene.getState());// 关卡的类型 int
						scene.getMonsterArmys().sendArmysClient(module.getParams());// 关卡的怪兽 的部队信息
						int packageSize = scene.getPackages().size();
						module.add(packageSize);// 关卡背包类型大小 int
						for (Byte tempType : scene.getPackages().keySet()) {
							module.add(tempType); // byte 类型
							int itemSize = scene.getPackages().get(tempType).size();
							module.add(itemSize); // item的大小 int
							for (String itemId : scene.getPackages().get(tempType).keySet()) {
								module.add(itemId);// String itemID
								module.add(scene.getPackages().get(tempType).get(itemId));// int item数量
							}
						}
						module.add(scene.getBattleReport());// String 战报
					}
					module.add(JsonUtil.ObjectToJsonString(roleRelic.getFinishReward()));//通关奖励
					module.add(roleRelic.getCurrentFreeResetNum());//免费次数int
					module.add(roleRelic.getCurrentItemResetNum());//道具次数int
					module.add(roleRelic.getMonsterIconId());//怪物的头像ID ★ string
				}
			}
		}
		rms.addModule(module);
		if (send) {
			MessageSendUtil.sendModule(rms, userInfo);
		}
	}
	/**
	 * 添加一个副本对象
	 * @param relicType 类型
	 * @param position 位置
	 * @param relic 副本对象
	 * @param scenes 初始化副本
	 */
	public void addRelic(Relic relic) {
		RoleRelic roleRelic = roleCopys.get(relic.getType());
		Ruins ruin = dataManager.serach(Ruins.class, relic.getId());
		if (ruin == null) {
			GameLog.error("read ruins is fail");
			return;
		}
		if (roleRelic == null) {
			roleRelic = new RoleRelic();
		}
		List<Scene> scenes = roleRelic.getScenes();
		if (scenes == null || scenes.size() < 1) {
			Scene scene = mapWorld.createScene(ruin.getCheckpoin().get(0));
			scenes.add(scene);
		}
		//随机出一个怪物的头像
		List<Heroicon> heroicons = dataManager.serachList(Heroicon.class);
		Heroicon temp = MathUtils.randomOne(heroicons);
		roleRelic.setMonsterIconId(temp == null ? "1" : temp.getId());
		roleRelic.setType(relic.getType());
		roleRelic.getRelicArmys().put(relic.getPosition(), relic);
		// 随机获取奖励的列表(如果列表为空)
		if (roleRelic.getFinishReward() == null || roleRelic.getFinishReward().size() < 1) {
			List<String> rewards = roleRelic.randomRelicReward(ruin);
			roleRelic.setFinishReward(rewards);
		}
		roleCopys.put(relic.getType(), roleRelic);
	}

	/** 
	 * 移除一个副本对象
	 * @param ectype 
	 * @param startPosition
	 */
	public boolean removeRoleRelic(MapEctype ectype) {
		Ruins ruin = dataManager.serach(Ruins.class, ectype.getBulidKey());
		if (ruin == null) {
			GameLog.error("read Ruins table is fail");
			return false;
		}
		if (roleCopys.get(ruin.getType()) == null || roleCopys.get(ruin.getType()).getRelicArmys().isEmpty()) {
			GameLog.error("roleCopys isEmpty where startPosition = " + ectype.getPosition());
			return false;
		}
		if(roleCopys.get(ruin.getType()).getRelicArmys().remove(ectype.getPosition()) == null){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取一个副本对象
	 * 
	 * @param type
	 *            副本类型
	 * @param pos
	 *            副本的位置
	 * @return
	 */
	public Relic searchRoleRelic(int type, int pos) {
		RoleRelic roleRelic = roleCopys.get(type);
		if (roleRelic == null || roleRelic.getRelicArmys().size() < 1) {
			return null;
		}
		return roleRelic.getRelicArmys().get(pos);
	}
	
	public void addPackage(Map<Byte,Map<String,Integer>> packages,List<ItemCell> changes, List<Object> objs){
		for (Byte type : packages.keySet()){
			Map<String,Integer> values = packages.get(type);
			if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_GOODS.ordinal()){
				for (String key : values.keySet()){
					int num = values.get(key).intValue();
					changes.addAll(bagAgent.addGoods(key,num));
					String itemst  = key;
					LogManager.itemOutputLog(this, num, EventName.packageBack.getName(), itemst);
				}
			}else if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_EQUIP.ordinal()){
				for (String key : values.keySet()){
					int num = values.get(key).intValue();
					changes.addAll(bagAgent.addEquip(key,num));
					Equip equip  = dataManager.serach(Equip.class, key);
					LogManager.equipLog(this, equip.getEquipType(), equip.getBeizhuname(), EventName.packageBack.getName());
				}
			}else if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_STONE.ordinal()){
				for (String key : values.keySet()){
					int num = values.get(key).intValue();
					changes.addAll(bagAgent.addOther(key,num));
					String itemst =key;
					LogManager.itemOutputLog(this, num, EventName.packageBack.getName(), itemst);
				}
			}else if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_RESOURCE.ordinal()){
				for (String key : values.keySet()){
					long num = values.get(key).longValue();
					objs.add(ResourceTypeConst.search(key));
					objs.add(num);
					LogManager.itemOutputLog(this, num, EventName.packageBack.getName(), key);
					try {
						NewLogManager.buildLog(this, "grain_resource",key,num);
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}
			}else if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_EXP.ordinal()){
				for (String key : values.keySet()){
					int num = values.get(key).intValue();
					addExp(num);
				}
			}else if (type.byteValue() == ExpeditePackageType.PACKAGE_TYPE_GOLD.ordinal()){
				for (String key : values.keySet()){
					int num = values.get(key).intValue();
					addRoleMoney(num);
					LogManager.goldOutputLog(this, num, EventName.packageBack.getName());
				}
			}
		}
	}
	
	public void roleCopySerialize(SqlData data) {
		JoyBuffer out = JoyBuffer.allocate(8192);
		out.putInt(roleCopys.size());
		for (Integer copyType : roleCopys.keySet()) {
			out.putInt(copyType);
			roleCopys.get(copyType).serialize(out);
		}
		data.put(DaoData.RED_ALERT_ROLE_COPYS, out.arrayToPosition());
	}

	public void roleCopyDeserialize(Object data) {
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		int size = buffer.getInt();
		for (int i = 0; i < size; i++) {
			int copyType = buffer.getInt();
			RoleRelic roleRelic = new RoleRelic();
			roleRelic.deserialize(buffer);
			roleCopys.put(copyType, roleRelic);
		}
	}
	
	/**
	 * 城市建筑的被帮助的次数自增
	 * 
	 * @param cityId
	 * @param buildId
	 */
	public void addBuildHelper(int cityId, long buildId, UnionHelper helper) {
		RoleCityAgent cityAgent = getCity(cityId);
		if (cityAgent == null) {
			GameLog.error("getCity is fail");
			return;
		}
		Map<Long, List<UnionHelper>> unionHelpers = cityAgent.getUnionHelpers();
		if (unionHelpers.get(buildId) == null) { // 创建个新的对象加入第一个帮助的人
			List<UnionHelper> helpers = new ArrayList<>();
			helpers.add(helper);
			unionHelpers.put(buildId, helpers);
		} else { // 加入帮助的人
			List<UnionHelper> helpers = unionHelpers.get(buildId);
			helpers.add(helper);
		}
	}

	/**
	 * 清除该建筑的被帮助的次数
	 * 
	 * @param cityId
	 * @param buildId
	 */
	public void clearBuildHelpers(int cityId, long buildId) {
		RoleCityAgent cityAgent = getCity(cityId);
		if (cityAgent == null) {
			GameLog.error("getCity is fail");
			return;
		}
		Map<Long, List<UnionHelper>> unionHelpers = cityAgent.getUnionHelpers();
		if (unionHelpers.get(buildId) != null) { // 清除对应的 建筑的ID 的被帮助的用户列表
			List<UnionHelper> list = unionHelpers.get(buildId);
			list.clear();
		}
	}
	
	/**
	 * 下发用户红包信息
	 */
	public void sendRoleRedpackets() {
		RespModuleSet rms = new RespModuleSet();
		roleRedpackets.sendClient(rms);
		MessageSendUtil.sendModule(rms, userInfo);
	}

	public int getChargeSuccessNum() {
		return chargeSuccessNum;
	}

	public void setChargeSuccessNum(int chargeSuccessNum) {
		this.chargeSuccessNum = chargeSuccessNum;
	}
	
	/**
	 * 增加一个加入联盟的倒计时
	 */
	public void addJoinUnionTimer() {
		joinTimer = new TimerLast(TimeUtils.nowLong() / 1000, GameConfig.EXIT_UNION_TO_JOIN_CD_TIME,
				TimerLastType.TIME_JOIN_UNION);
		joinTimer.registTimeOver(new JoinUnionTimerFinish(joy_id));
	}
	
	/**
	 * 可以加入(没有加入倒计时)
	 * @return
	 */
	public boolean isCanJoinUnion() {
		return joinTimer == null;
	}
}
