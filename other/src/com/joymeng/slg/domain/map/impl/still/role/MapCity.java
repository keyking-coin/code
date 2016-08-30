package com.joymeng.slg.domain.map.impl.still.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
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
import com.joymeng.slg.domain.map.impl.dynamic.MassTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.proxy.MapProxy;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.DefenseArmyInfo;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.CityState;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.object.technology.data.Techupgrade;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionRecords;
import com.joymeng.slg.world.GameConfig;

/**
 * 玩家城市在大地图的对象
 * @author tanyong
 *
 */
public class MapCity extends MapObject implements TimerOver{
	MassTroops mass;//集结部队
	TimerLast moveTimer;//5秒迁城倒计时
	int radarLevle;//雷达等级
	CityState cityState = new CityState(this);//城池状态

	public void init(Role role , RoleCityAgent city){
		info.setUid(role.getId());
		info.setName(role.getName());
		info.setLevel(city.getCityCenterLevel());
		info.setUnionId(role.getUnionId());
		info.setCityId(city.getId());
		city.setPosition(position);
		info.setPosition(position);
		info.getIcon().copy(role.getIcon());
	}
	
	public CityState getCityState() {
		return cityState;
	}
	
	public void setCityState(CityState cityState) {
		this.cityState = cityState;
	}
	
	public int getRadarLevle() {
		return radarLevle;
	}

	public void setRadarLevle(int radarLevle) {
		this.radarLevle = radarLevle;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_ROLE_CITY;
	}
	
	@Override
	public void registerAll() {
		
	}
	
	@Override
	public void _tick(long now) {
		if (mass != null){
			mass.tick();
		}
		if (moveTimer != null && moveTimer.over(now)){
			moveTimer.die();
			moveTimer = null;
		}
		cityState._tick();
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		info.serialize(out);
		super.serialize(out);
		cityState.serialize(out);
	}
	
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_CITY;
	}

	@Override
	public String[] wheres() {
		return new String[]{RED_ALERT_GENERAL_ID,RED_ALERT_GENERAL_UID};
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(DaoData.RED_ALERT_GENERAL_ID,info.getCityId());
		data.put(DaoData.RED_ALERT_GENERAL_UID,info.getUid());
		String str = mass == null ? "null" : mass.serialize();
		data.put(DaoData.RED_ALERT_CITY_MESS,str);
		String strState = cityState.serialize();
		data.put(DaoData.RED_ALERT_CITY_STATUS, strState);
	}

	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		info.loadFromData(data,position);
		String str = data.getString(DaoData.RED_ALERT_CITY_MESS);
		if (!StringUtils.isNull(str)){
			mass = new MassTroops(0,0);
			mass.deserialize(str,this);
		}
		radarLevle = data.getInt("radarLevle");
		str = data.getString(DaoData.RED_ALERT_CITY_STATUS);
		if (!StringUtils.isNull(str)){
			cityState.deserialize(str);
		}
	}
	
	public MassTroops getMass() {
		return mass;
	}

	public void setMass(MassTroops mass) {
		this.mass = mass;
	}

	public void massEnd(){
		if (mass != null){
			mass = null;//集结
			Role role = world.getRole(info.getUid());
			role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);//联盟战斗变化
		}
	}
	
	public void massCancle(Role role){
		mass.cancle(role);
		massEnd();
	}
	
	private ExpediteTroops createRoleExpedite(List<ArmyEntity> armyEntitys,int targetPosition){
		ExpediteTroops expedite = new ExpediteTroops();
		long expediteId = keyData.key(DaoData.TABLE_RED_ALERT_ROLEEXPEDITE);
		expedite.setId(expediteId);
		expedite.setStartPosition(position);
		expedite.setTargetPosition(targetPosition);
		TroopsData troops = new TroopsData();
		troops.getInfo().copy(info);
		troops.setComePosition(position);
		troops.getArmys().addAll(armyEntitys);
		troops.setId(expediteId);
		expedite.addTroops(troops);
		expedite.addLook(info.getUid());//攻击方肯定可以看见
		return expedite;
	}
	
	public ExpediteTroops tryToMove(Role role,List<ArmyInfo> armys,Map<String, String> poses,int targetPosition, byte type,int massTime){
		if (targetPosition < 0 || targetPosition > GameConfig.MAP_WIDTH * GameConfig.MAP_HEIGHT){
			return null;
		}
		MapCell targetCell = mapWorld.getMapCell(targetPosition);
		if (targetCell.getType().ordinal() == MapCellType.MAP_CELL_TYPE_RESIST.ordinal()){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
			return null;
		}
		int addNum = 0;
		List<ArmyEntity> armyEntitys = new ArrayList<ArmyEntity>();
		for (int i = 0 ; i < armys.size() ; i++){
			ArmyInfo army = armys.get(i);
			String armyKey = army.getArmyId();
			ArmyEntity entity = new ArmyEntity();
			entity.setKey(armyKey);
			entity.setSane(army.getArmyNum());
			entity.setPos(poses.get(armyKey));
			armyEntitys.add(entity);
			addNum += army.getArmyNum();
		}
		MapCity startCity = null;
		long now = TimeUtils.nowLong() / 1000;
		TimerLastType expediteType = null;
		if (type == 0){//建造要塞
			expediteType = TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS;
		}else if (type == 1){//建筑迁城点
			expediteType = TimerLastType.TIME_EXPEDITE_CREATE_MOVE;
		}else if (type == 3){
			expediteType = TimerLastType.TIME_EXPEDITE_GARRISON;
		}else if (type == 4){//侦查
			expediteType = TimerLastType.TIME_EXPEDITE_SPY;
		}else if (type == 5){//调拨
			MapObject targetObj = mapWorld.searchObject(targetCell);
			if (targetObj == null || !(targetObj instanceof MapFortress)){
				GameLog.error("目的地不是要塞或者军营");
				return null;
			}
			MapFortress fortress = (MapFortress)targetObj;
			if (fortress.getEmptyNum() == 0){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_GRID);
				return null;
			}
			expediteType = TimerLastType.TIME_EXPEDITE_STATION;
		}else if (type == 6){//集结
			MapObject targetObj = mapWorld.searchObject(targetCell);
			if (targetObj == null){
				GameLog.error("找不到集结目标");
				return null;
			}
			if (mass != null){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_CITY_MASS_ING);
				return null;
			}
			int gridMax    = role.getRoleMaxMassArmyNum(info.getCityId());
			int soldierMax = role.getRoleMaxMassNum(info.getCityId());
			mass = new MassTroops(gridMax,soldierMax);
			GarrisonTroops occupyer = new GarrisonTroops();
			long occuperId = keyData.key(DaoData.TABLE_RED_ALERT_ROLEEXPEDITE);
			occupyer.setId(occuperId);
			TroopsData troops = occupyer.getTroops();
			troops.getArmys().addAll(armyEntitys);
			troops.getInfo().copy(info);
			troops.setComePosition(position);
			occupyer.setPosition(position);
			troops.setId(occuperId);
			//设置集结
			TimerLast timer = new TimerLast(now,0,TimerLastType.TIME_MAP_MASS);
			occupyer.registTimer(timer);
			occupyer.addSelf();
			mass.addGrid(occupyer);
			mass.getTargetInfo().copy(targetObj.getInfo());
			mass.getTargetInfo().setPosition(targetPosition);
			mass.setPosition(position);//设置集结点
			//注册出发时间
			//massTime = 60;
			TimerLast goTimer = new TimerLast(now,massTime,TimerLastType.TIME_MAP_MASS_END);
			mass.registTimeOver(goTimer,this);
//			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ASSEMBLE);//活动发起集结
			return null;
		}else if (type == 7){//去集结
			if (targetCell.getTypeKey() != MapCity.class){//
				GameLog.error("客户端传错目标了");
				return null;
			}
		    startCity = mapWorld.searchObject(targetCell);
			expediteType = TimerLastType.TIME_EXPEDITE_MASS;
		}else if (type == 8){//联盟采集
			expediteType = TimerLastType.TIME_EXPEDITE_UNION_RES_COLLECT;
		}else if (type == 9){//去副本的路上
			expediteType = TimerLastType.TIME_GO_TO_ECTYPE;
		}else{
			if (targetCell.getType() == MapCellType.MAP_CELL_TYPE_UINON_CITY && role.getUnionId() == 0){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_NO_UNION,role.getName());
				return null;
			}
			expediteType = mapWorld.getExpediteOutType(targetCell,info.getUnionId());
		}
		if (targetCell.getTypeKey() == null && type == 4){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
			return null;
		}else if (targetCell.getTypeKey() == null && type <= 1){//去建造选中的是空格子
			MapProxy proxy = null;
			if (expediteType.ordinal() == TimerLastType.TIME_EXPEDITE_CREATE_FORTRESS.ordinal()){
				proxy = mapWorld.create(false,MapCellType.MAP_CELL_TYPE_FORTRESS_PROXY);
			}else if (expediteType.ordinal() == TimerLastType.TIME_EXPEDITE_CREATE_MOVE.ordinal()){
				proxy = mapWorld.create(false,MapCellType.MAP_CELL_TYPE_MOVE_PROXY);
			}
			if (proxy != null && mapWorld.checkPosition(proxy,targetPosition)){//这个代理类能放下
				mapWorld.insertObj(proxy);
				mapWorld.updatePosition(proxy,targetPosition);
			}else{
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_ROLE_EXPEDITE_FAIL);
				return null;
			}
		}
		ExpediteTroops expedite = createRoleExpedite(armyEntitys,targetPosition);
		if (startCity != null){
			if (!startCity.mass.haveGrid()){
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MAP_CITY_MASS_NO_GRID);
				return null;
			}
			if (!startCity.mass.checkNum(startCity,role,addNum)){
				return null;
			}
			startCity.mass.addGrid(expedite);
		}
		expedite.computMoveSpeed(role);
		expedite.addSelf();
		long castTime = MapUtil.computeCastTime(position,targetPosition,expedite.getSpeed());//行军需要的时间
		if (type == 5){//调拨行军速度加倍
			castTime /= 2;
		}
		TimerLast timer = new TimerLast(now,castTime,expediteType);
		expedite.registTimer(timer);
		//通知别人目标点有人打你，这个逻辑还没实现等内城的雷达实现
		targetCell.expedite(expedite.getId());//终点格子加入行军
		mapWorld.getMapCell(position).expedite(expedite.getId());//起点格子加入行军
		return expedite;
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			PointVector point = MapUtil.getPointVector(position);
			int start = expedite.getStartPosition();
			int end = expedite.getTargetPosition();
			TroopsData leader = expedite.getLeader();
			Role aRole =world.getRole(leader.getInfo().getUid());
			Role role = world.getRole(info.getUid());//防守者
			MapCell toArmy = mapWorld.getMapCell(expedite.getTargetPosition());
			long group_id = 0;
			if (toArmy.getTypeKey() == MapCity.class){
				MapCity city = mapWorld.searchObject(toArmy);
				group_id = city.getInfo().getUid();
			}
			if (expedite.getTimer().getType() == TimerLastType.TIME_ARMY_BACK){
				//回城操作,部队回营
				RespModuleSet rms = new RespModuleSet();
				//部队回营
				expedite.armyBack(rms,role);
				expedite.packageBack(rms,role);
				MapCell comeCell = mapWorld.getMapCell(expedite.getStartPosition());
				if (comeCell.getTypeKey() == MapFortress.class || comeCell.getTypeKey() == MapBarracks.class){
					MapFortress fortress = mapWorld.searchObject(comeCell);
					fortress.removeGrid(expedite);
				}
				for (int i = 0 ; i < expedite.getTeams().size() ; i++){
					TroopsData troops = expedite.getTeams().get(i);
					if (troops.getInfo().getUid() != role.getId()){
						goBackHomeFromHere(troops);
					}
				}
				MessageSendUtil.sendModule(rms,role.getUserInfo());
				expedite.remove();//部队任务完成,移除部队
			}else if(expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){
				if (info.getUnionId() == expedite.getLeader().getInfo().getUnionId()){//驻防
					garrison(expedite);
				}else{
					expedite.goBackToCome();
				}
			}else if(expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){
				MapRoleInfo attInfo = expedite.getLeader().getInfo();
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
				UnionBody dUnion = unionManager.search(info.getUnionId());//防守方联盟
				if (dUnion != null){
					UnionBody aUnion = unionManager.search(attInfo.getUnionId());
					if (aUnion != null){
						boolean couldNotify = true;
						long now = TimeUtils.nowLong() / 1000;
						if (dUnion.getNotifyTimes().containsKey(attInfo.getUnionId())){
							long preNotifyTime = dUnion.getNotifyTimes().get(attInfo.getUnionId());
							couldNotify =  now >= preNotifyTime + 30 * 60;//通知已经过了30分钟
						}
						if (couldNotify){
							String aName = "0$" + aUnion.getName();
							String dName = "0$" + dUnion.getName();
							chatMgr.addStringContentNotice(5,true,I18nGreeting.MSG_UNION_ATTCK_UNION_ING,aName,dName);
							dUnion.getNotifyTimes().put(attInfo.getUnionId(),now);
						}
					}
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				NewLogManager.mapLog(aRole, "attack_player",info.getUid(),point.x,point.y,newStr);//(暂时先这样)
				if (!expedite.isMass()){//非集结部队
					List<GarrisonTroops> defenders = getDefencers();//防守者
					if (defenders.size() > 0){
						expedite.isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_P_CITY,ReportTitleType.TITLE_TYPE_D_P_CITY);
						//下发联盟战斗记录
						createUnionBattleRecord(expedite.isWin,expedite.isMass(),attInfo,info);						
						//任务事件
						aRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_ATK_WIN, false);
						for (GarrisonTroops defender : defenders){
							long uid = defender.getTroops().getInfo().getUid();
							Role defRole = world.getObject(Role.class, uid);
							defRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_HLP_DEF, true);
						}
						role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_DEF_WIN, true);
						if (!expedite.isWin){//连驻防者都没有干过
							LogManager.mapLog(aRole,expedite.getStartPosition(),expedite.getTargetPosition(), expedite.getId(),"endOfBattle");
							GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s allyers fail at " + position);
							return;
						}
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s allyers successful at " + position);
						LogManager.mapLog(aRole, expedite.getStartPosition(), expedite.getTargetPosition(), expedite.getId(), "endOfBattle");
					}
				}
				RoleCityAgent city = role.getCity(info.getCityId());
				RoleArmyAgent armyAgent = city.getCityArmys();
				List<RoleBuild> defenceBuilds = city.searchBuildByCompomemt(BuildComponentType.BUILD_COMPONENT_DEFENSE);
				List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
				armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(),armys);
				List<TroopsData> attackers = expedite.getTeams();
				BattleField battle = new BattleField();
				for (int i = 0 ; i < attackers.size() ; i++){
					TroopsData attacker = attackers.get(i);
					battle.add(attacker,Side.ATTACK);
				}
				for (int i = 0 ; i < armys.size() ; i++){
					ArmyInfo army = armys.get(i);
					battle.add(army,Side.DEFENSE);
				}
				for (int i = 0 ; i < defenceBuilds.size() ; i++){
					RoleBuild build = defenceBuilds.get(i);
					BuildComponentDefense defenseComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
					DefenseArmyInfo buildArmy = defenseComponent.getDefenseArmy();
					if (buildArmy != null){
						battle.add(buildArmy,Side.DEFENSE);
					}
				}
				List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
				List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
				troopses_d.addAll(battle.getHooks());
				aRole.effectFightTroops(troopses_a,troopses_d,attInfo.getCityId());
				role.effectFightTroops(troopses_d,troopses_a,info.getCityId());
				BattleRecord record = battle.startFight();
				//计算攻击方损耗
				List<FightTroops> troopses = battle.getTroopses(Side.ATTACK);
				Map<Integer,FightResutTemp> attackersResult = new HashMap<Integer,FightResutTemp>();
				for (int i = 0 ; i < attackers.size() ; i++){
					TroopsData attacker = attackers.get(i);
					Map<Integer,FightResutTemp> temp = MapUtil.computeFightResult(troopses,attacker);
					Role ar = world.getRole(attacker.getInfo().getUid());
					MapUtil.triggerAE_kill_soldier(ar,temp.values());
					attackersResult.putAll(temp);
				}
				//计算防御方损耗
				troopses = battle.getTroopses(Side.DEFENSE);
				troopses.addAll(battle.getHooks());//陷阱
				Map<String,FightResutTemp> defencerResult = MapUtil.computeFightResult(troopses,role,info.getCityId(),armyAgent,defenceBuilds,armys,true);
				MapUtil.triggerAE_kill_soldier(role,defencerResult.values());
				boolean couldLoot = false;
				Side winnerSide = battle.GetWinner();
				if (winnerSide != null && winnerSide.ordinal() == Side.ATTACK.ordinal()){
					MapUtil.report(expedite,this,true,battle,record,defenceBuilds,attackersResult,defencerResult,city);
					if (expedite.isMass()){//集结部队打下主人，在打驻防者
						List<GarrisonTroops> defenders = getDefencers();//防守者
						expedite.isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_P_CITY,ReportTitleType.TITLE_TYPE_D_P_CITY);
						if (expedite.isWin){
							couldLoot=  true;
							GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s allyers successful at " + position);
						}else{
							GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s allyers fail at " + position);
							LogManager.pvpLog(role, group_id, (byte)0, "0", 0);
						}
						createUnionBattleRecord(expedite.isWin,expedite.isMass(),attInfo,info);
						LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
					}else{
						createUnionBattleRecord(true,expedite.isMass(),attInfo,info);	
						couldLoot=  true;
						LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s city successful at " + position);
					}
				}else{
					MapUtil.report(expedite,this,false,battle,record,defenceBuilds,attackersResult,defencerResult,city);
					createUnionBattleRecord(false,expedite.isMass(),attInfo,info);
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s city fail at " + position);
					LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
					LogManager.pvpLog(role, group_id, (byte)0, "0", 0);
				}
				if (couldLoot){//计算掠夺的资源
					//对城墙造成50点伤害
					List<RoleBuild> builds = city.searchBuildByBuildId(BuildName.FENCE.getKey());
					for (int i = 0 ; i < builds.size() ; i++){
						RoleBuild build = builds.get(i);
						BuildComponentWall bcw =  build.getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
						bcw.redDefenseValue(50,build,role);
					}
 					Map<String,Integer> lootResources = city.getMaxLootResource();
					for (String rk : lootResources.keySet()){
						int value = lootResources.get(rk).intValue();
						if (value > 0){
							ResourceTypeConst rt = ResourceTypeConst.search(rk);
							city.redResource(rt,value);//先扣
						}
					}
					expedite.goBackToCome(lootResources);//资源塞入行军部队的包裹
					for (String rk : lootResources.keySet()){
						int value = lootResources.get(rk).intValue();
						if (value > 0){
							ResourceTypeConst rt = ResourceTypeConst.search(rk);
							city.addResource(rt,value,role);//剩余的资源再加回去
						}
					}
				}else{
					expedite.goBackToCome();
				}
				if (role.isOnline()){
					MessageSendUtil.sendModule(role.sendToClient(3),role.getUserInfo());
				}
				//任务事件
				if(expedite.isMass()){
					for (TroopsData troops : expedite.getTeams()){
						Role attRole = world.getRole(troops.getInfo().getUid());
						if (!troops.isLeader()) {
							attRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_HLP_MS_WIN, couldLoot);
						}
						attRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_MS_WIN, couldLoot);
						attRole.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ATTACK_PLAYER);//活动进攻玩家主城
						attRole.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ASSEMBLE);
					}
				}else{
					Role attRole = world.getRole(attInfo.getUid());
					attRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_ATK_WIN,couldLoot);
					attRole.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ATTACK_PLAYER);//活动进攻玩家主城
				}
				for(GarrisonTroops defender : getDefencers()){
					long uid = defender.getTroops().getInfo().getUid();
					Role defRole = world.getRole(uid);
					defRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_HLP_DEF, couldLoot);
				}
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_DEF_WIN, couldLoot);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){
				//城市侦查结果报告
				MapUtil.spyResport(SpyType.SPY_TYPE_CITY,expedite,this);
				aRole.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.SPY);
				NewLogManager.mapLog(aRole, "detect_player",info.getUid(),point.x,point.y);
				logSpy(aRole,start, end,expedite.getId());
			}else if(expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_MASS){
				if (mass != null && !mass.isExpedite() && leader.getInfo().getUnionId() == info.getUnionId()){
					//集结部队还没有出发
					GarrisonTroops garrison = expedite.occuper(this);
					TimerLast timer = new TimerLast(TimeUtils.nowLong()/1000,0,TimerLastType.TIME_MAP_MASS);
					garrison.registTimer(timer);//注册集结时间
					garrison.getTroops().setLeader(false);
					mass.changeGrid(expedite,garrison);
					role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);//联盟战斗变化
					//role = world.getRole(expedite.getLeader().getInfo().getUid());
					//role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ASSEMBLE);//活动参加集结
				} else {
					expedite.goBackToCome();
				}
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_MONSTER_ATTACK){//怪物攻城
				//王建说的先打驻防者
				MapRoleInfo attInfo = leader.getInfo();
				List<GarrisonTroops> defenders = getDefencers();//防守者
				boolean isWin = defenders.size() == 0;
				if (defenders.size() > 0){
					isWin = attackDefenders(defenders,expedite,null,ReportTitleType.TITLE_TYPE_D_MONSTER);
					//下发联盟战斗记录
					createUnionBattleRecord(isWin,expedite.isMass(),attInfo,info);						
					for (GarrisonTroops defender : defenders){
						long uid = defender.getTroops().getInfo().getUid();
						Role defRole = world.getObject(Role.class, uid);
						defRole.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_HLP_DEF, true);
					}
					if (!isWin){//连驻防者都没有干过
						GameLog.info(expedite.getLeader().getInfo().getName() + " attack " + info.getUid() + "'s allyers fail at " + position);
					}else{
						GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s allyers successful at " + position);
					}
				}
				if (isWin){
					RoleCityAgent city = role.getCity(info.getCityId());
					RoleArmyAgent armyAgent = city.getCityArmys();
					List<RoleBuild> defenceBuilds = city.searchBuildByCompomemt(BuildComponentType.BUILD_COMPONENT_DEFENSE);
					List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
					armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(),armys);
					List<TroopsData> attackers = expedite.getTeams();
					BattleField battle = new BattleField();
					for (int i = 0 ; i < attackers.size() ; i++){
						TroopsData attacker = attackers.get(i);
						battle.add(attacker,Side.ATTACK);
					}
					for (int i = 0 ; i < armys.size() ; i++){
						ArmyInfo army = armys.get(i);
						battle.add(army,Side.DEFENSE);
					}
					for (int i = 0 ; i < defenceBuilds.size() ; i++){
						RoleBuild build = defenceBuilds.get(i);
						BuildComponentDefense defenseComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
						DefenseArmyInfo buildArmy = defenseComponent.getDefenseArmy();
						if (buildArmy != null){
							battle.add(buildArmy,Side.DEFENSE);
						}
					}
					List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
					List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
					troopses_d.addAll(battle.getHooks());
					role.effectFightTroops(troopses_d,troopses_a,info.getCityId());
					BattleRecord record = battle.startFight();
					//计算攻击方损耗
					List<FightTroops> troopses = battle.getTroopses(Side.ATTACK);
					Map<Integer,FightResutTemp> attackersResult = new HashMap<Integer,FightResutTemp>();
					for (int i = 0 ; i < attackers.size() ; i++){
						TroopsData attacker = attackers.get(i);
						Map<Integer,FightResutTemp> temp = MapUtil.computeFightResult(troopses,attacker);
						attackersResult.putAll(temp);
					}
					//计算防御方损耗
					troopses = battle.getTroopses(Side.DEFENSE);
					troopses.addAll(battle.getHooks());//陷阱
					Map<String,FightResutTemp> defencerResult = MapUtil.computeFightResult(troopses,role,info.getCityId(),armyAgent,defenceBuilds,armys,false);
					MapUtil.triggerAE_kill_soldier(role,defencerResult.values());
					Side winnerSide = battle.GetWinner();
					if (winnerSide != null && winnerSide.ordinal() == Side.ATTACK.ordinal()){
						isWin = true;
						createUnionBattleRecord(true,expedite.isMass(),attInfo,info);	
						LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
						GameLog.info(expedite.getLeader().getInfo().getName() + " attack " + info.getUid() + "'s city successful at " + position);
					}else{
						createUnionBattleRecord(false,expedite.isMass(),attInfo,info);
						GameLog.info(expedite.getLeader().getInfo().getName() + " attack " + info.getUid() + "'s city fail at " + position);
						LogManager.mapLog(role, start, end, expedite.getId(), "endOfBattle");
						LogManager.pvpLog(role, group_id, (byte)0,"0",0);
					}
					MapUtil.report(expedite,this,true,battle,record,defenceBuilds,attackersResult,defencerResult,city);
					MapUtil.triggerAE_monster_act_role(role,isWin,defencerResult.values());
				}
			}
		}
	}

	private void createUnionBattleRecord(boolean isWin, boolean isMass, MapRoleInfo attInfo, MapRoleInfo defInfo) {
		if (attInfo == null || defInfo == null) {
			GameLog.error("attInfo or defInfo is null");
			return;
		}
		UnionBody attUnion = unionManager.search(attInfo.getUnionId());
		UnionBody defUnion = unionManager.search(defInfo.getUnionId());
		long battleTime = TimeUtils.nowLong() / 1000;
		String paramList = "";
		paramList += "0:" + isWin + ":" + (attUnion == null ? "" : attUnion.getShortName()) + ":" + attInfo.getName()
				+ ":" + !isWin + ":" + (defUnion == null ? "" : defUnion.getShortName()) + ":" + defInfo.getName();
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
		if (cityState.isNowar() && expedite.getTimer().getType() != TimerLastType.TIME_MONSTER_ATTACK){//开了保护罩子
			expedite.setNoBattleTip(I18nGreeting.MSG_MAP_NO_FIGHT_ISNOWAR);
			return false;
		}
		return true;
	}

	@Override
	public void finish() {
		if (mass != null){
			mass.go_off(this);
		}
		setMapThreadFlag(moveTimer == null);
	}

	public void move(RoleCityAgent city,int newPosition) {
		int prePos = position;
		mapWorld.forceInsert(this,this,newPosition);
		city.setPosition(newPosition);
		info.setPosition(newPosition);
		save();
		long uid = info.getUid();
		List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < expedites.size() ; i++){
			ExpediteTroops expedite = expedites.get(i);
			for (int j = 0 ; j < expedite.getTeams().size() ; j++){
				TroopsData troops = expedite.getTeams().get(j);
				if (troops.getInfo().getUid() == uid){
					troops.getInfo().copy(info);
				}
			}
		}
		if (mass != null && !mass.isExpedite()){
			//目前还有没出发的集结部队
			mass.changePos(prePos,newPosition);
		}
		List<GarrisonTroops> others = new ArrayList<GarrisonTroops>();
		List<GarrisonTroops> garrisons = world.getListObjects(GarrisonTroops.class);
		for (int i = 0 ; i < garrisons.size() ; i++){
			GarrisonTroops garrison = garrisons.get(i);
			if (garrison.getPosition() == prePos && garrison.getTimer().getType() == TimerLastType.TIME_MAP_GARRISON){
				//在我的主城驻防的友军
				others.add(garrison);
			}else{
				TroopsData troops = garrison.getTroops();
				if (troops.getInfo().getUid() == uid && troops.getComePosition() == prePos){//是来自主城的部队
					troops.getInfo().copy(info);
					troops.setComePosition(newPosition);
				}
			}
		}
		if (others.size() > 0){
			//我走了留下别人的驻防点。
			Collections.sort(others);
			GarrisonTroops other = others.get(0);
			MapGarrison mg = mapWorld.create(MapGarrison.class,true);
			mapWorld.updatePosition(mg,prePos);
			mg.getInfo().copy(other.getTroops().getInfo());
		}
		List<MapObject> objs = world.getMapObjects(uid);
		for (int i = 0 ; i < objs.size() ; i++){
			MapObject obj = objs.get(i);
			if (obj.getInfo().getUid() == uid){
				obj.getInfo().copy(info);
			}
		}
	}

	@Override
	public boolean destroy(String buffStr) {
		//添加核弹buff
		Role role = world.getRole(info.getUid());
		Techupgrade grade  = dataManager.serach(Techupgrade.class,buffStr);
		role.getEffectAgent().addSkillBuff(role,grade.getBuffList().get(0),grade.getBuffList().get(1),grade.getTechID(),grade.getResearchTime());
		return true;
	}
	
	public void moveByRandom(){
		int pos = mapWorld.randomDrop(this);
		moveToNewPlace(pos);
	}
	
	public void moveToNewPlace(int pos){
		if (moveTimer != null){
			return;
		}
		MoveLogic move = new MoveLogic(pos);
		moveTimer = new TimerLast(TimeUtils.nowLong()/1000,5,TimerLastType.TIME_CITY_MOVE_IN_RECENT);
		taskPool.mapTread.addObj(this,moveTimer);
		moveTimer.registTimeOver(move);
	}
	
	/**
	 * 其他地方最好不要掉，这个只给gm用
	 * @param pos
	 */
	public boolean moveAtOnece(int pos){
		if (mapWorld.checkPosition(this,pos)) {
			MoveLogic move = new MoveLogic(pos);
			move.finish();
			return true;
		}
		return false;
	}
	
	/**
	 * 获取我的视野
	 * @param views
	 * @param role
	 */
	public void getViews(List<Integer> views , Role role){
		long uid = info.getUid();
		int rLevel = Math.max(1,role == null ? radarLevle : role.getRadarlevel(info.getCityId()));
		Buildinglevel bl = RoleBuild.getBuildinglevelByCondition(BuildName.RADAR.getKey(),rLevel);
		List<String> lis = bl.getParamList();
		int w = Integer.parseInt(lis.get(0)) + cityState.getCityViewBuff();
		int h = Integer.parseInt(lis.get(1)) + cityState.getCityViewBuff();
		List<Integer> temp = MapUtil.computeIndexs(position,w,h);
		for (int i = 0 ; i < temp.size() ; i++){
			Integer pos = temp.get(i);
			if (!views.contains(pos)){
				views.add(pos);
			}
		}
		List<MapFortress> fortresses = mapWorld.getAllFortresses(uid);
		for (int i = 0 ; i < fortresses.size() ; i++){
			MapFortress fortress = fortresses.get(i);
			fortress.getViews(views);
		}
	}
	
	class MoveLogic implements TimerOver,Instances{
		int newPos = 0;
		public MoveLogic(int newPos){
			this.newPos = newPos;
		}
		
		@Override
		public void finish() {
			long uid   = info.getUid();
			int cityId = info.getCityId();
			Role role = world.getRole(uid);
			MapCityMove cityMove = mapWorld.searchCityMove(uid,cityId);
			if (cityMove != null){
				cityMove._remove();//删除迁城对象
			}
			move(role.getCity(cityId),newPos);
			RespModuleSet rms1 = new RespModuleSet();
			AbstractClientModule module = new AbstractClientModule(){
				@Override
				public short getModuleType() {
					return ClientModule.NTC_DTCD_CITY_NEED_MOVE;
				}
			};
			module.add(uid);//int 迁城的用户编号
			module.add(cityId);//int 城市编号
			module.add(newPos);//int 新的位置坐标
			role.sendRoleToClient(rms1);
			rms1.addModule(module);
			role.sendViews(rms1,false);
			MessageSendUtil.sendModule(rms1,role.getUserInfo());
			//通知服务器的其他人
			RespModuleSet rms2 = new RespModuleSet();
			rms2.addModule(module);
			MessageSendUtil.sendMessageToOnlineRole(rms2,role);
			
			PointVector point = MapUtil.getPointVector(newPos);
			NewLogManager.mapLog(role, "move_city_success", (int) point.x, (int) point.y);		
		}
	}
	
	/**
	 * 盟友从我这里回家
	 * @param troops
	 */
	public void goBackHomeFromHere(TroopsData troops){
		ExpediteTroops expedite = new ExpediteTroops();
		TroopsData leader = expedite.addTroops(troops);
		Role role = world.getRole(troops.getInfo().getUid());
		leader.setLeader(true);
		TimerLastType type  = TimerLastType.TIME_ARMY_BACK;
		expedite.setStartPosition(position);
		expedite.setTargetPosition(leader.getComePosition());
		expedite.setId(troops.getId());
		expedite.computMoveSpeed(leader,role);
		long castTime = MapUtil.computeCastTime(expedite.getStartPosition(),expedite.getTargetPosition(),expedite.getSpeed());//行军需要的时间
		TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000,castTime,type);
		expedite.registTimer(timer);//计入行军倒计时
		MapCell targetCell = mapWorld.getMapCell(expedite.getTargetPosition());
		MapCell startCell = mapWorld.getMapCell(expedite.getStartPosition());
		targetCell.expedite(expedite.getId());//终点格子加入回去行军
		startCell.expedite(expedite.getId());//起点格子加入回去行军
		expedite.addSelf();//添加行军到列表
		expedite.addLook(role.getId());
		role.handleEvent(GameEvent.TROOPS_SEND);
	}
}
	
