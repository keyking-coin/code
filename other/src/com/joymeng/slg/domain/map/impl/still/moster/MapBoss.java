package com.joymeng.slg.domain.map.impl.still.moster;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.map.data.Boss;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.map.physics.MapCellType;
import com.joymeng.slg.domain.timer.TimerLast;


/**
 * boss类型
 * @author tanyong
 * 
 */
public class MapBoss extends MapMonster {
	
	List<TroopsData> troopses = new ArrayList<TroopsData>();//部队列表
	
	TimerLast troopsRebirthTimer;//部队重生时间
	
	@Override
	public MapCellType cellType() {
		return MapCellType.MAP_CELL_TYPE_BOSS;
	}
	
	@Override
	public int getVolume() {
		Boss boss = dataManager.serach(Boss.class,key);
		if (boss != null){
			return boss.getSize();
		}
		return cellType().getVolume();
	}
}
