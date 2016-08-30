package com.joymeng.slg.domain.map.fight.obj.enumType;


public enum ArmyType {
	NONE,//站位用
	FOOT,//步兵
	ROBOT,//机器人
	TANK,//装甲车或者坦克
	PLANE,//飞机
	HOOK,//陷阱
	TOWNER;//防御塔
	
	public static ArmyType search(int type){
		ArmyType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ArmyType at = datas[i];
			if (at.ordinal() == type){
				return at;
			}
		}
		return null;
	}
}
