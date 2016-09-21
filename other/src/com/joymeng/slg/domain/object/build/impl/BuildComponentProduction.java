package com.joymeng.slg.domain.object.build.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.data.RoleBuildState;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 资源生成组件
 * 
 * @author tanyong
 *
 */
public class BuildComponentProduction implements BuildComponent, Instances {
	private BuildComponentType buildComType;
	// 0-不能收取，1-提示收取，2-数量满,3-允许收取,4-暂停生产
	byte state = 0;
	// 资源类型
	ResourceTypeConst resType;
	// 生产时间起点
	long timer = 0;
	// buff改变之前的未收获数量
	long resNum = 0;
	// 当前最大存储上限
	long resMaxNum = 0;
	// 基础最大储存上限
	long resBaseMaxNum = 0;
	// 当前产出
//	long resOutput = 0;
	// 基础产出
	long resBaseOutput = 0;
	// 道具指定建筑加成
	TimerLast specialTimer = null;
	boolean isRate = true;
	String itemId;
	boolean isSpeedup = false;
	//
	// float itemBuff = 0f;
	// float techBuff = 0f;
	// float skillBuff = 0f;
	// float vipBuff = 0f;
	// float equipBuff = 0f;
	//
	// long itemNumBuff = 0;
	// long techNumBuff = 0;
	// long skillNumBuff = 0;
	// long vipNumBuff = 0;
	// long equipNumBuff = 0;

	long uid;
	int cityId;
	long buildId;

	public BuildComponentProduction() {
		buildComType = BuildComponentType.BUILD_COMPONENT_PRODUCTION;
	}

	public TimerLast getSpecialTimer() {
		return specialTimer;
	}

	public void setSpecialTimer(TimerLast specialTimer) {
		this.specialTimer = specialTimer;
	}

	@Override
	public void init(long uid, int cityId, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityId;
		this.buildId = buildId;

		BuildName name = BuildName.search(buildID);
		if (name == null) {
			return;
		}
		switch (name) {
		case REFINERY:
			resType = ResourceTypeConst.RESOURCE_TYPE_OIL;
			break;
		case SMELTER:
			resType = ResourceTypeConst.RESOURCE_TYPE_METAL;
			break;
		case FOOD_FACT:
			resType = ResourceTypeConst.RESOURCE_TYPE_FOOD;
			break;
		case TITANIUM_PLANT:
			resType = ResourceTypeConst.RESOURCE_TYPE_ALLOY;
			break;
		default:
			break;
		}
	}

	/**
	 * 计算当前建筑的产量和最大储存量
	 * 
	 * @param uid
	 * @param cityId
	 * @param buildId
	 */
	@Override
	public void setBuildParams(RoleBuild build) {
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
		if (buildLevel == null) {
			GameLog.error(" set BUILD_COMPONENT_PRODUCTION params fail.");
			return;
		}
		List<String> paramLst = buildLevel.getParamList();
		if (paramLst.size() < 2) {
			return;
		}
		String strParam = paramLst.get(0);
		resBaseOutput = Integer.parseInt(strParam);
		strParam = paramLst.get(1);
		resBaseMaxNum = Integer.parseInt(strParam);
		state = 0;
		timer = TimeUtils.nowLong() / 1000;// 起始时间点
		// 重新计算当前产量
		updateResOutput();
		resMaxNum = resBaseMaxNum;

		IsCanStateChange();
	}

	/**
	 * buff 计算和更新
	 * 
	 * @return
	 */
	// public float getAllRateBuff() {
	// return techBuff + itemBuff + skillBuff + vipBuff + equipBuff;
	// }
	//
	// public long getAllNumBuff() {
	// return techNumBuff + itemNumBuff + skillNumBuff + vipNumBuff +
	// equipNumBuff;
	// }

	private List<Effect> getEffectByBuildId() {
		List<Effect> effects = new ArrayList<Effect>();
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			RoleBuild build = null;
			RoleCityAgent cityAgent = role.getCity(cityId);
			if (cityAgent != null)
				build = cityAgent.searchBuildById(buildId);
			if (build != null && build.getState() != RoleBuildState.COND_DELETED.getKey()) {
				if (build.getBuildId().equals(BuildName.FOOD_FACT.getKey())) {
					effects = role.getEffectAgent().searchBuffByTargetType(TargetType.T_B_IMP_FP,
							TargetType.B_ADD_FOOD_PROD);
				} else if (build.getBuildId().equals(BuildName.SMELTER.getKey())) {
					effects = role.getEffectAgent().searchBuffByTargetType(TargetType.T_B_IMP_MP,
							TargetType.B_ADD_METAL_PROD);
				} else if (build.getBuildId().equals(BuildName.REFINERY.getKey())) {
					effects = role.getEffectAgent().searchBuffByTargetType(TargetType.T_B_IMP_OP,
							TargetType.B_ADD_OIL_PROD);
				} else if (build.getBuildId().equals(BuildName.TITANIUM_PLANT.getKey())) {
					effects = role.getEffectAgent().searchBuffByTargetType(TargetType.T_B_IMP_AP,
							TargetType.B_ADD_ALLOY_PROD);
				} else {
					// 核弹减产
					effects = role.getEffectAgent().searchBuffByTargetType(TargetType.C_A_RED_RES);
					for (Effect eff : effects) {
						if (eff.isPercent()) {
							eff.setRate(eff.getRate() * (-1));
						} else {
							eff.setNum(eff.getNum() * (-1));
						}
					}
				}
			}
		}
		return effects;
	}

	public float[] getAllRateBuff() {
		float[] value = new float[] { 0, 0, 0, 0, 0 ,0};
		List<Effect> effects = getEffectByBuildId();
		for (Effect eff : effects) {
			switch (eff.getsType()) {
			case EFF_TECH:
				value[0] += eff.getRate();
				break;
			case EFF_EQUIP:
				value[1] += eff.getRate();
				break;
			case EFF_ITEM:
				value[2] += eff.getRate();
				break;
			case EFF_SKILL:
				value[3] += eff.getRate();
				break;
			case EFF_VIP:
				value[4] += eff.getRate();
				break;
			case EFF_UCITY:
				value[5] += eff.getRate();
				break;
			default:
				break;
			}
		}
		if(specialTimer != null && isRate)
			value[2] += (float) specialTimer.getParam();
//		GameLog.info(
//				"[getAllRateBuff]uid=" + this.uid + "|cityId=" + cityId + "|buildid=" + buildId + "|rate=" + (value[0]+":"+value[1]+":"+value[2]+":"+value[3]+":"+value[4])+
//				"|specialTimer="+((specialTimer != null && isRate) ? specialTimer.getParam() : 0 ));
		return value;
	}

	public long[] getAllNumBuff() {
		long[] value = new long[] { 0, 0, 0, 0, 0, 0 };
		List<Effect> effects = getEffectByBuildId();
		for (Effect eff : effects) {
			switch (eff.getsType()) {
			case EFF_TECH:
				value[0] += eff.getNum();
				break;
			case EFF_EQUIP:
				value[1] += eff.getNum();
				break;
			case EFF_ITEM:
				value[2] += eff.getNum();
				break;
			case EFF_SKILL:
				value[3] += eff.getNum();
				break;
			case EFF_VIP:
				value[4] += eff.getNum();
			case EFF_UCITY:
				value[5] += eff.getNum();
				break;
			default:
				break;
			}
		}
		if(specialTimer != null && !isRate)
			value[2] += (long) specialTimer.getParam();
//		GameLog.info(
//				"[getAllRateBuff]uid=" + this.uid + "|cityId=" + cityId + "|buildid=" + buildId + "|num=" + (value[0]+":"+value[1]+":"+value[2]+":"+value[3]+":"+value[4])+"|specialTimer="+((specialTimer != null && !isRate) ? specialTimer.getParam() : 0 ));
		return value;
	}

	public void updateResBuffRate() {
		// long now = TimeUtils.nowLong() / 1000;
		// resNum += calcResourceNum();
		// timer = now;
//		switch (e.getsType()) {
//		case EFF_TECH: {
//			if (e.isPercent()) {
//				if (isRemove) {
//					techBuff -= e.getRate();
//				} else {
//					techBuff += e.getRate();
//				}
//			} else {
//				if (isRemove) {
//					techNumBuff -= e.getNum();
//				} else {
//					techNumBuff += e.getNum();
//				}
//			}
//		}
//			break;
//		case EFF_EQUIP: {
//			if (e.isPercent()) {
//				if (isRemove) {
//					equipBuff -= e.getRate();
//				} else {
//					equipBuff += e.getRate();
//				}
//			} else {
//				if (isRemove) {
//					equipNumBuff -= e.getNum();
//				} else {
//					equipNumBuff += e.getNum();
//				}
//			}
//		}
//			break;
//		case EFF_ITEM: {
//			if (e.isPercent()) {
//				if (isRemove) {
//					itemBuff -= e.getRate();
//				} else {
//					itemBuff -= e.getRate();
//				}
//			} else {
//				if (isRemove) {
//					itemNumBuff -= e.getNum();
//				} else {
//					itemNumBuff -= e.getNum();
//				}
//			}
//		}
//			break;
//		case EFF_SKILL: {
//			if (e.isPercent()) {
//				if (isRemove) {
//					skillBuff -= e.getRate();
//				} else {
//					skillBuff += e.getRate();
//				}
//			} else {
//				if (isRemove) {
//					skillNumBuff -= e.getNum();
//				} else {
//					skillNumBuff += e.getNum();
//				}
//			}
//		}
//			break;
//		case EFF_VIP: {
//			if (e.isPercent()) {
//				if (isRemove) {
//					vipBuff -= e.getRate();
//				} else {
//					vipBuff += e.getRate();
//				}
//			} else {
//				if (isRemove) {
//					vipNumBuff -= e.getNum();
//				} else {
//					vipNumBuff += e.getNum();
//				}
//			}
//		}
//			break;
//		default:
//			break;
//		}
		updateResOutput();
		Role role = world.getOnlineRole(uid);
		if (role == null) {
			return;
		}
		RoleCityAgent cityAgent = role.getCity(cityId);
		RespModuleSet rms = new RespModuleSet();
		RoleBuild build = cityAgent.searchBuildById(buildId);
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
	
	public float getPowerRatio(){
		float powerRatio = Const.DEFAULT_CONSUMPTION;
		Role role = world.getOnlineRole(uid);
		if (role != null) {
			RoleCityAgent agent = role.getCity(cityId);
			if(agent != null)
				powerRatio = agent.geteAgent().searchPoweRatio(buildId, this.getBuildComponentType().getKey());
		}
		return powerRatio;
	}
	
	/**
	 * 
	* @Title: getresOutPutFinal 
	* @Description: 得到最终产出
	* 
	* @return long
	* @return
	 */
	public long getOutput(){
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		long finals =   (long) (resBaseOutput + resBaseOutput * (rate[0]+rate[1]+rate[2]+rate[3]+rate[4]+rate[5]) + (num[0]+num[1]+num[2]+num[3]+num[4]+num[5]));
		finals = (long) (finals * getPowerRatio());
		return finals;
	}
	

	private void updateResOutput() {
		long resOutput = getOutput();
		// 任务事件
		Role role = world.getObject(Role.class, uid);
		if (role != null) {
			role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_RESOURCE, cityId,
					resType.getKey(), resOutput);
		}
		GameLog.info("[updateResOutput]uid="+uid+"|resOutput="+resOutput);
	}

	public long getBaseOutput() {
		return resBaseOutput;
	}

	public long getTechBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[0] * resBaseOutput + num[0]);
	}

	public long getItemBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[2] * resBaseOutput + num[2]);
	}

	public long getSkillBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[3] * resBaseOutput + num[3]);
	}

	public long getVipBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[4] * resBaseOutput + num[4]);
	}

	public long getEquipBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[1] * resBaseOutput + num[1]);
	}
	
	public long getUnionCitysBuffOutput() {
		float[] rate = getAllRateBuff();
		long[] num = getAllNumBuff();
		return (long) (rate[5] * resBaseOutput + num[5]);
	}

	/**
	 * 停止生产
	 * 
	 * @return
	 */
	public synchronized void setStopProduction(byte buildState) {
		if (buildState == RoleBuildState.COND_NORMAL.getKey()) {
			state = 0;
			IsCanStateChange();
		} else if (buildState == RoleBuildState.COND_UPGRADE.getKey()) {
			state = 4;
			resNum = calcResourceNum();
		}
		timer = TimeUtils.nowLong() / 1000;
	}

	/**
	 * 计算当前可收获资源数量
	 * 
	 * @return
	 */
	public long calcResourceNum() {
		double result = 0.0D;
		if (state == 4) {
			if (resNum < 1) {
				return (long) result;
			}
		} else {
			long newTimer = TimeUtils.nowLong() / 1000;
			long lastTime = newTimer - timer;
			if (lastTime > 0) {
				// 计算产量
				result = (double) getOutput() * lastTime / Const.ONE_HOUR_TIME;
			}
		}
		result += resNum;
		result = (result > resMaxNum) ? resMaxNum : result;
		return (long) result;
	}
	
	public long calcResourceNumTimes(int hour) {
		double result = 0.0D;
		if (hour > 0) {
			// 计算产量
			result = (double) getOutput() * hour;
		}
//		result += resNum;
//		result = (result > resMaxNum) ? resMaxNum : result;
		return (long) result;
	}

	/**
	 * 是否可收获，客户端收获提示按钮
	 * 
	 * @return
	 */
	private synchronized boolean IsCanStateChange() {
		if (state == 4) {
			return false;
		}
		//GameLog.info("[IsCanStateChange]uid="+uid+"|build="+this.buildId+"|state="+state);
		long num = calcResourceNum();
		if (num + resNum >= resMaxNum) {// 停止增长
			if (state == 2) {
				return false;
			} else {
				state = 2;
				return true;
			}
		} else if (num + resNum >= resBaseOutput) {// 提示收取
			if (state == 1) {
				return false;
			} else {
				state = 1;
				return true;
			}
		} else if (num + resNum >= 1) {// 允许收取
			if (state == 3) {
				return false;
			} else {
				state = 3;
				return true;
			}
		}
		return false;
	}

	/***
	 * 收集
	 * 
	 * @param role
	 * @param city
	 * @param build
	 * @return
	 */
	public synchronized long collectResource(Role role, RoleBuild build) {
		long collectNum = 0;
		if (state == 4) {
			if (resNum < 1) {
				return collectNum;
			}
		} else {
			long newTimer = TimeUtils.nowLong() / 1000;
			long lastTime = newTimer - timer;
			if (lastTime > 0) {
				// 计算产量
				double result = calcResourceNum();
				resNum += (long) result;
			}
			if (resNum < 1) {
				return collectNum;
			}
			state = 0;
			timer = newTimer;
		}
		resNum = (resNum > resMaxNum) ? resMaxNum : resNum;
		collectNum = resNum;
		resNum = 0;
		// 下发
		RespModuleSet rms = new RespModuleSet();
		build.sendToClient(rms);
		role.addResourcesToCity(rms, cityId, resType, collectNum);
		String item = resType.getKey();
		LogManager.itemOutputLog(role, collectNum, EventName.collectResource.getName(), item);
		try {
			NewLogManager.buildLog(role, "collect_resources", item, collectNum);
			NewLogManager.buildLog(role, "grain_resource", item, collectNum);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		// 任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_RES_HARVEST,
				resType.getKey(), collectNum);
		return collectNum;
	}

	public static long TIME_NOW = 0l;
	@Override
	public void tick(Role role, RoleBuild build, long now) {
		if (specialTimer != null && specialTimer.over(now)) {
			specialTimer.die();
			build.sendToClient();
		}
		if (IsCanStateChange()) {
			build.sendToClient();
		}
		
	}

	private void updateParams(Role role, RoleBuild build) {
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
		if (buildLevel == null) {
			GameLog.error(" set BUILD_COMPONENT_PRODUCTION params fail.");
			return;
		}
		List<String> paramLst = buildLevel.getParamList();
		if (paramLst.size() < 2) {
			return;
		}
		String strParam = paramLst.get(0);
		resBaseOutput = Integer.parseInt(strParam);
		strParam = paramLst.get(1);
		resBaseMaxNum = Integer.parseInt(strParam);
		state = 0;
		IsCanStateChange();
		calcOffLineRes(role, build);
	}

	/**
	 * 计算离线产量
	 * 
	 * @param role
	 */
	public void calcOffLineRes(Role role, RoleBuild build) {
		long now = TimeUtils.nowLong() / 1000;
		if (state == 4) {// 从建筑升级结束开始计算
			TimerLast tmpTimer = build.getBuildTimer();
			if (tmpTimer != null) {
				if (tmpTimer.getStart() + tmpTimer.getLast() < now) {
					state = 0;
					timer = tmpTimer.getStart() + tmpTimer.getLast();
					updateParams(role, build);
				}
			}
		} else if (state != 2) { // 数量未满,计算离线产量
			List<Effect> prodBuffLst = role.getEffectAgent().getProductionTimerBuff(resType);
			if (prodBuffLst.size() == 0) {
				resNum = calcResourceNum();
			} else {
				resNum = resBaseOutput * (now - timer) / Const.ONE_HOUR_TIME;
				for (Effect e : prodBuffLst) {
					if (e.getTimer() == null) {
						continue;
					}
					long lastTime = e.getTimer().getStart() + e.getTimer().getLast();
					if (lastTime <= timer) {
						continue;
					}
					if (e.isPercent()) {
						resNum += resBaseOutput * (lastTime - timer) * e.getRate() / Const.ONE_HOUR_TIME;
					} else {
						resNum += e.getNum() * (lastTime - timer) / Const.ONE_HOUR_TIME;
					}
				}
			}
			timer = now;
		}
	}

	/**
	 * 保存数据
	 */
	@Override
	public String serialize(RoleBuild build) {
		if (resType == null) {
			return "";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("resType", resType.getKey());
		map.put("state", String.valueOf(state));
		map.put("timer", String.valueOf(timer));
		map.put("resNum", String.valueOf(resNum));
		map.put("resBaseOutput", String.valueOf(resBaseOutput));
		map.put("resOutput", String.valueOf(getOutput()));
		map.put("resBaseMaxNum", String.valueOf(resBaseMaxNum));
		map.put("resMaxNum", String.valueOf(resMaxNum));
		map.put("isRate", isRate ? "true" : "false");
		map.put("itemId", StringUtils.isNull(itemId) ? "null" : itemId);
		if (specialTimer == null) {
			map.put("specialTimer", "null");
		} else {
			map.put("specialTimer",
					specialTimer.getStart() + ":" + specialTimer.getLast() + ":" + specialTimer.getParam());
		}
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	/***
	 * 加载数据库中的数据
	 */
	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String, String> map = JsonUtil.JsonToObjectMap(str, String.class, String.class);
		resType = ResourceTypeConst.search(map.get("resType"));
		state = Byte.parseByte(map.get("state"));
		timer = Long.parseLong(map.get("timer"));
		resNum = Long.parseLong(map.get("resNum"));
		resBaseOutput = Long.parseLong(map.get("resBaseOutput"));
		 Long.parseLong(map.get("resOutput"));
		resBaseMaxNum = Long.parseLong(map.get("resBaseMaxNum"));
		resMaxNum = Long.parseLong(map.get("resMaxNum"));
		itemId = map.get("itemId");
		if (StringUtils.isNull(itemId)) {
			itemId = "";
		}
		String temp = map.get("isRate");
		isRate = temp.equals("true");
		temp = map.get("specialTimer");
		if (!StringUtils.isNull(temp)) {
			String[] strText = temp.split(":");
			long start = Long.parseLong(strText[0]);
			long last = Long.parseLong(strText[1]);
			String param = strText[2];
			specialTimer = new TimerLast(start, last, TimerLastType.TIME_ITEM_IMP_RES);
			if (isRate) {
				float rate = Float.parseFloat(param);
				//itemBuff += rate;
				specialTimer.setParam(rate);
			} else {
				long rate = Long.parseLong(param);
				//itemNumBuff += rate;
				specialTimer.setParam(rate);
			}
			specialTimer.registTimeOver(this);
			isSpeedup = true;
		}
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey());// String, 功能组件名称
		IsCanStateChange();
		params.put(state); // byte 状态
		params.put(getOutput());// long 当前产量
		params.put(resMaxNum);// long 当前容量
		params.put(isSpeedup ? (byte) 1 : (byte) 0);// 是否道具加速
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	@Override
	public void finish() {
//		if (isRate) {
//			itemBuff -= (float) specialTimer.getParam();
//		} else {
//			itemNumBuff -= (long) specialTimer.getParam();
//		}
		specialTimer = null;
		isSpeedup = false;
		updateResOutput();
	}

	/**
	 *  特殊道具加成
	* @Title: addSpecialItemBuff 
	* 
	* @return boolean
	* @param itemId
	* @param start
	* @param last
	* @param value
	* @param isRate
	* @return
	 */
	public boolean addSpecialItemBuff(String itemId, long start, long last, String value, boolean isRate) {
		if (specialTimer == null) {
			this.isRate = isRate;
			this.itemId = itemId;
			this.specialTimer = new TimerLast(TimeUtils.nowLong() / 1000, last, TimerLastType.TIME_ITEM_IMP_RES);
			specialTimer.registTimeOver(this);
			if (isRate) {
//				itemBuff += Float.parseFloat(value);
				specialTimer.setParam(Float.parseFloat(value));
			} else {
//				itemNumBuff += Long.parseLong(value);
				specialTimer.setParam(Long.parseLong(value));
			}
			isSpeedup = true;
			updateResOutput();
			return true;
		}
		return false;
	}

	// 创建时同步当前buff
	public void initProductEffect(Role role) {
		if (role != null) {
			updateResOutput();
//			List<Effect> effects = role.getEffectAgent().searchProductEffs(resType);
//			if (effects.size() > 0) {
//				for (int i = 0; i < effects.size(); i++) {
//					Effect e = effects.get(i);
//					switch (e.getsType()) {
//					case EFF_TECH: {
//						if (e.isPercent()) {
//							techBuff += e.getRate();
//						} else {
//							techNumBuff += e.getNum();
//						}
//						break;
//					}
//					case EFF_EQUIP: {
//						if (e.isPercent()) {
//							equipBuff += e.getRate();
//						} else {
//							equipNumBuff += e.getNum();
//						}
//						break;
//					}
//					case EFF_ITEM: {
//						if (e.isPercent()) {
//							itemBuff -= e.getRate();
//						} else {
//							itemNumBuff -= e.getNum();
//						}
//						break;
//					}
//					case EFF_SKILL: {
//						if (e.isPercent()) {
//							skillBuff += e.getRate();
//						} else {
//							skillNumBuff += e.getNum();
//						}
//						break;
//					}
//					case EFF_VIP: {
//						if (e.isPercent()) {
//							vipBuff += e.getRate();
//						} else {
//							vipNumBuff += e.getNum();
//						}
//						break;
//					}
//					default:
//						break;
//					}
//					updateResOutput();
//				}
//			}
		}
	}

	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		// TODO Auto-generated method stub
		return false;
	}
}
