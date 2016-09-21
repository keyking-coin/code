package com.joymeng.slg.domain.timer;


public enum TimerLastType {
	TIME_FOREVER("FOREVER",true),//永久
	TIME_CREATE("CREATE",true),//创建
	TIME_TRAIN("TRAIN",true),//训练
	TIME_LEVEL_UP("LEVEL",true),//升级
	TIME_REMOVE("REMOVE",true),//拆除建筑
	TIME_RESEARCH("RESEARCH",true),//研究
	TIME_CURE("CURE",true),//治疗
	TIME_QUEUE("QUEUE",true),//队列的倒计时类型
	TIME_REPAIR_DEFENSE("REPAIR",true),//城墙修理
	TIME_MAP_COLLECT("MAPCOLLECT",true),//在大地图采集
	TIME_EXPEDITE_GARRISON("GOTOGARRISON",true),//去大地图驻防
	TIME_MAP_GARRISON("MAPGARRISON",false),//在大地图驻防
	TIME_EXPEDITE_STATION("GOTOSTATION",true),//去大地图驻扎
	TIME_MAP_STATION("MAPSTATION",false),//在大地图驻扎
	TIME_CITY_FIRE("CITYFIRE",true),//城池失火
	TIME_PD_GEM("GEM",true), //生产宝石
	TIME_ARMY_BACK("BACKCITY",true),//部队返回主城
	TIME_ARMY_BACK_FORTRESS("BACKFORTRESS",true),//部队返回要塞
	TIME_MAP_OBJ_SAFE("SAFETIME",true),//保护时间
	TIME_ITEM_LAST("ITEMLAST",true),//道具持续时间
	TIME_EXPEDITE_CREATE_MOVE("CREATEMOVE",true),//去空地建造迁城点
	TIME_EXPEDITE_CREATE_FORTRESS("CREATEFORTRESS",true),//去空地建造要塞
	TIME_UP_EQUIP("UPGRADEEQUIP",true),//装备升级
	TIME_PROXY_OBJ_DEL("DELETEPROXY",true),//代理类的删除倒计时
	TIME_SKILL_CD("SKILLCDTIME",true),//主动技能cd时间
	TIME_SKILL_LAST("SKILLLASTTIME",true),//技能效果持续时间
	TIME_VIP("VIPTIME",true),//vip持续时间
	TIME_EXPEDITE_FIGHT("GOTOFIGHT",true),//去战斗
	TIME_NPC_TROOPS_CURE("NPC_CURE",true),//NPC部队恢复
	TIME_EXPEDITE_SPY("GOTOSPY",true),//去侦查
	TIME_CITY_MOVE_IN_RECENT("DO_CITY_MOVE",true),//3秒迁城倒计时
	TIME_UNION_TECH("UNION_TECH",true), //联盟科技倒计时
	TIME_DAILY_REWARD("DAILY_REWARD",true),//在线奖励
	TIME_NPC_OCCUPY_END("NPC_OCCUPY",true),//NPC部队恢复
	TIME_CITY_NOWAR("WAR_PROTECT",true),//防护罩
	TIME_CITY_NOSPY("ATISPY",true),//防侦查
	TIME_CITY_DBSPY("CAMOUFLAGE",true),//侦查伪装
	TIME_CITY_RESPRT("RESPROTECT",true),//资源保护
	TIME_ITEM_IMP_FOOD("ITEM_FOOD",true),//提升食品产量
	TIME_ITEM_IMP_METAL("ITEM_METAL",true),//提升金属产量
	TIME_ITEM_IMP_OIL("ITEM_OIL",true),//提升石油产量
	TIME_ITEM_IMP_ALLOY("ITEM_ALLOY",true),//提升合金产量
	TIME_ITEM_IMP_RES("ITEM_RES",true),//
	TIME_ITEM_TROOPS_LIMIT("TROOPS_LIMIT",true),//出征上限
	TIME_ITEM_IMP_DEF("DEF_ADD",true),//增加防御
	TIME_ITEM_IMP_ATK("ATK_ADD",true),//增加攻击
	TIME_ITEM_RED_FOOD("FOOD_CONSUME_RED",true),//粮食消耗减少
	TIME_ITEM_IMP_COLL("RESOURCE_COLLECTION",true),//采集加速
	TIME_MAP_MASS("MAP_MASS",false),//在大地图集结
	TIME_MAP_MASS_END("MAP_MASS_END",true),//集结结束
	TIME_EXPEDITE_MASS("GO_TO_MASS",true),//去集结
	TIME_REP_DEFENSE("DEFENSE_REPAIR",true),//防御建筑修理
	TIME_MAP_UNION_NUCLEARSILO_COOL("MAP_NUCLEARSILO_COOL",true),//联盟核弹发射井冷却时间
	TIME_EXPEDITE_UNION_RES_COLLECT("MAP_UNION_COLLECT",true),//联盟采集
	TIME_EFFECT("EFFECT_TIME",true),//buff倒计时
	TIME_MAP_RADIATION_DIE("MAP_RADIATION_DIE",true),//核辐射地块倒计时
	TIME_SHOP_LIMIT_BUY("SHOP_LIMIT",true),//商店限购倒计时
	TIME_BACK_SPY("BACKSPY",true),//侦查回城
	TIME_BLACK_MARKET_REFRESH("MARKET_REFRESH",true),//黑市刷新倒计时
	TIME_OBJ_AUTO_DIE("AUTO_DIE",true),//自动死亡倒计时
	TIME_OBJ_REBIRTH("AUTO_REBIRTH",true),//死亡自动刷新
	TIME_UNION_DONATE("UNION_DONATE",true),//联盟捐赠倒计时
	TIME_CITY_VIEW_BUFF("CITY_VIEW_BUFF",true),//城市视野buff
	TIME_FORT_VIEW_BUFF("FORT_VIEW_BUFF",true),//要塞视野buff
	TIME_CITY_TRADE_CD("TRADE_CD_CITY",true),//资源交易CD时间
	TIME_GO_TO_ECTYPE("GO_TO_ECTYPE",true),//去副本的路上
	TIME_AT_ECTYPE("AT_ECTYPE",false),//在副本待着
	TIME_MONSTER_ATTACK("MONSTER_ATTACK",true),//怪物攻城
	TIME_ARMY_PROMOT("ARMY_PROMOT",true),//兵种晋级
	TIME_JOIN_UNION("JOIN_UNION",true)//加入联盟需要的CD
	;
	
	String key;
	boolean flag;//时间可以流失
	private TimerLastType(String key,boolean flag){
		this.key = key;
		this.flag = flag;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public static TimerLastType search(String key){
		TimerLastType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			TimerLastType component = datas[i];
			if (component.key.equals(key)){
				return component;
			}
		}
		return null;
	}
}
