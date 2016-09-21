package com.joymeng.slg.union;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Const;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.common.util.expression.ProtoExpression;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatSystemContent;
import com.joymeng.slg.domain.chat.ChatSystemContentType;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgType;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.chat.RoleChatMail;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Npccity;
import com.joymeng.slg.domain.map.fight.result.FightVersus;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GridType;
import com.joymeng.slg.domain.map.impl.dynamic.MassTroops;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.union.AttackerDamage;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionOther;
import com.joymeng.slg.domain.map.union.UnionFightTransformData;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.effect.EffectAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.mod.chat.ModBattleReportUpdate;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.data.Alliance;
import com.joymeng.slg.union.data.Alliancemembers;
import com.joymeng.slg.union.data.Allianceresearch;
import com.joymeng.slg.union.data.Allianceshop;
import com.joymeng.slg.union.data.Alliancetechlevel;
import com.joymeng.slg.union.data.Flageffects;
import com.joymeng.slg.union.data.RecordForGm;
import com.joymeng.slg.union.data.UnionPostType;
import com.joymeng.slg.union.impl.DonateButton;
import com.joymeng.slg.union.impl.MemberAssistance;
import com.joymeng.slg.union.impl.UnionApply;
import com.joymeng.slg.union.impl.UnionItem;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.union.impl.UnionMemberTechProgress;
import com.joymeng.slg.union.impl.UnionRecords;
import com.joymeng.slg.union.impl.UnionTech;
import com.joymeng.slg.union.impl.UnionTechFinish;
import com.joymeng.slg.world.GameConfig;

/**
 * 联盟体
 * 
 * @author tanyong
 *
 */
public class UnionBody extends AbstractObject implements Comparable<UnionBody> {
	long id;// 编号
	String name;// 名称
	String shortName;// 简称
	int position;// 位置 没有主城是盟主的位置 有主城是主城的位置
	String notice = "欢迎大家加入本联盟";// 公告
	String icon = "allianceFlag_red";// 图标
	String inNotice = "hi";
	String language = "CN";
	int level = 1;// 等级
	long score = 0;// 积分
	String createTime;// 创建时间
	String recruits = "";// 招募条件
	int assistanceIndex = 0;
	List<UnionMember> members = new ArrayList<UnionMember>();// 成员列表
	List<UnionApply> applys = new ArrayList<UnionApply>();// 申请列表
	List<Long> invites = new ArrayList<Long>();// 邀请列表
	List<MemberAssistance> assistances = new ArrayList<MemberAssistance>();// 帮助列表
	Map<String, UnionTech> unionTechMap = new ConcurrentHashMap<String, UnionTech>(); // 联盟科技
	TimerLast timers;// 科技倒计时
	String upgradeTechID = ""; // 当前正在升级的联盟科技 没有则为""
	Map<String, UnionItem> sysStore = new ConcurrentHashMap<String, UnionItem>(); // 系统商店
	Map<String, UnionItem> unionStore = new ConcurrentHashMap<String, UnionItem>(); // 联盟商店
	Map<Integer, String> unionTitle = new ConcurrentHashMap<Integer, String>(); // 联盟称谓
																				// Int-String:军衔-称谓
																				// 称谓:"0/1|***"
																				// 0:固化数据(读取StringContent)
																				// 1:用户自定义
	ConcurrentLinkedQueue<UnionRecords> unionRecords = new ConcurrentLinkedQueue<UnionRecords>(); // 联盟商店记录队列
	ConcurrentLinkedQueue<UnionRecords> unionBattleRecords = new ConcurrentLinkedQueue<UnionRecords>(); // 联盟战争记录队列
	ConcurrentLinkedQueue<UnionRecords> unionGeneralRecords = new ConcurrentLinkedQueue<UnionRecords>(); // 联盟常规记录队列
	long lastSaveTime = TimeUtils.nowLong();// 上一次保存时间
	UnionStaticInfo usInfo = new UnionStaticInfo();
	RecordForGm record = new RecordForGm();
	Map<Long, Long> notifyTimes = new HashMap<Long, Long>();// 公告通知事件
	int gmMemberNum = 0;// gm调试上限人数
	int gmTeachLevelUpTime = 0;// gm调试联盟科技升级时间
	int gmBuildCreateTime = 0;// gm调试联盟建筑建造时间
	int gmBuildLevelUpTime = 0;// gm调试联盟建筑升级时间
	int gmBuildDropTime = 0;// gm调试联盟建筑放弃时间
	int gmShareNum = 0;// gm调试联盟捐献的经验值

	public void init() {
		initUnionTech();
		initUnionTitle();
		initSysStore();
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setGmMemberNum(int gmMemberNum) {
		this.gmMemberNum = gmMemberNum;
	}

	public void setGmTeachLevelUpTime(int gmTeachLevelUpTime) {
		this.gmTeachLevelUpTime = gmTeachLevelUpTime;
	}

	public void setGmBuildCreateTime(int gmBuildCreateTime) {
		this.gmBuildCreateTime = gmBuildCreateTime;
	}

	public int getGmBuildCreateTime() {
		return gmBuildCreateTime;
	}

	public int getGmBuildDropTime() {
		return gmBuildDropTime;
	}

	public void setGmBuildDropTime(int gmBuildDropTime) {
		this.gmBuildDropTime = gmBuildDropTime;
	}

	public int getGmBuildLevelUpTime() {
		return gmBuildLevelUpTime;
	}

	public void setGmBuildLevelUpTime(int gmBuildLevelUpTime) {
		this.gmBuildLevelUpTime = gmBuildLevelUpTime;
	}

	public void setGmShareNum(int gmShareNum) {
		this.gmShareNum = gmShareNum;
	}

	public Map<Long, Long> getNotifyTimes() {
		return notifyTimes;
	}

	/**
	 * 初始化系统商店
	 */
	public void initSysStore() {
		sysStore.clear();
		List<Allianceshop> datas = dataManager.serachList(Allianceshop.class);
		if (datas == null) {
			GameLog.error("read allianceshop is fail !");
			return;
		}
		for (int i = 0; i < datas.size(); i++) {
			Allianceshop allianceshop = datas.get(i);
			if (allianceshop == null) {
				continue;
			}
			UnionItem item = new UnionItem(allianceshop.getId(), (byte) 1, allianceshop.getNum());
			sysStore.put(allianceshop.getId(), item);
		}
	}

	public void loadMembers() {
		String sql = "select * from " + TABLE_RED_ALERT_UNION_MEMBER + " where " + RED_ALERT_GENERAL_UNION_ID + "="
				+ id;
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasBySql(sql);
		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> data = datas.get(i);
			UnionMember member = new UnionMember();
			member.loadFromData(new SqlData(data));
			addMember(member);
		}
	}

	public RecordForGm getRecord() {
		return record;
	}

	public void setRecord(RecordForGm record) {
		this.record = record;
	}

	public Map<Integer, String> getUnionTitle() {
		return unionTitle;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setUnionTitle(Map<Integer, String> unionTitle) {
		this.unionTitle = unionTitle;
	}

	public Map<String, UnionItem> getSysStore() {
		return sysStore;
	}

	public void setSysStore(Map<String, UnionItem> sysStore) {
		this.sysStore = sysStore;
	}

	public Map<String, UnionItem> getUnionStore() {
		return unionStore;
	}

	public void setUnionStore(Map<String, UnionItem> unionStore) {
		this.unionStore = unionStore;
	}

	public int getAssistanceIndex() {
		return assistanceIndex;
	}

	public void setAssistanceIndex(int assistanceIndex) {
		this.assistanceIndex = assistanceIndex;
	}

	public List<MemberAssistance> getAssistances() {
		return assistances;
	}

	public ConcurrentLinkedQueue<UnionRecords> getUnionGeneralRecords() {
		return unionGeneralRecords;
	}

	public void setUnionGeneralRecords(ConcurrentLinkedQueue<UnionRecords> unionGeneralRecords) {
		this.unionGeneralRecords = unionGeneralRecords;
	}

	public List<Long> getInvites() {
		return invites;
	}

	public void setInvites(List<Long> invites) {
		this.invites = invites;
	}

	public void setAssistances(List<MemberAssistance> assistances) {
		this.assistances = assistances;
	}

	public Map<String, UnionTech> getUnionTechMap() {
		return unionTechMap;
	}

	public void setUnionTechMap(Map<String, UnionTech> unionTechMap) {
		this.unionTechMap = unionTechMap;
	}

	public ConcurrentLinkedQueue<UnionRecords> getUnionBattleRecords() {
		return unionBattleRecords;
	}

	public void setUnionBattleRecords(ConcurrentLinkedQueue<UnionRecords> unionBattleRecords) {
		this.unionBattleRecords = unionBattleRecords;
	}

	public TimerLast getTimers() {
		return timers;
	}

	public void setTimers(TimerLast timers) {
		this.timers = timers;
	}

	public String getUpgradeTechID() {
		return upgradeTechID;
	}

	public void setUpgradeTechID(String upgradeTechID) {
		this.upgradeTechID = upgradeTechID;
	}

	enum MEMBER_POST_TYPE {
		POST_NONE, // 没有意义
		POST_OFFICER, // 官员
		POST_NORMAL;// 普通成员
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getRecruits() {
		return recruits;
	}

	public void setRecruits(String recruits) {
		this.recruits = recruits;
	}

	public List<UnionApply> getApplys() {
		return applys;
	}

	public void setApplys(List<UnionApply> applys) {
		this.applys = applys;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}
	
	public String getInNotice() {
		return inNotice;
	}

	public void setInNotice(String inNotice) {
		this.inNotice = inNotice;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<UnionMember> getMembers() {
		return members;
	}

	public void setMembers(List<UnionMember> members) {
		this.members = members;
	}

	public ConcurrentLinkedQueue<UnionRecords> getUnionRecords() {
		return unionRecords;
	}

	public void setUnionRecords(ConcurrentLinkedQueue<UnionRecords> unionRecords) {
		this.unionRecords = unionRecords;
	}

	public UnionStaticInfo getUsInfo() {
		return usInfo;
	}

	public void setUsInfo(UnionStaticInfo usInfo) {
		this.usInfo = usInfo;
	}

	/**
	 * 获取全部的联盟记录
	 * 
	 * @return
	 */
	public ConcurrentLinkedQueue<UnionRecords> getAllUnionRecords() {
		ConcurrentLinkedQueue<UnionRecords> allRecords = new ConcurrentLinkedQueue<UnionRecords>();
		for (UnionRecords unionRecord : unionRecords) {
			allRecords.add(unionRecord);
		}
		for (UnionRecords unionRecord : unionBattleRecords) {
			allRecords.add(unionRecord);
		}
		for (UnionRecords unionRecord : unionGeneralRecords) {
			allRecords.add(unionRecord);
		}
		return allRecords;
	}

	@Override
	public int compareTo(UnionBody o) {
		return 0;
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_UNION;
	}

	@Override
	public String[] wheres() {
		return new String[] { RED_ALERT_GENERAL_ID };
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void loadFromData(SqlData data) {
		id = data.getLong(RED_ALERT_GENERAL_ID);
		icon = data.getString(RED_ALERT_UNION_ICON);
		name = data.getString(RED_ALERT_GENERAL_NAME);
		shortName = data.getString(RED_ALERT_UNION_SHORT_NAME);
		int positionStr = data.getInt(RED_ALERT_UNION_POSITION);
		if (positionStr == 0) {
			GameLog.info("old unionData is error,set default 99999");
			position = 99999;
		} else {
			position = positionStr;
		}
		language = data.getString(RED_ALERT_UNION_LANGUAGE);
		if (StringUtils.isNull(language)) {
			language = "CN";
		}
		notice    = data.getString(RED_ALERT_UNION_NOTICE);
		inNotice  = data.getString(RED_ALERT_UNION_IN_NOTICE);
		if (StringUtils.isNull(inNotice)) {
			inNotice = "hi";
		}
		level     = data.getInt(RED_ALERT_GENERAL_LEVEL);
		score     = data.getLong(RED_ALERT_UNION_SCORE);

		Timestamp ts = data.getTimestamp(RED_ALERT_GENERAL_CREATE_TIME);
		createTime = ts.toString().substring(0, 19);
		String upgradeTechIdString = data.getString(RED_ALERT_UNION_UPGRADETECHID);
		upgradeTechID = upgradeTechIdString == null ? "" : upgradeTechIdString;
		String str = data.getString(RED_ALERT_UNION_RECRUITS);
		if (!str.equals("null")) {
			recruits = data.getString(RED_ALERT_UNION_RECRUITS);
		}
		String timersStr = data.getString(DaoData.RED_ALERT_UNION_TIMERS);
		if (!upgradeTechID.equals("")) {
			timers = JsonUtil.JsonToObject(timersStr, TimerLast.class);
			timers.registTimeOver(new UnionTechFinish(this, upgradeTechID));
		}
		String unionTechString = data.getString(RED_ALERT_UNION_TECH);
		if (!StringUtils.isNull(unionTechString)) {
			unionTechMap = JSON.parseObject(unionTechString, new TypeReference<Map<String, UnionTech>>() {
			});
		} else { // 初始化所有的联盟科技
			initUnionTech();
		}
		String sysStoreString = data.getString(RED_ALERT_UNION_SYSTEM_STORE);
		if (!StringUtils.isNull(sysStoreString)) {
			sysStore = JSON.parseObject(sysStoreString, new TypeReference<Map<String, UnionItem>>() {
			});
		} else {
			initSysStore();// 初始化联盟系统商店
		}
		String unionStoreString = data.getString(RED_ALERT_UNION_STORE);
		if (!StringUtils.isNull(unionStoreString) && !unionStoreString.equals("null")) {
			unionStore = JSON.parseObject(unionStoreString, new TypeReference<Map<String, UnionItem>>() {
			});
		}
		String unionTitleStrings = data.getString(RED_ALERT_UNION_TITLE);
		if (StringUtils.isNull(unionTitleStrings) || unionTitleStrings.equals("null")) {
			initUnionTitle();
		} else {
			unionTitle = JSON.parseObject(unionTitleStrings, new TypeReference<Map<Integer, String>>() {
			});
		}
		String unionRecordStrings = data.getString(RED_ALERT_UNION_RECORDS);
		if ((!StringUtils.isNull(unionRecordStrings)) && (!unionRecordStrings.equals("null"))) {
			String[] uRsStrings = unionRecordStrings.split(",");
			for (int i = 0; i < uRsStrings.length; i += 5) {
				byte recordType = Byte.valueOf(uRsStrings[i]);
				byte colorType = Byte.valueOf(uRsStrings[i + 1]);
				String recordContent = uRsStrings[i + 2];
				String recordContentPara = uRsStrings[i + 3];
				long recordTime = Long.valueOf(uRsStrings[i + 4]);
				UnionRecords record = new UnionRecords(recordType, colorType, recordContent, recordContentPara,
						recordTime);
				addOneUnionRecord(record);
			}
		}
		String unionBattleRecordStrings = data.getString(RED_ALERT_UNION_BALLTE_RECORDS);
		if ((!StringUtils.isNull(unionBattleRecordStrings)) && (!unionBattleRecordStrings.equals("null"))) {
			String[] uRsStrings = unionBattleRecordStrings.split(",");
			for (int i = 0; i < uRsStrings.length; i += 5) {
				byte recordType = Byte.valueOf(uRsStrings[i]);
				byte colorType = Byte.valueOf(uRsStrings[i + 1]);
				String recordContent = uRsStrings[i + 2];
				String recordContentPara = uRsStrings[i + 3];
				long recordTime = Long.valueOf(uRsStrings[i + 4]);
				UnionRecords record = new UnionRecords(recordType, colorType, recordContent, recordContentPara,
						recordTime);
				addOneUnionBattleRecord(record);
			}
		}
		String unionGeneralRecordStrings = data.getString(RED_ALERT_UNION_GENERAL_RECORDS);
		if ((!StringUtils.isNull(unionGeneralRecordStrings)) && (!unionGeneralRecordStrings.equals("null"))) {
			String[] uRsStrings = unionGeneralRecordStrings.split(",");
			for (int i = 0; i < uRsStrings.length; i += 5) {
				byte recordType = Byte.valueOf(uRsStrings[i]);
				byte colorType = Byte.valueOf(uRsStrings[i + 1]);
				String recordContent = uRsStrings[i + 2];
				String recordContentPara = uRsStrings[i + 3];
				long recordTime = Long.valueOf(uRsStrings[i + 4]);
				UnionRecords record = new UnionRecords(recordType, colorType, recordContent, recordContentPara,
						recordTime);
				addOneUnionGeneralRecord(record);
			}
		}
		String staticStr = data.getString(RED_ALERT_UNION_STATIC_INFO);
		if (!StringUtils.isNull(staticStr)) {
			usInfo = JsonUtil.JsonToObject(staticStr, UnionStaticInfo.class);
		}
		String rec = data.getString(RED_ALERT_UNION_FIGHT_RECORD);
		if (!StringUtils.isNull(rec)) {
			record = JsonUtil.JsonToObject(staticStr, RecordForGm.class);
		}
	}

	public synchronized void addOneUnionRecord(UnionRecords records) {
		if (unionRecords.size() > GameConfig.UNION_RECORD_MAX) {
			unionRecords.poll();
		}
		unionRecords.add(records);
	}

	public synchronized void addOneUnionBattleRecord(UnionRecords records) {
		if (unionBattleRecords.size() > GameConfig.UNION_RECORD_MAX) {
			unionBattleRecords.poll();
		}
		unionBattleRecords.add(records);
	}

	public synchronized void addOneUnionGeneralRecord(UnionRecords records) {
		if (unionGeneralRecords.size() > GameConfig.UNION_RECORD_MAX) {
			unionGeneralRecords.poll();
		}
		unionGeneralRecords.add(records);
	}

	/**
	 * 初始化联盟称谓
	 */
	public void initUnionTitle() {
		List<Alliancemembers> ases = new ArrayList<Alliancemembers>();
		ases = dataManager.serachList(Alliancemembers.class);
		if (ases.size() < 1) {
			GameLog.error("read alliancemember is fail!");
			return;
		}
		for (int i = 0; i < ases.size(); i++) {
			Alliancemembers alliancemembers = ases.get(i);
			unionTitle.put(alliancemembers.getRank(), "0|" + alliancemembers.getName());
		}
	}

	/**
	 * 初始化联盟科技
	 */
	public void initUnionTech() {
		List<Tech> techList = dataManager.serachList(Tech.class, new SearchFilter<Tech>() {
			@Override
			public boolean filter(Tech data) {
				if (data.getTechTreeID().equals("3")) {
					return true;
				} else {
					return false;
				}
			}
		});
		if (techList == null) {
			GameLog.error("FAIL!!! init uniontech is fail!");
			return;
		}
		for (Tech tech : techList) {
			UnionTech tempUnionTech = new UnionTech();
			tempUnionTech.setTechId(tech.getId());
			tempUnionTech.setTechlevel(0);
			tempUnionTech.setCurrentExp(0);
			unionTechMap.put(tempUnionTech.getTechId(), tempUnionTech);
		}
	}

	@Override
	public void save() {
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			member.save();
		}
		List<MapUnionCity> citys = mapWorld.searchUnionCity(id);
		for (int i = 0; i < citys.size(); i++) {
			MapUnionCity city = citys.get(i);
			city.save();
		}
		taskPool.saveThread.addSaveData(this);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID, id);
		data.put(RED_ALERT_GENERAL_NAME, name);
		data.put(RED_ALERT_UNION_SHORT_NAME, shortName);
		data.put(RED_ALERT_UNION_POSITION, position);
		data.put(RED_ALERT_UNION_LANGUAGE, language);
		data.put(RED_ALERT_UNION_ICON,icon);
		data.put(RED_ALERT_UNION_NOTICE,notice);
		data.put(RED_ALERT_UNION_IN_NOTICE,inNotice);
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_UNION_SCORE,score);
		data.put(RED_ALERT_GENERAL_CREATE_TIME,createTime);

		String str = null;
		if (recruits == null) {
			str = "null";
		} else {
			str = recruits;
		}
		data.put(RED_ALERT_UNION_RECRUITS, str);
		data.put(RED_ALERT_UNION_TECH, JsonUtil.ObjectToJsonString(unionTechMap));
		String timersStr = JsonUtil.ObjectToJsonString(timers);
		data.put(DaoData.RED_ALERT_UNION_TIMERS, timersStr);
		data.put(DaoData.RED_ALERT_UNION_UPGRADETECHID, upgradeTechID);
		data.put(RED_ALERT_UNION_SYSTEM_STORE, JsonUtil.ObjectToJsonString(sysStore));
		data.put(RED_ALERT_UNION_STORE, JsonUtil.ObjectToJsonString(unionStore));
		data.put(RED_ALERT_UNION_TITLE, JsonUtil.ObjectToJsonString(unionTitle));

		StringBuffer recordSb = new StringBuffer();
		for (UnionRecords records : unionRecords) {
			recordSb.append(records.getRecordType() + ",");
			recordSb.append(records.getColorType() + ",");
			recordSb.append(records.getUnionRecordContent() + ",");
			recordSb.append(records.getUnionRecordPara() + ",");
			recordSb.append(records.getUnionRecordTime() + ",");
		}
		if (recordSb.length() > 1) {
			recordSb.deleteCharAt(recordSb.length() - 1);
		}
		data.put(RED_ALERT_UNION_RECORDS, recordSb.toString());

		StringBuffer bRecordSb = new StringBuffer();
		for (UnionRecords records : unionBattleRecords) {
			bRecordSb.append(records.getRecordType() + ",");
			bRecordSb.append(records.getColorType() + ",");
			bRecordSb.append(records.getUnionRecordContent() + ",");
			bRecordSb.append(records.getUnionRecordPara() + ",");
			bRecordSb.append(records.getUnionRecordTime() + ",");
		}
		if (bRecordSb.length() > 1) {
			bRecordSb.deleteCharAt(bRecordSb.length() - 1);
		}
		data.put(RED_ALERT_UNION_BALLTE_RECORDS, bRecordSb.toString());
		StringBuffer gRecordSb = new StringBuffer();
		for (UnionRecords records : unionGeneralRecords) {
			gRecordSb.append(records.getRecordType() + ",");
			gRecordSb.append(records.getColorType() + ",");
			gRecordSb.append(records.getUnionRecordContent() + ",");
			gRecordSb.append(records.getUnionRecordPara() + ",");
			gRecordSb.append(records.getUnionRecordTime() + ",");
		}
		if (gRecordSb.length() > 1) {
			gRecordSb.deleteCharAt(gRecordSb.length() - 1);
		}
		data.put(RED_ALERT_UNION_GENERAL_RECORDS, gRecordSb.toString());
		// data.put(RED_ALERT_UNION_STATIC_INFO, usInfo.serialize());
		data.put(RED_ALERT_UNION_STATIC_INFO, JsonUtil.ObjectToJsonString(usInfo));
		data.put(RED_ALERT_UNION_FIGHT_RECORD, JsonUtil.ObjectToJsonString(record));
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public void _tick(long now) {
		if (!upgradeTechID.equals("") && timers != null && timers.over(now)) {
			timers.die();
			GameLog.info("run timer finish timer type=" + timers.getType().getKey());
		}
		if (now > lastSaveTime + Const.MINUTE * 15) {
			// 15分钟自动保存
			save();
			lastSaveTime = now;
		}
	}

	@Override
	public void remove() {
		setDeleteFlag(true);
		save();
		super.remove();
	}

	public String getLeaderName() {
		UnionMember leader = getLeader();
		return leader == null ? "" : leader.getName();
	}

	public UnionMember getLeader() {
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.isLeader()) {
				return member;
			}
		}
		return null;
	}

	public synchronized boolean memberExit(long id) {
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.getUid() == id) {
				Role role = world.getRole(id);
				if (role == null) {
					GameLog.error("getRole is error id = " + id);
					return false;
				}
				updateUnionCityBuff(role, false);
				member.destroy(role);
				members.remove(member);
				String recordPara = member.getName();
				long recordTime = TimeUtils.nowLong() / 1000;
				UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
						UnionRecords.UNION_GENERAL_RECORD_COLOR_RED, UnionRecords.CONTENT_TYPE_ALLIAN_EXIT_MEMBER,
						recordPara, recordTime);
				addOneUnionGeneralRecord(record);
				String systemChatPara = recordPara.replace(":", "|");
				ChatSystemContent systemContent = new ChatSystemContent(
						ChatSystemContentType.CONTENT_TYPE_ALLIAN_EXIT_MEMBER, systemChatPara);
				String chatContent = JsonUtil.ObjectToJsonString(systemContent);
				chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
				// TODO 联盟总战斗力变化
				usInfo.updateUnionFight(member.getFight() * -1);
				// 删除联盟帮助列表中自己请求的
				removeAssistances(id);
				sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_DEL);
				sendMeToAllMembers(0);
				sendUnionRecordsToAllMembers();
				removeMemberAllUnionTechBuff(role);
				removeMemberAllUnionCityBuff(role);
				NewLogManager.unionLog(role, "quit_alliance");
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除联盟帮助列表中 uid请求的
	 * 
	 * @param uid
	 */
	private synchronized void removeAssistances(long uid) {
		for (int j = 0; j < assistances.size(); j++) {
			MemberAssistance assistance = assistances.get(j);
			if (assistance == null) {
				continue;
			}
			if (assistance.getUid() == uid) {
				assistances.remove(j--);
			}
		}
	}

	public boolean checkLeader(long id) {
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.isLeader()) {
				return member.getUid() == id;
			}
		}
		return false;
	}

	public synchronized void addMember(UnionMember member) {
		if (member == null || members.contains(member)) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember um = members.get(i);
			if (um.getUid() == member.getUid()) {
				return;
			}
		}
		members.add(member);
	}

	public UnionMember searchMember(long uid) {
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.getUid() == uid) {
				return member;
			}
		}
		return null;
	}

	public RespModuleSet sendToClient() {
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms);
		return rms;
	}

	public void sendToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_BODY;
			}
		};

		module.add(id);//long 联盟编号
		module.add(name);//string 联盟名称
		module.add(shortName);//string 联盟简称
		module.add(position);//int 联盟的位置★
		module.add(language);//string 语言
		module.add(icon);//string 联盟图标
		module.add(notice);//string 联盟公告
		module.add(inNotice);//string 联盟内部公告
		module.add(level);//int 联盟等级
		module.add(score);//long 联盟积分
		module.add(recruits==null ? "" : recruits);//sting 招募表达式
		List<MapUnionCity> cities = mapWorld.searchUnionCity(id);// 联盟城市的列表
		int size = cities.size();
		module.add(size);
		for (int i = 0; i < size; i++) {
			MapUnionCity unionCity = cities.get(i);
			if (unionCity == null) {
				continue;
			}
			module.add(unionCity.getKey());// 城市的固化表ID
		}
		module.add(members);//list 成员
		module.add(getMemberMaxNum());//int 成员上限
		module.add(usInfo.getUnionFight() / 1000 + (usInfo.getUnionFight() / 1000.0 > 0 ? 1 : 0));//long 战斗力
		module.add(applys);//int 申请列表
		module.add(assistances);//int 帮组列表		
		module.add(unionTitle.size()); //联盟称谓的个数
		for (int rank : unionTitle.keySet()) {
			module.add(rank); // 军衔 int
			module.add(unionTitle.get(rank)); // 对应的称谓 string
		}
		sendUnionCityInfo(module);
		rms.addModule(module);
	}

	private void sendUnionCityInfo(AbstractClientModule module) {
		List<MapUnionCity> citys = mapWorld.searchUnionCity(id);
		int maxLevel = 0, storageLevel = 0;
		String key = BuildName.MAP_UNION_STORAGE.getKey();
		for (int i = 0; i < citys.size(); i++) {
			MapUnionCity city = citys.get(i);
			if (city.getLevel() > maxLevel) {
				maxLevel = city.getLevel();
			}
			List<MapUnionBuild> builds = city.search(key);
			for (int j = 0; j < builds.size(); j++) {
				MapUnionBuild build = builds.get(j);
				if (build != null) {
					storageLevel = Math.max(storageLevel, build.getLevel());
				}
			}
		}
		module.add(maxLevel);//联盟的NPC城的最高等级 int
		module.add(storageLevel);//联盟最高仓库等级
	}
	
	public void listResp(CommunicateResp resp){
		resp.add(id);//long 
		resp.add(position);//int
		resp.add(name);//string
		resp.add(shortName);//string
		resp.add(language); //string
		resp.add(notice);//string
		resp.add(getLeaderName());//string
		resp.add(getLeader() == null ? (byte) 0 : getLeader().getIcon().getIconType());// byte
		resp.add(getLeader() == null ? (byte) 0 : getLeader().getIcon().getIconId());// byte
		resp.add(getLeader() == null ? "" : getLeader().getIcon().getIconName());// string
		resp.add(icon);//sting
		resp.add(recruits);//string ★
		resp.add(members.size());//int 当前成员数
		resp.add(getMemberMaxNum());//int 最大成员数
		resp.add(usInfo.getUnionFight() / 1000 + (usInfo.getUnionFight() / 1000.0 > 0 ? 1 : 0));// int战斗力
		resp.add(level);// int 等级
		// 已申请的成员编号列表
		resp.add(applys.size());// int 已申请的成员编号个数
		for (int i = 0; i < applys.size(); i++) {
			UnionApply apply = applys.get(i);
			resp.add(apply.getUid());// long 已申请的成员的编号
		}
	}

	public Alliance getData(final int level) {
		Alliance alliabce = dataManager.serach(Alliance.class, new SearchFilter<Alliance>() {
			@Override
			public boolean filter(Alliance data) {
				return data.getLevel() == level;
			}
		});
		return alliabce;
	}

	public int getMemberMaxNum() {
		if (gmMemberNum > 0) {
			return gmMemberNum;
		}
		Alliance alliabce = getData(level);
		List<String> techs = new ArrayList<>(Arrays.asList("Tech181", "Tech186", "Tech192", "Tech201"));
		int techMembermaxNum = 0;
		for (int i = 0; i < techs.size(); i++) {
			final String techId = techs.get(i);
			final int techLevel = unionTechMap.get(techId).getTechlevel();
			// 这里还要计算联盟科技的加成
			if (techLevel > 0) {
				Techupgrade techupgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
					@Override
					public boolean filter(Techupgrade data) {
						return (data.getTechID().equals(techId) && data.getLevel() == techLevel);
					}
				});
				if (techupgrade == null || techupgrade.getBuffList().size() < 2) {
					continue;
				}
				techMembermaxNum += Integer.valueOf(techupgrade.getBuffList().get(1));
			}
		}
		return alliabce.getNum() + techMembermaxNum;
	}

	public boolean isFull() {
		return members.size() >= getMemberMaxNum();
	}

	public long getFight() {
		return usInfo.getUnionFight();
	}

	/**
	 * 添加一个联盟科技倒计时
	 * 
	 * @param last
	 * @param type
	 * @return
	 */
	public TimerLast addUnionTechTimer(long last, TimerLastType type, String upgradeTechIdString) {
		timers = new TimerLast(TimeUtils.nowLong() / 1000, last, type);
		upgradeTechID = upgradeTechIdString;
		return timers;
	}

	/**
	 * 移除科技升级的的timer
	 * 
	 * @param type
	 * @return
	 */
	public boolean removeUnionTechTimer() {
		timers = null;
		upgradeTechID = "";
		return true;
	}

	/**
	 * 解散联盟
	 */
	public synchronized void dissolve() {
		List<MapUnionCity> citys = mapWorld.searchUnionCity(id);
		for (int i = 0; i < members.size(); i++) {
			Role role = world.getObject(Role.class, members.get(i).getUid());
			updateUnionCityBuff(role, false);
		}
		for (int i = 0; i < citys.size(); i++) {
			MapUnionCity city = citys.get(i);
			if (city.getUnionId() == id) {
				city.giveUpOver(null);
			}
		}
		sendViewsToAllMember();
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getRole(member.getUid());
			member.destroy(role);
			if (role != null) {
				removeMemberAllUnionTechBuff(role);
				removeMemberAllUnionCityBuff(role);
			}
		}
		members.clear();
		remove();
	}

	public void sendMeToAllMembers(long except) {
		RespModuleSet rms = sendToClient();
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.getUid() == except) {
				continue;
			}
			Role role = world.getOnlineRole(member.getUid());
			if (role != null && role.isOnline()) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	public void sendMemberToAllMembers(UnionMember target, byte type) {
		RespModuleSet rms = target.sendToClient(type);
		if (rms == null) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	/**
	 * 对联盟成员群发一份邮件
	 * 
	 * @param reportContent
	 * @param chatType
	 */
	public void sendMailToAllMembers(String reportContent, MsgType msgType) {
		RespModuleSet rms = new RespModuleSet();
		ChannelType channelType = ChannelType.SYSTEM_REPORT;
		ChatMsg msg = new ChatMsg(reportContent, MsgTextColorType.COLOR_BLACK, channelType, msgType,
				ReportType.TYPE_DEFAULT, null, null);
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			RoleChatMail roleMails = chatMgr.roleMail.get(role.getId());
			if (roleMails == null) {
				roleMails = new RoleChatMail();
				chatMgr.roleMail.put(role.getId(), roleMails);
			}
			roleMails.addRoleMail(msg);
			rms.addModule(new ModBattleReportUpdate(msg));
			if (role.isOnline()) { // 在线,直接发给用户
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	/**
	 * 打包联盟科技所需内容
	 * 
	 * @param rms
	 * @param unionTechList
	 */
	public void sendUnionTechToClient(RespModuleSet rms, List<UnionTech> unionTechList, TimerLast timer,
			String upgradeTechId) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_TECH;
			}
		};
		upgradeTechId = upgradeTechId == null ? "" : upgradeTechId;
		module.add(upgradeTechId); // 正在升级的科技ID
		if (!upgradeTechId.equals("")) {
			timer.sendToClient(module.getParams()); // 科技的倒计时
		}
		module.add(unionTechList.size()); // 科技的数目
		Collections.sort(unionTechList, new Comparator<UnionTech>() {
			@Override
			public int compare(UnionTech o1, UnionTech o2) {
				Tech t1 = dataManager.serach(Tech.class, o1.getTechId());
				Tech t2 = dataManager.serach(Tech.class, o2.getTechId());
				if (t1 == null || t2 == null) {
					GameLog.error("json isnt mysql data --- fail");
					return 0;
				}
				return t1.getRanknumber() == t2.getRanknumber() ? 0
						: (t1.getRanknumber() < t2.getRanknumber() ? -1 : 1);
			}
		});
		for (int i = 0; i < unionTechList.size(); i++) {
			UnionTech unionTech = unionTechList.get(i);
			module.add(unionTech.getTechId()); // 科技的Id
			module.add(unionTech.getTechlevel()); // 科技的等级
			module.add(unionTech.getCurrentExp()); // 科技的经验
		}
		rms.addModule(module);
	}

	/**
	 * 打包联盟记录所需内容
	 * 
	 * @param rms
	 * @param unionTechList
	 */
	public void sendUnionRecordsToClient(RespModuleSet rms, ConcurrentLinkedQueue<UnionRecords> sendRecords) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_RECORDS;
			}
		};
		if (sendRecords.size() > 0) {
			module.add(sendRecords.size()); // 个数 int
			for (UnionRecords record : sendRecords) {
				module.add(record.getRecordType()); // 记录的类型//1-个人 2-联盟
				module.add(record.getColorType()); // 记录的颜色 1 红色 2 绿色 3 蓝色
				module.add(record.getUnionRecordContent()); // 记录内容 string
				module.add(record.getUnionRecordPara()); // 记录的参数
				module.add(record.getUnionRecordTime()); // 记录时间 long
			}
			rms.addModule(module);
		}
	}

	/**
	 * 发送联盟记录到每个联盟成员
	 * 
	 * @param rms
	 * @param arrayList
	 */
	public void sendUnionRecordsToAllMembers() {
		RespModuleSet rms = new RespModuleSet();
		sendUnionRecordsToClient(rms, getAllUnionRecords());
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	/**
	 * 发送联盟科技到每个联盟成员
	 * 
	 * @param rms
	 * @param arrayList
	 */
	public void sendUnionTechToAllMembers(RespModuleSet rms, List<UnionTech> arrayList, TimerLast timer,
			String upgradeTechId) {
		sendUnionTechToClient(rms, arrayList, timer, upgradeTechId);
		if (rms == null) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	public synchronized boolean tryToApply(Role role, CommunicateResp resp) {
		if (searchApply(role.getId()) != null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_ROLE_HAS_APPLIED);
			return false;
		}
		// 条件判断
		if (!StringUtils.isNull(recruits)) {// 非开启公开招募
			if (isFull()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_FULL);
				return false;
			}
			String str = recruits.replaceAll("uid", String.valueOf(role.getId()));
			try {
				Object obj = ProtoExpression.ExecuteExpression(str);
				boolean flag = Boolean.parseBoolean(obj.toString());
				if (!flag) {// 不满足招募条件
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NOT_MEET_REQUIREMENT);
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			UnionApply apply = new UnionApply();
			apply.setUid(role.getId());
			apply.setName(role.getName());
			apply.getIcon().copy(role.getIcon());
			apply.setFight(role.getFightPower());
			applys.add(apply);
			resp.add(1);
			listResp(resp);
			sendMeToAllMembers(0);
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_APPLY_SUC);
		} else {// 开启公开招募
			Alliancemembers stand = computeMilitary(0);
			if (stand == null) {
				return false;
			}
			if (isFull()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_FULL);
				return false;
			}
			RespModuleSet rms = new RespModuleSet();
			if (!tryToAddmember(rms, role, stand)) {
				return false;
			}
			if (role.isOnline()) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
			resp.add(0);
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NEW_MEMBER_IN, name);
		}
		return true;
	}

	private UnionApply searchApply(long uid) {
		for (int i = 0; i < applys.size(); i++) {
			UnionApply apply = applys.get(i);
			if (apply.getUid() == uid) {// 已申请过 了
				return apply;
			}
		}
		return null;
	}

	/**
	 * 根据累计贡献获取军衔等级
	 * 
	 * @param score
	 * @return
	 */
	public Alliancemembers computeMilitary(long score) {
		List<Alliancemembers> alliances = dataManager.serachList(Alliancemembers.class,
				new SearchFilter<Alliancemembers>() {
					@Override
					public boolean filter(Alliancemembers data) {
						return data.getType() == MEMBER_POST_TYPE.POST_NORMAL.ordinal();// 非官员
					}
				});
		for (int i = 0; i < alliances.size(); i++) {
			Alliancemembers alliance = alliances.get(i);
			List<String> contributions = alliance.getPersContr();
			int a = Integer.parseInt(contributions.get(0));
			int b = Integer.parseInt(contributions.get(1));
			if (score >= Math.min(a, b) && score <= Math.max(a, b)) {
				return alliance;
			}
		}
		return null;
	}

	public synchronized boolean tryToAddmember(RespModuleSet rms, Role role, Alliancemembers stand) {
		UnionMember member = createmMember(role, stand);
		role.handleEvent(GameEvent.UNION_JOIN, this);
		MapUtil.updateUnionBuidlBuff(role, id, true);
		addMember(member);
		if (role.isOnline() && rms != null) {
			role.sendRoleToClient(rms);
		}
		usInfo.updateUnionFight(role.getRoleStatisticInfo().getRoleFight());
		member.setFight(role.getRoleStatisticInfo().getRoleFight());
		sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_ADD);
		sendMeToAllMembers(0);
		// 联盟记录
		String recordPara = role.getName();
		long recordTime = TimeUtils.nowLong() / 1000;
		String systemChatPara = "";
		ChatSystemContent systemContent = null;
		if (member.getAllianceKey().equals("1")) { // 为盟主
			UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
					UnionRecords.UNION_GENERAL_RECORD_COLOR_GREEN, UnionRecords.CONTENT_TYPE_MEMBER_CREATE_UNION,
					recordPara, recordTime);
			addOneUnionGeneralRecord(record);
			// 发送联盟频道通知
			systemChatPara = recordPara.replace(":", "|");
			systemContent = new ChatSystemContent(ChatSystemContentType.CONTENT_TYPE_MEMBER_CREATE_UNION,
					systemChatPara);
		} else {
			UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
					UnionRecords.UNION_GENERAL_RECORD_COLOR_GREEN, UnionRecords.CONTENT_TYPE_ALLIAN_ADD_MEMBER,
					recordPara, recordTime);
			addOneUnionGeneralRecord(record);
			// 发送联盟频道通知
			systemChatPara = recordPara.replace(":", "|");
			systemContent = new ChatSystemContent(ChatSystemContentType.CONTENT_TYPE_ALLIAN_ADD_MEMBER, systemChatPara);
		}
		String chatContent = JsonUtil.ObjectToJsonString(systemContent);
		chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
		sendUnionTech(role, rms); // 发送商店科技
		sendUnionStore(role, rms); // 发送联盟商店
		sendUnionRecordsToAllMembers(); // 发送联盟记录
		sendMemberTechProgress(role, rms); // 发送个人对应联盟科技的捐赠按钮
		updataMemberAllUnionTechBuff(role);// 更新科技提供的buff
		updateUnionCityBuff(role,true);// 更新联盟城市buff
		// 任务事件
		for (Role user : world.getOnlineRoles()) {
			user.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_KING_UNIONFIGHT, 0);
		}
		for (UnionMember uMember : getMembers()) {
			Role memrole = world.getObject(Role.class, uMember.getUid());
			if (memrole != null) {
				memrole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_ADD, id,
						members.size());
			}
		}
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_LVLUP, level);
		sendViewsToAllMember();
		NewLogManager.unionLog(role, "join_alliance");
		return true;
	}

	public synchronized boolean tryToVerification(Role role, long uid, byte flag) {
		// 判断权限
		UnionApply apply = searchApply(uid);
		UnionMember operator = searchMember(role.getId());
		if (apply == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_ON_APPLY);
			return false;
		}
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_VERIFICATION)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
			return false;
		}
		if (flag == 1) {
			Alliancemembers stand = computeMilitary(0);
			if (stand == null) {
				return false;
			}
			Role other = world.getRole(uid);
			if (isFull()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_FULL);
				return false;
			}
			if (other.getUnionId() != 0) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_HAVE_IN_OTHER);
				applys.remove(apply);
				sendMeToAllMembers(0);
				return false;
			}
			RespModuleSet rms = new RespModuleSet();
			if (!tryToAddmember(rms, other, stand)) {
				return false;
			}
			if (other.isOnline()) {
				MessageSendUtil.tipModule(rms, MessageSendUtil.TIP_TYPE_NORMAL, I18nGreeting.MSG_UNION_NEW_MEMBER_IN,
						"(" + shortName + ")" + name);
				MessageSendUtil.sendModule(rms, other.getUserInfo());
			}
			applys.remove(apply);
		} else {
			applys.remove(apply);
		}
		sendMeToAllMembers(0);
		return true;
	}

	private int getOfficerNum(int officeLevel) {
		int count = 0;
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.getOfficerLevel() == officeLevel) {
				count++;
			}
		}
		return count;
	}

	/*
	 * 联盟贡献度最高的（除了盟主）
	 */
	public long getMemberDemise() {
		long uid = 0;
		long score = 0;
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			if (member.getScore() >= score) {
				uid = member.getUid();
			}
		}
		return uid;
	}

	public synchronized boolean tryToAppoint(Role role, long uid, final int index) {
		// 判断权限
		UnionMember operator = searchMember(role.getId());
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_OFFICER_APPOINT)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
			return false;
		}
		UnionMember target = searchMember(uid);
		if (target == null) {
			GameLog.error("tryToAppoint taget unionMember is null");
			return false;
		}
		Alliancemembers t_alliance = null;
		if (index == 1) {// 禅让盟主
			if (!operator.checkPost(UnionPostType.UNION_POST_DEMISE)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			t_alliance = dataManager.serach(Alliancemembers.class, new SearchFilter<Alliancemembers>() {
				@Override
				public boolean filter(Alliancemembers data) {
					return data.getRank() == index;// 非官员
				}
			});
			Alliancemembers o_alliance = dataManager.serach(Alliancemembers.class, new SearchFilter<Alliancemembers>() {
				@Override
				public boolean filter(Alliancemembers data) {
					return data.getRank() == 5;// 非官员
				}
			});
			if (t_alliance == null || o_alliance == null) {
				GameLog.error("read Alliancemember is fail ---null");
				return false;
			}
			target.resetPermission(t_alliance);
			operator.resetPermission(o_alliance);
			// 更新其称谓
			if (o_alliance.getType() == 2) {
				operator.updateUnionMemberTitle();
			}
			// 联盟记录
			String recordPara = role.getName() + ":" + target.getName();
			long recordTime = TimeUtils.nowLong() / 1000;
			UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
					UnionRecords.UNION_GENERAL_RECORD_COLOR_BLUE, UnionRecords.CONTENT_TYPE_ALLIAN_ABDICATION,
					recordPara, recordTime);
			addOneUnionGeneralRecord(record);
			// 发送联盟频道消息
			String systemChatPara = recordPara.replace(":", "|");
			ChatSystemContent systemContent = new ChatSystemContent(
					ChatSystemContentType.CONTENT_TYPE_ALLIAN_ABDICATION, systemChatPara);
			String chatContent = JsonUtil.ObjectToJsonString(systemContent);
			chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
			// 发送全体邮件
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("content", chatContent);
			temp.put("iconName", icon);
			temp.put("title", ChatSystemContentType.UNION_ALL_MAILS_MEMBER_TITLE_CHANGE);
			temp.put("unionId", id);
			temp.put("type", 0);
			chatMgr.sendUnionNotice(role, JsonUtil.ObjectToJsonString(temp));
		} else if (index <= 4) {// 官员任职
			Alliancemembers m_alliance = operator.getData();
			if (m_alliance.getRank() > index) {// 只能把目标成员任命为比自己低的军衔
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			t_alliance = dataManager.serach(Alliancemembers.class, new SearchFilter<Alliancemembers>() {
				@Override
				public boolean filter(Alliancemembers data) {
					return data.getRank() == index;// 非官员
				}
			});
			Alliance alliance = getData(level);
			for (String str : alliance.getMembers()) {
				String[] ss = str.split(":");
				int sl = Integer.parseInt(ss[0]);
				int snum = Integer.parseInt(ss[1]);
				if (sl == index) {
					if (snum <= getOfficerNum(index)) {// 官员数量已满
						MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OFFICER_NUM);
						return false;
					}
				}
			}
			if (t_alliance == null) {
				GameLog.error("read Alliancemember is fail ---null");
				return false;
			}
			target.resetPermission(t_alliance);
			String recordPara = target.getName() + ":" + role.getName() + ":" + t_alliance.getRank();
			long recordTime = TimeUtils.nowLong() / 1000;
			UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
					UnionRecords.CONTENT_TYPE_ALLIAN_RAISE_RANK1, recordPara, recordTime);
			addOneUnionGeneralRecord(record);
			String systemChatPara = recordPara.replace(":", "|");
			ChatSystemContent systemContent = new ChatSystemContent(
					ChatSystemContentType.CONTENT_TYPE_ALLIAN_RAISE_RANK, systemChatPara);
			String chatContent = JsonUtil.ObjectToJsonString(systemContent);
			chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
			// 发送全体邮件
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("content", chatContent);
			temp.put("iconName", icon);
			temp.put("title", ChatSystemContentType.UNION_ALL_MAILS_MEMBER_TITLE_CHANGE);
			temp.put("unionId", id);
			temp.put("type", 0);
			chatMgr.sendUnionNotice(role, JsonUtil.ObjectToJsonString(temp));
		} else {// 非官员
			long have = target.getScoreRecord();
			t_alliance = computeMilitary(have);
			if (t_alliance == null) {
				GameLog.error("read Alliancemember is fail ---null");
				return false;
			}
			target.resetPermission(t_alliance);
			String recordPara = target.getName() + ":" + role.getName() + ":" + t_alliance.getRank();
			long recordTime = TimeUtils.nowLong() / 1000;
			UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
					UnionRecords.CONTENT_TYPE_ALLIAN_RAISE_RANK1, recordPara, recordTime);
			addOneUnionGeneralRecord(record);
			String systemChatPara = recordPara.replace(":", "|");
			ChatSystemContent systemContent = new ChatSystemContent(
					ChatSystemContentType.CONTENT_TYPE_ALLIAN_RAISE_RANK, systemChatPara);
			String chatContent = JsonUtil.ObjectToJsonString(systemContent);
			chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
			// 发送全体邮件
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("content", chatContent);
			temp.put("iconName", icon);
			temp.put("title", ChatSystemContentType.UNION_ALL_MAILS_MEMBER_TITLE_CHANGE);
			temp.put("unionId", id);
			temp.put("type", 0);
			chatMgr.sendUnionNotice(role, JsonUtil.ObjectToJsonString(temp));
		}
		// 任务事件
		Role targetRole = world.getObject(Role.class, uid);
		if (targetRole != null) {
			targetRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_POS,
					index);
		}
		// sendMemberToAllMembers(target,ClientModule.DATA_TRANS_TYPE_UPDATE);
		sendMeToAllMembers(0);
		sendUnionRecordsToAllMembers();
		return true;
	}

	public UnionMember createmMember(Role role, Alliancemembers alliance) {
		UnionMember member = new UnionMember();
		member.setAllianceKey(String.valueOf(alliance.getRank()));
		member.setUid(role.getId());
		member.setLevel(role.getLevel());
		member.setUnionId(id);
		member.setName(role.getName());
		member.setFight(role.getFightPower());
		member.setJoinTime(TimeUtils.nowStr());
		member.getPermissions().addAll(alliance.getJurisdiction());
		member.getIcon().copy(role.getIcon());
		return member;
	}

	public synchronized boolean tryToChangeName(Role role, byte changeType, String newName) {
		UnionMember operator = searchMember(role.getId());
		String contentType = "";
		if (operator == null) {
			return false;
		}
		if (changeType == 0) { // 简称
			if (!operator.checkPost(UnionPostType.UNION_POST_SHORT_NAME_CHANGE)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			if (!role.redRoleMoney(GameConfig.CHANGE_UNION_SHORTNAME_PRICE)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,
						GameConfig.CHANGE_UNION_SHORTNAME_PRICE);
				return false;
			}
			shortName = newName;
			contentType = UnionRecords.CONTENT_TYPE_ALLIAN_CHANGE_SHORTNAME;
		} else if (changeType == 1) {// 名字
			if (!operator.checkPost(UnionPostType.UNION_POST_NAME_CHANGE)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			if (!role.redRoleMoney(GameConfig.CHANGE_UNION_NAME_PRICE)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,
						GameConfig.CHANGE_UNION_NAME_PRICE);
				return false;
			}
			name = newName;
			contentType = UnionRecords.CONTENT_TYPE_ALLIAN_CHANGE_NAME;
		} else if (changeType == 2) { // 公告
			if (!operator.checkPost(UnionPostType.UNION_POST_NOTICE_CHANGE2)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			notice = newName;
			contentType = UnionRecords.CONTENT_TYPE_ALLIAN_CHANGE_NOTICE;
		}else if (changeType == 3) { //内部公告
			if (!operator.checkPost(UnionPostType.UNION_POST_NOTICE_CHANGE1)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
				return false;
			}
			inNotice = newName;
			contentType = UnionRecords.CONTENT_TYPE_ALLIAN_CHANGE_IN_NOTICE;
		}else {
			return false;
		}
		String recordPara = newName;
		long recordTime = TimeUtils.nowLong() / 1000;
		UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
				UnionRecords.UNION_GENERAL_RECORD_COLOR_BLUE, contentType, recordPara, recordTime);
		addOneUnionGeneralRecord(record);
		sendUnionRecordsToAllMembers();
		String systemChatPara = recordPara.replace(":", "|");
		ChatSystemContent systemContent = new ChatSystemContent(contentType, systemChatPara);
		String chatContent = JsonUtil.ObjectToJsonString(systemContent);
		chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
		sendMeToAllMembers(0);
		sendUnionRecordsToAllMembers();
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	public synchronized boolean tryToChangeRecruit(long uid, String condition) {
		UnionMember operator = searchMember(uid);
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_RECRUIT_CHANGE)) {
			return false;
		}
		recruits = condition;
		sendMeToAllMembers(0);
		return true;
	}

	public synchronized boolean checkFlagName(String flagName, List<Flageffects> flageffects) {
		for (Flageffects flage : flageffects) {
			if (flage.getId().equals(flagName)) {
				return true;
			}
		}
		return false;
	}

	public synchronized boolean tryToChangeFlag(Role role, String flagName) {
		RespModuleSet rms = new RespModuleSet();
		UnionMember operator = searchMember(role.getId());
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_FLAG_CAHNGE)) {
			return false;
		}
		role.redRoleMoney(GameConfig.UNION_CHANGE_FLAG_PRICE);
		LogManager.goldConsumeLog(role, GameConfig.UNION_CHANGE_FLAG_PRICE, EventName.tryToChangeFlag.getName());
		icon = flagName;
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role);
		sendMeToAllMembers(0);
		return true;
	}

	public synchronized boolean tryToLevelUp(Role role) {
		UnionMember operator = searchMember(role.getId());
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_LEVEL_UP)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
			return false;
		}
		Alliance next = getData(level + 1);
		if (score < next.getExpend()) {// 联盟积分不足
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_SCORE);
			return false;
		}
		// 判断是否占领有联盟城市
		String needStr = next.getNeed();
		String str = needStr.replaceAll("unionId", String.valueOf(String.valueOf(id)));
		try {
			Object result = ProtoExpression.ExecuteExpression(str);
			boolean flag = Boolean.parseBoolean(result.toString());
			if (!flag) {// 不满足升级条件
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NOT_UNION_NPC_CITY);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		score -= next.getExpend();
		level++;
		sendMeToAllMembers(0);
		// 任务事件
		for (int i = 0; i < members.size(); i++) {
			UnionMember uMember = members.get(i);
			Role member = world.getObject(Role.class, uMember.getUid());
			if (member != null) {
				member.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_LVLUP,
						level);
			}
		}
		return true;
	}

	public synchronized boolean tryToKickMemeber(Role role, long memberId) {
		UnionMember operator = searchMember(role.getId());
		if (operator == null || !operator.checkPost(UnionPostType.UNION_POST_KICK)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION);
			return false;
		}
		UnionMember targte = searchMember(memberId);
		if (targte == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_NOT_FIND);
			return false;
		}
		Role targetRole = world.getRole(memberId);
		if (targetRole == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_MEMBER_NOT_FIND);
			return false;
		}
		// TODO 联盟总战斗力变化
		usInfo.updateUnionFight(targte.getFight() * -1);
		targte.destroy(targetRole, I18nGreeting.MSG_UNION_KICK_MEMBER_OUT, role.getName(), name);
		members.remove(targte);
		// 移除帮助请求
		removeAssistances(memberId);
		// 移除联盟buff
		removeMemberAllUnionTechBuff(targetRole);
		removeMemberAllUnionCityBuff(targetRole);
		String recordPara = targte.getName() + ":" + operator.getName();
		long recordTime = TimeUtils.nowLong() / 1000;
		UnionRecords record = new UnionRecords(UnionRecords.UNION_GENERAL_RECORD,
				UnionRecords.UNION_GENERAL_RECORD_COLOR_RED, UnionRecords.CONTENT_TYPE_ALLIAN_FIRE_MEMBER, recordPara,
				recordTime);
		addOneUnionGeneralRecord(record);
		String systemChatPara = recordPara.replace(":", "|");
		ChatSystemContent systemContent = new ChatSystemContent(ChatSystemContentType.CONTENT_TYPE_ALLIAN_FIRE_MEMBER,
				systemChatPara);
		String chatContent = JsonUtil.ObjectToJsonString(systemContent);
		chatMgr.generateOneMsgsToUnionAndSend(id, chatContent);
		sendUnionRecordsToAllMembers();
		// sendMemberToAllMembers(targte, ClientModule.DATA_TRANS_TYPE_DEL);
		sendMeToAllMembers(0);
		updateUnionCityBuff(role,false);;
		RespModuleSet rms = new RespModuleSet();
		targetRole.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, targetRole.getUserInfo());
		return true;
	}

	private MemberAssistance createAssistance(Role role, TimerLast timer, RoleBuild build, RoleCityAgent city) {
		if (timer.getType() != TimerLastType.TIME_CREATE && timer.getType() != TimerLastType.TIME_LEVEL_UP
				&& timer.getType() != TimerLastType.TIME_RESEARCH) {
			return null;
		}
		assistanceIndex++;
		MemberAssistance assistance = new MemberAssistance(assistanceIndex);
		if(!assistance.init(role, timer, build,city)){
			GameLog.error("init assistance is fail");
			return null;
		}
		return assistance;
	}

	public void sendHelperInfoToAllMembers(Role current, MemberAssistance assistance, byte type) {
		RespModuleSet rms = assistance.sendToClient(type);
		if (rms == null) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	public synchronized boolean tryToAddAssistance(Role role, RoleCityAgent city, RoleBuild build) {
		List<TimerLast> timers = build.getTimers();
		int flag = 1;
		for (int i = 0; i < timers.size(); i++) {
			TimerLast timer = timers.get(i);
			if (timer == null) {
				continue;
			}
			if (timer.getType() != TimerLastType.TIME_CREATE && timer.getType() != TimerLastType.TIME_LEVEL_UP
					&& timer.getType() != TimerLastType.TIME_RESEARCH) {
				continue;
			}
			for (int j = 0; j < assistances.size(); j++) {
				MemberAssistance ma = assistances.get(j);
				if (ma.isMyassistance(build, timer)) {
					flag = 0;
					break;
				}
			}
			if (flag != 0) {
				MemberAssistance assistance = createAssistance(role, timer, build, city);
				if (assistance != null) {
					assistances.add(assistance);
					sendHelperInfoToAllMembers(role, assistance, ClientModule.DATA_TRANS_TYPE_ADD);
				} else {
					return false;
				}
			}
			flag = 1;
		}
		return true;
	}

	public synchronized boolean tryToDoAssistance(Role role, long assistanceid) {
		for (int i = 0; i < assistances.size(); i++) {
			MemberAssistance assistance = assistances.get(i);
			if (assistance.getId() == assistanceid && assistance.couldHelp(role)) {
				if (assistance.getUid() == role.getId()) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_HELP_FAIL_NO_HELP_SELF);
					return false;
				}
				assistance.addhelp(role);
				RespModuleSet rms = assistance.sendToClient(ClientModule.DATA_TRANS_TYPE_DEL);
				MessageSendUtil.sendModule(rms, role);
				sendHelperInfoToAllMembers(role, assistance, ClientModule.DATA_TRANS_TYPE_UPDATE);
				// 任务事件
				NewLogManager.unionLog(role, "alliance_help");
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_HELP, 0);
				role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UNION_HELP);
			}
		}
		return true;
	}

	public synchronized boolean tryToDoAssistanceList(Role role, List<Long> assistanceIds) {
		if (assistanceIds.contains(role.getId())) {
			assistanceIds.remove(role.getId());
		}
		for (int i = 0; i < assistanceIds.size(); i++) {
			Long assistanceid = assistanceIds.get(i);
			if (!tryToDoAssistance(role, assistanceid)) {
				continue;
			}
		}
		return true;
	}

	public synchronized void abolishAssistance(Role role, RoleBuild build, TimerLast timer) {
		Iterator<MemberAssistance> iter = assistances.iterator();
		while (iter.hasNext()) {
			MemberAssistance assistance = iter.next();
			if (assistance.isMyassistance(build, timer)) {
				sendHelperInfoToAllMembers(role, assistance, ClientModule.DATA_TRANS_TYPE_DEL);
				iter.remove();
			}
		}
	}

	/**
	 * 联盟科技
	 * 
	 * @param role
	 * @return
	 */
	public boolean sendUnionTech(Role role, RespModuleSet rms) {
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			GameLog.error("FAIL! role isnt in union");
			return false;
		}
		unionBody.sendUnionTechToClient(rms, new ArrayList<UnionTech>(unionBody.getUnionTechMap().values()),
				unionBody.getTimers(), unionBody.getUpgradeTechID());
		return true;
	}

	/**
	 * 联盟科技捐献
	 * 
	 * @param role
	 * @param unionTechId
	 * @param donateId
	 * @return
	 */
	public synchronized boolean unionDonate(Role role, final String unionTechId, int donateId) {
		UnionMember unionMember = searchMember(role.getId());
		if (unionMember == null) {
			GameLog.error("searchMember is fail uid = " + role.getId() + " unionId" + id);
			return false;
		}
		if (!unionMember.isCanDonate()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_DONATE_TIME_RUN);
			return false;
		}
		RespModuleSet rms = new RespModuleSet();
		if (donateId == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_DONATE_INEXIST);
			return false;
		}
		final UnionTech unionTech = getUnionTechMap().get(unionTechId);
		if (unionTech == null) {
			GameLog.error("FAIL! unionTech isnt in null");
			return false;
		}
		Allianceresearch allianceresearch = dataManager.serach(Allianceresearch.class, donateId);
		if (allianceresearch == null) {
			GameLog.error("get AllianceResearch base is fail!");
			return false;
		}
		Alliancetechlevel alliancetechlevel = dataManager.serach(Alliancetechlevel.class,
				new SearchFilter<Alliancetechlevel>() {
					@Override
					public boolean filter(Alliancetechlevel data) {
						if (data.getTechid().equals(unionTechId) && data.getTechLevel() == unionTech.getTechlevel() + 1)
							return true;
						return false;
					}
				});
		if (alliancetechlevel == null) {
			GameLog.error("get Alliancetechlevel base is fail!");
			return false;
		}
		// 检查资源
		List<String> costLst = allianceresearch.getResources();
		for (String cost : costLst) {
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find donate where danateid =" + donateId);
				return false;
			}
			String itemId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			RoleCityAgent agent = role.getCity(0);
			ResourceTypeConst type = ResourceTypeConst.search(itemId);
			if (type == ResourceTypeConst.RESOURCE_TYPE_GOLD) {
				if (role.getMoney() < num) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, itemId);
					return false;
				}
			} else {
				if (agent.getResource(type) < num) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT, itemId);
					return false;
				}
			}
		}
		// 检查联盟经验是否已经满
		if (unionTech.getCurrentExp() >= alliancetechlevel.getTechExp() * alliancetechlevel.getStarnumber()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TECH_EXP_FULL);
			return false;
		}
		// 扣除相应的资源
		for (String cost : costLst) {
			String[] costArray = cost.split(":");
			if (costArray.length < 2) {
				GameLog.error("cannot find donate where danateid =" + donateId);
				return false;
			}
			String itemId = costArray[0];
			int num = Integer.parseInt(costArray[1]);
			ResourceTypeConst type = ResourceTypeConst.search(itemId);
			if (itemId.equals(ResourceTypeConst.RESOURCE_TYPE_GOLD.getKey())) {
				if (!role.redRoleMoney(num)) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, itemId);
					return false;
				}
				LogManager.goldConsumeLog(role, GameConfig.UNION_CHANGE_FLAG_PRICE, EventName.unionDonate.getName());
			} else {
				if (!role.redResourcesFromCity(0, type, num)) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MATERIAL_INSUFFICIENT, itemId);
					return false;
				}
			}
		}
		// 增加个人贡献
		unionMember.setScore(unionMember.getScore() + allianceresearch.getPersContr());
		unionMember.setScoreDaily(unionMember.getScoreDaily() + allianceresearch.getPersContr());
		unionMember.setScoreWeekly(unionMember.getScoreWeekly() + allianceresearch.getPersContr());
		unionMember.setScoreRecord(unionMember.getScoreRecord() + allianceresearch.getPersContr());
		unionMember.setDonateDaily(unionMember.getDonateDaily() + allianceresearch.getResExp());
		unionMember.setDonateWeekly(unionMember.getDonateWeekly() + allianceresearch.getResExp());
		unionMember.setDonateRecord(unionMember.getDonateRecord() + allianceresearch.getResExp());
		unionMember.incrementDonateTime(); // 记录捐赠次数
		sendMemberToAllMembers(unionMember, ClientModule.DATA_TRANS_TYPE_UPDATE);
		// 增加联盟贡献
		setScore(getScore() + allianceresearch.getAllianceContr());
		// 增加联盟科技经验
		sendMeToAllMembers(0);
		int add = gmShareNum > 0 ? gmShareNum : allianceresearch.getResExp();
		if (unionTech.getCurrentExp() + add < alliancetechlevel.getTechExp() * alliancetechlevel.getStarnumber()) {
			unionTech.setCurrentExp(unionTech.getCurrentExp() + add);
		} else {
			unionTech.setCurrentExp(alliancetechlevel.getTechExp() * alliancetechlevel.getStarnumber());
		}
		sendUnionTechToAllMembers(rms, new ArrayList<UnionTech>(getUnionTechMap().values()), getTimers(),
				getUpgradeTechID());
		// TODO 随机下一组捐赠按钮
		randomDonateButton(role, unionTechId, donateId, unionMember);
		// 下发用户金币
		RespModuleSet rms1 = new RespModuleSet();
		role.sendRoleToClient(rms1);
		MessageSendUtil.sendModule(rms1, role);
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_SCORE,
				allianceresearch.getPersContr());
		role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UNION_DONATE);
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_POS,
				Integer.valueOf(unionMember.getAllianceKey()));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < costLst.size(); i++) {
			String cost = costLst.get(i);
			String[] str = cost.split(":");
			for (int j = 0; j < str.length; j++) {
				String paramer = str[j];
				sb.append(paramer);
				sb.append(GameLog.SPLIT_CHAR);
			}
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		NewLogManager.unionLog(role, "alliance_donate", unionTechId, newStr);
		return true;
	}

	private void randomDonateButton(Role role, String techId, int donateId, UnionMember member) {
		if (member.getTechProgresses() == null) {
			GameLog.error("member getTechProgresses is fail! ");
			return;
		}
		UnionMemberTechProgress techProgress = member.getTechProgresses().get(techId);
		if (techProgress == null || techProgress.getTechProgresses().size() < 3) {
			GameLog.error("member techProgress is fail! ");
			return;
		}

		List<Allianceresearch> allianceresearchs = dataManager.serachList(Allianceresearch.class);
		List<List<Allianceresearch>> buttons = new ArrayList<List<Allianceresearch>>();
		List<Allianceresearch> firstButtons = new ArrayList<Allianceresearch>();
		List<Allianceresearch> secondButtons = new ArrayList<Allianceresearch>();
		List<Allianceresearch> thirdButtons = new ArrayList<Allianceresearch>();
		for (Allianceresearch allianceresearch : allianceresearchs) {
			if (allianceresearch.getResType() == 1 && role.getLevel() >= allianceresearch.getRoleLevel()) {
				firstButtons.add(allianceresearch);
			} else if (allianceresearch.getResType() == 2 && role.getLevel() >= allianceresearch.getRoleLevel()) {
				secondButtons.add(allianceresearch);
			} else if (allianceresearch.getResType() == 3 && role.getLevel() >= allianceresearch.getRoleLevel()) {
				thirdButtons.add(allianceresearch);
			}
		}
		buttons.add(firstButtons);
		buttons.add(secondButtons);
		buttons.add(thirdButtons);

		for (int i = 1; i < 3; i++) {
			DonateButton donateButton = techProgress.getTechProgresses().get(i);
			if (donateButton == null) {
				continue;
			}
			Allianceresearch allianceresearch = dataManager.serach(Allianceresearch.class, donateId);
			if (allianceresearch == null) {
				GameLog.error("read base Allianceresearch is fail");
				continue;
			}
			if (allianceresearch.getResType() == (i + 1)) {
				DonateButton result = randomOneDonate(donateButton, buttons.get(i));
				if (result != null) {
					donateButton.setDonateId(result.getDonateId());
					donateButton.setWeightingfactor(result.getWeightingfactor());
					donateButton.setUnusedNum(result.getUnusedNum());
				}
				continue;
			}
			if (donateButton.getDonateId() == 0) { // 未随机到 权重翻倍
				DonateButton result = randomOneDonate(donateButton, buttons.get(i));
				if (result != null) {
					donateButton.setDonateId(result.getDonateId());
					donateButton.setUnusedNum(result.getUnusedNum());
				}
				if (result.getDonateId() == 0) {
					donateButton.setWeightingfactor(donateButton.getWeightingfactor() + 1);
				} else {
					donateButton.setWeightingfactor(result.getWeightingfactor());
				}
			} else {
				if (donateButton.getDonateId() != 0 && donateButton.getUnusedNum() >= 3) { // 出现三次且未使用
																							// 权重重置
					DonateButton result = randomOneDonate(donateButton, buttons.get(i));
					if (result != null) {
						donateButton.setDonateId(result.getDonateId());
						donateButton.setWeightingfactor(result.getWeightingfactor());
						donateButton.setUnusedNum(result.getUnusedNum());
					}
				}
				if (donateButton.getDonateId() != 0 && donateButton.getUnusedNum() < 3) {
					donateButton.setUnusedNum(donateButton.getUnusedNum() + 1);
				}
			}

		}
		RespModuleSet rms = new RespModuleSet();
		List<UnionMemberTechProgress> reslut = new ArrayList<>();
		reslut.add(techProgress);
		member.sendMemberTechProgress(reslut, rms);
		MessageSendUtil.sendModule(rms, role);
	}

	private DonateButton randomOneDonate(DonateButton donateButton, List<Allianceresearch> list) {
		int weightSum = 0;
		for (int i = 0; i < list.size(); i++) {
			Allianceresearch allianceresearch = list.get(i);
			weightSum += allianceresearch.getWeightValue() * donateButton.getWeightingfactor();
		}
		int rand = MathUtils.random(1, 1000);
		int randDonateId = 0;
		int tag = 0;
		if (rand > weightSum) {
			randDonateId = 0;
		} else {
			for (int i = 0; i < list.size(); i++) {
				Allianceresearch allianceresearch = list.get(i);
				if (allianceresearch == null) {
					return null;
				}
				tag += allianceresearch.getWeightValue() * donateButton.getWeightingfactor();
				if (rand < tag) {
					randDonateId = allianceresearch.getId();
					break;
				}
			}
		}
		DonateButton result = new DonateButton(randDonateId, 1, 1);
		return result;
	}

	/**
	 * 联盟科技升级
	 * 
	 * @param role
	 * @param unionTechId
	 * @return
	 */
	public synchronized boolean UnionTechUpgrade(Role role, final String unionTechId) {
		RespModuleSet rms = new RespModuleSet();
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_UNION, role.getName());
			return false;
		}
		final UnionTech unionTech = unionBody.getUnionTechMap().get(unionTechId);
		if (unionTech == null) {
			GameLog.error("FAIL! unionTech isnt in null");
			return false;
		}
		Alliancetechlevel alliancetechlevel = dataManager.serach(Alliancetechlevel.class,
				new SearchFilter<Alliancetechlevel>() {
					@Override
					public boolean filter(Alliancetechlevel data) {
						if (data.getTechid().equals(unionTechId) && data.getTechLevel() == unionTech.getTechlevel() + 1)
							return true;
						return false;
					}
				});
		if (alliancetechlevel == null) {
			GameLog.error("get Alliancetechlevel base is fail!");
			return false;
		}
		Tech tech = dataManager.serach(Tech.class, unionTechId);
		if (tech == null) {
			GameLog.error("get Tech base is fail!");
			return false;
		}
		// 检查是否有升级科技的权限
		UnionMember unionMember = unionBody.searchMember(role.getId());
		if (!unionMember.checkPost(UnionPostType.UNION_POST_TECHNOLOGY_LEVEL_UP)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION,
					role.getName());
			return false;
		}
		// 检查是不是最高等级
		if (tech.getMaxPoints() == unionTech.getTechlevel()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TECH_MAX_LELEL,
					unionTech.getTechId());
			return false;
		}
		// 判断经验是否满足升级条件
		if (unionTech.getCurrentExp() != (alliancetechlevel.getTechExp() * alliancetechlevel.getStarnumber())) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TECH_NO_UPGRADE,
					unionTech.getTechId());
			return false;
		}
		// 检查有没有有倒计时空闲
		if (timers != null && !timers.over()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TECH_UPGRADING,
					unionTech.getTechId());
			return false;
		}
		long last = gmTeachLevelUpTime > 0 ? gmTeachLevelUpTime : alliancetechlevel.getRestime();
		unionBody.addUnionTechTimer(last, TimerLastType.TIME_UNION_TECH, unionTechId);
		timers.registTimeOver(new UnionTechFinish(unionBody, unionTechId));
		try {
			unionBody.sendUnionTechToAllMembers(rms, new ArrayList<UnionTech>(unionBody.getUnionTechMap().values()),
					unionBody.getTimers(), unionBody.getUpgradeTechID());
		} catch (Exception e) {
			GameLog.error("sendUnionTechToAllMembers in techTechFinish is fail");
			return false;
		}
		return true;
	}

	/**
	 * 获取联盟商店物品
	 * 
	 * @param role
	 * @return
	 */
	public boolean sendUnionStore(Role role, RespModuleSet rms) {
		sendUnionStoreToClient(rms, new ArrayList<UnionItem>(unionStore.values()));
		// save();
		return true;
	}

	/**
	 * 兑换联盟物品
	 * 
	 * @param role
	 * @param itemId
	 * @param num
	 * @return
	 */
	public synchronized boolean convertUnionGoods(Role role, String itemId, int num) {
		RespModuleSet rms = new RespModuleSet();
		Allianceshop allianceshop = dataManager.serach(Allianceshop.class, itemId);
		if (allianceshop == null) {
			GameLog.error("read Allianceshop base is fail!");
			return false;
		}
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_UNION, role.getName());
			return false;
		}
		// TODO 5
		// if(unionBody.getSysStore() == null ||
		// unionBody.getSysStore().get(itemId) == null){
		// GameLog.error("union sys Store is errordata");
		// return false;
		// }
		// 检测联盟积分不足
		if (unionBody.getScore() < allianceshop.getAllianceContr() * num) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_SCORE_SHORTAGE);
			return false;
		}
		// 检测联盟等级
		if (unionBody.getLevel() < allianceshop.getAllianceLv()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_LEVEL_SHORTAGE);
			return false;
		}
		// TODO 6
		// if(unionBody.getSysStore().get(itemId).getNum() < num){
		// MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_UNION_SYS_STORE_INSUFFICIENT);
		// return false;
		// }
		// 扣除积分
		unionBody.setScore(unionBody.getScore() - allianceshop.getAllianceContr() * num);

		// 加入联盟商店
		addUnionGood(itemId, (byte) 1, num);
		// 移除系统商店
		redUnionSystemStore(itemId, num);
		// 发送联盟物品到所有在线的用户
		// TODO 2 sendUnionSystemStoreToAllMembers(rms, new
		// ArrayList<UnionItem>(sysStore.values()));
		sendUnionStoreToAllMembers(rms, new ArrayList<UnionItem>(unionStore.values()));
		sendMeToAllMembers(0);

		String recordPara = role.getName() + ":" + num + ":" + itemId;
		long recordTime = TimeUtils.nowLong() / 1000;
		UnionRecords record = new UnionRecords(UnionRecords.UNION_SHOP_RECORD_TYPE_UNION,
				UnionRecords.CONTENT_TYPE_ALLIAN_SHOP_UNION, recordPara, recordTime);
		addOneUnionRecord(record);
		sendUnionRecordsToAllMembers();

		role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.UNION_SHOP_BUY);
		return true;
	}

	/**
	 * 发送联盟物品给所有的在线用户
	 * 
	 * @param rms
	 * @param items
	 */
	public void sendUnionStoreToAllMembers(RespModuleSet rms, List<UnionItem> items) {
		sendUnionStoreToClient(rms, items);
		if (rms == null) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	/**
	 * 打包联盟商店的所有的物品
	 * 
	 * @param rms
	 * @param items
	 */
	public void sendUnionStoreToClient(RespModuleSet rms, List<UnionItem> items) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_STORE;
			}
		};
		module.add(items.size()); // 物品的数量 int
		for (int i = 0; i < items.size(); i++) {
			UnionItem item = items.get(i);
			module.add(item.getId()); // 物品的Id String
			module.add(item.getType()); // 物品的类型
			module.add(item.getNum()); // 物品的数量 int
		}
		rms.addModule(module);
	}

	/**
	 * 打包联盟系统商店的所有的物品
	 * 
	 * @param rms
	 * @param items
	 */
	public void sendSysStoreToClient(RespModuleSet rms, List<UnionItem> items) {
		// TODO 联盟系统商店协议
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_SYSTEM_STORE;
			}
		};
		module.add(items.size()); // 物品的数量 int
		for (int i = 0; i < items.size(); i++) {
			UnionItem item = items.get(i);
			module.add(item.getId()); // 物品的Id String
			module.add(item.getType()); // 物品的类型
			module.add(item.getNum()); // 物品的数量 int
		}
		rms.addModule(module);
	}

	/**
	 * 购买联盟商店的物品
	 * 
	 * @param role
	 * @param itemId
	 * @param num
	 * @return
	 */
	public synchronized boolean buyUnionGoods(Role role, String itemId, int num) {
		RespModuleSet rms = new RespModuleSet();
		RoleBagAgent bagAgent = role.getBagAgent();
		if (bagAgent == null) {
			GameLog.error("get roleBag is fail!");
			return false;
		}
		int presContr = 0;
		// 检查联盟是否有相应的物品及其个数
		if (unionStore.get(itemId) == null || unionStore.get(itemId).getNum() < num) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_STORE_ITEM_SHORTAGE, itemId);
			return false;
		}
		UnionItem unionItem = unionStore.get(itemId);
		if (unionItem.getType() == 0) {
			Item item = dataManager.serach(Item.class, itemId);
			if (item == null) {
				GameLog.error("read Item base is fail!");
				return false;
			}
			presContr = item.getStoragePrice();
		} else {
			Allianceshop allianceshop = dataManager.serach(Allianceshop.class, itemId);
			if (allianceshop == null) {
				GameLog.error("read Allianceshop base is fail!");
				return false;
			}
			presContr = allianceshop.getPersContr();
		}
		// 检查个人联盟贡献够不够
		UnionMember unionMember = searchMember(role.getId());
		if (unionMember == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_UNION, role.getName());
			return false;
		}
		if (unionMember.getScore() < presContr * num) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_SCORE_SHORTAGE);
			return false;
		}
		// 扣除个人积分
		unionMember.setScore(unionMember.getScore() - presContr * num);
		// 扣除联盟物品
		if (!redUnionItem(itemId, num)) {
			GameLog.error("From UnionStore remove itemId = " + itemId + "num:" + num + "---FAIL");
			return false;
		}
		// 物品加入用户背包物品
		List<ItemCell> alList = new ArrayList<>();
		alList = bagAgent.addGoods(itemId, num);
		LogManager.itemOutputLog(role, num, EventName.buyUnionGoods.getName(), itemId);
		bagAgent.sendItemsToClient(rms, alList);
		unionMember.sendToClient(rms, ClientModule.DATA_TRANS_TYPE_UPDATE);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		rms = new RespModuleSet();
		sendUnionStoreToAllMembers(rms, new ArrayList<UnionItem>(unionStore.values()));
		String recordPara = role.getName() + ":" + num + ":" + itemId;
		long recordTime = TimeUtils.nowLong() / 1000;
		UnionRecords record = new UnionRecords(UnionRecords.UNION_SHOP_RECORD_TYPE_ROLE,
				UnionRecords.CONTENT_TYPE_ALLIAN_SHOP, recordPara, recordTime);
		addOneUnionRecord(record);
		sendUnionRecordsToAllMembers();
		return true;
	}

	/**
	 * 移除联盟商店的物品itemId 数量为num
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	public synchronized boolean redUnionItem(String itemId, int num) {
		for (UnionItem item : unionStore.values()) {
			if (item.getId().equals(itemId)) {
				if (item.getNum() - num < 0) {
					return false;
				} else if (item.getNum() == num) {
					unionStore.remove(item.getId());
					return true;
				} else {
					item.setNum(item.getNum() - num);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 科技升级结束
	 * 
	 * @param unionBody
	 * @param techId
	 */
	public synchronized void techTechFinish(final String techId) {
		RespModuleSet rms = new RespModuleSet();
		setUpgradeTechID("");
		UnionTech unionTech = getUnionTechMap().get(techId);
		unionTech.setTechlevel(unionTech.getTechlevel() + 1);
		unionTech.setCurrentExp(0);
		try {
			sendMeToAllMembers(0);
			sendUnionTechToAllMembers(rms, new ArrayList<UnionTech>(getUnionTechMap().values()), getTimers(),
					getUpgradeTechID());
			GameLog.info("run timer finish timer type=Union_tech");
		} catch (Exception e) {
			GameLog.error("sendUnionTechToAllMembers in techTechFinish is fail");
			return;
		}
		// 任务事件
		for (UnionMember uMember : getMembers()) {
			Role memrole = world.getObject(Role.class, uMember.getUid());
			if (memrole != null) {
				// 任务检测
				memrole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_TECH, id,
						techId, unionTech.getTechlevel());
				// 给用户添加科技的buff
				updataMemberUnionTechBuff(memrole, unionTech);
			}
		}
		// 下发

	}

	/**
	 * 
	* @Title: motifyCityBuff 
	* @Description: 修改城市buff
	* 
	* @return void
	* @param city
	* @param isAdd
	 */
	public synchronized void motifyCityBuff(MapUnionCity city,boolean isAdd) {
		for (UnionMember uMember : getMembers()) {
			Role memrole = world.getObject(Role.class, uMember.getUid());
			if (memrole != null) {
				if(isAdd)
					addUnionCityBuff(memrole, city);
				else
					// 删除城市buff
					removeCityBuff(memrole, city.getId());
			}
		}

	}

	/**
	 * 修改联盟的称谓
	 * 
	 * @param role
	 * @param changeTitle
	 * @return
	 */
	public synchronized boolean changeUnionTitle(Role role, Map<Integer, String> changeTitle) {
		// 检查是否有修改联盟称谓的权限
		UnionMember unionMember = searchMember(role.getId());
		if (!unionMember.checkPost(UnionPostType.UNION_POST_OFFICER_NAME_CHANGE)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NO_OPERAT_PERMISSION,
					role.getName());
			return false;
		}
		for (int r1 : changeTitle.keySet()) {
			for (int r2 : changeTitle.keySet()) {
				if (r1 == r2) {
					continue;
				}
				if (changeTitle.get(r1).equals(changeTitle.get(r2))) {
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TITLE_NOT_SAME);
					return false;
				}
			}
		}
		// 检查联盟称谓的合法性
		for (String title : changeTitle.values()) {
			if (!nameManager.isNameCharLegal(title, GameConfig.REGEX_CHINESE_AND_NUMBER_AND_ALL_LETTER)
					|| !nameManager.isNameLegal(title)) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TITLE_ILLEGALITY_SENSITIVE,
						title);
				return false;
			}
			if (StringUtils.countStringLength(title) > GameConfig.UNION_TITLE_MAX
					|| StringUtils.countStringLength(title) < GameConfig.UNION_TITLE_MIN) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_TITLE_ILLEGALITY_LENGTH,
						title);
				return false;
			}
		}
		// 修改对应的称谓
		for (int rank : changeTitle.keySet()) {
			unionTitle.put(rank, "1|" + changeTitle.get(rank));
		}
		// 下发联盟消息
		sendMeToAllMembers(0);
		return true;
	}

	public boolean checkPermission(long uid, UnionPostType postType) {
		UnionMember unionMember = searchMember(uid);
		return unionMember.checkPost(postType);
	}

	/**
	 * 发送用户联盟捐献进度
	 * 
	 * @param role
	 */
	public synchronized void sendMemberTechProgress(Role role, RespModuleSet rms) {
		UnionMember member = searchMember(role.getId());
		if (member == null) {
			GameLog.error("member is null");
			return;
		}
		Map<String, UnionMemberTechProgress> techPros = member.getTechProgresses();
		if (techPros.size() == 0) {
			initMemberTechPro(member);
		}
		List<UnionMemberTechProgress> allTechProgresses = new ArrayList<UnionMemberTechProgress>();
		for (String temp : techPros.keySet()) {
			allTechProgresses.add(techPros.get(temp));
		}
		member.sendMemberTechProgress(allTechProgresses, rms);
	}

	public void initMemberTechPro(UnionMember member) {
		Map<String, UnionMemberTechProgress> techPros = member.getTechProgresses();
		for (String techId : unionTechMap.keySet()) {
			DonateButton donateButton1 = new DonateButton(1, 1, 1);
			DonateButton donateButton2 = new DonateButton(0, 1, 1);
			DonateButton donateButton3 = new DonateButton(0, 1, 1);
			List<DonateButton> donateButtons = new ArrayList<DonateButton>();
			donateButtons.add(donateButton1);
			donateButtons.add(donateButton2);
			donateButtons.add(donateButton3);
			UnionMemberTechProgress unionMemberTechProgress = new UnionMemberTechProgress(techId, donateButtons);
			techPros.put(techId, unionMemberTechProgress);
		}
	}

	/**
	 * 加入一个联盟商品
	 * 
	 * @param unionBody
	 * @param itemId
	 * @param type
	 * @param num
	 * @return
	 */
	private synchronized boolean addUnionGood(String itemId, byte type, int num) {
		// 加入联盟商店
		if (unionStore.size() == 0) {
			UnionItem tempItem = new UnionItem();
			tempItem.setId(itemId);
			tempItem.setType(type);
			tempItem.setNum(num);
			unionStore.put(itemId, tempItem);
		} else {
			UnionItem item = unionStore.get(itemId);
			if (item == null || item.getId() == null) { // 联盟商店如果不存在 加入一个新的物品
				UnionItem tempItem = new UnionItem();
				tempItem.setId(itemId);
				tempItem.setType(type);
				tempItem.setNum(num);
				unionStore.put(itemId, tempItem);
			} else { // 联盟商店存在 修改联盟物品的个数
				item.setNum(item.getNum() + num);
			}
		}
		return true;
	}

	/**
	 * 
	 * @Title: updateUnionCityBuff
	 * @Description: 联盟城市buff
	 * 
	 * @return void
	 * @param role
	 * @param unionId
	 */
	public void updateUnionCityBuff(Role role, Boolean isAdd) {
		List<MapUnionCity> unionCitys = mapWorld.searchUnionCity(this.id);
		for (int i = 0; i < unionCitys.size(); i++) {
			if (isAdd) {
				addUnionCityBuff(role, unionCitys.get(i));
			} else {
				removeCityBuff(role, unionCitys.get(i).getId());
			}
		}
	}

	public void loadCityBuff(Map<Long, List<Effect>> unionCityMap) {
		List<MapUnionCity> unionCitys = mapWorld.searchUnionCity(this.id);
		for (MapUnionCity city : unionCitys) {
			Npccity data = city.getData();
			if (data != null && city.getUnionId() == this.id) {
				List<String> resbuff = data.getResbuff();
				if (resbuff != null) {
					for (String buff : resbuff) {
						if (StringUtils.isNull(buff))
							continue;
						String[] buffs = buff.split(":");
						String buffid = buffs[0];
						int num = Integer.parseInt(buffs[1]);
						EffectAgent.loadUnionBuffs(unionCityMap, city.getId(), buffid, num);
					}
				}
			}
		}
	}

	/**
	 * 移除玩家所以城市id
	 * 
	 * @Title: removeMemberAllUnionCityBuff
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * 
	 * @return void
	 * @param role
	 */
	public void removeMemberAllUnionCityBuff(Role role) {
		role.getEffectAgent().removeAllUnionBuffs(role);
	}

	/**
	 * 
	 * @Title: removeCityBuff
	 * @Description: 删除城市buff
	 * 
	 * @return void
	 * @param role
	 * @param cid
	 */
	public void removeCityBuff(Role role, long cid) {
		role.getEffectAgent().removeUnionBuffs(role, cid);
	}

	/**
	 * 
	 * @Title: addUnionCityBuff
	 * @Description: 添加城市buff
	 * 
	 * @return void
	 * @param role
	 * @param city
	 */
	public void addUnionCityBuff(Role role, MapUnionCity city) {
		Npccity data = city.getData();
		if (data != null && city.getUnionId() == this.id) {
			List<String> resbuff = data.getResbuff();
			if (resbuff != null) {
				for (String buff : resbuff) {
					if (StringUtils.isNull(buff))
						continue;
					String[] buffs = buff.split(":");
					String buffid = buffs[0];
					int num = Integer.parseInt(buffs[1]);
					role.getEffectAgent().addUnionBuffs(role, city.getId(), buffid, num);
				}
			}
		}
	}
	
	/**
	 * 更新用户memrole的联盟科技unionTech的科技buff
	 * 
	 * @param memrole
	 * @param unionTech
	 */
	public void updataMemberUnionTechBuff(Role memrole, UnionTech unionTech) {
		final String techId = unionTech.getTechId();
		final int techLevel = unionTech.getTechlevel();
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
			@Override
			public boolean filter(Techupgrade data) {
				if (data.getTechID().equals(techId) && data.getLevel() == techLevel) {
					return true;
				}
				return false;
			}
		});
		if (techUpgrade == null) {
			return;
		}
		List<String> buffLst = techUpgrade.getBuffList();
		if (buffLst != null) {
			if (unionTech.getTechlevel() > 1) {
				memrole.getEffectAgent().removeTechBuff(memrole, techId);
			}
			memrole.getEffectAgent().addTechBuff(memrole, buffLst.get(0), buffLst.get(1), techId);
		}
	}

	/**
	 * 更新用户的联盟科技buff
	 * 
	 * @param role
	 */
	public void updataMemberAllUnionTechBuff(Role role) {
		for (UnionTech unionTech : unionTechMap.values()) {
			if (unionTech == null || unionTech.getTechlevel() <= 0) {
				continue;
			}
			updataMemberUnionTechBuff(role, unionTech);
		}
	}

	/**
	 * 移除联盟科技buff
	 * 
	 * @param role
	 */
	public void removeMemberAllUnionTechBuff(Role role) {
		for (UnionTech unionTech : unionTechMap.values()) {
			if (unionTech == null || unionTech.getTechlevel() <= 0) {
				continue;
			}
			final String techId = unionTech.getTechId();
			final int techLevel = unionTech.getTechlevel();
			Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
				@Override
				public boolean filter(Techupgrade data) {
					if (data.getTechID().equals(techId) && data.getLevel() == techLevel) {
						return true;
					}
					return false;
				}
			});
			if (techUpgrade == null) {
				return;
			}
			List<String> buffLst = techUpgrade.getBuffList();
			if (buffLst != null) {
				role.getEffectAgent().removeTechBuff(role, techId);
			}
		}
	}

	/**
	 * 出售物品到联盟的仓库
	 * 
	 * @param role
	 * @param itemId
	 * @return
	 */
	public synchronized boolean removeItemToUnionStorage(Role role, String itemId, int num) {
		RespModuleSet rms = new RespModuleSet();
		Item item = dataManager.serach(Item.class, itemId);
		if (item == null) {
			GameLog.error("read Item base is fail where itemId = " + itemId);
			return false;
		}
		// TODO 是否可以出售
		if (item.getSell() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_UNION_NOT_FIND);
			return false;
		}
		// 移除背包
		RoleBagAgent bagAgent = role.getBagAgent();
		ItemCell itemCell = bagAgent.getItemFromBag(itemId);
		bagAgent.removeItems(itemId, num);
		bagAgent.sendItemsToClient(rms, itemCell);
		MessageSendUtil.sendModule(rms, role);
		// 个人贡献
		UnionMember member = searchMember(role.getId());
		if (member == null) {
			GameLog.error("get member from unionbody is fail");
			return false;
		}
		member.setScore(member.getScore() + item.getRemovePrice());
		member.setScoreDaily(member.getScoreDaily() + item.getRemovePrice());
		member.setScoreWeekly(member.getScoreWeekly() + item.getRemovePrice());
		member.setScoreRecord(member.getScoreRecord() + item.getRemovePrice());
		sendMemberToAllMembers(member, ClientModule.DATA_TRANS_TYPE_UPDATE);
		// 加入联盟商店
		addUnionGood(itemId, (byte) 0, num);
		rms = new RespModuleSet();
		sendUnionStoreToAllMembers(rms, new ArrayList<UnionItem>(unionStore.values()));
		// 添加下发联盟记录
		String recordPara = role.getName() + ":" + num + ":" + itemId;
		long recordTime = TimeUtils.nowLong() / 1000;
		UnionRecords record = new UnionRecords(UnionRecords.UNION_ROLE_REMOVE_ITEM,
				UnionRecords.CONTENT_TYPE_ALLIAN_REMOVE_ITEM, recordPara, recordTime);
		addOneUnionRecord(record);
		sendUnionRecordsToAllMembers();
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_SCORE,
				item.getRemovePrice());
		return true;
	}

	public synchronized void useScore(int needScore) {
		score -= needScore;
	}

	public void addInvitesList(long uid) {
		invites.add(uid);
	}

	public void redInvitesList(Long uid) {
		if (invites != null) {
			invites.remove(uid);
		}
	}

	public int getUnionTechById(String techId) {
		if (this.unionTechMap.get(techId) == null) {
			return 0;
		}
		return unionTechMap.get(techId).getTechlevel();
	}

	public UnionMember getUnionMemberById(long uid) {
		for (UnionMember mem : members) {
			if (mem.getUid() == uid) {
				return mem;
			}
		}
		return null;
	}

	public void sendOcpCityToMems(int level) {
		for (UnionMember uMember : members) {
			Role memrole = world.getObject(Role.class, uMember.getUid());
			if (memrole != null) {
				memrole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_OCP_CITY,
						level);
			}
		}
	}

	/**
	 * 获取联盟其他成员的视野
	 * 
	 * @param views
	 * @param role
	 */
	public void getViews(List<Integer> views, Role role) {
		long uid = role == null ? 0 : role.getId();
		// 联盟成员的视野
		for (UnionMember uMember : members) {
			if (uMember.getUid() == uid) {
				continue;
			}
			List<MapCity> mcs = mapWorld.searchMapCity(uMember.getUid());
			for (MapCity mc : mcs) {
				mc.getViews(views, null);
			}
		}
		// 获取联盟城市的视野
		List<MapUnionCity> mucs = mapWorld.searchUnionCity(id);
		for (MapUnionCity muc : mucs) {
			muc.getViews(views);
		}
	}

	public void sendViewsToAllMember() {
		RespModuleSet rms = null;
		for (UnionMember uMember : members) {
			Role role = world.getOnlineRole(uMember.getUid());
			if (role != null) {
				if (rms == null) {
					rms = new RespModuleSet();
					List<Integer> views = new ArrayList<Integer>();
					getViews(views, null);
					AbstractClientModule module = new AbstractClientModule() {
						@Override
						public short getModuleType() {
							return NTC_DTCD_ROLE_VIEWS;
						}
					};
					module.add(views); // 玩家视野范围坐标
					rms.addModule(module);
				}
				MessageSendUtil.sendModule(rms, role);
			}
		}
	}

	/**
	 * 发送系统商店到用户
	 * 
	 * @param role
	 * @return
	 */
	public boolean sendUnionSystemStore(Role role) {
		RespModuleSet rms = new RespModuleSet();
		sendSysStoreToClient(rms, new ArrayList<UnionItem>(sysStore.values()));
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	/**
	 * 发送系统商店到联盟所有用户
	 * 
	 * @param rms
	 * @param items
	 */
	public void sendUnionSystemStoreToAllMembers(RespModuleSet rms, List<UnionItem> items) {
		sendSysStoreToClient(rms, items);
		if (rms == null) {
			return;
		}
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}

	/**
	 * 删除联盟商店的物品
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	public synchronized boolean redUnionSystemStore(String itemId, int num) {
		if (sysStore == null || sysStore.get(itemId) == null) {
			GameLog.error("get item from unionsysStore is fail!");
			return false;
		}
		if (sysStore.get(itemId).getNum() < num) {
			return false;
		}
		sysStore.get(itemId).setNum(sysStore.get(itemId).getNum() - num);
		return true;
	}

	/**
	 * 给客户端同步联盟战斗数量变化
	 * 
	 * @param module
	 */
	public void sendUnionFights(AbstractClientModule module) {
		List<ExpediteTroops> ets = new ArrayList<ExpediteTroops>();
		List<MassTroops> mts = new ArrayList<MassTroops>();
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			List<ExpediteTroops> es = mapWorld.getRelevanceRoleExpedites(member.getUid());
			for (int j = 0; j < es.size(); j++) {
				ExpediteTroops expedite = es.get(j);
				if (expedite.isMass() || expedite.getTimer().getType() != TimerLastType.TIME_EXPEDITE_FIGHT) {
					continue;
				}
				MapObject obj = mapWorld.searchObject(expedite.getTargetPosition());
				boolean flag = obj instanceof MapUnionCity || obj instanceof MapUnionBuild;
				if (obj == null || (obj.getInfo().getUid() == 0 && !flag)) {
					continue;
				}
				if (!ets.contains(expedite)) {
					ets.add(expedite);
				}
			}
			List<MassTroops> masses = mapWorld.getRelevanceRoleMasses(member.getUid());
			mts.addAll(masses);
		}
		module.add(ets.size() + mts.size());
		for (int i = 0; i < ets.size(); i++) {
			ExpediteTroops expedite = ets.get(i);
			module.add(2);
			module.add(expedite.getId());
		}
		for (int i = 0; i < mts.size(); i++) {
			MassTroops mass = mts.get(i);
			module.add(1);
			module.add(mass.getPosition());
		}
	}

	/***
	 * 联盟战斗的详细数据
	 * 
	 * @return
	 */
	public List<UnionFightTransformData> getFightDatas() {
		List<UnionFightTransformData> result = new ArrayList<UnionFightTransformData>();
		List<ExpediteTroops> ets = new ArrayList<ExpediteTroops>();
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			List<ExpediteTroops> expedites = mapWorld.getRelevanceRoleExpedites(member.getUid());
			for (int j = 0; j < expedites.size(); j++) {
				ExpediteTroops expedite = expedites.get(j);
				if (expedite.isMass() || expedite.getTimer().getType() != TimerLastType.TIME_EXPEDITE_FIGHT) {
					continue;
				}
				MapObject obj = mapWorld.searchObject(expedite.getTargetPosition());
				boolean flag = obj instanceof MapUnionCity || obj instanceof MapUnionBuild;
				if (obj == null || (obj.getInfo().getUid() == 0 && !flag)) {
					continue;
				}
				if (ets.contains(expedite)) {
					continue;
				}
				ets.add(expedite);
				UnionFightTransformData uft = new UnionFightTransformData();
				GridType[] grids = new GridType[1];
				grids[0] = new GridType();
				grids[0].setType(expedite.getClass());
				grids[0].setId(expedite.getId());
				uft.setGrids(grids);
				FightVersus defender = uft.getDefender();
				if (obj instanceof MapUnionCity) {
					MapUnionCity unionCity = (MapUnionCity) obj;
					defender.getInfo().setName(unionCity.getKey());
					defender.setType(FightVersus.Fight_TARGET_TYPE_CITY);
				} else {
					defender.copy(obj.getInfo());
				}
				defender.getInfo().setPosition(expedite.getTargetPosition());
				result.add(uft);
			}
			List<MassTroops> masses = mapWorld.getRelevanceRoleMasses(member.getUid());
			for (int j = 0; j < masses.size(); j++) {
				MassTroops mass = masses.get(j);
				int targetPos = mass.getTargetInfo().getPosition();
				MapObject obj = mapWorld.searchObject(targetPos);
				if (obj == null) {
					continue;
				}
				UnionFightTransformData uft = new UnionFightTransformData();
				uft.setMaxMassNum(mass.getMaxNum());
				uft.setMassTimer(mass.getEndTimer());
				uft.setGrids(mass.getGrids());
				uft.setState(mass.isExpedite() ? UnionFightTransformData.UNION_FIGHT_STATE_GOING
						: UnionFightTransformData.UNION_FIGHT_STATE_STILL);
				FightVersus defender = uft.getDefender();
				defender.copy(mass.getTargetInfo());
				if (obj instanceof MapUnionCity) {
					MapUnionCity unionCity = (MapUnionCity) obj;
					defender.getInfo().setName(unionCity.getKey());
					defender.setType(FightVersus.Fight_TARGET_TYPE_CITY);
				} else {
					defender.copy(mass.getTargetInfo());
				}
				uft.setType(UnionFightTransformData.UNION_FIGHT_TYPE_MASS);
				result.add(uft);
			}
		}
		return result;
	}

	public void gmRecord(boolean isWin, boolean isMass, boolean attOrdef) {
		record.record(isWin, isMass, attOrdef);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + id;
	}

	public synchronized void updateBuildBuff(MapUnionOther build, int type) {
		for (int i = 0; i < members.size(); i++) {
			UnionMember memeber = members.get(i);
			Role role = world.getRole(memeber.getUid());
			if (type == 1) {// 添加buff
				build.addBuff(role, level);
			} else if (type == 2) {// 更新buff
				build.removeBuff(role);
				build.addBuff(role, build.getLevel() + 1);
			} else if (type == 3) {// 删除buff
				build.removeBuff(role);
			}
		}
	}

	public synchronized void notifyAttackCitySucc(MapUnionCity unionCity) {
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ATTACK_CITY_SUCC;
			}
		};
		module.add(unionCity.getPosition());
		rms.addModule(module);
		for (int i = 0; i < members.size(); i++) {
			UnionMember member = members.get(i);
			Role role = world.getOnlineRole(member.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role);
			}
		}
	}

	public void computeReword(Npccity npc, Map<Long, Integer> damages) {
		List<AttackerDamage> temp = new ArrayList<AttackerDamage>();
		for (Long key : damages.keySet()) {
			Role role = world.getRole(key.longValue());
			if (role.getUnionId() == id) {
				AttackerDamage ad = new AttackerDamage();
				ad.setUid(key.longValue());
				ad.setNum(damages.get(key).intValue());
				temp.add(ad);
			}
		}
		Collections.sort(temp);
		// 伤害排名奖励
		List<String> hrs = npc.getHurtreward();
		int max = Math.min(3, temp.size());
		for (int i = 0; i < max; i++) {
			AttackerDamage ad = temp.get(i);
			String items = hrs.get(i);
			String[] iss = items.split(":");
			BriefItem bi = new BriefItem();
			bi.setItemType(iss[0]);
			bi.setItemId(iss[1]);
			bi.setNum(Integer.parseInt(iss[2]));
			List<BriefItem> bis = new ArrayList<BriefItem>();
			bis.add(bi);
			String cityName = I18nGreeting.search(npc.getCityname());
			String content = I18nGreeting.search("gongchengshadijiangli", cityName, i + 1);
			chatMgr.creatSystemEmail("gongchengshadijiangli_title", content, bis, ad.getUid());
			Role role = world.getRole(ad.getUid());
			LogManager.pvpLog(role, Long.valueOf(npc.getId()), EventName.AllianceWar.getName(),
					EventName.OffensiveNPCCity.getName(), (byte) 1, iss[1], Integer.parseInt(iss[2]));
		}
		List<String> nrs = npc.getReward();
		for (int i = 0; i < members.size(); i++) {
			UnionMember memeber = members.get(i);
			List<BriefItem> bis = new ArrayList<BriefItem>();
			for (int j = 0; j < nrs.size(); j++) {
				String rs = nrs.get(j);
				String[] nss = rs.split(":");
				BriefItem bi = new BriefItem();
				bi.setItemType(nss[0]);
				bi.setItemId(nss[1]);
				bi.setNum(Integer.parseInt(nss[2]));
				bis.add(bi);
				Role role = world.getRole(memeber.getUid());
				LogManager.pvpLog(role, Long.valueOf(npc.getId()), EventName.AllianceWar.getName(),
						EventName.OffensiveNPCCity.getName(), (byte) 1, nss[1], Integer.parseInt(nss[2]));
			}
			String cityName = I18nGreeting.search(npc.getCityname());
			String content = I18nGreeting.search("gongchengjiangli", cityName);
			chatMgr.creatSystemEmail("gongchengjiangli_title", content, bis, memeber.getUid());
		}
	}

	/**
	 * 设置联盟初始的位置为盟主的坐标位置
	 */
	public void setUnionPosition() {
		UnionMember member = getLeader();
		if (member == null) {
			GameLog.error("getLeader is fail ,unionId = " + id);
			return;
		}
		Role role = world.getRole(member.getUid());
		if (role == null || role.getCity(0) == null) {
			GameLog.error("getRole is fail ,uid = " + member.getUid());
			return;
		}
		setPosition(role.getCity(0).getPosition());
	}
}
