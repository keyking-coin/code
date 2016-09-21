package com.joymeng.slg.domain.map.impl.dynamic;

import java.util.Collections;
import java.util.List;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.LogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.still.role.MapBarracks;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.role.MapGarrison;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.SerializeEntity;

/**
 * 固定部队,依附于地图格子
 * @author tanyong
 */
public class GarrisonTroops extends AbstractObject implements TimerOver,SerializeEntity,Comparable<GarrisonTroops>{
	long id;//数据库主键
	int position;//在那个坐标
	TimerLast timer;//驻防倒计时
	TroopsData troops = new TroopsData();//部队信息
	boolean mustDie;//下个循环必须死
	boolean needGoBackQuick = false;//需要在10秒内回城
	
	public void setId(long id) {
		this.id = id;
	}

	public TroopsData getTroops() {
		return troops;
	}

	public void setTroops(TroopsData troops) {
		this.troops = troops;
	}

	public TimerLast getTimer() {
		return timer;
	}

	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String table() {
		return TABLE_RED_ALERT_GARRISON;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void loadFromData(SqlData data) {
		id              = data.getLong(RED_ALERT_GENERAL_ID);
		position        = data.getInt(RED_ALERT_GENERAL_POSITION);
		String str      = data.getString(RED_ALERT_GARRISON_TIMER);
		if (!StringUtils.isNull(str)) {
			timer = JsonUtil.JsonToObject(str,TimerLast.class);
		}
		str             = data.getString(RED_ALERT_GARRISON_TROOPS);
		troops          = JsonUtil.JsonToObject(str,TroopsData.class);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_ID,id);
		data.put(RED_ALERT_GARRISON_TIMER,JsonUtil.ObjectToJsonString(timer));
		data.put(RED_ALERT_GENERAL_POSITION,position);
		data.put(RED_ALERT_GARRISON_TROOPS,JsonUtil.ObjectToJsonString(troops));
	}
	
	@Override
	public void _tick(long now) {
		if (mustDie || needGoBackQuick || timer.over(now)){
			timer.die();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		setDeleteFlag(true);
		save();
		MapCell cell = mapWorld.getMapCell(position);
		cell.removeOccupyer(id);//格子移除
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(id);//long 部队主键编号
		troops.serialize(out);
		timer.serialize(out);//驻防倒计时
	}

	@Override
	public void finish() {
		backToCome();
	}
	
	public boolean isMustDie() {
		return mustDie;
	}

	public void die(){
		mustDie = true;
		if (!timer.getType().isFlag()){
			timer.registTimeOver(this);
			taskPool.mapTread.addObj(this,timer);
		}
	}
	
	public void registTimer(TimerLast timer) {
		this.timer = timer;
		timer.registTimeOver(this);
	}
	
	public void registTimer(TimerLast timer,TimerOver timerOver) {
		this.timer = timer;
		timer.registTimeOver(timerOver);
		taskPool.mapTread.addObj(this,timer);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String[] wheres() {
		String[] result = new String[1];
		result[0] = RED_ALERT_GENERAL_ID;
		return result;
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public int compareTo(GarrisonTroops o) {//先驻防的先出手
		if (timer.getType().ordinal() == o.timer.getType().ordinal()){
			if (timer.getStart() > o.timer.getStart()){
				return 1;
			}else if (timer.getStart() == o.timer.getStart()){
				return 0;
			}else{
				return -1;
			}
		}else{
			if (timer.getType().ordinal() < o.timer.getType().ordinal()){
				//驻防先出战
				return -1;
			}else{
				return 1;
			}
		}
	}
	
	public boolean isDieAll(){
		for (int i = 0 ; i < troops.getArmys().size() ; i++){
			ArmyEntity army = troops.getArmys().get(i);
			if (army.getSane() > 0){
				return false;
			}
		}
		return true;
	}
	
	/***
	 * 固定的部队回到它来的地方
	 * @return
	 */
	public ExpediteTroops backToCome(){
		Role role = world.getRole(troops.getInfo().getUid());
		int targetPos = troops.getComePosition();
		TimerLastType expediteType = null;
		if (timer.getType() == TimerLastType.TIME_MAP_STATION){//驻扎部队撤退
			MapCity city = mapWorld.searchMapCity(troops.getInfo());
			targetPos = city.getPosition();
			expediteType = TimerLastType.TIME_ARMY_BACK;
		}
		long now  = TimeUtils.nowLong() / 1000;
		ExpediteTroops expedite = new ExpediteTroops();
		expedite.setId(id);
		TroopsData leader = expedite.addTroops(troops);
		leader.setLeader(true);
		expedite.computMoveSpeed(leader,role);
		if (expediteType == null){
			expediteType = mapWorld.getBackExpedteType(expedite);
		}
		expedite.setStartPosition(position);
		expedite.setTargetPosition(targetPos);
		long castTime = 0;
		if (needGoBackQuick){
			castTime = 10;
		}else{
			castTime = MapUtil.computeCastTime(position,targetPos,expedite.getSpeed());
		}
		MapCell targetCell = mapWorld.getMapCell(leader.getComePosition());
		TimerLast backTtimer = new TimerLast(now,castTime,expediteType);
		expedite.registTimer(backTtimer);
		targetCell.expedite(id);//终点格子加入回城行军
		MapCell startCell = mapWorld.getMapCell(position);
		startCell.expedite(id);//起点格子加入回城行军
		expedite.addSelf();//添加行军到列表
		expedite.addLook(role.getId());
		remove();
		MapCell comeCell = mapWorld.getMapCell(troops.getComePosition());
		if (timer.getType() == TimerLastType.TIME_MAP_STATION){
			//或者是驻扎部队(这个是在要塞或者兵营的格子)
			MapFortress fortress = mapWorld.searchObject(position);
			if (fortress != null && fortress.getInfo().getUid() == troops.getInfo().getUid()){
				fortress.changeGrid(this,expedite);
			}
		}else if (comeCell.getTypeKey() == MapFortress.class || comeCell.getTypeKey() == MapBarracks.class){
			//从要塞或者兵营来的部队
			MapFortress fortress = mapWorld.searchObject(comeCell);
			if (fortress != null && fortress.getInfo().getUid() == troops.getInfo().getUid()){
				fortress.changeGrid(this,expedite);
			}
		}
		if (timer.getType() == TimerLastType.TIME_MAP_MASS){
			//在大地图集结
			MapCity mc  = mapWorld.searchObject(position);
			if (mc != null && mc.getMass() != null){
				mc.getMass().removeGrid(this);
			}
			role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);//联盟战斗变化
		}
		if (startCell.getType() == MapCellType.MAP_CELL_GARRISON){
			//如果部队是驻防在据点上的
			MapGarrison mapGarrison = mapWorld.searchObject(startCell);
			if (mapGarrison != null){
				if (mapGarrison.getInfo().getUid() == troops.getInfo().getUid()){
					//如果我是当前据点的主人
					List<GarrisonTroops> garrisons = MapUtil.computeGarrisons(startCell);
					garrisons.remove(this);
					for (int i = 0 ; i < garrisons.size() ; i++){
						GarrisonTroops garrison = garrisons.get(i);
						garrison.remove();
					}
					boolean del = garrisons.size() == 0;
					if (!del){//把驻防点给后面来的最早的人
						del = true;
						Collections.sort(garrisons);
						for (int i = 0 ; i < garrisons.size() ; i++){
							GarrisonTroops garrison = garrisons.get(i);
							if (!garrison.isRemoving()){
								mapGarrison.getInfo().copy(garrison.getTroops().getInfo());
								del = false;
								break;
							}
						}
					}
					if (del){
						mapGarrison.remove();
					}
				}
			}
		}
		LogManager.mapLog(role, position, targetPos,expedite.getId(), EventName.withdrawalForce.getName());
		role.handleEvent(GameEvent.TROOPS_SEND);
		return expedite;
	}
	
	public ExpediteTroops tryToMove(Role role , int targetPos,TimerLastType expediteType){
		long now  = TimeUtils.nowLong() / 1000;
		ExpediteTroops expedite = new ExpediteTroops();
		expedite.setId(id);
		TroopsData leader = expedite.addTroops(troops);
		leader.comePosition = position;
		expedite.computMoveSpeed(leader,role);
		leader.setLeader(true);
		expedite.setStartPosition(position);
		expedite.setTargetPosition(targetPos);
		long castTime = MapUtil.computeCastTime(position,targetPos,expedite.getSpeed());
		if (expediteType == TimerLastType.TIME_EXPEDITE_STATION){
			castTime /= 2;
		}
		TimerLast timer = new TimerLast(now,castTime,expediteType);
		expedite.registTimer(timer);
		MapCell targetCell = mapWorld.getMapCell(leader.getComePosition());
		targetCell.expedite(id);//终点格子加入回城行军
		MapCell startCell = mapWorld.getMapCell(position);
		startCell.expedite(id);//起点格子加入回城行军
		expedite.addSelf();//添加行军到列表
		remove();
		return expedite;
	}
	
	public int getAliveNum() {
		return troops.getAliveNum();
	}

	@Override
	public void addSelf() {
		super.addSelf();
		MapCell cell = mapWorld.getMapCell(position);
		cell.occupyer(id);
	}

	public void goBackQuick() {
		needGoBackQuick = true;
		if (!timer.getType().isFlag()){
			timer.die();
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + position + "_" + id;
	}
}
