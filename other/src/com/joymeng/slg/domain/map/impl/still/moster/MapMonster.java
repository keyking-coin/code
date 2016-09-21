package com.joymeng.slg.domain.map.impl.still.moster;

import java.util.List;
import java.util.Map;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.data.Monster;
import com.joymeng.slg.domain.map.data.Monsterrefresh;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.MapRefreshObj;
import com.joymeng.slg.domain.map.impl.still.RebirthLogic;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.physics.PointVector;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.ConditionType;
import com.joymeng.slg.domain.object.task.TaskEventDelay;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

/**
 * 大地图没有AI的怪
 * @author tanyong
 *
 */
public class MapMonster extends MapRefreshObj {
	
	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_MONSTER;
	}
	
	@Override
	public void registerAll() {
		
	}

	
	@Override
	public String table() {
		return TABLE_RED_ALERT_MONSTER;
	}

	@Override
	public void _loadFromData(SqlData data) {
		
	}

	@Override
	public void _saveToData(SqlData data) {
		
	}

	public Monster getData(){
		Monster data = dataManager.serach(Monster.class,new SearchFilter<Monster>(){
			@Override
			public boolean filter(Monster data) {
				return data.getLevel() == level && data.getId().equals(key);
			}
		});
		return data;
	}
	
	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(key,JoyBuffer.STRING_TYPE_SHORT);//string 怪物固化编号
		out.putInt(level);//int 怪物等级
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			Monster monster = getData();
			if (monster == null){
				GameLog.info("SB策划又填错数据了");
			}
			//怪物
			if (monster != null){
				TroopsData defender = TroopsData.create(monster,position);
				boolean isWin = attackMonster(defender,expedite,false,ReportTitleType.TITLE_TYPE_MONSTER);
				Role role =world.getRole(expedite.getLeader().getInfo().getUid());
				if (isWin){
					MapUtil.drop(monster,expedite);//掉落后在发战报
					Map<Byte, Map<String, Integer>> packages = expedite.getLeader().getPackages();
					MapUtil.recordDropItems(role, key, packages);
					die();
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack monster<" + key + "> successful and destroy it at " + position);
				}else{
					LogManager.pveLog(role, EventName.RebelForces.getName(), key, (byte)0, EventName.RebelForces.getName(), "0", 0);
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack monster<" + key + "> fail at " + position);
				}
				expedite.goBackToCome();
				//任务事件
				role.handleEvent(GameEvent.TASK_CHECK_EVENT, new TaskEventDelay(),ConditionType.C_FIT_MST_T, isWin, monster.getId());
				LogManager.mapLog(role, expedite.getStartPosition(), expedite.getTargetPosition(), expedite.getId(), EventName.endOfBattle.getName());
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
					NewLogManager.mapLog(role, "attack_enemy", monster.getLevel(),(int) point.x, (int) point.y, newStr);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
			}
			fight = 0;
		}
	}

	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return true;
	}
	
	public void die(){
		//其他刷新逻辑
		Monsterrefresh mf = dataManager.serach(Monsterrefresh.class,refreshId);
		if (mf != null && mf.getDeathRefresh() > 0){//死亡重生逻辑
			RebirthLogic rl = RebirthLogic.create(RebirthLogic.REBIRTH_TYPE_MONSTER,refreshId);
			TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000,mf.getDeathRefresh(),TimerLastType.TIME_OBJ_REBIRTH);
			rl.registTimer(timer);
			rl.addSelf();
		}
		remove();
	}
}
