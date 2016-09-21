package com.joymeng.slg.domain.event.impl;

import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.imp.RoleStatisticInfo;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.MissionManager;
import com.joymeng.slg.domain.object.task.RoleTaskType.TaskConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class TaskEvent extends AbstractGameEvent{
	
	private void addUnionKillNum(Role role, int num){
		if(role.getUnionId() != 0){
			UnionBody body = unionManager.search(role.getUnionId());
			if(body != null){
				body.getUsInfo().addKillSoldNum(num);
				for(UnionMember member : body.getMembers()){
					Role memRole = world.getObject(Role.class, member.getUid());
					if(memRole != null){
						memRole.getTaskAgent().checkTaskConditions(role,TaskConditionType.C_A_KIL_NUM, num);//杀敌数统计
					}
				}
			}
		}
	}

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.ROLE_CREATE://角色创建
			{
				role.getTaskAgent().setUid(role.getId(), role.getTaskState());
				role.getTaskAgent().initMissions(role);
				role.getDailyTaskAgent().setUid(role.getId());
				role.getDailyTaskAgent().initDailyTask(role);
				break;
			}
			case GameEvent.LOAD_FROM_DB:
			{
				role.getTaskAgent().initMissions(role);
				world.loadDailyTasks(role);
				role.getTaskAgent().checkMission(role);
				break;
			}
			case GameEvent.TASK_CHECK_EVENT:
			{
				ConditionType type = (ConditionType) params[1];
				Object[] newParams = new Object[params.length-2];
				for(int i=0; i< newParams.length;i++){
					newParams[i] = params[i+2];
				}
				RoleStatisticInfo sInfo = role.getRoleStatisticInfo();
				MissionManager taskAgent = role.getTaskAgent();
				try{
					switch(type){
					case COND_BUILD://建筑升级
					{
						sInfo.updataRoleBuildFight(role);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_BD_LVL, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_BD_NUM, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_BD_LVL_NUM, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);//玩家总战斗力
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_BUILD_LVL, 0);//每日任务
						String buildId = (String)newParams[1];
						if (buildId.equals(BuildName.CITY_CENTER.getKey())){
							role.getDailyTaskAgent().openNewDailyTask(role,(byte)newParams[2]);
						}
						break;
					}
					case COND_RESEARCH://科技研究
					{
						boolean isMaxPoint = (boolean)params[5];
						int time = sInfo.getResearchTimes();
						sInfo.setResearchTimes(time + 1);
						sInfo.updataRoleTechFight(role);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_TECH_LVL, newParams);
						if(isMaxPoint){
							taskAgent.checkTaskConditions(role,TaskConditionType.C_RS_MAX, newParams);
						}
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RS_CNT, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);//玩家总战斗力
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_RESEARCH, 0);//每日任务
						break;
					}
					case C_SKILL_LP: //技能解锁
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_SKL, newParams);
						break;
					}
					case COND_SOLD_UNLOK://士兵解锁
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_SOLD_ULK, newParams);
						break;
					}
					case COND_TRAIN://训练/生产
					{
						String armyId = get(params[2]);
						int num = get(params[3]);
						if(num < 0){//解雇
							sInfo.updataRoleArmyFight(role);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_S_NUM_T);//当前玩家拥有某种兵数量
							taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF_A);//玩家部队战斗力
							taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);//玩家总战斗力
							break;
						}
						Map<String, Integer> trainMap = sInfo.getTrainsMap();
						if(trainMap.get(armyId) == null){
							trainMap.put(armyId, num);
						}else{
							trainMap.put(armyId, num + trainMap.get(armyId));
						}
						sInfo.updataRoleArmyFight(role);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_TRN_S, newParams);//某子类兵训练/生产多少个
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_TRN_B, newParams);//某大类兵训练/生产多少个
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_TRN_A, newParams);//所有兵训练/生产多少个
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_TRN_ID, newParams);//某个ID的士兵训练/生产多少个
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_NUM_T);//当前玩家拥有某种兵数量
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF_A);//玩家部队战斗力
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);//玩家总战斗力
						break;
					}
					case COND_RESOURCE://资源产量变化
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RESS_OTP, newParams);
						break;
					}
					case COND_HEAL://治疗
					{
						List<ArmyInfo> armys = get(params[3]);					
						Map<String, Integer> curesMap = sInfo.getCuresMap();
						for(ArmyInfo army : armys){
							if(curesMap.get(army.getArmyId()) == null){
								curesMap.put(army.getArmyId(), army.getArmyNum());
							}else{
								curesMap.put(army.getArmyId(), army.getArmyNum() + curesMap.get(army.getArmyId()));
							}
						}
						sInfo.updataRoleArmyFight(role);
//						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_CUE_T, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_CUE, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_REP, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_NUM_T);//当前玩家拥有某种兵数量
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);//玩家总战斗力
						break;
					}
					case COND_VIP://vip等级提升
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_VIP_LVL, newParams);
						break;
					}
					case COND_LEVEL://指挥官等级提升
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_ROLE_LVL, newParams);
						break;
					}
					case COND_ALLI_HELP://联盟帮助次数
					{
						int cnt = sInfo.getHelpTimes();
						sInfo.setHelpTimes(cnt + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_HLP_ALLI);
						break;
					}
					case COND_RES_HARVEST://收获资源 
					{
						String str = get(params[2]);
						ResourceTypeConst resType = ResourceTypeConst.search(str);
						if(resType == null)
							break;
						long num = get(params[3]);
						Map<ResourceTypeConst, Long> harvestsMap = sInfo.getHarvestsMap();
						if(harvestsMap.get(resType) == null){
							harvestsMap.put(resType, num);
						}else{
							harvestsMap.put(resType, num + harvestsMap.get(resType));
						}
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RESS_HAT, resType.getKey(), num);
						break;
					}
					case COND_EQUIP_LVLUP://装备升级
					{
						int time = sInfo.getEquipUpTimes();
						sInfo.setEquipUpTimes(time + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_UP_TM, newParams);
						//TODD
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_EQP_UP_TM, 0);//每日任务
						break;
					}
					case COND_EQUIP_REFIN://装备炼化
					{
						int time = sInfo.getEquiplhTimes();
						sInfo.setEquiplhTimes(time + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_REF_TM, newParams);
						break;
					}				
					case COND_EQUIP_RESOLV://装备分解
					{
						int time = sInfo.getEquipfjTimes();
						sInfo.setEquipfjTimes(time + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_DCP_TM, newParams);
						break;
					}
					case COND_MATERIAL_PROD://材料生产
					{
						int time = sInfo.getMaterialProdNums();
						sInfo.setMaterialProdNums(time + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_MTL_PRD_NUM, newParams);
						break;
					}
					case COND_GET_EQUIP://获得装备
					{
						String key = get(params[2]);
						int num = get(params[3]);
						Map<String, Integer> equipsMap = sInfo.getEquipsMap();
						if(equipsMap.get(key) == null){
							equipsMap.put(key, num);
						}else{
							equipsMap.put(key, num + equipsMap.get(key));
						}
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_GAN_T, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_GAN_ALL, newParams);
						break;
					}
					case COND_EQUIP_WIELD://穿上装备
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_EQP_DRS_SUT, newParams);
						break;
					}
					case COND_FORT: //成功建造要塞    
					{
						int num = sInfo.getBuildFortNum();
						sInfo.setBuildFortNum(num + 1);
						
//						RespModuleSet rms = new RespModuleSet();
//						role.sendGameConfigToClient(rms);
//						MessageSendUtil.sendModule(rms,role.getUserInfo());
						
						taskAgent.checkTaskConditions(role,TaskConditionType.C_FORT_NUM, newParams);//当前
						taskAgent.checkTaskConditions(role,TaskConditionType.C_BD_FT_NUM, newParams);//累计
						break;
					}
					case C_RESS_CLT:	//18 采集某种资源达到多少			参数：资源类型，数量
					{
						ResourceTypeConst resType = get(params[2]);
						int num = get(params[3]);
						Map<ResourceTypeConst, Long> collectsMap = sInfo.getCollectsMap();
						if (collectsMap.get(resType) == null) {
							collectsMap.put(resType, (long) num);
						} else {	
							collectsMap.put(resType, num + collectsMap.get(resType));
						}
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RESS_CLT, resType.getKey(), (long)num);
						break;
					}
					
					case C_RESS_ROB:   //19	      掠夺某种资源达到多少	
					{
						ResourceTypeConst resType = get(params[2]);
						int num = get(params[3]);
						Map<ResourceTypeConst, Long> robsMap = sInfo.getRobsMap();
						if (robsMap.get(resType) == null) {
							robsMap.put(resType, (long) num);
						} else {
							robsMap.put(resType, (long) num + robsMap.get(resType));
						}
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RESS_ROB, resType.getKey(), (long)num);
						break;
					}
					
					
					case C_ATK_WIN:		//20 自己进攻玩家基地的战斗			参数：战斗结果
					{
						boolean isWin = get(params[2]);
						if (isWin) {
							sInfo.setAttackWinTimes(sInfo.getAttackWinTimes() + 1);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_ATK_WIN, sInfo.getAttackWinTimes());// 进攻胜利次数
						} else {
							sInfo.setAttackFailTimes(sInfo.getAttackFailTimes() + 1);
						}
						break;
					}
					case C_DEF_WIN:		//21 自己基地被玩家攻击的防御战		参数：战斗结果
					{
						boolean bWin = get(params[2]);
						if(bWin){
							sInfo.setDefenceWinTimes(sInfo.getDefenceWinTimes() + 1);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_DEF_WIN, sInfo.getDefenceWinTimes());//防守胜利次数
						}else{
							sInfo.setDefenceFailTimes(sInfo.getDefenceFailTimes() + 1);
						}
						break;
					}
					case C_FIT_MST_T:	//22自己击杀某个怪物		参数：战斗结果，怪物Id
					{
						boolean bWin = get(params[2]);
						String monsterId = get(params[3]);
						if (bWin) {
							worldSInfo.addKillMonster(monsterId); //攻击某种怪物的次数
							sInfo.setAttackWinTimes(sInfo.getAttackWinTimes() + 1);
							Map<String, Integer> killsMap = sInfo.getKillsMap();
							if (killsMap.get(monsterId) == null) {
								killsMap.put(monsterId, 1);
							} else {
								killsMap.put(monsterId, killsMap.get(monsterId) + 1);
							}
							for (Role player : world.getOnlineRoles()) {
								if (player.getId() == role.getId()) {
									continue;
								}
								player.getTaskAgent().checkTaskConditions(role,TaskConditionType.C_CN_KIL_NUM, newParams);
								player.getTaskAgent().checkTaskConditions(role,TaskConditionType.C_CN_ALL_KIL_NUM);
							}
							taskAgent.checkTaskConditions(role,TaskConditionType.C_CN_KIL_NUM, newParams);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_CN_ALL_KIL_NUM);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_FIT_MST_T, newParams);// 杀怪数
							taskAgent.checkTaskConditions(role,TaskConditionType.C_FIT_MST, newParams);// 总杀怪数
						} else {
							sInfo.setAttackFailTimes(sInfo.getAttackFailTimes() + 1);
						}
						break;
					}
					case C_MS_WIN: // 33 个人发起集结进攻		 参数：战斗结果，杀兵数,死兵数，资源类型，数量
					{
						boolean bWin = get(params[2]);
						if (bWin) {
							sInfo.setMassWinTimes(sInfo.getMassWinTimes() + 1);
							sInfo.setAttackWinTimes(sInfo.getAttackWinTimes() + 1);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_MS_WIN, newParams);
						}
						break;
					}
					case C_HLP_MS_WIN://参与集结
					{
						boolean bWin = get(params[2]);
						if (bWin) {
							sInfo.setHelpMassWinTimes(sInfo.getHelpMassWinTimes() + 1);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_HLP_MS_WIN, newParams);
						}
						break;
					}
					case C_HLP_DEF:		//34帮助盟友驻防			参数：战斗结果
					{
						boolean bWin = get(params[2]);
						if(bWin){
							sInfo.setHelpDefenceWinTimes(sInfo.getHelpDefenceWinTimes() + 1);
							taskAgent.checkTaskConditions(role,TaskConditionType.C_HLP_DEF, newParams);
						}
						break;
					}
					case C_FIGHT_RESULT://所有战斗， 统计结果    参数：战斗类型，杀兵数，死兵数，资源类型，数量
					{
						byte fightType = get(params[2]);
						int killNum = get(params[3]);
						int deadNum = get(params[4]);
						sInfo.setDeadSoldNum(sInfo.getDeadSoldNum() + deadNum);
						if(fightType != Const.ATK_MST){
							sInfo.setKillSoldsNum(sInfo.getKillSoldsNum() + killNum);	
							role.handleEvent(GameEvent.RANK_ROLEKILLENEMY_CHANGE, new TaskEventDelay());
							if(fightType == Const.DEF_HLP_CY){
								sInfo.setHelpDefenceKillNum(sInfo.getHelpDefenceKillNum() + killNum);
								taskAgent.checkTaskConditions(role,TaskConditionType.C_HLP_KIL_NUM, killNum);
							}else if(fightType == Const.MS_ATK || fightType == Const.MS_HLP_ATK){
								sInfo.setMassKillNum(sInfo.getMassKillNum() + killNum);
								taskAgent.checkTaskConditions(role,TaskConditionType.C_MS_KIL_NUM, killNum);
							}
							taskAgent.checkTaskConditions(role,TaskConditionType.C_ATK_S_NUM, killNum);//杀死玩家的总士兵数统计
							addUnionKillNum(role, killNum);
							//TODO 玩家杀敌数有更新
							role.sendCommanderInfo();
						}
						break;
					}
					case C_FIGHT_BACK://所有战斗部队返回基地    参数：抢夺资源类型，数量
					{
						sInfo.updataRoleArmyFight(role);	
//						if (params.length > 0) {
//							List<Object> reses = get(params[2]);
//							for (int i = 0; i < reses.size();) {
//								String resource = (String) reses.get(i);
//								ResourceTypeConst resType = ResourceTypeConst.search(resource);
//								i++;
//								int num = (int) reses.get(i);
//								Map<ResourceTypeConst, Long> robsMap = sInfo.getRobsMap();
//								if (robsMap.get(resType) == null) {
//									robsMap.put(resType, (long) num);
//								} else {
//									robsMap.put(resType, (long) num + robsMap.get(resType));
//								}
//								taskAgent.checkTaskConditions(role, TaskConditionType.C_RESS_ROB, resType.getKey(), num);
//								i++;
//							}
//						}
						//战斗力更新
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF_A);// 部队战斗力
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_FF);// 玩家总战斗力
						taskAgent.checkTaskConditions(role,TaskConditionType.C_S_NUM_T);// 当前玩家拥有某种兵数量
						break;
					}
					case C_SPY_NUM:		//34 成功侦查				参数：结果
					{
						sInfo.setSpyTimes(sInfo.getSpyTimes() + 1);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_SPY_NUM, newParams);//侦查次数统计
						break;
					}
					case C_OCP_RES_T:	//25 占领某类某级以上的资源地块	参数：资源地类型，等级
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_OCP_RES_T, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_OCP_RES_A, newParams);
						break;
					}
					case COND_ALLI_LVLUP: //联盟升级
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_LVL, newParams);
						break;
					}
					case COND_ALLI_ADD://添加联盟成员
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_MEM_NUM, newParams);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_FIT_FOC);
						break;
					} 
					case COND_ALLI_TECH://联盟科技升级
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_TECH_LVL, newParams);
						break;
					}
					case COND_ALLI_SCORE://玩家的联盟贡献度
					{
						int score = (int) newParams[0];
						int league = role.getRoleStatisticInfo().getLeagueGlory();
						role.getRoleStatisticInfo().setLeagueGlory(league + score);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_ALLI_HN);
						break;
					}
					case COND_ALLI_BUILD://联盟建筑升级
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_BUD_NUM, newParams);
						break;
					}
					case COND_ALLI_POS://玩家在联盟中任职
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_RL_AL_POS, newParams);
						break;
					}
					case COND_ALLI_OCP_CITY://联盟占领城池
					{
						int cityLevel = get(params[2]);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_OCP_CT_CNT, cityLevel);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_CN_OCP_C_NUM_T, cityLevel);
						taskAgent.checkTaskConditions(role,TaskConditionType.C_CN_OCP_C_NUM, cityLevel);
						break;
					}
					case COND_ALLI_FIGHT://联盟总战斗力
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_A_FIT_FOC, newParams);
						break;
					}
					case C_UNLOK_FIELD://外城地块解锁
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_UNLOK_BLD, 0);
						break;
					}
					case C_KING_UNIONFIGHT:
					{
						taskAgent.checkTaskConditions(role,TaskConditionType.C_CN_FF_ANUM, newParams);
						break;
					}
					case C_SIGN_CNT:	//100	30日签到
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_SIGN_CNT, 0);
						break;
					}
					case C_ONLINE_CNT://101	领取在线奖励
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ONLINE_CNT, 0);
						break;
					}
					case C_LOGIN_CNT://102	领取连续登陆奖励
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_LOGIN_CNT, 0);
						break;
					}
					case C_LUCKY_CNT://103	幸运转盘
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_LUCKY_CNT, 0);
						break;
					}
					case C_ITEM_USE://104	使用道具
					{
						String itemId = get(newParams[0]);
						long num = get(newParams[1]);
						Item data = dataManager.serach(Item.class, itemId);
						if(data != null){
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_USE_ITEM_T, data.getItemType(), num);
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ITEM_USE_ID, newParams);
						}else{
							GameLog.error("task event check item use error, itemid=" + itemId);
						}
						break;
					}
					case C_ACC_BUILD://109	建筑升级加速
					{
						TimerLastType timeType = get(newParams[0]);
						switch(timeType){
						case TIME_CREATE:
						case TIME_LEVEL_UP:
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ACC_BUILD, 0);
							break;
						case TIME_TRAIN:
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ACC_TRAIN, 0);
							break;
						case TIME_CURE:
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ACC_CURE, 0);
							break;
						case TIME_RESEARCH:
							role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ACC_TECH, 0);
							break;
						default:
							break;
						}
						break;
					}
					case C_ALLI_JX://111	联盟科技捐献
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ALLI_JX, 0);
						break;
					}
					case C_ALLI_LB://112	领取联盟礼包
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ALLI_LB, 0);
						break;
					}
					case C_ALLI_PS://113	参加联盟跑商
					{
						int num = get(newParams[0]);
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_ALLI_PS, num);
						break;
					}
					case C_BUY_MARK://114	购买黑市商品
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_BUY_MARK, newParams);
						break;
					}
					case C_MTL_SYNTH://115	合成材料
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_MTL_SYNTH, 0);
						break;
					}
					case C_CHAT_WORLD://116	世界聊天
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_CHAT_WORLD, 0);
						break;
					}
					case C_VIP_ACTIVE://117	激活VIP
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_VIP_ACTIVE, 0);
						break;
					}
					case C_BUY_ITEM://118	购买道具
					{
						int num = get(newParams[0]);
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_BUY_ITEM, num);
						break;
					}
					case C_RECHARGE:	//119	充值
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_RECHARGE, 0);
						break;
					}
					case C_CARBON_N:	//120	通过普通副本
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_CARBON_N, 0);
						break;
					}
					case C_CARBON_H://121	通关困难副本
					{
						role.getDailyTaskAgent().checkTaskConditions(role,TaskConditionType.C_CARBON_N, 0);
						break;
					}
					default:
						break;
					}
				}catch(Exception e){
					GameLog.error("check task event error!");
					e.printStackTrace();
				}
			}
			default: 
				break;
		}
	}

}
