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
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

public class BuildComponentCure implements BuildComponent, Instances {
	private BuildComponentType buildComType;
	long uid;
	int cityId;
	long buildId;
	byte state;
	//治疗中的伤兵列表
	List<ArmyInfo> cureArmyInfo = new ArrayList<ArmyInfo>();

	public BuildComponentCure() {
		buildComType = BuildComponentType.BUILD_COMPONENT_CURE;
	}

	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityID;
		this.buildId = buildId;
		state = 0;
	}

	/**
	 * 治疗士兵
	 * 
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param cureArmys
	 * @param money
	 * @return
	 */
	public boolean cureArmys(Role role, int cityId, long buildId, List<String> cureArmys, int money) {
		// 将受伤的军队列表存放在cureArmyInfo中
		RoleCityAgent agent = role.getCity(cityId);
		RoleArmyAgent roleArmyAgent = agent.getCityArmys();
		cureArmyInfo.clear();
		if (cureArmys.size() < 1) {
			return false;
		}
		for (int i = 0; i < cureArmys.size(); i += 2) {
			ArmyInfo tempArmyInfo = roleArmyAgent.createArmy(cureArmys.get(i), Integer.parseInt(cureArmys.get(i + 1)),
					ArmyState.ARMY_IN_HOSPITAL.getValue());
			if (tempArmyInfo.getArmyNum() > 0) {
				cureArmyInfo.add(tempArmyInfo);
			}
		}
		// 检查伤兵的数量真实有效性
		for (int i = 0 ; i < cureArmyInfo.size() ; i++){
			ArmyInfo tempArmyInfo = cureArmyInfo.get(i);
			if (tempArmyInfo.getArmyNum() > roleArmyAgent.getCityArmysNum(ArmyState.ARMY_IN_HOSPITAL.getValue(),
					tempArmyInfo.getArmyId())) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_CUREARMY_DATA_INVALID);
				return false;
			}
		}
		RoleBuild build = agent.searchBuildById(buildId);
		List<RoleBuild> buildList = agent.searchBuildByBuildId(build.getBuildId());
		// 检查此医院的状态，0--空闲 1--升级 6--治疗使用
		if (build.getState() == 6) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_STATE_WRONG);
			return false;
		}
		// 倒计时检查，一个建筑同时只能有一个倒计时
		if (build.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		// 检查资源条件
		Map<String, Integer> resMap = new HashMap<String, Integer>();
		long cureTime = 0;
		// add buff
		float buffRes = Float.MIN_VALUE;
		float buffTime = Float.MIN_VALUE;
		if (build.getBuildId().equals(BuildName.HOSPITAL.getKey())) {
			buffRes = agent.getCityAttr().getReduRepaRes_1();
			buffTime = agent.getCityAttr().getReduHospTime();
		} else if (build.getBuildId().equals(BuildName.REPAIRER.getKey())) {
			buffRes = agent.getCityAttr().getReduRepaRes_2();
			buffTime = agent.getCityAttr().getReduRepaTime();
		}
		for (int i = 0; i < cureArmys.size(); i++) {
			String armyId = cureArmys.get(i);
			i++;
			int armyNum = Integer.parseInt(cureArmys.get(i));
			Army armyBase = dataManager.serach(Army.class, armyId);
			if (armyBase == null) {
				GameLog.error("cannot find this army base information where armyId=" + armyId);
				return false;
			}
			// 检查资源
			List<String> resCostListTemp = armyBase.getTrainCostList();
			for (int j = 0 ; j < resCostListTemp.size() ; j++){
				String strRes = resCostListTemp.get(j);
				String[] strArray = strRes.split(":");
				if (strArray.length < 2) {
					return false;
				}
				int num = (int) (Integer.parseInt(strArray[1]) * armyNum * Const.CURE_SOLDIER_COST_RATE);
				if (resMap.get(strArray[0]) != null) {
					num += resMap.get(strArray[0]);
				}
				resMap.put(strArray[0], num);
			}
//			if (buffRes > Float.MIN_VALUE) {
//				for (int num : resMap.values()) {
//					num = (int) (num * (1 - buffRes));
//				}
//			}
			long time = (long) (armyBase.getTrainTime() * armyNum * Const.CURE_SOLDIER_COST_RATE);
			if (buffTime > Float.MIN_VALUE) {
				time = (long) (time * (1 - buffTime));
			}
			cureTime += time;
		}
		// 计算治疗需要的金币数
		int costMoney = 0;
		List<String> resCostList = new ArrayList<String>();
		if (resMap.size() > 0) {
			for (Map.Entry<String, Integer> entry : resMap.entrySet()) {
				String str = entry.getKey() + ":"
						+ (buffRes == 0F ? entry.getValue() : (int) (entry.getValue() * (1.0f - buffRes)));
				resCostList.add(str);
			}
		}
		// 计算治疗时间
		if (money > 0) {
			if (money == 2) {
				costMoney = agent.getCostMoney(role, resCostList,null, 0, (byte) 1);
			} else {
				costMoney = agent.getCostMoney(role, resCostList,null, cureTime, (byte) 1);
			}
			if (costMoney > role.getMoney()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return false;
			}
		} else if (!agent.checkResConditions(role, resCostList)) {
			return false;
		}
		// 扣除消耗
		List<Object> resLst = agent.redCostResource(resCostList, costMoney,"cureArmys");
		// 下发数据
		RespModuleSet rms = new RespModuleSet();
		if (money > 0 && money != 2) {
			role.redRoleMoney(costMoney);
			String event = "cureArmys";
			LogManager.goldConsumeLog(role, costMoney, event);
			role.sendRoleToClient(rms);
			for (int i = 0 ; i < buildList.size() ; i++){
				RoleBuild tempBuild = buildList.get(i);
				tempBuild.sendToClient(rms);
			}
			finish();
			// 任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD,
					TimerLastType.TIME_CURE);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ACCELERATE,cureTime);
		} else {
			if (money == 2) {
				role.redRoleMoney(costMoney);
				String event = "cureArmys";
				LogManager.goldConsumeLog(role, costMoney, event);
				role.sendRoleToClient(rms);
			}
			state = 1;
//			// 添加时间队列
//			TimerLast timer = build.addBuildTimer(cureTime, TimerLastType.TIME_CURE);
//			timer.registTimeOver(this);
			
			// 设置所有的同类建筑状态
			for (int i = 0 ; i < buildList.size() ; i++){
				RoleBuild tempBuild = buildList.get(i);
				if (tempBuild != null && tempBuild.getState() != 1) {
					tempBuild.setState((byte) 6);
					TimerLast timer = tempBuild.addBuildTimer(cureTime, TimerLastType.TIME_CURE);
					timer.registTimeOver(this);
					tempBuild.sendToClient(rms);
				}
			}
		}
		StringBuffer cure = new StringBuffer();
		for(int k=0;k<cureArmys.size();k++){
			String army = cureArmys.get(k);
			String number = cureArmys.get(k+1);
			k++;
			cure.append(army);
			cure.append(GameLog.SPLIT_CHAR);
			cure.append(number);
			cure.append(GameLog.SPLIT_CHAR);
		}
		String str = cure.toString().substring(0, cure.toString().length() - 1);
		try {
			if(money==1){
				NewLogManager.buildLog(role, "cure_army",str,true,costMoney);
			}else if(money ==0||money==2){
				StringBuffer sb = new StringBuffer();
				if(money==2){
					sb.append(true);
					sb.append(GameLog.SPLIT_CHAR);
					sb.append(costMoney);
					sb.append(GameLog.SPLIT_CHAR);
				}
				for(int i=0;i<resCostList.size();i++){
					String resource = resCostList.get(i);
					String[] params = resource.split(":");
					for (int j = 0 ; j < params.length ; j++){
						Object obj = params[j];
						sb.append(obj.toString());
						sb.append(GameLog.SPLIT_CHAR);
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				if(money==2){
					NewLogManager.buildLog(role, "cure_army",str,newStr);
				}else{
					NewLogManager.buildLog(role, "cure_army",str,false,0,newStr);
				}
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		
		role.sendResourceToClient(rms,cityId,resLst.toArray());
		for (int i = 0; i < cureArmys.size(); i++) {
			String armyId = cureArmys.get(i);
			i++;
			int armyNum = Integer.parseInt(cureArmys.get(i));
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.TREAT_SOLDIER,armyId,armyNum);
		}
		return true;
	}

	@Override
	public void tick(Role role,RoleBuild build,long now) {

	}

	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		String temp = map.get("cureArmyInfo");
		if (!StringUtils.isNull(temp)){
			String[] strText = temp.split(":");
			for (int i = 0 ; i < strText.length ; i++){
				String armyId = strText[i++];
				int num = Integer.parseInt(strText[i]);
				ArmyInfo army = new ArmyInfo(armyId,num,ArmyState.ARMY_IN_HOSPITAL.getValue());
				cureArmyInfo.add(army);
			}
		}
		TimerLast timer = build.searchTimer(TimerLastType.TIME_CURE);
		if (timer != null) {
			timer.registTimeOver(this);
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		if (cureArmyInfo.size() == 0){
			map.put("cureArmyInfo","null");
		}else{
			StringBuffer sb = new StringBuffer();
			for (int i = 0 ; i < cureArmyInfo.size() ; i++){
				ArmyInfo temp = cureArmyInfo.get(i);
				sb.append(temp.getArmyId() + ":" + temp.getArmyNum());
				if (i < cureArmyInfo.size() -1){
					sb.append(":");
				}
			}
			map.put("cureArmyInfo",sb.toString());
		}
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());
		params.put(state);
		params.put(cureArmyInfo.size());//int 
		for (ArmyInfo armyInfo : cureArmyInfo) {
			params.put(armyInfo.getArmyId()); //String 
		}
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	@Override
	public void finish() {
		// 更新建筑的状态
		Role role = world.getRole(uid);
		RoleCityAgent cityAgent = role.getCity(cityId);
		RoleArmyAgent roleArmyAgent = cityAgent.getCityArmys();
		RoleBuild build = cityAgent.searchBuildById(buildId);
		List<RoleBuild> roleBuilds = cityAgent.searchBuildByBuildId(build.getBuildId());
		RespModuleSet rms = new RespModuleSet();
		for (int i = 0; i < roleBuilds.size(); i++) {
			RoleBuild tempBuild = roleBuilds.get(i);
			if (tempBuild.getId() == this.buildId) {
				tempBuild.setState((byte) 0);
				tempBuild.removeTimer(TimerLastType.TIME_CURE);
				tempBuild.sendToClient(rms);
				continue;
			}
			if (tempBuild != null && tempBuild.getState() == (byte) 6) {
				tempBuild.setState((byte) 0);
				tempBuild.removeTimer(TimerLastType.TIME_CURE);
				tempBuild.sendToClient(rms);
			}
		}
		// 更新的士兵的相关信息
		roleArmyAgent.updateArmysState(ArmyState.ARMY_IN_NORMAL.getValue(), cureArmyInfo);
		// 结束后下发军队数据给客户端
		roleArmyAgent.sendToClient(rms,cityAgent);
		MessageSendUtil.sendModule(rms, role);
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_HEAL, cityId,cureArmyInfo);
	}

	@Override
	public void setBuildParams(RoleBuild build) {

	}

}
