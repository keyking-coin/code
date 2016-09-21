package com.joymeng.slg.domain.object.skill;


public enum ActiveSkillType {
	//new  Tech145	快速生产	部队生产时间缩短20%，持续6小时
	FOOD_MATCH("Tech128"),//食物搭配
	SATEL_NAVI("Tech132"),//卫星导航
	FLASH_RETREAT("Tech136"),//火速撤军
	HIGHEST_ALERT("Tech145"),//快速生产
	URGENCY_EXPAN("Tech156"),//紧急扩军
	FULL_OF_VIT("Tech157"),//体力充沛
	URGENCY_PROD("Tech171"),//紧急抢收
	CRAZY_COLLECT("Tech176"),//疯狂采集
	RES_PROTECT("Tech180"),;//资源保护
	
	private String key;
	private ActiveSkillType(String key){
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public static ActiveSkillType search(String key){
		ActiveSkillType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ActiveSkillType type  = datas[i];
			if (type.key.equals(key)){
				return type;
			}
		}
		return null;
	}
}
