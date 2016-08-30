package com.joymeng.slg.domain.map.spyreport.data;

public class SpyType {
	public static final byte SPY_TYPE_CITY = 0;	//城市
	public static final byte SPY_TYPE_FORTRESS = 1;//要塞/军营
	public static final byte SPY_TYPE_RESOURCE = 2;//资源田
	public static final byte SPY_TYPE_NPC = 3;//NPC城
	
	public static final byte SPY_TYPE_CITY_D = 4;	//城市 被侦查
	public static final byte SPY_TYPE_FORTRESS_D = 5;//要塞 被侦查
	public static final byte SPY_TYPE_BARRACK_D = 14;//军营 被侦查
	public static final byte SPY_TYPE_RESOURCE_D = 6;//资源田 被侦查
	public static final byte SPY_TYPE_NPC_D = 7;//NPC城 被侦查
	
	public static final byte SPY_TYPE_CITY_MOVE = 8;//迁城点
	public static final byte SPY_TYPE_CITY_MOVE_D= 9;	//迁城点 被侦查
	public static final byte SPY_TYPE_GARRISON = 10;//驻防点
	public static final byte SPY_TYPE_GARRISON_D= 11;	//驻防点 被侦查
	
	public static final byte SPY_TYPE_UNION_BUILD = 12;//联盟建筑
	public static final byte SPY_TYPE_UNION_BUILD_D= 13;	//联盟建筑 被侦查
}
