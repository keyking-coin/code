package com.joymeng.slg.domain.activity;


public enum ActivityOperationType {
	ACTIVITY_OPERATION_TYPE_INSERT("insert"),
	ACTIVITY_OPERATION_TYPE_DELETE("delete"),
	ACTIVITY_OPERATION_TYPE_UPDATE("update")
	;
	private String key;
	public String getKey() {
		return key;
	}
	private ActivityOperationType(String key){
		this.key = key;
	}
	
	public static ActivityOperationType search(String key){
		ActivityOperationType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ActivityOperationType type = datas[i];
			if (type.key.equals(key)){
				return type;
			}
		}
		return null;
	}
}
