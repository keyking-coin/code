package com.joymeng.slg.domain.map.impl.still.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

/**
 * 站位的代理类
 * @author tanyong
 *
 */
public class MapProxy extends MapObject{
	
	boolean justLogic = false;
	
	MapCellType cellType;
	
	String moveKey = "null";
	
	String name = "null";
	
	public MapProxy(){
		
	}
	
	public MapProxy(boolean justLogic){
		this.justLogic = justLogic;
	}
	
	public void setCellType(MapCellType cellType) {
		this.cellType = cellType;
	}
	
	public String getMoveKey() {
		return moveKey;
	}

	public void setMoveKey(String moveKey) {
		this.moveKey = moveKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void remove() {
		if (justLogic){
			return;
		}
		setDeleteFlag(true);//如果存档过了就删除
		save();
		handleEvent(GameEvent.REMOVE_LIST);
		mapWorld.clearPosition(this);
		//super.remove();
	}
	
	@Override
	public MapCellType cellType() {
		return cellType;
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			long uid = expedite.getLeader().getInfo().getUid();
			Role role = world.getRole(uid);
			int start = expedite.getStartPosition();
			int end = expedite.getTargetPosition();
			TimerLast timer = expedite.getTimer();
			long now = TimeUtils.nowLong() / 1000;
			if (timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS){
				try {
					PointVector point = MapUtil.getPointVector(position);
					StringBuffer sb = new StringBuffer();
					List<ArmyEntity> armys = expedite.getLeader().getArmys();
					for (int j = 0; j < armys.size(); j++) {
						ArmyEntity entry = armys.get(j);
						sb.append(entry.getKey());
						sb.append(GameLog.SPLIT_CHAR);
						sb.append(entry.getSane());
						sb.append(GameLog.SPLIT_CHAR);
					}
					String newStr = sb.toString().substring(0,sb.toString().length() - 1);
					NewLogManager.mapLog(role, "build_fortress", (int) point.x, (int) point.y, newStr);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
				//建造要塞
				MapFortress fortress = mapWorld.create(MapFortress.class,false);
				//判断这里是否可以建筑
				if (mapWorld.checkPosition(fortress,position)){//能放下
					GarrisonTroops occuper = expedite.occuper(this);
					Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,BuildName.MAP_FORTRESS.getKey() + "1");
					int buidlTime = role.getGmFortressCreateTime() > 0 ? role.getGmFortressCreateTime() : wbl.getTime();
					TimerLast buildtTimer = new TimerLast(now,buidlTime,TimerLastType.TIME_CREATE);
					occuper.registTimer(buildtTimer,fortress);//注册建造时间
					//设置保护时间
					TimerLast safeTimer = new TimerLast(now,wbl.getFreetime(),TimerLastType.TIME_MAP_OBJ_SAFE);
					fortress.registSafeTimer(safeTimer);
					fortress.getInfo().copy(occuper.getTroops().getInfo());
					fortress.addGrid(occuper);
					fortress.setName(name);
					remove();
					mapWorld.insertObj(fortress);
					mapWorld.updatePosition(fortress,position);//放置要塞
					role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.BUILD_STRONG_HOLD);//活动建筑要塞
					LogManager.mapLog(role, start, end,expedite.getId(), "startBuildFortres");
				}else{//原路返回
					expedite.goBackToCome();
				}
			}else if (timer.getType() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE){
				//建造迁城点
				MapCityMove move = mapWorld.create(MapCityMove.class,false);
				GarrisonTroops occuper = expedite.occuper(this);
				occuper.getTroops().getPackages().clear();//清空包裹
				Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,moveKey);
				if (wbl == null){
					expedite.goBackToCome();
				}else{
					int buidlTime = role.getGmCityMoveTime() > 0 ? role.getGmCityMoveTime() : wbl.getTime();
					TimerLast buildtTimer = new TimerLast(now,buidlTime,TimerLastType.TIME_CREATE);
					occuper.registTimer(buildtTimer,move);//注册建造时间
					TimerLast safeTimer = new TimerLast(now,wbl.getFreetime(),TimerLastType.TIME_MAP_OBJ_SAFE);
					move.registSafeTimer(safeTimer);
					remove();
					move.getInfo().copy(occuper.getTroops().getInfo());
					mapWorld.insertObj(move);
					mapWorld.updatePosition(move,occuper.getPosition());//放置迁城点
					LogManager.mapLog(role, start, end, expedite.getId(),"startBuildCity");
				}
			}else if (timer.getType() == TimerLastType.TIME_MAP_GARRISON){
				expedite.goBackToCome();
			}
		}
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public void _tick(long now) {
		
	}
	
	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		String str = getClass().getName();
		data.put(RED_ALERT_GENERAL_TYPE,str);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("ct",cellType);
		map.put("mk",moveKey);
		map.put("nm",name);
		str = JsonUtil.ObjectToJsonString(map);
		data.put(RED_ALERT_GENERAL_OTHER,str);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		String str = data.getString(RED_ALERT_GENERAL_OTHER);
		if (!StringUtils.isNull(str)){
			Map<String,Object> temp = (Map<String,Object>)JSON.parse(str);
			if (temp.containsKey("ct")){
				Object obj = temp.get("ct");
				cellType = MapCellType.search(obj.toString());
			}
			if (temp.containsKey("mk")){
				Object obj   = temp.get("mk");
				moveKey = obj.toString();
			}
			if (temp.containsKey("nm")){
				Object obj   = temp.get("nm");
				name = obj.toString();
			}
		}
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_MAPOBJ;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		
	}

	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return false;
	}
}
