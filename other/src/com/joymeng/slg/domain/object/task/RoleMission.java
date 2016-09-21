package com.joymeng.slg.domain.object.task;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.RoleTaskType.TaskConditionType;
import com.joymeng.slg.domain.object.task.data.Task2;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class RoleMission implements Instances {
	long id;// 数据库主键id
	// 任务id
	String missionId;
	// 任务条件类型
	int conditionId;
	// 大分支类型
	String type;
	// 小分支类型
	int branchId;
	// 完成条件
	List<String> condition;
	// 任务当前进度
	int schedule;
	// 任务当前状态0-未完成，1-已完成,2-已领取
	byte awardStatus;
	// 0-状态任务，1-触发任务,2-累积任务
	byte isActive;
	// 更新时间
	long updateTime;

	long uid;

	public RoleMission() {
		
	}

	public RoleMission(long uid) {
		this.uid = uid;
	}
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public String getMissionId() {
		return missionId;
	}
	
	public void setMissionId(String missionId) {
		this.missionId = missionId;
	}

	public List<String> getCondition() {
		return condition;
	}

	public void setCondition(List<String> condition) {
		this.condition = condition;
	}

	public int getSchedule() {
		return schedule;
	}

	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}

	public byte getAwardStatus() {
		return awardStatus;
	}

	public void setAwardStatus(byte awardStatus) {
		this.awardStatus = awardStatus;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

//	public byte getIsActive() {
//		return isActive;
//	}
//
//	public void setIsActive(byte isActive) {
//		this.isActive = isActive;
//	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getConditionId() {
		return conditionId;
	}

	public void setConditionId(int conditionId) {
		this.conditionId = conditionId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object obj) {
		try {
			return (T) obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 检查任务是否完成
	public void checkTaskState(Role role,Object... params) {
		if (this.awardStatus != 0) {
			return;
		}
		try{
			TaskConditionType condType = TaskConditionType.valueof(conditionId);
			switch (condType) {
			case C_BD_LVL: {
				String cBuildId = condition.get(0);
				byte cLevel = Byte.parseByte(condition.get(1));
				if (params.length == 0) {
					List<RoleCityAgent> agents = role.getCityAgents();
					for (int i = 0 ; i < agents.size() ; i++){
						RoleCityAgent agent = agents.get(i);
						int level = agent.checkBuildLevelByBuildId(cBuildId);
						schedule = level;
						if (schedule >= cLevel) {
							schedule = cLevel;
							awardStatus = 1;
						}
					}
				} else {
					int cityId = (int) params[0];
					String buildId = (String) params[1];
					byte level = (byte) params[2];
					if (buildId.equals(condition.get(0))) {
						if (level == 0) {// 拆除建筑
							RoleCityAgent agent = role.getCity(cityId);
							int newlevel = agent.checkBuildLevelByBuildId(cBuildId);
							schedule = newlevel;
							if (schedule >= cLevel) {
								schedule = cLevel;
								awardStatus = 1;
							}
						} else {
							if (level > schedule) {
								schedule = level;
							}
							if (schedule >= cLevel) {
								schedule = cLevel;
								awardStatus = 1;
							}
						}
					}
				}
				break;
			}
			case C_BD_NUM:// 2 建造/升级某个建筑多少个/多少级
			{
				String cBuildId = condition.get(0);
				int cNum = Integer.parseInt(condition.get(1));
				int cLevel = Integer.parseInt(condition.get(2));
				List<RoleCityAgent> agents = role.getCityAgents();
				for (int i = 0 ; i < agents.size() ; i++){
					RoleCityAgent agent = agents.get(i);
					int num = 0;
					List<RoleBuild> builds = agent.searchBuildByBuildId(cBuildId);
					for (int j = 0 ; j < builds.size() ; j++){
						RoleBuild build = builds.get(j);
						if (build.getLevel() >= cLevel) {
							num++;
						}
					}
					schedule = num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_TECH_LVL:// 3 研究某个科技达到多少级
			{
				String cTechId = condition.get(0);
				int cLevel = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					List<RoleCityAgent> agents = role.getCityAgents();
					for (int i = 0 ; i < agents.size() ; i++){
						RoleCityAgent agent = agents.get(i);
						int level = agent.getTechAgent().getTechLevel(cTechId);
						if (level > schedule) {
							schedule = level;
						}
						if (schedule >= cLevel) {
							schedule = cLevel;
							awardStatus = 1;
						}
					}
				} else {
					String techId = (String) params[0];
					int level = (int) params[1];
					if (techId.equals(condition.get(0))) {
						if (level > schedule) {
							schedule = level;
						}
						if (schedule >= cLevel) {
							schedule = cLevel;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_RS_MAX:// 4 研究X个科技到最高级
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					List<RoleCityAgent> agents = role.getCityAgents();
					for (int i = 0 ; i < agents.size() ; i++){
						RoleCityAgent agent = agents.get(i);
						int num = agent.getTechAgent().getMaxLevelTechs();
						if (num > schedule) {
							schedule = num;
						}
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_RS_CNT:// 5 科技研究成功累计多少次
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int researchTime = role.getRoleStatisticInfo().getResearchTimes();
					if (researchTime > schedule) {
						schedule = researchTime;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_RL_SKL:// 6 升级成功某个主角技能
			{
				String cSkillId = condition.get(0);
				if (params.length == 0) {
					int level = role.getSkillAgent().getSkillLevel(cSkillId);
					if (level > 0) {
						schedule = 1;
						awardStatus = 1;
					}
				} else {
					String skillId = (String) params[0];
					if (skillId.equals(cSkillId)) {
						int level = (int) params[1];
						if(level > 0){
							schedule = 1;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_SOLD_ULK:// 7 兵种科技树解锁某个兵种
			{
				String cSoldId = condition.get(0);
				if (params.length == 0) {
					int level = role.getSoldUnlockLvl(cSoldId);
					if (level > 0) {
						schedule = 1;
						awardStatus = 1;
					}
				} else {
					String soldId = (String) params[0];
					if (soldId.equals(cSoldId)) {
						schedule = 1;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_S_TRN_S:// 8 某子类兵训练/生产多少个
			{
				int cSoldierType = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getTrainNumBySoldierType(cSoldierType);
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					String armyId = (String) params[0];
					Army army = dataManager.serach(Army.class, armyId);
					if (army.getSoldiersType() == cSoldierType) {
						int num = (int) params[1];
						schedule += num;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_S_TRN_B:// 9 某大类兵训练/生产多少个
			{
				int cArmyType = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getTrainNumByArmyType(cArmyType);
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					String armyId = (String) params[0];
					Army army = dataManager.serach(Army.class, armyId);
					if (army.getArmyType() == cArmyType) {
						int num = (int) params[1];
						schedule += num;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_S_TRN_A:// 10 所有兵训练/生产多少个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getTrainNum();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					int num = (int) params[1];
					schedule += num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
				// case C_S_CUE_T:// 11 某类兵治疗/维修多少个
			case C_S_CUE:// 12 治疗成功多少士兵
			{
				int cNum = Integer.parseInt(condition.get(0));
				int num = role.getRoleStatisticInfo().getCureNum();
				if (num > schedule) {
					schedule = num;
				}
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_S_REP:// 13 维修成功多少机械
			{
				int cNum = Integer.parseInt(condition.get(0));
				int num = role.getRoleStatisticInfo().getRepairNum();
				if (num > schedule) {
					schedule = num;
				}
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
				// case C_S_TRP_T://14 建造某类陷阱多少个
				// case C_S_TRP://15 建造所有陷阱多少个
			case C_RESS_OTP:// 16 某种资源当前产量达到多少/小时
			{
				String cResType = condition.get(0);
				if (params.length != 0) {
					String resType = (String) params[1];
					if (!resType.equals(cResType))
						break;
				}
				int cNum = Integer.parseInt(condition.get(1));
				for (int i = 0 ; i < role.getCityAgents().size() ; i++){
					RoleCityAgent city = role.getCityAgents().get(i);
					long num = city.getResourceProduction(cResType);
					if (num > schedule) {
						schedule = (int) num;
					}
					if (schedule > cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_RESS_HAT:// 17 收集城内建筑的某种资源达到多少
			case C_RESS_CLT:// 18 采集某种资源达到多少
			case C_RESS_ROB:// 19 掠夺某种资源达到多少
			{
				String strType = condition.get(0);
				ResourceTypeConst cResType = ResourceTypeConst.search(strType); 
				long cNum = Long.parseLong(condition.get(1));
				if (params.length == 0) {
					long num = 0;
					if (condType == TaskConditionType.C_RESS_HAT) {
						if (role.getRoleStatisticInfo().getHarvestsMap().get(cResType) != null) {
							num = role.getRoleStatisticInfo().getHarvestsMap().get(cResType);
						}
					} else if (condType == TaskConditionType.C_RESS_CLT) {
						if (role.getRoleStatisticInfo().getCollectsMap().get(cResType) != null) {
							num = role.getRoleStatisticInfo().getCollectsMap().get(cResType);
						}
					} else if (condType == TaskConditionType.C_RESS_ROB) {
						if (role.getRoleStatisticInfo().getRobsMap().get(cResType) != null) {
							num = role.getRoleStatisticInfo().getRobsMap().get(cResType);
						}
					}
					if (num > schedule) {
						schedule = (int) num;
					}
					if (num >= cNum) {
						schedule = (int) cNum;
						awardStatus = 1;
					}
				} else {
					String resType = (String) params[0];
					if (resType.equals(cResType.getKey())) {
						long num = (long)params[1];
						schedule += num;
						if (schedule >= cNum) {
							schedule = (int) cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_ATK_WIN:// 20 自己进攻玩家城市的战斗中胜利多少次
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getAttackWinTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_DEF_WIN:// 21 自己被玩家攻击的防御战中胜利多少次
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getDefenceWinTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_FIT_MST_T:// 22 自己击杀某个怪物多少次
			{
				String mosterId = condition.get(0);
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					if (role.getRoleStatisticInfo().getKillsMap().get(mosterId) != null) {
						int num = role.getRoleStatisticInfo().getKillsMap().get(mosterId);
						if (num > schedule) {
							schedule = num;
						}
						if (num >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				} else {
					String targetId = (String) params[1];
					if (targetId.equals(mosterId)) {
						schedule += 1;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_FIT_MST:// 23 自己击杀所有怪物次数
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getKillMonsters();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ATK_S_NUM:// 24 自己击杀所有玩家士兵的总数
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getKillSoldsNum();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					int num = (int) params[0];
					schedule += num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_OCP_RES_T:// 25 占领某类某级以上的资源地块X个
			{
				int cLevel = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				// TODO 资源地块的类型 检查
				if (params.length == 0) {

				} else {
					int level = (int) params[1];
					if (level >= cLevel) {
						schedule += 1;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_OCP_RES_A:// 26 占领资源地块总数达到X个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {

				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_FORT_NUM:// 27 自己当前拥有X个要塞
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = mapWorld.getFortresses(role.getId()).size();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ROLE_LVL:// 28 指挥官达到多少级
			{
				int cLevel = Integer.parseInt(condition.get(0));
				byte level = role.getLevel();
				if (level > schedule) {
					schedule = level;
				}
				if (level >= cLevel) {
					schedule = cLevel;
					awardStatus = 1;
				}
				break;
			}
			case C_VIP_LVL:// 29 指挥官VIP达到多少级
			{
				int cLevel = Integer.parseInt(condition.get(0));
				byte level = role.getVipInfo().getVipLevel();
				if (level > schedule) {
					schedule = level;
				}
				if (level >= cLevel) {
					schedule = cLevel;
					awardStatus = 1;
				}
				break;
			}
			case C_RL_FF_A:// 30 个人部队战斗力达到多少
			{
				int cNum = Integer.parseInt(condition.get(0));
				int num = role.getRoleStatisticInfo().getRoleArmyFight();
				schedule = num;
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_RL_FF:// 31 个人总战斗力达到多少
			{
				int cNum = Integer.parseInt(condition.get(0));
				int num = role.getRoleStatisticInfo().getRoleFight();
				schedule = num;
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_S_NUM_T:// 32 当前拥有某种类型的兵多少个
			{
				byte cType = Byte.parseByte(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				int num = 0;
				for (int i = 0 ; i < role.getCityAgents().size() ; i++){
					RoleCityAgent city = role.getCityAgents().get(i);
					num = city.getCityArmys().getCityArmysNumByArmyType(cType);
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_MS_WIN:// 33 个人发起集结进攻并胜利次数
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getMassWinTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_HLP_DEF:// 34 帮助盟友驻防并胜利次数
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getHelpDefenceWinTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_HLP_MS_WIN:// 35 帮助盟友进行集结战斗并胜利次数
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getHelpMassWinTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_HLP_KIL_NUM:// 36 自己驻守的部队消灭敌军总数多少个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getHelpDefenceKillNum();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					int num = (int) params[0];
					schedule += num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_MS_KIL_NUM:// 37 自己集结的部队消灭敌军总数多少个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getMassKillNum();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					int num = (int) params[0];
					schedule += num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_HLP_ALLI:// 38 帮助其它联盟成员进行加速X次
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getHelpTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_UNLOK_BLD:// 39 解锁X块外城区域
			{
				int cNum = Integer.parseInt(condition.get(0));
				for (int i = 0 ; i < role.getCityAgents().size() ; i++){
					RoleCityAgent city = role.getCityAgents().get(i);
					int num = city.getLandIds().size() - 3;
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_EQP_GAN_T:// 40 获得某个品质的装备X件
			{
				int cType = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getEquipsByQuality(cType);
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					String equipId = (String) params[0];
					Equip equip = dataManager.serach(Equip.class, equipId);
					if (equip != null && equip.getEquipQuality() == cType) {
						schedule += 1;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_EQP_GAN_ALL:// 41 获得多少件装备
			{
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getEquipsNum();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					int num = (int) params[1];
					schedule += num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_EQP_DRS_SUT:// 42 穿戴X件某个品质的装备
			{
				int cType = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				int num = role.getBagAgent().getEquipsNumByQuality(cType);
				schedule = num;
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_EQP_UP_TM:// 43 成功升级多少件装备
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getEquipUpTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_EQP_REF_TM:// 44 炼化多少次装备
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getEquiplhTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_EQP_DCP_TM:// 45 分解多少件装备
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getEquipfjTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_MTL_PRD_NUM:// 46 生产多少个材料
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getMaterialProdNums();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_A_LVL:// 47 联盟升级到X级
			{
				int cLevel = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					if (role.getUnionId() != 0) {
						UnionBody uby = unionManager.search(role.getUnionId());
						if (uby != null) {
							int level = uby.getLevel();
							schedule = level;
							if (schedule >= cLevel) {
								schedule = cLevel;
								awardStatus = 1;
							}
						}
					}
				} else {
					int level = (int) params[0];
					if (level > schedule) {
						schedule = level;
					}
					if (schedule >= cLevel) {
						schedule = cLevel;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_A_MEM_NUM:// 48 联盟成员数量达到X个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (role.getUnionId() != 0) {
					UnionBody uby = unionManager.search(role.getUnionId());
					if (uby != null) {
						int num = uby.getMembers().size();
						if (num > schedule) {
							schedule = num;
						}
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_A_BUD_NUM:// 49 联盟拥有X座某级建筑物
			{
				int cLevel = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (role.getUnionId() != 0) {
					int num = Instances.mapWorld.searchUnionBuildsNum(role.getUnionId(), cLevel);
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_A_TECH_LVL:// 50 联盟某个科技升级到某级
			{
				String techId = condition.get(0);
				int cLevel = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					if (role.getUnionId() != 0) {
						if (unionManager.search(role.getUnionId()) != null) {
							int level = unionManager.search(role.getUnionId()).getUnionTechById(techId);
							schedule = level;
							if (schedule >= cLevel) {
								schedule = cLevel;
								awardStatus = 1;
							}
						}
					}
				} else {
					long unionId = (long) params[0];
					String uTechId = (String) params[1];
					int level = (int) params[2];
					if (role.getUnionId() == unionId && uTechId.equals(techId)) {
						if (level > schedule) {
							schedule = level;
						}
						if (schedule >= cLevel) {
							schedule = cLevel;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_A_OCP_CT_CNT:// 51 联盟占领过某级以上的城市多少座
			{
				int cLevel = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					if (role.getUnionId() != 0) {
						UnionBody ubody = unionManager.search(role.getUnionId());
						if (ubody != null) {
							int num = ubody.getUsInfo().getCitysNumByLevel(cLevel);
							if (num > schedule) {
								schedule = num;
							}
							if (schedule >= cNum) {
								schedule = cNum;
								awardStatus = 1;
							}
						}
					}
				} else {
					int level = (int) params[0];
					if (level >= cLevel) {
						schedule += 1;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_A_FIT_FOC:// 52 联盟总战斗力达到多少
			{
				long cNum = Long.parseLong(condition.get(0));
				if (role.getUnionId() != 0) {
					UnionBody body = unionManager.search(role.getUnionId());
					if (body != null) {
						long num = body.getUsInfo().getUnionFight() / 1000 + (body.getUsInfo().getUnionFight() / 1000.0 > 0 ? 1 : 0);
						schedule = (int) num;
						if (schedule >= cNum) {
							schedule = (int) cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_A_KIL_NUM:// 53 联盟总击杀士兵达到多少数量
			{
				long cNum = Long.parseLong(condition.get(0));
				if (role.getUnionId() != 0) {
					UnionBody ubody = unionManager.search(role.getUnionId());
					if (ubody != null) {
						long num = ubody.getUsInfo().getKillSoldNum();
						if (num > schedule) {
							schedule = (int) num;
						}
						if (schedule >= cNum) {
							schedule = (int) cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_CN_OCP_NUM_T:// 54 王国内某级以上资源地被占领达到多少块
			{
				break;
			}
			case C_CN_OCP_NUM:// 55 王国内资源地总共被占领达到多少块
			{
				break;
			}
			case C_CN_ALLI_NUM:// 56 王国内人数超过50人的联盟达到X个
			{
				int cMemSize = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				int num = unionManager.searchUnionsNum(cMemSize);
				if (num > schedule) {
					schedule = num;
				}
				if (num >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_CN_KIL_NUM:// 57 王国内所有玩家共击杀某个怪X个
			{
				String cMosterId = condition.get(0);
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = (int)worldSInfo.getKillMonster(cMosterId);
					schedule = num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					String mosterId = (String) params[1];
					if (mosterId.equals(cMosterId)) {
						schedule += 1;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_CN_OCP_C_NUM_T:// 58 王国内某级以上城市被占领多少个
			{
				int cLevel = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					int num = worldSInfo.getOcpCitysByLevel(cLevel);
					schedule = num;
					if (schedule >= cNum) {
						awardStatus = 1;
					}
				} else {
					int level = (int) params[0];
					if (level >= cLevel) {
						schedule += 1;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_CN_OCP_C_NUM:// 59 王国内城市总计被占领多少个
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = worldSInfo.getAllOcpCitys();
					schedule = num;
					if (schedule >= cNum) {
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_CN_FF_ANUM:// 60 王国内联盟战斗力超过多少X的联盟达到多少个
			{
				long cFight = Long.parseLong(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				int num = unionManager.getUnionFightsNum(cFight);
				if (num > schedule) {
					schedule = num;
				}
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_ALLI_HN:// 61 获得X点联盟个人荣誉度
			{
				long cNum = Long.parseLong(condition.get(0));
				if (params.length == 0) {					
					int num = role.getRoleStatisticInfo().getLeagueGlory();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = (int) cNum;
						awardStatus = 1;
					}
				} else {
					long num = (long) params[0];
					schedule = (int) num;
					if (schedule >= cNum) {
						schedule = (int) cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_SPY_NUM:// 62 成功侦查X次
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getSpyTimes();
					if (num > schedule) {
						schedule = num;
					}
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_RL_AL_POS:// 63 在联盟中担任某个职位
			{
				String cPos = condition.get(0);
				if (params.length == 0) {
					if (role.getUnionId() != 0) {
						UnionBody body = unionManager.search(role.getUnionId());
						if (body != null) {
							UnionMember member = body.getUnionMemberById(role.getId());
							if (member != null) {
								if (cPos.equals(member.getAllianceKey())) {
									schedule = 1;
									awardStatus = 1;
								}
							}
						}
					}
				} else {
					int index = (int) params[0];
					int cIndex = Integer.parseInt(cPos);
					if (index == cIndex) {
						schedule = 1;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_RL_CN_POS:// 64 在国家中担任某个职位
			{
				break;
			}
			case C_BD_LVL_NUM:// 65拥有X级的建筑物X个
			{
				int cLevel = Integer.parseInt(condition.get(0));
				int cNum = Integer.parseInt(condition.get(1));
				for (int i = 0 ; i < role.getCityAgents().size() ; i++){
					RoleCityAgent city = role.getCityAgents().get(i);
					int num = city.getBuildsNumByLevel(cLevel);
					schedule = num;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_BD_FT_NUM:// 66累计建造过X座要塞
			{
				int cNum = Integer.parseInt(condition.get(0));
				if (params.length == 0) {
					int num = role.getRoleStatisticInfo().getBuildFortNum();
					if (num > schedule) {
						schedule = num;
					}
					if (num >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				} else {
					schedule += 1;
					if (schedule >= cNum) {
						schedule = cNum;
						awardStatus = 1;
					}
				}
				break;
			}
			case C_S_TRN_ID:// 67某个ID的士兵训练/生产多少个
			{
				String cArmyId = condition.get(0);
				int cNum = Integer.parseInt(condition.get(1));
				if (params.length == 0) {
					if (role.getRoleStatisticInfo().getTrainsMap().get(cArmyId) != null) {
						int num = role.getRoleStatisticInfo().getTrainsMap().get(cArmyId);
						if (num > schedule) {
							schedule = num;
						}
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				} else {
					String armyId = (String) params[0];
					int num = (int) params[1];
					if (armyId.equals(cArmyId)) {
						schedule += num;
						if (schedule >= cNum) {
							schedule = cNum;
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_CN_ALL_KIL_NUM: //70    全国击杀怪物次数
			{
				int cNum = Integer.parseInt(condition.get(0));
				int num = (int) worldSInfo.getAllKillMonster();
				schedule = num;
				if (schedule >= cNum) {
					schedule = cNum;
					awardStatus = 1;
				}
				break;
			}
			case C_SIGN_CNT:	//100	30日签到
			{
				if(params.length != 0){
					schedule = 1;
					awardStatus = 1;
				}
				break;
			}
			case C_ONLINE_CNT://101	领取在线奖励
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_LOGIN_CNT://102	领取连续登陆奖励
			{
				if(params.length != 0){
					schedule = 1;
					awardStatus = 1;
				}
				break;
			}
			case C_LUCKY_CNT://103	幸运转盘
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ITEM_USE_ID://104	使用道具
			{
				String cItemId = condition.get(0);
				int stage = Integer.parseInt(condition.get(1));
				if(params.length != 0){
					String itemId = (String)params[0];
					long num = (long)params[1];
					if(cItemId.equals(itemId)){
						schedule += num;
						if(schedule >= stage){
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_ACC_TRAIN://105	训练生产加速
			case C_ACC_CURE://106	治疗维修加速
			case C_BUILD_LVL://107	升级建筑
			case C_RESEARCH://108	研究科技
			case C_ACC_BUILD://109	建筑升级加速
			case C_ACC_TECH://110   研究科技加速
			case C_BUY_MARK://114	购买黑市商品
			case C_MTL_SYNTH://115	合成材料
			case C_CHAT_WORLD://116	世界聊天
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ALLI_JX://111	联盟科技捐献
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ALLI_LB://112	领取联盟礼包
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_ALLI_PS://113	参加联盟跑商
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_VIP_ACTIVE://117	激活VIP
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}else{
					if(role.getVipInfo().isActive()){
						schedule += 1;
						if(schedule >= stage){
							awardStatus = 1;
						}
					}
				}
				break;
			}
			case C_BUY_ITEM://118	购买道具
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					int  num = (int)params[0];
					schedule += num;
					if(schedule >= stage){
						awardStatus = 1;
						schedule = stage;
					}
				}
				break;
			}
			case C_RECHARGE:	//119	充值
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_CARBON_N:	//120	通过普通副本
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_CARBON_H://121	通关困难副本
			{
				int stage = Integer.parseInt(condition.get(0));
				if(params.length != 0){
					schedule += 1;
					if(schedule >= stage){
						awardStatus = 1;
					}
				}
				break;
			}
			case C_USE_ITEM_T://122  使用某类道具
			{
				byte cItemType = Byte.parseByte(condition.get(0));
				int stage = Integer.parseInt(condition.get(1));
				if(params.length != 0){
					byte itemType = (byte) params[0];
					if (itemType == cItemType) {
						long num = (long) params[1];
						schedule += num;
						if (schedule >= stage) {
							awardStatus = 1;
							schedule = stage;
						}
					}
				}
				break;
			}
			default:
				break;
			}
		}catch(Exception e){
			GameLog.error("check Task object error!");
			e.printStackTrace();
		}
	}
	
	public int getDailySchedule(){
		Task2 taskData = dataManager.serach(Task2.class, missionId);
		if(taskData == null){
			return -1;
		}
		int index = schedule/taskData.getStage();
		for(String strReward : taskData.getRewardList()){
			String[] arr = strReward.split(":");
			ResourceTypeConst type = ResourceTypeConst.search(arr[0]);
			if(type == ResourceTypeConst.RESOURCE_TYPE_POINT){
				index *= Integer.parseInt(arr[1]);
			}
		}
		return index;
	}
}
