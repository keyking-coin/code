package com.joymeng.slg.domain.object.build.impl;

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
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.technology.RoleTechAgent;
import com.joymeng.slg.domain.object.technology.Technology;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 研究组件
 * 
 * @author tanyong
 *
 */
public class BuildComponentResearch implements BuildComponent, Instances {
	private BuildComponentType buildComType;
	long roleId;
	byte state;
	String sTechId;
	///
	int cityId;
	long buildId;
	public BuildComponentResearch() {
		buildComType = BuildComponentType.BUILD_COMPONENT_RESEARCH;
		state = (byte) 0;
		sTechId = "";
	}

	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		state = (byte) 0;
		sTechId = "";
		roleId = uid;
		this.cityId = cityID;
		this.buildId = buildId;
	}
	
	public String getsTechId() {
		return sTechId;
	}

	public void setsTechId(String sTechId) {
		this.sTechId = sTechId;
	}

	@Override
	public void setBuildParams(RoleBuild build) {

	}
	
	public String getBuildParams(RoleBuild build){
		if(build != null){
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
			if(buildLevel == null){
				GameLog.error("read buildLevel base is fail");
				return "0";
			}
			if (buildLevel.getParamList().size() > 0) {
				if (build.getBuildId().equals(BuildName.MILITARY_FACT.getKey())) {
					return buildLevel.getParamList().get(1);
				} else {
					return buildLevel.getParamList().get(0);
				}
			}
		}
		return null;
	}
	/**
	 * 升级科技
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param techId
	 * @param money
	 * @return
	 * @throws Exception 
	 */
	public boolean upgradeTech(Role role, int cityId, long buildId, final String techId, int money) throws Exception {
		RoleCityAgent agent = role.getCity(cityId);
		RoleTechAgent techAgent = agent.getTechAgent();
		if (techAgent == null) {
			GameLog.error("get tech agent error where uid=" + role.getId());
			return false;
		}
		// 倒计时检查，一个建筑同时只能有一个倒计时
		RoleBuild build = agent.searchBuildById(buildId);
		if (build.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		// 检查科技的前置条件
		Tech tech = dataManager.serach(Tech.class, techId);
		if (tech == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_NO_PRETECH, techId);
			return false;
		}
		// 检查科技的升级条件
		List<String> limitions = tech.getLimitation();
		if (!agent.checkTechLimition(limitions)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_LIMITED, techId);
			return false;
		}
		// 检查科技前置条件
		List<String> condList = tech.getPrecedingTechList();
		boolean bSuc = false;
		for (int i = 0; i < condList.size(); i++) {
			String str = condList.get(i);
			if (str.equals("ture") || techAgent.getTechLevel(str) > 0) {
				bSuc = true;
				break;
			}
		}
		if (!bSuc) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_NO_PRETECH, condList.get(0));
			return false;
		}
		// 检查等级
		final int curLevel = techAgent.getTechLevel(techId);
		if (curLevel >= tech.getMaxPoints()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_LEVEL_MAX, techId);
			return false;
		}
		// 检查资源
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>() {
			@Override
			public boolean filter(Techupgrade data) {
				if (data.getTechID().equals(techId) && data.getLevel() == curLevel + 1) {
					return true;
				}
				return false;
			}
		});

		// 检查科技的升级条件
		List<String> limitionslist = tech.getLimitation();
		if (!agent.checkTechLimition(limitionslist)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_LIMITED, techId);
			return false;
		}
		List<String> costList = techUpgrade.getResearchCostList();
		// 计算研究时间
		float buffTime = 0;
		String pararm = getBuildParams(build);
		if (!StringUtils.isNull(pararm)) {
			buffTime = Float.parseFloat(pararm);
		}
		buffTime += agent.getCityAttr().getImpResSpeed();
		long researchTimeOld = (long) (techUpgrade.getResearchTime() * (1.0f - buffTime) + 0.5f);
		float powerRaito = agent.geteAgent().searchPoweRatio(buildId, this.getBuildComponentType().getKey());
		long researchTime = (long) (researchTimeOld*1.0/powerRaito);
		GameLog.info("[upgradeTech]uid="+role.getJoy_id()+"|techId="+techId+"|buff1="+buffTime+"|buildBuf2f="+agent.getCityAttr().getImpResSpeed()+"|researchTimeOld="+researchTimeOld+"|powerRaito="+powerRaito+"|researchTime="+researchTime);
		// 计算金币
		int costMoney = 0;
		if (money > 0) {
			if (money == 2) {
				costMoney = agent.getCostMoney(role, costList, null,0, (byte) 0);
			} else {
				costMoney = agent.getCostMoney(role, costList,null, researchTime, (byte) 0);
			}
			if (costMoney > role.getMoney()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return false;
			}
		} else if (!agent.checkResConditions(role, costList)) {
			return false;
		}
		// 扣除资源
		List<Object> resLst = agent.redCostResource(costList, costMoney,EventName.upgradeTech.getName());
		sTechId = techId;
		// 下发数据
		RespModuleSet rms = new RespModuleSet();
		if (money == 1) {
			techAgent.techLevelup(role,techId);
			role.redRoleMoney(costMoney);
			role.getRoleStatisticInfo().updataRoleTechFight(role);
			LogManager.goldConsumeLog(role, costMoney, EventName.upgradeTech.getName());
			// 更新技能树
			techAgent.sendToClient(rms);
			role.sendRoleToClient(rms);
			role.sendResourceToClient(rms, cityId, resLst.toArray());
			// 任务事件
			int level = techAgent.getTechLevel(sTechId);
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_RESEARCH, sTechId,
					level, cityId, isMaxPoint(level));
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD,
					TimerLastType.TIME_RESEARCH);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.RESEARCH_SCIENCE,sTechId);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ACCELERATE,researchTime);
		} else {
			if (money == 2) {
				role.redRoleMoney(costMoney);
				role.sendRoleToClient(rms);
			}
			// 添加时间队列
			state = 1;
			TimerLast timer = build.addBuildTimer((long) researchTime, TimerLastType.TIME_RESEARCH);
			timer.registTimeOver(this);
			build.sendToClient(rms);
			role.sendResourceToClient(rms, cityId, resLst.toArray());
		}
		try {
			if(money==1){
				NewLogManager.buildLog(role, "study_tech",techId,true,costMoney);
			}else if(money ==0||money==2){
				StringBuffer sb = new StringBuffer();
				if(money==2){
					sb.append(true);
					sb.append(GameLog.SPLIT_CHAR);
					sb.append(costMoney);
					sb.append(GameLog.SPLIT_CHAR);
				}
				for(int i=0;i<costList.size();i++){
					String resource = costList.get(i);
					String[] params = resource.split(":");
					for (int j = 0 ; j < params.length ; j++){
						Object obj = params[j];
						sb.append(obj.toString());
						sb.append(GameLog.SPLIT_CHAR);
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				if(money==2){
					NewLogManager.buildLog(role,techId, "study_tech",newStr);
				}else{
					NewLogManager.buildLog(role, techId,"study_tech",false,0,newStr);
				}
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		
		return true;
	}
	
	public boolean cancelUpgradeTech(Role role, int cityId, long id){
		RoleCityAgent agent = role.getCity(cityId);
		RoleTechAgent techAgent = agent.getTechAgent();
		if (techAgent == null) {
			GameLog.error("get tech agent error where uid=" + role.getId());
			return false;
		}
		// 倒计时检查，一个建筑同时只能有一个倒计时
		RoleBuild build = agent.searchBuildById(buildId);
		if (build.getTimerSize() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		boolean bCancel = build.modifyTimer(0, TimerLastType.TIME_RESEARCH, true);
		if(!bCancel){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		final int curLevel = techAgent.getTechLevel(sTechId);
		Techupgrade techUpgrade = dataManager.serach(Techupgrade.class, new SearchFilter<Techupgrade>(){
			@Override
			public boolean filter(Techupgrade data){
				if(data.getTechID().equals(sTechId) && data.getLevel() == curLevel+1){
					return true;
				}
				return false;
			}
		});
		List<String> costList = techUpgrade.getResearchCostList();

		ResourceTypeConst[] types = { ResourceTypeConst.RESOURCE_TYPE_FOOD, ResourceTypeConst.RESOURCE_TYPE_METAL,
				ResourceTypeConst.RESOURCE_TYPE_OIL, ResourceTypeConst.RESOURCE_TYPE_ALLOY };
		// 返还消耗的资源
		List<Object> costs = new ArrayList<Object>();
		for (int i = 0; i < costList.size(); i++) {
			String[] cs = costList.get(i).split(":");
			types[i] = ResourceTypeConst.search(cs[0]);
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			long need = (long)(Long.parseLong(cs[1]) * Const.RES_CANCEL_RETURN_RATE);
			if (type != null && need > 0){
				costs.add(type);
				costs.add(need);
				LogManager.itemOutputLog(role,need,EventName.cancelUpgradeTech.getName(), cs[0]);
			}
		}
		state = 0;
		//下发数据
		RespModuleSet rms = new RespModuleSet();
		build.sendToClient(rms);
		role.addResourcesToCity(rms,cityId,costs.toArray());
		try {
			NewLogManager.buildLog(role, "cancel_study", sTechId);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		sTechId = map.get("sTechId");
		if (StringUtils.isNull(sTechId)){
			sTechId = "";
		}
		TimerLast timer = build.searchTimer(TimerLastType.TIME_RESEARCH);
		if (timer != null) {
			timer.registTimeOver(this);
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		map.put("sTechId",StringUtils.isNull(sTechId) ? "null" : sTechId);
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey()); // string
		params.put(state); //byte
		params.put(sTechId); //string
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	@Override
	public void finish() {
		state = 0;
		Role role = world.getRole(roleId);
		if(role == null){
			return;
		}
		RoleTechAgent techAgent = role.getCity(cityId).getTechAgent();
		if (techAgent != null) {
			techAgent.techLevelup(role,sTechId);
			//更新技能树
			RespModuleSet rms = new RespModuleSet();
			techAgent.sendToClient(rms);
			MessageSendUtil.sendModule(rms, world.getRole(roleId).getUserInfo());
		}else{
			GameLog.error("BuildComponentResearch research finsh, cannot find RoleTechAgent uid:"+roleId);
		}
		//任务事件
		int level = techAgent.getTechLevel(sTechId);
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_RESEARCH, sTechId, level, cityId, isMaxPoint(level));
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.RESEARCH_SCIENCE,sTechId);
		// 重置该建筑的联盟被帮助的次数为0
		role.clearBuildHelpers(0, buildId);
	}

	@Override
	public void tick(Role role,RoleBuild build,long now) {

	}
	
	public Technology getResearchingTechnology(RoleCityAgent agent){
//		Role role = world.getOnlineRole(id)
		if(agent == null){
			return null;
		}
		return agent.getTechAgent().getTechnology(sTechId);
	}

	private boolean isMaxPoint(int level){
		Tech tech = dataManager.serach(Tech.class, sTechId);
		if(tech == null){
			return false;
		}
		if(tech.getMaxPoints() == level){
			return true;
		}
		return false;
	}

	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		if (build.getTimerSize() > 0) {
			return true;
		}
		return false;
	}
}
