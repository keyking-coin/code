package com.joymeng.slg.domain.event.impl;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.GarrisonTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.impl.still.res.MapResource;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.union.impl.MapUnionResource;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.GameConfig;

public class RoleMapEvent extends AbstractGameEvent{

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.ROLE_CREATE:
			{
				RoleCityAgent city = role.getCity(0);
				MapCity mapCity = mapWorld.create(MapCity.class,true);
				mapWorld.bornAtBigMap(mapCity);
				mapCity.init(role,city);
				break;
			}
			case GameEvent.LOAD_FROM_DB:
			{
				break;
			}
			case GameEvent.CENTER_LEVE_UP:
			{
				byte level = get(params[1]);
				long uid   = role.getId();
				List<GarrisonTroops> garrisons = mapWorld.getRelevanceGarrisons(uid);
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					garrison.getTroops().getInfo().setLevel(level);
				}
				List<ExpediteTroops> expedites = mapWorld.getMyRoleExpedites(uid);
				for (int i = 0 ; i < expedites.size() ; i++){
					ExpediteTroops expedite = expedites.get(i);
					for (int j = 0 ; j < expedite.getTeams().size() ; j++){
						TroopsData troops = expedite.getTeams().get(j);
						if (troops.getInfo().getUid() == uid){
							troops.getInfo().setLevel(level);
						}
					}
				}
				List<MapObject> objs = world.getMapObjects(uid);
				for (int i = 0 ; i < objs.size() ; i++){
					MapObject obj = objs.get(i);
					obj.getInfo().setLevel(level);
				}
				break;
			}
			case GameEvent.TROOPS_SEND:
			{
				if (role.isOnline()){
					RespModuleSet rms = new RespModuleSet();
					AbstractClientModule module = new AbstractClientModule(){
						@Override
						public short getModuleType() {
							return NTC_DTCD_MAP_TROOPS;
						}
					};
					List<GarrisonTroops> garrisons = mapWorld.getRelevanceGarrisons(role.getId());
					List<MapObject> objs = new ArrayList<MapObject>();
					for (int i = 0 ; i < garrisons.size() ; i++){
						GarrisonTroops garrison = garrisons.get(i);
						if (garrison.isRemoving()){
							continue;
						}
						int position = garrison.getPosition();
						MapObject obj = mapWorld.searchObject(position);
						if (obj != null && !objs.contains(obj)){
							objs.add(obj);
						}
					}
					List<MapFortress> fortresses = mapWorld.getAllFortresses(role.getId());
					if (fortresses.size() > 0){
						for (int i = 0 ; i < fortresses.size() ; i++){
							MapFortress fortress = fortresses.get(i);
							if (!objs.contains(fortress)){
								objs.add(fortress);
							}
						}
					}
					module.add(objs.size());
					for (int i = 0 ; i < objs.size() ; i++){
						MapObject obj = objs.get(i);
						int col = obj.getPosition() % GameConfig.MAP_WIDTH;
						int row = obj.getPosition() / GameConfig.MAP_WIDTH;
						module.add(col);//int 在地图格子坐标x
						module.add(row);//int 在地图格子坐标y
						module.add(obj.getId());//long 在地图格子坐标x
						module.add(obj.cellType().ordinal());//int 地图固定对象类型编号
						module.add(obj);
					}
					List<ExpediteTroops> expedites = mapWorld.getRelevanceRoleExpedites(role.getId());
					module.add(expedites);
					rms.addModule(module);
					MessageSendUtil.sendModule(rms,role.getUserInfo());
				}
				break;
			}
		case GameEvent.UNION_FIGHT_CHANGE:
			{
				long unionId = role.getUnionId();
				UnionBody body = unionManager.search(unionId);
				if (body == null){
					return;
				}
				AbstractClientModule module = new AbstractClientModule(){
					@Override
					public short getModuleType() {
						return NTC_DTCD_UNION_FIGHT_NUM;
					}
				};
				body.sendUnionFights(module);
				RespModuleSet rms = new RespModuleSet();
				rms.addModule(module);
				boolean justToMe = get(params[1]);
				if (justToMe){
					if (role.isOnline()){
						MessageSendUtil.sendModule(rms,role.getUserInfo());
					}
				}else{
					List<UnionMember> members = body.getMembers();
					for (int i = 0 ; i < members.size() ; i++){
						UnionMember member = members.get(i);
						Role mRole = world.getRole(member.getUid());
						if (mRole != null) {
							if (mRole.isOnline()) {
								MessageSendUtil.sendModule(rms, mRole.getUserInfo());
							}
						}
					}
				}
				break;
			}
			case GameEvent.ROlE_CHANGE_BASE_INFO:{
				long uid = role.getId();
				String name = role.getName();
				List<GarrisonTroops> garrisons = mapWorld.getRelevanceGarrisons(uid);
				for (int i = 0 ; i < garrisons.size() ; i++){
					GarrisonTroops garrison = garrisons.get(i);
					garrison.getTroops().getInfo().setName(name);
					garrison.getTroops().getInfo().getIcon().copy(role.getIcon());
				}
				List<ExpediteTroops> expedites = mapWorld.getMyRoleExpedites(uid);
				for (int i = 0 ; i < expedites.size() ; i++){
					ExpediteTroops expedite = expedites.get(i);
					for (int j = 0 ; j < expedite.getTeams().size() ; j++){
						TroopsData troops = expedite.getTeams().get(j);
						if (troops.getInfo().getUid() == uid){
							troops.getInfo().setName(name);
							troops.getInfo().getIcon().copy(role.getIcon());
						}
					}
				}
				List<MapObject> objs = world.getMapObjects(uid);
				for (int i = 0 ; i < objs.size() ; i++){
					MapObject obj = objs.get(i);
					obj.getInfo().setName(name);
					obj.getInfo().getIcon().copy(role.getIcon());
				}
				break;
			}
			case GameEvent.ROLE_RES_BUFF_CHANGE:{
				long uid = role.getId();
				List<MapResource> reses = world.getListObjects(MapResource.class);
				for (int i = 0 ; i < reses.size() ; i++){
					MapResource res = reses.get(i);
					if (res.getInfo().getUid() == uid){
						res.updateCollecteBuff(role);
					}
				}
				List<MapUnionResource> murs = world.getListObjects(MapUnionResource.class);
				for (int i = 0 ; i < murs.size() ; i++){
					MapUnionResource mur = murs.get(i);
					mur.updateCollecteBuff(role);
				}
				break;
			}
		}
	}
}
