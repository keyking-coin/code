package com.joymeng.slg.domain.object.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.object.technology.data.Techtree;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.domain.shop.data.Shop;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleSkillAgent implements Instances{
	private long uid;
	private Map<String, RoleSkill> skillMap = new HashMap<String, RoleSkill>();
	private Map<Byte, Integer> branchs = new HashMap<Byte,Integer>();//每个分支的技能点数
	
	public final static String RESET_SKILL_ITEMID="test";
	
	
	public RoleSkillAgent(){
	}
	
	public void setUid(long id){
		uid = id;
		initBranch();
	}
	
	public Map<String, RoleSkill> getSkillMap() {
		return skillMap;
	}

	public void setSkillMap(Map<String, RoleSkill> skillMap) {
		this.skillMap = skillMap;
	}

	public Map<Byte, Integer> getBranchs() {
		return branchs;
	}

	public void setBranchs(Map<Byte, Integer> branchs) {
		this.branchs = branchs;
	}

	public long getUid() {
		return uid;
	}

	public static String getResetSkillItemid() {
		return RESET_SKILL_ITEMID;
	}

	public void loadData(Role role){
		List<SqlData> datas = dbMgr.getGameDao().getDatas(DaoData.TABLE_RED_ALERT_SKILL,DaoData.RED_ALERT_GENERAL_UID,role.getId());
		if (datas == null){
			return;
		}
		for (int i = 0 ; i < datas.size() ; i++){
			SqlData data = datas.get(i);
			loadFromData(role, data);
		}
		//载入分支信息
		loadBranch();
	}
	
	private void loadFromData(Role role, SqlData data) {
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		String skillId = data.getString(DaoData.RED_ALERT_SKILL_ID);
		byte branchId = data.getByte(DaoData.RED_ALERT_SKILL_BRANCHID);
		byte skillType = data.getByte(DaoData.RED_ALERT_SKILL_TYPE);
		int level = data.getInt(DaoData.RED_ALERT_SKILL_LEVEL);
		byte state = data.getByte(DaoData.RED_ALERT_SKILL_STATE);
		RoleSkill roleSkill = new RoleSkill(uid, skillId, level, branchId, skillType, state);
		String str = data.getString(DaoData.RED_ALERT_SKILL_TIME);
		if (skillType == 0 && str != null) { // 重置主动技能的状态
			TimerLast time = JsonUtil.JsonToObject(str, TimerLast.class);
			long leaveTime = (time.getStart() + time.getLast()) - TimeUtils.nowLong() / 1000;
			if (leaveTime > 0) {
				roleSkill.addTimer(time);
			} else {
				roleSkill.setState((byte) 0);
				if (state == 1) {// 由生效转为技能CD状态
					Tech skilldata = dataManager.serach(Tech.class, skillId);
					if (Math.abs(leaveTime) < skilldata.getCdTime()) {
						roleSkill.setState((byte) 2);
						time = new TimerLast(time.getStart() + time.getLast(), skilldata.getCdTime(),TimerLastType.TIME_SKILL_CD);
						roleSkill.addTimer(time);
					}
				}
			}
		}
		skillMap.put(skillId, roleSkill);
	}
	
	private void initBranch(){
		List<Techtree> treeLst = dataManager.serachList(Techtree.class, new SearchFilter<Techtree>(){
			@Override
			public boolean filter(Techtree data){
				if(data.getTechTreeName().equals(TechTreeType.SKILL_TREE.getName())){
					return true;
				}
				return false;
			}
		});
		
		for(Techtree tree : treeLst){
			byte key = Byte.parseByte(tree.getId());
			branchs.put(key,0);
		}
	}
	
	private void loadBranch(){
		for(RoleSkill s : skillMap.values()){
			byte key = s.getBranchId();
			if(branchs.get(key) != null){
				int value = branchs.get(s.getBranchId()) + s.getLevel();
				branchs.put(key, value);
			}
		}
	}
	
	public void serialize(SqlData data){
		JoyBuffer out = JoyBuffer.allocate(4096);
		out.putInt(skillMap.size());
		for(RoleSkill skill : skillMap.values()){
			out.putPrefixedString(skill.getSkillId(),JoyBuffer.STRING_TYPE_SHORT);
			out.putInt(skill.getLevel());
			out.put(skill.getBranchId());
			out.put((byte)(skill.isActive() ? 0 : 1));
			out.put(skill.getState());
			String str = "null";
			if (skill.getTimer() != null) {
				str = JsonUtil.ObjectToJsonString(skill.getTimer());
				out.putPrefixedString(str,JoyBuffer.STRING_TYPE_SHORT);
			}else{
				out.putPrefixedString(str,JoyBuffer.STRING_TYPE_SHORT);
			}
		}
		data.put(DaoData.RED_ALERT_ROLE_SKILLDATAS, out.arrayToPosition());
	}
	
	public void deserialize(Object data){
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		int size = buffer.getInt();
		for(int i=0; i < size; i++){
			String skillId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			int level = buffer.getInt();
			byte branchId = buffer.get();
			byte isActive = buffer.get();
			byte state = buffer.get();
			RoleSkill roleSkill = new RoleSkill(uid, skillId, level, branchId, isActive, state);
			String str = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			if (isActive == 0 && !str.equals("null")) { // 重置主动技能的状态
				TimerLast timer = JsonUtil.JsonToObject(str,TimerLast.class);
				if (timer.over()){
					roleSkill.setState((byte)0);
					if (state == 2){//生效时间结束
						long leaveTime = TimeUtils.nowLong() / 1000 - timer.getStart() - timer.getLast();
						Tech skilldata = dataManager.serach(Tech.class, skillId);
						if (leaveTime < skilldata.getCdTime()) {//还在技能的CD时间
							roleSkill.setState((byte)1);
							timer = new TimerLast(timer.getStart() + timer.getLast(), skilldata.getCdTime(),TimerLastType.TIME_SKILL_CD);
							roleSkill.addTimer(timer);
						}
					}
				}else{
					roleSkill.addTimer(timer);
				}
			}
			skillMap.put(skillId, roleSkill);
		}
		loadBranch();
	}
	
	/**
	 * 获取技能等级
	 * @param skillId
	 * @return
	 */
	public int getSkillLevel(String skillId){
		if( skillMap.get(skillId) == null){
			return 0;
		}
		return skillMap.get(skillId).getLevel();
	}
	/**
	 * 使用技能
	 * @param role
	 * @param skillId
	 * @return
	 */
	public boolean useSkill(Role role, final String skillId){
		RoleSkill roleSkill = skillMap.get(skillId);
		if (roleSkill == null || !roleSkill.checkIsCanUse()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_SKILL_CANNOT_USE, skillId);
			return false;
		}
		Tech tech = dataManager.serach(Tech.class, skillId);
		if(tech == null || tech.getSkillType() == 1){
			GameLog.error("read tech is error ,skillId = " + skillId);
			return false;
		}
		if(roleSkill.isActive()){//主动技能
			if (tech.getLastTime() > 0){//有持续CD
				roleSkill.createTimer(tech.getLastTime(), TimerLastType.TIME_SKILL_LAST);
				roleSkill.setState((byte)2);
			} else {//无持续CD
				roleSkill.createTimer(tech.getCdTime(), TimerLastType.TIME_SKILL_CD);
				roleSkill.setState((byte)1);
			}
		}
		//添加技能效果buff
		ActiveSkillType type = ActiveSkillType.search(skillId);
		switch(type){
		case FOOD_MATCH:
		case SATEL_NAVI:
		case URGENCY_EXPAN:
		case FULL_OF_VIT:
		case CRAZY_COLLECT:
		{
			addSkillBuff(role,roleSkill);
			if (type == ActiveSkillType.CRAZY_COLLECT){
				role.handleEvent(GameEvent.ROLE_RES_BUFF_CHANGE);
				role.handleEvent(GameEvent.TROOPS_SEND);
			}
			break;
		}
		case HIGHEST_ALERT:
		{
			addSkillBuff(role,roleSkill);
//			updateHighestAlert(skillId, false);
//			for (int i = 0 ; i < role.getCityAgents().size() ; i++){
//				RoleCityAgent agent = role.getCityAgents().get(i);
//				Map<ResourceTypeConst, Long> resMap = agent.getCityCurrentRes();
//				List<Object> resLst = new ArrayList<Object>();
//				for(Map.Entry<ResourceTypeConst, Long> res : resMap.entrySet()){
//					resLst.add(res.getKey());
//					resLst.add(res.getValue()*6);
//				}
//				role.addResourcesToCity(agent.getId(), resLst.toArray());
//			}
			break;
		}
		case RES_PROTECT:
		{
			MapCity mapCity = mapWorld.searchMapCity(uid,0);
			if(mapCity != null){
				mapCity.getCityState().setResprotect(true);
			}
			break;
		}
		case FLASH_RETREAT://10秒回城
			List<ExpediteTroops> ets = world.getListObjects(ExpediteTroops.class);
			for (int i = 0 ; i < ets.size() ; i++){
				ExpediteTroops troops = ets.get(i);
				if (troops.isMass() || troops.getLeader().getInfo().getUid() != uid ||
					troops.getTimer().getType() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE ||
					troops.getTimer().getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS){
					//集结部队,去建造迁城点，去建造要塞的除外
					continue;
				}
				troops.backQuick(10);
			}
			List<GarrisonTroops> gts = world.getListObjects(GarrisonTroops.class);
			for (int i = 0 ; i < gts.size() ; i++){
				GarrisonTroops troops = gts.get(i);
				if (troops.getTroops().getInfo().getUid() != uid ||//不是我的部队
					troops.getTimer().getType() == TimerLastType.TIME_MAP_STATION ||//驻扎部队
					troops.getTimer().getType() == TimerLastType.TIME_MAP_MASS ||//集结部队
					troops.getTimer().getType() == TimerLastType.TIME_CREATE//建造部队(要塞或者迁城点)
					){
					continue;
				}
				troops.goBackQuick();
			}
			break;
		case URGENCY_PROD://加6小时资源产量
			for (int i = 0 ; i < role.getCityAgents().size() ; i++){
				RoleCityAgent agent = role.getCityAgents().get(i);
				Map<ResourceTypeConst, Long> resMap = agent.getCityTimesRes(6);
				List<Object> resLst = new ArrayList<Object>();
				for(Map.Entry<ResourceTypeConst, Long> res : resMap.entrySet()){
					resLst.add(res.getKey());
					resLst.add(res.getValue());
				}
				role.addResourcesToCity(agent.getId(), resLst.toArray());
			}
			break;
			default:
				break;
		}
		//下发技能状态更新
		RespModuleSet rms = new RespModuleSet();
		sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}
	
	public void updateHighestAlert(final String skillId, boolean isRemove){
		final RoleSkill roleSkill = skillMap.get(skillId);
		if(roleSkill == null){
			return;
		}
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>(){
			@Override
			public boolean filter( Techupgrade data){
				if (data.getTechID().equals(skillId) && data.getLevel() == roleSkill.getLevel()){
					return true;
				}
				return false;
			}
		});
		if(techUpgrade == null){
			GameLog.error("cannot find techupgrade data where techid="+skillId);
			return;
		}
		List<String> buffLst = techUpgrade.getBuffList();
		if(buffLst == null || buffLst.size() < 2){
			GameLog.error("cannot find techupgrade data where techid="+skillId);
			return;
		}
		MapCity mapCity = mapWorld.searchMapCity(uid,0);
		if(mapCity != null){
			mapCity.getCityState().updateCityViewBuff(isRemove, Integer.parseInt(buffLst.get(1)));
		}
	}
	
	/**
	 * 技能升级
	 * @param techId
	 */
	public boolean skillLevelup(Role role, final String skillId, int points) {
		Tech tech = dataManager.serach(Tech.class, skillId);
		if (tech == null) {
			GameLog.error("cannot find tech data where techid=" + skillId);
			return false;
		}
		// 前置条件检查
		List<String> condList = tech.getPrecedingTechList();
		boolean bSuc = false;
		for (int i = 0; i < condList.size(); i++) {
			String str = condList.get(i);
			if (str.equals("ture")) {
				bSuc = true;
				break;
			} else {
				Tech techCon = dataManager.serach(Tech.class, str);
				if (techCon != null) {
					if (this.getSkillLevel(str) == techCon.getMaxPoints()) {
						bSuc = true;
						break;
					}
				}
			}
		}
		if (!bSuc) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_NO_PRETECH, condList.get(0));
			return false;
		}
		// 升级条件检查
		List<String> limitions = tech.getLimitation();
		if (!role.checkSkillLimition(limitions)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SKILL_LIMITED, skillId);
			return false;
		}
		int needPoints = 0;
		RoleSkill roleSkill = skillMap.get(skillId);
		if (roleSkill == null) {
			needPoints = (points == 0) ? 1 : tech.getMaxPoints();
			roleSkill = new RoleSkill(role.getId(), skillId, 0, tech.getBranchID(), tech.getSkillType(), (byte) 0);
		} else {
			needPoints = (points == 0) ? 1 : (tech.getMaxPoints() - roleSkill.getLevel());
		}
		if (needPoints > role.getSkillPoints()) {
			needPoints = role.getSkillPoints();
		}
		if (needPoints + roleSkill.getLevel() > tech.getMaxPoints()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SKILL_LEVELUP_MAX, skillId);
			return false;
		}
		// 消耗技能点
		// if(role.getSkillPoints() < levelPoint){
		// MessageSendUtil.sendNormalTip(role.getUserInfo(),
		// I18nGreeting.MSG_SKILLPOINTS_NOT_ENOUGH,skillId);
		// return false;
		// }
		final int level = roleSkill.getLevel() + needPoints;
		// 更新技能树
		role.useSkillPoints(needPoints);
		roleSkill.setLevel(level);
		skillMap.put(skillId, roleSkill);
		// 添加技能buff
		if (!roleSkill.isActive()) {
			addSkillBuff(role, roleSkill);
		}
		// 更新分支信息
		if (branchs.get(roleSkill.getBranchId()) != null) {
			int value = branchs.get(roleSkill.getBranchId()) + needPoints;
			branchs.put(roleSkill.getBranchId(), value);
		}
		// 下发技能更新
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_SKILL_LP, skillId,roleSkill.getLevel());
		try {
			NewLogManager.baseEventLog(role, "study_commander_skill",roleSkill.getSkillId(),roleSkill.getLevel());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}
	/**
	 * add buff
	 * @param role
	 * @param skillId
	 * @param level
	 */
	private void addSkillBuff(Role role, final RoleSkill roleSkill){
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>(){
			@Override
			public boolean filter( Techupgrade data){
				if(data.getTechID().equals(roleSkill.getSkillId()) && data.getLevel() == roleSkill.getLevel()){
					return true;
				}
				return false;
			}
		});
		if(techUpgrade == null){
			GameLog.error("cannot find techupgrade data where techid=" + roleSkill.getSkillId());
			return;
		}
		List<String> buffLst = techUpgrade.getBuffList();
		if(buffLst == null || buffLst.size() < 2){
			GameLog.error("cannot find techupgrade data where techid=" + roleSkill.getSkillId());
			return;
		}
		if (roleSkill.getLevel() > 1) {
			role.getEffectAgent().removeSkillBuff(role,roleSkill.getSkillId());
		}
		role.getEffectAgent().addSkillBuff(role,buffLst.get(0), buffLst.get(1), roleSkill.getSkillId(),roleSkill.getTimer(),false);
	}
	
	/**
	 * 重置技能
	 * @param role
	 * @return
	 */
	public boolean resetSkills(Role role, int type){
		if (skillMap.isEmpty()){
			return false;
		}
		final String itemId = "userSkill_resetting";
		Shop shopData = dataManager.serach(Shop.class, new SearchFilter<Shop>(){
			@Override
			public boolean filter(Shop data) {
				return data.getItemid().equals(itemId);
			}
		});
		if(shopData == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_ITEM_USE_LMT,itemId);
			return false;
		}
		for(RoleSkill skill : skillMap.values()){
			if(skill.isActive()){
				if(skill.getState() == 2){
					MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SKILL_ACTIVE_RESET);
					return false;
				}
			}
		}
		RespModuleSet rms = new RespModuleSet();
		if(type ==0){//消耗金币
			if(role.getMoney() < shopData.getNormalPrice()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY,shopData.getNormalPrice());
				return false;
			}
			role.redRoleMoney(shopData.getNormalPrice());
			LogManager.goldConsumeLog(role, shopData.getNormalPrice(), EventName.resetSkills.getName());
			role.sendRoleToClient(rms);
		}else if(type == 1){
			RoleBagAgent agent = role.getBagAgent();
			ItemCell item = agent.getItemFromBag(itemId);
			if(item == null){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ITEM_NOT_ENOUGH,itemId,1);
				return false;
			}
			agent.removeItems(itemId, 1);
			ItemCell itemcell = agent.getItemFromBag(itemId);
			if(itemcell == null){
				itemcell = item;
				itemcell.setNum(0);
			}
			agent.sendItemsToClient(rms, itemcell);
		}
		Iterator<Map.Entry<String,RoleSkill>> it = skillMap.entrySet().iterator();
		while(it.hasNext()){
			RoleSkill s = it.next().getValue();
			//更新buff
			role.getEffectAgent().removeSkillBuff(role,s.getSkillId());
			//返回技能点
			role.addSkillPoints(s.getLevel());
			if(s.getState() == 0){
				it.remove();
			}else{
				s.setLevel(0);
			}
		}
		//重置分支信息
		for(byte key : branchs.keySet()){
			branchs.put(key, 0);
		}
		try {
			NewLogManager.baseEventLog(role, "reset_skill_study_point",shopData.getNormalPrice());
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		role.sendRoleToClient(rms);
		sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}
	
	/**
	 * 发送玩家技能树到客户端
	 * @param rms
	 */
	public void sendToClient(RespModuleSet rms) {
		//技能模块
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_SKILL;
			}
		};
		module.add(skillMap.size());//列表大小， int
		for (RoleSkill roleSkill : skillMap.values()) {
			module.add(roleSkill.getSkillId()); // 技能ID, String
			module.add(roleSkill.getLevel());// 等级, int
			module.add((byte) (roleSkill.isActive() ? 1 : 0));// 是否主动技能
			module.add(roleSkill.getState());// byte 技能状态0-未使用，1-cd中，2-技能生效中
			if (roleSkill.getTimer() == null) {
				roleSkill.setState((byte) 0);
			}
			if (roleSkill.isActive && roleSkill.getState() != 0) {
				roleSkill.getTimer().sendToClient(module.getParams());// 技能cd计时器
			}
		}
		module.getParams().put(branchs.size());//分支信息列表大小
		for(Map.Entry<Byte, Integer> iter : branchs.entrySet()){
			module.add(iter.getKey()); //分支id byte
			module.add(iter.getValue());//分支技能点数  int
		}
		rms.addModule( module );
	}
	
	public void sendSkillChgToClient(RespModuleSet rms, RoleSkill s){
		//技能模块
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_SKILL_CHANGE;
			}
		};
		module.add(s.getSkillId()); //技能ID, String
		module.add(s.getLevel());//等级, int
		module.add((byte)(s.isActive()?1:0));//是否主动技能
		module.add(s.getState());//byte 技能状态0-未使用，1-cd中，2-技能生效中
		if(s.getState() == 1){
			s.getTimer().sendToClient(module.getParams());//技能cd计时器
		}
		module.getParams().put(branchs.size());//分支信息列表大小
		for(Map.Entry<Byte, Integer> iter : branchs.entrySet()){
			module.add(iter.getKey()); //分支id byte
			module.add(iter.getValue());//分支技能点数  int
		}
		rms.addModule(module);
	}
	
	public void tick(Role role){
		for(RoleSkill s : skillMap.values()){
			if(s.isActive()){
				s.tick(role);
			}
		}
	}
}
