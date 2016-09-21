package com.joymeng.slg.domain.map.impl.still.res;


import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.still.copy.Relic;
import com.joymeng.slg.domain.map.impl.still.copy.Scene;
import com.joymeng.slg.domain.map.impl.still.copy.data.Ruins;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 副本
 * @author tanyong
 *
 */
public class MapEctype extends MapObject {

	String bulidKey;//副本的固化表ID

	
	public String getBulidKey() {
		return bulidKey;
	}

	public void setBulidKey(String bulidKey) {
		this.bulidKey = bulidKey;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_ECTYPE;
	}

	@Override
	public void registerAll() {
		
	}

	@Override
	public void _tick(long now) {
		
	}

	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
		out.putPrefixedString(bulidKey,JoyBuffer.STRING_TYPE_SHORT);//string 固化编号
	}

	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		// TODO 部队到达
		synchronized (expedite) {
			if (expedite.getTimer().getType() == TimerLastType.TIME_GO_TO_ECTYPE) {
				// 部队到达副本
				GarrisonTroops troops = expedite.occuper(this);
				TimerLast timer = new TimerLast(TimeUtils.nowLong() / 1000, 0, TimerLastType.TIME_AT_ECTYPE);
				troops.registTimer(timer);// 注册副本驻守时间
				// 玩家
				Role role = world.getRole(expedite.getLeader().getInfo().getUid());
				Ruins ruin = dataManager.serach(Ruins.class, this.getBulidKey());
				if (ruin == null) {
					GameLog.error("read Ruins table is fail");
					return;
				}
				Relic relic = new Relic(this.getBulidKey(), this.getPosition(), ruin.getType(), 1, troops.getId());
				role.addRelic(relic);
				RespModuleSet rms = new RespModuleSet();
				if(role.isOnline()){
					role.sendRoleCopysToClient(rms, true);
				}
			}
		}
	}

	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return true;
	}

	/**
	 * 检查我是不是已经在这个副本了
	 * @param role
	 * @param ectype
	 * @return
	 */
	public boolean checkMeIsInHere(Role role) {
		Ruins ruin = dataManager.serach(Ruins.class, getBulidKey());
		if (ruin == null) {
			GameLog.error("read Ruins table is fail");
			return false;
		}
		if (role.getRoleCopys().size() < 1 || role.getRoleCopys().get(ruin.getType()) == null
				|| role.getRoleCopys().get(ruin.getType()).getRelicArmys().size() < 1
				|| role.getRoleCopys().get(ruin.getType()).getRelicArmys().get(position) == null) {
			return false;
		}
		return true;
	}
	
	public GarrisonTroops searchTroops(long troopsId){
		List<GarrisonTroops> troopses = getDefencers();
		for (int j = 0 ; j < troopses.size() ; j++){
			GarrisonTroops troops = troopses.get(j);
			if (troops.getId() == troopsId){
				return troops;
			}
		}
		return null;
	}

	/**
	 * 把关卡的的奖励加入城市
	 * @param scene
	 */
	public void addSceneReward(Role role , Scene scene , RespModuleSet rms) {
		List<ItemCell> changes = new ArrayList<ItemCell>();
		List<Object> objs = new ArrayList<Object>();
		role.addPackage(scene.getPackages(),changes, objs);
		if (role.isOnline()){
			role.sendRoleToClient(rms);
			if (changes.size() > 0){
				role.getBagAgent().sendItemsToClient(rms,changes);//下发背包变化的道具修改
			}
			if (objs.size() > 0){
				role.addResourcesToCity(false,rms,0,objs.toArray());
			}
		}
	}

	@Override
	public void save() {
		//super.save();
	}

	@Override
	public boolean isMyUnionMember(MapRoleInfo info2) {
		return true;
	}
}
