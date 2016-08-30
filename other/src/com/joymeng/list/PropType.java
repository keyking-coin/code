package com.joymeng.list;

public enum PropType {
	RESOURCE_TYPE_FOOD("food"), // 粮食
	RESOURCE_TYPE_METAL("metal"), // 金属
	RESOURCE_TYPE_OIL("oil"), // 石油
	RESOURCE_TYPE_ALLOY("alloy"), // 钛合金
	RESOURCE_TYPE_GOLD("money"), // 金币
	RESOURCE_TYPE_ITEM("item"), // 道具
	RESOURCE_TYPE_COIN("copper"), // 铜币
	RESOURCE_TYPE_KRYPTON("krypton"), // 氪金
	RESOURCE_TYPE_GEM("gem"), // 宝石
	RESOURCE_TYPE_SILVER("silver"), // 银币
	;

	String key;

	private PropType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
