package com.joymeng.slg.domain.object.task;

public enum ConditionType {
//	COND_NONE(0,"none"),
	COND_BUILD(1,"taskBuild"),//建筑事件
	COND_RESEARCH(2,"taskResearch"),//科技研究
	COND_TRAIN(3,"taskTrain"),//士兵训练
	COND_RESOURCE(4,"taskResource"),//资源产量变化
	COND_HEAL(5,"taskHeal"),//治疗士兵
	COND_VIP(6,"taskVIP"),//vip等级
	COND_LEVEL(7,"taskLevel"),//指挥官等级
	C_SKILL_LP(8,"roleskilllevelup"),//指挥官技能等级
	COND_ALLI_HELP(9,"taskAllianceHelp"),//帮助盟友
	COND_SOLD_UNLOK(10,"taskSoldierUnlock"),//士兵解锁
	COND_RES_HARVEST(11,"taskHarvestRes"),//内城收集资源
	COND_EQUIP_LVLUP(12,"taskEquipLevelup"),//装备升级
	COND_EQUIP_REFIN(13,"taskEquipRefining"),//装备炼化
	COND_EQUIP_RESOLV(14,"taskEquipResolve"),//装备分解
	COND_MATERIAL_PROD(15,"taskProdMaterial"),//材料生产
	COND_GET_EQUIP(16,"taskGetEquip"),//获得装备
	COND_EQUIP_WIELD(17,"taskWeildEquip"),//穿戴装备
	C_RESS_CLT(18,"collectRes"),//18 采集某种资源达到多少				参数：资源类型，数量
	C_ATK_WIN(19,"attackWin"),//20 自己进攻玩家城市的战斗				参数：战斗结果，
	C_DEF_WIN(20,"definceWin"),//21 自己被玩家攻击的防御战中胜利多少次		参数：战斗结果，
	C_FIT_MST_T(21,"fightWin"),//22	自己击杀某个怪物多少次				参数：战斗结果，
	C_MS_WIN(22,"massAttack"),//33 个人发起集结进攻并胜利次数				参数：战斗结果，
	C_HLP_DEF(23,"helpDefence"),// 34帮助盟友驻防并胜利次数				参数：战斗结果
	C_HLP_MS_WIN(24,"helpMass"),// 35帮助盟友进行集结战斗并胜利次数		参数：战斗结果，
	C_SPY_NUM(25,"spyTimes"),//34 成功侦查X次						参数：
	C_OCP_RES_T(26,"occupy"),//25 占领某类某级以上的资源地块X个			参数：资源地类型，等级
	COND_ALLI_LVLUP(27,"taskAllianceLevelup"),//联盟升级
	COND_FORT(28,"buildFortNum"),//累计建造要塞数量
	COND_ALLI_ADD(29,"addUnionMember"),//添加联盟成员
	COND_ALLI_TECH(30,"unionTechLevelup"),//联盟科技升级
	COND_ALLI_SCORE(31,"roleUnionScore"),//玩家的联盟贡献度
	COND_ALLI_BUILD(32,"unionBuildLevelup"),//联盟建筑升级
	COND_ALLI_POS(32,"roleUnionPosition"),//玩家在联盟中的职位
	COND_ALLI_OCP_CITY(33,"UnionOcpCity"),//联盟占领城池
	COND_ALLI_FIGHT(34,"unionFight"),//联盟总战斗力更新
	C_UNLOK_FIELD(35,"unlockCityField"),//解锁城池地块
	C_KING_UNIONFIGHT(35,"CountryUnionsFight"),//王国内联盟总战力更新
	C_FIGHT_RESULT(36,"FightResult"),//其他战斗统计    参数：战斗类型，杀兵数,死兵数
	C_FIGHT_BACK(37,"helpDefenceKillNum"),// 战斗部队回城后战力更新
	
	C_SIGN_CNT(38,"sign"),	//100	30日签到
	C_ONLINE_CNT(39,"online"),//101	领取在线奖励
	C_LOGIN_CNT(40,"login"),//102	领取连续登陆奖励
	C_LUCKY_CNT(41,"luckyDraw"),//103	幸运转盘
	C_ITEM_USE(42,"useItem"),	//104	使用道具
//	C_ACC_TRAIN(43,"accelerate train"),//105	训练生产加速
//	C_ACC_CURE(44,"accelerate"),	//106	治疗维修加速
	C_ACC_BUILD(45,"accelerate"),//109	建筑升级加速
//	C_ACC_TECH(46,"accelerate"),	//110	科技研究加速
	C_ALLI_JX(47,"allian"),	//111	联盟科技捐献
	C_ALLI_LB(48,"allian bag"),	//112	领取联盟礼包
	C_ALLI_PS(49,"allian"),	//113	参加联盟跑商
	C_BUY_MARK(50,"BuyMarketItem"),	//114	购买黑市商品
	C_MTL_SYNTH(51,"materials synthesis"),	//115	合成材料
	C_CHAT_WORLD(52,"chatInWorld"),//116	世界聊天
	C_VIP_ACTIVE(53,"ActiveVip"),//117	激活VIP
	C_BUY_ITEM(54,"BuyItem"),	//118	购买道具
	C_RECHARGE(55,"recharge"),	//119	充值
	C_CARBON_N(56,"normalCarbon"),	//120	通过普通副本
	C_CARBON_H(57,"hardCarbon"),	//121	通关困难副本
	C_RESS_ROB(58,"collectRes"),   // 19     掠夺某种资源达到多少				参数：资源类型，数量
	;
	String name;
	int key;
	
	private ConditionType(int key, String name){
		this.key = key;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public static ConditionType valueof(int key){
		ConditionType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ConditionType mType = datas[i];
			if(mType.key == key){
				return mType;
			}
		}
		return null;
	}

	public static ConditionType search(String key){
		ConditionType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			ConditionType mType = datas[i];
			if (mType.name.equals(key)){
				return mType;
			}
		}
		return null;
	}
}
