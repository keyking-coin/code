package com.joymeng.slg.domain.event.impl;

import java.util.List;

import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.build.BuildComponentType;
import com.joymeng.slg.domain.object.build.BuildName;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.build.impl.BuildComponentDefense;
import com.joymeng.slg.domain.object.effect.ArmyEffVal;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.RespModuleSet;

public class EffectEvent extends AbstractGameEvent{

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
		case EFFECT_UPDATE:{
				Effect e = (Effect)get(params[1]);
				TargetType targetType = e.getType();
				if (targetType == null){
					GameLog.error("buff固化表又错了");
					return;
				}
				switch(targetType){
				case G_R_IMP_VS:		//体力恢复速度
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					role.getRoleStamina().updateStaminaSpeed(isRemove, value);
					RespModuleSet rms = new RespModuleSet();
					role.getRoleStamina().sendToClient(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				} 
				case T_B_ADD_SLT:		//仓库资源数量上限增加
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddStorageLimit(isRemove, value);
					}
					break;
				}
				case G_B_IMP_RT: //提升资源保护比例
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpProtect(isRemove, value);
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.LOGISTICS_CENTER.getKey(), e.getType().getName(), type, 0, value);
						}else{
							agent.removeBuffInMap(BuildName.LOGISTICS_CENTER.getKey(), e.getType().getName(), type, 0, value);
						}
					}
					break;
				}
				case T_B_ADD_HC:	//医院伤兵数量上限提升
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddHospCapa(isRemove, value);
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, value, 0);
						}else{
							agent.removeBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, value, 0);
						}
					}
					break;
				}
				case T_B_ADD_RC:	//增加维修厂的受损机械容量
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddRepaCapa(isRemove, value);
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, value, 0);
						}else{
							agent.removeBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, value, 0);
						}
					}
					break;
				}
				case T_B_ADD_SL://单支部队兵力上限增加
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddSoldLimit(isRemove, value);
						RespModuleSet rms = new RespModuleSet();
						agent.sendToClient(rms, false);
						MessageSendUtil.sendModule(rms, role.getUserInfo());
					}
					RespModuleSet rms = new RespModuleSet();
					role.sendArmyMobiBuff(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case T_B_ADD_TL://出征部队上限增加1支
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddTroopsLimit(isRemove, value);
					}
					RespModuleSet rms = new RespModuleSet(); 
					role.sendRoleToClient(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case T_B_ADD_SPL://单次训练的士兵数量提升
				{
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddSProdLimit(isRemove, e.getExtendInfo(), e.getNum());
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.MILITARY_SCHOOL.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}else{
							agent.removeBuffInMap(BuildName.MILITARY_SCHOOL.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}
						RespModuleSet rms = new RespModuleSet();
						agent.sendToClient(rms, false);
						MessageSendUtil.sendModule(rms, role.getUserInfo());
					}
					break;
				}
				case T_B_ADD_FHP://城防值增加
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for (RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddFenceHp(isRemove, value);
						agent.setWallBuff(isRemove, value);
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.FENCE.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}else{
							agent.removeBuffInMap(BuildName.FENCE.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}
					}
					break;
				}
				case T_B_ADD_FS://城防空间增加
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddFenceSpace(isRemove, value);
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.FENCE.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}else{
							agent.removeBuffInMap(BuildName.FENCE.getKey(), e.getType().getName(), type, e.getNum(), 0);
						}
					}
					break;
				}
				case T_B_ADD_PP://发电厂的电力产量提升	
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddPowerProd(isRemove, value);
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.POWER_PLANT.getKey(), e.getType().getName(), type, 0, value);
						}else{
							agent.removeBuffInMap(BuildName.POWER_PLANT.getKey(), e.getType().getName(), type, 0, value);
						}
						RespModuleSet rms = new RespModuleSet(); 
						agent.sendToClient(rms, false);
						MessageSendUtil.sendModule(rms, role.getUserInfo());
					}
					break;
				}
				case G_B_ADD_SDN://增加战争大厅的士兵数量
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddSoldNum(isRemove, value);
						
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.WAR_LOBBY.getKey(), e.getType().getName(), type, 0, value);
						}else{
							agent.removeBuffInMap(BuildName.WAR_LOBBY.getKey(), e.getType().getName(), type, 0, value);
						}
					}
					break;
				}
				case T_B_ADD_WL://增加战争大厅的部队数量上限
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateAddWarLimit(isRemove, value);
						
						byte type = 1;
						if(!isRemove){
							agent.addBuffInMap(BuildName.WAR_LOBBY.getKey(), e.getType().getName(), type, value, 0);
						}else{
							agent.removeBuffInMap(BuildName.WAR_LOBBY.getKey(), e.getType().getName(), type, value, 1);
						}
					}
					break;
				}
				case T_B_IMP_FP:		//食品生产率提升
				{
					String buildId = BuildName.FOOD_FACT.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case T_B_IMP_MP:		//金属生产率提升
				{
					String buildId = BuildName.SMELTER.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case T_B_IMP_OP:		//石油生产率提升
				{
					String buildId = BuildName.REFINERY.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case T_B_IMP_AP:		//钛合金生产率提升
				{
					String buildId = BuildName.TITANIUM_PLANT.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case B_ADD_FOOD_PROD:   //增加资源建筑物食品产量
				{
					String buildId = BuildName.FOOD_FACT.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case B_ADD_METAL_PROD:  //增加资源建筑物金属产量
				{
					String buildId = BuildName.SMELTER.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case B_ADD_OIL_PROD:	//增加资源建筑物石油产量
				{
					String buildId = BuildName.REFINERY.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case B_ADD_ALLOY_PROD:	//增加资源建筑物钛合金产量
				{
					String buildId = BuildName.TITANIUM_PLANT.getKey();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, buildId);
					}
					break;
				}
				case C_A_RED_RES://核弹4类资源减产50%
				{
					boolean isRemove = get(params[2]);
					if(e.isPercent()){
						e.setRate(e.getRate()*(-1));
					}else{
						e.setNum(e.getNum()*(-1));
					}
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.updateProductionBuff(e, isRemove, null);
					}
					break;
				}
				case T_B_RED_HT: 	//减少医院的伤兵的治疗时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduHospTime(isRemove, value);
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
						}else{
							agent.removeBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
						}
					}
					break;
				}
				case T_B_RED_RT: 	// 减少维修厂的受损机械的维修时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduRepaTime(isRemove, value);
						
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
						}else{
							agent.removeBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
						}
					}
					break;
				}
				case T_A_RED_RRHR:	//治疗伤兵的资源降低, 维修机械的资源降低
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
					agent.getCityAttr().updateReduRepaRes(isRemove, value, e.getExtendInfo());
					byte type = 0;
					if (!isRemove) {
						if (e.getExtendInfo().getId() == 1) {
							agent.addBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
						} else {
							agent.addBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
						}
					} else {
						if (e.getExtendInfo().getId() == 1) {
							agent.removeBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
						} else {
							agent.removeBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
						}
					}
				}
					break;
				}
				case C_A_IMP_FCS://提升食品采集速度
				{	
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpFoodCollSpeed(isRemove, value);
					}
					break;
				}
				case C_A_IMP_MCS://提升金属采集速度
				{	
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpMetalCollSpeed(isRemove, value);
					}
					break;
				}
				case C_A_IMP_OCS://提升石油采集速度
				{	
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpOilCollSpeed(isRemove, value);
					}
					break;
				}
				case C_A_IMP_ACS://提升钛合金采集速度
				{	
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpAlloyCollSpeed(isRemove, value);
					}
					break;
				}
				case T_A_RED_FCT://减少部队的食品的采集时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduFoodCollTime(isRemove, value);
					}
					break;
				}
				case T_A_RED_MCT://减少部队的金属的采集时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduMetalCollTime(isRemove, value);
					}
					break;
				}
				case T_A_RED_OCT://减少部队的石油的采集时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduOilCollTime(isRemove, value);
					}
					break;
				}
				case T_A_RED_ACT://减少部队的钛合金的采集时间
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateReduAlloyCollTime(isRemove, value);
					}
					break;
				}
				case T_A_IMP_SS://提升部队的机动力
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
					if (!isRemove) {
						role.getArmyAttr().addEffect(val);
					} else {
						role.getArmyAttr().removeEffect(val);
					}
					RespModuleSet rms = new RespModuleSet();
					role.sendArmyMobiBuff(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case T_A_ADD_IC://提升采集速度
				{
//						if (e.getTimer() == null) {
							float value = e.getRate();
							boolean isRemove = get(params[2]);
							ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
							if (!isRemove) {
								role.getArmyAttr().addEffect(val);
							} else {
								role.getArmyAttr().removeEffect(val);
							}
//						}
					break;
				}
				case T_A_RED_DR://减少士兵死亡率	
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
					if (!isRemove) {
						role.getArmyAttr().addEffect(val);
					} else {
						role.getArmyAttr().removeEffect(val);
					}
					for (RoleCityAgent agent : role.getCityAgents()) {
						byte type = 0;
						if (!isRemove) {
							if (e.getExtendInfo().getId() == 0) {
								agent.addBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
								agent.addBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
							}
						} else {
							if (e.getExtendInfo().getId() == 0) {
								agent.removeBuffInMap(BuildName.HOSPITAL.getKey(), e.getType().getName(), type, 0, value);
								agent.removeBuffInMap(BuildName.REPAIRER.getKey(), e.getType().getName(), type, 0, value);
							}
						}
					}
					break;
				}
				case T_A_RED_SPT://士兵生产时间缩短
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
					if(!isRemove){
						role.getArmyAttr().addEffect(val);
					}else{
						role.getArmyAttr().removeEffect(val);
					}
					for(RoleCityAgent agent : role.getCityAgents()){
						byte type = 0;
						switch(e.getExtendInfo().getType()){
						case EXTEND_ALL:
							if(!isRemove){
								agent.addBuffInMap(BuildName.SOLDIERS_CAMP.getKey(), e.getType().getName(), type, 0, value);
								agent.addBuffInMap(BuildName.WAR_FACT.getKey(), e.getType().getName(), type, 0, value);
								agent.addBuffInMap(BuildName.ARMORED_FACT.getKey(), e.getType().getName(), type, 0, value);
								agent.addBuffInMap(BuildName.AIR_COM.getKey(), e.getType().getName(), type, 0, value);
								agent.addBuffInMap(BuildName.MILITARY_FACT.getKey(), e.getType().getName(), type, 0, value);
							}else{
								agent.removeBuffInMap(BuildName.SOLDIERS_CAMP.getKey(), e.getType().getName(), type, 0, value);
								agent.removeBuffInMap(BuildName.WAR_FACT.getKey(), e.getType().getName(), type, 0, value);
								agent.removeBuffInMap(BuildName.ARMORED_FACT.getKey(), e.getType().getName(), type, 0, value);
								agent.removeBuffInMap(BuildName.AIR_COM.getKey(), e.getType().getName(), type, 0, value);
								agent.removeBuffInMap(BuildName.MILITARY_FACT.getKey(), e.getType().getName(), type, 0, value);
							}
							break;
						case EXTEND_ARMY:
							switch(e.getExtendInfo().getId()){
							case 1:
								if(!isRemove){
									agent.addBuffInMap(BuildName.SOLDIERS_CAMP.getKey(), e.getType().getName(), type, 0, value);
								}else{
									agent.removeBuffInMap(BuildName.SOLDIERS_CAMP.getKey(), e.getType().getName(), type, 0, value);
								}
								break;
							case 2:
								if(!isRemove){
									agent.addBuffInMap(BuildName.WAR_FACT.getKey(), e.getType().getName(), type, 0, value);
								}else{
									agent.removeBuffInMap(BuildName.WAR_FACT.getKey(), e.getType().getName(), type, 0, value);
								}
								break;
							case 3:
								if(!isRemove){
									agent.addBuffInMap(BuildName.ARMORED_FACT.getKey(), e.getType().getName(), type, 0, value);
								}else{
									agent.removeBuffInMap(BuildName.ARMORED_FACT.getKey(), e.getType().getName(), type, 0, value);
								}
								break;
							case 4:
								if(!isRemove){
									agent.addBuffInMap(BuildName.AIR_COM.getKey(), e.getType().getName(), type, 0, value);
								}else{
									agent.removeBuffInMap(BuildName.AIR_COM.getKey(), e.getType().getName(), type, 0, value);
								}
								break;
							case 5:
								if(!isRemove){
									agent.addBuffInMap(BuildName.MILITARY_FACT.getKey(), e.getType().getName(), type, 0, value);
								}else{
									agent.removeBuffInMap(BuildName.MILITARY_FACT.getKey(), e.getType().getName(), type, 0, value);
								}
								break;
							}
							break;
							default:
								break;
						}
					}
					break;
				}
				case T_A_RED_SC://部队消耗食品降低
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
					if(!isRemove){
						role.getArmyAttr().addEffect(val);
					}else{
						role.getArmyAttr().removeEffect(val);
					}
					RespModuleSet rms = new RespModuleSet();
					RoleCityAgent city = role.getCity(0);
					city.getArmyAgent().sendToClient(rms,city);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case T_A_IMP_SW://部队负重提升
				case T_A_IMP_SD://兵种防御提升
				case T_A_IMP_SA://兵种攻击提升
				case T_A_IMP_DMG://提升部队的伤害
				case T_A_IMP_AHP://部队生命值提升
				case T_A_IMP_ICR://部队的暴击值
				case T_A_IMP_IAR://部队的命中值
				case T_A_IMP_IER://部队的闪避值
				case C_A_RED_BDMG://降低部队承受的伤害	
				case C_A_RED_MB://降低部队机动力	
				case C_A_RED_ATK://降低部队的火力	
				case C_A_RED_DEF://降低部队的防御力	
				case C_A_RED_HP://降低部队的生命值	
				case C_A_RED_CRT://降低部队的暴击值	
				case C_A_RED_ATR://降低部队的命中值	
				case C_A_RED_EDR://降低部队的闪避值	
				case C_A_RED_DMG://降低部队的伤害
				case C_A_RED_BDMG_ALL://降低部队承受的伤害
				case C_RED_ALL_DG://降低自己的所有部队攻击力
				{
					float value = e.getRate();
					boolean isRemove = get(params[2]);
					ArmyEffVal val = new ArmyEffVal(e.getType(), e.getExtendInfo(), value, e.getTargetTypeId());
					if(!isRemove){
						role.getArmyAttr().addEffect(val);
					}else{
						role.getArmyAttr().removeEffect(val);
					}
					RespModuleSet rms = new RespModuleSet();
					if (targetType == TargetType.T_A_IMP_SA ||
						targetType == TargetType.T_A_IMP_SD ||
						targetType == TargetType.T_A_IMP_AHP){
							if (e.getExtendInfo().getType() == ExtendsType.EXTEND_SOLDIER &&
								(e.getExtendInfo().getId() == 41 || 
								 e.getExtendInfo().getId() == 42 || 
								 e.getExtendInfo().getId() == 43)){
								//光棱塔、巨炮、磁暴线圈
								RoleCityAgent city = role.getCity(0);
								String buildkey = BuildName.GRANDE_CANNON.getKey();
								if (e.getExtendInfo().getId() == 41){
									buildkey = BuildName.LASER_TOWER.getKey();
								}else if (e.getExtendInfo().getId() == 42){
									buildkey = BuildName.TESLA_COIL.getKey();
								}
								byte paramType = 0;
								int preMax = 0;
								List<RoleBuild> builds = null;
								if (targetType == TargetType.T_A_IMP_AHP){
									builds = city.searchBuildByBuildId(buildkey);
									if (builds.size() > 0){
										BuildComponentDefense bcd = builds.get(0).getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
										if (bcd != null){
											preMax = bcd.getDefenceHPVal();
										}
									}
								}
								if (!isRemove){
									city.addBuffInMap(buildkey,e.getType().getName(),paramType,0,value);
								}else{
									city.removeBuffInMap(buildkey,e.getType().getName(),paramType,0,value);
								}
								if (targetType == TargetType.T_A_IMP_AHP){
									if (builds.size() > 0){
										RoleBuild build = builds.get(0);
										BuildComponentDefense bcd = build.getComponent(BuildComponentType.BUILD_COMPONENT_DEFENSE);
										if (bcd != null){
											bcd.updateBuffValue(preMax);
										}
										build.sendToClient(rms);
									}
								}
								city.sendCityBuffToClient(rms);
							}
					}
					role.sendArmyMobiBuff(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case G_C_ADD_FTN:		//要塞数量增加
				{
					int value = e.getNum();
					boolean isRemove = get(params[2]);
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateFortNum(isRemove, value);
					}
					RespModuleSet rms = new RespModuleSet();
					role.sendArmyMobiBuff(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case C_ADD_BUILD_QUEUE:	//增加建筑队列时间
				{
					break;
				}
				case G_R_PHY_NUM:  //购买体力
				{
					boolean isRemove= get(params[2]);
					int times = e.getNum();
					if(isRemove){
						role.getRoleStamina().updateBuyTimes(-times);
					}else{
						role.getRoleStamina().updateBuyTimes(times);
					}
					break;
				}
				case G_C_REDU_BT:	//免费加速时间增加
				{
					boolean isRemove= get(params[2]);
					long time = e.getNum();
					if(isRemove){
						role.addFreeTimeBuff(time*(-1));
					}else{
						role.addFreeTimeBuff(time);
					}
					break;
				}
				case G_C_IMP_BS://提升建造速度
				{
					boolean isRemove= get(params[2]);
					float rate = e.getRate();
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpBuildSpeed(isRemove, rate);
					}
					RespModuleSet rms = new RespModuleSet();
					role.sendArmyMobiBuff(rms);
					MessageSendUtil.sendModule(rms, role.getUserInfo());
					break;
				}
				case G_C_IMP_RS://提升研究速度
				{
					boolean isRemove= get(params[2]);
					float rate = e.getRate();
					for(RoleCityAgent agent : role.getCityAgents()){
						agent.getCityAttr().updateImpResSpeed(isRemove, rate);
						
						byte type = 0;
						if(!isRemove){
							agent.addBuffInMap(BuildName.TECH_CENTER.getKey(), e.getType().getName(), type, 0, rate);
						}else{
							agent.removeBuffInMap(BuildName.TECH_CENTER.getKey(), e.getType().getName(), type, 0, rate);
						}
					}
					break;
				}
					default:
						break;
				}
			}
			break;
		}
	}

}
