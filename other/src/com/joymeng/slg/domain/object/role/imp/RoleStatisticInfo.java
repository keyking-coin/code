package com.joymeng.slg.domain.object.role.imp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.MissionManager;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.task.RoleTaskType.TaskConditionType;
import com.joymeng.slg.domain.object.technology.RoleTechAgent;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class RoleStatisticInfo implements DaoData, Instances{
	long uid;
	int roleFight = 0;
	int roleBuildFight = 0;
	int roleTechFight = 0;
	int roleArmyFight = 0;
	int fightWinTimes = 0;//打怪胜利次数
	int fightFailTimes = 0;//打怪失败次数
//	int killMsterSold = 0;//杀怪的兵数量
	int attackWinTimes = 0;//攻击玩家胜利次数
	int attackFailTimes = 0;//攻击玩家失败次数
	int defenceWinTimes = 0;//防御胜利次数
	int defenceFailTimes = 0;//防御失败次数
	int helpDefenceWinTimes = 0;//驻防部队胜利次数
	int massWinTimes = 0;//集结战斗胜利次数
	int helpMassWinTimes = 0;//参与集结战斗胜利次数
	int helpDefenceKillNum = 0;//驻防的部队消灭敌军总数多少个
	int massKillNum = 0;	//自己集结的部队消灭敌军总数多少个
	int spyTimes = 0;		//成功侦查次数
	int researchTimes = 0;	//科技研究次数
	int killSoldsNum = 0;	//消灭玩家士兵数量
	int deadSoldNum = 0;	//部队损失数量
	int helpTimes = 0;		//联盟帮助次数，帮助其他玩家
	int buildFortNum = 0;	//累计建造要塞数量
	//装备信息
	int equipUpTimes = 0;//升级成功次数
	int equiplhTimes = 0;//炼化成功次数
	int equipfjTimes = 0;//分解成功次数
	int materialProdNums = 0;//生产材料数量
	Map<String, Integer> equipsMap = new HashMap<String, Integer>();//获得装备记录
	//战斗信息/杀怪信息
	Map<String, Integer> killsMap = new HashMap<String, Integer>();//自己击杀某个怪物多少次
	//士兵信息
	Map<String, Integer> trainsMap = new HashMap<String, Integer>();
	Map<String, Integer> curesMap = new HashMap<String, Integer>();
	//资源信息
	Map<ResourceTypeConst, Long> harvestsMap = new HashMap<ResourceTypeConst, Long>();
	Map<ResourceTypeConst, Long> collectsMap = new HashMap<ResourceTypeConst, Long>();
	Map<ResourceTypeConst, Long> robsMap = new HashMap<ResourceTypeConst, Long>();
	boolean savIng = false;
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getRoleFight() {
		return roleFight;
	}
	public void setRoleFight(int roleFight) {
		this.roleFight = roleFight;
	}
	public int getRoleBuildFight() {
		return roleBuildFight;
	}
	public void setRoleBuildFight(int roleBuildFight) {
		this.roleBuildFight = roleBuildFight;
	}
	public int getRoleTechFight() {
		return roleTechFight;
	}
	public void setRoleTechFight(int roleTechFight) {
		this.roleTechFight = roleTechFight;
	}
	public int getRoleArmyFight() {
		return roleArmyFight;
	}
	public void setRoleArmyFight(int roleArmyFight) {
		this.roleArmyFight = roleArmyFight;
	}
	public int getFightWinTimes() {
		return fightWinTimes;
	}
	public void setFightWinTimes(int fightWinTimes) {
		this.fightWinTimes = fightWinTimes;
	}
	public int getFightFailTimes() {
		return fightFailTimes;
	}
	public void setFightFailTimes(int fightFailTimes) {
		this.fightFailTimes = fightFailTimes;
	}
//	public int getKillMsterSold() {
//		return killMsterSold;
//	}
//	public void setKillMsterSold(int killMsterSold) {
//		this.killMsterSold = killMsterSold;
//	}
	public int getAttackWinTimes() {
		return attackWinTimes;
	}
	public void setAttackWinTimes(int attackWinTimes) {
		this.attackWinTimes = attackWinTimes;
	}
	public int getAttackFailTimes() {
		return attackFailTimes;
	}
	public void setAttackFailTimes(int attackFailTimes) {
		this.attackFailTimes = attackFailTimes;
	}
	public int getDefenceWinTimes() {
		return defenceWinTimes;
	}
	public void setDefenceWinTimes(int defenceWinTimes) {
		this.defenceWinTimes = defenceWinTimes;
	}
	public int getDefenceFailTimes() {
		return defenceFailTimes;
	}
	public void setDefenceFailTimes(int defenceFailTimes) {
		this.defenceFailTimes = defenceFailTimes;
	}
	public int getHelpDefenceWinTimes() {
		return helpDefenceWinTimes;
	}
	public void setHelpDefenceWinTimes(int helpDefenceWinTimes) {
		this.helpDefenceWinTimes = helpDefenceWinTimes;
	}
	public int getMassWinTimes() {
		return massWinTimes;
	}
	public void setMassWinTimes(int massWinTimes) {
		this.massWinTimes = massWinTimes;
	}
	public int getHelpMassWinTimes() {
		return helpMassWinTimes;
	}
	public void setHelpMassWinTimes(int helpMassWinTimes) {
		this.helpMassWinTimes = helpMassWinTimes;
	}
	public int getHelpDefenceKillNum() {
		return helpDefenceKillNum;
	}
	public void setHelpDefenceKillNum(int helpDefenceKillNum) {
		this.helpDefenceKillNum = helpDefenceKillNum;
	}
	public int getMassKillNum() {
		return massKillNum;
	}
	public void setMassKillNum(int massKillNum) {
		this.massKillNum = massKillNum;
	}
	public int getSpyTimes() {
		return spyTimes;
	}
	public void setSpyTimes(int spyTimes) {
		this.spyTimes = spyTimes;
	}
	public int getResearchTimes() {
		return researchTimes;
	}
	public void setResearchTimes(int researchTimes) {
		this.researchTimes = researchTimes;
	}
	public int getKillSoldsNum() {
		return killSoldsNum;
	}
	public void setKillSoldsNum(int killSoldsNum) {
		this.killSoldsNum = killSoldsNum;
	}
	public int getDeadSoldNum() {
		return deadSoldNum;
	}
	public void setDeadSoldNum(int deadSoldNum) {
		this.deadSoldNum = deadSoldNum;
	}
	public int getHelpTimes() {
		return helpTimes;
	}
	public void setHelpTimes(int helpTimes) {
		this.helpTimes = helpTimes;
	}
	public int getBuildFortNum() {
		return buildFortNum;
	}
	public void setBuildFortNum(int buildFortNum) {
		this.buildFortNum = buildFortNum;
	}
	public int getEquipUpTimes() {
		return equipUpTimes;
	}
	public void setEquipUpTimes(int equipUpTimes) {
		this.equipUpTimes = equipUpTimes;
	}
	public int getEquiplhTimes() {
		return equiplhTimes;
	}
	public void setEquiplhTimes(int equiplhTimes) {
		this.equiplhTimes = equiplhTimes;
	}
	public int getEquipfjTimes() {
		return equipfjTimes;
	}
	public void setEquipfjTimes(int equipfjTimes) {
		this.equipfjTimes = equipfjTimes;
	}
	public int getMaterialProdNums() {
		return materialProdNums;
	}
	public void setMaterialProdNums(int materialProdNums) {
		this.materialProdNums = materialProdNums;
	}
	public Map<String, Integer> getEquipsMap() {
		return equipsMap;
	}
	public void setEquipsMap(Map<String, Integer> equipsMap) {
		this.equipsMap = equipsMap;
	}
	public Map<String, Integer> getKillsMap() {
		return killsMap;
	}
	public void setKillsMap(Map<String, Integer> killsMap) {
		this.killsMap = killsMap;
	}
	public Map<String, Integer> getTrainsMap() {
		return trainsMap;
	}
	public void setTrainsMap(Map<String, Integer> trainsMap) {
		this.trainsMap = trainsMap;
	}
	public Map<String, Integer> getCuresMap() {
		return curesMap;
	}
	public void setCuresMap(Map<String, Integer> curesMap) {
		this.curesMap = curesMap;
	}
	public Map<ResourceTypeConst, Long> getHarvestsMap() {
		return harvestsMap;
	}
	public void setHarvestsMap(Map<ResourceTypeConst, Long> harvestsMap) {
		this.harvestsMap = harvestsMap;
	}
	public Map<ResourceTypeConst, Long> getCollectsMap() {
		return collectsMap;
	}
	public void setCollectsMap(Map<ResourceTypeConst, Long> collectsMap) {
		this.collectsMap = collectsMap;
	}
	public Map<ResourceTypeConst, Long> getRobsMap() {
		return robsMap;
	}
	public void setRobsMap(Map<ResourceTypeConst, Long> robsMap) {
		this.robsMap = robsMap;
	}
	
	@Override
	public String table() {
		return DaoData.TABLE_RED_ALERT_STATIC;
	}
	
	@Override
	public String[] wheres() {
		return new String[]{DaoData.RED_ALERT_GENERAL_UID};
	}
	
	@Override
	public boolean delete() {
		return false;
	}
	
	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}
	
	@Override
	public void save() {
		if (savIng){
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}
	
	@Override
	public void loadFromData(SqlData data) {
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		roleFight = data.getInt(DaoData.RED_ALERT_S_RF);
		roleBuildFight = data.getInt(DaoData.RED_ALERT_S_RBF);
		roleTechFight = data.getInt(DaoData.RED_ALERT_S_RTF);
		roleArmyFight = data.getInt(DaoData.RED_ALERT_S_RAF);
		fightWinTimes = data.getInt(DaoData.RED_ALERT_S_FWT);
		fightFailTimes = data.getInt(DaoData.RED_ALERT_S_FFT);
//		killMsterSold = data.getInt(DaoData.RED_ALERT_S_KMS);
		attackWinTimes = data.getInt(DaoData.RED_ALERT_S_AWT);
		attackFailTimes = data.getInt(DaoData.RED_ALERT_S_AFT);
		defenceWinTimes = data.getInt(DaoData.RED_ALERT_S_DWT);
		defenceFailTimes = data.getInt(DaoData.RED_ALERT_S_DFT);
		helpDefenceWinTimes = data.getInt(DaoData.RED_ALERT_S_HDWT);
		helpDefenceKillNum = data.getInt(DaoData.RED_ALERT_S_HDKN);
		massKillNum = data.getInt(DaoData.RED_ALERT_S_MKN);
		helpMassWinTimes = data.getInt(DaoData.RED_ALERT_S_HMWT);
		spyTimes = data.getInt(DaoData.RED_ALERT_S_ST);
		researchTimes = data.getInt(DaoData.RED_ALERT_S_RT);
		killSoldsNum = data.getInt(DaoData.RED_ALERT_S_KSN);
		deadSoldNum = data.getInt(DaoData.RED_ALERT_S_DSN);
		helpTimes = data.getInt(DaoData.RED_ALERT_S_FWT);
		buildFortNum = data.getInt(DaoData.RED_ALERT_S_BFN);
		equipUpTimes = data.getInt(DaoData.RED_ALERT_S_EUT);
		equiplhTimes = data.getInt(DaoData.RED_ALERT_S_ELT);
		equipfjTimes = data.getInt(DaoData.RED_ALERT_S_EFT);
		materialProdNums = data.getInt(DaoData.RED_ALERT_S_MN);
		
		String esmStr = data.getString(DaoData.RED_ALERT_S_ESM);
		equipsMap = JSON.parseObject(esmStr,new TypeReference<Map<String, Integer>>(){});
		String ksmStr = data.getString(DaoData.RED_ALERT_S_KSM);
		killsMap = JSON.parseObject(ksmStr,new TypeReference<Map<String, Integer>>(){});
		String tsmStr = data.getString(DaoData.RED_ALERT_S_TSM);
		trainsMap = JSON.parseObject(tsmStr,new TypeReference<Map<String, Integer>>(){});
		String csmStr = data.getString(DaoData.RED_ALERT_S_CSM);
		curesMap = JSON.parseObject(csmStr,new TypeReference<Map<String, Integer>>(){});
		String hsmStr = data.getString(DaoData.RED_ALERT_S_HSM);
		harvestsMap = JSON.parseObject(hsmStr,new TypeReference<Map<ResourceTypeConst, Long>>(){});
		String cltsmStr = data.getString(DaoData.RED_ALERT_S_CLTSM);
		collectsMap = JSON.parseObject(cltsmStr,new TypeReference<Map<ResourceTypeConst, Long>>(){});
		String rsmStr = data.getString(DaoData.RED_ALERT_S_RSM);
		robsMap = JSON.parseObject(rsmStr,new TypeReference<Map<ResourceTypeConst, Long>>(){});
	}
	@Override
	public void saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_GENERAL_UID,uid);
		data.put(DaoData.RED_ALERT_S_RF, roleFight);
		data.put(DaoData.RED_ALERT_S_RBF, roleBuildFight);
		data.put(DaoData.RED_ALERT_S_RTF, roleTechFight);
		data.put(DaoData.RED_ALERT_S_RAF, roleArmyFight);
		data.put(DaoData.RED_ALERT_S_FWT, fightWinTimes);
		data.put(DaoData.RED_ALERT_S_FFT, fightFailTimes);
//		data.put(DaoData.RED_ALERT_S_KMS, killMsterSold);
		data.put(DaoData.RED_ALERT_S_AWT, attackWinTimes);
		data.put(DaoData.RED_ALERT_S_AFT, attackFailTimes);
		data.put(DaoData.RED_ALERT_S_DWT, defenceWinTimes);
		data.put(DaoData.RED_ALERT_S_DFT, defenceFailTimes);
		data.put(DaoData.RED_ALERT_S_HDWT, helpDefenceWinTimes);
		data.put(DaoData.RED_ALERT_S_HDKN, helpDefenceKillNum);
		data.put(DaoData.RED_ALERT_S_MKN, massKillNum);
		data.put(DaoData.RED_ALERT_S_ST, spyTimes);
		data.put(DaoData.RED_ALERT_S_KSN, killSoldsNum);
		data.put(DaoData.RED_ALERT_S_DSN, deadSoldNum);
		data.put(DaoData.RED_ALERT_S_AHT, helpTimes);
		data.put(DaoData.RED_ALERT_S_BFN, buildFortNum);
		data.put(DaoData.RED_ALERT_S_RT, researchTimes);
		data.put(DaoData.RED_ALERT_S_EUT, equipUpTimes);
		data.put(DaoData.RED_ALERT_S_ELT, equiplhTimes);
		data.put(DaoData.RED_ALERT_S_EFT, equipfjTimes);
		data.put(DaoData.RED_ALERT_S_MN, materialProdNums);
		data.put(DaoData.RED_ALERT_S_HMWT, helpMassWinTimes);
		
		data.put(DaoData.RED_ALERT_S_ESM, JsonUtil.ObjectToJsonString(equipsMap));
		data.put(DaoData.RED_ALERT_S_KSM, JsonUtil.ObjectToJsonString(killsMap));
		data.put(DaoData.RED_ALERT_S_TSM, JsonUtil.ObjectToJsonString(trainsMap));
		data.put(DaoData.RED_ALERT_S_CSM, JsonUtil.ObjectToJsonString(curesMap));
		data.put(DaoData.RED_ALERT_S_HSM, JsonUtil.ObjectToJsonString(harvestsMap));
		data.put(DaoData.RED_ALERT_S_CLTSM, JsonUtil.ObjectToJsonString(collectsMap));
		data.put(DaoData.RED_ALERT_S_RSM, JsonUtil.ObjectToJsonString(robsMap));
	}
	
	/**
	 * 获取所有训练士兵数量 
	 */
	public int getTrainNum(){
		int num = 0;
		for(int value : trainsMap.values()){
			num += value;
		}
		return num;
	}
	/**
	 * 获取某类型的训练士兵数量 soldierType
	 */
	public int getTrainNumBySoldierType(int typeId){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : trainsMap.entrySet()){
			Army army = dataManager.serach(Army.class, mapset.getKey());
			if(army != null){
				if(army.getSoldiersType() == typeId){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	
	/**
	 * 获取某类型的训练士兵数量 soldierType
	 */
	public int getTrainNumByArmyType(int typeId){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : trainsMap.entrySet()){
			Army army = dataManager.serach(Army.class, mapset.getKey());
			if(army != null){
				if(army.getArmyType() == typeId){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	
	/**
	 * 获取所有治疗士兵数量 
	 */
	public int getCureNum(){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : curesMap.entrySet()){
			Army army = dataManager.serach(Army.class, mapset.getKey());
			if(army != null){
				if(army.getArmyType() == 1){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	public int getRepairNum(){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : curesMap.entrySet()){
			Army army = dataManager.serach(Army.class, mapset.getKey());
			if(army != null){
				if(army.getArmyType() != 1){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	/**
	 * 获取某类型的士兵治疗数量 armyType
	 */
	public int getCureNumByArmyType(int typeId){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : curesMap.entrySet()){
			Army army = dataManager.serach(Army.class, mapset.getKey());
			if(army != null){
				if(army.getArmyType() == typeId){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	
	/**
	 * 获取玩家击杀所有怪物的数量
	 */
	public int getKillMonsters(){
		int num = 0;
		for(int value : killsMap.values()){
			num += value;
		}
		return num;
	}
	
	/**
	 * 获取某种品质的装备数量
	 */
	public int getEquipsByQuality(int type){
		int num = 0;
		for(Map.Entry<String, Integer> mapset : equipsMap.entrySet()){
			Equip equip = dataManager.serach(Equip.class, mapset.getKey());
			if(equip != null){
				if(equip.getEquipQuality() == type){
					num += mapset.getValue();
				}
			}
		}
		return num;
	}
	
	public int getEquipsNum(){
		int num = 0;
		for(int value : equipsMap.values()){
			num += value;
		}
		return num;
	}
	
	private void updateRoleFight(Role role) {
		int fight = roleTechFight + roleBuildFight + roleArmyFight;
		if (role != null && role.getUnionId() != 0) {
			UnionBody body = unionManager.search(role.getUnionId());
			if (body != null) {
				body.getUsInfo().updateUnionFight(fight - roleFight);
				List<Role> roles = world.getListObjects(Role.class);
				for (int i = 0 ; i <  roles.size() ; i++){
					Role user = roles.get(i);
					user.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_KING_UNIONFIGHT,0);
				}
				for (int i = 0 ; i <  body.getMembers().size() ; i++){
					UnionMember member = body.getMembers().get(i);
					if (member.getUid() == uid) {
						member.setFight(fight);
					}
					Role rm = world.getObject(Role.class,member.getUid());
					if (rm != null) {
						rm.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.COND_ALLI_FIGHT, role.getUnionId());
					}
				}
				body.sendMeToAllMembers(0);
			}
		}
		roleFight = fight;
		role.handleEvent(GameEvent.RANK_ROLE_FIGHT_CHANGE, new TaskEventDelay());
		MissionManager taskAgent = role.getTaskAgent();
		taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);// 玩家总战斗力
		role.sendFrequentVariables();// 下发战斗力变化
	}
	
	public void updataRoleTechFight(Role role) {
		if (role != null) {
			RoleCityAgent cityAgent = role.getCity(0);
			int p3 = 0;// 科技战斗力
			RoleTechAgent roleTechAgent = cityAgent.getTechAgent();
			Map<String, Technology> allTech = roleTechAgent.getTechMap();
			if (allTech != null) {
				for (Technology tech : allTech.values()) {
					if (tech == null) {
						continue;
					}
					final String techId = tech.getTechId();
					final int techLevel = tech.getLevel();
					Techupgrade techupgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
						@Override
						public boolean filter(Techupgrade data) {
							if (data.getTechID().equals(techId) && data.getLevel() == techLevel) {
								return true;
							}
							return false;
						}
					});
					if (techupgrade == null) {
						continue;
					}
					p3 += techupgrade.getAttackForce();
				}
			}
			roleTechFight = p3;
			updateRoleFight(role);
		}
	}

	// 军队战斗力
	public void updataRoleArmyFight(Role role) {
		if (role != null) {
			RoleCityAgent cityAgent = role.getCity(0);
			RoleArmyAgent armyAgent = cityAgent.getCityArmys();
			List<ArmyInfo> cityArmys = armyAgent.getAllCityArmy();
			int p1 = 0;
			if (cityArmys != null){
				for (int i = 0 ; i < cityArmys.size() ; i++){
					ArmyInfo armyInfo = cityArmys.get(i);
					if (armyInfo == null || armyInfo.getState() == ArmyState.ARMY_IN_HOSPITAL.getValue() ||
						armyInfo.getState() == ArmyState.ARMY_DIED.getValue() || 
						armyInfo.getState() == ArmyState.ARMY_REMOVE.getValue()) {
						continue;
					}
					Army army = dataManager.serach(Army.class, armyInfo.getArmyId());
					if (army == null) {
						GameLog.error("read base army is fail");
						continue;
					}
					p1 += (int)(armyInfo.getArmyNum() * army.getFightingForce());
				}
			}
			roleArmyFight = p1;
			updateRoleFight(role);
		}
	}

	public void updataRoleBuildFight(Role role) {
		if (role != null) {
			RoleCityAgent cityAgent = role.getCity(0);
			int p2 = 0;// 建筑战斗力
			List<RoleBuild> allBuild = cityAgent.getBuilds();
			if (allBuild != null){
				for (int i = 0 ; i < allBuild.size() ; i++){
					RoleBuild roleBuild = allBuild.get(i);
					if (roleBuild == null) {
						continue;
					}
					Buildinglevel buildinglevel = roleBuild.getBuildingLevel();
					if (buildinglevel == null) {
						continue;
					}
					p2 += buildinglevel.getAttackForce();
				}
			}
			roleBuildFight = p2;
			updateRoleFight(role);
		}
	}
	
	public void initRoleFight(Role role){
		uid = role.getId();
		RoleCityAgent cityAgent = role.getCity(0);
		List<RoleBuild> allBuild = cityAgent.getBuilds();
		if (allBuild != null){
			for (int i = 0 ; i < allBuild.size() ; i++){
				RoleBuild roleBuild = allBuild.get(i);
				if (roleBuild == null) {
					continue;
				}
				Buildinglevel buildinglevel = roleBuild.getBuildingLevel();
				if (buildinglevel == null) {
					continue;
				}
				roleBuildFight += buildinglevel.getAttackForce();
			}
		}
		roleFight += roleBuildFight;
	}
	
	@Override
	public void over() {
		savIng = false;
	}
	
	@Override
	public boolean saving() {
		return savIng;
	}
	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + uid;
	}
}
