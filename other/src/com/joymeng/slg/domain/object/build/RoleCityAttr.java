package com.joymeng.slg.domain.object.build;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.object.effect.BuffTypeConst;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.Effect;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;

public class RoleCityAttr implements Instances {

	long uid;
	long cityId;
	public RoleCityAttr() {
		
	}
	
	public void init(long uid,int cityId){
		this.uid = uid;
		this.cityId = cityId;
	}

	public int getFortNum() {
		////要塞数量增加
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.G_C_ADD_FTN);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddFortNum=" + value);
		return value;
	}

	public int getAddTroopsLimit() {
		// 出征部队上限增加1支
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_TL);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddTroopsLimit=" + value);
		return value;
		// return AddTroopsLimit;
	}
	//
	// public void updateAddTroopsLimit(boolean isRemove, int addTroopsLimit) {
	// if (!isRemove) {
	// this.AddTroopsLimit += addTroopsLimit;
	// } else {
	// this.AddTroopsLimit -= addTroopsLimit;
	// }
	// }

	public int getAddSProdLimit_1() {
		//// 单次训练的士兵数量提升1
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SPL);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getExtendInfo().getType().ordinal() == ExtendsType.EXTEND_ALL.ordinal()
						|| list.get(i).getExtendInfo().getId() == 1) {
					value += list.get(i).getNum();
				}
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSProdLimit_1=" + value);
		return value;
	}

	public int getAddSProdLimit_2() {
		// 单次训练的士兵数量提升2
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SPL);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getExtendInfo().getType().ordinal() == ExtendsType.EXTEND_ALL.ordinal()
						|| list.get(i).getExtendInfo().getId() == 2) {
					value += list.get(i).getNum();
				}
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSProdLimit_2=" + value);
		return value;
	}

	public int getAddSProdLimit_3() {
		// 单次训练的士兵数量提升3
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SPL);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getExtendInfo().getType().ordinal() == ExtendsType.EXTEND_ALL.ordinal()
						|| list.get(i).getExtendInfo().getId() == 3) {
					value += list.get(i).getNum();
				}
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSProdLimit_3=" + value);
		return value;
	}

	public int getAddSProdLimit_4() {
		// 单次训练的士兵数量提升4
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SPL);
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getExtendInfo().getType().ordinal() == ExtendsType.EXTEND_ALL.ordinal()
						|| list.get(i).getExtendInfo().getId() == 4) {
					value += list.get(i).getNum();
				}

			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSProdLimit_4=" + value);
		return value;
	}

	public float getAddSoldLimit() {
		// 单支部队兵力上限增加
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SL);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSoldLimit=" + value);
		return value;
	}

	public int getAddFenceHp() {
		// 城防值增加
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_FHP);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddFenceHp=" + value);
		return value;
	}

	public int getAddFenceSpace() {
		// 城防空间增加
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_FS);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddFenceSpace=" + value);
		return value;
	}

	// public float getImpUserCityVision() {
	// return ImpUserCityVision;
	// }
	//
	// public void updateImpUserCityVisionVision(boolean isRemove, float
	// fortVision) {
	// if(!isRemove){
	// ImpUserCityVision += fortVision;
	// }else{
	// ImpUserCityVision -= fortVision;
	// }
	// }
	// public float getImpUserFortVision() {
	// return ImpUserFortVision;
	// }
	//
	// public void updateImpUserFortVision(boolean isRemove, float fortVision) {
	// if(!isRemove){
	// ImpUserFortVision += fortVision;
	// }else{
	// ImpUserFortVision -= fortVision;
	// }
	// }
	// public float getImpNpcCityVision() {
	// return ImpNpcCityVision;
	// }
	//
	// public void updateImpNpcCityVision(boolean isRemove, float fortVision) {
	// if(!isRemove){
	// ImpNpcCityVision += fortVision;
	// }else{
	// ImpNpcCityVision -= fortVision;
	// }
	// }
	public float getImpProtect() {
		// 提升资源保护比例
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.G_B_IMP_RT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ImpProtect=" + value);
		return value;
	}

	public float getAddStorageLimit() {
		// 仓库资源数量上限增加
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_SLT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|AddStorageLimit=" + value);
		return value;
	}

	public float getReduFoodCollTime() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_FCT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|ReduFoodCollTime=" + value);
		return value;
	}

	public float getReduMetalCollTime() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_MCT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|ReduFoodCollTime=" + value);
		return value;
	}

	public float getReduOilCollTime() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_OCT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|ReduOilCollTime=" + value);
		return value;
	}

	public float getReduAlloyCollTime() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_ACT);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|ReduAlloyCollTime=" + value);
		return value;
	}

	public float getImpCollSpeed(ResourceTypeConst type) {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		List<Effect> list =  new ArrayList<Effect>();
		if (role == null) {
			
		}else{
			switch (type) {
			case RESOURCE_TYPE_FOOD:
				list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.C_A_IMP_FCS);
				for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
				}
//				GameLog.info("<BUFF>uid=" + uid + "|ImpFoodCollSpeed=" + value);
				return value;
			case RESOURCE_TYPE_METAL:
				list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.C_A_IMP_MCS);
				for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
				}
//				GameLog.info("<BUFF>uid=" + uid + "|ImpMetalCollSpeed=" + value);
				return value;

			case RESOURCE_TYPE_OIL:
				list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.C_A_IMP_OCS);
				for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
				}
//				GameLog.info("<BUFF>uid=" + uid + "|ImpOilCollSpeed=" + value);
				return value;
			case RESOURCE_TYPE_ALLOY:
				list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.C_A_IMP_ACS);
				for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
				}
//				GameLog.info("<BUFF>uid=" + uid + "|ImpAlloyCollSpeed=" + value);
				return value;
			default:
				break;
			}
		}
		return 0;
	}

	public float getImpBuildSpeed() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_BUILD_TIME) / 100.0f;
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.G_C_IMP_BS);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|ImpBuildSpeed=" + value + ",newServerBuff="+newServerBuff);
		return value + newServerBuff;
	}

	public float getImpResSpeed() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_RESEARCH_SCIENCE_TIME) / 100.0f;
		// 单次训练的士兵数量提升2
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.G_C_IMP_RS);
			for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ImpResSpeed=" + value+",newServerBuff="+newServerBuff);
		return value + newServerBuff;
	}

	public int getAddWarLimit() {
		// 增加战争大厅的部队数量上限
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_WL);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddWarLimit=" + value);
		return value;
	}

	public int getAddSoldNum() {
		//// 增加战争大厅的士兵数量
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.G_B_ADD_SDN);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddSoldNum=" + value);
		return value;
	}

	public float getAddPowerProd() {
		// 发电厂的电力产量提升
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_PP);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|AddPowerProd=" + value);
		return value;
	}

	public int getAddHospCapa() {
		//// 医院伤兵数量上限提升
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_HC);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|HospCapa=" + value);
		return value;
	}

	public int getAddRepaCapa() {
		int value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_ADD_RC);
			for (int i = 0; i < list.size(); i++) {
				value += list.get(i).getNum();
			}
		}
//		GameLog.info("<BUFF>uid=" + uid + "|AddRepaCapa=" + value);
		return value;
	}

	public float getReduRepaTime() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TREAT_SOLDIER_TIME) / 100.0f;
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_RED_RT);
			for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ReduRepaTime=" + value+",newServerBuff="+newServerBuff);
		return value + newServerBuff;
	}

	public float getReduHospTime() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TREAT_SOLDIER_TIME) / 100.0f;
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_B_RED_HT);
			for (int i = 0; i < list.size(); i++) {
					value += list.get(i).getRate();
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ReduHospTime=" + value+",newServerBuff="+newServerBuff);
		return value + newServerBuff;
	}

	public float getReduRepaRes_1() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_RRHR);
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getExtendInfo().getId()==1)
					value += list.get(i).getRate();
				if (list.get(i).getExtendInfo().getType() != ExtendsType.EXTEND_BIO) {
					continue;
				}
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ReduRepaRes_1=" + value);
		return value ;
	}

	public float getReduRepaRes_2() {
		float value = 0;
		Role role = world.getOnlineRole(this.uid);
		if (role == null) {
			
		} else {
			List<Effect> list = role.getEffectAgent().searchBuffByTargetType(BuffTypeConst.TargetType.T_A_RED_RRHR);
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getExtendInfo().getId()==2)
					value += list.get(i).getRate();
				if (list.get(i).getExtendInfo().getType() != ExtendsType.EXTEND_BIO) {
					continue;
				}
			}
		}
//		GameLog.info("<BUFF>uid=" + this.uid + "|ReduRepaRes_2=" + value);
		return value ;
	}


}
