package com.joymeng.slg.domain.map.impl.still.res;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Resourcefield;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.MapRefreshObj;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;

/**
 * 大地图资源点
 * @author tanyong
 *
 */
public class MapResource extends MapRefreshObj implements TimerOver {
	float output;//产量
	ResourceCollecter collecter = new ResourceCollecter();
	
	public float getOutput() {
		return output;
	}

	public void setOutput(float output) {
		this.output = output;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_RESOURCE;
	}

	public Resourcefield getData(){
		Resourcefield data = dataManager.serach(Resourcefield.class,new SearchFilter<Resourcefield>(){
			@Override
			public boolean filter(Resourcefield data) {
				return data.getLevel() == level && data.getType().equals(key);
			}
		});
		return data;
	}
	
	/**
	 * 初始化产量
	 */
	public void initOutPut(){
		Resourcefield data = getData();
		output = data.getTotal();
	}
	
	@Override
	public void registerAll() {
		
	}
	
	@Override
	public void _tick(long now) {
		if (collecter.getTroopId() > 0){
			collecter.tick(this);
		}
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(key,JoyBuffer.STRING_TYPE_SHORT);//string 资源类型固化编号
		out.putInt(level);//int 资源等级
		collecter.serialize(out);
		out.putPrefixedString(String.valueOf(output),JoyBuffer.STRING_TYPE_SHORT);//string
		super.serialize(out);
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_RESOURCES;
	}

	@Override
	public void _loadFromData(SqlData data) {
		output             = data.getFloat(RED_ALERT_RESOURCES_OUTPUT);
		String str = data.getString(RED_ALERT_GENERAL_SAFETIMER);
		safeTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		collecter._loadFromData(this,data);
		List<GarrisonTroops> troopses = getDefencers();
		for (GarrisonTroops toops : troopses){
			if (toops.getTimer().getType() == TimerLastType.TIME_MAP_COLLECT){
				toops.registTimer(toops.getTimer(),this);
				collecter.setTroopId(toops.getId());
				info.copy(toops.getTroops().getInfo());
				break;
			}
		}
	}

	@Override
	public void _saveToData(SqlData data) {
		data.put(RED_ALERT_RESOURCES_OUTPUT,output);
		String str = JsonUtil.ObjectToJsonString(safeTimer);
		data.put(RED_ALERT_GENERAL_SAFETIMER,str);
		collecter._saveToData(data);
	}
	
	@Override
	public boolean _couldAttack(ExpediteTroops expedite){
		return true;
	}
	
	public GarrisonTroops getCollecterTroops(){
		return collecter.troops();
	}
	
	public float computeCollectNum(){
		float cRate = MapUtil.getCollectRate(key);
		float result = collecter.computeCollectNum(cRate,level);
		return result;
	}
	
	/**
	 * 获取帮我驻防的盟友
	 * @return
	 */
	public List<GarrisonTroops> getHelpers(){
		List<GarrisonTroops> result = getDefencers();
		if (result.size() > 0){
			Iterator<GarrisonTroops> iterator = result.iterator();
			while (iterator.hasNext()){
				GarrisonTroops garrison = iterator.next();
				if (garrison.getTimer().getType().ordinal() == TimerLastType.TIME_MAP_COLLECT.ordinal()){
					iterator.remove();
				}
			}
		}
		return result;
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			long  uid = expedite.getLeader().getInfo().getUid();
			Role role = world.getRole(uid);
			int start = expedite.getStartPosition(); //出发位置
			int end = expedite.getTargetPosition(); //目标位置
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
				//驻防部队
				garrison(expedite);
				logSpy(role,start,end,expedite.getId());
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){
				//资源田侦查报告
				MapUtil.spyResport(SpyType.SPY_TYPE_RESOURCE, expedite, this);
				logSpy(role,start, end,expedite.getId());
			} else {
				//防御方战斗部队初始化
				if (info.getUid() > 0 && collecter.getTroopId() > 0){//玩家占领的
					List<GarrisonTroops> defenders = getDefencers();//驻防者
					GarrisonTroops collecterTroops = getCollecterTroops();//采集者
					Collections.sort(defenders);
					boolean isWin = attackDefenders(false,defenders,expedite,ReportTitleType.TITLE_TYPE_A_RESOURCES,ReportTitleType.TITLE_TYPE_D_RESOURCES);
					float cRate = MapUtil.getCollectRate(key);
					if (isWin){//攻击方胜利
						float have = collecter.computeCollectNum(collecterTroops,cRate,level);
						float redNum = collecter.computeAllCollectNum(collecterTroops,cRate);
						output = Math.max(0,output-redNum);
						Map<String,Integer> resources = new HashMap<String,Integer>();
						int grabNum = Math.round(have);
						resources.put(key,grabNum);
						role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ROB_RESOURCE,key,grabNum);
						expedite.goBackToCome(resources);//资源塞入行军部队的包裹
						backToComeFrom(-1);
						GameLog.info(info.getUid() + "'s resource<" + key + "> was destroyed by " + expedite.getLeader().getInfo().getUid() + " at " + position);
					    LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
					}else{
						expedite.goBackToCome();
						//检测采集者战斗后负重够了
						Role preRole = world.getRole(info.getUid());
						float weight = collecterTroops.getTroops().computeWeight(preRole);
						float have = collecter.computeCollectNum(collecterTroops,cRate,level);
						if  (have >= weight){//负重不够了需要立即返回
							backToComeFrom(have);
						}else{
							updateCollecteBuff(preRole,weight,have,cRate);
						}
						LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
						GameLog.info(info.getUid() + "'s resource<" + key + "> defend successful when fight with " + expedite.getLeader().getInfo().getUid() + " at " + position);
					}
				}else{//怪物防守
					Resourcefield resourceField = getData();//资源田固化数据
					String monsterKey = resourceField.getMonster();
					Monster monster = dataManager.serach(Monster.class,monsterKey);
					if (monster == null){
						GameLog.info("SB策划又填错数据了");
					}else{//战斗逻辑,战报下发,伤兵处理等等
						TroopsData defender = TroopsData.create(monster,position);
						boolean isWin = attackMonster(defender,expedite,true,ReportTitleType.TITLE_TYPE_A_RESOURCES);
						if (isWin){
							MapUtil.drop(monster,expedite);
							startCollect(expedite,resourceField);
							LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
							LogManager.mapLog(role, start, end,  expedite.getId(),"startCollecting");
							GameLog.info(expedite.getLeader().getInfo().getUid() + " occopy resource<" + key + "> successful at " + position);
						}else{
							LogManager.mapLog(role, start, end,  expedite.getId(),"endOfBattle");
							GameLog.info(expedite.getLeader().getInfo().getUid() + " occopy resource<" + key + "> fail at " + position);
							expedite.goBackToCome();
						}
						role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_FIT_MST_T, isWin, monster.getId());
					}
				}
			}
		}
	}
	
	
	private void startCollect(ExpediteTroops expedite, Resourcefield resourceField) {
		collecter.clear();
		TroopsData leader = expedite.getLeader();
		MapRoleInfo cInfo = leader.getInfo();
		Role role = world.getRole(cInfo.getUid());
		Resourcestype rType = dataManager.serach(Resourcestype.class,key);
		float weight = expedite.computeWeight(role) / rType.getWeight();//部队负重能采集多少资源的数量
		float total = Math.min(weight,output);
		float value = MapUtil.updateCollecter(this,collecter,role,cInfo.getCityId());
		float collectSpeed = leader.computeCollectSpeed(role, key,value);
		//实际采集时间 = 资源兑换率  * 野地现有资源量/平均采集速度*(1-科技缩短的采集时间)
		float rate = MapUtil.getCollectRate(key);//资源兑换率
		int garrisonTime = (int)(rate * total / (collectSpeed * (1 + value)));
		long now = TimeUtils.nowLong() / 1000;
		TimerLast collecteTimer = new TimerLast(now,garrisonTime,TimerLastType.TIME_MAP_COLLECT);
		GarrisonTroops occuper = expedite.occuper(this);
		occuper.registTimer(collecteTimer,this);//添加采集倒计时
		//设置保护时间
		safeTimer = new TimerLast(now,resourceField.getFreetime(),TimerLastType.TIME_MAP_OBJ_SAFE);
		info.copy(occuper.getTroops().getInfo());
		collecter.start(occuper,collectSpeed,value);
		sendChange();
		try {
			PointVector point = MapUtil.getPointVector(position);
			StringBuffer sb = new StringBuffer();
			List<ArmyEntity> armys = leader.getArmys();
			for (int j = 0; j < armys.size(); j++) {
				ArmyEntity entry = armys.get(j);
				sb.append(entry.getKey());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(entry.getSane());
				sb.append(GameLog.SPLIT_CHAR);
			}
			String newStr = sb.toString().substring(0,sb.toString().length() - 1);
			NewLogManager.mapLog(role, "attack_resource_field",key,(int)point.x,(int)point.y,newStr);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
		
	}

	/**
	 * 采集者要回家了，这时候其他防御者强制回家
	 * @param have
	 */
	private void backToComeFrom(float have){
		GarrisonTroops collecterTroops = collecter.troops();
		if (collecterTroops == null){
			return;
		}
		if (destoryFlag){
			collecterTroops.backToCome();
		}else{
			MapCell startCell = mapWorld.getMapCell(position);
			List<GarrisonTroops> occupyers = getDefencers();
			for (int i = 0 ; i < occupyers.size() ; i++){
				GarrisonTroops garrison = occupyers.get(i);
				startCell.removeOccupyer(garrison.getId());//移除部队
				if (garrison.getTimer().getType().ordinal() == TimerLastType.TIME_MAP_COLLECT.ordinal()){
					continue;
				}
				garrison.die();//驻防的其他玩家的部队都强制回家
			}
			int total = 0;
			float weight = 0;
			float collectRate = MapUtil.getCollectRate(key);
			Role role = world.getRole(info.getUid());
			if (have == 0){
				weight = collecterTroops.getTroops().computeWeight(role);
				have = collecter.computeCollectNum(collecterTroops,collectRate,level);
			}
			total = (int)Math.min(weight,have);//本次采集的资源
			ExpediteTroops expedite = collecterTroops.backToCome();
			if (total > 0){
				expedite.getLeader().addSomethingToPackage(ExpeditePackageType.PACKAGE_TYPE_RESOURCE,key,total);//添加资源到行军部队
				// 下发采集报告
				CollectReport report = new CollectReport(String.valueOf(TimeUtils.nowLong() / 1000), position, total,level, key);
				String reportStr = JsonUtil.ObjectToJsonString(report);
				chatMgr.creatBattleReportAndSend(reportStr, ReportType.TYPE_COLLECTION, null, role);
				//任务事件
				ResourceTypeConst resType = ResourceTypeConst.search(key);
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_RESS_CLT, resType, total);
				role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.GATHER_RESOURCE,key,total);
			}
			float redNum = collecter.computeAllCollectNum(collecterTroops,collectRate);
			output = Math.max(0,output-redNum);
			if (output <= 10){//资源田被采集完了,低于10的资源就算这个资源田被采集空
				remove();
			} else {
				setDeleteFlag(true);
				save();
			}
			int start = role.getCity(0).getPosition();
			int end = collecterTroops.getPosition();
			LogManager.mapLog(role, start, end, collecterTroops.getId(),"endOfCollection");
			collecter.clear();
			safeTimer = null;
			info.clear();
			sendChange();
		}
	}
	
	@Override
	public void finish() {//采集倒计时结束了
		backToComeFrom(0);
	}
	
	private void updateCollecteBuff(Role role,float weight , float have , float rate){
		GarrisonTroops troops = collecter.troops();
		if (troops != null){
			//实际采集时间 = 资源兑换率  * 野地现有资源量/平均采集速度*（1-科技缩短的采集时间）
			float value = MapUtil.updateCollecter(this,collecter,role,info.getCityId());
			float collectSpeed = troops.getTroops().computeCollectSpeed(role,key,value);
			long now = TimeUtils.nowLong() / 1000 ;
			float total = weight - have;
			long collectTime = Math.max(0,now - troops.getTimer().getStart());
			collecter.addBuff(collectTime,have,collectSpeed,value);
			int garrisonTime = (int)(rate * total / (collectSpeed + collectSpeed * value));
			troops.getTimer().resetLastAt(now,garrisonTime);//修改持续时间
		}
	}
	
	public void updateCollecteBuff(){
		Role role = world.getRole(info.getUid());
		if (role != null){
			updateCollecteBuff(role);
		}
	}
	
	public void updateCollecteBuff(Role role){
		GarrisonTroops troops = collecter.troops();
		if (troops != null){
			float weight = troops.getTroops().computeWeight(role);
			float rate = MapUtil.getCollectRate(key);
			float have = collecter.computeCollectNum(rate,level);
			updateCollecteBuff(role,weight,have,rate);
		}
	}
}
