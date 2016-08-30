package com.joymeng.slg.domain.map.impl.still.union.impl;

import java.util.List;

import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.union.UnionBody;
/**
 * 联盟间谍卫星
 * @author tanyong
 *
 */
public class MapUnionSatellite extends MapUnionBuild {
	
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
	public void range(int[] result) {
		MapUnionCity city = getCity();
		if (city != null && !city.isMain()){//建筑废弃了
			return;
		}
		List<String> views = getLevelData().getParamList();
		result[0] = Integer.parseInt(views.get(0));
		result[1] = Integer.parseInt(views.get(1));
	}
	
	@Override
	public void _finish(int type) {
		buildTimer = null;
		setMapThreadFlag(true);
		UnionBody union = unionManager.search(info.getUnionId());
		if (union != null){
			union.sendViewsToAllMember();
		}
	}

	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_SATELLITE;
	}
	
	@Override
	public boolean destroy(String buffStr) {
		super.destroy(buffStr);
		UnionBody union = unionManager.search(info.getUnionId());
		if (union != null){
			union.sendViewsToAllMember();
		}
		return false;
	}
}
