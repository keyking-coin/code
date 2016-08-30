package com.joymeng.slg.domain.map.impl.still.union.impl;

import com.joymeng.slg.domain.map.physics.MapCellType;

/**
 * 联盟心灵探测仪,功能和联盟间谍卫星一样
 * @author tanyong
 *
 */
public class MapUnionDetector extends MapUnionSatellite {
	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_UINON_DETECTOR;
	}
}
