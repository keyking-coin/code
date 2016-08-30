package com.joymeng.slg.domain.map.impl.still.role;

import java.util.List;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.fight.result.ReportTitleType;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.map.spyreport.data.SpyType;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;

/**
 * 玩家迁城点
 * @author tanyong
 *
 */
public class MapCityMove extends MapObject implements TimerOver{
	
	public void registSafeTimer(TimerLast timer) {
		safeTimer = timer;
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_CITY_MOVE;
	}
	
	@Override
	public String table() {
		return TABLE_RED_ALERT_CITY_MOVE;
	}
	
	@Override
	public void loadFromData(SqlData data) {
		super.loadFromData(data);
		String str = data.getString(RED_ALERT_GENERAL_SAFETIMER);
		safeTimer = JsonUtil.JsonToObject(str,TimerLast.class);
		str = data.getString(RED_ALERT_FORTRESS_INFO);
		info      = JsonUtil.JsonToObject(str,MapRoleInfo.class);
		GarrisonTroops creater = getOwner();
		if (creater != null){
			creater.registTimer(creater.getTimer(),this);//注册建造倒计时结束逻辑
		}else{//建造部队异常了,立马迁城
			MapCity mapCity = mapWorld.searchMapCity(info);
			if (mapCity != null){
				mapCity.moveToNewPlace(position);
			}
		}
	}

	@Override
	public void saveToData(SqlData data) {
		super.saveToData(data);
		String str = JsonUtil.ObjectToJsonString(safeTimer);
		data.put(RED_ALERT_GENERAL_SAFETIMER,str);
		str = JsonUtil.ObjectToJsonString(info);
		data.put(RED_ALERT_FORTRESS_INFO,str);
	}
	
	@Override
	public void troopsArrive(ExpediteTroops expedite) throws Exception{
		synchronized (expedite) {
			if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_GARRISON){//驻防
				garrison(expedite);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_SPY){//侦查
				MapUtil.spyResport(SpyType.SPY_TYPE_FORTRESS, expedite,this);
				expedite.goBackToCome();
				logSpy(expedite);
			}else if (expedite.getTimer().getType() == TimerLastType.TIME_EXPEDITE_FIGHT){//战斗
				List<GarrisonTroops> defenders = getDefencers();//防守者
				boolean isWin = attackDefenders(defenders,expedite,ReportTitleType.TITLE_TYPE_A_MOVE,ReportTitleType.TITLE_TYPE_D_MOVE);
				if (isWin){
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s cityMove successful and destroy it at " + position);
					remove();//移除
				}else{
					GameLog.info(expedite.getLeader().getInfo().getUid() + " attack " + info.getUid() + "'s cityMove fail at " + position);
				}
				expedite.goBackToCome();
			}
		}
	}

	@Override
	public void registerAll() {
		
	}

	public GarrisonTroops getOwner(){
		List<GarrisonTroops> occupyers = getDefencers();
		for (int i = 0 ; i < occupyers.size() ; i++){
			GarrisonTroops occupyer = occupyers.get(i);
			if (occupyer.getTimer().getType().ordinal() == TimerLastType.TIME_CREATE.ordinal()){
				return occupyer;
			}
		}
		return null;
	}
	
	@Override
	public void finish() {
		//迁城逻辑
		MapCity mapCity = mapWorld.searchMapCity(info);
		List<ExpediteTroops> expedites = mapWorld.getMyRoleExpedites(info.getUid());
		for (int i = 0 ; i < expedites.size() ; i++){
			ExpediteTroops expedite = expedites.get(i);
			if(expedite.isMass()){
				continue;
			}
			expedite.tryToBackQuick(mapCity.getPosition(),3);
		}
		GarrisonTroops creater = getOwner();
		if (creater != null){//迁城部队回主城
			Role role = world.getRole(info.getUid());
			RespModuleSet rms = new RespModuleSet();
			creater.getTroops().armyBack(rms,role);
			MessageSendUtil.sendModule(rms,role.getUserInfo());
			creater.remove();
			role.handleEvent(GameEvent.TROOPS_SEND);
			LogManager.mapLog(role, info.getPosition(), position, creater.getId(),"moveCityComplete");
		}
		mapCity.moveToNewPlace(position);
	}
	
	@Override
	public boolean _couldAttack(ExpediteTroops expedite) {
		return true;
	}
}
