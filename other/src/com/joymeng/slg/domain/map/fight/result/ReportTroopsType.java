package com.joymeng.slg.domain.map.fight.result;


public enum ReportTroopsType {
	TROOPS_TYPE_DESTORY_NUM,//消灭数量
	TROOPS_TYPE_INJU_NUM,//受伤数量
	TROOPS_TYPE_DIE_NUM,//死亡数量
	TROOPS_TYPE_ALIVE_NUM,//存活
	TROOPS_TYPE_BUILD_NUM,//防御设施损失
	TROOPS_TYPE_BUILD_HP;//防御设施损伤
	
	public static ReportTroopsType search(int code){
		ReportTroopsType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ReportTroopsType type = datas[i];
			if (type.ordinal() == code){
				return type;
			}
		}
		return null;
	}
}
