package com.joymeng.slg.domain.map.impl.still.res;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionResource;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerOver;

public class ResourceCollecter implements Instances,TimerOver{
	long troopId;
	float collectSpeed = 5.0f;//采集速度
	float collectEffect;//TODO buff 功能 采集buff加成   
	long  collectTime;//已经采集的时间
	float collectNum;//已经采集的量
	boolean needRemove;//需要从列表移除
	List<EffectListener> listeners = new ArrayList<EffectListener>();//buff效果监听者
	GarrisonTroops troops = null;
	
	public long getTroopId() {
		return troopId;
	}

	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}
	
	public boolean remove() {
		return needRemove;
	}

	public void remove(boolean needRemove) {
		this.needRemove = needRemove;
	}

	public GarrisonTroops troops(){
		if (troops == null){
			troops = world.getObject(GarrisonTroops.class,troopId);
		}
		return troops;
	}

	public void _loadFromData(MapResource res,SqlData data) {
		collectSpeed     = data.getFloat(DaoData.RED_ALERT_RESOURCES_COLLECT_SPEED);
		collectEffect    = data.getFloat(DaoData.RED_ALERT_RESOURCES_COLLECT_EFFECT);
		collectTime      = data.getLong(DaoData.RED_ALERT_RESOURCES_COLLECT_TIME);
		collectNum       = data.getFloat(DaoData.RED_ALERT_RESOURCES_COLLECT_NUM);
		byte[] tempDatas = (byte[])data.get(DaoData.RED_ALERT_RESOURCES_COLLECT_LISTENERS);
		JoyBuffer buffer = JoyBuffer.wrap(tempDatas);
		int size = buffer.getInt();
		for (int i = 0 ; i < size ; i++) {
			EffectListener listener = new EffectListener();
			listener.deserialize(buffer);
			listeners.add(listener);
			taskPool.mapTread.addObj(res,listener.getTimer());
		}
	}

	public void _saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_RESOURCES_COLLECT_SPEED,collectSpeed);
		data.put(DaoData.RED_ALERT_RESOURCES_COLLECT_EFFECT,collectEffect);
		data.put(DaoData.RED_ALERT_RESOURCES_COLLECT_TIME,collectTime);
		data.put(DaoData.RED_ALERT_RESOURCES_COLLECT_NUM,collectNum);
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		_serialize(buffer);
		data.put(DaoData.RED_ALERT_RESOURCES_COLLECT_LISTENERS,buffer.arrayToPosition());
	}
	
	public void clear(){
		troopId       = 0;
		collectSpeed  = 0;
		collectEffect = 0;
		collectTime   = 0;
		collectNum    = 0;
		troops        = null;
		listeners.clear();
	}

	public void start(GarrisonTroops occuper,float collectSpeed,float collectEffect) {
		troopId            = occuper.getId();
		this.collectSpeed  = collectSpeed;
		this.collectEffect = collectEffect;
	}
	
	public void addBuff(long time , float value , float collectSpeed , float collectEffect) {
		this.collectSpeed  = collectSpeed;
		this.collectEffect = collectEffect;
		this.collectTime   = time;
		this.collectNum    = value;
	}
	
	public void motifyBuff(float collectSpeed , float collectEffect) {
		this.collectSpeed  = collectSpeed;
		this.collectEffect = collectEffect;
	}
	
	/**
	 * 计算实际获得量
	 * @param rate
	 * @param level
	 * @return
	 */
	public float computeCollectNum(GarrisonTroops collecter,float rate , int level){
		//实际采集量=平均采集速度*采集时间*(1+科技缩短的采集时间)*采集收益率/资源兑换率
		//采集收益率 = 1+(野地等级^1.65-1.45*基地等级)/100; (取消)
		
		if (collecter == null){
			GameLog.info("--------computeCollectNum--collecter="+null+"level="+level+"|rate="+rate+"------\n");
			return 0;
		}
		StringBuffer buffer = new StringBuffer("--------computeCollectNum--collecter="+collecter.getPosition()+"level="+level+"|rate="+rate+"------\n");
		int cityLevel = collecter.getTroops().getInfo().getLevel();
		float effect = 1;//(float)(1 + (Math.pow(level,1.65f) - 1.45f * cityLevel) / 100);
		long now = TimeUtils.nowLong() / 1000;
		long last = collecter.getTimer().getLast();
		long totalTime = 0;
		if (now > collecter.getTimer().getStart() + last){
			totalTime = last - collectTime;
		}else{
			totalTime = now - collecter.getTimer().getStart() - collectTime;
		}
		if (totalTime <= 0){
			return 0;
		}
		float temp = collectSpeed * (1 + collectEffect) * totalTime * effect / rate ;
		
		GameLog.info(buffer.append("cityLevel="+cityLevel+"|effect="+effect+"|totalTime="+totalTime+"|temp="+temp+"|collectNum="+collectNum+"\n").append("-------------").toString());
		return temp + collectNum;
	}
	
	public float computeAllCollectNum(GarrisonTroops collecter,float rate){
		long now = TimeUtils.nowLong() / 1000;
		long last = collecter.getTimer().getLast();
		long totalTime = 0;
		if (now > collecter.getTimer().getStart() + last){
			totalTime = last - collectTime;
		}else{
			totalTime = now - collecter.getTimer().getStart() - collectTime;
		}
		if (totalTime <= 0){
			return 0;
		}
		float temp = collectSpeed * (1 + collectEffect) * totalTime / rate ;
		return temp + collectNum;
	}
	
	/**
	 * 收益量
	 * @param rate
	 * @param level
	 * @return
	 */
	public float computeCollectNum(float rate , int level){
		return computeCollectNum(troops(),rate,level);
	}
	
	/**
	 * 采集量
	 * @param rate
	 * @return
	 */
	public float computeAllCollectNum(float rate){
		return computeAllCollectNum(troops(),rate);
	}
	
	public void serialize(JoyBuffer out) {
		out.putLong(troopId);
		out.putPrefixedString(String.valueOf(collectSpeed),JoyBuffer.STRING_TYPE_SHORT);//string 采集速度
		out.putPrefixedString(String.valueOf(collectEffect),JoyBuffer.STRING_TYPE_SHORT);//string 采集时间加成
		out.putLong(collectTime);//已采集的时间
		out.putPrefixedString(String.valueOf(collectNum),JoyBuffer.STRING_TYPE_SHORT);//已采集的资源量
	}
	
	public void _serialize(JoyBuffer buffer) {
		buffer.putLong(troopId);
		buffer.putPrefixedString(String.valueOf(collectSpeed),JoyBuffer.STRING_TYPE_SHORT);//string 采集速度
		buffer.putPrefixedString(String.valueOf(collectEffect),JoyBuffer.STRING_TYPE_SHORT);//string 采集时间加成
		buffer.putLong(collectTime);//已采集的时间
		buffer.putPrefixedString(String.valueOf(collectNum),JoyBuffer.STRING_TYPE_SHORT);//已采集的资源量
		buffer.putPrefixedString(String.valueOf(listeners.size()));
		for (int i = 0 ; i < listeners.size() ; i++){
			EffectListener listener = listeners.get(i);
			listener.serialize(buffer);
		}
	}
	
	public void _deserialize(JoyBuffer buffer) {
		troopId = buffer.getLong();
		collectSpeed  = Float.parseFloat(buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
		collectEffect = Float.parseFloat(buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
		collectTime   = buffer.getLong();
		collectNum    = Float.parseFloat(buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));
		int size      = Integer.parseInt(buffer.getPrefixedString());
		for (int i = 0 ; i < size ; i++){
			EffectListener listener = new EffectListener();
			listener.deserialize(buffer);
			listeners.add(listener);
		}
		GarrisonTroops troops = troops();
		if (troops != null){
			troops.getTimer().registTimeOver(this);
			taskPool.mapTread.addObj(troops,troops.getTimer());
		}
	}
	
	@Override
	public void finish() {
		needRemove = true;
	}

	public int over(MapUnionResource mapUnionResource, float value) {
		GarrisonTroops troops = troops();
		ExpediteTroops expedite = troops.backToCome();
		Role role = world.getRole(troops.getTroops().getInfo().getUid());
		float weight = troops.getTroops().computeWeight(role);
		int getNum = (int)Math.min(weight,value);//本次采集的资源
		if (getNum > 0){
			String resKey = mapUnionResource.getResourceType().getId();
			expedite.getLeader().addSomethingToPackage(ExpeditePackageType.PACKAGE_TYPE_RESOURCE,resKey,getNum);//添加资源到行军部队
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.GATHER_RESOURCE,resKey,getNum);
		}
		return getNum;
	}

	public void tick(MapResource mapResource) {
		boolean needUpdate = false;
		for (int  i = 0 ; i < listeners.size() ;){
			EffectListener listener  = listeners.get(i);
			if (listener.getTimer().over()){
				listeners.remove(i);
				needUpdate = true;
			}else{
				i++;
			}
		}
		if (needUpdate){
			mapResource.updateCollecteBuff();
		}
		if (listeners.size() == 0){
			mapResource.setMapThreadFlag(true);
		}
	}

	public boolean add(EffectListener el) {
		for (int i = 0 ; i < listeners.size() ; i++){
			EffectListener listener = listeners.get(i);
			if (listener.getTimer() == el.getTimer()){//相同的buff不在添加了
				return false;
			}
		}
		listeners.add(el);
		return true;
	}
}
