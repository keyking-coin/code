package com.joymeng.slg.domain.map.impl.still.union;

import java.util.ArrayList;
import java.util.Collections;
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
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.NPCDistributionData;
import com.joymeng.slg.domain.map.data.Npccity;
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
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionRecords;
import com.joymeng.slg.world.GameConfig;

public class MapUnionCity extends MapObject {
	int level;//城市等级
	String key;//固化表编号
	String name;//名称
	byte state;//0npc占领,1占领单不是领地,2被联盟占领切是领地
	List<TroopsData> normalArmys = new ArrayList<TroopsData>();//普通部队
	List<TroopsData> liteArmys   = new ArrayList<TroopsData>();//精英部队
	TimerLast monsterRebirthTimer;//守卫重生时间
	TimerLast giveUpTimer;//放弃倒计时
	Map<Long,Integer> damages = new HashMap<Long, Integer>();//伤害列表
	List<Integer> builds = new ArrayList<Integer>();//关联的城市建筑
	Conquerer conquerer = null;//征服者
	Map<Long,Long> notifyTimes = new HashMap<Long,Long>();//公告通知事件
	
	public void init(NPCDistributionData data, Npccity npc) {
		position = data.getCenterY() * GameConfig.MAP_WIDTH + data.getCenterX();
		level    = npc.getLevel();
		key      = npc.getId();
		name     = npc.getCityname();
		info.setName(name);
		initMonster(npc);
	}
	
	public void initMonster(Npccity npc){
		//精英守卫
		List<String> monsterStr = npc.getLitemonster();
		for (int j = 0 ; j < monsterStr.size() ; j++){
			String str = monsterStr.get(j);
			String[] ss = str.split(":");
			int count = Integer.parseInt(ss[1]);
			while (count > 0){
				Monster monster = dataManager.serach(Monster.class,ss[0]);
				TroopsData troops = TroopsData.create(monster,position);
				troops.isLite = true;
				liteArmys.add(troops);
				count--;
			}
		}
		monsterStr = npc.getCitymonster();
		for (int j = 0 ; j < monsterStr.size() ; j++){
			String str = monsterStr.get(j);
			String[] ss = str.split(":");
			int count = Integer.parseInt(ss[1]);
			while (count > 0){
				Monster monster = dataManager.serach(Monster.class,ss[0]);
				TroopsData troops = TroopsData.create(monster,position);
				troops.isLite = false;
				troops.getInfo().setPosition(position);
				normalArmys.add(troops);
				count--;
			}
		}
	}
	
	public long getUnionId() {
		return info.getUnionId();
	}

	public void setUnionId(long unionId) {
		info.setUnionId(unionId);
	}
	
	@Override
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public List<TroopsData> getNormalArmys() {
		return normalArmys;
	}

	public void setNormalArmys(List<TroopsData> normalArmys) {
		this.normalArmys = normalArmys;
	}

	public List<TroopsData> getLiteArmys() {
		return liteArmys;
	}

	public void setLiteArmys(List<TroopsData> liteArmys) {
		this.liteArmys = liteArmys;
	}
	
	public List<Integer> getBuilds() {
		return builds;
	}
	
	@Override
	public void _tick(long now) {
		if (monsterRebirthTimer != null && monsterRebirthTimer.over(now)){
			monsterRebirthTimer.die();
		}
		if (giveUpTimer != null && giveUpTimer.over(now)){
			giveUpTimer.die();
		}
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_CITY;
	}
	
	private TroopsData  getDefenceNpcs(){
		for(TroopsData troops : liteArmys){
			if (troops.couldFight()){
				return troops;
			}
		}
		for(TroopsData troops : normalArmys){
			if (troops.couldFight()){
				return troops;
			}
		}
		return null;
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
			//驻防部队
			if (couldGarrison(expedite.getLeader().getInfo().getUnionId())){
				garrison(expedite);
			}else{
				expedite.goBackToCome();
			}
		}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){
			//侦查NPC城
			MapUtil.spyResport(SpyType.SPY_TYPE_NPC, expedite,this);
			logSpy(expedite);
		}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){//攻打
			TroopsData defender = getDefenceNpcs();
			expedite.isWin = defender == null;
			TroopsData leader = expedite.getLeader();
			long attackUnionId = leader.getInfo().getUnionId();
			if (attackUnionId == 0){
				expedite.setNoBattleTip(I18nGreeting.MSG_ATTACK_CITY_NO_UNION);
				expedite.goBackNoFight();
			}
			long now = TimeUtils.nowLong() / 1000;
			notifyAttackIng(now,attackUnionId);
			Role rl = world.getRole(leader.getInfo().getUid());
			if (defender != null){//先打npc
				do {
					if (expedite.isMass()){//集结逻辑
						BattleField battle = new BattleField();
						for (int i = 0 ; i < expedite.getTeams().size() ; i++){
							TroopsData attacker = expedite.getTeams().get(i);
							battle.add(attacker,Side.ATTACK);
						}
						battle.add(defender,Side.DEFENSE);
						List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
						List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
						rl.effectFightTroops(troopses_a,troopses_d,leader.getInfo().getCityId());
						BattleRecord record = battle.startFight();
						//计算防守方的损耗
						List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
						Map<Integer,FightResutTemp> defenderResult = MapUtil.computeFightResult(troopses,defender);
						//计算攻击方的损耗
						troopses = battle.getTroopses(Side.ATTACK);
						Map<Integer,FightResutTemp> attackersResult = new HashMap<Integer,FightResutTemp>();
						for (int i = 0 ; i < expedite.getTeams().size() ; i++){
							TroopsData attacker = expedite.getTeams().get(i);
							Map<Integer,FightResutTemp> temp = MapUtil.computeFightResult(troopses,attacker);
							attackersResult.putAll(temp);
						}
						Side winnerSide = battle.GetWinner();
						if (winnerSide != null && winnerSide == Side.ATTACK){
							//攻击方获胜
							TroopsData nowDefender = defender;
							defender = getNext(expedite,defender);
							MapUtil.report(expedite,true,nowDefender,position,attackersResult,defenderResult,battle,record,damages);
						}else{
							expedite.isWin = false;
							MapUtil.report(expedite,false,defender,position,attackersResult,defenderResult,battle,record,damages);
							String header = expedite.getHeader();
							if (defender.isLite){
								GameLog.info(header + " attack " + name + "'s liteMonster fail at " + position);
							}else{
								GameLog.info(header + " attack " + name + "'s normalMonster fail at " + position);
							}
							break;
						}
					}else{
						BattleField battle = new BattleField();
						battle.add(defender,Side.DEFENSE);
						battle.add(leader,Side.ATTACK);
						List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
						List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
						rl.effectFightTroops(troopses_a,troopses_d,leader.getInfo().getCityId());
						BattleRecord record = battle.startFight();
						//计算防守方的损耗
						int preNum = defender.getAliveNum();
						List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
						Map<Integer,FightResutTemp> defenderResult = MapUtil.computeFightResult(troopses,defender);
						int nowNum = defender.getAliveNum();
						if (preNum - nowNum > 0){
							long attackId = leader.getInfo().getUid();
							int damage = preNum - nowNum;
							if (damages.containsKey(attackId)){
								damage += damages.get(attackId).intValue();
							}
							damages.put(attackId,damage);//累计伤害
						}
						troopses = battle.getTroopses(Side.ATTACK);
						Map<Integer,FightResutTemp> attackerResult = MapUtil.computeFightResult(troopses,leader);
						Side winerSide = battle.GetWinner();
						if (winerSide != null && winerSide.ordinal() == Side.ATTACK.ordinal()){
							//攻击方获胜
							TroopsData nowDefender = defender;
							defender = getNext(expedite,defender);
							ReportTitleType title = defender == null ? ReportTitleType.TITLE_TYPE_A_U_CITY : ReportTitleType.TITLE_TYPE_JUST_FIGHT;
							MapUtil.report(expedite,leader,nowDefender,position,true,battle,attackerResult,defenderResult,record,title,null,expedite.isMass());
						}else{
							expedite.isWin = false;
							MapUtil.report(expedite,leader,defender,position,false,battle,attackerResult,defenderResult,record,ReportTitleType.TITLE_TYPE_JUST_FIGHT,null,expedite.isMass());
							String header = expedite.getHeader();
							if (defender.isLite){
								GameLog.info(header + " attack " + name + "'s liteMonster fail at " + position);
							}else{
								GameLog.info(header + " attack " + name + "'s normalMonster fail at " + position);
							}
							break;
						}
					}
				}while(defender != null);
				if (monsterRebirthTimer == null){//npc被攻击;
					Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class, BuildName.MAP_UNION_CITY_NAME.getKey() + level);
					monsterRebirthTimer = new TimerLast(now,wbl.getTime(),TimerLastType.TIME_NPC_TROOPS_CURE);
					monsterRebirthTimer.registTimeOver(new MonsterRebirth());
					taskPool.mapTread.addObj(this,monsterRebirthTimer);
				}
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
					NewLogManager.mapLog(rl,"attack_city",(int) point.x,(int) point.y,newStr);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
			}
			List<GarrisonTroops> defenders = getDefencers();//防守者
			if (expedite.isWin && defenders.size() > 0){//npc打完了，继续打玩家
				expedite.isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_U_CITY,ReportTitleType.TITLE_TYPE_D_U_CITY);
			}
			createUnionBattleRecord(expedite.isWin,expedite.isMass(),expedite.getLeader().getInfo(),info);	
			if (expedite.isWin || GameConfig.ATTACK_CITY_MUST_WIN){//可以占领城市
				UnionBody preUnion = unionManager.search(info.getUnionId());//上一个联盟
				UnionBody union = unionManager.search(attackUnionId);
				if (union != null){
					state = 1;
					info.setUnionId(attackUnionId);
					if (conquerer == null){
						conquerer = new Conquerer(union,damages);
					}
					union.notifyAttackCitySucc(this);
					union.getUsInfo().updateOcpCitys(level);
					notifyAttackSucc(union);
					//奖励下发
					union.computeReword(getData(),damages);
					Worldbuildinglevel wbl = dataManager.serach(Worldbuildinglevel.class, BuildName.MAP_UNION_CITY_NAME.getKey() + level);
					safeTimer = new TimerLast(now, wbl.getFreetime(), TimerLastType.TIME_MAP_OBJ_SAFE);
					giveUpTimer = null;
					destoryBuilds();//销毁上个占领者的所有建筑
					if (monsterRebirthTimer != null){
						monsterRebirthTimer.die();
					}
					GameLog.info("union <" + unionManager.search(info.getUnionId()).getName() + "> occupy < " + name + ">");
					EvntManager.getInstance().Notify("unionOccupyCity", String.format("%d#%d",info.getUnionId(), level));
				}
				if (preUnion != null){
					preUnion.sendMeToAllMembers(0);
				}
			}
			expedite.goBackToCome();
		}
	}
	
	private TroopsData getNext(ExpediteTroops expedite,TroopsData cur){
		TroopsData next = getDefenceNpcs();
		String header = expedite.getHeader();
		if (cur.isLite) {//精英NPC允许连续攻击
			GameLog.info(header + " attack " + name + "'s liteMonster successful at " + position);
			if (next == null){
				expedite.isWin = true;
				return null;
			} else {
				expedite.isWin = false;
				return next;
			}
		} else {//非精英NPC,只能一次打一个
			GameLog.info(header + " attack " + name + "'s normalMonster successful at " + position);
			expedite.isWin = next == null;
			return null;
		}
	}
	
	public void notifyAttackIng(long now,long attUnionId){
		if (attUnionId == 0){
			return;
		}
		boolean couldNotify = true;
		if (notifyTimes.containsKey(attUnionId)){
			long preNotifyTime = notifyTimes.get(attUnionId);
			couldNotify = now  >= preNotifyTime + 30 * 60;//通知已经过了30分钟
		}
		if (couldNotify){
			UnionBody union = unionManager.search(attUnionId);
			if (union == null){
				return;
			}
			Npccity city = getData();
			String cityName = "1$" + city.getCityname();
			if (info.getUnionId() > 0){
				UnionBody defenceUnion = unionManager.search(info.getUnionId());
				if (defenceUnion != null){
					cityName = "0$" + defenceUnion.getName();
				}
			}
			chatMgr.addStringContentNotice(5,true,I18nGreeting.MSG_UNION_ATTCK_CITY_ING,"0$" + union.getName(),cityName);
			notifyTimes.put(attUnionId,now);
		}
	}
	
	public void notifyAttackSucc(UnionBody union){
		if (union == null){
			return;
		}
		Npccity city = getData();
		List<AttackerDamage> temps = new ArrayList<AttackerDamage>();
		for (Long key : damages.keySet()){
			long uid = key.longValue();
			Role role = world.getRole(uid);
			if (role.getUnionId() != union.getId()){
				continue;
			}
			AttackerDamage ad = new AttackerDamage();
			ad.setNum(damages.get(key).intValue());
			ad.setName(role.getName());
			temps.add(ad);
		}
		if (temps.size() > 1){
			Collections.sort(temps);
		}
		int index = 0;
		boolean flag = temps.size() >= index + 1;
		String param2 = flag ? ("0$" + temps.get(index).getName()) : "1$nil";
		String param3 = "0$" + (flag ? temps.get(index).getNum() : 0);
		index++;
		flag = temps.size() >= index + 1;
		String param4 = flag ? ("0$" + temps.get(index).getName()) : "1$nil";
		String param5 = "0$" + (flag ? temps.get(index).getNum() : 0);
		index++;
		flag = temps.size() >= index + 1;
		String param6 = flag ? ("0$" + temps.get(index).getName()) : "1$nil";
		String param7 = "0$" + (flag ? temps.get(index).getNum() : 0);
		chatMgr.addStringContentNotice(5,true,I18nGreeting.MSG_UNION_ATTCK_CITY_SUCC,"0$" + union.getName() , "1$" + city.getCityname(),param2,param3,param4,param5,param6,param7);
	}
	
	private void createUnionBattleRecord(boolean isWin,boolean isMass, MapRoleInfo attInfo, MapRoleInfo defInfo) {
		if (attInfo == null || defInfo == null) {
			GameLog.error("attInfo or defInfo is null");
			return;
		}
		UnionBody attUnion = unionManager.search(attInfo.getUnionId());
		UnionBody defUnion = unionManager.search(defInfo.getUnionId());
		long battleTime = TimeUtils.nowLong() / 1000;
		String paramList = "";
		paramList += "0:" + isWin + ":" + (attUnion == null ? "" : attUnion.getShortName()) + ":" + attInfo.getName()
				+ ":" + !isWin + ":" + (defUnion == null ? "---" : defUnion.getShortName()) + ":" + defInfo.getName();
		UnionRecords record = new UnionRecords(UnionRecords.UNION_BATTLE_RECORD_TYPE_UNION,
				UnionRecords.CONTENT_TYPE_ALLIAN_BATTLE_UNION, paramList, battleTime);
		if (attUnion != null) {
			attUnion.addOneUnionBattleRecord(record);
			attUnion.sendUnionRecordsToAllMembers();
			attUnion.gmRecord(isWin,isMass,true);
		}
		if (defUnion != null) {
			defUnion.addOneUnionBattleRecord(record);
			defUnion.sendUnionRecordsToAllMembers();
			defUnion.gmRecord(isWin,isMass,false);
		}
	}
	
	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return true;
	}

	public int getAliveArmy(List<TroopsData> armys){
		int count = 0;
		for (int i = 0 ; i < armys.size() ; i++){
			TroopsData troops = armys.get(i);
			if (troops.couldFight()){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_NPC_CITY;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_POSITION};
	}

	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		long unionId = data.getLong(RED_ALERT_GENERAL_UNION_ID);
		info.setUnionId(unionId);
		level   = data.getInt(RED_ALERT_GENERAL_LEVEL);
		name    = data.getString(RED_ALERT_GENERAL_NAME);
		key     = data.getString(RED_ALERT_NPC_CITY_KEY);
		state   = data.getByte(RED_ALERT_GENERAL_STATE);
		String str  = data.getString(RED_ALERT_GENERAL_SAFETIMER);
		safeTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		str         = data.getString(RED_ALERT_NPC_CITY_GIVEUP);
		giveUpTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		if (giveUpTimer != null){
			giveUpTimer.registTimeOver(new GiveUpOver());
			taskPool.mapTread.addObj(this,giveUpTimer);
		}
		str         = data.getString(RED_ALERT_NPC_CITY_BUILDS);
		builds      = JsonUtil.JsonToObjectList(str,Integer.class);
		Npccity npc = dataManager.serach(Npccity.class,key);
		initMonster(npc);
		Object obj = data.get(RED_ALERT_NPC_CITY_CONQUERER);
		if (obj != null){
			JoyBuffer buffer = JoyBuffer.wrap((byte[])obj);
			conquerer = new Conquerer();
			conquerer.deserialize(buffer);
		}
		info.setName(name);
	}

	@Override
	public void save() {
		for (int i = 0 ; i < builds.size() ;){
			int ub = builds.get(i).intValue();
			MapUnionBuild build = mapWorld.searchObject(ub);
			if (build == null || build.isRemoving()){
				builds.remove(i);
			}else{
				build.save();
				i++;
			}
		}
		super.save();
	}

	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		data.put(RED_ALERT_GENERAL_UNION_ID,info.getUnionId());
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_GENERAL_NAME,name);
		data.put(RED_ALERT_NPC_CITY_KEY,key);
		data.put(RED_ALERT_GENERAL_STATE,state);
		String str  = null;
		if (safeTimer != null){
			str = JsonUtil.ObjectToJsonString(safeTimer);
			data.put(RED_ALERT_GENERAL_SAFETIMER,str);
		}
		str         = JsonUtil.ObjectToJsonString(giveUpTimer);
		data.put(RED_ALERT_NPC_CITY_GIVEUP,str);
		str         = JsonUtil.ObjectToJsonString(builds);
		data.put(RED_ALERT_NPC_CITY_BUILDS,str);
		if (conquerer != null){
			conquerer.serialize(data);
		}
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putLong(info.getUnionId());
		unionManager.serializeSimple(info.getUnionId(),out);
		out.putInt(level);
		out.putPrefixedString(key,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(getAliveArmy(liteArmys));
		out.putInt(liteArmys.size());
		out.putInt(getAliveArmy(normalArmys));
		out.putInt(normalArmys.size());
		out.put(state);
		if (monsterRebirthTimer != null){
			out.putInt(1);
			monsterRebirthTimer.serialize(out);
		}else{
			out.putInt(0);
		}
		if (safeTimer != null){
			out.putInt(1);
			safeTimer.serialize(out);
		}else{
			out.putInt(0);
		}
		if (giveUpTimer != null){
			out.putInt(1);
			giveUpTimer.serialize(out);
		}else{
			out.putInt(0);
		}
		super.serialize(out);
		out.putInt(builds.size());
		for (int i = 0 ; i < builds.size() ; i++){
			int bp = builds.get(i).intValue();
			MapUnionBuild unionBuild = mapWorld.searchObject(bp);
			out.putInt(unionBuild.getLevel());
			out.putPrefixedString(unionBuild.getBuildKey(),JoyBuffer.STRING_TYPE_SHORT);
			out.putInt(bp);//int position
			out.put(unionBuild.getState());//byte 0 正常状态，1正在建造，2正在升级，3正在拆除,4特殊建造的倒计时状态
			TimerLast timer = unionBuild.getBuildTimer();
			if (timer != null){
				out.putInt(1);
				timer.serialize(out);
			}else{
				out.putInt(0);
			}
		}
		int[] result = getRange();
		out.putInt(result[0]);
		out.putInt(result[1]);
		if (conquerer != null){
			conquerer.serialize(out);
		}else{
			out.putInt(0);
		}
	}

	public Npccity getData(){
		return dataManager.serach(Npccity.class,key);
	}
	
	public void rebirthMonster(){
		damages.clear();
		monsterRebirthTimer = null;
		for (int i = 0 ; i < liteArmys.size() ; i++){
			TroopsData troops = liteArmys.get(i);
			troops.resetArmys();
		}
		for (int i = 0 ; i < normalArmys.size() ; i++){
			TroopsData troops = normalArmys.get(i);
			troops.resetArmys();
		}
		if (giveUpTimer == null){
			setMapThreadFlag(true);
		}
	}
	
	public void oprate(int type) {
		if (type == 0){//设置为领地
			if (state == 1){
				state = 2;
				UnionBody body = unionManager.search(info.getUnionId());
				if(body != null){
					body.sendOcpCityToMems(level);
				}
				activeBuilds();
			}
		}else if (type == 1){//放弃
			if (state > 0){
				giveUpTimer = new TimerLast(TimeUtils.nowLong()/1000,3600,TimerLastType.TIME_REMOVE);
				giveUpTimer.registTimeOver(new GiveUpOver());
				taskPool.mapTread.addObj(this,giveUpTimer);
			}
		}else if (type == 2){//取消放弃
			if (giveUpTimer != null){
				giveUpTimer = null;
			}
		}else if (type == 3){//取消主城
			lockBuilds();
		}
	}
	
	private void activeBuilds(){
		for (int i = 0 ; i < builds.size() ;){
			int ub = builds.get(i).intValue();
			MapUnionBuild build = mapWorld.searchObject(ub);
			if (build == null || build.isRemoving()){
				builds.remove(i);
			}else{
				build.active();
				i++;
			}
		}
	}
	
	/**
	 * 建筑不生效
	 */
	private void lockBuilds(){
		state = 1;
		for (int i = 0 ; i < builds.size() ;){
			int ub = builds.get(i).intValue();
			MapUnionBuild build = mapWorld.searchObject(ub);
			if (build == null || build.isRemoving()){
				builds.remove(i);
			}else{
				build.lock();
				i++;
			}
		}
	}
	
	public int[] getMoreRange(){
		int[] result = new int[2];
		for (int i = 0 ; i < builds.size() ; i++){
			Integer id = builds.get(i);
			MapUnionBuild build = mapWorld.searchObject(id.intValue());
			if (build != null){
				build.range(result);
			}
		}
		return result;
	}
	
	public int[] getRange(){
		int[] more = getMoreRange();
		Npccity npcData = getData();
		String views = npcData.getViewrange();
		String[] ss = views.split(":");
		int[] result = new int[2];
		result[0] = Integer.parseInt(ss[0]) + more[0];
		result[1] = Integer.parseInt(ss[1]) + more[1];
		return result;
	}
	
	
	public boolean isMain(){
		return state == 2;
	}
	
	private void destoryBuilds(){
		for (int i = 0 ; i < builds.size() ; i++){
			int pos = builds.get(i).intValue();
			MapUnionBuild buid = mapWorld.searchObject(pos);
			if (buid != null){
				buid.remove(false);
			}
		}
		builds.clear();
	}
	
	public void giveUpOver(UnionBody union){
		giveUpTimer = null;
		state = 0;
		info.setUnionId(0);
		rebirthMonster();
		destoryBuilds();
		if (union != null){
			union.sendViewsToAllMember();
		}
	}
	
	@Override
	public boolean couldGarrison(long unionId) {
		return info.getUnionId() == unionId && info.getUnionId() != 0;
	}
	
	public List<MapUnionBuild> search(String buildKey){
		List<MapUnionBuild> result = new ArrayList<MapUnionBuild>();
		for (int i = 0 ; i < builds.size() ; i++){
			Integer id = builds.get(i);
			MapUnionBuild build = mapWorld.searchObject(id.intValue());
			if (build != null && build.getBuildKey().equals(buildKey)){
				result.add(build);
			}
		}
		return result;
	}
	
	public List<MapUnionBuild> searchBuilds() {
		List<MapUnionBuild> result = new ArrayList<MapUnionBuild>();
		for (int i = 0; i < builds.size(); i++) {
			Integer id = builds.get(i);
			MapUnionBuild build = mapWorld.searchObject(id.intValue());
			if (build != null) {
				result.add(build);
			}
		}
		return result;
	}
	
	public MapUnionBuild search(int pos){
		if (builds.contains(pos)){
			MapUnionBuild build = mapWorld.searchObject(pos);
			return build;
		}
		return null;
	}
	
	public synchronized void addBuild(int pos) {
		if (!builds.contains(pos)){
			builds.add(pos);
		}
	}
	
	public synchronized boolean removeBuild(int pos) {
		for (int i = 0 ; i < builds.size();){
			int bPos = builds.get(i).intValue();
			if (bPos == pos){
				builds.remove(i);
				return true;
			}else{
				i++;
			}
		}
		return false;
	}
	
	public boolean checkBuildPosition(int target) {
		int[] ranges = getRange();
		List<Integer> poses = MapUtil.computeIndexs(position,ranges[0],ranges[1]);
		return poses.contains(target);
	}
	
	@Override
	public boolean checkUnion(long unionId) {
		if (info.getUnionId() == unionId && state >= 2){
			return true;
		}
		return false;
	}
	
	@Override
	public boolean destroy(String buffStr) {
		destoryFlag = true;
		List<GarrisonTroops> garrisons = getDefencers();
		for (int i = 0 ; i < garrisons.size() ; i++){
			GarrisonTroops garrison = garrisons.get(i);
			garrison.die();
		}
		state = 0;
		UnionBody union = unionManager.search(info.getUnionId());
		info.setUnionId(0);
		rebirthMonster();
		destoryBuilds();
		if (union != null){
			union.sendViewsToAllMember();
		}
		return true;
	}

	/**
	 * 怪物重生逻辑
	 * @author tanyong
	 *
	 */
	class MonsterRebirth implements TimerOver,Instances{
		@Override
		public void finish() {
			rebirthMonster();
			sendChange();
		}
	}
	

	/**
	 * 放弃逻辑完成
	 * @author tanyong
	 *
	 */
	class GiveUpOver implements TimerOver,Instances{
		@Override
		public void finish() {
			UnionBody union = unionManager.search(info.getUnionId());
			giveUpOver(union);
			sendChange();
		}
	}

	/**
	 * 获取这个城市视野
	 * @param views
	 */
	public void getViews(List<Integer> views) {
		int[] range = getRange();
		List<Integer> temp = MapUtil.computeIndexs(position,range[0],range[1]);
		for (int i = 0 ; i < temp.size() ; i++){
			Integer pos = temp.get(i);
			if (!views.contains(pos)){
				views.add(pos);
			}
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + name;
	}
}

