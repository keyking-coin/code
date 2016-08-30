package com.joymeng.slg.domain.event.impl;

import java.util.List;

import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class UnionEvent extends AbstractGameEvent {

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.ROlE_CHANGE_BASE_INFO:{// 更新联盟成员中用户的名称
				UnionBody unionBody = unionManager.search(role.getUnionId());
				if (unionBody != null) {
					UnionMember member = unionBody.searchMember(role.getId());
					member.setName(role.getName());
					member.setLevel(role.getLevel());
					member.getIcon().copy(role.getIcon());
					unionBody.sendMemberToAllMembers(member,ClientModule.DATA_TRANS_TYPE_UPDATE);
				}
				chatMgr.updateRoleName(role);
				break;
			}
			
			case GameEvent.UNION_WAR_RECORD:{//联盟战争记录
				boolean isWin = get(params[1]);
				boolean isMass = get(params[2]);
				UnionBody atbody = get(params[3]);
				UnionBody debody = get(params[4]);//玩家uid
				if (atbody!=null){
					atbody.gmRecord(isWin,isMass,true);
				}
				if (debody!=null){
					debody.gmRecord(isWin,isMass,false);
				}
				break;
			}
			case GameEvent.UNION_JOIN:
			{
				UnionBody union = get(params[1]);
				long unionId = union.getId();
				role.setUnionId(unionId);
				GameLog.info("uid is " + role.getId() + " join in " + unionId);
				long uid = role.getId();
				List<ExpediteTroops> expedites = world.getListObjects(ExpediteTroops.class);
				for (int i = 0 ; i < expedites.size() ; i++){
					ExpediteTroops expedite = expedites.get(i);
					for (int j = 0 ; j < expedite.getTeams().size() ; j++){
						TroopsData troops = expedite.getTeams().get(j);
						if (troops.getInfo().getUid() == uid){
							troops.getInfo().setUnionId(unionId);
						}
					}
				}
				List<GarrisonTroops> garrisons = world.getListObjects(GarrisonTroops.class);
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					TroopsData troops = garrison.getTroops();
					if (troops.getInfo().getUid() == uid){
						troops.getInfo().setUnionId(unionId);
					}
				}
				List<MapObject> objs = world.getMapObjects(uid);
				for (int i = 0 ; i < objs.size() ; i++){
					MapObject obj = objs.get(i);
					obj.getInfo().setUnionId(unionId);
				}
				List<MapCity> citys = world.getListObjects(MapCity.class);
				for (int i = 0 ; i < citys.size() ; i++){
					MapCity city = citys.get(i);
					if (city.getMass() != null){
						if (city.getMass().getTargetInfo().getUid() == uid){
							city.getMass().getTargetInfo().setUnionId(unionId);
						}
					}
				}
				role.sendViews(new RespModuleSet(),true);
				break;
			}
			case GameEvent.UNION_EXIT:
			{
				long uid = role.getId();
				List<GarrisonTroops> garrisons = world.getListObjects(GarrisonTroops.class);
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					TroopsData troops = garrison.getTroops();
					if (troops.getInfo().getUid() == uid && garrison.getTimer().getType() == TimerLastType.TIME_MAP_MASS){//集结部队除外
						troops.getInfo().setUnionId(0);
						int pos = garrison.getPosition();
						MapObject obj = mapWorld.searchObject(pos);
						if (obj != null){
							boolean isOwner = false;
							if (obj.getInfo().getUid() == uid){
								isOwner = true;
							}
							if (isOwner){//我是建筑的主人，其他人就回家
								List<GarrisonTroops> others = MapUtil.computeGarrisons(pos);
								for (int j = 0 ; j < others.size() ; j++){
									GarrisonTroops other = others.get(j);
									if (other.getTroops().getInfo().getUid() == uid){
										continue;
									}
									other.die();
								}
							}else{//我不是建筑的主人，我回家
								garrison.die();
							}
						}
					}
				}
				List<MapObject> objs = world.getMapObjects(uid);
				for (int i = 0 ; i < objs.size() ; i++){
					MapObject obj = objs.get(i);
					obj.getInfo().setUnionId(0);
				}
				break;
			}		
		}
	}
}
