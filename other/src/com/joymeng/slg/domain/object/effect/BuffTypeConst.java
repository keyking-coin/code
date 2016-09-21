package com.joymeng.slg.domain.object.effect;

public class BuffTypeConst {
	//
	public enum TargetType {
		G_R_IMP_VS(1,"R_ImpVitSp",1),//体力恢复速度
		G_C_IMP_BS(2,"C_ImpBuildSpeed",1),//提升建造速度
		G_C_IMP_RS(3,"C_ImpResSpeed",1),//提升研究速度
		G_C_ADD_FTN(4,"C_AddFortNum",1),	//增加要塞上限数
		G_C_IMP_UCV(5,"C_ImpUserCityVision",1),//扩大玩家城市的视野	
		G_C_IMP_UFV(6,"C_ImpUserFortVision",1),//扩大要塞/军营的视野	
		G_C_IMP_NCV(7,"C_ImpNpcCityVision",1),//扩大NPC城市的视野
		C_ADD_BUILD_QUEUE(8,"C_AddbuildQueue",1),//增加建筑队列时间
		T_B_ADD_TL(9,"B_AddTroopsLimit",1),	//出征部队上限增加1支
		T_A_RED_FCT(10,"A_ReduFoodCollTime",2),//减少食品采集时间
		T_A_RED_MCT(11,"A_ReduMetalCollTime",2),//减少金属采集时间
		T_A_RED_OCT(12,"A_ReduOilCollTime",2),	//减少石油采集时间
		T_A_RED_ACT(13,"A_ReduAlloyCollTime",2),//减少钛合金采集时间
		C_A_IMP_FCS(14,"A_ImpFoodCollSpeed",1),//提升食品采集速度	
		C_A_IMP_MCS(15,"A_ImpMetalCollSpeed",1),//提升金属采集速度	
		C_A_IMP_OCS(16,"A_ImpOilCollSpeed",1),//提升石油采集速度	
		C_A_IMP_ACS(17,"A_ImpAlloyCollSpeed",1),//提升合金采集速度
		B_ADD_FOOD_PROD(18,"B_AddFoodProd",1),//增加资源建筑物食品产量
		B_ADD_METAL_PROD(19,"B_AddMetalProd",1),//增加资源建筑物金属产量
		B_ADD_OIL_PROD(20,"B_AddOilProd",1),//增加资源建筑物石油产量
		B_ADD_ALLOY_PROD(21,"B_AddAlloyProd",1),//增加资源建筑物钛合金产量
		G_B_IMP_RT(22, "B_ImpProtect",1),		//增加资源掠夺保护比例上限
		T_B_IMP_FP(23,"B_ImpFoodProd",1),		//食品生产率提升
		T_B_IMP_MP(24,"B_ImpMetalProd",1),	//金属生产率提升
		T_B_IMP_OP(25,"B_ImpOilProd",1),		//石油生产率提升
		T_B_IMP_AP(26,"B_ImpAlloyProd",1),	//钛合金生产率提升
		T_B_ADD_SLT(27,"B_AddStorageLimit",1),//仓库保护资源数量增加
		T_B_ADD_SL(28,"B_AddSoldLimit",1),		//单支部队兵力上限增加
		T_B_ADD_FHP(29,"B_AddFenceHp",1),		//城防值增加
		T_B_ADD_FS(30,"B_AddFenceSpace",1),	//城防空间增加
		T_B_ADD_WL(31,"B_AddWarLimit",1),		//增加战争大厅的部队队伍上限
		G_B_ADD_SDN(32,"B_AddSoldNum",1),		//增加集结的部队空间
		T_B_ADD_PP(33,"B_AddPowerProd",1),		//发电厂的电力产量提升	
		T_B_ADD_HC(34,"B_AddHospCapa",1),		//医院伤兵数量上限提升
		T_B_ADD_RC(35,"B_AddRepaCapa",1),		//增加维修厂的受损机械容量
		T_B_RED_HT(36,"B_ReduHospTime",2),	//减少医院的伤兵的治疗时间
		T_B_RED_RT(37,"B_ReduRepaTime",2),	//减少维修厂的受损机械的维修时间
		T_A_RED_RRHR(38,"B_ReduRepaRes",2),	//治疗伤兵的资源降低, 维修机械的资源降低
		T_A_RED_DR(39,"A_ReduDeathRate",1),	//提升伤兵率
		T_B_ADD_SPL(40,"B_AddSProdLimit",1),	//单次训练的士兵数量增加
		T_A_RED_SPT(41,"A_ReduProdTime",2),	//士兵生产时间缩短
		T_A_ADD_IC(42,"A_ImpColl",1),			//提升部队采集速度
		T_A_IMP_SW(43,"A_ImpWeight",1),	//部队负重提升
		T_A_RED_SC(44,"A_Redufood",2),		//部队消耗食品降低
		T_A_IMP_SS(45,"A_ImpMobi",1),		//提升部队的机动力
		C_A_RED_MB(46,"A_ReduMobi",2),//降低部队机动力
		T_A_IMP_SA(47,"A_ImpAtk",1),		//部队火力提升
		T_A_IMP_SD(48,"A_ImpDef",1),		//部队防御提升
		T_A_IMP_AHP(49,"A_ImpHp",1),		//生命值提升
		T_A_IMP_DMG(50,"A_ImpDamage",1),	//增加部队的伤害
		C_A_RED_BDMG_ALL(51,"A_ReduBearDMG_All",1),//降低全兵种受到的伤害	
		C_A_RED_ATK(52,"A_ReduAtk",4),//降低部队的火力	
		C_A_RED_DEF(53,"A_ReduDef",4),//降低部队的防御	
		C_A_RED_HP(54,"A_ReduHp",4),//降低部队的生命值	
		C_A_RED_DMG(55,"A_ReduDamage",4),//降低部队造成的伤害
		C_A_RED_BDMG(56,"A_ReduBearDMG",1),//降低类部队受到的伤害
		T_A_IMP_ICR(57,"A_ImpCritRate",1),//提升部队的暴击值
		C_A_RED_CRT(58,"A_ReduCritRate",4),//降低部队的暴击值	
		T_A_IMP_IAR(59,"A_ImpAttackRate",1),//提升部队的命中值
		C_A_RED_ATR(60,"A_ReduAttackRate",4),//降低部队的命中值	
		T_A_IMP_IER(61,"A_ImpEvadeRate",1),//提升部队的闪避值
		C_A_RED_EDR(62,"A_ReduEvadeRate",4),//降低部队的闪避值	
		A_IMP_DAMAGE(63,"AL_ImpDamage",1),	//提升联盟防御建筑的伤害
		G_C_ADD_F(64,"C_Addfood",1), 	//添加食品资源，固定数量
		G_C_ADD_M(65,"C_Addmetal",1),	//添加金属资源，固定数量
		G_C_ADD_O(66,"C_Addoil",1),  	//添加石油资源，固定数量
		G_C_ADD_A(67,"C_Addalloy",1),	//添加合金资源，固定数量
		G_C_ADD_KPT(68, "C_Addkrypton",1),//增加城市拥有的氪金
		G_C_ADD_GEM(69, "C_Addgem",1),//增加城市拥有的宝石
		G_C_ADD_COP(70, "C_Addcopper",1),//增加城市拥有的铜币
		G_C_ADD_SLV(71, "C_Addsilver",1),//增加城市拥有的银币
		G_C_REDU_T(72,"C_ReduTime",1),//加速，固定时间
		G_C_REDU_BT(73,"C_ReduBuildTime",1),	//建筑加速,固定时间
		G_C_RED_RT(74,"C_ReduResTime",1),	//科技加速，固定时间
		G_C_RED_ST(75,"C_ReduSoldProdTime",1),//士兵生产加速，固定时间
		G_C_RED_CT(76,"C_ReduSoldCureTime",1),//治疗加速，固定时间
		G_C_RED_AMT(77,"C_ReduArmyMoveTime",1),//行军加速，固定时间
		G_C_NO_WAR(78,"Nowar",1),	//战争守护，固定时间
		G_C_NO_SPY(79,"Nospy",1),	//反侦察，固定时间
		G_C_DB_SPY(80,"DBnum",1),	//伪装术，固定时间
		G_C_ADD_PR(81,"C_AddProt",1),	//资源保护不被掠夺
		G_C_RAN_MOVE(82,"C_RandomCity",1),//随机迁城
		G_C_DIR_MOVE(83,"C_DirectedCity",1),	//高级迁城
		G_C_NOTICE(84, "C_Notice",1),//全服通知
		G_C_CHGPOS(85, "C_ChangePos",1),//
		G_R_ADD_EXP(86,"R_Exp",1),//增加主角的经验值
		G_R_ADD_VIT(87,"R_Vit",1),//增加主角的当前体力
		G_R_PHY_NUM(88, "R_phybuynumber",1),//增加体力购买次数
		G_R_VIP_EXP(89,"R_VipExp",1),	//vip经验，固定值
		G_R_VIP_TIME(90,"R_VipTime",1),//vip时间，固定时间
		G_R_ADD_ITEM(91,"R_AddItem",1),//宝箱
		G_R_ADD_GOLD(92,"R_Gold",1),//增加主角的金币
		G_R_ADD_PAP(93,"R_Paper",1),//增加主角的纸币
		G_R_ADD_GEM(94,"R_Gem",1),//增加主角的宝石币
		G_R_ADD_ALL(95,"R_All",1),//增加主角的联盟贡献度
		G_R_ADD_COU(96,"R_Cou",1),//增加主角的国家贡献度
		G_R_ADD_GY(97,"R_Glory",1),//增加主角的军衔荣誉值
		G_C_ADD_RES(98,"C_AddRes",1),	//立即获得6小时的资源建筑的产出 TODO
		G_R_SKILL(99, "R_Skill",1),//重置主角技能
		G_R_NAME(100, "R_Name",1),//更改主角名称
		G_R_IMAGE(101, "R_Image",1),//更改主角形象
		G_C_TRPRTN(102, "C_TroopsReturn",1),//10秒内返回城堡
		G_C_RESPRT(103, "C_ResProtect",1),//资源保护
		G_C_ADD_MEM(104, "C_AddAlliNumber",1),//联盟增加5人
		G_C_ADD_TUT(105, "C_AddTurret",1),//多功能炮台等级
		G_C_ADD_AF(106, "C_AddAirdefense",1),//防空炮塔等级
		G_C_ADD_AD(107, "C_AddAlliancedepot",1),//联盟仓库等级
		G_C_ADD_SLL(108, "C_AddSatellite",1),//间谍卫星等级
		G_C_ADD_SF(109, "C_AddSuperfood",1),//联盟超级食品等级上限提升
		G_C_ADD_SM(110, "C_AddSupermetal",1),//联盟超级金属等级上限提升
		G_C_ADD_SO(111, "C_AddSuperoil",1),//联盟超级石油等级上限提升
		G_C_ADD_SA(112, "C_AddSuperalloy",1),//联盟超级合金等级上限提升
		C_A_RED_RES(113,"R_ReduResProd",6),//核弹4类资源减产50%
		C_RECALL_TRP_LOW(114,"C_RecallTroopsLow",1),//普通行军召回
		C_RECALL_TRP_HIGH(115,"C_RecallTroopsHigh",1),//高级行军召回
		C_RED_FORG_TIME(116,"C_ReduUpLevelTime",1),//锻造加速
		C_RED_ALL_DG(117,"A_ReduAllDamage",2),//降低自己的所有部队火力
		G_C_RED_AMTH(118,"C_ReduArmyMoveTimeHigh",1),//高级行军加速，加速集结部队
		G_C_REP_FENCE(119,"B_RepairFenceHp",7),//城墙修理
		G_R_COPY(120,"C_ResetsCopyProgress",7),//重置副本进度
		;
		private int value;
		private String name;
		private int symbol;//1给自己提升,2给自己减少,3给敌人提升,4给敌人减少,5所有人加,6所有人减,7没用
		
		private TargetType(int value, String name,int symbol){
			this.value = value;
			this.name = name;
			this.symbol = symbol;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public int getSymbol() {
			return symbol;
		}

		public void setSymbol(int symbol) {
			this.symbol = symbol;
		}

		public static TargetType search(String name){
			TargetType[] datas = values();
			for(TargetType data : datas){
				if(data.getName().equals(name)){
					return data;
				}
			}
			return null;
		}
		
		public TargetType valueOf(int ordinal){
			if(ordinal > 0 && ordinal < TargetType.values().length){
				return TargetType.values()[ordinal];
			}
			return null;
		}
	}
	
	public enum ExtendsType {
		EXTEND_ALL(0,"all"),
		EXTEND_ARMY(1,"armyType"),//
		EXTEND_ARMOR(2,"armorType"),//
		EXTEND_BIO(3,"bioType"),//
		EXTEND_SOLDIER(4,"soldiersType"),//
		EXTEND_WEAPON(5,"weaponType"),//
		EXTEND_BUILD(6,"buildType"),
		;
		private int value;
		private String name;
		
		private ExtendsType(int value, String name){
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static ExtendsType search(String name){
			ExtendsType[] datas = values();
			for(ExtendsType data : datas){
				if(data.getName().equals(name)){
					return data;
				}
			}
			return null;
		}

		public ExtendsType valueOf(int ordinal){
			if(ordinal > 0 && ordinal < ExtendsType.values().length){
				return ExtendsType.values()[ordinal];
			}
			return null;
		}
	}
	
	public enum SourceType {
		EFF_TECH(1,"tech"),//科技
		EFF_ITEM(2,"item"),//道具
		EFF_EQUIP(3,"equipment"),//装备
		EFF_SKILL(4,"roleSkill"),//玩家技能
		EFF_ALLY(5,"ally"),//联盟科技
		EFF_VIP(6,"vip"),//vip特效
		EFF_UCITY(7,"unioncity"),//联盟城市特效
		;
		private int value;
		private String name;
		
		private SourceType(int value, String name){
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public SourceType valueOf(int ordinal){
			if(ordinal > 0 && ordinal < SourceType.values().length){
				return SourceType.values()[ordinal];
			}
			return null;
		}
	}
	
	public enum TargetRSType {
		TARGET_ROLE(0,"role"),//角色
		TARGET_CITY(1,"city"),//城池
		TARGET_BUILD(2,"build"),//建筑
		TARGET_ARMY(3,"army"),//部队
		TARGET_ALLY(4,"ally"),//联盟
		TARGET_COUNTRY(5,"country"),//国家
		;
		private int value;
		private String name;
		
		private TargetRSType(int value, String name){
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public static TargetRSType search(String name){
			TargetRSType[] datas = values();
			for(TargetRSType data : datas){
				if(data.getName().equals(name)){
					return data;
				}
			}
			return null;
		}

		public TargetRSType valueOf(int ordinal){
			if(ordinal > 0 && ordinal < TargetRSType.values().length){
				return TargetRSType.values()[ordinal];
			}
			return null;
		}
	}

}
