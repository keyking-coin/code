package com.joymeng.slg.domain.object.build.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.CityFireFinish;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

public class BuildComponentWall implements BuildComponent,Instances {
	private BuildComponentType buildComType;
	byte state;//城墙状态0-正常，1-修理中，2-破损状态
	int defenseValue;//当前城防值
	int defenseMaxValue;//最大值
	int wallHPBuff=0;//城墙生命上限buff
	TimerLast timer = null;//扣城防倒计时
	int redDefenceTimes = 0;
	long uid;
	int cityId;
	long buildId;
	
	public BuildComponentWall(){
		buildComType = BuildComponentType.BUILD_COMPONENT_WALL;
		state = 0;
	}
	
	@Override
	public void init(long uid, int cityID, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityID;
		this.buildId = buildId;
	}
	
	public int getFenseMaxValue(){
		return defenseMaxValue;
	}
	
	public int getDefenseValue() {
		return defenseValue;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	/**
	 * 城墙修理
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param money 用金币秒时间
	 * @return
	 */
	public synchronized boolean repairDefense(Role role, int cityId, long buildId, int money){
		RoleCityAgent cityAgent = role.getCity(cityId);
		RoleBuild build = cityAgent.searchBuildById(buildId);
		if (build == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_NOT_FIND, buildId);
			return false;
		}
		//计时器检查
		if (build.getTimerSize() > 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_BUILD_TIMER_UNUSED, build.getId());
			return false;
		}
		//添加计时器
		RespModuleSet rms = new RespModuleSet();
		defenseValue += Const.REPAIR_WALL_VALUE;
		if (defenseValue >= defenseMaxValue) {
			defenseValue = defenseMaxValue;
			state = 0;
		}
		TimerLast repairTimer = build.searchTimer(TimerLastType.TIME_REPAIR_DEFENSE);
		if (repairTimer != null){
			repairTimer.resetLastAt(TimeUtils.nowLong()/1000,Const.REPAIR_WALL_TIME);
		}else{
			build.addBuildTimer(Const.REPAIR_WALL_TIME,TimerLastType.TIME_REPAIR_DEFENSE);
		}
		//下发数据
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}
	
	/***
	 * 重置城墙状态
	 */
	public synchronized void reinforceWall(Role role,RoleBuild build){
		defenseValue = getFenseMaxValue();
		state = 0;
		//强制移除城墙修复的倒计时
		TimerLast repairTimer = build.searchTimer(TimerLastType.TIME_REPAIR_DEFENSE);
		if (repairTimer != null){
			repairTimer.setLast(0);
			build.runTimers(null,role,TimeUtils.nowLong());
		}
		if (role != null && role.isOnline()){
			RespModuleSet rms = new RespModuleSet();
			build.sendToClient(rms);
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
	}
	
	@Override
	public void tick(Role role,RoleBuild build,long now) {
		//失火状态下扣城防值
		if (timer != null) {
			long start = timer.getStart();
			if (timer.over(now)) {
				timer.die();
			}
			long time = now / 1000 - start;
			if (time > Const.CITY_FIRE_INTERVAL * (redDefenceTimes + 1)) {
				//每隔五分钟扣一次城防值
				updateRedDef();
				redDefenseValue(Const.CITY_FIRE_RED_DEFENCE,build,role);
			}
		}
	}
	
	public void initWallStatus(Role role, RoleBuild build){
		if(defenseMaxValue > 0){
			return;
		}
		Buildinglevel buildLevel = RoleBuild.getBuildinglevelByCondition(build.getBuildId(), build.getLevel());
		if(buildLevel == null){
			GameLog.error("cann't get build level buff where buildId="+buildId);
			return;
		}
		List<String> paramLst = buildLevel.getParamList();
		if(paramLst.size() == 0){
			GameLog.error("cann't get build level build buff where buildId="+buildId);
			return;
		}
		if(!build.getBuildId().equals(BuildName.FENCE.getKey())){
			GameLog.error("cann't find wall static data");
		}else{
			defenseMaxValue = Integer.parseInt(paramLst.get(1));
			defenseValue = defenseMaxValue;
		}
	}

	@Override
	public void deserialize(String str,RoleBuild build) {
		if (StringUtils.isNull(str)) {
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		defenseValue = Integer.parseInt(map.get("defenseValue"));
		defenseMaxValue = Integer.parseInt(map.get("defenseMaxValue"));
		redDefenceTimes = Integer.parseInt(map.get("redDefenceTimes"));
		String temp = map.get("timer");
		if (!StringUtils.isNull(temp)){
			String[] strText = temp.split(":");
			long start = Long.parseLong(strText[0]);
			long last = Long.parseLong(strText[1]);
			long now = TimeUtils.nowLong() / 1000;
			if(now >= start + last){
				int times = (int) (last / Const.CITY_FIRE_INTERVAL) - redDefenceTimes;
				if (times > 0){
					redDefenseValue(Const.CITY_FIRE_RED_DEFENCE * times,build,null);
				}
			}else{
				long time = (redDefenceTimes + 1) * Const.CITY_FIRE_INTERVAL;
				while (start + time < now) {
					redDefenseValue(Const.CITY_FIRE_RED_DEFENCE,build,null);
					redDefenceTimes ++;
					time = (redDefenceTimes + 1) * Const.CITY_FIRE_INTERVAL;
					if (defenseValue == 0){
						break;
					}
				}
				timer = new TimerLast(start, last, TimerLastType.TIME_CITY_FIRE);
				timer.registTimeOver(new CityFireFinish(this));
			}
		}
		if (defenseValue < defenseMaxValue){
			state = 2;
		}
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		map.put("defenseValue", String.valueOf(defenseValue-wallHPBuff));
		map.put("redDefenceTimes", String.valueOf(redDefenceTimes));
		map.put("defenseMaxValue", String.valueOf(defenseMaxValue-wallHPBuff));
		if (timer == null){
			map.put("timer","null");
		}else{
			map.put("timer",timer.getStart() + ":" + timer.getLast());
		}
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put(buildComType.getKey()); //String
		params.put(state); //byte 城墙状态
		params.put(defenseValue); //int 城防值
		params.put(defenseMaxValue);//int城防总值
	}

	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}
	
	@Override
	public void finish() {
		
	}
	
	private synchronized void redDefence(int value){
		defenseValue -= value;
	}

	/**
	 * 减城防
	 * @param value
	 */
	public void redDefenseValue(int value,RoleBuild build,Role role){
		redDefence(value);
		//城池状态更新
		if (state != 2){
			state = 2;//破损
		}
		//城池着火
		MapCity mapCity = mapWorld.searchMapCity(uid,cityId);
		if (defenseValue <= 0){
			defenseValue = 0;
		} 
		long last = Const.CITY_FIRE_TIME;
		if (mapCity.getCityState().isFire() && timer != null){
			timer.setLast(last);
		}else{
			mapCity.getCityState().setFire(true);
			//启动倒计时
			timer = new TimerLast(TimeUtils.nowLong()/1000,last,TimerLastType.TIME_CITY_FIRE);
			timer.registTimeOver(this);
		}
		//外城状态
		mapCity.getCityState().setFire(true);
		mapCity.getCityState().addTimer(last,TimerLastType.TIME_CITY_FIRE,0);
		//下发数据
		if (role != null && role.isOnline()){
			RespModuleSet rms = new RespModuleSet();
			build.sendToClient(rms);
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
	}

	public void cancelFireState(){
		redDefenceTimes = 0;
		timer = null;
	}
	
	public void updateWallHP(boolean isRemove, int param){
		if (!isRemove){
			wallHPBuff += param;
			defenseValue += param;
			defenseMaxValue += param;
		}else{
			wallHPBuff -= param;
			defenseValue -= param;
			defenseMaxValue -= param;
		}
		Role role = world.getOnlineRole(uid);
		if(role != null){
			RoleBuild build = role.getCity(cityId).searchBuildById(buildId);
			if(build != null){
				RespModuleSet rms = new RespModuleSet();
				build.sendToClient(rms);
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
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
		List<String> paramLst = buildLevel.getParamList();
		if(paramLst.size() == 0){
			GameLog.error("cann't get build level build buff where buildId="+buildId);
			return;
		}
		int newStaticDefense = 0;
		if(!build.getBuildId().equals(BuildName.FENCE.getKey())){
			GameLog.error("cann't find wall static data");
		}else{
			newStaticDefense = Integer.parseInt(paramLst.get(1));
		}
		if(state != 0){
			int breakValue =  defenseMaxValue - defenseValue;
			defenseMaxValue = newStaticDefense + wallHPBuff;
			defenseValue = defenseMaxValue - breakValue;
		}else{
			defenseMaxValue = newStaticDefense + wallHPBuff;
			defenseValue = defenseMaxValue;
		}
	}

	public int getRedDefenceTimes() {
		return redDefenceTimes;
	}

	public void setRedDefenceTimes(int redDefenceTimes) {
		this.redDefenceTimes = redDefenceTimes;
	}
	
	public void updateRedDef(){
		redDefenceTimes ++;
	}
}
