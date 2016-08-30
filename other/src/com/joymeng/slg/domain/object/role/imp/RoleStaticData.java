package com.joymeng.slg.domain.object.role.imp;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.slg.domain.map.data.Worldbuilding;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.data.Buildinglevel;
import com.joymeng.slg.domain.object.build.impl.BuildComponentResearch;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.data.Heroinfostatistic;

public class RoleStaticData implements Instances{
	private Heroinfostatistic hfss = new Heroinfostatistic();
	private double num;

	public Heroinfostatistic getHfss() {
		return hfss;
	}

	public void setHfss(Heroinfostatistic hfss) {
		this.hfss = hfss;
	}

	public double getNum() {
		return num;
	}

	public void setNum(double num) {
		this.num = num;
	}
	
	public static List<RoleStaticData> getDetailList(Role role, int cityId){
		RoleCityAgent agent = role.getCity(cityId);
//		MapCity mc = mapWorld.searchMapCity(role.getId(), cityId);
		RoleStatisticInfo sInfo = role.getRoleStatisticInfo();
		List<RoleStaticData> roleInfoList = new ArrayList<RoleStaticData>();
		List<Heroinfostatistic> hfssLst = dataManager.serachList(Heroinfostatistic.class);
		float base = 0.0F; //建筑提供的基础值
		
		for(Heroinfostatistic hfss : hfssLst){
			RoleStaticData info = new RoleStaticData();
			info.setHfss(hfss);
			StatisticData type = StatisticData.search(hfss.getStatisticName());
			boolean isSuc = true;
			switch(type){
			case S_FIT_CMD:// 指挥官总战斗力 数值
				info.setNum(sInfo.getRoleFight());
				break;
			case S_FIT_TRP:// 部队战斗力 数值
				info.setNum(sInfo.getRoleArmyFight());
				break;
			case S_FIT_BUD:// 建筑战斗力 数值
				info.setNum(sInfo.getRoleBuildFight());
				break;
			case S_FIT_TCH:// 科技战斗力 数值
				info.setNum(sInfo.getRoleTechFight());
				break;
			case S_FIT_WIN:// 战斗胜利次数 数值
				info.setNum(sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes());
				break;
			case S_FIT_FAL:// 战斗失败次数 数值
				info.setNum(sInfo.getAttackFailTimes() + sInfo.getDefenceFailTimes());
				break;
				
			case S_ATK_WIN:// 进攻胜利次数 数值
				info.setNum(sInfo.getAttackWinTimes());
				break;
			case S_ATK_FAL:// 进攻失败次数 数值
				info.setNum(sInfo.getAttackFailTimes());
				break;
				
			case S_DFC_WIN:// 防御胜利次数 数值
				info.setNum(sInfo.getDefenceWinTimes());
				break;
			case S_DFC_FAL:// 防御失败次数 数值
				info.setNum(sInfo.getDefenceFailTimes());
				break;
			case S_WIN_PER:// 胜率 百分比
				if (sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes() + sInfo.getAttackFailTimes() + sInfo.getDefenceFailTimes() == 0) {
					info.setNum(0);
				} else {
					float rate = (float)(sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes()) / (float)(sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes() + sInfo.getAttackFailTimes() + sInfo.getDefenceFailTimes());
					info.setNum(rate);
				}
				break;
			case S_ITG_NUM://侦查次数	数值
				info.setNum(sInfo.getSpyTimes()); 
				break;
			case S_DST_NUM://消灭部队数量	数值	
				info.setNum(sInfo.getKillSoldsNum()); 
				break;
			case S_TRL_NUM://部队损失数量	数值	
				info.setNum(sInfo.getDeadSoldNum()); 
				break;
			case S_TRP_NUM://部队维修数量	数值
				info.setNum(sInfo.getRepairNum()); 
				break;
			case S_TRH_NUM://部队治疗数量	数值
				info.setNum(sInfo.getCureNum()); 
				break;
			case S_MHT_NUM://行军部队总数	数值
				info.setNum(role.getExpediteMaxNum(0)); 
				break;
			case S_RD_SGT://雷达视野范围
				int level = agent.getRadarLevel();
				Buildinglevel bl = RoleBuild.getBuildinglevelByCondition(BuildName.RADAR.getKey(),level);
				List<String> lis = bl.getParamList();
				int value = Integer.parseInt(lis.get(0));
				info.setNum(value);
//				info.setNum(mc.getCityState().getCityViewBuff());
				break;
			case S_FTS_NUM://要塞数量	数值	
				Worldbuilding wb = dataManager.serach(Worldbuilding.class,BuildName.MAP_FORTRESS.getKey());
				int maxNum = 0;
				List<String> counts = wb.getMaxBuildCount();
				int standLevel = agent.getCityCenterLevel();
				for (int i = 0 ; i < counts.size() ; i++){
					String str = counts.get(i);
					String[] ss = str.split(":");
					int min = Integer.parseInt(ss[0]);
					int max = Integer.parseInt(ss[1]);
					int num = Integer.parseInt(ss[2]);
					if (standLevel >= min && standLevel <= max){
						maxNum = Math.max(maxNum,num);
					}
				}
				info.setNum(maxNum + agent.getCityAttr().getFortNum());
				break;
//			case S_FTS_SGT://要塞视野范围
//				info.setNum(mc.getCityState().getFortViewBuff());
//				break;
			case S_BTF_LMT://出征部队上限	数值	
//				info.setNum(role.getExpediteMaxNum(cityId));
				info.setNum(agent.getMaxOutBattleAllNum());
				break;
//			case S_BTFN_LMT://出征单支部队兵力容纳上限	数值	
//				info.setNum(agent.getMaxOutBattleNum());
//				break;
			case S_PDI_NUM://训练步兵数量	数值	
				info.setNum(agent.getMaxTrainNum(BuildName.SOLDIERS_CAMP));
				break;
			case S_PDC_NUM://生产战车数量	数值	
				info.setNum(agent.getMaxTrainNum(BuildName.WAR_FACT)/5);
				break;
			case S_PDT_NUM://生产坦克数量	数值
				info.setNum(agent.getMaxTrainNum(BuildName.ARMORED_FACT)/10);
				break;
			case S_PDW_NUM://生产战机数量	数值	
				info.setNum(agent.getMaxTrainNum(BuildName.AIR_COM)/10);
				break;
			case S_PDF_NUM://生产防御设施数量	数值
				info.setNum(agent.getMaxTrainNum(BuildName.MILITARY_FACT));
				break;
			case S_PDI_SPD://训练/生产步兵速度	加成百分比	army:A_ReduProdTime:armyType
				base = agent.getRoleBuildBaseBuffValue(BuildName.SOLDIERS_CAMP.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_PDC_SPD://训练/生产战车速度	加成百分比
				base = agent.getRoleBuildBaseBuffValue(BuildName.WAR_FACT.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_PDT_SPD://训练/生产坦克速度	加成百分比
				base = agent.getRoleBuildBaseBuffValue(BuildName.ARMORED_FACT.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_PDW_SPD://训练/生产飞机速度	加成百分比
				base = agent.getRoleBuildBaseBuffValue(BuildName.AIR_COM.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_PDF_SPD://训练/生产城防设施速度	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SPT, ExtendsType.EXTEND_ARMY, 5));
				break;
			case  S_RES_DER://降低士兵部队死亡率
				base = agent.getRoleBuildBaseBuffValue(BuildName.HOSPITAL.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_DR, ExtendsType.EXTEND_ARMY, 1));
				break;
			case  S_REA_DER://降低战车部队报废率
				base = agent.getRoleBuildBaseBuffValue(BuildName.REPAIRER.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_DR, ExtendsType.EXTEND_ARMY, 2));
				break;
			case  S_RET_DER://降低坦克部队报废率
				base = agent.getRoleBuildBaseBuffValue(BuildName.REPAIRER.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_DR, ExtendsType.EXTEND_ARMY, 3));
				break;
			case  S_RAF_DER://降低战机部队报废率
				base = agent.getRoleBuildBaseBuffValue(BuildName.REPAIRER.getKey());
				info.setNum(base + role.getArmyAttr().getEffValV2(TargetType.T_A_RED_DR, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_TF_BOS://提升部队的火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_TD_BOS://提升部队的防御力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_TH_BOS://提升部队的生命值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_TM_BOS://提升部队的机动力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_QF_BOS://枪弹类兵种火力	加成百分比	army:A_ImpAtk:weaponType
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 1));
				break;
			case S_SF_BOS://炮弹类兵种火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 2));
				break;
			case S_EOF_BOS://能量类兵种火力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 3));
				break;
			case S_MF_BOS://导弹类兵种火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 4));
				break;
			case S_BF_BOS://炸弹类兵种火力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 5));
				break;
			case S_LAF_BOS://轻甲类兵种防御	加成百分比	army:A_ImpDef:armorType
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMOR, 1));
				break;
			case S_HAD_BOS://重甲类兵种防御	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMOR, 2));
				break;
			case S_AAF_BOS://装甲类兵种防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMOR, 3));
				break;
			case S_MAD_BOS://机甲类兵种防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMOR, 4));
				break;
			case S_WAF_BOS: //城甲类兵种防御提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMOR, 5));
				break;
			case S_BUD_BOS://枪弹类兵种造成的伤害提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_WEAPON, 1));
				break;
			case S_BTD_BOS://炮弹类兵种造成的伤害提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_WEAPON, 2));
				break;
			case S_EOD_BOS://能量类兵种造成的伤害提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_WEAPON, 3));
				break;
			case S_MSD_BOS://导弹类兵种造成的伤害提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_WEAPON, 4));
				break;
			case S_BOS_BOS://炸弹类兵种造成的伤害提升
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_WEAPON, 5));
				break;
			case S_LMF_BOS://轻甲类兵种承受的伤害降低
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.C_A_RED_BDMG, ExtendsType.EXTEND_ARMOR, 1));
				break;
			case S_HAF_BOS://重甲类兵种承受的伤害降低
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.C_A_RED_BDMG, ExtendsType.EXTEND_ARMOR, 2));
				break;
			case S_AMF_BOS://装甲类兵种承受的伤害降低
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.C_A_RED_BDMG, ExtendsType.EXTEND_ARMOR, 3));
				break;
			case S_MAF_BOS://机甲类兵种承受的伤害降低
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.C_A_RED_BDMG, ExtendsType.EXTEND_ARMOR, 4));
				break;
			case S_WMF_BOS://城甲类兵种承受的伤害降低
				info.setNum(role.getArmyAttr().getEffValV2NoAll(TargetType.C_A_RED_BDMG, ExtendsType.EXTEND_ARMOR, 5));
				break;
			case S_IF_BOS://步兵火力		加成百分比 army:A_ReduProdTime:armyType	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_ID_BOS://步兵防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_IHP_BOS://步兵生命	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_IM_BOS://步兵机动力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CF_BOS://战车火力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_CD_BOS://战车防御	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_CHP_BOS://战车生命	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_CM_BOS://战车机动力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TKF_BOS://坦克火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_TKD_BOS://坦克防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_TKHP_BOS://坦克生命	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_TKM_BOS://坦克机动力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WPF_BOS://空军火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_WPD_BOS://空军防御	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_WPHP_BOS://空军生命	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_WPM_BOS://空军机动力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SS, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_IIF_RAS://步兵造成伤害提升		加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CIF_RAS://战车造成伤害提升		加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TIF_RAS://坦克造成伤害提升		加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WIF_RAS://战机造成伤害提升		加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_IDT_LOR://步兵承受伤害降低		加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.C_A_RED_BDMG_ALL, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CDT_LOR://战车承受伤害降低		加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.C_A_RED_BDMG_ALL, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TDT_LOR://坦克承受伤害降低		加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.C_A_RED_BDMG_ALL, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WDT_LOR://战机承受伤害降低		加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.C_A_RED_BDMG_ALL, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_COL_RAS:// 部队的采集速度提升
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_ADD_IC, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_COL_INF:// 士兵部队的采集速度提升
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_ADD_IC, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_COL_CHA:// 战车部队的采集速度提升
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_ADD_IC, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_COL_TAN:// 坦克部队的采集速度提升
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_ADD_IC, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_COL_PLA:// 战机部队的采集速度提升
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_ADD_IC, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_FYD_ADD://食物产量增加	数值
				info.setNum(agent.getProductionNumBuff(ResourceTypeConst.RESOURCE_TYPE_FOOD));
				break;
			case S_FYD_RS://食物产量提升	加成百分比
				info.setNum(agent.getProductionRateBuff(ResourceTypeConst.RESOURCE_TYPE_FOOD));
				break;
			case S_FCS_BOS://食物采集速度	加成百分比
				RoleBuild cityCenter = agent.getCiytCenter();
				Buildinglevel buildinglevel = cityCenter.getBuildingLevel();
				if (buildinglevel != null) {
					base = Float.valueOf(buildinglevel.getParamList().get(0));
				}
				info.setNum(base + agent.getCityAttr().getImpCollSpeed(ResourceTypeConst.RESOURCE_TYPE_FOOD));
				break;
			case S_MYD_ADD://金属产量增加	数值
				info.setNum(agent.getProductionNumBuff(ResourceTypeConst.RESOURCE_TYPE_METAL));
				break;
			case S_MYD_RS://金属产量提升	加成百分比
				info.setNum(agent.getProductionRateBuff(ResourceTypeConst.RESOURCE_TYPE_METAL));
				break;
			case S_MCS_BOS://金属采集速度	加成百分比
				RoleBuild cityCenter2 = agent.getCiytCenter();
				Buildinglevel buildinglevel2 = cityCenter2.getBuildingLevel();
				if (buildinglevel2 != null) {
					base = Float.valueOf(buildinglevel2.getParamList().get(1));
				}
				info.setNum(base + agent.getCityAttr().getImpCollSpeed(ResourceTypeConst.RESOURCE_TYPE_METAL));
				break;
			case S_OYD_ADD://石油产量增加	数值
				info.setNum(agent.getProductionNumBuff(ResourceTypeConst.RESOURCE_TYPE_OIL));
				break;
			case S_OYD_RS://石油产量提升	加成百分比
				info.setNum(agent.getProductionRateBuff(ResourceTypeConst.RESOURCE_TYPE_OIL));
				break;
			case S_OCS_BOS://石油采集速度	加成百分比
				info.setNum(agent.getCityAttr().getImpCollSpeed(ResourceTypeConst.RESOURCE_TYPE_OIL));
				break;
			case S_AYD_ADD://钛合金产量增加	数值
				info.setNum(agent.getProductionNumBuff(ResourceTypeConst.RESOURCE_TYPE_ALLOY));
				break;
			case S_AYD_RS://钛合金产量提升	加成百分比
				info.setNum(agent.getProductionRateBuff(ResourceTypeConst.RESOURCE_TYPE_ALLOY));
				break;
			case S_ACS_BOS://钛合金采集速度	加成百分比
				info.setNum(agent.getCityAttr().getImpCollSpeed(ResourceTypeConst.RESOURCE_TYPE_ALLOY));
				break;
			case S_FCT_BOS: //部队采集食品时间
				info.setNum(agent.getCityAttr().getReduFoodCollTime());
				break;
			case S_MCT_BOS://部队采集金属时间
				info.setNum(agent.getCityAttr().getReduMetalCollTime());
				break;
			case S_OCT_BOS://部队采集石油时间
				info.setNum(agent.getCityAttr().getReduOilCollTime());
				break;
			case S_ACT_BOS://部队采集合金时间
				info.setNum(agent.getCityAttr().getReduAlloyCollTime());
				break;
			case S_BDS_BOS: //建筑建造速度 
				info.setNum(agent.getCityAttr().getImpBuildSpeed());
				break;
			case S_TS_BOS: //科技研究速度 
				List<RoleBuild> researchBuilds = agent.searchBuildByCompomemt(BuildComponentType.BUILD_COMPONENT_RESEARCH);
				if (researchBuilds.size() > 0) {
					BuildComponentResearch componentResearch = researchBuilds.get(0).getComponent(BuildComponentType.BUILD_COMPONENT_RESEARCH);
					base = Float.valueOf(
							componentResearch == null ? "0" : componentResearch.getBuildParams(researchBuilds.get(0)));
				}
				info.setNum(agent.getCityAttr().getImpResSpeed() + base);
				break;
			case S_EYD_RS://电力产量提升	加成百分比
				info.setNum(agent.getCityAttr().getAddPowerProd());
				break;
			case S_IC_BOS://提升步兵的暴击值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CC_BOS://提升战车的暴击值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TC_BOS://提升坦克的暴击值	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WC_BOS://提升战机的暴击值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_PC_BOS://电网暴击提升	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_SOLDIER, 37));
				break;
			case S_SC_BOS://高爆地雷暴击提升	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_SOLDIER, 38));
				break;
			case S_MC_BOS://反坦克地雷暴击提升	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_SOLDIER, 40));
				break;
			case S_FC_BOS://防空导弹暴击提升	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_ICR, ExtendsType.EXTEND_SOLDIER, 39));
				break;
			case S_IH_BOS://提升步兵的命中值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IAR, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CH_BOS://提升战车的命中值	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IAR, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TKH_BOS://提升坦克的命中值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IAR, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WPH_BOS://提升战机的命中值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IAR, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_IDD_BOS://提升步兵的闪避值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IER, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CDD_BOS://提升战车的闪避值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IER, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TDD_BOS://提升坦克的闪避值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IER, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WDD_BOS://提升战机的闪避值	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_IER, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_AWT_BOS://所有部队负重 加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ALL, 0));
			    break;
			case S_IWT_BOS://提升步兵的负重量	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_CWT_BOS://提升战车的负重量	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TWT_BOS://提升坦克的负重量	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_WWT_BOS://提升战机的负重量	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SW, ExtendsType.EXTEND_ARMY, 4));
				break;
			case S_AFC_BOS://所有部队的食品消耗
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SC, ExtendsType.EXTEND_ALL, 0));
				break;
			case S_SFC_BOS://士兵部队的食品消耗
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SC, ExtendsType.EXTEND_ARMY, 1));
				break;
			case S_AMC_BOS://战车部队的食品消耗
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SC, ExtendsType.EXTEND_ARMY, 2));
				break;
			case S_TFC_BOS://坦克部队的食品消耗
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SC, ExtendsType.EXTEND_ARMY, 3));
				break;
			case S_ACC_BOS://战机部队的食品消耗
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_RED_SC, ExtendsType.EXTEND_ARMY, 4));
				break;		
			case S_HOS_CAP://医院容量	数值	
				info.setNum(agent.getRepairerHospital(BuildName.HOSPITAL.getKey()));
				break;
			case S_HEL_SPD://医院治疗速度	加成百分比
				info.setNum(agent.getCityAttr().getReduHospTime());
				break;
			case S_SDT_CAP://维修厂容量	数值	
				info.setNum(agent.getRepairerHospital(BuildName.REPAIRER.getKey()));
				break;
			case S_RPR_SPD://维修厂维修速度	加成百分比	
				info.setNum(agent.getCityAttr().getReduRepaTime());
				break;
			case S_HEL_CST://治疗伤兵的资源降低	加成百分比	
				info.setNum(agent.getCityAttr().getReduRepaRes_1());
				break;
			case S_REP_CST://维修伤兵的资源降低	加成百分比
				info.setNum(agent.getCityAttr().getReduRepaRes_2());
				break;
			case S_FSTM_SPD://体力恢复速度	加成百分比
				info.setNum(role.getRoleStamina().getSpeedBuff());
				break;
			case S_CDFC_NUM://城防值	数值	
				info.setNum(agent.getWallMaxValue());
				break;
			case S_CDFC_CAP:;//城防空间	加成百分比	
				info.setNum(agent.getFenceMaxTrip());
				break;
			case S_PBF_BOS://电网火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 37));
				break;
			case S_SGF_BOS:// 高爆地雷火力 加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 38)
						+ role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 5));
				break;
			case S_FCF_BOS:// 防空导弹火力 加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 39));
				break;
			case S_PMSF_BOS:// 反坦克地雷火力 加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 40)
						+ role.getArmyAttr().getEffValV2NoAll(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_WEAPON, 5));
				break;
			case S_PTF_BOS:// 光棱塔火力 加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 41));
				break;
			case S_TCF_BOS://磁暴线圈火力	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 42));	
				break;
			case S_GCF_BOS://巨炮火力	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SA, ExtendsType.EXTEND_SOLDIER, 43));
				break;
			case S_PTD_BOS://光棱塔防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_SOLDIER, 41));
				break;
			case S_TCD_BOS://磁暴线圈防御	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_SOLDIER, 42));
				break;
			case S_GCD_BOS://巨炮防御	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_SD, ExtendsType.EXTEND_SOLDIER, 43));
				break;
			case S_PTH_BOS://光棱塔耐久	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_SOLDIER, 41));
				break;
			case S_TCH_BOS://磁暴线圈耐久	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_SOLDIER, 42));	
				break;
			case S_GCH_BOS://巨炮耐久	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_AHP, ExtendsType.EXTEND_SOLDIER, 43));
				break;
			case S_PBH_RAS://机枪碉堡造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 37));	
				break;
			case S_SGF_RAS://哨戒炮造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 38));	
				break;
			case S_FCF_RAS://防空炮造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 39));
				break;
			case S_PMSF_RAS://爱国者飞弹造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 40));	
				break;
			case S_PTF_RAS://光棱塔造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 41));
				break;
			case S_TCF_RAS://磁暴线圈造成伤害提升	加成百分比	
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 42));
				break;
			case S_GCF_RAS://巨炮造成伤害提升	加成百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.T_A_IMP_DMG, ExtendsType.EXTEND_SOLDIER, 43));
				break;
			case S_FIN_FDO://食品不足部队火力降低  百分比
				info.setNum(role.getArmyAttr().getEffValV2(TargetType.C_RED_ALL_DG, ExtendsType.EXTEND_ALL, 0));
				break;
			default:
				isSuc = false;
				break;
			}
			if(!isSuc){
				continue;
			}
			roleInfoList.add(info);
		}
		return roleInfoList;
	}
	
	public static List<RoleStaticData> getOtherDetailList(Role role) {
		RoleStatisticInfo sInfo = role.getRoleStatisticInfo();
		List<RoleStaticData> roleInfoList = new ArrayList<RoleStaticData>();
		List<Heroinfostatistic> hfssLst = dataManager.serachList(Heroinfostatistic.class);
		for (int i = 0 ; i < hfssLst.size() ; i++){
			Heroinfostatistic hfss = hfssLst.get(i);
			RoleStaticData info = new RoleStaticData();
			info.setHfss(hfss);
			StatisticData type = StatisticData.search(hfss.getStatisticName());
			boolean isSuc = true;
			switch (type) {
			case S_FIT_CMD:// 指挥官总战斗力 数值
				info.setNum(sInfo.getRoleFight());
				break;
			case S_FIT_TRP:// 部队战斗力 数值
				info.setNum(sInfo.getRoleArmyFight());
				break;
			case S_FIT_BUD:// 建筑战斗力 数值
				info.setNum(sInfo.getRoleBuildFight());
				break;
			case S_FIT_TCH:// 科技战斗力 数值
				info.setNum(sInfo.getRoleTechFight());
				break;
			case S_FIT_WIN:// 战斗胜利次数 数值
				info.setNum(sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes());
				break;
			case S_FIT_FAL:// 战斗失败次数 数值
				info.setNum(sInfo.getAttackFailTimes() + sInfo.getDefenceFailTimes());
				break;
			case S_ATK_WIN:// 进攻胜利次数 数值
				info.setNum(sInfo.getAttackWinTimes());
				break;
			case S_ATK_FAL:// 进攻失败次数 数值
				info.setNum(sInfo.getAttackFailTimes());
				break;
			case S_DFC_WIN:// 防御胜利次数 数值
				info.setNum(sInfo.getDefenceWinTimes());
				break;
			case S_DFC_FAL:// 防御失败次数 数值
				info.setNum(sInfo.getDefenceFailTimes());
				break;
			case S_WIN_PER:// 胜率 百分比
				if (sInfo.getAttackFailTimes() + sInfo.getAttackWinTimes() == 0) {
					info.setNum(0);
				} else {
					float rate = (sInfo.getAttackWinTimes() + sInfo.getDefenceWinTimes()) / (sInfo.getAttackWinTimes()
							+ sInfo.getDefenceWinTimes() + sInfo.getAttackFailTimes() + sInfo.getDefenceFailTimes());
					info.setNum(rate);
				}
				break;
			case S_ITG_NUM:// 侦查次数 数值
				info.setNum(sInfo.getSpyTimes());
				break;
			case S_DST_NUM:// 消灭部队数量 数值
				info.setNum(sInfo.getKillSoldsNum());
				break;
			case S_TRL_NUM:// 部队损失数量 数值
				info.setNum(sInfo.getDeadSoldNum());
				break;
			case S_TRP_NUM:// 部队维修数量 数值
				info.setNum(sInfo.getRepairNum());
				break;
			case S_TRH_NUM:// 部队治疗数量 数值
				info.setNum(sInfo.getCureNum());
				break;
			default:
				isSuc = false;
				break;
			}
			if (!isSuc) {
				continue;
			}
			roleInfoList.add(info);
		}
		return roleInfoList;
	}

}
