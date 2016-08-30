package com.joymeng.slg.domain.map.impl.still.union.impl;

import java.util.List;

import com.joymeng.slg.domain.map.data.Worldbuildinglevel;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;

/**
 * buff类型的建筑
 * @author tanyong
 *
 */
public class MapUnionOther extends MapUnionBuild {
	
	@Override
	public void _init(){
		
	}
	
	@Override
	public String serializeSelf() {
		return null;
	}

	@Override
	public void deserializeSelf(String str) {
		
	}

	@Override
	public void _finish(int type) {
		buildTimer = null;
		UnionBody union = unionManager.search(info.getUnionId());
		if (union != null) {
			union.updateBuildBuff(this,type);
		}
		setMapThreadFlag(true);
	}


	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_OTHER;
	}
	
	public void addBuff(Role role){
		addBuff(role,level);
	}
	
	public void addBuff(Role role,int level){
		Worldbuildinglevel wbl = getLevelData(level);
		if (wbl != null){
			List<String> buffStrs = wbl.getParamList();
			for (int i = 0 ; i < buffStrs.size() ; i++){
				String  buffStr = buffStrs.get(i);
				String[] bss = buffStr.split(":");
				role.getEffectAgent().addTechBuff(role,bss[1], bss[2], bss[3]);
			}
		}
	}
	
	public void removeBuff(Role role){
		Worldbuildinglevel wbl = getLevelData();
		if (wbl != null){
			List<String> buffStrs = wbl.getParamList();
			for (int i = 0 ; i < buffStrs.size() ; i++){
				String  buffStr = buffStrs.get(i);
				String[] bss = buffStr.split(":");
				role.getEffectAgent().removeTechBuff(role,bss[3]);
			}
		}
	}

	@Override
	public void active() {
		UnionBody union = unionManager.search(info.getUnionId());
		if (union != null) {
			union.updateBuildBuff(this,1);
		}
	}

	@Override
	public void lock() {
		UnionBody union = unionManager.search(info.getUnionId());
		if (union != null) {
			union.updateBuildBuff(this,3);
		}
	}
	
}
