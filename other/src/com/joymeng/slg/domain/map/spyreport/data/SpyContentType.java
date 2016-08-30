package com.joymeng.slg.domain.map.spyreport.data;

public enum SpyContentType {
	SPY_CITY_RESOURCE(8),					//城市中各种物资数量 //SPY_CITY_RESOURCE|food|10000|100|...
	
	SPY_CITY_ARMY_R(9),						//基础部队大致数量//SPY_CITY_ARMY_R|name|10000
	SPY_CITY_DEFENCE_INSTALLATION_R(10),	//城防设施大致数量//SPY_CITY_DEFENCE_INSTALLATION_R|10000
	SPY_CITY_GARRISON_ARMY_R(11),			//驻防部队大致数量//SPY_CITY_GARRISON_ARMY_R|?1|?|?|1000
	SPY_CITY_GARRISON_ARMY_R_LEADER(12),	//驻防部队大致数量+指挥官等级和用户名//SPY_CITY_GARRISON_ARMY_R_LEADER|name|level|icon|1000|...
	SPY_CITY_GARRISON_LEADER(13),			//指挥官等级和用户名//SPY_CITY_GARRISON_LEADER|name|level|...
	
	SPY_CITY_ARMY_NR(14),					//基础部队兵种类型数量大致//SPY_CITY_ARMY_NR|0|name|1000|...
	SPY_CITY_DEFENCE_INSTALLATION_NR(15),	//城防设施类型数量大致//SPY_CITY_DEFENCE_INSTALLATION_NR|name|1000|...
	SPY_CITY_GARRISON_ARMY_NR(16),			//驻防部队兵种类型数量大致//SPY_CITY_GARRISON_ARMY_NR|?1|?|?|armyName|pos|100|...
	SPY_CITY_GARRISON_ARMY_NR_LEADER(17),	//驻防部队兵种类型数量大致+指挥官等级和用户名//SPY_CITY_GARRISON_ARMY_NR_LEADER|name|level|icon|armyName|pos|100|...
	
	SPY_CITY_ARMY_NP(18),					//基础部队兵种类型数量精准	//SPY_CITY_ARMY_NP|0|name|1000|...
	SPY_CITY_DEFENCE_INSTALLATION_NP(19),	//城防设施类型数量精准//SPY_CITY_DEFENCE_INSTALLATION_NP|name|1000|...
	SPY_CITY_GARRISON_ARMY_NP(20),			//驻防部队兵种类型数量精准//SPY_CITY_GARRISON_ARMY_NP|?1|?|?|armyName|pos|100|...
	SPY_CITY_GARRISON_ARMY_NP_LEADER(21),	//驻防部队兵种类型数量精准+指挥官等级和用户名//SPY_CITY_GARRISON_ARMY_NP_LEADER|name|level|icon|armyName|pos|100|...
	
	SPY_CITY_DEFENCE_BUILDING_R(22),		//防御建筑类型和数量//SPY_CITY_DEFENCE_BUILDING_R|type|num|...
	SPY_CITY_DEFENCE_BUILDING_P(23),		//防御类型类型和等级//SPY_CITY_DEFENCE_BUILDING_R|type|level|...
	SPY_CITY_TECHCENTER_LEVEL(24),			//科技中心等级	//SPY_CITY_TECHCENTER_LEVEL|name|level
	SPY_CITY_DEFENCE_VALUE(25),				//城防值//SPY_CITY_DEFENCE_VALUE|currentValue|allValue

	SPY_FORTRESS_BASE_LEVEL(26),			//要塞基础数据 等级 SPY_FORTRESS_BASE_LEVEL|level
	SPY_RESOURCE_BASE_VALUE(27),			//资源田基础数据 资源量SPY_RESOURCE_BASE_VALUE|all|odd
	SPY_NPC_BASE_LEVEL(28),					//NPC城基础数据 等级SPY_NPC_BASE_LEVEL|name|level
	SPY_NPC_BASE_MONTER(29),				//NPC城基础数据 精英怪SPY_NPC_BASE_MONTER|type|num|...
	SPY_NPC_BASE_GENERAL_MONTER(30)	,		//NPC城基础数据 普通怪SPY_NPC_BASE_GENERAL_MONTER|num
	
	SPY_LEVEL_INSUFFICIENT_TIP(31),			//侦查报告的提示.(自己的权限不够,but存在部队信息)
	;
	
	int key ;
	
	private SpyContentType(int key) {
		this.key = key;
	}
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public static SpyContentType search(int key) {
		SpyContentType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			SpyContentType component = datas[i];
			if (component.key == key) {
				return component;
			}
		}
		return null;
	}
}
