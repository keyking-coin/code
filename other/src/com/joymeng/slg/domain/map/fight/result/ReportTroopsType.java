package com.joymeng.slg.domain.map.fight.result;


public enum ReportTroopsType {
	TROOPS_TYPE_DESTORY_NUM,
	TROOPS_TYPE_INJU_NUM,
	TROOPS_TYPE_DIE_NUM,
	TROOPS_TYPE_ALIVE_NUM,
	TROOPS_TYPE_BUILD_NUM,
	TROOPS_TYPE_BUILD_HP;
	
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
