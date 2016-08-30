package com.joymeng.slg.domain.map.impl.still;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.FightResutTemp;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.role.Role;

/**
 * 可以被刷新的对象
 * @author tanyong
 *
 */
public abstract class MapRefreshObj extends MapObject {
	
	protected String refreshId;//刷新的编号
	
	protected int level;	//等级
	
	protected String key;	//固化编号
	
	public String getRefreshId() {
		return refreshId;
	}

	public void setRefreshId(String refreshId) {
		this.refreshId = refreshId;
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
	
	@Override
	public void loadFromData(SqlData data) {
		level              = data.getInt(RED_ALERT_GENERAL_LEVEL);
		refreshId          = data.getString(RED_ALERT_RESOURCES_REFRESHID);
		key                = data.getString(RED_ALERT_GENERAL_TYPE);
		super.loadFromData(data);
		_loadFromData(data);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_RESOURCES_REFRESHID,refreshId);
		data.put(RED_ALERT_GENERAL_TYPE,key);
		super.saveToData(data);
		_saveToData(data);
	}
	
	public boolean attackMonster(TroopsData defender,ExpediteTroops expedite,boolean send,ReportTitleType title) throws Exception{
		List<Long> attackerIds = new ArrayList<Long>();
		TroopsData attacker = expedite.getFightTroops(attackerIds);
		boolean isWin = false;
		while (attacker != null){
			if (!attackerIds.contains(attacker.getInfo().getUid())){
				attackerIds.add(attacker.getInfo().getUid());
			}
			BattleField battle = new BattleField();
			battle.add(defender,Side.DEFENSE);
			battle.add(attacker,Side.ATTACK);
			List<FightTroops> troopses_a = battle.getTroopses(Side.ATTACK);
			List<FightTroops> troopses_d = battle.getTroopses(Side.DEFENSE);
			MapRoleInfo info = attacker.getInfo();
			Role role = world.getRole(info.getUid());
			role.effectFightTroops(troopses_a,troopses_d,info.getCityId());
			BattleRecord record = battle.startFight();
			//防守方计算损耗
			List<FightTroops> troopses = battle.getTroopses(Side.DEFENSE);
			Map<Integer,FightResutTemp> defenderResult = MapUtil.computeFightResult(troopses,defender);
			//攻击方计算损耗
			troopses = battle.getTroopses(Side.ATTACK);
			Side winerSide = battle.GetWinner();
			Map<Integer,FightResutTemp> attackerResult = MapUtil.computeFightResult(troopses,attacker);
			if (winerSide == null){//本次战斗平局
				MapUtil.report(expedite,attacker,defender,position,false,battle,attackerResult,defenderResult,record,title,null,expedite.isMass());
				attacker = expedite.getFightTroops(attackerIds);
				if (attacker == null){//找不到可以出战的攻击者
					isWin = false;			
					break;
				}
			}else{
				if (winerSide.ordinal() == Side.DEFENSE.ordinal()){
					MapUtil.report(expedite,attacker,defender,position,false,battle,attackerResult,defenderResult,record,title,null,expedite.isMass());
					attacker = expedite.getFightTroops(attackerIds);
					if (attacker == null){//找不到可以出战的攻击者
						isWin = false;
						break;
					}
				}else{
					MapUtil.triggerAE_kill_monster(role,defender.getInfo().getLevel(),attackerResult.values());
					MapUtil.report(expedite,attacker,defender,position,true,battle,attackerResult,defenderResult,record,title,null,expedite.isMass());
					isWin = true;
					break;
				}
			}
		}
		return isWin;
	}
	
	public abstract void _loadFromData(SqlData data);
	
	public abstract void _saveToData(SqlData data);
}
