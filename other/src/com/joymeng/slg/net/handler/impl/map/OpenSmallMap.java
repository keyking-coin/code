package com.joymeng.slg.net.handler.impl.map;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.map.impl.still.role.MapCityMove;
import com.joymeng.slg.domain.map.impl.still.role.MapFortress;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.CommunicateResp;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class OpenSmallMap extends ServiceHandler {

	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		
	}

	@Override
	public JoyProtocol handle(UserInfo info, ParametersEntity params)
			throws Exception {
		CommunicateResp resp = newResp(info);
		Role role = getRole(info);
		if (role == null){
			resp.fail();
			return resp;
		}
		List<MapCity>      mmcs  = mapWorld.searchMapCity(info.getUid());
		resp.add(mmcs.size());
		for (int i = 0 ; i < mmcs.size() ; i++){
			MapCity mc = mmcs.get(i);
			resp.add(mc.getPosition());
		}
		List<MapFortress>  mmfs  = mapWorld.getAllFortresses(info.getUid());
		resp.add(mmfs.size());
		for (int i = 0 ; i < mmfs.size() ; i++){
			MapFortress mf = mmfs.get(i);
			resp.add(mf.getPosition());
		}
		List<MapCity>      omcs  = new ArrayList<MapCity>();
		List<MapFortress>  omfs  = new ArrayList<MapFortress>();
		UnionBody union = unionManager.search(role.getUnionId());
		if (union != null){
			for (int i = 0 ; i < union.getMembers().size() ; i++){
				UnionMember member = union.getMembers().get(i);
				if (member.getUid() == info.getUid()){
					continue;
				}
				List<MapCity> tmcs = mapWorld.searchMapCity(member.getUid());
				omcs.addAll(tmcs);
				List<MapFortress> tmfs = mapWorld.getAllFortresses(member.getUid());
				omfs.addAll(tmfs);
			}
		}
		resp.add(omcs.size());
		for (int i = 0 ; i < omcs.size() ; i++){
			MapCity mc = omcs.get(i);
			resp.add(mc.getPosition());
		}
		resp.add(omfs.size());
		for (int i = 0 ; i < omfs.size() ; i++){
			MapFortress mf = omfs.get(i);
			resp.add(mf.getPosition());
		}
		List<MapCityMove>  mcms = mapWorld.searchCityMove(info.getUid());
		resp.add(mcms.size());
		for (int i = 0 ; i < mcms.size() ; i++){
			MapCityMove mcm = mcms.get(i);
			resp.add(mcm.getPosition());
		}
		List<MapUnionCity> mucs = world.getListObjects(MapUnionCity.class);
		resp.add(mucs.size());
		for (int i = 0 ; i < mucs.size() ; i++){
			MapUnionCity muc = mucs.get(i);
			resp.add(muc.getState());
			resp.add(muc.getLevel());
			resp.add(muc.getUnionId());
			resp.add(muc.getPosition());
			resp.add(muc.getBuilds());
		}
		return resp;
	}

}
