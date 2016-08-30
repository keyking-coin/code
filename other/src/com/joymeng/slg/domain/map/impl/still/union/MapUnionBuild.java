package com.joymeng.slg.domain.map.impl.still.union;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Npccity;
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
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public abstract class MapUnionBuild extends MapObject implements TimerOver{
	protected TimerLast buildTimer;//建筑倒计时
	protected byte level = 1;//等级
	protected String buildKey;
	protected String cityKey;
	protected List<TroopsData> defenders = new ArrayList<TroopsData>();//npc守卫
	TimerLast monsterRebirthTimer;//守卫重生时间
	MapUnionCity unionCity = null;
	
	public void init(){
		//初始化怪物
		Worldbuildinglevel wbl = getLevelData();
		if (wbl != null){
			List<String> monsters = wbl.getMonster();
			for (int i  = 0 ; i < monsters.size() ; i++){
				String key = monsters.get(i);
				String[] ks = key.split(":");
				Monster monster = dataManager.serach(Monster.class,ks[0]);
				int count = Integer.parseInt(ks[1]);
				while (count > 0){
					TroopsData troops = TroopsData.create(monster,position);
					defenders.add(troops);
					count--;
				}
			}
		}
		_init();
	}
	
	public abstract void _init();
	
	public void die(){
		remove();
	}
	
	public void registTimer(TimerLast timer){
		buildTimer = timer;
		buildTimer.registTimeOver(this);
		taskPool.mapTread.addObj(this,buildTimer);
	}
	
	public long getUnionId() {
		return info.getUnionId();
	}
	
	public void setUnionId(long unionId) {
		info.setUnionId(unionId);
	}

	public String getBuildKey() {
		return buildKey;
	}

	public void setBuildKey(String buildKey) {
		this.buildKey = buildKey;
	}

	@Override
	public int getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	
	public String getCityKey() {
		return cityKey;
	}

	public void setCityKey(String cityKey) {
		this.cityKey = cityKey;
	}

	public Worldbuildinglevel getLevelData(){
		Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildKey + level);
		return wbl;
	}
	
	public Worldbuildinglevel getLevelData(int level){
		Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildKey + level);
		return wbl;
	}
	
	public Worldbuilding getData(){
		Worldbuilding wb = dataManager.serach(Worldbuilding.class,buildKey);
		return wb;
	}
	
	public String getName(){
		return getData().getBuildingName();
	}
	
	public String getCityName(){
		Npccity city = dataManager.serach(Npccity.class,cityKey);
		return city.getCityname();
	}
	
	public byte getState(){
		if (buildTimer != null){
			if (buildTimer.getType().ordinal() == TimerLastType.TIME_CREATE.ordinal()){
				return 1;//正在建造
			}else if (buildTimer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal()){
				return 2;//正在升级
			}else if (buildTimer.getType().ordinal() == TimerLastType.TIME_REMOVE.ordinal()){
				return 3;//正在拆除
			}
		}
		return 0;
	}
	
	public TimerLast getBuildTimer() {
		return buildTimer;
	}

	@Override
	public void _tick(long now) {
		if (buildTimer != null){
			if (buildTimer.over(now)){
				buildTimer.die();
			}
		}
		if (monsterRebirthTimer != null){
			if (monsterRebirthTimer.over(now)){
				monsterRebirthTimer.die();
			}
		}
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_UNION_BUILD;
	}

	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		long unionId  = data.getLong(RED_ALERT_GENERAL_UNION_ID);
		info.setUnionId(unionId);
		level    = data.getByte(RED_ALERT_GENERAL_LEVEL);
		buildKey = data.getString(RED_ALERT_UNION_BUILD_KEY);
		cityKey  = data.getString(RED_ALERT_UNION_CITY_KEY);
		String str = data.getString(RED_ALERT_GENERAL_BUILD_TIMER);
		buildTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		init();
		str = data.getString(RED_ALERT_UNION_BUILD_SUNS);
		deserializeSelf(str);
	}


	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		data.put(RED_ALERT_GENERAL_UNION_ID,info.getUnionId());
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_UNION_BUILD_KEY,buildKey);
		data.put(RED_ALERT_UNION_CITY_KEY,cityKey);
		String str = getClass().getName();
		data.put(RED_ALERT_GENERAL_TYPE,str);
		str = serializeSelf();
		data.put(RED_ALERT_UNION_BUILD_SUNS,str);
		str = JsonUtil.ObjectToJsonString(buildTimer);
		data.put(RED_ALERT_GENERAL_BUILD_TIMER,str);
	}
	
	public abstract String serializeSelf();
	
	public abstract void deserializeSelf(String str);
	
	public abstract void _finish(int type);
	
	public int getAliveArmy(){
		int count = 0;
		for (int i = 0 ; i < defenders.size() ; i++){
			TroopsData troops = defenders.get(i);
			if (troops.couldFight()){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(info.getUnionId());//long 联盟编号
		unionManager.serializeSimple(info.getUnionId(),out);
		out.put(level);//byte 建筑等级
		out.putPrefixedString(buildKey,JoyBuffer.STRING_TYPE_SHORT);//string 固化编号
		out.putPrefixedString(cityKey,JoyBuffer.STRING_TYPE_SHORT);//string 固化编号
		out.putInt(getAliveArmy());//守卫当前数据
		out.putInt(defenders.size());//守卫上限
		if (monsterRebirthTimer != null){//守卫重生时间
			 out.putInt(1);
			 monsterRebirthTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
		if (buildTimer != null){
			 out.putInt(1);
			 buildTimer.serialize(out);
		}else{
			 out.putInt(0);
		}
		super.serialize(out);
	}
	
	public void delFromCity(){
		List<MapUnionCity> citys = mapWorld.searchUnionCity(info.getUnionId());
		for (int j = 0 ; j < citys.size() ; j++){
			MapUnionCity city = citys.get(j);
			if (city.removeBuild(position)){
				break;
			}
		}
	}
	
	public void remove(boolean flag) {
		if (flag){
			remove();
		}else{
			super.remove();
		}
	}
	
	@Override
	public void remove() {
		super.remove();
		delFromCity();
	}

	@Override
	public void finish() {
		if (buildTimer != null){
			if (buildTimer.getType().ordinal() == TimerLastType.TIME_CREATE.ordinal()){//创建
				_finish(1);
			}else if (buildTimer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal()){//升级
				_finish(2);
				level ++;
			}else if (buildTimer.getType().ordinal() == TimerLastType.TIME_REMOVE.ordinal()){//拆除
				_finish(3);
				remove();
			}
			//任务事件
			UnionBody union = unionManager.search(info.getUnionId());
			if(union != null){
				for (int i = 0 ; i < union.getMembers().size() ; i++){
					UnionMember mem = union.getMembers().get(i);
					Role role = world.getObject(Role.class, mem.getUid());
					if (role != null){
						role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.COND_ALLI_BUILD, cityKey, buildKey, level);
					}
				}
				union.sendMeToAllMembers(0);
			}
			sendChange();
		}
	}
	
	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		if (info.getUnionId() != 0 && info.getUnionId() == expedite.getLeader().getInfo().getUnionId()){
			expedite.setNoBattleTip(I18nGreeting.MSG_MAP_NO_FIGHT_MEMBER);
			return false;
		}
		return true;
	}
	
	public void levelUp(UnionBody union) {
		Worldbuildinglevel wbl = getLevelData();
		long now = TimeUtils.nowLong() / 1000;
		long last = union.getGmBuildLevelUpTime() > 0 ? union.getGmBuildLevelUpTime() : wbl.getTime();
		TimerLast timer = new TimerLast(now,last,TimerLastType.TIME_LEVEL_UP);
		registTimer(timer);
	}
	
	public void cancleRemove() {
		if (getState() == 3){
			_finish(4);
		}
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite){
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){
				//侦查NPC城的建筑
				MapUtil.spyResport(SpyType.SPY_TYPE_UNION_BUILD, expedite, this);
				logSpy(expedite);
			}else if(expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				StringBuffer sb = new StringBuffer();
				List<TroopsData> teams  = expedite.getTeams();
				for(int i=0;i<teams.size();i++){
					TroopsData troop = teams.get(i);
					List<ArmyEntity> armys = troop.getArmys();
					for(int j=0;j<armys.size();j++){
						ArmyEntity entry= armys.get(j);
						sb.append(entry.getKey());
						sb.append(GameLog.SPLIT_CHAR);
						sb.append(entry.getSane());
						sb.append(GameLog.SPLIT_CHAR);
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				Role rl = world.getRole(expedite.getLeader().getInfo().getUid());
				PointVector vextor = MapUtil.getPointVector(expedite.getTargetPosition());
				NewLogManager.unionLog(rl, "attack_alliance_building",(int)vextor.x,(int)vextor.y,newStr);
				if (info.getUnionId() == expedite.getLeader().getInfo().getUnionId()){
					expedite.goBackToCome();
				}else{
					MapUnionCity city = getCity();
					if (city != null){
						long now = TimeUtils.nowLong() / 1000;
						city.notifyAttackIng(now,expedite.getLeader().getInfo().getUnionId());
					}
					boolean isWin = false;
					List<GarrisonTroops> defenders = getDefencers();//防守者
					if (MapUtil.couldFight(defenders)){//先打玩家
						isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_U_BUILD,ReportTitleType.TITLE_TYPE_D_U_BUILD);
					}else{
						isWin = true;
					}
					TroopsData defender = getDefenceNpcs();//获取可以参战的npc
					if (isWin && defender != null){//再打npc
						do{
							isWin = false;
							BattleField battle = new BattleField();
							battle.add(expedite);//攻击方进战场
							battle.add(defender,Side.DEFENSE);//防御方进战场
							List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
							List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
							MapRoleInfo info = expedite.getLeader().getInfo();
							Role role = world.getRole(info.getUid());
							role.effectFightTroops(troopses_a,troopses_d,info.getCityId());
							BattleRecord record = battle.startFight();
							//计算防守方的损耗
							List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
							Map<Integer,FightResutTemp> defenderResult = MapUtil.computeFightResult(troopses,defender);
							//计算攻击方战斗数据
							troopses = battle.getTroopses(Side.ATTACK);
							Map<Integer,FightResutTemp> attackerResults = new HashMap<Integer,FightResutTemp>();
							for (int i = 0 ; i < expedite.getTeams().size() ; i++){
								TroopsData attacker = expedite.getTeams().get(i);
								Map<Integer,FightResutTemp> temp = MapUtil.computeFightResult(troopses,attacker);
								attackerResults.putAll(temp);
							}
							Side winerSide = battle.GetWinner();
							String header = expedite.getHeader();
							if (winerSide != null && winerSide == Side.ATTACK){//攻击胜利
								MapUtil.report(expedite,defender,position,true,battle,attackerResults,defenderResult,record,ReportTitleType.TITLE_TYPE_JUST_FIGHT,null);
								GameLog.info(header + " attack unionBuild_" + buildKey + "'s monster successful at " + position);
								if (getDefenceNpcs() == null){
									isWin = true;
								}
								break;
							}else{
								MapUtil.report(expedite,defender,position,false,battle,attackerResults,defenderResult,record,ReportTitleType.TITLE_TYPE_JUST_FIGHT,null);
								GameLog.info(header + " attack unionBuild_" + buildKey + "'s monster fail at " + position);
								isWin = false;
								break;
							}
						}while(true);
						if (monsterRebirthTimer == null){//npc被攻击;
							Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class,buildKey+ level);
							monsterRebirthTimer = new TimerLast(TimeUtils.nowLong()/1000,wbl.getMonsterCD(),TimerLastType.TIME_NPC_TROOPS_CURE);
							monsterRebirthTimer.registTimeOver(new MonsterRebirth());
							taskPool.mapTread.addObj(this,monsterRebirthTimer);
						}
					}
					if (isWin){//联盟建筑被消灭
						die();
						NewLogManager.unionLog(rl, "alliance_building_destroyed",(int)vextor.x,(int)vextor.y);
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack unionBuild_" + buildKey + " successful and destroy it at " + position);
					}else{
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack unionBuild_" + buildKey + " fail at " + position);
					}
					expedite.goBackToCome();
				}
			}else if(expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
				if (info.getUnionId() == expedite.getLeader().getInfo().getUnionId()){//驻防
					garrison(expedite);
				}else{
					expedite.goBackToCome();
				}
			}
		}
	}

	public void range(int[] result) {
		
	}

	@Override
	public boolean checkUnion(long unionId) {
		return info.getUnionId() == unionId;
	}
	
	private TroopsData getDefenceNpcs(){
		for(TroopsData troops : defenders){
			if (troops.couldFight()){
				return troops;
			}
		}
		return null;
	}
	
	public MapUnionCity getCity(){
		if (unionCity == null){
			List<MapUnionCity> citys = mapWorld.searchUnionCity(info.getUnionId());
			for (int i = 0 ; i < citys.size() ; i++){
				MapUnionCity city = citys.get(i);
				if (city.getKey().equals(cityKey)){
					unionCity = city;
					break;
				}
			}
		}
		return unionCity;
	}
	
	public void active(){
		
	}
	
	public void lock(){
		
	}
	
	class MonsterRebirth implements TimerOver,Instances{
		@Override
		public void finish() {
			for (int i = 0 ; i < defenders.size(); i++){
				TroopsData troops = defenders.get(i);
				troops.resetArmys();
			}
			monsterRebirthTimer = null;
			sendChange();
		}
	}
}
