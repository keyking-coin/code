package com.joymeng.slg.net.handler.impl.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuilding;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.moster.MapMonster;
import com.joymeng.slg.domain.map.impl.still.proxy.MapProxy;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.map.impl.still.res.MapResource;
import com.joymeng.slg.domain.map.impl.still.role.MapBarracks;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.role.MapGarrison;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionDefenderTower;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionWareHouse;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.resource.data.Resourcestype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.world.GameConfig;

public class ExpediteSelectArmy extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//兵种类型、数量、位置信息
		params.put(in.getInt());//int 起点格子数
		params.put(in.getInt());//int 格子数
		params.put(in.get());//byte 0建造要塞,1建造迁城点,2去攻击,3驻防,4侦查,5调拨,6集结,7去集结,8联盟采集,9去副本
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//String 参数列表
		params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT));//String 建筑要塞用的
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		String soldiers = params.get(0);
		int startPos = params.get(1);
		int target   = params.get(2);
		byte type    = params.get(3);
		if (startPos == target){
			GameLog.error("客户端傻了吧，怎么起点和终点是一个坐标");
			resp.fail();
			return resp;
		}
		/*
		List<Integer> views = role.getViews();
		if (!views.contains(target)){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_EXPEDITE_NOT_IN_VIEWS);
			resp.fail();
			return resp;
		}*/
		MapCell targetCell   = mapWorld.getMapCell(target);
		MapCell startCell    = mapWorld.getMapCell(startPos);
		MapObject startBuild = mapWorld.searchObject(startCell);
		MapObject targetObj  = mapWorld.searchObject(targetCell);
		if (type == 3){//驻防判断
			if (targetObj != null && !targetObj.couldGarrison(role.getUnionId())){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_CANT_NOT_GARRISON);
				resp.fail();
				return resp;
			}
		}
		ExpediteTroops expedite = null;
		Worldbuilding wb = null;
		Worldbuildinglevel wbl= null;
		int costMoney = 0;
		int spyNeedFood = 0;
		if (startBuild == null){
			GameLog.error("error start position");
			resp.fail();
			return resp;
		}
		RoleCityAgent city = role.getCity(startBuild.getInfo().getCityId());
		int needStamina = 0;
		if (type == 0){//建造要塞,需要判断消耗的资源
			MapFortress obj = new MapFortress();
			if (targetCell.getTypeKey() != MapGarrison.class && !mapWorld.checkPosition(obj,target)){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_FORTRESS_MUST_AT_NONE);
				resp.fail();
				return resp;
			}
			if (targetCell.getTypeKey() == MapGarrison.class){
				MapGarrison mapGarrison = mapWorld.searchObject(targetCell);
				if (mapGarrison.getInfo().getUid() != info.getUid()){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_CREATE_FORTRESS_NOT_YOUR_GARRSION);
					resp.fail();
					return resp;
				}
			}
			wb = dataManager.serach(Worldbuilding.class,BuildName.MAP_FORTRESS.getKey());
			wbl = dataManager.serach(Worldbuildinglevel.class,BuildName.MAP_FORTRESS.getKey() + "1");
			int have = mapWorld.getFortressesCount(info.getUid());
			int maxNum = 0;
			List<String> counts = wb.getMaxBuildCount();
			int standLevel = city.getCityCenterLevel();
			for (int i = 0 ; i < counts.size() ; i++){
				String str = counts.get(i);
				String[] ss = str.split(":");
				int min = Integer.parseInt(ss[0]);
				int max = Integer.parseInt(ss[1]);
				int num = Integer.parseInt(ss[2]);
				if (standLevel >= min && standLevel <= max){
					maxNum = Math.max(maxNum,num);
				}
			}
			maxNum += city.getCityAttr().getFortNum();
			if (have >= maxNum){//建造上限
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_FORTRESS_LIMITE , maxNum);
				resp.fail();
				return resp;
			}
			//建筑条件
			if (!city.checkBuildConditions(role,wbl.getNeedBuildingIDList())){
				resp.fail();
				return resp;
			}
			//资源条件
			String money = params.get(4);
			if (money.equals("true")){
				costMoney = city.getCostMoney(role,wbl.getBuildCostList(),null,0,(byte)0);
				if (costMoney > role.getMoney()){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_MONEY,costMoney);
					resp.fail();
					return resp;
				}
			}else if (!city.checkResConditions(role,wbl.getBuildCostList())){
				resp.fail();
				return resp;
			}
		}else if (type == 1){//建造迁城点,需要判断消耗的资源
			MapProxy proxy = mapWorld.create(false, MapCellType.MAP_CELL_TYPE_MOVE_PROXY);
			if (!mapWorld.checkPosition(proxy,target)){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_BASE_MUST_AT_NONE);
				resp.fail();
				return resp;
			}
			if (mapWorld.checkMovingCity(role)){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_BASE_ONLY_ONE);
				resp.fail();
				return resp;
			}
			wbl = dataManager.serach(Worldbuildinglevel.class,BuildName.MAP_BASE.getKey() + city.getCityCenterLevel());
			//建筑条件
			if (!city.checkBuildConditions(role,wbl.getNeedBuildingIDList())){
				resp.fail();
				return resp;
			}
			//资源条件
			if (!city.checkResConditions(role,wbl.getBuildCostList())){
				resp.fail();
				return resp;
			}
		}else if (type == 4){//侦查消耗
			spyNeedFood = targetObj.getLevel() * GameConfig.EXPEDITE_SPY_COST;
			if (city.getResource(ResourceTypeConst.RESOURCE_TYPE_FOOD) < spyNeedFood){
				Resourcestype rt = dataManager.serach(Resourcestype.class,ResourceTypeConst.RESOURCE_TYPE_FOOD.getKey());
				MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_NO_RESOURCE,spyNeedFood,rt.getName());
				resp.fail();
				return resp;
			}
		}else if (type == 9){//去副本的条件判断
			//选择的目标不是副本
			if (targetCell.getTypeKey() != MapEctype.class){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_TARGET_ISNT_ECTYPE);
				resp.fail();
				return resp;
			}
			needStamina = GameConfig.EXPEDITE_ECTYPE_NEED_STAMINA;
			MapEctype ectype = (MapEctype)targetObj;
			//您已经在这个副本上了
			if (ectype.checkMeIsInHere(role)){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_YOU_WERE_IN_ECTYPE);
				resp.fail();
				return resp;
			}
			//您正在去此副本的路上
			if (mapWorld.checkIsMovingToEctype(role, target)){
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_MOVING_ECTYPE);
				resp.fail();
				return resp;
			}
		}else if (type == 2){//判断体力条件
			if (targetObj instanceof MapMonster  ||
				targetObj instanceof MapUnionCity ||
				targetObj instanceof MapBarracks ||
				(targetObj instanceof MapUnionBuild && !(targetObj instanceof MapUnionWareHouse))){
				needStamina = GameConfig.EXPEDITE_MONSTER_NEED_STAMINA;
			}
		}
		//体力的判断
		if (role.getRoleStamina().getCurStamina() < needStamina){
			MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_STAMINA);
			resp.fail();
			return resp;
		}
		MapCity mapCity = null;
		RespModuleSet rms = new RespModuleSet();
		if (startCell.getTypeKey() == MapCity.class){//主城出发
			int massTime = 0;
			mapCity = (MapCity)startBuild;
			if (type == 6){//集结
				if (city.searchBuildByBuildId(BuildName.WAR_LOBBY.getKey()).size() == 0){
					MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_MAP_CITY_MASS_NOT_BUILD);
					resp.fail();
					return resp;
				}
				String tempStr = params.get(4);
				massTime = Integer.parseInt(tempStr);
			}
			RoleArmyAgent armyAgent = city.getCityArmys();
			List<ArmyInfo> armys = new ArrayList<ArmyInfo>();
			Map<String,String> poses = new HashMap<String, String>();
			String[] ss = soldiers.split(",");
			int needNum = 0;
			try {
				for (int i = 0 ; i  < ss.length ; i+=4){
					String armyKey = ss[i];
					int armyNum = Integer.parseInt(ss[i + 1]);
					needNum += armyNum;
					ArmyInfo army = armyAgent.createArmy(armyKey,armyNum,ArmyState.ARMY_IN_NORMAL.getValue());
					armys.add(army);
					poses.put(armyKey,ss[i+2] + "," + ss[i+3]);
				}
			} catch (Exception e) {				
				e.printStackTrace();
				resp.fail();
				return resp;
			}
			int maxNum = city.getMaxOutBattleAllNum();
			if (needNum > maxNum){//单次出征的部队超过上限。
				resp.fail();
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_EXPEDITE_NUM_ONE,maxNum);
				return resp;
			}
			if (role.getExpediteCurNum() >= role.getExpediteMaxNum(city.getId())){//出征部队数量以达上限
				resp.fail();
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_EXPEDITE_NUM);
				return resp;
			}
			if (type != 4 && !armyAgent.checkArmysOut(armys)){
				//侦查不需要判断兵力
				MessageSendUtil.sendNormalTip(info,I18nGreeting.MSG_ROLE_NO_SOLDIER);
				resp.fail();
				return resp;
			}
			expedite = mapCity.tryToMove(role,armys,poses,target,type,massTime);
			if (expedite == null && type != 6){
				resp.fail();
			}
			if (resp.isSucc() && type != 4){
				armyAgent.updateArmysState(ArmyState.ARMY_OUT_BATTLE.getValue(),armys);//修改城里士兵状态
				armyAgent.sendToClient(rms,city);//下发城里士兵状态
			}
		}else if (startCell.getTypeKey() == MapFortress.class || startCell.getTypeKey() == MapBarracks.class ){//要塞和兵营出发
			MapFortress fortress = (MapFortress)startBuild;
			long troopId = Long.parseLong(soldiers);
			if (fortress.isMyMaster(info.getUid())){
				expedite = fortress.tryToMove(role,target,type,troopId);
				if (expedite == null){
					resp.fail();
				}
			}else{
				GameLog.error("出征失败");
				resp.fail();
			}
		}else{
			resp.fail();
		}
		if (resp.isSucc()){//出征成功
			role.handleEvent(GameEvent.TROOPS_SEND);//通知攻击方
			role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);//联盟战斗变化
			if (needStamina > 0){
				role.getRoleStamina().updateCurStamina((short)-needStamina);
			}
			if (type == 0){//建造要塞
				String fortressName = params.get(5);
				if (targetCell.getTypeKey() == MapGarrison.class){
					MapGarrison mapGarrison = mapWorld.searchObject(targetCell);
					if (mapGarrison != null){
						mapGarrison.setFortressName(fortressName);
					}
				}else if (targetCell.getTypeKey() == MapProxy.class){
					MapProxy proxy = mapWorld.searchObject(target);
					if (proxy != null){
						proxy.setName(fortressName);
					}
				}
			}else if (type == 1){//建造迁城点
				MapProxy proxy = mapWorld.searchObject(targetCell);
				proxy.setMoveKey(BuildName.MAP_BASE.getKey() + city.getCityCenterLevel());
			}
			//需要扣资源的地方
			if (type <= 1){//建造要塞、迁城点消耗的资源
				List<Object> costRes = city.redCostResource(wbl.getBuildCostList(),costMoney,"fortressAnd");
				role.sendResourceToClient(false,rms,city.getId(),costRes.toArray());
			}
			if (type == 4){//侦查消耗粮食
				city.redResource(ResourceTypeConst.RESOURCE_TYPE_FOOD,spyNeedFood);
				role.sendResourceToClient(false,rms,city.getId(),ResourceTypeConst.RESOURCE_TYPE_FOOD,-spyNeedFood);
				ResourceTypeConst resource = ResourceTypeConst.RESOURCE_TYPE_FOOD;
				LogManager.itemConsumeLog(role, spyNeedFood, "investConsumption", resource.getKey());
			}
			
			//如果处于保护状态攻击别人,自动消除保护状态
			boolean needRemoveCityNowar = false;
			if (targetObj != null){
				if (targetObj instanceof MapResource || targetObj instanceof MapBarracks){
					needRemoveCityNowar = expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT && targetObj.getInfo().getUid() > 0;
				}else if (targetObj instanceof MapMonster){//去集结
					needRemoveCityNowar = false;
				}else {
					if ((expedite != null && expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT) || type == 6 || type == 7){
						needRemoveCityNowar = true;
					}
				}
			}
			if (needRemoveCityNowar){
				MapCity mc = mapWorld.searchMapCity(info.getUid(),startBuild.getInfo().getCityId());
				if (mc.getCityState().isNowar()){
					mc.getCityState().updateTimer(TimerLastType.TIME_CITY_NOWAR);
					city.sendCityStateToClient(role,rms);
				}
			}
			MessageSendUtil.sendModule(rms,info);
			//通知防御方,有人攻击你
			if (type != 6 && targetObj != null){
				long targetUid = targetObj.getInfo().getUid();
				List<GarrisonTroops> garrisons = targetObj.getDefencers();
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					long uid = garrison.getTroops().getInfo().getUid();
					Role gRole = world.getOnlineRole(uid);
					if (gRole != null) {
						gRole.handleEvent(GameEvent.TROOPS_SEND);
					}
					expedite.addLook(uid);
				}
				if (targetUid > 0){
					expedite.addLook(targetUid);
					Role tRole = world.getRole(targetUid);
					if (tRole != null) {
						tRole.handleEvent(GameEvent.TROOPS_SEND);
						tRole.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);// 联盟战斗变化
					}
				}
			}
			if (expedite != null){//联盟防御塔锁定
				List<MapUnionDefenderTower> towers = world.getListObjects(MapUnionDefenderTower.class);
				for (int i = 0 ; i < towers.size() ; i++){
					MapUnionDefenderTower tower = towers.get(i);
					tower.tryToLock(expedite);
				}
			}
		}
		// byte 0建造要塞,1建造迁城点,2其他,3驻防,4侦查,5调拨,6集结,7去集结,8联盟采集,9去副本
		String event = null;
		switch (type) {
		case 0:
			event = "buildFortress";
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.BUILD_STRONG_HOLD);
			break;
		case 1:
			event = "buildCity";
			try {
				PointVector point = MapUtil.getPointVector(expedite.getTargetPosition());
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
				NewLogManager.mapLog(role, "move_city", (int) point.x, (int) point.y, newStr);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}

			break;
		case 2:
			event = "others";
			break;
		case 3:
			event = "garrison";
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.GARRISON);
			break;
		case 4:
			event = "spy";
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.SPY);
			break;
		case 5:
			event = "allocation";
			break;
		case 6:
			event = "aggregation";
			NewLogManager.mapLog(role, "teamattack");
			break;
		case 7:
			event = "toAggregation";
//			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ASSEMBLE);
			break;
		case 8:
			event = "collection";
			break;
		case 9:
			event = "toEctype";
			break;
		default:
			break;
		}
		long id = 0;
		if (expedite == null) {
			id = -1;
		} else {
			id = expedite.getId();
		}
		LogManager.mapLog(role, startPos, target, id, event);
		return resp;
	}
	
	public static void main(String[] args) {
		System.out.println(TimeUtils.getTime("2016-08-19 00:00:00").getMillis());
	}
}
