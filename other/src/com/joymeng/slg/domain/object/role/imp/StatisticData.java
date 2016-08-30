package com.joymeng.slg.domain.object.role.imp;


public enum StatisticData {
	S_FIT_CMD("statistic_fighting_commander"), // 指挥官总战斗力
	S_FIT_TRP("statistic_fighting_troop"), // 部队战斗力
	S_FIT_BUD("statistic_fighting_building"), // 建筑战斗力
	S_FIT_TCH("statistic_fighting_tech"), // 科技战斗力
	S_FIT_WIN("statistic_fightWin_num"), // 战斗胜利次数
	S_FIT_FAL("statistic_fightFail_num"), // 战斗失败次数
	S_ATK_WIN("statistic_attackWin_num"), // 进攻胜利次数
	S_ATK_FAL("statistic_attackFail_num"), // 进攻失败次数
	S_DFC_WIN("statistic_defenceWin_num"), // 防御胜利次数
	S_DFC_FAL("statistic_defenceFail_num"), // 防御失败次数
	S_WIN_PER("statistic_winning_percentage"), // 胜率
	S_ITG_NUM("statistic_investigate_num"), // 侦查次数
	S_DST_NUM("statistic_destroyTroop_num"), // 消灭部队数量
	S_TRL_NUM("statistic_troopLose_num"), // 部队损失数量
	S_TRP_NUM("statistic_troopRepair_num"), // 部队维修数量
	S_TRH_NUM("statistic_troopHealing_num"), // 部队治疗数量
	S_MHT_NUM("statistic_marchTroop_num"), // 行军部队总数
	S_RD_SGT("statistic_radar_sight"), // 雷达视野范围
	S_FTS_NUM("statistic_fortress_num"), // 要塞数量
	S_FTS_SGT("statistic_fortress_sight"), // 要塞视野范围
	S_BTF_LMT("statistic_battleForce_limit"), // 出征兵力上限
	S_BTFN_LMT("statistic_battleSingleForceNum_limit"), // 出征单支部队兵力容纳上限
	S_PDI_NUM("statistic_trainInfantry_num"), // 训练步兵数量
	S_PDC_NUM("statistic_produceChariot_num"), // 生产战车数量
	S_PDT_NUM("statistic_produceTank_num"), // 生产坦克数量
	S_PDW_NUM("statistic_produceWarplane_num"), // 生产战机数量
	S_PDF_NUM("statistic_producedefenceBuild_num"), // 生产防御设施数量
	S_PDI_SPD("statistic_infantryProduction_speed"), // 步兵训练速度
	S_PDC_SPD("statistic_chariotProduction_speed"), // 战车生产速度
	S_PDT_SPD("statistic_tankProduction_speed"), // 坦克生产速度
	S_PDW_SPD("statistic_warplaneProduction_speed"), // 飞机生产速度
	S_PDF_SPD("statistic_defenseProduction_speed"), // 防御设施生产速度
	S_RES_DER("statistic_ReduSoldierDeathRate"), // 降低士兵部队死亡率
	S_REA_DER("statistic_ReduArmorDeathRate"), // 降低战车部队报废率
	S_RET_DER("statistic_ReduTankDeathRate"), // 降低坦克部队报废率
	S_RAF_DER("statistic_ReduAircraftDeathRate"), // 降低战机部队报废率
	S_TF_BOS("statistic_troopsFire_bonus"), // 提升部队的火力
	S_TD_BOS("statistic_troopsdefence_bonus"), // 提升部队的防御力
	S_TH_BOS("statistic_troopsHP_bonus"), // 提升部队的生命值
	S_TM_BOS("statistic_troopsMachinePower_bonus"), // 提升部队的机动力
	S_IF_BOS("statistic_infantryFire_bonus"), // 步兵火力
	S_ID_BOS("statistic_infantrydefence_bonus"), // 步兵防御
	S_IHP_BOS("statistic_infantryHP_bonus"), // 步兵生命
	S_IM_BOS("statistic_infantryMachinePower_bonus"), // 步兵机动力
	S_CF_BOS("statistic_chariotFire_bonus"), // 战车火力
	S_CD_BOS("statistic_chariotdefence_bonus"), // 战车防御
	S_CHP_BOS("statistic_chariotHP_bonus"), // 战车生命
	S_CM_BOS("statistic_chariotMachinePower_bonus"), // 战车机动力
	S_TKF_BOS("statistic_tankFire_bonus"), // 坦克火力
	S_TKD_BOS("statistic_tankdefence_bonus"), // 坦克防御
	S_TKHP_BOS("statistic_tankHP_bonus"), // 坦克生命
	S_TKM_BOS("statistic_tankMachinePower_bonus"), // 坦克机动力
	S_WPF_BOS("statistic_warplaneFire_bonus"), // 空军火力
	S_WPD_BOS("statistic_warplanedefence_bonus"), // 空军防御
	S_WPHP_BOS("statistic_warplaneHP_bonus"), // 空军生命
	S_WPM_BOS("statistic_warplaneMachinePower_bonus"), // 空军机动力
	S_IC_BOS("statistic_infantryCritical_bonus"), // 提升步兵的暴击值
	S_CC_BOS("statistic_chariotCritical_bonus"), // 提升战车的暴击值
	S_TC_BOS("statistic_tankCritical_bonus"), // 提升坦克的暴击值
	S_WC_BOS("statistic_warplaneCritical_bonus"), // 提升战机的暴击值
	S_PC_BOS("statistic_pillBoxCrit_bonus"), // 电网暴击提升
	S_SC_BOS("statistic_sentryGunCrit_bonus"), // 高爆地雷暴击提升
	S_MC_BOS("statistic_patriotMissleSysteCrit_bonus"), // 反坦克地雷暴击提升
	S_FC_BOS("statistic_flakCannonCrit_bonus"), // 防空导弹暴击提升
	S_IH_BOS("statistic_infantryHit_bonus"), // 提升步兵的命中值
	S_CH_BOS("statistic_chariotHit_bonus"), // 提升战车的命中值
	S_TKH_BOS("statistic_tankHit_bonus"), // 提升坦克的命中值
	S_WPH_BOS("statistic_warplaneHit_bonus"), // 提升战机的命中值
	S_IDD_BOS("statistic_infantryDodge_bonus"), // 提升步兵的闪避值
	S_CDD_BOS("statistic_chariotDodge_bonus"), // 提升战车的闪避值
	S_TDD_BOS("statistic_tankDodge_bonus"), // 提升坦克的闪避值
	S_WDD_BOS("statistic_warplaneDodge_bonus"), // 提升战机的闪避值
	S_AWT_BOS("statistic_allWeight_bonus"), // 所有部队负重
	S_IWT_BOS("statistic_infantryWeight_bonus"), // 提升步兵的负重量
	S_CWT_BOS("statistic_chariotWeight_bonus"), // 提升战车的负重量
	S_TWT_BOS("statistic_tankWeight_bonus"), // 提升坦克的负重量
	S_WWT_BOS("statistic_warplaneWeight_bonus"), // 提升战机的负重量
	S_AFC_BOS("statistic_allfoodconsume_bonus"), // 所有部队的食品消耗
	S_SFC_BOS("statistic_soldierfoodconsume_bonus"), // 士兵部队的食品消耗
	S_AMC_BOS("statistic_armorfoodconsume_bonus"), // 战车部队的食品消耗
	S_TFC_BOS("statistic_tankfoodconsume_bonus"), // 坦克部队的食品消耗
	S_ACC_BOS("statistic_aircraftfoodconsume_bonus"), // 战机部队的食品消耗
	S_IIF_RAS("statistic_infantryInflict_raise"), // 步兵造成伤害提升
	S_CIF_RAS("statistic_chariotInflict_raise"), // 战车造成伤害提升
	S_TIF_RAS("statistic_tankInflict_raise"), // 坦克造成伤害提升
	S_WIF_RAS("statistic_warplaneInflict_raise"), // 战机造成伤害提升
	S_IDT_LOR("statistic_infantryDamageTaken_lower"), // 步兵承受伤害降低
	S_CDT_LOR("statistic_chariotDamageTaken_lower"), // 战车承受伤害降低
	S_TDT_LOR("statistic_tankDamageTaken_lower"), // 坦克承受伤害降低
	S_WDT_LOR("statistic_warplaneDamageTaken_lower"), // 战机承受伤害降低
	S_QF_BOS("statistic_bulletFire_bonus"), // 枪弹类兵种火力
	S_SF_BOS("statistic_shellFire_bonus"), // 炮弹类兵种火力
	S_EOF_BOS("statistic_energyOrbFire_bonus"), // 能量类兵种火力
	S_MF_BOS("statistic_missileFire_bonus"), // 导弹类兵种火力
	S_BF_BOS("statistic_bombFire_bonus"), // 炸弹类兵种火力
	S_LAF_BOS("statistic_lightArmordefence_bonus"), // 轻甲类兵种防御
	S_HAD_BOS("statistic_heavyArmordefence_bonus"), // 重甲类兵种防御
	S_AAF_BOS("statistic_armouredArmordefence_bonus"), // 装甲类兵种防御
	S_MAD_BOS("statistic_mechArmordefence_bonus"), // 机甲类兵种防御
	S_WAF_BOS("statistic_wallArmordefence_bonus"), // 城甲类兵种防御提升
	S_BUD_BOS("statistic_bulletDamage_bonus"), // 枪弹类兵种造成的伤害提升
	S_BTD_BOS("statistic_shellDamage_bonus"), // 炮弹类兵种造成的伤害提升
	S_EOD_BOS("statistic_energyOrbDamage_bonus"), // 能量类兵种造成的伤害提升
	S_MSD_BOS("statistic_missileDamage_bonus"), // 导弹类兵种造成的伤害提升
	S_BOS_BOS("statistic_bombDamage_bonus"), // 炸弹类兵种造成的伤害提升
	S_LMF_BOS("statistic_lightArmordefdamage_bonus"), // 轻甲类兵种承受的伤害降低
	S_HAF_BOS("statistic_heavyArmordefdamage_bonus"), // 重甲类兵种承受的伤害降低
	S_AMF_BOS("statistic_armouredArmordefdamage_bonus"), // 装甲类兵种承受的伤害降低
	S_MAF_BOS("statistic_mechArmordefdamage_bonus"), // 机甲类兵种承受的伤害降低
	S_WMF_BOS("statistic_wallArmordefdamage_bonus"), // 城甲类兵种承受的伤害降低
	S_FYD_ADD("statistic_foodYield_add"), // 食物产量增加
	S_MYD_ADD("statistic_metalYield_add"), // 金属产量增加
	S_OYD_ADD("statistic_oilYield_add"), // 石油产量增加
	S_AYD_ADD("statistic_alloyYield_add"), // 钛合金产量增加
	S_FYD_RS("statistic_foodYield_raise"), // 食物产量提升
	S_OYD_RS("statistic_oilYield_raise"), // 石油产量提升
	S_MYD_RS("statistic_metalYield_raise"), // 金属产量提升
	S_AYD_RS("statistic_alloyYield_raise"), // 钛合金产量提升
	S_FCS_BOS("statistic_foodcollectSpeed_bonus"), // 食物采集速度
	S_MCS_BOS("statistic_metalcollectSpeed_bonus"), // 金属采集速度
	S_OCS_BOS("statistic_oilcollectSpeed_bonus"), // 石油采集速度
	S_ACS_BOS("statistic_alloycollectSpeed_bonus"), // 钛合金采集速度
	S_FCT_BOS("statistic_foodcollectTime_bonus"), // 部队采集食品时间
	S_MCT_BOS("statistic_metalcollectTime_bonus"), // 部队采集金属时间
	S_OCT_BOS("statistic_oilcollectTime_bonus"), // 部队采集石油时间
	S_ACT_BOS("statistic_alloycollectTime_bonus"), // 部队采集合金时间
	S_BDS_BOS("statistic_buildBuildingSpeed_bonus"), // 建筑建造速度 未使用
	S_TS_BOS("statistic_techSpeed_bonus"), // 科技研究速度 未使用
	S_HOS_CAP("statistic_hospital_capacity"), // 医院容量
	S_HEL_SPD("statistic_healing_speed"), // 医院治疗速度
	S_SDT_CAP("statistic_serviceDepot_capacity"), // 维修厂容量
	S_RPR_SPD("statistic_repair_speed"), // 维修厂维修速度
	S_HEL_CST("statistic_healing_cost"), // 治疗费用
	S_REP_CST("statistic_repair_cost"), // 维修费用
	S_FSTM_SPD("statistic_forceavStaminarate_speed"), // 体力恢复速度
	S_EYD_RS("statistic_electricityYield_raise"), // 电力产量提升
	S_CDFC_NUM("statistic_cityDefence_num"), // 城防值
	S_CDFC_CAP("statistic_cityDefence_capacity"), // 城防空间
	S_PBF_BOS("statistic_pillBoxFire_bonus"), // 机枪碉堡火力
	S_SGF_BOS("statistic_sentryGunFire_bonus"), // 哨戒炮火力
	S_FCF_BOS("statistic_flakCannonFire_bonus"), // 防空炮火力
	S_PMSF_BOS("statistic_patriotMissleSysteFire_bonus"), // 爱国者飞弹火力
	S_PTF_BOS("statistic_prisimTowerFire_bonus"), // 光棱塔火力
	S_TCF_BOS("statistic_teslaCoilFire_bonus"), // 磁暴线圈火力
	S_GCF_BOS("statistic_grandCannonFire_bonus"), // 巨炮火力
	S_PTD_BOS("statistic_prisimTowerDefence_bonus"), // 光棱塔防御
	S_TCD_BOS("statistic_teslaCoilDefence_bonus"), // 磁暴线圈防御
	S_GCD_BOS("statistic_grandCannonDefence_bonus"), // 巨炮防御
	S_PTH_BOS("statistic_prisimTowerHP_bonus"), // 光棱塔耐久
	S_TCH_BOS("statistic_teslaCoilHP_bonus"), // 磁暴线圈耐久
	S_GCH_BOS("statistic_grandCannonHP_bonus"), // 巨炮耐久
	S_PBH_RAS("statistic_pillBoxInflict_raise"), // 机枪碉堡造成伤害提升
	S_SGF_RAS("statistic_sentryGunInflict_raise"), // 哨戒炮造成伤害提升
	S_FCF_RAS("statistic_flakCannonInflict_raise"), // 防空炮造成伤害提升
	S_PMSF_RAS("statistic_patriotMissleSysteInflict_raise"), // 爱国者飞弹造成伤害提升
	S_PTF_RAS("statistic_prisimTowerInflict_raise"), // 光棱塔造成伤害提升
	S_TCF_RAS("statistic_teslaCoilInflict_raise"), // 磁暴线圈造成伤害提升
	S_GCF_RAS("statistic_grandCannonInflict_raise"), // 巨炮造成伤害提升
	S_FIN_FDO("statistic_food_troopsFire_reduce"),//食品不足部队火力降低
	S_COL_RAS("statistic_troopscollect_bonus"),//部队采集加速
	S_COL_INF("statistic_infantrytroopscollect_bonus"),//士兵部队采集加速
	S_COL_CHA("statistic_chariotroopscollect_bonus"),//战车部队采集加速
	S_COL_TAN("statistic_tanktroopscollect_bonus"),//坦克部队采集加速
	S_COL_PLA("statistic_warplanetroopscollect_bonus"),//战机部队采集加速
	;

	String key;

	private StatisticData(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static StatisticData search(String key) {
		StatisticData[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			StatisticData component = datas[i];
			if (component.key.equals(key)) {
				return component;
			}
		}
		return null;
	}
}
