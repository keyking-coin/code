package com.joymeng.slg.domain.map.impl.still.union.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.res.ResourceCollecter;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

/**
 * 联盟资源田
 * @author tanyong
 *
 */
public class MapUnionResource extends MapUnionBuild {
	float total;//总产量
	List<ResourceCollecter> collecters = new ArrayList<ResourceCollecter>();//采集者们
	boolean nextAllGoBack = false;//下个循环需要让所有采集部队回家
	boolean nextDie = false;//下个循环死亡
	
	@Override
	public void _init(){
		TimerLast timer = buildTimer;
		if (timer == null){
			timer = new TimerLast(TimeUtils.nowLong()/1000,0,TimerLastType.TIME_FOREVER);
		}
		taskPool.mapTread.addObj(this,timer);
		total = Float.parseFloat(getLevelData().getParamList().get(0).split(":")[1]);
	}
	
	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}
	
	public Resourcestype getResourceType(){
		ResourceTypeConst type = null;
		if (buildKey.equals(BuildName.MAP_UNION_FOOD.getKey())){
			type = ResourceTypeConst.RESOURCE_TYPE_FOOD;
		}else if (buildKey.equals(BuildName.MAP_UNION_METAL.getKey())){
			type = ResourceTypeConst.RESOURCE_TYPE_METAL;
		}else if (buildKey.equals(BuildName.MAP_UNION_OIL.getKey())){
			type = ResourceTypeConst.RESOURCE_TYPE_OIL;
		}else{
			type = ResourceTypeConst.RESOURCE_TYPE_ALLOY;
		}
		return dataManager.serach(Resourcestype.class,type.getKey());
	}
	
	@Override
	public void _tick(long now) {
		super._tick(now);
		synchronized (collecters) {
			float collectNum = 0;
			float rate = MapUtil.getCollectRate(buildKey);
			for (int  i  = 0 ; i < collecters.size() ; ){
				ResourceCollecter collecter = collecters.get(i);
				if (nextAllGoBack || nextDie){
					collecter.troops().die();
					collecters.remove(i);
				}else{
					float value = collecter.computeAllCollectNum(rate);
					if (collecter.remove()){
						value = collecter.over(this,value);
						total = Math.max(0,total-value);
						collecters.remove(i);
					}else{
						collectNum += value;
						i++;
					}
				}
			}
			if (nextDie){
				remove();
			}else{
				nextDie = collectNum >= total;
			}
		}
	}
	
	@Override
	public String serializeSelf() {
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		buffer.putPrefixedString(String.valueOf(total));
		buffer.putPrefixedString(String.valueOf(collecters.size()));
		for (int i = 0 ; i < collecters.size() ; i++){
			ResourceCollecter collecter = collecters.get(i);
			collecter._serialize(buffer);
		}
		byte[] data = buffer.arrayToPosition();
		String result = null;
		try {
			result = new String(data,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void deserializeSelf(String str) {
		try {
			if (StringUtils.isNull(str)){
				return;
			}
			byte[] data = null;
			try {
				data = str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return;
			}
			JoyBuffer buffer = JoyBuffer.wrap(data);
			total = Float.parseFloat(buffer.getPrefixedString());
			String sizeStr = buffer.getPrefixedString();
			int size = Integer.parseInt(sizeStr);
			for (int i = 0 ; i < size ; i++){
				ResourceCollecter collecter  = new ResourceCollecter();
				collecter._deserialize(buffer);
				collecters.add(collecter);
			}
		} catch (Exception e) {
			total = Float.parseFloat(getLevelData().getParamList().get(0).split(":")[1]);
			List<GarrisonTroops> troopses = getDefencers();
			for (int i = 0 ; i < troopses.size() ; i++){
				GarrisonTroops troops = troopses.get(i);
				TimerLast timer = troops.getTimer();
				if (timer.getType() != TimerLastType.TIME_MAP_COLLECT){
					continue;
				}
				ResourceCollecter collecter  = new ResourceCollecter();
				collecter.setTroopId(troops.getId());
				timer.registTimeOver(collecter);
				taskPool.mapTread.addObj(troops,timer);
				collecters.add(collecter);
			}
		}
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_RESOURCE;
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite){
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_UNION_RES_COLLECT){
				MapUnionCity city = getCity();
				if (city != null && !city.isMain()){//建筑废弃了
					expedite.setNoBattleTip(I18nGreeting.MSG_UNION_BUILD_COULD_NOT_USE);
					expedite.goBackNoFight();
					return;
				}
				//联盟资源点采集
				TroopsData leader = expedite.getLeader();
				Role role = world.getRole(leader.getInfo().getUid());
				Resourcestype rType = getResourceType();
				float weight = expedite.computeWeight(role) / rType.getWeight();//部队负重能采集多少资源的数量
				float could = Math.min(weight,total);
				ResourceCollecter collecter = new ResourceCollecter();
				float value = MapUtil.updateCollecter(this,collecter,role,leader.getInfo().getCityId());
				float collectSpeed = leader.computeCollectSpeed(role,rType.getId(),value);
				//实际采集时间 = 资源兑换率  * 野地现有资源量/平均采集速度*(1-科技缩短的采集时间)
				float rate = MapUtil.getCollectRate(buildKey);//资源兑换率
				int collectTime = (int)(rate * could / (collectSpeed * (1 + value)));
				long now = TimeUtils.nowLong() / 1000;
				TimerLast collecteTimer = new TimerLast(now,collectTime,TimerLastType.TIME_MAP_COLLECT);
				GarrisonTroops occuper = expedite.occuper(this);
				collecter.start(occuper,collectSpeed,value);
				occuper.registTimer(collecteTimer,collecter);//添加采集倒计时
				collecters.add(collecter);//添加采集者
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				super.troopsArrive(expedite);
			}
		}
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
		out.putPrefixedString(String.valueOf(total),JoyBuffer.STRING_TYPE_SHORT);//long 剩余的总产量
		out.putInt(collecters.size());//采集者数量
		for (int i = 0 ; i < collecters.size() ; i++){//采集者数据
			ResourceCollecter collecter = collecters.get(i);
			collecter.serialize(out);
		}
	}

	@Override
	public void _finish(int type) {
		buildTimer = null;
	}

	/**
	 * 联盟资源田修正buff
	 * @param role
	 */
	public void updateCollecteBuff(Role role) {
		for (int i = 0 ; i < collecters.size() ; i++){
			ResourceCollecter collecter = collecters.get(i);
			GarrisonTroops troops = collecter.troops();
			if (troops != null && troops.getTroops().getInfo().getUid() == role.getId()){
				Resourcestype rType = getResourceType();
				float weight = troops.getTroops().computeWeight(role);
				float rate = MapUtil.getCollectRate(buildKey);
				float have = collecter.computeCollectNum(rate,level);
				float value = MapUtil.updateCollecter(this,collecter,role,troops.getTroops().getInfo().getCityId());
				float collectSpeed = troops.getTroops().computeCollectSpeed(role,rType.getId(),value);
				long now = TimeUtils.nowLong() / 1000 ;
				float total = weight - have;
				//实际采集时间 = 资源兑换率  * 野地现有资源量/平均采集速度*(1-科技缩短的采集时间)
				long collectTime = Math.max(0,now - troops.getTimer().getStart());
				collecter.addBuff(collectTime,have,collectSpeed,value);
				int garrisonTime = (int)(rate * total / (collectSpeed * (1 + value)));
				troops.getTimer().resetLastAt(now,garrisonTime);//修改持续时间
			}
		}
	}
	
	public void updateCollecteBuffNoRecive(Role role) {
		for (int i = 0 ; i < collecters.size() ; i++){
			ResourceCollecter collecter = collecters.get(i);
			GarrisonTroops troops = collecter.troops();
			if (troops != null && troops.getTroops().getInfo().getUid() == role.getId()){
				Resourcestype rType = getResourceType();
				float value = MapUtil.updateCollecter(this,collecter,role,troops.getTroops().getInfo().getCityId());
				float collectSpeed = troops.getTroops().computeCollectSpeed(role,rType.getId(),value);
				collecter.motifyBuff(collectSpeed,value);
			}
		}
	}
	
	@Override
	public void die() {
		nextDie = true;
	}
	
	public void lock(){
		nextAllGoBack = true;
	}
}
