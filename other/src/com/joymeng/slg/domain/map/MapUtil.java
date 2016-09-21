package com.joymeng.slg.domain.map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.map.data.Droppool;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Resourcefield;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.FightReport;
import com.joymeng.slg.domain.map.fight.result.FightResutTemp;
import com.joymeng.slg.domain.map.fight.result.FightVersus;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.fight.result.ReportTroopsType;
import com.joymeng.slg.domain.map.fight.result.RoundData;
import com.joymeng.slg.domain.map.fight.result.SkillInfo;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.res.EffectListener;
import com.joymeng.slg.domain.map.impl.still.res.MapResource;
import com.joymeng.slg.domain.map.impl.still.res.ResourceCollecter;
import com.joymeng.slg.domain.map.impl.still.role.MapBarracks;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.role.MapGarrison;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionOther;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PixelVector;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.map.spyreport.SpyReport;
import com.joymeng.slg.domain.map.spyreport.data.SpyContentType;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.RoleArmyAgent;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.data.Radar;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionRecords;
import com.joymeng.slg.world.GameConfig;

public class MapUtil implements Instances{
	
	public static PointVector getPointVector(int position){
		int row = PointVector.getY(position);
		int col = PointVector.getX(position);
		return new PointVector(col,row);
	}
	
	public static PointVector getPointVectorF(float position){
		return getPointVector((int)position);
	}
	
	public static float computePointsDistance(int pos1,int pos2) {
		PointVector p1 = getPointVector(pos1);
		PointVector p2 = getPointVector(pos2);
		return p1.distance(p2);
	}
	
	public static String getStrPosition(int position){
		PointVector pv =getPointVector(position);
		int x =(int)pv.x;
		int y =(int)pv.y;
		String str =x+","+y;
		return str;
	}
	
	public static int getIntPosition(String position) {
		String[] str = position.split(":");
		int x, y, pos;
		if (str.length != 2) {
			String[] st = position.split("：");
			x = Integer.valueOf(st[0]);
			y = Integer.valueOf(st[1]);
			pos = PointVector.getPosition(x, y);
		} else {
			x = Integer.valueOf(str[0]);
			y = Integer.valueOf(str[1]);
			pos = PointVector.getPosition(x, y);
		}
		return pos;
	}
	
	
	public static PointVector getPointVector(PixelVector vector){
		int x = (int)(vector.x / GameConfig.MAP_CELL_WIDTH - vector.y / GameConfig.MAP_CELL_HEIGHT);
	    int y = -(int)(vector.x / GameConfig.MAP_CELL_WIDTH + vector.y / GameConfig.MAP_CELL_HEIGHT);
	    return new PointVector(x,y);
	}
	
	public static PixelVector getPixelVector(int position){
		PointVector point = getPointVector(position);
		return getPixelVector(point);
	}
	
	public static PixelVector getPixelVector(PointVector point){
		float x = (point.x - point.y) *  GameConfig.MAP_CELL_WIDTH / 2;
		float y = -(point.x + point.y) *  GameConfig.MAP_CELL_HEIGHT / 2;
		return new PixelVector(x,y);
	}
	
	public static float[] computeLineParams(int start , int end){
		float[] results = new float[2];
		PointVector sv = getPointVector(start);
		PointVector ev = getPointVector(end);
		//直线方程  y = a * x + b;
		results[0]  = (ev.y - sv.y) / (ev.x - sv.x);//斜率
		results[1]  = (sv.y * ev.x - ev.y * sv.x) / (ev.x - sv.x);
		return results;
	}
	
	/**
	 * 计算直线与它经过的格子的交点坐标
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<PointVector> computeLinePoints(int start , int end){
		List<PointVector> pixels = new ArrayList<PointVector>();
		final PointVector sv = getPointVector(start);
		PointVector ev = getPointVector(end);
		pixels.add(sv);//加入开始点
		pixels.add(ev);//加入结束点
		int left  = (int)Math.min(sv.x,ev.x);
		int right = (int)Math.max(sv.x,ev.x);
		int up    = (int)Math.min(sv.y,ev.y);
		int down  = (int)Math.max(sv.y,ev.y);
		if (sv.x == ev.x){//x = b;
			for (int row = up ; row < down ; row++){
				PointVector pv1 = getPointVectorF(row * GameConfig.MAP_WIDTH + sv.x);
				PointVector pv2 = getPointVectorF((row + 1) * GameConfig.MAP_WIDTH + sv.x);
				PointVector pvc = pv1.center(pv2);
				pixels.add(pvc);
			}
		}else if (sv.y == ev.y) {// y == b;
			for (int col = left ; col < right ; col++){
				PointVector pv1 = getPointVectorF(sv.y * GameConfig.MAP_WIDTH + col);
				PointVector pv2 = getPointVectorF(sv.y * GameConfig.MAP_WIDTH + col + 1);
				PointVector pvc = pv1.center(pv2);
				pixels.add(pvc);
			}
		}else{//y = ax + b;
			float[] params = computeLineParams(start,end);
			for (int col = left + 1 ; col < right ; col++){
				float x = col;
				float y = params[0] * x + params[1];//y = ax + b;
				PointVector pixel = new PointVector(x,y);
				pixels.add(pixel);
			}
			for (int row = up + 1 ; row < down ; row++){
				float y = row;
				float x = (y - params[1]) / params[0];// x = (y-b)/a;
				PointVector pixel = new PointVector(x,y);
				pixels.add(pixel);
			}
		}
		//按离开始点距离最近来排序
		Collections.sort(pixels,new Comparator<PointVector>() {
			@Override
			public int compare(PointVector o1, PointVector o2) {
				return o1.distance(sv) > o2.distance(sv) ? 1 : -1;
			}
		});
		return pixels;
	}
	
	public static float computeSpeed(int start , int end ,long time){
		PixelVector spv = getPixelVector(start);
		PixelVector epv = getPixelVector(end);
		return  spv.distance(epv) / time;
	}
	
	public static long computeCastTime(int start , int end,float speed){
		List<PointVector> pixels = MapUtil.computeLinePoints(start,end);
		float time = 0;//行军需要的时间
		if (pixels != null){
			for (int i = 0 ; i < pixels.size() - 1 ; i++){
				PointVector cp =  pixels.get(i);
				PointVector np =  pixels.get(i+1);
				PointVector pp = cp.center(np);//两点的中心点
				MapCell cell   = mapWorld.getMapCell(pp.getPosition());
				float s = speed;
				if (cell.isSlow()){//减速
					s *= GameConfig.MAP_SPEED_SLOW;
				}
				float des = cp.distance(np);
				time += des / s;
			}
		}
		return (long)time;
	}
	
	/**
	 * 
	* @Title: computeFightResult 
	* @Description: 计算战斗结果
	* 
	* @return Map<Integer,FightResutTemp>
	* @param troopses
	* @param troopsData
	* @param dieRate
	* @return
	 */
	public static Map<Integer,FightResutTemp> computeFightResult(List<FightTroops> troopses , TroopsData troopsData,float dieRate){
		Map<Integer,FightResutTemp> result = new HashMap<Integer,FightResutTemp>();
		for (int i = 0 ; i <  troopses.size(); i++){
			FightTroops fightTroops =  troopses.get(i);
			int dieAll = fightTroops.getDieNum();
			int injurieNum = 0;
			int die = (int)(dieAll * dieRate);
			ArmyEntity entity = troopsData.search(fightTroops.getFightId());
			if (entity == null){
				continue;
			}
			FightResutTemp frt = new FightResutTemp(fightTroops,entity.getSane(),entity.getInjurie(),entity.getDied());
			result.put(fightTroops.getFightId(),frt);
			int newNormalNum  = entity.getSane() - die;
			newNormalNum = Math.max(0,newNormalNum);
			int newInjurieNum = entity.getInjurie() + injurieNum;
			int newDieNum     = entity.getDied() + die;
			entity.setSane(newNormalNum);
			entity.setInjurie(newInjurieNum);
			entity.setDied(newDieNum);
			
			Map<String,Integer> thisKills = frt.getKills();
			entity.setMyKills(thisKills);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: computeFightResult 
	* @Description: 计算战斗结果
	* 
	* @return Map<Integer,FightResutTemp>
	* @param troopses
	* @param troopsData
	* @return
	 */
	public static Map<Integer,FightResutTemp> computeFightResult(List<FightTroops> troopses,TroopsData troopsData){
		long uid = troopsData.getInfo().getUid();
		Role role = null;
		if (uid > 0){
			role = world.getRole(uid);
		}
		Map<Integer,FightResutTemp> save = new HashMap<Integer,FightResutTemp>();
		for (int i = 0 ; i <  troopses.size(); i++){
			FightTroops fightTroops =  troopses.get(i);
			//死亡总人数
			int dieAll = fightTroops.getDieNum();
			dieAll = Math.max(0,dieAll);
			//获取伤兵率
			float injurieRate = role == null ? 0 : role.getFightInjurieRate(troopsData.getInfo().getCityId(),fightTroops.getAttribute().getArmyType());
			//得到可医疗的人数
			int injurieNum = Math.round(dieAll * injurieRate);
			injurieNum = Math.max(0,injurieNum);
			//最终死亡人数
			int die = dieAll - injurieNum;
			die = Math.max(0,die);
			//兵种实体
			ArmyEntity entity = troopsData.search(fightTroops.getFightId());
			if (entity == null){
				continue;
			}
			int newNormalNum  = entity.getSane() - dieAll;
			//最终存活人数
			newNormalNum = Math.max(0,newNormalNum);
			//最终医疗人数
			int newInjurieNum = entity.getInjurie() + injurieNum;
			//最终死亡人数
			int newDieNum     = entity.getDied() + die;
			FightResutTemp frt = new FightResutTemp(fightTroops,newNormalNum,entity.getInjurie(),entity.getDied());
			save.put(fightTroops.getFightId(),frt);
			entity.setSane(newNormalNum);
			entity.setInjurie(newInjurieNum);
			entity.setDied(newDieNum);
			
			Map<String,Integer> thisKills = frt.getKills();
			entity.setMyKills(thisKills);
		}
		return save;
	}
	
	public static Map<String,FightResutTemp> computeFightResult(List<FightTroops> troopses,Role role,int cityId,
			RoleArmyAgent armyAgent,List<RoleBuild> defenceBuilds,List<ArmyInfo> armys,boolean dieFlag) {
		Map<String,FightResutTemp> result = new HashMap<String,FightResutTemp>();
		List<ArmyInfo> injuries = new ArrayList<ArmyInfo>();
		List<ArmyInfo> dies = new ArrayList<ArmyInfo>();
		for (int i = 0 ; i <  troopses.size(); i++){
			FightTroops fightTroops =  troopses.get(i);
			String armyId = fightTroops.getAttribute().getName();
			Army armyData = dataManager.serach(Army.class,armyId);
			boolean needContinue = false;
			//防御型建筑
			for (int j = 0 ; j <  defenceBuilds.size(); j++){
				RoleBuild build =  defenceBuilds.get(j);
				BuildComponentDefense defenseComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
				String defensekey = defenseComponent.getArmyId();
				if (defensekey != null && defensekey.equals(armyId)){
					needContinue = true;
					int a = fightTroops.getAttribute().getbHp() + fightTroops.getAttribute().getcHp();
					int change = fightTroops.getAttribute().getHp() - a;
					int pre  = defenseComponent.getCurrentDefenceHP();
					defenseComponent.change(change);//战斗后设置防御型建筑的耐久
					int now = defenseComponent.getCurrentDefenceHP();
					FightResutTemp frt = new FightResutTemp(fightTroops,1,pre,now);
					result.put(armyId + "@" + build.getId() , frt);
					break;
				}
			}
			if (needContinue){
				continue;
			}
			ArmyInfo preArmy = null;
			for (int j = 0 ; j <  armys.size(); j++){
				ArmyInfo info = armys.get(j);
				if (info.getArmyId().equals(armyId)){
					preArmy = info;
					break;
				}
			}
			float injurieRate = dieFlag ? role.getFightInjurieRate(cityId,fightTroops.getAttribute().getArmyType()) : 0;
			if (armyData.getArmyType() == ArmyType.HOOK.ordinal()){//陷阱
				injurieRate = 0;
			}
			int dieAll = dieFlag ? fightTroops.getDieNum() : 0;
			int injurieNum = Math.round(dieAll * injurieRate);
			int dieNum = (dieAll - injurieNum);
			int alive = preArmy.getArmyNum() - dieAll;
			if (injurieNum > 0){
				ArmyInfo info = armyAgent.createArmy(armyId,injurieNum,ArmyState.ARMY_IN_NORMAL.getValue());
				injuries.add(info);
			}
			if (dieNum > 0){
				ArmyInfo info = armyAgent.createArmy(armyId,dieNum,ArmyState.ARMY_IN_NORMAL.getValue());
				dies.add(info);
			}
			FightResutTemp frt = new FightResutTemp(fightTroops,alive,injurieNum,dieNum);
			result.put(armyId,frt);
		}
		if (injuries.size() > 0){
			armyAgent.updateArmysState(ArmyState.ARMY_IN_HOSPITAL.getValue(),injuries);
		}
		if (dies.size() > 0){
			armyAgent.updateArmysState(ArmyState.ARMY_DIED.getValue(),dies);
		}
		return result;
	}
	
	public static float computeMoveSpeed(List<String> result, Role role, int cityId){
		float minSpeed = Float.MAX_VALUE;
		for (int i = 0 ; i <  result.size(); i++){
			String armyId =  result.get(i);
			Army armyBase = dataManager.serach(Army.class,armyId);
			float buff    = 0;
			if (role != null){
				buff = role.getExpediteSpeedEffect(cityId, armyId);
			}
			float speed = armyBase.getSpeed() * (1 + buff);
			minSpeed = Math.min(speed,minSpeed);
		}
		return minSpeed*5;
	}
	
	private static void computeExpedites(int position,List<ExpediteTroops> expedites){
		MapCell cell = mapWorld.getMapCell(position);
		List<Long> expediteIds = cell.getExpedites();
		if (expediteIds != null){
			for (int i = 0 ; i < expediteIds.size() ; i++){
				Long expediteId = expediteIds.get(i);
				ExpediteTroops expedite = world.getObject(ExpediteTroops.class,expediteId.longValue());
				if (expedite != null && !expedites.contains(expedite)){
					expedites.add(expedite);
				}
			}
		}
	}
	
	public static GarrisonTroops searchGarrisons(int position , long id){
		MapCell cell = mapWorld.getMapCell(position);
		List<Long> garrisonIds = cell.getGarrisons();
		if (garrisonIds != null){
			for (int i = 0 ; i < garrisonIds.size() ; i++){
				long garrisonId = garrisonIds.get(i);
				if (garrisonId == id){
					return world.getObject(GarrisonTroops.class,garrisonId);
				}
			}
		}
		return null;
	}
	
	public static List<GarrisonTroops> computeGarrisons(MapCell cell){
		List<GarrisonTroops> result = new ArrayList<GarrisonTroops>();
		if (cell == null){
			return result;
		}
		synchronized (cell) {
			List<Long> garrisonIds = cell.getGarrisons();
			if (garrisonIds != null){
				for (int i = 0 ; i < garrisonIds.size() ;){
					long garrisonId = garrisonIds.get(i);
					GarrisonTroops garrison = world.getObject(GarrisonTroops.class,garrisonId);
					if (garrison == null){
						garrisonIds.remove(i);
						continue;
					}
					result.add(garrison);
					i++;
				}
			}
		}
		return result;
	}
	
	public static List<GarrisonTroops> computeGarrisons(int position){
		MapCell cell = mapWorld.getMapCell(position);
		return computeGarrisons(cell);
	}
	
	public static void searchDatas(List<Integer> indexs,List<MapObject> objs,List<ExpediteTroops> expedites){
		for (int i = 0 ; i  < indexs.size() ; i++){
			Integer index = indexs.get(i);
			MapObject obj = mapWorld.searchObject(index.intValue());
			if (obj != null && !obj.cellType().isProxy()){
				objs.add(obj);
			}
			computeExpedites(index.intValue(),expedites);
		}
	}
	
	public static void drop(String dropId,Map<Byte,Map<String,Integer>> packages){
		Droppool dropData = dataManager.serach(Droppool.class,dropId);
		if (dropData != null){
			//先处理比固定掉落
			List<String> mustDrop = dropData.getFixedreward();
			for (int i = 0 ; i < mustDrop.size() ; i++){
				String dropStr = mustDrop.get(i);
				DropData itemInfo = new DropData(dropStr,true);
				addToPackage(itemInfo,packages);
			}
			List<String> dropNums = dropData.getNumberWeight();
			int[] rates = new int[dropNums.size()];
			String[] nums  = new String[dropNums.size()];
			for (int i = 0 ; i < dropNums.size() ; i++){
				String dns = dropNums.get(i);
				String[] ss = dns.split(":");
				nums[i]  = ss[0];
				rates[i] = Integer.parseInt(ss[1]);
			}
			String numStr = MathUtils.getRandomObj(nums,rates);
			int num = Integer.parseInt(numStr);
			List<DropData> itemInfos = new ArrayList<DropData>();
			List<String> dropItems = dropData.getItemWeight();
			for (int i = 0 ; i < dropItems.size() ; i++){
				String item = dropItems.get(i);
				DropData itemInfo = new DropData(item,false);
				itemInfos.add(itemInfo);
			}
			while (num > 0){
				int[] dRates = new int[itemInfos.size()];
				DropData[] temps = new DropData[itemInfos.size()];
				for (int i = 0 ; i < itemInfos.size() ; i++){
					DropData temp = itemInfos.get(i);
					temps[i] = temp;
					dRates[i] = temp.rate;
				}
				DropData itemInfo = MathUtils.getRandomObj(temps,dRates);
				itemInfo.max --;
				if (itemInfo.max <= 0){
					itemInfos.remove(itemInfo);
				}
				addToPackage(itemInfo,packages);
				num--;
			}
		}
	}
	
	public static void drop(Monster monster,ExpediteTroops expedite){
		String dropId = monster.getDroplist();
		drop(dropId,expedite.getLeader().getPackages());
	}
	
	/**
	 * 怪物掉落日志记录
	 */
	public static void recordDropItems(Role role, String key, Map<Byte, Map<String, Integer>> packages) {
		if (packages == null || packages.size() == 0) {
			return;
		}
		for (Byte bt : packages.keySet()) {
			Map<String, Integer> pack = packages.get(bt);
			if (pack == null) {
				continue;
			}
			for (String str : pack.keySet()) {
				LogManager.pveLog(role, EventName.RebelForces.getName(), key, (byte) 1, EventName.RebelForces.getName(), str, pack.get(str));
			}
		}
	}
	
	public static void addSomethingToPackage(ExpeditePackageType type ,String key,int num,Map<Byte,Map<String,Integer>> packages){
		if (type == null){
			return;
		}
		Map<String,Integer> reses = packages.get(type.getType());
		if (reses == null){
			reses = new HashMap<String, Integer>();
			packages.put(Byte.valueOf(type.getType()),reses);
		}
		int newNum = num;
		if (reses.containsKey(key)){
			newNum += reses.get(key).intValue();
		}
		reses.put(key,newNum);
	}
	
	private static void addToPackage(DropData itemInfo,Map<Byte,Map<String,Integer>> packages){
		ResourceTypeConst type = ResourceTypeConst.search(itemInfo.type);
		if (type == null){
			GameLog.error("策划掉落类型配置错误");
			return;
		}
		int num = itemInfo.fix ? itemInfo.rate : 1;
		ExpeditePackageType et = null;
		String key = itemInfo.key;
		if (type.ordinal() <= ResourceTypeConst.RESOURCE_TYPE_ALLOY.ordinal()){
			num = Integer.parseInt(itemInfo.key);
			key = itemInfo.type;
			et = ExpeditePackageType.PACKAGE_TYPE_RESOURCE;
		}else if (type.ordinal() == ResourceTypeConst.RESOURCE_TYPE_GOLD.ordinal()){
			et = ExpeditePackageType.PACKAGE_TYPE_GOLD;
			num = Integer.parseInt(itemInfo.key);
		}else if (type.ordinal() == ResourceTypeConst.RESOURCE_TYPE_ITEM.ordinal()){
			et = ExpeditePackageType.PACKAGE_TYPE_GOODS;
		}else if (type.ordinal() == ResourceTypeConst.RESOURCE_TYPE_EQUIP.ordinal()){
			et = ExpeditePackageType.PACKAGE_TYPE_EQUIP;
		}else if (type.ordinal() == ResourceTypeConst.RESOURCE_TYPE_MATERIAL.ordinal()){
			et = ExpeditePackageType.PACKAGE_TYPE_STONE;
		}else{
			GameLog.error("错误的掉落类型");
		}
		addSomethingToPackage(et,key,num,packages);
	}
	
	static class DropData{
		
		public DropData(String str,boolean fix){
			String[] ss = str.split(":");
			type  = ss[0];
			key   = ss[1];
			rate  = Integer.parseInt(ss[2]);
			if (ss.length > 3){
				max   = Integer.parseInt(ss[3]);
			}
			this.fix = fix;
		}
		
		boolean fix;
		String type;
		String key;
		int max;
		int rate;
	}
	
	private static List<String> computeFightInfo(Map<Integer,FightResutTemp> before,TroopsData troops,int[] nums){
		List<String> result = new ArrayList<String>();
		for (int i = 0 ; i < troops.getArmys().size() ; i++){
			ArmyEntity army = troops.getArmys().get(i);
			int aId = army.getId();
			if (aId == -1){
				continue;
			}
			FightResutTemp ftr = before.get(aId);
			int preInju = ftr.getInjurie();
			int preDie  = ftr.getDie();
			int inju   = army.getInjurie() - preInju;
			int die    = army.getDied() - preDie;
			nums[0] += army.getSane();//正常的
			nums[1] += inju;//损伤
			nums[2] += die;//死亡
			String asi = army.getKey() + "|" + army.getSane() + "|" + die + "|" + inju + "|" + ftr.getKill();
			result.add(asi);
		}
		return result;
	}
	
	/**
	 * 触发杀兵活动事件
	 * @param role
	 * @param frts
	 */
	public static void triggerAE_kill_soldier(Role role,Collection<FightResutTemp> frts){
		Map<String,Integer> kills = new HashMap<String,Integer>();
		for (FightResutTemp frt : frts){
			for (String key : frt.getKills().keySet()){
				int kill = frt.getKills().get(key).intValue();
				if (kills.containsKey(key)){
					kill += kills.get(key).intValue();
				}
				kills.put(key,kill);
			}
		}
		for (String aKey : kills.keySet()){
			int kill = kills.get(aKey).intValue();
			role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.KILL_SOLDIER,aKey,kill);//活动杀兵事件
		}
	}
	
	public static void triggerAE_kill_monster(Role role,int monsterLevel,Collection<FightResutTemp> frts){
		Map<String,Integer> kills = new HashMap<String,Integer>();
		for (FightResutTemp frt : frts){
			for (String key : frt.getKills().keySet()){
				int kill = frt.getKills().get(key).intValue();
				if (kills.containsKey(key)){
					kill += kills.get(key).intValue();
				}
				kills.put(key,kill);
			}
		}
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.KILL_NPC,monsterLevel);
	}
	
	public static void triggerAE_monster_act_role(Role role,boolean isWin,Collection<FightResutTemp> frts){
		Map<String,Integer> allKills = new HashMap<String, Integer>();
		for (FightResutTemp frt : frts){
			Map<String,Integer> kills = frt.getKills();
			for (String key : kills.keySet()){
				Integer value =  kills.get(key);
				if (allKills.containsKey(key)){
					value += allKills.get(key);
				}
				allKills.put(key,value);
			}
		}
		role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.ARMY_REBELL_OVER,isWin,allKills);
	}
	
	public static FightReport createRelicReport(Map<Integer,FightResutTemp> preAttackerInfos, Map<Integer,FightResutTemp> preDefencerInfos,BattleField battle,
			TroopsData afterAttacker,TroopsData afterDefencer,int position,boolean isWin,BattleRecord record){
		int[] nums1 = new int[6];
		int[] nums2 = new int[6];
		List<String> armyInfo1 = computeFightInfo(preAttackerInfos,afterAttacker,nums1);
		List<String> armyInfo2 = computeFightInfo(preDefencerInfos,afterDefencer,nums2);
		nums1[3] = Math.max(0,nums1[0]);
		nums2[3] = Math.max(0,nums2[0]);
		nums1[0] = nums2[1] + nums2[2];
		nums2[0] = nums1[1] + nums1[2];
		List<String> fightResult1 = new ArrayList<String>();
		List<String> fightResult2 = new ArrayList<String>();
		for (int i = 0 ; i <= ReportTroopsType.TROOPS_TYPE_BUILD_HP.ordinal() ; i++){
			ReportTroopsType rtt = ReportTroopsType.search(i);
			insert(fightResult1,rtt,nums1[i]);
			insert(fightResult2,rtt,nums2[i]);
		}
		List<SkillInfo> starts = record.getPrepares();
		List<RoundData> rounds = record.reportRoundDatas();
		FightVersus people1 = new FightVersus();
		people1.copy(afterAttacker.getInfo());
		FightVersus people2 = new FightVersus();
		people2.copy(afterDefencer.getInfo());
		FightReport report = new FightReport();
		report.setPosition(position);
		report.setResult(isWin ? 1 : 0);
		report.setTime(TimeUtils.nowStr());
		report.setTitle(ReportTitleType.TITLE_TYPE_ECTYPE);
		report.setPeople1(people1);
		report.setPeople2(people2);
		report.setPeopleSide(Side.ATTACK);
		report.setFightResult1(fightResult1);
		report.setFightResult2(fightResult2);
		battle.reportEff(report,Side.ATTACK);
		report.setRounds(rounds);
		report.setStarts(starts);
		report.setArmyInfo1(armyInfo1);
		report.setArmyInfo2(armyInfo2);
		for(ArmyEntity entity: afterAttacker.getArmys()){
			report.addMyKills(entity.getMyKills());
		}
		return report;
	}
	
	public static void report(ExpediteTroops expedite,TroopsData p2,int position,boolean isWin,
			BattleField battle, Map<Integer,FightResutTemp> attackerResult, Map<Integer,FightResutTemp> defenderResult,
			BattleRecord record,ReportTitleType title1,ReportTitleType title2){
		for (int i = 0 ; i < expedite.getTeams().size() ; i++){
			TroopsData attacker = expedite.getTeams().get(i);
			report(expedite,attacker,p2,position,isWin,battle,attackerResult,defenderResult,record,title1,title2,expedite.isMass());
		}
	}
	
	public static void report(ExpediteTroops expedite,TroopsData p1,TroopsData p2,int position,boolean isWin,
			BattleField battle, Map<Integer,FightResutTemp> attackerResult, Map<Integer,FightResutTemp> defenderResult,
			BattleRecord record,ReportTitleType title1,ReportTitleType title2, boolean isMass){
		int[] nums1 = new int[6];
		int[] nums2 = new int[6];
		List<String> armyInfo1 = computeFightInfo(attackerResult,p1,nums1);
		List<String> armyInfo2 = computeFightInfo(defenderResult,p2,nums2);
		nums1[3] = Math.max(0,nums1[0]);
		nums2[3] = Math.max(0,nums2[0]);
		nums1[0] = nums2[1] + nums2[2];
		nums2[0] = nums1[1] + nums1[2];
		List<String> fightResult1 = new ArrayList<String>();
		List<String> fightResult2 = new ArrayList<String>();
		for (int i = 0 ; i <= ReportTroopsType.TROOPS_TYPE_BUILD_HP.ordinal() ; i++){
			ReportTroopsType rtt = ReportTroopsType.search(i);
			insert(fightResult1,rtt,nums1[i]);
			insert(fightResult2,rtt,nums2[i]);
		}
		List<SkillInfo> starts = record.getPrepares();
		List<RoundData> rounds = record.reportRoundDatas();
		long uid1 = p1.getInfo().getUid();
		long uid2 = p2.getInfo().getUid();
		FightVersus people1 = new FightVersus();
		people1.copy(p1.getInfo());
		FightVersus people2 = new FightVersus();
		people2.copy(p2.getInfo());
		if (uid1 > 0){//给用户
			Role role = world.getRole(uid1);
			FightReport report = new FightReport();
			report.setPosition(position);
			report.setResult(isWin ? 1 : 0);
			report.setTime(TimeUtils.nowStr());
			report.setTitle(title1);
			report.setPeople1(people1);
			report.setPeople2(people2);
			report.setPeopleSide(Side.ATTACK);
			report.setFightResult1(fightResult1);
			report.setFightResult2(fightResult2);
			battle.reportEff(report,Side.ATTACK);
			report.setRounds(rounds);
			report.setStarts(starts);
			report.setArmyInfo1(armyInfo1);
			report.setArmyInfo2(armyInfo2);
			for(ArmyEntity entity: p1.getArmys()){
				report.addMyKills(entity.getMyKills());
			}
			//String battleReport = JsonUtil.ObjectToJsonString(report);
			//chatMgr.creatBattleReportAndSend(battleReport,ReportType.TYPE_BATTLE_REPORT,null,role);
			expedite.addReport(report);
			//任务事件
			if(title1 == ReportTitleType.TITLE_TYPE_A_P_CITY && title2 == ReportTitleType.TITLE_TYPE_D_P_CITY){
				byte fightType = Const.ATK_CY;
				if(isMass){
					if (p1.isLeader()){
						fightType = Const.MS_ATK;
					}else{
						fightType = Const.MS_HLP_ATK;
					}
				}
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,fightType,nums1[0],nums1[2]);
			}else if(title2 == null){//攻击NPC
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,Const.ATK_MST, 0, nums1[2]);
			}else{
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,Const.OTHER_F, nums1[0], nums1[2]);
			}
		}
		if (uid2 > 0){//给用户
			Role role = world.getRole(uid2);
			FightReport report = new FightReport();
			report.setPosition(position);
			report.setResult(isWin ? 0 : 1);
			report.setTime(TimeUtils.nowStr());
			report.setTitle(title2);
			report.setPeople1(people2);
			report.setPeople2(people1);
			report.setPeopleSide(Side.DEFENSE);
			report.setFightResult1(fightResult2);
			report.setFightResult2(fightResult1);
			battle.reportEff(report,Side.DEFENSE);
			report.setRounds(rounds);
			report.setStarts(starts);
			report.setArmyInfo1(armyInfo2);
			report.setArmyInfo2(armyInfo1);
			for(ArmyEntity entity: p2.getArmys()){
				report.addMyKills(entity.getMyKills());
			}
			//String battleReport = JsonUtil.ObjectToJsonString(report);
			//chatMgr.creatBattleReportAndSend(battleReport,ReportType.TYPE_BATTLE_REPORT,null,role);
			expedite.addReport(report);
			//任务事件
			if(title1 == ReportTitleType.TITLE_TYPE_A_P_CITY && title2 == ReportTitleType.TITLE_TYPE_D_P_CITY){
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,Const.DEF_HLP_CY,nums1[0],nums1[2]);
			}else{
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,Const.OTHER_F, nums1[0], nums1[2]);
			}
		}
	}

	public static void report(ExpediteTroops expedite,MapCity mapCity,boolean isWin,
			BattleField battle,BattleRecord record,List<RoleBuild> defenceBuilds,
			Map<Integer,FightResutTemp> attackersResult,Map<String,FightResutTemp> defencerResult,
			RoleCityAgent roleCity) {
		FightVersus city = new FightVersus();
		city.copy(mapCity.getInfo());
		List<String> armyInfo1 = new ArrayList<String>();
		List<String> armyInfo2 = new ArrayList<String>();
		List<String> fightResult1 = new ArrayList<String>();
		List<String> fightResult2 = new ArrayList<String>();
		List<TroopsData> attackers = expedite.getTeams();
		int[] nums1 = new int[6];
		int[] nums2 = new int[6];
		FightVersus leader = null;
		Map<Long,Integer> attackerDies = new HashMap<Long,Integer>();
		for (int i = 0 ; i < attackers.size() ; i++){
			TroopsData attacker = attackers.get(i);
			long auid = attacker.getInfo().getUid();
			if (attacker.isLeader()){
				leader = new FightVersus();
				leader.getInfo().copy(attacker.getInfo());
			}
			for (int j = 0 ; j < attacker.getArmys().size() ; j++){
				ArmyEntity army = attacker.getArmys().get(j);
				int aId = army.getId();
				if (aId == -1){
					continue;
				}
				FightResutTemp frt = attackersResult.get(aId);
				int preInju = frt.getInjurie();
				int preDie  = frt.getDie();
				int nInju = army.getInjurie() - preInju;
				int nDie  = army.getDied() - preDie;
				nums1[1] += nInju;
				nums1[2] += nDie;
				int dieCount = (attackerDies.containsKey(auid) ? attackerDies.get(auid).intValue() : 0) + nDie;
				attackerDies.put(auid,dieCount);
				String asi = army.getKey() + "|" + army.getSane() + "|" + nDie + "|" + nInju + "|" + frt.getKill();
				armyInfo1.add(asi);
			}
			nums1[0] += attacker.getAliveNum();
		}
		Map<String,Integer> deffMaps = new HashMap<String,Integer>();//我击杀的对象
		for (String key : defencerResult.keySet()){
			FightResutTemp frt  = defencerResult.get(key);
			int inju   = frt.getInjurie();
			int die    = frt.getDie();
			int alives = frt.getAlive();
			if (key.contains("@")){//这个防御建筑的
				continue;
			}
			String asi = key + "|" + alives + "|" + die + "|" + inju + "|" + frt.getKill();
			armyInfo2.add(asi);
			Army army = dataManager.serach(Army.class,key);
			if (army.getArmyType() == ArmyType.HOOK.ordinal()){
				nums2[4] += die;//陷阱
			}
			nums2[1] += inju;
			nums2[0] += alives;
			if(frt.getKills() == null)
				continue;
			for(String kill:frt.getKills().keySet()){
				if(deffMaps.get(kill) == null){
					deffMaps.put(kill, frt.getKills().get(kill));
				}else{
					deffMaps.put(kill, frt.getKills().get(kill)+deffMaps.get(kill));
				}
			}
		}
		for (int i = 0 ; i < defenceBuilds.size() ; i++){
			RoleBuild build = defenceBuilds.get(i);
			long buildId = build.getId();
			BuildComponentDefense defenseComponent = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
			String buildKey = defenseComponent.getArmyId();
			if (buildKey == null){
				continue;
			}
			String key          = buildKey + "@" + buildId;
			FightResutTemp frt  = defencerResult.get(key);
			if (frt == null){
				continue;
			}
			int pre  = frt.getInjurie();//战前血量
			int now  = frt.getDie();//战后血量
			String asi = defenseComponent.getArmyId() + "|1|" + pre + "|" + now + "|" + frt.getKill();
			armyInfo2.add(asi);
			if (now < pre){
				nums2[5] += 1 ;
			}
			
			if(frt.getKills() == null)
				continue;
			for(String kill:frt.getKills().keySet()){
				if(deffMaps.get(kill) == null){
					deffMaps.put(kill, frt.getKills().get(kill));
				}else{
					deffMaps.put(key, frt.getKills().get(key)+deffMaps.get(key));
				}
			}
		}
		nums1[3] = Math.max(0,nums1[0]);
		nums2[3] = Math.max(0,nums2[0]); 
		nums1[0] = nums2[1] + nums2[2];
		nums2[0] = nums1[1] + nums1[2];
		for (int i = 0 ; i <= ReportTroopsType.TROOPS_TYPE_BUILD_HP.ordinal() ; i++){
			ReportTroopsType rtt = ReportTroopsType.search(i);
			insert(fightResult1,rtt,nums1[i]);
			insert(fightResult2,rtt,nums2[i]);
		}
		List<SkillInfo> starts = record.getPrepares();
		List<RoundData> rounds = record.reportRoundDatas();
		if (leader.getInfo().getUid() > 0){
			for (int i = 0 ; i < attackers.size() ; i++){
				TroopsData attacker = attackers.get(i);
				long auid = attacker.getInfo().getUid();
				Role role = world.getRole(auid);
				FightReport report = new FightReport();
				report.setPosition(mapCity.getPosition());
				report.setResult(isWin ? 1 : 0);
				report.setTime(TimeUtils.nowStr());
				report.setTitle(ReportTitleType.TITLE_TYPE_A_P_CITY);
				report.setMass(expedite.isMass());
				FightVersus p1 = null;
				if (attacker.isLeader()){
					p1 = leader;
				}else{
					p1 = new FightVersus();
				}
				p1.copy(attacker.getInfo());
				report.setPeople1(p1);
				report.setPeople2(city);
				report.setPeopleSide(Side.ATTACK);
				report.setFightResult1(fightResult1);
				report.setFightResult2(fightResult2);
				report.setArmyInfo1(armyInfo1);
				report.setArmyInfo2(armyInfo2);
				battle.reportEff(report,Side.ATTACK);
				report.setRounds(rounds);
				report.setStarts(starts);
				for(ArmyEntity entity: attacker.getArmys()){
					report.addMyKills(entity.getMyKills());
				}
				expedite.addReport(report);
				//任务事件，进攻方报告
				int num = record.getKillsNum(attacker);
				int adie = attackerDies.containsKey(auid) ? attackerDies.get(auid).intValue() : 0;
				byte fightType = Const.ATK_CY;
				if(expedite.isMass()){
					if (adie > 0){
						if(attacker.isLeader()){//自己集结
							fightType = Const.MS_ATK;
							role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT, fightType, num, adie);
						}else{//参与集结
							fightType = Const.MS_HLP_ATK;
							role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT, fightType, num, adie);
						}
					}
				}else{
					role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT,fightType,nums1[0],nums1[2]);
				}
			}
		}
		Role role = world.getRole(mapCity.getInfo().getUid());
		FightReport report = new FightReport(roleCity);
		report.setPosition(mapCity.getPosition());
		report.setResult(isWin ? 0 : 1);
		report.setTime(TimeUtils.nowStr());
		report.setTitle(leader.getInfo().getUid() > 0 ? ReportTitleType.TITLE_TYPE_D_P_CITY : ReportTitleType.TITLE_TYPE_D_MONSTER);
		report.setMass(expedite.isMass());
		report.setPeople1(city);
		report.setPeople2(leader);
		report.setPeopleSide(Side.DEFENSE);
		report.setFightResult1(fightResult2);
		report.setFightResult2(fightResult1);
		battle.reportEff(report,Side.DEFENSE);
		report.setArmyInfo1(armyInfo2);
		report.setArmyInfo2(armyInfo1);
		report.setRounds(rounds);
		report.setStarts(starts);
		report.addMyKills(deffMaps);
		expedite.addReport(report);
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_FIGHT_RESULT, Const.DEF_CY, nums2[0], nums2[2]);
	}
	
	public static void report(ExpediteTroops expedite,boolean isWin,TroopsData npc,
			int position,Map<Integer,FightResutTemp> attackersResult,
			Map<Integer,FightResutTemp> defencerResult,BattleField battle,
			BattleRecord record,Map<Long,Integer> damages){
		ReportTitleType title = expedite.isWin ? ReportTitleType.TITLE_TYPE_A_U_CITY : ReportTitleType.TITLE_TYPE_JUST_FIGHT;
		FightVersus n_v = new FightVersus();
		n_v.copy(npc.getInfo());
		int[] nums1 = new int[6];
		int[] nums2 = new int[6];
		List<String> armyInfo1 = new ArrayList<String>();
		List<String> armyInfo2 = computeFightInfo(defencerResult,npc,nums2);
		List<String> fightResult1 = new ArrayList<String>();
		List<String> fightResult2 = new ArrayList<String>();
		List<TroopsData> attackers = expedite.getTeams();
		for (int i = 0 ; i < attackers.size() ; i++){
			TroopsData attacker = attackers.get(i);
			long auid = attacker.getInfo().getUid();
			for (int j = 0 ; j < attacker.getArmys().size() ; j++){
				ArmyEntity army = attacker.getArmys().get(j);
				int aId = army.getId();
				if (aId == -1){
					continue;
				}
				FightResutTemp frt = attackersResult.get(aId);
				int preInju = frt.getInjurie();
				int preDie  = frt.getDie();
				int nInju = army.getInjurie() - preInju;
				int nDie  = army.getDied() - preDie;
				nums1[1] += nInju;
				nums1[2] += nDie;
				int killNum = frt.getKill();
				String asi = army.getKey() + "|" + army.getSane() + "|" + nDie + "|" + nInju + "|" + killNum;
				armyInfo1.add(asi);
				if (damages.containsKey(auid)){
					killNum += damages.get(auid).intValue();
				}
				damages.put(auid,killNum);
				army.setMyKills(frt.getKills());
			}
			nums1[0] += attacker.getAliveNum();
		}
		nums1[3] = Math.max(0,nums1[0]);
		nums2[3] = Math.max(0,nums2[0]); 
		nums1[0] = nums2[1] + nums2[2];
		nums2[0] = nums1[1] + nums1[2];
		for (int i = 0 ; i <= ReportTroopsType.TROOPS_TYPE_BUILD_HP.ordinal() ; i++){
			ReportTroopsType rtt = ReportTroopsType.search(i);
			insert(fightResult1,rtt,nums1[i]);
			insert(fightResult2,rtt,nums2[i]);
		}
		List<SkillInfo> starts = record.getPrepares();
		List<RoundData> rounds = record.reportRoundDatas();
		for (int i = 0 ; i < attackers.size() ; i++){
			TroopsData attacker = attackers.get(i);
			long auid = attacker.getInfo().getUid();
			Role role = world.getRole(auid);
			FightReport report = new FightReport();
			report.setPosition(position);
			report.setResult(isWin ? 1 : 0);
			report.setTime(TimeUtils.nowStr());
			report.setTitle(title);
			report.setMass(expedite.isMass());
			FightVersus p1 = new FightVersus();
			p1.copy(attacker.getInfo());
			report.setPeople1(p1);
			report.setPeople2(n_v);
			report.setPeopleSide(Side.ATTACK);
			report.setFightResult1(fightResult1);
			report.setFightResult2(fightResult2);
			report.setArmyInfo1(armyInfo1);
			report.setArmyInfo2(armyInfo2);
			battle.reportEff(report,Side.ATTACK);
			report.setRounds(rounds);
			report.setStarts(starts);
			for(ArmyEntity entity: attacker.getArmys()){
				report.addMyKills(entity.getMyKills());
			}
			report.reportAllianceScore();
			String battleReport = JsonUtil.ObjectToJsonString(report);
			chatMgr.creatBattleReportAndSend(battleReport,ReportType.TYPE_BATTLE_REPORT,null,role);
		}
	}
	
	public static void insert(List<String> lis , ReportTroopsType type , int num){
		String str = type + "|" + num;
		lis.add(str);
	}

	public static void spyResport(byte spyType, ExpediteTroops expedite,MapObject mapObject) {
		MapRoleInfo aimInfo = mapObject.getInfo();
		List<GarrisonTroops> garrisonTroops = mapObject.getDefencers();
		SpyReport spyReport = new SpyReport();
		SpyReport bySpiedReport = new SpyReport();
		if (expedite.getTeams().size() < 1 || expedite.getTeams().get(0).getInfo() == null) {
			GameLog.error("get expedite is fail");
			return;
		}
		MapRoleInfo selfInfo = expedite.getLeader().getInfo();
		Role role = world.getRole(selfInfo.getUid());
		RoleCityAgent city = role.getCity(0);
		List<RoleBuild> radar = city.searchBuildByBuildId(BuildName.RADAR.getKey());
		if (radar == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_BUILD_NOT_FIND);
			return;
		}
		Buildinglevel buildinglevel = RoleBuild.getBuildinglevelByCondition(radar.get(0).getBuildId(), radar.get(0).getLevel());
		if (buildinglevel == null || buildinglevel.getParamList().size() < 1) {
			GameLog.error("read base Buildinglevel is fail");
			return;
		}
		Radar radarPower = dataManager.serach(Radar.class, buildinglevel.getId());
		if (radarPower == null) {
			GameLog.error("read base Radar is fail");
			return;
		}
		List<String> spyShowContentIds = radarPower.getScoutfunction();
		List<String> exclusiveSendTip = new ArrayList<>(
				Arrays.asList("9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"));// 包含部队信息
		// String[] spyShowContentIds =
		// buildinglevel.getParamList().get(0).split(":");
		// spyShowContentIds = new
		// String[]{"8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"};
		bySpiedReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
		bySpiedReport.setPosition(expedite.getTargetPosition());
		FightVersus p1 = new FightVersus();
		p1.copy(selfInfo);
		bySpiedReport.setAimRole(p1);
		List<String> contentList = spyReport.getContent();
		// 给出提示 - 是否包含
		boolean isContain = false;
		String data = "";
		for (int i = 0; i < exclusiveSendTip.size(); i++) {
			data = exclusiveSendTip.get(i);
			if (StringUtils.isNull(data)) {
				continue;
			}
			if (spyShowContentIds.contains(data)) {
				isContain = true;
				break;
			}
		}
		if (!isContain) {
			contentList.add(String.valueOf(SpyContentType.SPY_LEVEL_INSUFFICIENT_TIP));
		}
		switch (spyType) {
		case SpyType.SPY_TYPE_CITY: {
			MapCity mapCity = (MapCity)mapObject;
			bySpiedReport.setSpyType(SpyType.SPY_TYPE_CITY_D);
			Role aimRole = world.getRole(aimInfo.getUid());
			int valueMultiple = 1;
			if(mapCity.getCityState().isNospy()){
				spyReport.setSpyResult((byte) 0);
				bySpiedReport.setSpyResult((byte) 1);
				valueMultiple = 0;
			}else if(mapCity.getCityState().isDbspy()){
				spyReport.setSpyResult((byte) 1);
				bySpiedReport.setSpyResult((byte) 0);
				valueMultiple = 2;
			}else{
				spyReport.setSpyResult((byte) 1);
				bySpiedReport.setSpyResult((byte) 0);
				valueMultiple = 1;
			}
			spyReport.setSpyType(spyType);
			spyReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
			spyReport.setPosition(aimInfo.getPosition());
			FightVersus p = new FightVersus();
			p.copy(aimInfo);
			spyReport.setAimRole(p);

			if (valueMultiple != 0) {
				for (int i = 0 ; i < spyShowContentIds.size() ; i++){
					String spyshowId = spyShowContentIds.get(i);
					String oneContent = createOneCityContent(Integer.valueOf(spyshowId),aimInfo,valueMultiple,garrisonTroops,mapCity);
					if (oneContent != null) {
						contentList.add(oneContent);
					}
				}
			}
			if (role != null) {
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(spyReport), ReportType.TYPE_SPY_REPORT,null,role);
			}
			if (aimRole != null) {
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(bySpiedReport), ReportType.TYPE_SPY_REPORT,null,aimRole);
			}
		}
			break;
		case SpyType.SPY_TYPE_CITY_MOVE:
		case SpyType.SPY_TYPE_GARRISON:
		case SpyType.SPY_TYPE_FORTRESS: {	
			if (mapObject instanceof MapGarrison) {
				bySpiedReport.setSpyType(SpyType.SPY_TYPE_GARRISON_D);
			} else if (mapObject instanceof MapCityMove) {
				bySpiedReport.setSpyType(SpyType.SPY_TYPE_CITY_MOVE_D);
			} else if (mapObject instanceof MapBarracks) {
				bySpiedReport.setSpyType(SpyType.SPY_TYPE_BARRACK_D);
			} else if (mapObject instanceof MapFortress) {
				bySpiedReport.setSpyType(SpyType.SPY_TYPE_FORTRESS_D);
			} 
			spyReport.setSpyResult((byte)1);
			int valueMultiple = 1;
			if (aimInfo.getUid() != 0 && world.getRole(aimInfo.getUid()) != null) { //有所属人
				MapCity aimCity = mapWorld.searchMapCity(aimInfo);
				if(aimCity.getCityState().isNospy()){
					spyReport.setSpyResult((byte) 0);
					bySpiedReport.setSpyResult((byte) 1);
					valueMultiple = 0;
				} else if(aimCity.getCityState().isDbspy()){
					spyReport.setSpyResult((byte) 1);
					bySpiedReport.setSpyResult((byte) 0);
					valueMultiple = 2;
				} else {
					spyReport.setSpyResult((byte) 1);
					bySpiedReport.setSpyResult((byte) 0);
					valueMultiple = 1;
				}
			}
			spyReport.setSpyType(spyType);
			spyReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
			spyReport.setPosition(aimInfo.getPosition());
			FightVersus p = new FightVersus();
			p.copy(aimInfo);
			if (p.getInfo().getUid() == 0) {
				if (mapObject instanceof MapBarracks) {
					p.getInfo().setName(I18nGreeting.MSG_BIG_MAP_BARRACK);
				} else {
					p.getInfo().setName(I18nGreeting.MSG_BIG_MAP_FORTRESS);
				}
			}
			spyReport.setAimRole(p);
			if (valueMultiple != 0) {
				for (int i = 0 ; i < spyShowContentIds.size() ; i++){
					String spyshowId = spyShowContentIds.get(i);
					String oneContent = createOneFortressContent(Integer.valueOf(spyshowId),aimInfo,valueMultiple,garrisonTroops,mapObject);
					if (oneContent != null) {
						contentList.add(oneContent);
					}
				}
			}
			if (role != null) {	
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(spyReport), ReportType.TYPE_SPY_REPORT,null,role);
			}
			if (aimInfo.getUid() != 0 && world.getRole(aimInfo.getUid()) != null) {	
				Role aimRole = world.getRole(aimInfo.getUid());
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(bySpiedReport), ReportType.TYPE_SPY_REPORT,null,aimRole);
			}
		}
		break;
		case SpyType.SPY_TYPE_RESOURCE: {
			MapResource mapResource = (MapResource)mapObject;
			bySpiedReport.setSpyType(SpyType.SPY_TYPE_RESOURCE_D);
			spyReport.setSpyResult((byte)1);
			int valueMultiple = 1;
			if (aimInfo.getUid() != 0 && world.getRole(aimInfo.getUid()) != null) { //有所属人
				MapCity aimCity = mapWorld.searchMapCity(aimInfo);
				if(aimCity.getCityState().isNospy()){
					spyReport.setSpyResult((byte) 0);
					bySpiedReport.setSpyResult((byte) 1);
					valueMultiple = 0;
				} else if(aimCity.getCityState().isDbspy()){
					spyReport.setSpyResult((byte) 1);
					bySpiedReport.setSpyResult((byte) 0);
					valueMultiple = 2;
				} else {
					spyReport.setSpyResult((byte) 1);
					bySpiedReport.setSpyResult((byte) 0);
					valueMultiple = 1;
				}
			}
			spyReport.setSpyType(spyType);
			spyReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
			spyReport.setPosition(aimInfo.getPosition());
			FightVersus p = new FightVersus();
			p.copy(aimInfo);
			if (p.getInfo().getUid() == 0) {
				p.getInfo().setName(mapResource.getKey());
			}
			spyReport.setAimRole(p);
			
			if (valueMultiple != 0) {
				for (int i = 0 ; i < spyShowContentIds.size() ; i++){
					String spyshowId = spyShowContentIds.get(i);
					String oneContent = createOneResourceContent(Integer.valueOf(spyshowId),aimInfo,valueMultiple,expedite,mapResource);
					if (oneContent != null) {
						contentList.add(oneContent);
					}
				}
			}
			if (role != null) {	
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(spyReport), ReportType.TYPE_SPY_REPORT,null,role);
			}
			if (aimInfo.getUid() != 0 && world.getRole(aimInfo.getUid()) != null) {	
				Role aimRole = world.getRole(aimInfo.getUid());
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(bySpiedReport), ReportType.TYPE_SPY_REPORT,null,aimRole);
			}
		}
		break;
		case SpyType.SPY_TYPE_NPC: {
			MapUnionCity mapUnionCity = (MapUnionCity)mapObject;
			bySpiedReport.setSpyType(SpyType.SPY_TYPE_NPC_D);
			spyReport.setSpyResult((byte) 1);
			spyReport.setSpyType(spyType);
			spyReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
			spyReport.setPosition(mapUnionCity.getPosition());
			FightVersus p = new FightVersus();
			p.setType((byte)1);
			if (p.getInfo().getUid() == 0) {
				p.getInfo().setName(mapUnionCity.getKey());
			}
			spyReport.setAimRole(p);		
			for (int i = 0 ; i < spyShowContentIds.size() ; i++){
				String spyshowId = spyShowContentIds.get(i);
				String oneContent = createOneNpcCityContent(Integer.valueOf(spyshowId),garrisonTroops,mapUnionCity);
				if (oneContent != null) {
					contentList.add(oneContent);
				}
			}
			
			if (role != null) {	
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(spyReport), ReportType.TYPE_SPY_REPORT,null,role);
			}
			if (mapUnionCity != null ) {	
				//发给联盟成员
				if (mapUnionCity.getUnionId() != 0) {	
					UnionBody unionBody = unionManager.search(mapUnionCity.getUnionId());
					if (unionBody != null) {
						//加入联盟战争记录
						long battleTime = TimeUtils.nowLong();
						String  paramList = "";
						paramList += "0:" + bySpiedReport.getAimRole().getInfo().getName() + ":"
								+ mapUnionCity.getName();// 参数列表
						UnionRecords record = new UnionRecords(UnionRecords.UNION_BATTLE_RECORD_TYPE_UNION,
								UnionRecords.CONTENT_TYPE_ALLIAN_BATTLE_UNION_SPY, paramList, battleTime);
						unionBody.addOneUnionBattleRecord(record);
						unionBody.sendUnionRecordsToAllMembers();
					}
				}
			}
		}
		break;
		case SpyType.SPY_TYPE_UNION_BUILD: {
			MapUnionBuild mapUnionBuild = (MapUnionBuild)mapObject;
			bySpiedReport.setSpyType(SpyType.SPY_TYPE_UNION_BUILD_D);
			spyReport.setSpyResult((byte) 1);
			spyReport.setSpyType(spyType);
			spyReport.setTime(String.valueOf(TimeUtils.nowLong() / 1000));
			spyReport.setPosition(mapUnionBuild.getPosition());
			FightVersus p = new FightVersus();
			p.setType((byte)1);
			if (p.getInfo().getUid() == 0) {
				p.getInfo().setName(mapUnionBuild.getBuildKey());
			}
			spyReport.setAimRole(p);		
			for (int i = 0 ; i < spyShowContentIds.size() ; i++){
				String spyshowId = spyShowContentIds.get(i);
				String oneContent = createOneNpcCityBuildContent(Integer.valueOf(spyshowId),garrisonTroops,mapUnionBuild);
				if (oneContent != null) {
					contentList.add(oneContent);
				}
			}
			
			if (role != null) {	
				chatMgr.creatBattleReportAndSend(JsonUtil.ObjectToJsonString(spyReport), ReportType.TYPE_SPY_REPORT,null,role);
			}
			// 发给联盟成员
			if (mapUnionBuild.getUnionId() != 0) {
				UnionBody unionBody = unionManager.search(mapUnionBuild.getUnionId());
				if (unionBody != null) {
					// 加入联盟战争记录
					long battleTime = System.currentTimeMillis();
					String paramList = "0:" + bySpiedReport.getAimRole().getInfo().getName() + ":" + mapUnionBuild.getName();// 参数列表
					UnionRecords record = new UnionRecords(UnionRecords.UNION_BATTLE_RECORD_TYPE_UNION,
							UnionRecords.CONTENT_TYPE_ALLIAN_BATTLE_UNION_SPY, paramList, battleTime);
					unionBody.addOneUnionBattleRecord(record);
					unionBody.sendUnionRecordsToAllMembers();
				}
			}
		}
		break;
		}
		//任务事件
		role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(), ConditionType.C_SPY_NUM, 1);
	}
	
	/**
	 * 侦查npc城的内容
	 * @param spyshowId
	 * @param garrisonTroops
	 * @param mapUnionCity
	 * @return
	 */
	private static String createOneNpcCityContent(Integer spyshowId, List<GarrisonTroops> garrisonTroops, MapUnionCity mapUnionCity) {
		SpyContentType spyContentType = SpyContentType.search(spyshowId);
		if (spyContentType == null) {
			GameLog.error("spycontentId is error");
			return null;
		}
		String content = String.valueOf(spyContentType); //一条记录内容
		switch (spyContentType) {
		case SPY_NPC_BASE_LEVEL: {
			//城市等级
			content +="|" + mapUnionCity.getName();
//			content +="|" + mapUnionCity.getState();
			content +="|" + mapUnionCity.getLevel();
			content +="|" + mapUnionCity.getPosition();
		}
			break;
		case SPY_NPC_BASE_GENERAL_MONTER: {
			//普通怪
			if (mapUnionCity.getNormalArmys() != null) {
				List<TroopsData> normalArmys = mapUnionCity.getNormalArmys();
				content += "|" + normalArmys.size();
				int i = 1;
				for (int index = 0; index < 1; index++) {
					TroopsData troopsData = normalArmys.get(index);
					if (troopsData == null) {
						continue;
					}
					List<ArmyEntity> armyEntities = troopsData.getArmys();
					if (armyEntities != null) {
						for (int j = 0 ; j < armyEntities.size() ; j++){
							ArmyEntity armyEntity = armyEntities.get(j);
							content += "|" + i;
							String[] pos = armyEntity.getPos().split(",");
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
					i++;
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_NPC_BASE_MONTER: {
			//精英怪
			if (mapUnionCity.getLiteArmys() != null) {
				List<TroopsData> lites = mapUnionCity.getLiteArmys();
				int i =1;
				for (int j = 0 ; j < lites.size() ; j++){
					TroopsData troopsData = lites.get(j);
					if (troopsData == null ) {
						continue;
					}
					List<ArmyEntity> armyEntities = troopsData.getArmys();
					if (armyEntities != null) {
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i;
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
					i++;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R: {
			//驻防部队大致数量
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					int sum = 0;
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_STATION.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (sum == 0) {
						content = null;
					} else {
						content += "|" + i++;
						content += "|?";
						content += "|?";// 指挥官头像
						content += "|" + sum;
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR: {
			//驻防部队种类和数量大致
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_STATION.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i++;
							content += "|?";
							content += "|?";
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane(), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP: {
			//驻防部队种类和数量精准
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_STATION.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i++;
							content += "|?";
							content += "|?";//指挥官头像
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R_LEADER: {
			//驻防部队大致数量(含指挥官)
			int sum = 0;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (troops.getTroops() == null || troops.getTroops().getInfo() == null) {
						continue;
					}
					content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
					Role role = world.getRole(troops.getTroops().getInfo().getUid());
					if (role == null) {
						GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
						content += "|" + 0; 
					}
					content += "|" + role.getLevel(); // 指挥官等级
					content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
							+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
							+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
									: troops.getTroops().getInfo().getIcon().getIconName());
					content += "|" + sum;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane(), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
				}
			}
		}
			break;
		default:
			content = null;
			break;
		}
		return content;
	}

	/**
	 * 侦查npc建筑的内容
	 * @param spyshowId
	 * @param garrisonTroops
	 * @param mapUnionBuild
	 * @return
	 */
	private static String createOneNpcCityBuildContent(Integer spyshowId, List<GarrisonTroops> garrisonTroops, MapUnionBuild mapUnionBuild) {
		SpyContentType spyContentType = SpyContentType.search(spyshowId);
		if (spyContentType == null) {
			GameLog.error("spycontentId is error");
			return null;
		}
		String content = String.valueOf(spyContentType); //一条记录内容
		switch (spyContentType) {
		case SPY_NPC_BASE_LEVEL: {
			//城市建筑基础信息
			content +="|" + mapUnionBuild.getName();
//			content +="|" + mapUnionCity.getState();
			content +="|" + mapUnionBuild.getLevel();
			content +="|" + mapUnionBuild.getPosition();
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R: {
			//驻防部队大致数量
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					int sum = 0;
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (sum == 0) {
						content = null;
					} else {
						content += "|" + i++;
						content += "|?";
						content += "|?";// 指挥官头像
						content += "|" + sum;
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR: {
			//驻防部队种类和数量大致
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);						
							content += "|" + i++;
							content += "|?";
							content += "|?";
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane(), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP: {
			//驻防部队种类和数量精准
			int i = 1;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i++;
							content += "|?";
							content += "|?";//指挥官头像
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R_LEADER: {
			//驻防部队大致数量(含指挥官)
			int sum = 0;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (troops.getTroops() == null || troops.getTroops().getInfo() == null) {
						continue;
					}
					content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
					Role role = world.getRole(troops.getTroops().getInfo().getUid());
					if (role == null) {
						GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
						content += "|" + 0; 
					}
					content += "|" + role.getLevel(); // 指挥官等级
					content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
							+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
							+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
									: troops.getTroops().getInfo().getIcon().getIconName());
					content += "|" + sum;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane(), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey().equals(TimerLastType.TIME_MAP_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
				}
			}
		}
			break;
		default:
			content = null;
			break;
		}
		return content;
	}

	/**
	 * 加一条资源侦查信息
	 * @param spyshowId
	 * @param aimInfo
	 * @param valueMultiple
	 * @param expedite
	 * @param mapResource
	 * @return
	 */
	private static String createOneResourceContent(Integer spyshowId, MapRoleInfo aimInfo, int valueMultiple,
			ExpediteTroops expedite, MapResource mapResource) {
		SpyContentType spyContentType = SpyContentType.search(spyshowId);
		if (spyContentType == null) {
			GameLog.error("spycontentId is error");
			return null;
		}
		String content = String.valueOf(spyContentType); //一条记录内容
		switch (spyContentType) {
		case SPY_RESOURCE_BASE_VALUE:{
			//资源田基础数值
			content += "|" + mapResource.getKey();
			content += "|" + mapResource.getLevel();
			content += "|" + mapResource.getPosition();
			content += "|" + mapResource.getOutput();
			content += "|" + (mapResource.getCollecterTroops() == null ? 0 : mapResource.computeCollectNum());
		}
			break;
		case SPY_CITY_ARMY_R: {
			//基础部队大致数量(驻扎部队)
			int sum = 0;
			if(mapResource.getCollecterTroops() != null){ //玩家占领
				GarrisonTroops collecter = mapResource.getCollecterTroops();
				if (collecter.getTroops() !=null && collecter.getTroops().getArmys()!=null && collecter.getTroops().getInfo()!=null) {
					List<ArmyEntity> armys = collecter.getTroops().getArmys();
					for (int i = 0 ; i < armys.size() ; i++){
						ArmyEntity armyEntity = armys.get(i);
						if (armyEntity == null) {
							continue;
						}
						sum += armyEntity.getSane();
					}
					content += "|" + sum * valueMultiple;
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NR: {
			//基础部队兵种类型数量大致(驻扎部队)
			if(mapResource.getCollecterTroops() != null){ //玩家占领
				GarrisonTroops collecter = mapResource.getCollecterTroops();
				if (collecter.getTroops() !=null && collecter.getTroops().getArmys()!=null && collecter.getTroops().getInfo()!=null) {
					List<ArmyEntity> armys = collecter.getTroops().getArmys();
					for (int i = 0 ; i < armys.size() ; i++){
						ArmyEntity armyEntity = armys.get(i);
						if (armyEntity == null) {
							continue;
						}
						String[] pos = armyEntity.getPos().split(",");							
						content += "|" + pos[0];
						content += "|" + armyEntity.getKey();
						content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple, GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
					}
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NP: {
			//基础部队兵种类型数量精准(驻扎部队)
			if(mapResource.getCollecterTroops() != null){ //玩家占领
				GarrisonTroops collecter = mapResource.getCollecterTroops();
				if (collecter.getTroops() !=null && collecter.getTroops().getArmys()!=null && collecter.getTroops().getInfo()!=null) {
					List<ArmyEntity> armys = collecter.getTroops().getArmys();
					for (int i = 0 ; i < armys.size() ; i++){
						ArmyEntity armyEntity = armys.get(i);
						if (armyEntity == null) {
							continue;
						}
						String[] pos = armyEntity.getPos().split(",");							
						content += "|" + pos[0];
						content += "|" + armyEntity.getKey();
						content += "|" + armyEntity.getSane() * valueMultiple;
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R: {
			//驻防部队大致数量
			if (mapResource.getCollecterTroops() != null) {
				int sum = 0;
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				if (garrisonTroops != null) {
					int i = 1;
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								sum += armyEntity.getSane();
							}
						}
						if (sum == 0) {
							content = null;
						} else {
							content += "|?" + i++; // 指挥官名字
							content += "|?"; // 指挥官等级
							content += "|?";// 指挥官头像
							content += "|" + sum * valueMultiple;
						}
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR: {
			//驻防部队种类和数量大致
			if (mapResource.getCollecterTroops() != null) {
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				if (garrisonTroops != null) {
					int i = 1;
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|?" + i;	//指挥官名字
								content += "|?";		//指挥官等级
								content += "|?";		//指挥官头像
								String[] pos = armyEntity.getPos().split(",");							
								content += "|" + pos[0];
								content += "|" + armyEntity.getKey();
								content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple, GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
							}
						}
						i++;
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP: {
			//驻防部队种类和数量精准
			if (mapResource.getCollecterTroops() != null) {
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				if (garrisonTroops != null) {
					int i = 1;
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|?" + i;	//指挥官名字
								content += "|?";		//指挥官等级	
								content += "|?";//指挥官头像
								String[] pos = armyEntity.getPos().split(",");							
								content += "|" + pos[0];
								content += "|" + armyEntity.getKey();
								content += "|" + armyEntity.getSane() * valueMultiple;
							}
						}
						i++;
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R_LEADER: {
			//驻防部队大致数量(含指挥官)
			if (mapResource.getCollecterTroops() != null) {
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				int sum =0;
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								sum += armyEntity.getSane() * valueMultiple;
							}
						}
						if (troops.getTroops() == null || troops.getTroops().getInfo() == null) {
							continue;
						}
						content += "|" + troops.getTroops().getInfo().getName();		//指挥官名字
						Role role = world.getRole(troops.getTroops().getInfo().getUid());
						if (role == null) {
							GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
							content += "|" + 0; 
						}
						content += "|" + role.getLevel(); // 指挥官等级
						content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
								+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
								+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
										: troops.getTroops().getInfo().getIcon().getIconName());
						content += "|" + sum;
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR_LEADER: {
			//驻防部队种类和数量大致(含指挥官)
			if (mapResource.getCollecterTroops() != null) {
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|" + troops.getTroops().getInfo().getName();		//指挥官名字
								Role role = world.getRole(troops.getTroops().getInfo().getUid());
								if (role == null) {
									GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
									content += "|" + 0; 
								}
								content += "|" + role.getLevel(); // 指挥官等级
								content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
										+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
										+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
												: troops.getTroops().getInfo().getIcon().getIconName());
								String[] pos = armyEntity.getPos().split(",");							
								content += "|" + pos[0];
								content += "|" + armyEntity.getKey();
								content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple, GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
							}
						}
						if (troops.getTroops().getInfo() != null) {
							continue;
						}
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP_LEADER: {
			//驻防部队种类和数量精准(含指挥官)
			if (mapResource.getCollecterTroops() != null) {
				List<GarrisonTroops> garrisonTroops = mapResource.getHelpers();
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|" + troops.getTroops().getInfo().getName();		//指挥官名字
								Role role = world.getRole(troops.getTroops().getInfo().getUid());
								if (role == null) {
									GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
									content += "|" + 0; 
								}
								content += "|" + role.getLevel(); // 指挥官等级
								content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
										+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
										+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
												: troops.getTroops().getInfo().getIcon().getIconName());
								String[] pos = armyEntity.getPos().split(",");							
								content += "|" + pos[0];
								content += "|" + armyEntity.getKey();
								content += "|" + armyEntity.getSane() * valueMultiple;
							}
						}
						if (troops.getTroops().getInfo() != null) {
							continue;
						}
					}
				}
			} else {
				content = null;
			}
		}
			break;
		case SPY_NPC_BASE_GENERAL_MONTER:{
			if (mapResource.getCollecterTroops() != null) {
				break;
			}
			Resourcefield resourceField = mapResource.getData();//资源田固化数据
			String monsterKey = resourceField.getMonster();
			Monster monster = dataManager.serach(Monster.class,monsterKey);
			if (monster == null){
				GameLog.info("策划又填错数据了");
				return null;
			}
			TroopsData monsterTroops = TroopsData.create(monster,0);
			int i = 1;
			content += "|" + 1;
			if (monsterTroops != null && monsterTroops.getArmys().size() > 1) {
				for (int j = 0 ; j < monsterTroops.getArmys().size() ; j++){
					ArmyEntity armyEntity = monsterTroops.getArmys().get(j);
					if (armyEntity == null) {
						continue;
					}
					content += "|" + i;
					String[] pos = armyEntity.getPos().split(",");							
					content += "|" + pos[0];
					content += "|" + armyEntity.getKey();
					content += "|" + armyEntity.getSane();
				}
			}
		}
			break;
		default:
			content = null;
			break;
		}
		return content;
	}

	/**
	 * 加一条要塞消息
	 * @param spyshowId
	 * @param aimInfo
	 * @param valueMultiple
	 * @param garrisonTroops
	 * @param mapFortress
	 * @return
	 */
	private static String createOneFortressContent(Integer spyshowId, MapRoleInfo aimInfo, int valueMultiple
			, List<GarrisonTroops> garrisonTroops, MapObject mapObject) {
		SpyContentType spyContentType = SpyContentType.search(spyshowId);
		if (spyContentType == null) {
			GameLog.error("spycontentId is error");
			return null;
		}
		String content = String.valueOf(spyContentType); //一条记录内容
		switch (spyContentType) {
		case SPY_FORTRESS_BASE_LEVEL: {
			//要塞基础数据
			if (mapObject == null) {
				GameLog.error("mapFortress is null ");
				content = null;
			}
			if (mapObject instanceof MapBarracks) { //军营
				MapBarracks mapBarracks = (MapBarracks) mapObject;
				content += "|0";
				content += "|" + mapBarracks.getName();
				content += "|" + mapBarracks.getLevel();
				content += "|" + mapBarracks.getPosition();
			} else if (mapObject instanceof MapFortress) {//要塞
				MapFortress mapFortress = (MapFortress) mapObject;
				content += "|1";
				content += "|" + mapFortress.getName();
				content += "|" + mapFortress.getLevel();
				content += "|" + mapFortress.getPosition();
			} else if (mapObject instanceof MapCityMove) {//迁城点
				MapCityMove mapCityMove = (MapCityMove)mapObject;
				content += "|2";
				content += "|" + mapCityMove.getInfo().getName();
				content += "|" + mapCityMove.getLevel();
				content += "|" + mapCityMove.getPosition();
			} else if (mapObject instanceof MapGarrison) {//驻防点
				MapGarrison mapGarrison = (MapGarrison)mapObject;
				content += "|3";
				content += "|" + mapGarrison.getInfo().getName();
				content += "|" + mapGarrison.getLevel();
				content += "|" + mapGarrison.getPosition();
			}
		}
			break;
		case SPY_CITY_ARMY_R: {
			// 基础部队大致数量(驻扎部队)
			if (mapObject instanceof MapBarracks) {
				if(mapObject.getInfo().getUid() == 0){
					break;
				}
				int i = 1;
				MapBarracks barrack = new MapBarracks();
				barrack = (MapBarracks) mapObject;
				if (barrack != null && barrack.getNpcs() != null) {
					List<TroopsData> troops = barrack.getNpcs();
					for (int j = 0 ; j < troops.size() ; j++){
						TroopsData troopsData = troops.get(j);
						int sum = 0;
						List<ArmyEntity> armyEntities = troopsData.getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane() * valueMultiple;
						}
						content += "|" + i++;
						content += "|" + sum;
					}
				}
			} else {
				int i = 1;
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						int sum = 0;
						if (troops != null && troops.getTimer() != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								sum += armyEntity.getSane() * valueMultiple;
							}
						}
						content += "|" + i++;
						content += "|" + sum;
					}
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NR: {
			// 基础部队兵种类型数量大致(驻扎部队)
			if (mapObject instanceof MapBarracks) {
				if(mapObject.getInfo().getUid() == 0){
					break;
				}
				int i = 1;
				MapBarracks barrack = new MapBarracks();
				barrack = (MapBarracks) mapObject;
				if (barrack != null && barrack.getNpcs() != null) {
					List<TroopsData> troops = barrack.getNpcs();
					for (int j = 0 ; j < troops.size() ; j++){
						TroopsData troopsData = troops.get(j);
						List<ArmyEntity> armyEntities = troopsData.getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i++;
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple,
									GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			} else {
				int i = 1;
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTimer() != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|" + i++;
								content += "|" + armyEntity.getKey();
								content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple,
										GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
							}
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NP: {
			// 基础部队兵种类型数量精准(驻扎部队)
			if (mapObject instanceof MapBarracks) {
				if(mapObject.getInfo().getUid() == 0){
					break;
				}
				int i = 1;
				MapBarracks barrack = new MapBarracks();
				barrack = (MapBarracks) mapObject;
				if (barrack != null && barrack.getNpcs() != null) {
					List<TroopsData> troops = barrack.getNpcs();
					for (int j = 0 ; j < troops.size() ; j++){
						TroopsData troopsData = troops.get(j);
						List<ArmyEntity> armyEntities = troopsData.getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i++;
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane() * valueMultiple;
						}
					}
				}
			} else {
				int i = 1;
				if (garrisonTroops != null) {
					for (int j = 0 ; j < garrisonTroops.size() ; j++){
						GarrisonTroops troops = garrisonTroops.get(j);
						if (troops != null && troops.getTimer() != null && troops.getTroops() != null) {
							List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
							for (int k = 0 ; k < armyEntities.size() ; k++){
								ArmyEntity armyEntity = armyEntities.get(k);
								content += "|" + i++;
								content += "|" + armyEntity.getKey();
								content += "|" + armyEntity.getSane() * valueMultiple;
							}
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R: {
			int sum = 0;
			if (garrisonTroops != null) {
				int i = 1;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (sum == 0) {
						content = null;
					} else {
						content += "|?" + i++;	//指挥官名字
						content += "|?";	//指挥官等级
						content += "|?";//指挥官头像
						content += "|" + sum * valueMultiple;
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR: {
			if (garrisonTroops != null) {
				int i =1;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0; k < armyEntities.size(); k++) {
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|?"+i;
							content += "|?";
							content += "|?";
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple, GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
					i++;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP: {
			if (garrisonTroops != null) {
				int i =1 ;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0; k < armyEntities.size(); k++) {
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|?"+i;
							content += "|?";
							content += "|?";//指挥官头像
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane() * valueMultiple;
						}
					}
					i++;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R_LEADER: {
			int sum = 0;
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0; k < armyEntities.size(); k++) {
							ArmyEntity armyEntity = armyEntities.get(k);
							sum += armyEntity.getSane();
						}
					}
					if (troops.getTroops() == null || troops.getTroops().getInfo() == null) {
						continue;
					}
					content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
					Role role = world.getRole(troops.getTroops().getInfo().getUid());
					if (role == null) {
						GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
						content += "|" + 0; 
					}
					content += "|" + role.getLevel(); // 指挥官等级
					content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
							+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
							+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
									: troops.getTroops().getInfo().getIcon().getIconName());
					content += "|" + sum * valueMultiple;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0; j < garrisonTroops.size(); j++) {
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0; k < armyEntities.size(); k++) {
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue(armyEntity.getSane() * valueMultiple, GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP_LEADER: {
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					if (troops != null && troops.getTimer() != null
							&& troops.getTimer().getType().getKey()
									.equals(TimerLastType.TIME_EXPEDITE_GARRISON.getKey())
							&& troops.getTroops() != null) {
						List<ArmyEntity> armyEntities = troops.getTroops().getArmys();
						for (int k = 0; k < armyEntities.size(); k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							if (troops.getTroops().getInfo() == null) {
								continue;
							}
							content += "|" + troops.getTroops().getInfo().getName();	//指挥官名字
							Role role = world.getRole(troops.getTroops().getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troops.getTroops().getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane() * valueMultiple;
						}
					}
				}
			}
		}
			break;
		case SPY_NPC_BASE_GENERAL_MONTER: {
			// 未被占领的军营
			if (mapObject instanceof MapBarracks && mapObject.getInfo().getUid() == 0) {
				int i = 1;
				content += "|" + 1;
				MapBarracks barrack = (MapBarracks) mapObject;
				if (barrack != null && barrack.getNpcs() != null) {
					List<TroopsData> troops = barrack.getNpcs();
					for (int j = 0 ; j < troops.size() ; j++){
						TroopsData troopsData = troops.get(j);
						List<ArmyEntity> armyEntities = troopsData.getArmys();
						for (int k = 0 ; k < armyEntities.size() ; k++){
							ArmyEntity armyEntity = armyEntities.get(k);
							content += "|" + i;
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + armyEntity.getSane();
						}
					}
				}
			}
		}
			break;
		default:
			content = null;
			break;
		}
		return content;
	}

	/**
	 * 侦查用户主城的建筑
	 * @param spyshowId
	 * @param aimInfo
	 * @param valueMultiple
	 * @param garrisonTroops
	 * @param mapCity 
	 * @return
	 */
	private static String createOneCityContent(int spyshowId, MapRoleInfo aimInfo, int valueMultiple,List<GarrisonTroops> garrisonTroops, MapCity mapCity) {
		Role aimRole = world.getRole(aimInfo.getUid());
		RoleCityAgent aimCity = aimRole.getCity(aimInfo.getCityId());
		RoleArmyAgent armyAgent = aimCity.getCityArmys();
		SpyContentType spyContentType = SpyContentType.search(spyshowId);
		if (spyContentType == null) {
			GameLog.error("spycontentId is error");
			return null;
		}
		String content = String.valueOf(spyContentType); //一条记录内容
		switch (spyContentType) {
		case SPY_CITY_RESOURCE:{
			// 资源信息 和 当前资源信息
			Map<ResourceTypeConst, Long> allResource = aimCity.getResources();
			Map<ResourceTypeConst, Long> currentResource = aimCity.getCityCurrentRes();
			for (ResourceTypeConst temp : allResource.keySet()) {
				content += "|" + temp.getKey();
				content += "|" + allResource.get(temp);
				content += "|" + (currentResource.get(temp) == null ? 0 : currentResource.get(temp));
			}
		}			
			break;
		case SPY_CITY_ARMY_R: {
			// 基地军队信息大致数量
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			int armyNumber = 0;
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() < ArmyType.HOOK.ordinal()){
					armyNumber += armyInfo.getArmyNum() * valueMultiple;
				}
			}
			content += "|" + armyNumber;
		}
			break;
		case SPY_CITY_DEFENCE_INSTALLATION_R: {
			//防御设施 大致数量
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			int armyNumber = 0;
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() == (byte) 5) {
					armyNumber += armyInfo.getArmyNum() * valueMultiple;
				} 
			}
			content += "|" + armyNumber;
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R: {
			//驻防部队大致数量
			if (garrisonTroops != null) {
				int i =1;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					int troopsSum = 0;
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null) {
								continue;
							}
							troopsSum += (armyEntity.getSane() * valueMultiple);
						}
					}
					if (troopsSum == 0) {
						content = null;
					} else {
						content += "|?" + i++;// 指挥官名称
						content += "|?";// 指挥官等级
						content += "|?";// 指挥官头像
						content += "|" + troopsSum;//// 指挥官部队总数
					}
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_R_LEADER: {
			// 驻防军队
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					int troopsSum = 0;
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null ) {
								continue;
							}
							troopsSum += (armyEntity.getSane() * valueMultiple);
						}
					}
					content += "|" + troopsData.getInfo().getName();
					Role role = world.getRole(troopsData.getInfo().getUid());
					if (role == null) {
						GameLog.error("getRole is error uid = " + troopsData.getInfo().getUid());
						content += "|" + 0; 
					}
					content += "|" + role.getLevel(); // 指挥官等级
					content += "|" + troopsData.getInfo().getIcon().getIconType() + "$"
							+ troopsData.getInfo().getIcon().getIconId() + "$"
							+ (StringUtils.isNull(troopsData.getInfo().getIcon().getIconName()) == true ? 0
									: troopsData.getInfo().getIcon().getIconName());
					content += "|" + troopsSum;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_LEADER: {
			// 指挥官等级和用户名
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					content += "|" + troopsData.getInfo().getName();
					Role role = world.getRole(troopsData.getInfo().getUid());
					if (role == null) {
						GameLog.error("getRole is error uid = " + troopsData.getInfo().getUid());
						content += "|" + 0; 
					}
					content += "|" + role.getLevel(); // 指挥官等级
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NR: {
			// 基础部队兵种类型数量大致
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() < (byte) 5) {
					content += "|0";
					content += "|" + army.getId();
					content += "|" + MathUtils.getFluctuateValue((armyInfo.getArmyNum() * valueMultiple),GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
				}
			}
		}
			break;
		case SPY_CITY_DEFENCE_INSTALLATION_NR: {
			// 城防设施类型数量大致
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() == (byte) 5) {
					content += "|" + army.getId();
					content += "|" + MathUtils.getFluctuateValue((armyInfo.getArmyNum() * valueMultiple),GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR: {
			// 驻防部队兵种类型数量大致
			if (garrisonTroops != null) {
				int i = 1;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null) {
								continue;
							}
							content += "|?"+i;
							content += "|?";
							content += "|?";
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue((armyEntity.getSane() * valueMultiple), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
					i++;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NR_LEADER: {
			//驻防部队兵种类型数量大致+指挥官等级和用户名
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null) {
								continue;
							}
							content += "|" + troopsData.getInfo().getName();
							Role role = world.getRole(troopsData.getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troopsData.getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + MathUtils.getFluctuateValue((armyEntity.getSane() * valueMultiple), GameConfig.SPY_NUM_FLUCTUATE_FACTOR);
						}
					}
				}
			}
		}
			break;
		case SPY_CITY_ARMY_NP: {
			// 基础部队兵种类型数量精准
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() < (byte) 5) {
					content += "|0";
					content += "|" + army.getId();
					content += "|" + (armyInfo.getArmyNum() * valueMultiple);
				} 
			}
		}
			break;
		case SPY_CITY_DEFENCE_INSTALLATION_NP: {
			// 城防设施类型数量精准
			List<ArmyInfo> cityArmys = new ArrayList<ArmyInfo>();
			armyAgent.getCityArmys(ArmyState.ARMY_IN_NORMAL.getValue(), cityArmys);
			for (int i = 0 ; i < cityArmys.size() ; i++){
				ArmyInfo armyInfo = cityArmys.get(i);
				String armyId = armyInfo.getArmyId();
				Army army = dataManager.serach(Army.class, armyId);
				if (army == null) {
					GameLog.error("read Army base is fail");
					continue;
				}
				if (army.getArmyType() == (byte) 5) {
					content += "|" + army.getId();
					content += "|" + (armyInfo.getArmyNum() * valueMultiple);
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP: {
			// 驻防部队兵种类型数量精准
			if (garrisonTroops != null) {
				int i =1;
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null) {
								continue;
							}
							content += "|?"+i;
							content += "|?";
							content += "|?";//指挥官头像
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + (armyEntity.getSane() * valueMultiple);
						}
					}
					i++;
				}
			}
		}
			break;
		case SPY_CITY_GARRISON_ARMY_NP_LEADER: {
			// 驻防部队兵种类型数量精准+指挥官等级和用户名
			if (garrisonTroops != null) {
				for (int j = 0 ; j < garrisonTroops.size() ; j++){
					GarrisonTroops troops = garrisonTroops.get(j);
					TroopsData troopsData = troops.getTroops();
					if (troopsData == null || troopsData.getInfo() == null) {
						continue;
					}
					if (troopsData.getArmys() != null) {
						for (int k = 0 ; k < troopsData.getArmys().size() ; k++){
							ArmyEntity armyEntity = troopsData.getArmys().get(k);
							if (armyEntity == null) {
								continue;
							}
							content += "|" + troopsData.getInfo().getName();
							Role role = world.getRole(troopsData.getInfo().getUid());
							if (role == null) {
								GameLog.error("getRole is error uid = " + troopsData.getInfo().getUid());
								content += "|" + 0; 
							}
							content += "|" + role.getLevel(); // 指挥官等级
							content += "|" + troops.getTroops().getInfo().getIcon().getIconType() + "$"
									+ troops.getTroops().getInfo().getIcon().getIconId() + "$"
									+ (StringUtils.isNull(troops.getTroops().getInfo().getIcon().getIconName()) == true ? 0
											: troops.getTroops().getInfo().getIcon().getIconName());
							String[] pos = armyEntity.getPos().split(",");							
							content += "|" + pos[0];
							content += "|" + armyEntity.getKey();
							content += "|" + (armyEntity.getSane() * valueMultiple);
						}
					}
				}
			}
		}
		break;
		case SPY_CITY_DEFENCE_BUILDING_R: {
			// 防御建筑类型和数量
			List<RoleBuild> grandeCannons = aimCity.searchBuildByBuildId(BuildName.GRANDE_CANNON.getKey());
			List<RoleBuild> teslaCoils = aimCity.searchBuildByBuildId(BuildName.TESLA_COIL.getKey());
			List<RoleBuild> laserTowers = aimCity.searchBuildByBuildId(BuildName.LASER_TOWER.getKey());
			if (grandeCannons != null && grandeCannons.size() > 1) {
				content += "|" + grandeCannons.get(0) .getBuildId(); 
				content += "|" + grandeCannons.size(); 
			}
			if (teslaCoils != null && teslaCoils.size() > 1) {
				content += "|" + teslaCoils.get(0) .getBuildId(); 
				content += "|" + teslaCoils.size(); 
			}
			if (laserTowers != null && laserTowers.size() > 1) {
				content += "|" + laserTowers.get(0) .getBuildId(); 
				content += "|" + laserTowers.size(); 
			}
		}
		break;
		case SPY_CITY_DEFENCE_BUILDING_P: {
			// 防御类型类型和数量及其等级
			List<RoleBuild> grandeCannons = aimCity.searchBuildByBuildId(BuildName.GRANDE_CANNON.getKey());
			List<RoleBuild> teslaCoils = aimCity.searchBuildByBuildId(BuildName.TESLA_COIL.getKey());
			List<RoleBuild> laserTowers = aimCity.searchBuildByBuildId(BuildName.LASER_TOWER.getKey());
			if (grandeCannons != null && grandeCannons.size() > 1) {
				for (int i = 0 ; i < grandeCannons.size() ; i++){
					RoleBuild roleBuild = grandeCannons.get(i);
					if (roleBuild == null) {
						continue;
					}
					content += "|" + roleBuild.getBuildId();
					content += "|" + roleBuild.getLevel();
				}
			}
			if (teslaCoils != null && teslaCoils.size() > 1) {
				for (int i = 0 ; i < teslaCoils.size() ; i++){
					RoleBuild roleBuild = teslaCoils.get(i);
					if (roleBuild == null) {
						continue;
					}
					content += "|" + roleBuild.getBuildId();
					content += "|" + roleBuild.getLevel();		
				}
			}
			if (laserTowers != null && laserTowers.size() > 1) {
				for (int i = 0 ; i < laserTowers.size() ; i++){
					RoleBuild roleBuild = laserTowers.get(i);
					if (roleBuild == null) {
						continue;
					}
					content += "|" + roleBuild.getBuildId();
					content += "|" + roleBuild.getLevel();		
				}
			}
		}
		break;
		case SPY_CITY_TECHCENTER_LEVEL: {
			List<RoleBuild>  techBuilds = aimCity.searchBuildByBuildId(BuildName.TECH_CENTER.getKey());
			content += "|" + BuildName.TECH_CENTER.getKey();
			if (techBuilds.size() < 1) {
				content += "|0";
			}else{
				content += "|" + techBuilds.get(0).getLevel();
			}
		}
		break;
		case SPY_CITY_DEFENCE_VALUE: {
			// 城防值
			List<RoleBuild> wallBuilds = aimCity.searchBuildByCompomemt(BuildComponentType.BUILD_COMPONENT_WALL);
			if (wallBuilds.size() > 0) {
				BuildComponentWall wall = wallBuilds.get(0).getComponent(BuildComponentType.BUILD_COMPONENT_WALL);
				content += "|" + wall.getDefenseValue();
				content += "|" + wall.getFenseMaxValue();
				content += "|" + (aimCity == null ? 0 : aimCity.getCityCenterLevel());
				content += "|" + mapCity.getPosition();
			} else {
				content += "|0";
				content += "|0";
				content += "|" + (aimCity == null ? 0 : aimCity.getCityCenterLevel());
				content += "|" + mapCity.getPosition();
				GameLog.error("searchBuildByCompomemt get wall is fail ");
			}
		}
		break;
		default:
			content = null;
			break;
		}
		return content;	
	}
	
	/**
	 * 获取矩形区域的所有索引
	 * @param centerX 矩形区域的中心x
	 * @param centerY 矩形区域的中心y
	 * @param width 半径宽
	 * @param height 半径高
	 * @return
	 */
	public static List<Integer> getRangeIndexs(int centerX,int centerY,int width,int height){
		List<Integer> result = new ArrayList<Integer>();
		int left  = Math.max(centerX-width,0);
		int right = Math.min(centerX+width,GameConfig.MAP_WIDTH-1);
		int up    = Math.max(centerY-height,0);
		int down  = Math.min(centerY+height,GameConfig.MAP_HEIGHT-1);
		for (int i = up ; i <= down ; i++){
			for (int j = left ; j <= right ; j++){
				int index = PointVector.getPosition(j,i);
				result.add(Integer.valueOf(index));
			}
		}
		return result;
	}
	
	/**
	 * 获取以baseIndex未中心的半径是radius的区域
	 * @param baseIndex
	 * @param radius
	 * @return
	 */
	public static List<Integer> computeIndexs(int baseIndex , int radius){
		List<Integer> result = null;
		if (radius == 0){
			result = new ArrayList<Integer>();
			result.add(Integer.valueOf(baseIndex));
		}else{
			int col   = PointVector.getX(baseIndex);
			int row   = PointVector.getY(baseIndex);
			result = getRangeIndexs(col,row,radius,radius);
		}
		return result;
	}
	
	public static List<Integer> computeIndexs(int baseIndex , int radius_x,int radius_y){
		List<Integer> result = null;
		if (radius_x == 0 && radius_y == 0){
			result = new ArrayList<Integer>();
			result.add(Integer.valueOf(baseIndex));
		}else{
			int col   = PointVector.getX(baseIndex);
			int row   = PointVector.getY(baseIndex);
			result = getRangeIndexs(col,row,radius_x,radius_y);
		}
		return result;
	}
	
	/**
	 * 判断集合indexs里面是否能容得下一格radius * radius的区域
	 * @param indexs
	 * @param radius
	 * @return
	 */
	public static List<Integer> computeInIndexs(List<Integer> indexs , int radius){
		if (radius == 0){//半径为0
			return indexs;
		}else if (radius > 0){//大于0的半径
			List<Integer> result = new ArrayList<Integer>();
			for (int i = 0 ; i < indexs.size() ; i++){
				Integer index = indexs.get(i);
				List<Integer> temp = computeIndexs(index.intValue(),radius);
				int count = 0;
				for (int j = 0 ; j < temp.size() ; j++){
					Integer ti = temp.get(j);
					if (indexs.contains(ti)){
						count ++;
					}
				}
				if (count == temp.size()){
					result.add(index);
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * 采集对象算加成buff
	 * @param obj
	 * @param collecter
	 * @param role
	 * @param cityId
	 * @return
	 */
	public static float updateCollecter(MapObject obj , ResourceCollecter collecter , Role role,int cityId){
		List<EffectListener> els  = role.getResourceCollectEffect(cityId);
		float value = 0;
		for (int i = 0 ; i < els.size() ; i++){
			EffectListener el = els.get(i);
			if (collecter.add(el)){
				value += el.getValue();
				taskPool.mapTread.addObj(obj,el.getTimer());
			}
		}
		float serverBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_GATHER_SPEED)/100.0f;
		GameLog.info("[updateCollecter-speed]uid="+role.getJoy_id()+"|serverBuff="+serverBuff+"|value="+value);
		return value + serverBuff;
	}
	
	
	public static boolean couldFight(List<GarrisonTroops> defenders){
		for (int  i = 0 ; i < defenders.size() ; i++){
			GarrisonTroops troops = defenders.get(i);
			if (troops.getTroops().couldFight()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean monsterAttackRoleCity(int position,String monsterId,long uid,int cityId){
		MapCity city = mapWorld.searchMapCity(uid,cityId);
		if (city == null){
			return false;
		}
		Monster monster = dataManager.serach(Monster.class,monsterId);
		if (monster == null){
			return false;
		}
		ExpediteTroops expedite = new ExpediteTroops();
		long expediteId = keyData.key(DaoData.TABLE_RED_ALERT_ROLEEXPEDITE);
		expedite.setId(expediteId);
		expedite.setStartPosition(position);
		expedite.setTargetPosition(city.getPosition());
		TroopsData troops = TroopsData.create(monster,position);
		troops.setComePosition(position);
		expedite.addTroops(troops);
		expedite.computMoveSpeed(null);
		troops.setId(expediteId);
		expedite.addSelf();
		long castTime = MapUtil.computeCastTime(position,city.getPosition(),expedite.getSpeed());//行军需要的时间
		TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000,castTime,TimerLastType.TIME_MONSTER_ATTACK);
		expedite.registTimer(timer);
		mapWorld.getMapCell(position).expedite(expediteId);//起点格子加入行军
		mapWorld.getMapCell(city.getPosition()).expedite(expediteId);//终点格子加入行军
		Role role = world.getOnlineRole(uid);
		if (role != null){
			role.handleEvent(GameEvent.TROOPS_SEND);
			role.handleEvent(GameEvent.UNION_FIGHT_CHANGE,false);// 联盟战斗变化
		}
		return true;
	}
	
	public static void updateUnionBuidlBuff(Role role,long unionId,boolean insert){
		List<MapUnionCity> unionCitys = mapWorld.searchUnionCity(unionId);
		for (int i = 0 ; i < unionCitys.size() ; i++){
			MapUnionCity unionCity = unionCitys.get(i);
			List<Integer> builds = unionCity.getBuilds();
			for (int j = 0 ; j < builds.size() ; j++){
				int pos = builds.get(j).intValue();
				MapCell cell = mapWorld.getMapCell(pos);
				if (cell.getType() == MapCellType.MAP_CELL_TYPE_UINON_OTHER){
					MapUnionOther buffBuild = mapWorld.searchObject(cell);
					if (insert){
						buffBuild.addBuff(role);
					}else{
						buffBuild.removeBuff(role);
					}
				}
			}
		}
	}
	
	public static  float getCollectRate(String key){
		float rate = 0;
		if (key.equals(BuildName.MAP_UNION_FOOD.getKey()) ||
			key.equals(BuildName.MAP_UNION_METAL.getKey()) ||
			key.equals(ResourceTypeConst.RESOURCE_TYPE_FOOD.getKey()) ||
			key.equals(ResourceTypeConst.RESOURCE_TYPE_METAL.getKey())){
			rate = 1;
		}else if (key.equals(BuildName.MAP_UNION_OIL.getKey()) || key.equals(ResourceTypeConst.RESOURCE_TYPE_OIL.getKey())){
			rate = 4;
		}else{
			rate = 16;
		}
		return rate;
	}
	
}
