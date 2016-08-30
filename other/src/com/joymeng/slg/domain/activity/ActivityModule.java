package com.joymeng.slg.domain.activity;

public enum ActivityModule {
	ACTIVITY_MODULE_NAME_SHOP("ChargeShop"),//活动商店
	ACTIVITY_MODULE_NAME_KAZUKA("kazuka");//七日冲级任务
	private String key;
	public String getKey() {
		return key;
	}
	private ActivityModule(String key){
		this.key = key;
	}
}
