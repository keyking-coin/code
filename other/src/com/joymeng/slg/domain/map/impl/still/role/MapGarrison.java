package com.joymeng.slg.domain.map.impl.still.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

/***
 * 玩家据点(驻防点)
 * @author tanyong
 */
public class MapGarrison extends MapObject {
	String fortressName = "null";
	public String getFortressName() {
		return fortressName;
	}

	public void setFortressName(String fortressName) {
		this.fortressName = fortressName;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_GARRISON;
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public void _tick(long now) {
		
	}
	
	public boolean couldMoveCity(){
		return false;
	}
	
	public void dispearWhenMoveCity(){
		remove();
	}

	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		String str = getClass().getName();
		data.put(RED_ALERT_GENERAL_TYPE,str);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("if",info);
		map.put("fn",fortressName);
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
			if (temp.containsKey("if")){
				Object obj = temp.get("if");
				info = JsonUtil.JsonToObject(obj.toString(),MapRoleInfo.class);
			}
			if (temp.containsKey("fn")){
				Object obj = temp.get("fn");
				fortressName = obj.toString();
			}
		}
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_MAPOBJ;
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			long  uid = expedite.getLeader().getInfo().getUid();
			Role role = world.getRole(uid);
			int start = expedite.getStartPosition(); //出发位置
			int end = expedite.getTargetPosition(); //目标位置
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
				info.copy(expedite.getLeader().getInfo());
				garrison(expedite);
				LogManager.mapLog(role, start, end,expedite.getId(), "arriveAtGarrisonPoint");
			} else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY) {
				MapUtil.spyResport(SpyType.SPY_TYPE_GARRISON, expedite, this);
				logSpy(role,start, end,expedite.getId());
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS){
				MapFortress fortress = mapWorld.create(MapFortress.class,true);
				long now = TimeUtils.nowLong() / 1000;
				GarrisonTroops occuper = expedite.occuper(this);
				Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,BuildName.MAP_FORTRESS.getKey() + "1");
				TimerLast buildtTimer = new TimerLast(now,wbl.getTime(),TimerLastType.TIME_CREATE);
				occuper.registTimer(buildtTimer,fortress);//注册建造时间
				//设置保护时间
				TimerLast safeTimer = new TimerLast(now,wbl.getFreetime(),TimerLastType.TIME_MAP_OBJ_SAFE);
				fortress.registSafeTimer(safeTimer);
				fortress.getInfo().copy(occuper.getTroops().getInfo());
				fortress.addGrid(occuper);
				fortress.setName(fortressName);
				_remove();
				mapWorld.forceInsert(this,fortress,position);
				LogManager.mapLog(role, start, end, expedite.getId(),"startBuildFortres");
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				List<GarrisonTroops> defenders = getDefencers();//防守者
				boolean isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_GARRISON,ReportTitleType.TITLE_TYPE_D_GARRISON);
				if (isWin){
					GameLog.info("attacker is winer");
					remove();
				}else{
					GameLog.info("defender is winer");
				}
				//记录战斗次数
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_ATK_WIN, isWin);
				LogManager.mapLog(role, start, end, expedite.getId(),"endOfBattle");
				expedite.goBackToCome();
			}
		}
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		info.serialize(out);
		super.serialize(out);
	}

	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return true;
	}
	
	@Override
	public boolean couldGarrison(long unionId) {
		List<GarrisonTroops> garrisons = getDefencers();
		if (garrisons.size() > 0){
			if (garrisons.get(0).getTroops().checkUnion(unionId)){//可以驻防
				return true;
			}
			return false;
		}else{
			return true;
		}
	}
	
}
