package com.joymeng.slg.domain.map.fight.obj.enumType;

/**
 * 护甲类型，主要是计算伤害时用
 * @author tanyong
 *
 */
public enum UnitType {
	TROOP_GROUD,
	TROOP_AIR,
	TROOP_WALL;
	public static UnitType search(int key){
		UnitType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			UnitType ut = datas[i];
			if (ut.ordinal() == key){
				return ut;
			}
		}
		return null;
	}
}
