package com.joymeng.slg.domain.map.impl.still;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
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
import com.joymeng.slg.domain.map.physics.MapCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;

/**
 * 可以被刷新的对象
 * @author tanyong
 *
 */
public abstract class MapRefreshObj extends MapObject{
	
	protected String refreshId;//刷新的编号
	
	protected int level;	//等级
	
	protected String key;	//固化编号
	
	protected TimerLast autoDie = null;//自动消失倒计时
	
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
		String str = data.getString(RED_ALERT_GENERAL_AUTO_DIE);
		if (!StringUtils.isNull(str)){
			autoDie = JsonUtil.JsonToObject(str,TimerLast.class);
			autoDie.registTimeOver(new AutoDieFinish(this));
			taskPool.mapTread.addObj(this,autoDie);
		}
		_loadFromData(data);
	}

	@Override
	public void saveToData(SqlData data) {
		data.put(RED_ALERT_GENERAL_LEVEL,level);
		data.put(RED_ALERT_RESOURCES_REFRESHID,refreshId);
		data.put(RED_ALERT_GENERAL_TYPE,key);
		super.saveToData(data);
		String str = autoDie == null ? "null" : JsonUtil.ObjectToJsonString(autoDie);
		data.put(RED_ALERT_GENERAL_AUTO_DIE,str);
		_saveToData(data);
	}
	
	@Override
	public void _tick(long now) {
		if (autoDie != null && autoDie.over(now)){
			autoDie.die();
		}
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
	/**
	 * 注册自动死亡倒计时
	 * @param time
	 */
	public void registAutoDie(long time) {
		if (time > 0){//自动死亡
			autoDie = new TimerLast(TimeUtils.nowLong() / 1000,time,TimerLastType.TIME_OBJ_AUTO_DIE);
			autoDie.registTimeOver(new AutoDieFinish(this));
			taskPool.mapTread.addObj(this,autoDie);
		}
	}
	
	class AutoDieFinish implements TimerOver{
		MapRefreshObj mro = null;
		public AutoDieFinish(MapRefreshObj mro){
			this.mro = mro;
		}
		@Override
		public void finish() {
			mro.die();
		}
	}
	
	/***
	 * 判断是否被锁定
	 * @return
	 */
	public boolean isLock(){
		if (info.getUid() > 0){
			return true;
		}
		MapCell cell = mapWorld.getMapCell(position);
		if (cell.getExpedites() != null){
			return cell.getExpedites().size() > 0;
		}
		return false;
	}
	
	public abstract void die();
	
	public abstract void _loadFromData(SqlData data);
	
	public abstract void _saveToData(SqlData data);
}
