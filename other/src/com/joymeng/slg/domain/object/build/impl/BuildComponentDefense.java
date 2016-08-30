package com.joymeng.slg.domain.object.build.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.object.army.DefenseArmyInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.effect.BuffObject;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

public class BuildComponentDefense implements BuildComponent,Instances {
	private BuildComponentType buildComType;
	byte state;//状态0-正常，1-修理中，2-破损状态
	int defenseValue;//当前生命值
	int defenseStaticValue;//固化值
	String armyId;//防御建筑对应的部队id
	long uid;
	int cityId;
	long buildId;
	
	public BuildComponentDefense(){
		buildComType = BuildComponentType.BUILD_COMPONENT_DEFENSE;
	}
	
	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityID;
		this.buildId = buildId;
		state = 0;
	}
	
	/**
	 * 更新buff
	 * @param preMax
	 */
	public void updateBuffValue(int preMax){
		int nowMax = getDefenceHPVal();
		int change = nowMax - preMax;
		defenseValue += change;
		defenseValue = Math.max(0,defenseValue);
		defenseValue = Math.min(nowMax,defenseValue);
	}
	
	public int getDefenceHPVal(){
		float effect = 0;
		Role role = world.getRole(uid);
		if (role != null){
			RoleCityAgent city = role.getCity(cityId);
			if (city != null){
				RoleBuild build = city.searchBuildById(buildId);
				if (build != null){
					Map<String, BuffObject> buffs = city.getCityBuffs().get(build.getBuildId());
					if (buffs != null){
						String key = TargetType.T_A_IMP_AHP.getName();
						BuffObject bo = buffs.get(key);
						if (bo != null){
							effect = bo.getRate();
						}
					}
				}
			}
		}
		return (int) (defenseStaticValue + effect * defenseStaticValue);
	}
	
	/**
	 * 防御型建筑修理，光棱塔，磁暴线圈等
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @return
	 */
	public boolean repairDefenseArmys(Role role, int cityId, long buildId, int money){
		RoleCityAgent cityAgent = role.getCity(cityId);
		RoleBuild build = cityAgent.searchBuildById(buildId);
		if(build == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		//计时器检查
		if(build.getTimerSize() > 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
		if(buildLevel == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		//防御建筑修理成本=(0.2*损失的生命值/最大生命值)*本级建造成本
		//防御建筑修理时间=(0.2*损失的生命值/最大生命值)*本级建造时间
		int maxHp  = getDefenceHPVal();
		int dropHp = maxHp - defenseValue;
		float rate1 = 0.2f * dropHp / maxHp;
		float rate2 = 0.2f * dropHp / maxHp;
		long time = (long)(rate2 * buildLevel.getTime());
		int costMoney = 0;
		List<String> resCostList = buildLevel.getBuildCostList();
		List<String> newCostList = new ArrayList<String>();
		for (String cost : resCostList){
			String[] cs = cost.split(":");
			long need = (long)(Long.parseLong(cs[1]) * rate1);
			newCostList.add(cs[0] + ":" + need);
		}
		if (money > 0){
			if (money == 2){
				costMoney = cityAgent.getCostMoney(role,newCostList,null,0,(byte)0);
			}else{
				costMoney = cityAgent.getCostMoney(role,newCostList,null,(int)time,(byte)0);
			}
			if (costMoney > role.getMoney()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,costMoney);
				return false;
			}
		}else if(!cityAgent.checkResConditions(role,newCostList)){
			return false;
		}
		//扣除消耗
		List<Object> resLst = cityAgent.redCostResource(newCostList,costMoney,"repairDefenseArmys");
		RespModuleSet rms = new RespModuleSet();
		if (money == 1){
			state = 0;
			setDefenseValue(getDefenceHPVal());
			role.redRoleMoney(costMoney);
			String event ="repairDefenseArmys";
			LogManager.goldConsumeLog(role,costMoney,event);
			role.sendRoleToClient(rms);
		}else{
			 if (money == 2){
				role.redRoleMoney(costMoney);
				String event ="repairDefenseArmys";
				LogManager.goldConsumeLog(role,costMoney,event);
				role.sendRoleToClient(rms);
			}
	    	//添加时间队列
	    	TimerLast timer = build.addBuildTimer(time,TimerLastType.TIME_REP_DEFENSE);
	    	timer.registTimeOver(this);
	    	state = 1;
		}
		build.sendToClient(rms);
		role.sendResourceToClient(rms,cityId,resLst.toArray());
		try {
			if(build.getName().equals("Fence")){
				NewLogManager.buildLog(role, "wall_repair",costMoney);
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}
	
	@Override
	public void tick(Role role,RoleBuild build,long now) {
		
	}

	@Override
	public void deserialize(String str,RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		armyId = map.get("armyId");
		if (StringUtils.isNull(armyId)){
			armyId = "";
		}
		defenseValue = Integer.parseInt(map.get("defenseValue"));
		defenseStaticValue = Integer.parseInt(map.get("defenseStaticValue"));
		TimerLast timer = build.searchTimer(TimerLastType.TIME_REP_DEFENSE);
		if (timer != null) {
			timer.registTimeOver(this);
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		map.put("defenseValue", String.valueOf(defenseValue));
		map.put("armyId", StringUtils.isNull(armyId) ? "null" : armyId);
		map.put("defenseStaticValue", String.valueOf(defenseStaticValue));
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());//String
		params.put(state);//byte 建筑状态
		params.put(defenseValue);//int 当前建筑生命值
		params.put(defenseStaticValue);//int 建筑生命总值
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}
	
	@Override
	public void finish() {
		defenseValue = getDefenceHPVal();
		state = 0;
	}
	
	public synchronized void setDefenseValue(int newValue){
		defenseValue = newValue;
		defenseValue = Math.min(defenseValue,getDefenceHPVal());
		defenseValue = Math.max(0,defenseValue);
	}
	
	public synchronized void change(int value){
		int max = getDefenceHPVal();
		defenseValue += value;
		defenseValue = Math.min(defenseValue,max);
		defenseValue = Math.max(0,defenseValue);
		if (defenseValue < max){//城池状态更新
			state = 2;//破损
		}
	}

	public int getDefenseValue() {
		return defenseValue;
	}
	
	public DefenseArmyInfo getDefenseArmy(){
		if(armyId == null || defenseValue == 0){
			return null;
		}
		DefenseArmyInfo armyInfo = DefenseArmyInfo.create(armyId,1,defenseValue);
		armyInfo.setBuildId(buildId);
		return armyInfo;
	}

	@Override
	public void setBuildParams(RoleBuild build) {
		if(build == null){
			GameLog.error("getbuildbuff error, param is null");
			return;
		}
		int level = build.getLevel();
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), level);
		if(buildLevel == null){
			GameLog.error("cann't get build level buff where buildId="+buildId);
			return;
		}
		armyId = buildLevel.getArmyID();
		Army armydata = dataManager.serach(Army.class, armyId);
		if (armydata == null) {
			GameLog.error("canot find defense build army static data armyId=" + armyId);
		    return;
		}
		int max = getDefenceHPVal();
		int breakValue =  max - defenseValue;//损耗的血量
		defenseStaticValue = (int) armydata.getHitPoints();
		defenseValue = getDefenceHPVal() - breakValue;
	}
	
	public String getArmyId() {
		return armyId;
	}
}
