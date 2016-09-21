package com.joymeng.slg.domain.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.broadcast.BroadcastMessage;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.FightResutTemp;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.AbstractObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.SerializeEntity;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public abstract class MapObject extends AbstractObject implements SerializeEntity {
	
	protected long id;//地图上的对象唯一的标示
	
	protected int position;//在地图的位置
	
	protected long fight;//与我战斗行军部队的编号
	
	protected MapRoleInfo info = new MapRoleInfo();
	
	protected TimerLast safeTimer;//保护时间
	
	protected boolean destoryFlag = false;
	
	@Override
	public long getId() {
		return id;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public long getFight() {
		return fight;
	}

	public void setFight(long fight) {
		this.fight = fight;
	}

	public MapRoleInfo getInfo() {
		return info;
	}

	/**
	 * 获取占地体积
	 * @return
	 */
	public int getVolume(){
		return cellType().getVolume();
	}
	
	public abstract MapCellType cellType();

	public abstract void troopsArrive(ExpediteTroops expedite) throws Exception;//有行军部队抵达我这里
	
	public int getLevel() {
		return info.getLevel();
	}
	
	@Override
	public void registerAll() {
		
	}

	@Override
	public String table() {
		return null;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_POSITION};
	}
	
	@Override
	public void loadFromData(SqlData data) {
		position           = data.getInt(RED_ALERT_GENERAL_POSITION);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_POSITION,position);
	}
	
	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		List<GarrisonTroops> occupyers = MapUtil.computeGarrisons(position);
		out.putInt(occupyers.size());
		for (int i = 0 ; i <  occupyers.size(); i++){
			GarrisonTroops garrison =  occupyers.get(i);
			garrison.serialize(out);
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		setDeleteFlag(true);//如果存档过了就删除
		save();
		mapWorld.clearPosition(this);
		notifyClientDel();
	}
	
	public void _remove(){
		handleEvent(GameEvent.REMOVE_LIST);
		setDeleteFlag(true);//如果存档过了就删除
		save();
		notifyClientDel();
	}
	
	/**
	 * 通知所有在大地图的玩家我消失了
	 */
	public void notifyClientDel(){
		taskPool.broadcastThread.addMessage(new BroadcastMessage() {
			@Override
			public boolean send() {
				RespModuleSet rms = new RespModuleSet();
				AbstractClientModule module = new AbstractClientModule() {
					@Override
					public short getModuleType() {
						return NTC_DTCD_MAP_OBJ_DEL;
					}
				};
				module.add(id);//long 要删除的对象编号
				rms.addModule(module);
				try {
					List<Role> roles = world.getOnlineRoles();
					for (int i = 0 ; i <  roles.size(); i++){
						Role role =  roles.get(i);
						if (role.isInMap()){
							MessageSendUtil.sendModule(rms,role);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
		});
	}

	@Override
	public void tick(long now) {
		super.tick(now);
		if (safeTimer != null && safeTimer.over(now)){
			safeTimer.die();
			safeTimer = null;
		}
	}

	@Override
	public void _tick(long now) {
		
	}
	
	public List<Integer> reject(){
		return MapUtil.computeIndexs(position,getVolume());
	}
	
	public List<GarrisonTroops> getDefencers(){
		List<GarrisonTroops> troopses = MapUtil.computeGarrisons(position);
		for (int i = 0 ; i <  troopses.size();){
			GarrisonTroops troops =  troopses.get(i);
			if (troops.getTimer().getType().ordinal() == TimerLastType.TIME_MAP_MASS.ordinal()){
				troopses.remove(i);
				continue;
			}
			i++;
		}
		return troopses;
	}
	
	/**
	 * 这个对象和我有关系
	 * @param id
	 * @return
	 */
	public boolean relevance(long id) {
		List<GarrisonTroops> occupyers = getDefencers();
		for (int i = 0 ; i <  occupyers.size(); i++){
			GarrisonTroops occupyer =  occupyers.get(i);
			if (occupyer.getTroops().getInfo().getUid() == id){
				return true;
			}
		}
		return false;
	}
	
	public boolean isRivalry(long uid , ExpediteTroops expedite) {
		List<GarrisonTroops> occupyers = getDefencers();
		TroopsData leader = expedite.getLeader();
		for (int i = 0 ; i <  occupyers.size(); i++){
			GarrisonTroops occupyer =  occupyers.get(i);
			if (occupyer.getTroops().getInfo().getUid() == uid && !occupyer.getTroops().checkUnion(leader)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 被行军部队驻防
	 * @param expedite
	 */
	public void garrison(ExpediteTroops expedite){
		GarrisonTroops garrison = expedite.occuper(this);
		TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000,0,TimerLastType.TIME_MAP_GARRISON);
		garrison.registTimer(timer);//注册驻防时间
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
			Role role = world.getRole(expedite.getLeader().getInfo().getUid());
			NewLogManager.mapLog(role, "guard_field", (int) point.x, (int) point.y, newStr);
		} catch (Exception e) {
			GameLog.info("埋点错误");
		}
	}
	
	public boolean couldAttack(ExpediteTroops expedite){
		if (safeTimer != null && !safeTimer.over()){
			expedite.setNoBattleTip(I18nGreeting.MSG_MAP_NO_FIGHT_IN_SAFE_TIME);
			return false;
		}
		if (!_couldAttack(expedite)){
			return false;
		}
		TroopsData leader = expedite.getLeader();
		long aUnionId = leader.getInfo().getUnionId();
		long dUnionId = info.getUnionId();
		if (dUnionId != 0 && dUnionId == aUnionId){
			expedite.setNoBattleTip(I18nGreeting.MSG_MAP_NO_FIGHT_MEMBER);
			return false;
		}
		return true;
	}
	
	public  abstract boolean _couldAttack(ExpediteTroops expedite);
	
	public boolean attackDefenders(boolean send,List<GarrisonTroops> defenders , ExpediteTroops expedite,ReportTitleType title1,ReportTitleType title2) throws Exception{
		if (defenders.size() == 0){
			return true;
		}
		List<Long> attackerIds = new ArrayList<Long>();
		boolean isWin = false;
		int defenderIndex = 0;
		GarrisonTroops defender = defenders.get(defenderIndex);
		TroopsData attacker = expedite.getFightTroops(attackerIds);
		while (attacker != null){
			attackerIds.add(attacker.getInfo().getUid());
			BattleField battle = new BattleField();
			battle.add(attacker,Side.ATTACK);
			battle.add(defender.getTroops(),Side.DEFENSE);
			List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
			List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
			MapRoleInfo info = attacker.getInfo();
			Role aRole = world.getRole(info.getUid());
			if (aRole != null){
				aRole.effectFightTroops(troopses_a,troopses_d,info.getCityId());
			}
			info = defender.getTroops().getInfo();
			Role dRole = world.getRole(info.getUid());
			dRole.effectFightTroops(troopses_d,troopses_a,info.getCityId());
			BattleRecord record = battle.startFight();
			List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
			//防御方损耗
			Map<Integer,FightResutTemp> defenderResult = null;
			if (aRole != null){
				defenderResult = MapUtil.computeFightResult(troopses,defender.getTroops());
				MapUtil.triggerAE_kill_soldier(dRole,defenderResult.values());
			}else{//怪物攻城
				defenderResult = MapUtil.computeFightResult(troopses,defender.getTroops(),0);
				//MapUtil.triggerAE_kill_monster(dRole,attacker.getInfo().getLevel(),defenderResult.values());
				MapUtil.triggerAE_monster_act_role(dRole,isWin,defenderResult.values());
			}
			//攻击损耗
			troopses = battle.getTroopses(Side.ATTACK);
			Map<Integer,FightResutTemp> attackerResult = MapUtil.computeFightResult(troopses,attacker);
			if (aRole != null){
				MapUtil.triggerAE_kill_soldier(aRole,attackerResult.values());
			}
			if (defender.isDieAll()){
				if (title1 == ReportTitleType.TITLE_TYPE_A_MOVE){
					if (defender.getTimer().getType() == TimerLastType.TIME_CREATE){
						//迁城点的部队移除完成逻辑
						defender.getTimer().removeTimeOver((MapCityMove)this);
					}
				}
				defender.die();//如果有防御方的人兵死光了,就必须回家。
			}
			Side winerSide = battle.GetWinner();
			if (winerSide == null){//平局
				MapUtil.report(expedite,attacker,defender.getTroops(),position,false,battle,attackerResult,defenderResult,record,title1,title2,expedite.isMass());
				attacker = expedite.getFightTroops(attackerIds);
				if (attacker == null){//找不到可以出战的攻击者
					isWin = false;
					break;
				}
			}else{
				if (winerSide == Side.DEFENSE){
					MapUtil.report(expedite,attacker,defender.getTroops(),position,false,battle,attackerResult,defenderResult,record,title1,title2,expedite.isMass());
					attacker = expedite.getFightTroops(attackerIds);
					if (attacker == null){//找不到可以出战的攻击者
						isWin = false;
						break;
					}
				}else{//攻击方获胜
					defenderIndex++;
					MapUtil.report(expedite,attacker,defender.getTroops(),position,true,battle,attackerResult,defenderResult,record,title1,title2,expedite.isMass());
					if (defenderIndex == defenders.size()){//已经没有防守者了
						isWin = true;
						break;
					}
					defender = defenders.get(defenderIndex);
				}
			}
		}
		return isWin;
	}
	
	public boolean attackDefenders(List<GarrisonTroops> defenders , ExpediteTroops expedite,ReportTitleType title1,ReportTitleType title2) throws Exception{
		return attackDefenders(true,defenders,expedite,title1,title2);
	}

	/**
	 * 是否能驻防
	 * @param unionId
	 * @return
	 */
	public boolean couldGarrison(long unionId) {
		List<GarrisonTroops> garrisons = getDefencers();
		if (garrisons.size() > 0){
			if (garrisons.get(0).getTroops().checkUnion(unionId)){//可以驻防
				return true;
			}
		}
		return info.getUnionId() == unionId;
	}

	public boolean checkUnion(long unionId) {
		return info.getUnionId() == unionId;
	}
	
	/**
	 * 被核弹摧毁
	 * @param buffStr
	 * @return
	 */
	public boolean destroy(String buffStr){
		destoryFlag = true;
		List<GarrisonTroops> garrisons = getDefencers();
		for (int i = 0 ; i <  garrisons.size(); i++){
			GarrisonTroops garrison =  garrisons.get(i);
			garrison.die();
		}
		remove();
		return false;
	}
	
	public void logSpy(ExpediteTroops expedite){
		Role role = world.getRole(expedite.getLeader().getInfo().getUid());
		logSpy(role,expedite.getStartPosition(),position,expedite.getId());
	}
	
	public void logSpy(Role role,int start,int end,long expediteId){
		LogManager.mapLog(role,start,end,expediteId,EventName.InvestCompletion.getName());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + position;
	}

	public boolean isMyUnionMember(MapRoleInfo info2) {
		if (info2.getUnionId() == 0){
			return info.getUid() == info2.getUid();
		}else{
			return info.getUnionId() == info2.getUnionId();
		}
	}
	
	public void sendChange(){
		RespModuleSet rms = new RespModuleSet();
		AbstractClientModule module = new AbstractClientModule(){
			@Override
			public short getModuleType() {
				return NTC_DTCD_MAP_OBJ_CHANGE;
			}
		};
		module.add(position);
		rms.addModule(module);
		List<Role> roles = world.getOnlineRoles();
		for (int i = 0 ; i < roles.size() ; i++){
			Role role = roles.get(i);
			if (role.isInMap()){
				MessageSendUtil.sendModule(rms,role.getUserInfo());
			}
		}
	}
}
