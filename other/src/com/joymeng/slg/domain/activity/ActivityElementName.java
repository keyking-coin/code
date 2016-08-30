package com.joymeng.slg.domain.activity;



public enum ActivityElementName {
	ACTIVITY_ELEMENT_NAME_SHOP("ShopLayout");
	
	private String key;
	
	public String getKey() {
		return key;
	}
	
	private ActivityElementName(String key){
		this.key = key;
	}
	
	public static ActivityElementName search(String key){
		ActivityElementName[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ActivityElementName type = datas[i];
			if (type.key.equals(key)){
				return type;
			}
		}
		return null;
	}
}
