package com.joymeng.slg.domain.map.impl.still.union.impl;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionBuild;
import com.joymeng.slg.domain.map.physics.MapCellType;

/***
 * 联盟仓库
 * @author tanyong
 *
 */
public class MapUnionWareHouse extends MapUnionBuild {
	
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
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_WAREHOUSE;
	}

	@Override
	public void serialize(JoyBuffer out) {
		super.serialize(out);
	}

	@Override
	public void _finish(int type) {
		buildTimer = null;
		setMapThreadFlag(true);
	}
}
