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
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 军队训练组件
 * 
 * @author tanyong
 *
 */
public class BuildComponentArmyTrain implements BuildComponent, Instances {
	// 组件类型
	private BuildComponentType buildComType;
	// 0空闲，1标示训练中，2训练结束
	byte state;
	// 兵种id
	String armyId = "";
	// 训练数量
	int trainNum;
	long uid;
	int cityId;
	long buildId;

	public BuildComponentArmyTrain() {
		state = (byte) 0;
		buildComType = BuildComponentType.BUILD_COMPONENT_ARMYTRAIN;
	}

	@Override
	public void init(long uid, int cityId, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityId;
		this.buildId = buildId;
	}

	@Override
	public void setBuildParams(RoleBuild build) {

	}

	private String getBuildParams(RoleBuild build) {
		if (build != null) {
			Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
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
	 * 训练士兵
	 * 
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param armyId
	 * @param num
	 * @param money 0 -资源 1-金币2-资源不足，金币补充
	 * @return
	 */
	public boolean trainArmy(Role role, int cityId, long buildId, String armyId, int num, int money) {
		RoleCityAgent agent = role.getCity(cityId);
		// 倒计时检查，一个建筑同时只能有一个倒计时
		RoleBuild build = agent.searchBuildById(buildId);
		if (build.getTimerSize() > 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		// 检查训练数量
		BuildName name = BuildName.search(build.getBuildId());
		if(name == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		if (agent.getMaxTrainNum(name) < num || num == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		// 检查训练条件
		Army armyBase = dataManager.serach(Army.class, armyId);
		if (armyBase == null) {
			GameLog.error("cannot find this army base information where armyId=" + armyId);
			return false;
		}
		// 检查当前兵种的解锁条件
		List<String> condList = armyBase.getUnlockLimitation();
		if (!agent.checkTechLimition(condList)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TECH_LIMITED, armyId);
			return false;
		}
		// 检查资源或金币
		int costMoney = 0;
		List<String> resCostList = new ArrayList<String>();
		List<String> resCostListTemp = armyBase.getTrainCostList();
		for (int i = 0 ; i < resCostListTemp.size() ; i++){
			String strRes = resCostListTemp.get(i);
			String[] strArray = strRes.split(":");
			if (strArray.length < 2) {
				return false;
			}
			int resnum = Integer.parseInt(strArray[1]) * num;
			String newStr = strArray[0] + ":" + resnum;
			resCostList.add(newStr);
		}
		// 计算训练时间
		float buildBuff = 0;
		String param = getBuildParams(build);
		if (param != null) {
			buildBuff = Float.parseFloat(param);
		}
		float buff = role.getArmyAttr().getEffVal(TargetType.T_A_RED_SPT, armyId);
		double trainTime = armyBase.getTrainTime() * num * (1.0f - buff - buildBuff) + 0.5;// +0.5方便后面取整
		if (money > 0) {
			if (money == 2) {
				costMoney = agent.getCostMoney(role, resCostList,null,0, (byte) 0);
			} else {
				costMoney = agent.getCostMoney(role, resCostList,null, (int) trainTime, (byte) 0);
			}
			if (costMoney > role.getMoney()) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_ROLE_NO_MONEY, costMoney);
				return false;
			}
		} else if (!agent.checkResConditions(role, resCostList)) {
			return false;
		}
		// 扣除消耗
		List<Object> resLst = agent.redCostResource(resCostList,costMoney,"trainArmy");
		changeState(armyId, num);
		// 下发数据
		RespModuleSet rms = new RespModuleSet();
		if (money == 1) {
			state = 2;
			// 添加士兵训练完成事件
			role.redRoleMoney(costMoney);
			String event ="trainArmy";
			LogManager.goldConsumeLog(role, costMoney, event);
			role.sendRoleToClient(rms);
			//任务事件
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_ACC_BUILD, TimerLastType.TIME_TRAIN);
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ACCELERATE,(long)trainTime);
		} else {
			if (money == 2) {
				role.redRoleMoney(costMoney);
				String event ="trainArmy";
				LogManager.goldConsumeLog(role, costMoney, event);
				role.sendRoleToClient(rms);
			}
			// 添加时间队列
			TimerLast timer = build.addBuildTimer((long) trainTime, TimerLastType.TIME_TRAIN);
			timer.registTimeOver(this);
			state = 1;
		}
		try {
			if(money==1){
				NewLogManager.buildLog(role, "train_unit",armyId,true,costMoney);
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
					NewLogManager.buildLog(role, "train_unit",armyId,newStr);
				}else{
					NewLogManager.buildLog(role, "train_unit",armyId,false,0,newStr);
				}
			}
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		
		build.sendToClient(rms);
		role.sendResourceToClient(rms, cityId, resLst.toArray());
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.TRAIN_SOLDIER,armyId,num);
		NewLogManager.armyLog(role, "训练士兵", armyId, num);
		return true;
	}

	@Override
	public void finish() {
		state = 2;
	}

	public boolean cancelTrainArmy(Role role, int cityId,
			long buildId/* ,String armyId, int num */) {
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);
		if (build.getTimerSize() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		if (state != 1) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TIMER_TYPE_NOT_RIGHT);
			return false;
		}
		boolean bCancel = build.modifyTimer(0, TimerLastType.TIME_TRAIN, true);
		if (!bCancel) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		state = 0;
		Army armyBase = dataManager.serach(Army.class, armyId);
		if (armyBase == null) {
			GameLog.error("cannot find this army base information where armyId=" + armyId);
			return false;
		}
		List<String> resCostList = armyBase.getTrainCostList();
		ResourceTypeConst[] types = { ResourceTypeConst.RESOURCE_TYPE_FOOD, ResourceTypeConst.RESOURCE_TYPE_METAL,
				ResourceTypeConst.RESOURCE_TYPE_OIL, ResourceTypeConst.RESOURCE_TYPE_ALLOY };
		// 返还消耗的资源
		List<Object> costs = new ArrayList<Object>();
		for (int i = 0; i < resCostList.size(); i++) {
			String[] cs = resCostList.get(i).split(":");
			types[i] = ResourceTypeConst.search(cs[0]);
			ResourceTypeConst type = ResourceTypeConst.search(cs[0]);
			long need = (long)(Long.parseLong(cs[1])* trainNum * Const.RES_CANCEL_RETURN_RATE);
			if (type != null && need > 0){
				costs.add(type);
				costs.add(need);
				LogManager.itemOutputLog(role,need,"cancelTrainArmy", cs[0]);
			}
		}
		// 下发数据
		RespModuleSet rms = new RespModuleSet();
		build.sendToClient(rms);
		role.addResourcesToCity(rms,cityId,costs.toArray());
		try {
			NewLogManager.buildLog(role, "cancel_train",armyId);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * 获取士兵
	 * 
	 * @param role
	 * @param cityId
	 * @return
	 */
	public boolean getTrainArmy(Role role, RoleCityAgent cityAgent, RoleBuild build) {
		if (state != 2) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_TRAIN_NO_FINISH, build.getBuildId());
			return false;
		}
		// 下发部队信息
		RoleArmyAgent armyAgent = cityAgent.getCityArmys();
		if (armyId == null || trainNum == 0) {
			GameLog.error("getTrainArmy armyId=" + armyId + "trainNum=" + trainNum);
			return false;
		}
		// 恢复状态
		state = 0;
		List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
		ArmyInfo army = armyAgent.createArmy(armyId, trainNum, ArmyState.ARMY_IN_NORMAL.getValue());
		armys.add(army);
		armyAgent.addClassArmys(armys);

		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_TRAIN, armyId, trainNum);
		RespModuleSet rms = new RespModuleSet();
		armyAgent.sendToClient(rms,cityAgent);
		build.sendToClient(rms);
		// 下发
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		NewLogManager.armyLog(role, "收获士兵", armyId, trainNum);
		return true;
	}

	// 修改军队训练的状态
	private void changeState(String armyId, int num) {
		this.armyId = armyId;
		this.trainNum = num;
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
		armyId = map.get("armyId");
		if (StringUtils.isNull(armyId)){
			armyId = "";
		}
		trainNum = Integer.parseInt(map.get("trainNum"));
		TimerLast timer = build.searchTimer(TimerLastType.TIME_TRAIN);
		if (timer != null) {
			timer.registTimeOver(this);
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> temp = new HashMap<String,String>();
		temp.put("state",String.valueOf(state));
		temp.put("armyId",StringUtils.isNull(armyId) ? "null" : armyId);
		temp.put("trainNum",String.valueOf(trainNum));
		String result = JsonUtil.ObjectToJsonString(temp);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());// String, 功能组件名称
		params.put(state);// byte 训练营的状态 0空闲，1标示训练中，2训练结束
		params.put(armyId);// String
		params.put(trainNum);// int
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

}
