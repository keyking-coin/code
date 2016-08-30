package com.joymeng.slg.domain.map.impl.still.union.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.fight.obj.enumType.UnitType;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.world.GameConfig;

/**
 * 联盟防御塔
 * @author tanyong
 *
 */
public class MapUnionDefenderTower extends MapUnionBuild {
	int arw;//攻击范围宽
	int arh;//攻击范围高
	int attPower;//攻击力
	int attSpeed;//攻击速度
	List<UnitType> ats = new ArrayList<UnitType>();//可攻击单位
	List<LockTroops> locks = new ArrayList<LockTroops>();//锁定的目标
	List<LockTroops> addList = new ArrayList<LockTroops>();//添加列表
	
	class LockTroops{
		long troopsId;
		long inTime;//进入射程时间
		long outTime;//离开射程时间
		long attCount;//攻击计数器
		ArmyEntity lastTarget = null;
		int hpTemp;
		boolean beginAttack = false;
		List<Integer> attackTimes = new ArrayList<Integer>();
		
		private int compute(boolean flag,ExpediteTroops troops){
			int result = 0;
			ArmyEntity target = null;
			if (lastTarget == null){
				List<ArmyEntity> emenys = getCouldAttackArmy(troops);
				if (emenys.size() == 0){//部队被消灭光了
					return 1;
				}
				int index = MathUtils.random(emenys.size());
				target = emenys.get(index);
			}else{
				target = lastTarget;
			}
			Army targetArmy = dataManager.serach(Army.class,target.getKey());
			int   hp     = Math.round(targetArmy.getHitPoints());
			float damage = (flag && hpTemp < 0) ? 0 : attPower;
			int preNum = target.getSane();
			int newNum = 0;
			int remainHp = (int)Math.floor(target.getSane() * hp + hpTemp - damage);
			if (remainHp >= 0) {
				newNum  = remainHp / hp;
				target.setSane(newNum);
				hpTemp = remainHp  % hp;
				if (hpTemp > 0){
					lastTarget = target;
				}else{
					lastTarget = null;
				}
			} else {//这个单位剩余血量不够扣的
				target.setSane(0);
				lastTarget = null;
				hpTemp = remainHp;
				result = 2;
			}
			int showDamage = preNum - newNum;
			AbstractClientModule module = new AbstractClientModule() {
				@Override
				public short getModuleType() {
					return NTC_DTCD_UNION_DEFENDER_ATTACK;
				}
			};
			module.add(position);//int 攻击者坐标
			module.add(troopsId);//long 挨打的行军部队编号
			module.add(showDamage);//int 本次伤害值
			RespModuleSet resp = new RespModuleSet();
			resp.addModule(module);
			MessageSendUtil.sendMessageToOnlineRole(resp,null);
			return result;
		}
		
		public boolean attack(long time){
			ExpediteTroops troops = world.getObject(ExpediteTroops.class,troopsId);
			if (troops == null){
				return true;
			}
			long start = troops.getTimer().getStart();
			if (time > start + outTime){//时间已经过了,瞬间算完所有的攻击过程
				if (!beginAttack){
					long as = inTime;
					while (as < outTime){
						int result = compute(true,troops);
						if (result == 1){
							break;
						}else if (result == 2){
							continue;
						}
						as += attSpeed;
					}
				}
				return true;
			}else{
				if (time >= start + inTime){//进入射程
					if (!beginAttack){
						//发送邮件给部队
						Map<String,Object> datas = new HashMap<String, Object>();
						datas.put("content", I18nGreeting.MSG_UNION_TOWNER_ATTACK);
						List<Object> params = new ArrayList<Object>();
						params.add(getCityName());
						params.add(level);
						params.add(getName());
						datas.put("params",params);
						String sendStr = JsonUtil.ObjectToJsonString(datas);
						for (int i = 0 ; i < troops.getTeams().size() ; i++){
							TroopsData td = troops.getTeams().get(i);
							Role role = world.getRole(td.getInfo().getUid());
							chatMgr.creatBattleReportAndSend(sendStr,ReportType.TYPE_SYSTEM_MAIL,null,role);
						}
					}
					beginAttack = true;
				}
				if (beginAttack){
					int desTime = (int)(start + inTime - time);
					if (desTime % attSpeed == 0 && !attackTimes.contains(desTime)){//攻击
						attackTimes.add(desTime);
						return compute(false,troops) == 1;
					}
				}
			}
			return false;
		}

		public void copy(LockTroops lock) {
			inTime = lock.inTime;
			outTime = lock.outTime;
		}
	}
	
	private List<ArmyEntity> getCouldAttackArmy(ExpediteTroops troops){
		List<ArmyEntity> result = new ArrayList<ArmyEntity>();
		for (int i = 0 ; i < troops.getTeams().size() ; i++){
			TroopsData td = troops.getTeams().get(i);
			for (int j = 0 ; j < td.getArmys().size() ; j++){
				ArmyEntity army = td.getArmys().get(j);
				if (army.getSane() <= 0){
					continue;
				}
				Army armydata = dataManager.serach(Army.class, army.getKey());
				UnitType at = UnitType.search(armydata.getUnitType());
				if (ats.contains(at)){
					result.add(army);
				}
			}
		}
		return result;
	}
	
	private LockTroops search(long troopsId){
		for (int i = 0 ; i < locks.size() ; i++){
			LockTroops lt = locks.get(i);
			if (lt.troopsId == troopsId){
				return lt;
			}
		}
		return null;
	}
	
	/**
	 * 检查是否可以锁定这个行军队列
	 * @param troops
	 */
	public void tryToLock(ExpediteTroops troops){
		MapUnionCity city = getCity();
		if (city != null && !city.isMain()){//建筑废弃了
			return;
		}
		if (troops.getTimer().getType() != TimerLastType.TIME_EXPEDITE_FIGHT || 
			info.getUnionId() == troops.getLeader().getInfo().getUnionId() || 
			getCouldAttackArmy(troops).size() == 0){
			return;
		}
		MapObject targetObj = mapWorld.searchObject(troops.getTargetPosition());
		if (targetObj == null || !targetObj.checkUnion(info.getUnionId())){
			//这个行军的目标不是我的盟友
			return;
		}
		List<Integer> ranges = MapUtil.computeIndexs(position,arw,arh);
		List<PointVector> points = MapUtil.computeLinePoints(troops.getStartPosition(),troops.getTargetPosition());
		boolean inFlag = false , outFlag = false;
		float castTime = 0;//行军需要的时间
		LockTroops lock = null;
		for (int i = 0 ; i < points.size() -1 ; i++){
			PointVector cp =  points.get(i);
			PointVector np =  points.get(i+1);
			PointVector pp = cp.center(np);//两点的中心点
			int pos = pp.getPosition();
			if (ranges.contains(pos)){
				if (!inFlag){
					inFlag = true;
					if (lock == null){
						lock = new LockTroops();
						lock.troopsId = troops.getId();
						synchronized (addList) {
							addList.add(lock);
						}
					}
					lock.inTime  = (int)Math.floor(castTime);
				}
			}else{//没在格子里面
				if (inFlag && !outFlag){
					outFlag = true;
					lock.outTime = (int)Math.floor(castTime);
				}
			}
			MapCell cell = mapWorld.getMapCell(pos);
			float speed = troops.getSpeed((long)castTime);
			if (cell.isSlow()){//减速
				speed *= GameConfig.MAP_SPEED_SLOW;
			}
			castTime += cp.distance(np) / speed;
		}
		if (lock != null && !outFlag){
			lock.outTime = (int)Math.floor(castTime);
		}
	}
	
	@Override
	public void _init(){
		Worldbuildinglevel wbl = getLevelData();
		List<String> datas = wbl.getParamList();
		attPower = Integer.parseInt(datas.get(0));
		attSpeed = Integer.parseInt(datas.get(1));
		String str = datas.get(2);
		String[] ss = str.split(":");
		for (int i = 0 ; i < ss.length ; i++){
			String s = ss[i];
			int num = Integer.parseInt(s);
			ats.add(UnitType.search(num));
		}
		arw = Integer.parseInt(datas.get(3));
		arh = Integer.parseInt(datas.get(4));
	}
	
	@Override
	public String serializeSelf() {
		return null;
	}

	private void lockTroops(){
		List<ExpediteTroops> troopses = world.getListObjects(ExpediteTroops.class);
		for (int i = 0 ; i < troopses.size() ; i++){
			ExpediteTroops troops = troopses.get(i);
			tryToLock(troops);
		}
	}
	
	@Override
	public void deserializeSelf(String str) {
		MapUnionCity city = getCity();
		if (city != null && !city.isMain()){//建筑废弃了
			return;
		}
		lockTroops();
		TimerLast timer = buildTimer;
		if (timer == null){
			timer = new TimerLast(TimeUtils.nowLong() / 1000,0,TimerLastType.TIME_FOREVER);
		}
		taskPool.mapTread.addObj(this,timer);
	}

	@Override
	public void _finish(int type) {
		buildTimer = null;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_TOWER;
	}

	@Override
	public void _tick(long now) {
		super._tick(now);
		synchronized (addList) {
			if (addList.size() > 0){
				for (int i = 0 ; i < addList.size() ; i++){
					LockTroops lock = addList.get(i);
					LockTroops lt = search(lock.troopsId);
					if (lt != null){
						lt.copy(lock);
					}else{
						locks.add(lock);
					}
				}
				addList.clear();
			}
		}
		//攻击逻辑
		if (locks.size() > 0){
			for (int i = 0 ; i < locks.size() ; ){
				LockTroops lockTroops = locks.get(i);
				if (lockTroops.attack(now / 1000)){
					locks.remove(i);
				}else{
					i++;
				}
			}
		}
	}

	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
		out.putInt(arw);//攻击宽
		out.putInt(arh);//攻击高
	}
	
	@Override
	public void active() {
		TimerLast timer = buildTimer;
		if (timer == null){
			timer = new TimerLast(TimeUtils.nowLong() / 1000,0,TimerLastType.TIME_FOREVER);
		}
		taskPool.mapTread.addObj(this,timer);
		lockTroops();
	}

	@Override
	public void lock() {
		setMapThreadFlag(true);
	}
}
