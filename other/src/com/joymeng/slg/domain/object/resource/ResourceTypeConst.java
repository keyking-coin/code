package com.joymeng.slg.domain.object.resource;


public enum ResourceTypeConst {
	RESOURCE_TYPE_FOOD("food"),//粮食
	RESOURCE_TYPE_METAL("metal"),//金属
	RESOURCE_TYPE_OIL("oil"),//石油
	RESOURCE_TYPE_ALLOY("alloy"),//钛合金
	RESOURCE_TYPE_POWER("power"),//电力
	RESOURCE_TYPE_TIME("time"),//时间
	RESOURCE_TYPE_USEREXP("userexp"),//经验
	RESOURCE_TYPE_GOLD("goldcoin"),//金币
	RESOURCE_TYPE_MATERIAL("material"),//材料
	RESOURCE_TYPE_ITEM("item"),//道具
	RESOURCE_TYPE_EQUIP("equip"),//装备
	RESOURCE_TYPE_UNION_SCORE("allianceContr"),//联盟积分
	RESOURCE_TYPE_UNION_MEMBER_SCORE("persContr"),//成员贡献度
	RESOURCE_TYPE_COIN("copper"),//铜币
	RESOURCE_TYPE_KRYPTON("krypton"),//氪金
	RESOURCE_TYPE_GEM("gem"),//宝石
	RESOURCE_TYPE_SILVER("silver"),//银币
	RESOURCE_TYPE_MONTH_CARD("monthCard"),//月卡
	RESOURCE_TYPE_POINT("point"),//活跃点数
	RESOURCE_TYPE_STAMINA("stamina")//玩家体力
	;
	
	String key;
	
	private ResourceTypeConst(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public static ResourceTypeConst search(String key){
		ResourceTypeConst[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourceTypeConst type = datas[i];
			if (type.key.equals(key)){
				return type;
			}
		}
		return null;
	}
	
	public static ResourceTypeConst search(int code){
		ResourceTypeConst[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ResourceTypeConst type = datas[i];
			if (type.ordinal() == code){
				return type;
			}
		}
		return null;
	}
}
