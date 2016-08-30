package com.joymeng.slg.domain.map.impl.dynamic;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 集结部队
 * @author tanyong
 *
 */
public class MassTroops implements Instances{
	MapRoleInfo targetInfo = new MapRoleInfo();
	GridType[] grids = null;//部队站位数据
	TimerLast endTimer;//集结结束是时间
	int maxNum;//数量
	int position;//坐标
	
	public MassTroops(int volume,int maxNum){
		if (volume == 0){
			return;
		}
		grids = new GridType[volume];
		this.maxNum = maxNum;
	}

	public MapRoleInfo getTargetInfo() {
		return targetInfo;
	}

	public void setTargetInfo(MapRoleInfo targetInfo) {
		this.targetInfo = targetInfo;
	}
	
	public GridType[] getGrids() {
		return grids;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public synchronized boolean haveGrid(){
		if (grids == null){
			return false;
		}
		for (int i = 0 ; i < grids.length ; i++){
			if (grids[i] == null){
				return true;
			}
		}
		return false;
	}
	
	public synchronized void changePos(int oldPos , int newPos) {
		MapCell cell = mapWorld.getMapCell(oldPos);
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i];
			if (grid != null){
				if (grid.getType() == GarrisonTroops.class){
					GarrisonTroops garrison = grid.object();
					cell.removeOccupyer(garrison.getId());
					garrison.setPosition(newPos);
				}
			}
		}
	}
	
	public synchronized void addGrid(IObject obj){
		if (grids == null || obj == null){
			return;
		}
		for (int i = 0 ; i < grids.length ; i++){
			if (grids[i] == null){
				grids[i] = new GridType();
				grids[i].setId(obj.getId());
				grids[i].setType(obj.getClass());
				break;
			}
		}
	}
	
	public synchronized void removeGrid(IObject obj) {
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i] ;
			if (grids == null){
				continue;
			}
			if (grid.getId() == obj.getId() && grid.getType() == obj.getClass()){
				grids[i] = null;
				break;
			}
		}
	}
	
	public synchronized void changeGrid(IObject pre , IObject obj) {
		if (grids == null){
			return;
		}
		GridType target = null;
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i] ;
			if (grids == null){
				continue;
			}
			if (grid.getId() == pre.getId() && grid.getType() == pre.getClass()){
				target = grid;
				break;
			}
		}
		if (target != null){
			target.setType(obj.getClass());
			target.setId(obj.getId());
		}
	}

	public TimerLast getEndTimer() {
		return endTimer;
	}

	public void setEndTimer(TimerLast endTimer) {
		this.endTimer = endTimer;
	}

	public void registTimeOver(TimerLast timer,MapCity city){
		if (timer == null){
			return;
		}
		endTimer = timer;
		timer.registTimeOver(city);
		taskPool.mapTread.addObj(city,timer);
	}
	
	public void tick(){
		if (endTimer != null && endTimer.over()){
			endTimer.die();
		}
	}
	
	public String serialize(){
		String str1 = JsonUtil.ObjectToJsonString(targetInfo);
		String str2 = JsonUtil.ObjectToJsonString(grids);
		String str3 = JsonUtil.ObjectToJsonString(endTimer);
		return str1 + "|" + str2 + "|" +  str3 + "|" + maxNum;
	}
	
	public void deserialize(String str,final MapCity city){
		String[] ss = str.split("\\|");
		targetInfo = JsonUtil.JsonToObject(ss[0],MapRoleInfo.class);
		grids      = JsonUtil.JsonToObject(ss[1],GridType[].class);
		endTimer   = JsonUtil.JsonToObject(ss[2],TimerLast.class);
		if (endTimer != null){
			endTimer.registTimeOver(city);
			taskPool.mapTread.addObj(city,endTimer);
		}
		maxNum   = Integer.parseInt(ss[3]);
		position = city.getPosition();
		//需要判断行军中的部队
		List<ExpediteTroops> troopses = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < troopses.size() ; i++){
			ExpediteTroops troops = troopses.get(i);
			if (troops.isMass() && troops.getStartPosition() == city.getPosition()){
				troops.getTimer().registTimeOver(new TimerOver() {
					@Override
					public void finish() {
						city.massEnd();
					}
				});
			}
		}
	}
	
	public boolean isExpedite(){
		return endTimer == null;
	}
	
	/**
	 * 集结部队出发
	 * @param city
	 */
	public synchronized void go_off(final MapCity city){
		endTimer = null;
		MapCell startCell = mapWorld.getMapCell(city.getPosition());
		ExpediteTroops expedite = new ExpediteTroops();
		TroopsData leader = null;
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i];
			if (grid == null){
				continue;
			}
			if (grid.getType() == GarrisonTroops.class){
				GarrisonTroops garrison = grid.object();
				if (garrison != null){
					TroopsData troops = expedite.addTroops(garrison.getTroops());
					if (garrison.getTroops().getInfo().getUid() == city.getInfo().getUid()){
						troops.setLeader(true);
						expedite.setId(garrison.getId());
						leader = troops;
					}else{
						troops.setLeader(false);
					}
					startCell.removeOccupyer(garrison.getId());//起点格子移除驻防者
					garrison.remove();
				}
			}
		}
		if (expedite.getTeams().size() == 0){//集结出发异常了
			GameLog.error("集结出发的时候没有部队了");
			city.massEnd();
			return;
		}
		grids = new GridType[1];
		grids[0] = new GridType();
		grids[0].setId(expedite.getId());
		grids[0].setType(ExpediteTroops.class);
		Role role = world.getRole(leader.getInfo().getUid());
		expedite.computMoveSpeed(leader,role);
		expedite.setStartPosition(city.getPosition());
		expedite.setTargetPosition(targetInfo.getPosition());
		long castTime = MapUtil.computeCastTime(expedite.getStartPosition(),expedite.getTargetPosition(),expedite.getSpeed());
		MapCell targetCell = mapWorld.getMapCell(expedite.getTargetPosition());
		TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000,castTime,TimerLastType.TIME_EXPEDITE_FIGHT);
		expedite.registTimer(timer);
		timer.registTimeOver(new TimerOver() {
			@Override
			public void finish() {
				city.massEnd();
			}
		});//这个行军到了就删除这个集结数据
		targetCell.expedite(expedite.getId());//终点格子加入回城行军
		startCell.expedite(expedite.getId());//起点格子加入回城行军
		expedite.addSelf();//添加行军到列表
		expedite.setMass(true);
		role.handleEvent(GameEvent.TROOPS_SEND);
		role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);//联盟战斗变化
		for (int i = 0 ; i < expedite.getTeams().size() ; i++){
			TroopsData troops = expedite.getTeams().get(i);
			if (!troops.isLeader()){
				Role tr = world.getOnlineRole(troops.getInfo().getUid());
				if (tr != null){
					tr.handleEvent(GameEvent.TROOPS_SEND);
				}
			}
			expedite.addLook(troops.getInfo().getUid());
		}
	}

	public synchronized boolean checkNum(MapCity city,Role role,int addNum) {
		Role owner = world.getRole(city.getInfo().getUid());
		int max = owner.getRoleMaxMassNum(city.getInfo().getCityId());
		int num = 0;
		boolean hasMass = false;
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i];
			if (grid != null){
				if (grid.getType() == GarrisonTroops.class){
					GarrisonTroops troops = grid.object();
					if (troops.getTroops().getInfo().getUid() == role.getId()){
						hasMass = true;
						break;
					}
					num += troops.getAliveNum();
				}else{
					ExpediteTroops troops = grid.object();
					if (troops.getLeader().getInfo().getUid() == role.getId()){
						hasMass = true;
						break;
					}	
					num += troops.getAliveNum();
				}
			}
		}
		if (hasMass){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_CITY_MASS_JOIN);
			return false;
		}
		if (addNum + num > max){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_CITY_MASS_NO_SOLIDER_NUM,num,max,addNum);
			return false;
		}
		return true;
	}
	
	public synchronized void cancle(Role role){
		for (int i = 0 ; i < grids.length ; i++){
			GridType grid = grids[i];
			if (grid == null){
				continue;
			}
			if (grid.getType() == GarrisonTroops.class){
				GarrisonTroops troops = grid.object();
				if (troops.getTroops().getInfo().getUid() == role.getId()){
					//集结发起者
					troopsBack(troops,role);
				}else{
					troops.die();
				}
			}
		}
	}
	
	/***
	 * 集结发起者的部队士兵回城
	 * @param troops
	 * @param role
	 */
	public synchronized void troopsBack(GarrisonTroops troops,Role role){
		RespModuleSet rms = new RespModuleSet();
		troops.getTroops().armyBack(rms,role);
		troops.remove();
		role.handleEvent(GameEvent.TROOPS_SEND);
		MessageSendUtil.sendModule(rms,role);
	}
}
