package com.joymeng.slg.domain.map.impl.still.union.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.still.union.MapRadiation;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.RaiseCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.world.TaskPool;

public class MapUnionNuclearsilo extends MapUnionBuild {
	TimerLast launchTimer;//发射倒计时
	TimerLast coolingTimer;//冷却倒计时
	int bombPosition;//爆炸点
	List<RaiseCell> raises = new ArrayList<RaiseCell>();//已收集的发射需要的物资的列表
	Map<String,Integer> needs = new HashMap<String,Integer>();//发射需要的物资列表
	int rw;
	int rh;
	
	@Override
	public void _init(){
		Worldbuildinglevel wbl = getLevelData();
		List<String> params = wbl.getParamList();
		String sw = params.get(2);
		String sh = params.get(3);
		rw = Integer.parseInt(sw);
		rh = Integer.parseInt(sh);
		for (int i = 4 ; i < params.size() ; i++){
			String[] temp = params.get(i).split(":");
			needs.put(temp[0], Integer.parseInt(temp[1]));
		}
	}

	public void registLaunchTimer(TimerLast timer){
		launchTimer = timer;
		timer.registTimeOver(this);
		taskPool.mapTread.addObj(this,timer);
	}
	
	public void registCoolingTimer(TimerLast timer){
		coolingTimer = timer;
		timer.registTimeOver(this);
		taskPool.mapTread.addObj(this,timer);
	}
	
	/**
	 * 添加集资物品
	 * @param key
	 * @param num
	 * @return
	 */
	public boolean tryToRaise(String key , int num){
		return false;
	}
	
	/**
	 * 尝试发射核弹
	 * @return
	 */
	public boolean tryToLaunch(){
		return false;
	}
	
	public void clear(){
		coolingTimer = null;
		bombPosition = 0;
		raises.clear();
		setMapThreadFlag(true);
	}
	
	@Override
	public String serializeSelf() {
		JoyBuffer buffer = JoyBuffer.allocate(1024);
		buffer.putInt(coolingTimer == null ? 0 : 1);
		if (coolingTimer != null){
			coolingTimer.serialize(buffer);
		}
		buffer.putInt(bombPosition);
		buffer.putInt(raises.size());
		for (int i = 0 ; i  < raises.size() ; i++){
			RaiseCell cell = raises.get(i);
			cell.serialize(buffer);
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
		if (buffer.getInt() == 1){
			TimerLast timer = new TimerLast();
			timer.deserialize(buffer);
			registCoolingTimer(timer);
		}
		bombPosition  = buffer.getInt();
		int size = buffer.getInt();
		for (int i = 0 ; i < size ; i++){
			RaiseCell cell = new RaiseCell();
			cell.deserialize(buffer);
			raises.add(cell);
		}
	}
	
	@Override
	public void _finish(int type) {
		buildTimer = null;
		if (type == 1){//建筑完成
			Worldbuildinglevel wbl = getLevelData();
			int time  = Integer.parseInt(wbl.getParamList().get(0));
			long now = TimeUtils.nowLong() / 1000;
			TimerLast timer = new TimerLast(now,time,TimerLastType.TIME_MAP_UNION_NUCLEARSILO_COOL);
			registCoolingTimer(timer);
		}else if (type == 2){//升级完成
			Worldbuildinglevel wbl = getLevelData();
			int delay = 10 * 60;//提前通知的时间
			int  time  = Integer.parseInt(wbl.getParamList().get(0)) - delay;
			long now = TimeUtils.nowLong() / 1000;
			TimerLast timer = new TimerLast(now,time,TimerLastType.TIME_MAP_UNION_NUCLEARSILO_COOL);
			timer.setParam(delay);
			registCoolingTimer(timer);
		}else{
			setMapThreadFlag(true);
		}
	}

	
	@Override
	public void _tick(long now) {
		super._tick(now);
		if (coolingTimer != null){//冷却逻辑
			if (coolingTimer.over(now)){
				coolingTimer.die();
			}
		}else if (launchTimer != null){//发射逻辑
			if (launchTimer.over(now)){
				launchTimer.die();
			}
		}
	}
	
	@Override
	public byte getState(){
		byte state = super.getState();
		if (state > 0){
			return state;
		}
		if (coolingTimer != null){//正在冷去
			return 4;
		}
		if (launchTimer != null){//正在发射
			return 5;
		}
		return 0;
	}
	
	@Override
	public void finish() {
		super.finish();
		if (coolingTimer != null){//冷却逻辑
			setMapThreadFlag(true);
			coolingTimer = null;
		}else if (launchTimer != null){//发射逻辑
			if (launchTimer.getParam() != null){
				int delay = Integer.parseInt(launchTimer.getParam().toString());
				long last = launchTimer.getLast() + delay;
				launchTimer.setLast(last);
				//通知全服核弹将在10秒后引爆
				AbstractClientModule module = new AbstractClientModule() {
					@Override
					public short getModuleType() {
						return NTC_DTCD_NUCLEARSILO_WORNING;
					}
				};
				module.add(position);//int 核弹发射位置
				module.add(bombPosition);//int 核爆中心
				module.add(rw);//int 核爆影响半径宽
				module.add(rh);//int 核爆影响半径高
				RespModuleSet resp = new RespModuleSet();
				resp.addModule(module);
				MessageSendUtil.sendMessageToOnlineRole(resp,null);
				launchTimer.setParam(null);
			}else{
				//爆炸范围
				List<Integer> ranges = MapUtil.computeIndexs(bombPosition,rw,rh);
				List<Integer> dels = new ArrayList<Integer>();
				//拆除所有的资源田
				String buffStr = getLevelData().getParamList().get(1);
				for (int i = 0 ; i < ranges.size() ; i++){
					Integer pos = ranges.get(i);
					MapObject obj = mapWorld.searchObject(pos.intValue());
					if (obj != null && obj.destroy(buffStr)){
						dels.addAll(obj.reject());
					}
				}
				for (int i = 0 ; i < dels.size() ; i++){
					Integer pos = dels.get(i);
					if (ranges.contains(pos)){
						ranges.remove(pos);
					}
				}
				for (int i = 0 ; i < ranges.size() ; i++){
					Integer pos = ranges.get(i);
					MapRadiation radiation = mapWorld.create(MapRadiation.class,true);
					mapWorld.updatePosition(radiation,pos);
					TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000,TaskPool.SECONDS_PER_DAY/1000,TimerLastType.TIME_MAP_RADIATION_DIE);
					radiation.registDieTimer(timer);
				}
				clear();
			}
		}
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_NUCLEARSILO;
	}
	
	@Override
	public void levelUp(UnionBody union) {
		super.levelUp(union);
		coolingTimer = null;
		launchTimer  = null;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
		if (coolingTimer != null){
			 out.putInt(1);
			 coolingTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
		if (launchTimer != null){
			 out.putInt(1);
			 launchTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
		//集资的物资
		out.putInt(raises.size());
		for (int i = 0 ; i < raises.size() ; i++){
			RaiseCell cell = raises.get(i);
			cell.serialize(out);
		}
	}
}
