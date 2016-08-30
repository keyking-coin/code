package com.joymeng.slg.domain.map.impl.still.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.CampDistributionData;
import com.joymeng.slg.domain.map.data.MapEntiy;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Worldbuilding;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
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
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.RespModuleSet;
/**
 * 军营
 * @author tanyong
 *
 */
public class MapBarracks extends MapFortress {
	String refreshId;//刷新编号
	String buildkey;//军营建筑等级编号
	List<TroopsData> npcs = new ArrayList<TroopsData>();//部队
	TimerLast cureTimer;//npc自愈时间
	
	public MapBarracks(){
		name = getData().getBuildingName();
		level = 5;
	}
	
	public Worldbuilding getData(){
		Worldbuilding wb = dataManager.serach(Worldbuilding.class, BuildName.MAP_BARRACKS.getKey());
		return wb;
	}
	
	@Override
	public Worldbuildinglevel getDataLevel(){
		Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class, BuildName.MAP_BARRACKS.getKey() + "1");
		return wbl;
	}
	
	public String getBuildkey() {
		return buildkey;
	}

	public void setBuildkey(String buildkey) {
		this.buildkey = buildkey;
	}

	public String getRefreshId() {
		return refreshId;
	}

	public List<TroopsData> getNpcs() {
		return npcs;
	}

	public void setNpcs(List<TroopsData> npcs) {
		this.npcs = npcs;
	}

	public void setRefreshId(String refreshId) {
		this.refreshId = refreshId;
	}
	
	public void initNpc(){
		CampDistributionData data = dataManager.serach(CampDistributionData.class,refreshId);
		initNpc(data);
	}
	
	public void initNpc(CampDistributionData data){
		if (data == null){
			return;
		}
		if (cureTimer != null){
			setMapThreadFlag(true);
		}
		cureTimer = null;
		npcs.clear();
		List<MapEntiy> lis = data.getNeedDistribution();
		for (int i = 0 ; i < lis.size() ; i++){
			MapEntiy entiy = lis.get(i);
			String monsterKey = entiy.getpName();
			int count = Integer.parseInt(entiy.getpValue());
			while (count > 0){
				Monster monster = dataManager.serach(Monster.class,monsterKey);
				TroopsData troops = TroopsData.create(monster,position);
				npcs.add(troops);
				count--;
			}
		}
	}
	
	public void registCure(TimerLast timer){
		cureTimer = timer;
		timer.registTimeOver(this);
		taskPool.mapTread.addObj(this,timer);
	}
	
	public int getAliveNpcs(){
		int count = 0;
		for (int i = 0 ; i < npcs.size() ; i++){
			TroopsData troops = npcs.get(i);
			if (troops.couldFight()){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putInt(getAliveNpcs());
		out.putInt(npcs.size());
		if (cureTimer != null){
			out.putInt(1);
			cureTimer.serialize(out);
		}else{
			out.putInt(0);
		}
		super.serialize(out);
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_BARRACKS;
	}
	
	private TroopsData  getDefenceNpcs(){
		for(TroopsData troops : npcs){
			if (troops.couldFight()){
				return troops;
			}
		}
		return null;
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				TroopsData defender = getDefenceNpcs();
				List<Long> attackerIds = new ArrayList<Long>();
				boolean isWin = true;
				long now = TimeUtils.nowLong() / 1000;
				Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildkey);
				if (defender != null){//先打npc
					TroopsData attacker = expedite.getFightTroops(attackerIds);
					while (attacker != null){
						attackerIds.add(attacker.getInfo().getUid());
						BattleField battle = new BattleField();
						battle.add(defender,Side.DEFENSE);
						battle.add(attacker,Side.ATTACK);
						List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
						List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
						MapRoleInfo info = attacker.getInfo();
						Role role = world.getRole(info.getUid());
						role.effectFightTroops(troopses_a,troopses_d,info.getCityId());
						BattleRecord record = battle.startFight();
						List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
						Map<Integer,FightResutTemp> preDefender =MapUtil.computeFightResult(troopses,defender);
						troopses = battle.getTroopses(Side.ATTACK);
						Map<Integer,FightResutTemp> preAttacker =MapUtil.computeFightResult(troopses,attacker);
						//MapUtil.triggerAE_kill_monster(role,defender.getInfo().getLevel(),preAttacker.values());
						Side winerSide = battle.GetWinner();
						try {
							PointVector point = MapUtil.getPointVector(position);
							StringBuffer sb = new StringBuffer();
							List<ArmyEntity> armys = attacker.getArmys();
							for (int j = 0; j < armys.size(); j++) {
								ArmyEntity entry = armys.get(j);
								sb.append(entry.getKey());
								sb.append(GameLog.SPLIT_CHAR);
								sb.append(entry.getSane());
								sb.append(GameLog.SPLIT_CHAR);
							}
							String newStr = sb.toString().substring(0,sb.toString().length() - 1);
							NewLogManager.mapLog(role, "attack_camp",(int)point.x,(int)point.y,newStr);
						} catch (Exception e) {
							GameLog.info("埋点错误");
						}

						if (winerSide == null){//本次战斗平局
							MapUtil.report(expedite,attacker,defender,position,false,battle,preAttacker,preDefender,record,ReportTitleType.TITLE_TYPE_MONSTER,null,expedite.isMass());
							attacker = expedite.getFightTroops(attackerIds);
							if (attacker == null){//找不到可以出战的攻击者
								isWin = false;
								break;
							}
						}else{
							if (winerSide.ordinal() == Side.DEFENSE.ordinal()){
								MapUtil.report(expedite,attacker,defender,position,false,battle,preAttacker,preDefender,record,ReportTitleType.TITLE_TYPE_MONSTER,null,expedite.isMass());
								attacker = expedite.getFightTroops(attackerIds);
								if (attacker == null){//找不到可以出战的攻击者
									isWin = false;
									break;
								}
							}else{//攻击方获胜
								MapUtil.report(expedite,attacker,defender,position,true,battle,preAttacker,preDefender,record,ReportTitleType.TITLE_TYPE_MONSTER,null,expedite.isMass());
								defender = getDefenceNpcs();
								if (defender == null){
									isWin = true;
									break;
								}
							}
						}
					}
					if (cureTimer == null){//npc被攻击;
						registCure(new TimerLast(now,wbl.getTime(),TimerLastType.TIME_NPC_TROOPS_CURE));
					}
				}
				if (info.getUid() > 0){//npc打完了，继续打玩家
					if (isWin){
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s barracks of monster successful at " + position);
						List<GarrisonTroops> defenders = getDefencers();//防守者
						isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_BARRRACKS,ReportTitleType.TITLE_TYPE_D_BARRRACKS);
						if (isWin){
							GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s barracks successful and occupy it at " + position);
						}else{
							GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s barracks fail at " + position);
						}
					}else{
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s barracks of monster fail at " + position);
					}
				}else{
					if (isWin){
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack barracks's monster successful and occupy it at " + position);
					}else{
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack barracks's monster fail at " + position);
					}
				}
				if (isWin){//占领军营
					GarrisonTroops occuper = expedite.occuper(this);
					addGrid(occuper);
					TimerLast timer = new TimerLast(now,0,TimerLastType.TIME_MAP_STATION);
					occuper.registTimer(timer);//注册驻扎时间
					//设置保护时间
					safeTimer = new TimerLast(now,wbl.getFreetime(),TimerLastType.TIME_MAP_OBJ_SAFE);
					info.copy(occuper.getTroops().getInfo());
					initNpc();
				}else{
					expedite.goBackToCome();
				}	
				//记录战斗次数
				Role role = world.getRole(expedite.getLeader().getInfo().getUid());
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_ATK_WIN, isWin);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_ARMY_BACK_FORTRESS){
				if (info.getUid() > 0 && info.getUid() == expedite.getLeader().getInfo().getUid()){
					super.troopsArrive(expedite);
				}else{//生成新的回家
					expedite.goBackCity();
				}
			} else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY) {
				// 要塞侦查结果报告
				MapUtil.spyResport(SpyType.SPY_TYPE_FORTRESS, expedite, this);
				logSpy(expedite);
			} else {
				super.troopsArrive(expedite);
			}
		}
	}
	
	@Override
	public void _tick(long now){
		super._tick(now);
		if (cureTimer != null && cureTimer.over(now)){
			cureTimer.die();
		}
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_BARRACKS;
	}
	
	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		buildkey   = data.getString(RED_ALERT_BARRACKS_BUILDKEY);
		refreshId  = data.getString(RED_ALERT_RESOURCES_REFRESHID);
		//npc初始化
		initNpc();
	}

	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		data.put(RED_ALERT_BARRACKS_BUILDKEY,buildkey);
		data.put(RED_ALERT_RESOURCES_REFRESHID,refreshId);
	}
	
	@Override
	public void save() {
		if (info.getUid() == 0){
			setDeleteFlag(true);
		}
		super.save();
	}

	@Override
	public void finish() {
		if (buildTimer != null){
			super.finish();
		}else{
			sendChange();
		}
		if (cureTimer != null && fight == 0){//npc复活
			//没有处于战斗状态，并且没有被别人占领
			initNpc();
		}
	}

	@Override
	public boolean destroy(String buffStr) {
		List<GarrisonTroops> garrisons = getDefencers();
		for (int i = 0 ; i < garrisons.size() ; i++){
			GarrisonTroops garrison = garrisons.get(i);
			garrison.die();
		}
		Role role = world.getOnlineRole(info.getUid());
		if (role != null){
			role.sendViews(new RespModuleSet(),true);
		}
		initNpc();
		setDeleteFlag(true);
		save();
		info.clear();
		return true;
	}
}
