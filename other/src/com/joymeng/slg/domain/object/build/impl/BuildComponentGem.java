package com.joymeng.slg.domain.object.build.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.build.BuildComponent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.mod.RespModuleSet;

public class BuildComponentGem implements BuildComponent,Instances {
	//组件类型
	private BuildComponentType buildComType;
	private LinkedList<String> itemIdLst = new LinkedList<String>();//格子里等待生产的物品列表
	private List<String> itemIdFinishLst = new ArrayList<String>();//生产完成的物品列表
	private int itemSize;//当前可用格子大小
	private byte state;
	private String itemId = ""; //正在生产的物品
	//购买加速次数
	private int accelerateTimes = 0;
	//刷新时间
	private int expiration = 0;
	//
	long uid;
	int cityId;
	long buildId;
	
	public BuildComponentGem(){
		buildComType = BuildComponentType.BUILD_COMPONENT_GEM;
		state = 0;
		itemSize = Const.GEM_GRID_SIZE;
	}
	
	@Override
	public void init(long uid, int cityId, long buildId, String buildID) {
		this.uid = uid;
		this.cityId = cityId;
		this.buildId = buildId;
		accelerateTimes = 0;
		//明天0点
		expiration = (int)(TimeUtils.getToday24Time()/Const.SECOND) ;
	}
	
	/**
	 * 宝石生产
	 * @return
	 */
	public boolean addProductionGems(Role role, RoleBuild build, String itemId){
		RespModuleSet rms = new RespModuleSet();
		if(build.getTimerSize() == 0){
			this.itemId = itemId;
			long last = Const.GEM_PD_TIME;
			TimerLast timer = build.addBuildTimer(last, TimerLastType.TIME_PD_GEM);
			timer.registTimeOver(this);
			state = 1;
		}else{
			if(itemSize == 0){
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_GEM_NO_GRAD);
				return false;
			}
			if(itemSize < itemIdLst.size() + 1){
				itemIdLst.pop();
			}
			itemIdLst.add(itemId);
		}
		// 下发数据
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "product_materialnumber");
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}

	/**
	 * 移除等待队列中的宝石
	 * @param role
	 * @param cityId
	 * @param buildId
	 * @param itemId
	 * @return
	 */
	public boolean removeProductionGems(Role role, int cityId, long buildId, String itemId,int index){
		RoleCityAgent agent = role.getCity(cityId);
		RoleBuild build = agent.searchBuildById(buildId);

		if(itemIdLst.size() == 0 || (index > itemIdLst.size()-1 || index < 0)){
			return false;
		}
		itemIdLst.remove(index);
		RespModuleSet rms = new RespModuleSet();
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}
	
	/**
	 * 收获宝石
	 */
	public boolean getGems(Role role, RoleBuild build, String itemId){
		if(itemIdFinishLst.size() == 0){
			return false;
		}
		itemIdFinishLst.remove(itemId);
		RespModuleSet rms = new RespModuleSet();
		//添加物品到背包
		RoleBagAgent bagagent = role.getBagAgent();
		bagagent.addOther(itemId,1);
		String itemst =itemId;
		LogManager.itemOutputLog(role, 1, EventName.getGems.getName(), itemst);
		//下发
		ItemCell e = bagagent.getItemFromBag(itemId);
		bagagent.sendItemsToClient(rms, e);
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_MATERIAL_PROD);
		role.handleEvent(GameEvent.ACTIVITY_EVENTS, ActvtEventType.PRODUCE_MATERIAL, 1);
		return true;
	}
	
	/**
	 * 购买格子
	 */
	public boolean buyItemSize(Role role,RoleBuild build){
		int costMoney = (int) (25*2*Math.pow(2, itemSize));//购买格子消耗金币
		if(role.getMoney() < costMoney){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,costMoney);
			return false;
		}
		if(itemSize >= Const.GEM_MAX_GRID_SIZE){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_MONEY,costMoney);
			return false;
		}
		itemSize += 1;
		role.redRoleMoney(costMoney);
		LogManager.goldConsumeLog(role, costMoney, EventName.buyItemSize.getName());
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		build.sendToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		try {
			NewLogManager.buildLog(role, "unlock_mine_slot",costMoney);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		return true;
	}
	
	@Override
	public void tick(Role role,RoleBuild build,long now) {
		tickUpdateExpirationTime(true);
	}

	@Override
	public void deserialize(String str, RoleBuild build) {
		if (StringUtils.isNull(str)){
			return;
		}
		Map<String,String> map = JsonUtil.JsonToObjectMap(str,String.class,String.class);
		state = Byte.parseByte(map.get("state"));
		itemSize = Integer.parseInt(map.get("itemSize"));
		itemId = map.get("itemId");
		if (StringUtils.isNull(itemId)){
			itemId = "";
		}
		String temp = map.get("itemIdFinishLst");
		if (!StringUtils.isNull(temp)){
			String[] strText = temp.split(":");
			for (int i = 0 ; i < strText.length ; i++){
				String armyId = strText[i];
				itemIdFinishLst.add(armyId);
			}
		}
		temp = map.get("itemIdLst");
		if (!StringUtils.isNull(temp)){
			String[] strText = temp.split(":");
			for (int i = 0 ; i < strText.length ; i++){
				String armyId = strText[i];
				itemIdLst.add(armyId);
			}
		}
		//购买次数
		String accelerate = map.get("accelerate");
		if (!StringUtils.isNull(accelerate)){
			String[] strText = accelerate.split(":");
			accelerateTimes = Integer.parseInt(strText[0]);
			expiration = Integer.parseInt(strText[1]);
		}
		tickUpdateExpirationTime(false);
		loadFinish(TimerLastType.TIME_PD_GEM,build);	
		
	}

	@Override
	public String serialize(RoleBuild build) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("state", String.valueOf(state));
		map.put("itemSize", String.valueOf(itemSize));
		map.put("itemId",StringUtils.isNull(itemId) ? "null" : itemId);
		map.put("accelerate", accelerateTimes+":"+expiration);
		if (itemIdFinishLst.size() == 0){
			map.put("itemIdFinishLst","null");
		}else{
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < itemIdFinishLst.size() ; i ++){
				sb.append(itemIdFinishLst.get(i));
				if (i < itemIdFinishLst.size() -1){
					sb.append(":");
				}
			}
			map.put("itemIdFinishLst",sb.toString());
		}
		if (itemIdLst.size() == 0){
			map.put("itemIdLst","null");
		}else{
			StringBuffer sb = new StringBuffer();
			for(int i = 0 ; i < itemIdLst.size() ; i ++){
				sb.append(itemIdLst.get(i));
				if (i < itemIdLst.size() -1){
					sb.append(":");
				}
			}
			map.put("itemIdLst",sb.toString());
		}
		String result = JsonUtil.ObjectToJsonString(map);
		return result;
	}
	
	public void loadFinish(TimerLastType type, RoleBuild build){
		TimerLast timer = build.searchTimer(type);
		if (timer == null){
			return;
		}
		long time = TimeUtils.nowLong() / 1000 - timer.getStart();
		if(time < Const.GEM_PD_TIME/* || itemIdLst.size() == 0*/){
			timer.registTimeOver(this);
			return;
		}
		if(time >= Const.GEM_PD_TIME){
			itemIdFinishLst.add(itemId);
			itemId = "";
			time -= Const.GEM_PD_TIME;
		}
		if(itemIdLst.size() > 0){
			while(time > Const.GEM_PD_TIME){
				itemIdFinishLst.add(itemIdLst.pop());
				time -= Const.GEM_PD_TIME;
				if(itemIdLst.size() == 0){
					state = 0;
					build.removeTimer(type);
					break;
				}
			}
		}else{
			state = 0;
			build.removeTimer(type);
		}
		if (itemIdLst.size() > 0){
			itemId = itemIdLst.pop();
			timer.setStart(TimeUtils.nowLong()/1000 - time);
			timer.registTimeOver(this);
		}
	}

	@Override
	public void sendToClient(ParametersEntity params) {
		params.put( buildComType.getKey() );//String, 功能组件名称
		params.put(state);//byte 0-无生产，1-正在生产
		params.put(itemId);//String
		
		params.put(itemSize);// 可用队列大小
		params.put(itemIdLst.size());// 等待物品列表大小
		for (int i = 0 ; i < itemIdLst.size() ; i++){
			String id = itemIdLst.get(i);
			params.put(id);// 物品id
		}
		params.put(itemIdFinishLst.size());//已完成物品列表大小
		for(String itemid : itemIdFinishLst){
			params.put(itemid);//物品id
		}
		//int 加速金币数量
		params.put(tickUpdateExpirationTime(false));
	}
	@Override
	public void finish() {
		itemIdFinishLst.add(itemId);
	}
	
	public void addLastItems() {
		if (itemIdLst.size() > 0) {
			// 继续生产下一个
			itemId = itemIdLst.pop();
			long last = Const.GEM_PD_TIME;
			Role role = world.getObject(Role.class, uid);
			if (role != null) {
				RoleCityAgent cityAgent = role.getCity(cityId);
				RoleBuild build = cityAgent.searchBuildById(buildId);
				if (build != null) {
					TimerLast timer = build.addBuildTimer(last, TimerLastType.TIME_PD_GEM);
					timer.registTimeOver(this);
					state = 1;
					RespModuleSet rms = new RespModuleSet();
					build.sendToClient(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
				}
			}
		} else {
			state = 0;
		}
	}
	
	
	/**
	 * 
	* @Title: updateExpirationTime 
	* isDown  是否下发
	* @Description: 更新时间
	* 材料加工厂的加速花费，按照10/20/40/60/80/100，6次之后每次消耗100金币；每日0点更新已使用的清除次数；
	* min 10
	* max 100
	* @return int 需要花费的钱
	* 
	 */
	public int tickUpdateExpirationTime(boolean isDown){
		//超过一天
		if(TimeUtils.nowLong()/Const.SECOND - expiration  >=0 ){
			//次数置为0
			accelerateTimes = 0;
			expiration = (int)(TimeUtils.getToday24Time()/Const.SECOND) ;
			if(isDown){
				//更新到客户端
				Role role = world.getObject(Role.class, uid);
				if (role != null) {
					RoleCityAgent cityAgent = role.getCity(cityId);
					RoleBuild build = cityAgent.searchBuildById(buildId);
					if (build != null) {
						RespModuleSet rms = new RespModuleSet();
						build.sendToClient(rms);
						MessageSendUtil.sendModule(rms, role.getUserInfo());
					}
				}
			}
		}
		int cost = accelerateTimes*20;
		cost=Math.min(cost, Const.MAX_GEM_ACCELERATE);
		return Math.max(Const.MIN_GEM_ACCELERATE, cost);
	}
	
	/**
	 * 购买加速
	* @Title: buyAccelerate 
	* @Description: 
	* 
	* @return void
	 */
	public void buyAccelerate(){
		accelerateTimes++;
	}
	
	@Override
	public BuildComponentType getBuildComponentType() {
		return buildComType;
	}

	@Override
	public void setBuildParams(RoleBuild build) {
	}


	@Override
	public boolean isWorking(Role role, RoleBuild build) {
		// TODO Auto-generated method stub
		return false;
	}


}
