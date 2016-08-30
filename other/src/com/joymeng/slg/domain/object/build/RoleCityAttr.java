package com.joymeng.slg.domain.object.build;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.object.effect.ExtendInfo;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;

public class RoleCityAttr implements Instances {
	int AddFortNum = 0; // 增加城市可以拥有的要塞数量
	// float ImpUserCityVision = 0;//扩大玩家城市的视野
	// float ImpUserFortVision = 0;//扩大要塞/军营的视野
	// float ImpNpcCityVision = 0;//扩大NPC城市的视野
	int addBuildQueue = 0; // 增加建造队列
	float AddmaterialsProd = 0;// 增加城外矿石的产量

	float AddStorageLimit = 0;// 增加物流中心的资源保护比例
	float ImpProtect = 0;// 提升资源保护比例

	int AddTroopsLimit = 0; // 出征部队数量上限增加
	int AddSProdLimit_1 = 0;// 单次训练的士兵数量提升
	int AddSProdLimit_2 = 0;// 单次训练的士兵数量提升
	int AddSProdLimit_3 = 0;// 单次训练的士兵数量提升
	int AddSProdLimit_4 = 0;// 单次训练的士兵数量提升
	float AddSoldLimit = 0;// 单支部队的兵力数量上限

	int AddFenceHp = 0; // 增加城墙的生命值
	int AddFenceSpace = 0; // 增加城墙的建造空间
	int AddWarLimit = 0; // 增加集结的队伍数量
	int AddSoldNum = 0; // 增加集结的部队空间
	float AddPowerProd = 0; // 发电厂的电力产量提升

	int AddHospCapa = 0; // 增加医院的伤兵容量
	int AddRepaCapa = 0; // 增加维修厂的受损机械容量
	float ReduHospTime = 0; // 减少医院的伤兵的治疗时间
	float ReduRepaTime = 0; // 减少维修厂的受损机械的维修时间
	float ReduRepaRes_1 = 0; // 治疗伤兵的资源降低
	float ReduRepaRes_2 = 0; // 维修机械的资源降低

	float ReduFoodCollTime = 0; // 减少部队的食品的采集时间
	float ReduMetalCollTime = 0;// 减少部队的金属的采集时间
	float ReduOilCollTime = 0; // 减少部队的石油的采集时间
	float ReduAlloyCollTime = 0;// 减少部队的钛合金的采集时间

	float ImpFoodCollSpeed = 0; // 增加食品的采集速度
	float ImpMetalCollSpeed = 0;// 增加金属的采集速度
	float ImpOilCollSpeed = 0; // 增加石油的采集速度
	float ImpAlloyCollSpeed = 0;// 增加钛合金的采集速度

	float ImpBuildSpeed = 0;// 提高建造速度
	float ImpResSpeed = 0;// 提高研究速度

	public RoleCityAttr() {
	}

	public int getFortNum() {
		return AddFortNum;
	}

	public void updateFortNum(boolean isRemove, int fortNum) {
		if (!isRemove) {
			AddFortNum += fortNum;
		} else {
			AddFortNum -= fortNum;
		}
	}

	public int getAddBuildQueue() {
		return addBuildQueue;
	}

	public void updateAddBuildQueue(boolean isRemove, int addBuildQueue) {
		if (!isRemove) {
			this.addBuildQueue += addBuildQueue;
		} else {
			this.addBuildQueue -= addBuildQueue;
		}
	}

	public int getAddTroopsLimit() {
		return AddTroopsLimit;
	}

	public void updateAddTroopsLimit(boolean isRemove, int addTroopsLimit) {
		if (!isRemove) {
			this.AddTroopsLimit += addTroopsLimit;
		} else {
			this.AddTroopsLimit -= addTroopsLimit;
		}
	}

	public int getAddSProdLimit_1() {
		return AddSProdLimit_1;
	}

	public int getAddSProdLimit_2() {
		return AddSProdLimit_2;
	}

	public int getAddSProdLimit_3() {
		return AddSProdLimit_3;
	}

	public int getAddSProdLimit_4() {
		return AddSProdLimit_4;
	}

	public void updateAddSProdLimit(boolean isRemove, ExtendInfo info, int num) {
		if (info.getType().ordinal() == ExtendsType.EXTEND_ALL.ordinal()) {
			if (isRemove) {
				AddSProdLimit_1 -= num;
				AddSProdLimit_2 -= num;
				AddSProdLimit_3 -= num;
				AddSProdLimit_4 -= num;
			} else {
				AddSProdLimit_1 += num;
				AddSProdLimit_2 += num;
				AddSProdLimit_3 += num;
				AddSProdLimit_4 += num;
			}
			return;
		}
		switch (info.getId()) {
		case 1:
			if (!isRemove) {
				this.AddSProdLimit_1 += num;
			} else {
				this.AddSProdLimit_1 -= num;
			}
			break;
		case 2:
			if (!isRemove) {
				this.AddSProdLimit_2 += num;
			} else {
				this.AddSProdLimit_2 -= num;
			}
			break;
		case 3:
			if (!isRemove) {
				this.AddSProdLimit_3 += num;
			} else {
				this.AddSProdLimit_3 -= num;
			}
			break;
		case 4:
			if (!isRemove) {
				this.AddSProdLimit_4 += num;
			} else {
				this.AddSProdLimit_4 -= num;
			}
			break;
		default:
			break;
		}
	}

	public float getAddSoldLimit() {
		return AddSoldLimit;
	}

	public void updateAddSoldLimit(boolean isRemove, float addSoldLimit) {
		if (!isRemove) {
			this.AddSoldLimit += addSoldLimit;
		} else {
			this.AddSoldLimit -= addSoldLimit;
		}
	}

	public int getAddFenceHp() {
		return AddFenceHp;
	}

	public void updateAddFenceHp(boolean isRemove, int addFenceHp) {
		if (!isRemove) {
			this.AddFenceHp += addFenceHp;
		} else {
			this.AddFenceHp -= addFenceHp;
		}
	}

	public int getAddFenceSpace() {
		return AddFenceSpace;
	}

	public void updateAddFenceSpace(boolean isRemove, int addFenceSpace) {
		if (!isRemove) {
			this.AddFenceSpace += addFenceSpace;
		} else {
			this.AddFenceSpace -= addFenceSpace;
		}
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
		return ImpProtect;
	}

	public void updateImpProtect(boolean isRemove, float impProtect) {
		if (!isRemove) {
			ImpProtect += impProtect;
		} else {
			ImpProtect += impProtect;
		}
	}

	public float getAddStorageLimit() {
		return AddStorageLimit;
	}

	public void updateAddStorageLimit(boolean isRemove, float addResProt) {
		if (!isRemove) {
			this.AddStorageLimit += addResProt;
		} else {
			this.AddStorageLimit -= addResProt;
		}
	}

	public float getReduFoodCollTime() {
		return ReduFoodCollTime;
	}

	public void updateReduFoodCollTime(boolean isRemove, float reduFoodCollTime) {
		if (!isRemove) {
			this.ReduFoodCollTime += reduFoodCollTime;
		} else {
			this.ReduFoodCollTime -= reduFoodCollTime;
		}
	}

	public float getReduMetalCollTime() {
		return ReduMetalCollTime;
	}

	public void updateReduMetalCollTime(boolean isRemove, float reduMetalCollTime) {
		if (!isRemove) {
			this.ReduMetalCollTime += reduMetalCollTime;
		} else {
			this.ReduMetalCollTime -= reduMetalCollTime;
		}
	}

	public float getReduOilCollTime() {
		return ReduOilCollTime;
	}

	public void updateReduOilCollTime(boolean isRemove, float reduOilCollTime) {
		if (!isRemove) {
			this.ReduOilCollTime += reduOilCollTime;
		} else {
			this.ReduOilCollTime -= reduOilCollTime;
		}
	}

	public float getReduAlloyCollTime() {
		return ReduAlloyCollTime;
	}

	public void updateReduAlloyCollTime(boolean isRemove, float reduAlloyCollTime) {
		if (!isRemove) {
			this.ReduAlloyCollTime += reduAlloyCollTime;
		} else {
			this.ReduAlloyCollTime -= reduAlloyCollTime;
		}
	}

	public float getImpCollSpeed(ResourceTypeConst type) {
		switch (type) {
		case RESOURCE_TYPE_FOOD:
			return ImpFoodCollSpeed;
		case RESOURCE_TYPE_METAL:
			return ImpMetalCollSpeed;
		case RESOURCE_TYPE_OIL:
			return ImpOilCollSpeed;
		case RESOURCE_TYPE_ALLOY:
			return ImpAlloyCollSpeed;
		default:
			break;
		}
		return 0;
	}

	public void updateImpFoodCollSpeed(boolean isRemove, float impFoodCollSpeed) {
		if (!isRemove) {
			this.ImpFoodCollSpeed += impFoodCollSpeed;
		} else {
			this.ImpFoodCollSpeed -= impFoodCollSpeed;
		}
	}

	public void updateImpMetalCollSpeed(boolean isRemove, float impMetalCollSpeed) {
		if (!isRemove) {
			this.ImpMetalCollSpeed += impMetalCollSpeed;
		} else {
			this.ImpMetalCollSpeed -= impMetalCollSpeed;
		}
	}

	public void updateImpOilCollSpeed(boolean isRemove, float impOilCollSpeed) {
		if (!isRemove) {
			this.ImpOilCollSpeed += impOilCollSpeed;
		} else {
			this.ImpOilCollSpeed -= impOilCollSpeed;
		}
	}

	public void updateImpAlloyCollSpeed(boolean isRemove, float impAlloyCollSpeed) {
		if (!isRemove) {
			this.ImpAlloyCollSpeed += impAlloyCollSpeed;
		} else {
			this.ImpAlloyCollSpeed -= impAlloyCollSpeed;
		}
	}

	public float getImpBuildSpeed() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_BUILD_TIME) / 100.0f;
		return ImpBuildSpeed + newServerBuff;
	}

	public void updateImpBuildSpeed(boolean isRemove, float impBuildSpeed) {
		if (!isRemove) {
			this.ImpBuildSpeed += impBuildSpeed;
		} else {
			this.ImpBuildSpeed -= impBuildSpeed;
		}
	}

	public float getImpResSpeed() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_RESEARCH_SCIENCE_TIME) / 100.0f;
		return ImpResSpeed + newServerBuff;
	}

	public void updateImpResSpeed(boolean isRemove, float impResSpeed) {
		if (!isRemove) {
			this.ImpResSpeed += impResSpeed;
		} else {
			this.ImpResSpeed -= impResSpeed;
		}
	}

	public float getAddMaterialsProd() {
		return AddmaterialsProd;
	}

	public void updateAddMaterialsProd(byte type, float addMaterialsProd) {
		if (type == 0) {
			this.AddmaterialsProd += addMaterialsProd;
		} else {
			this.AddmaterialsProd -= addMaterialsProd;
		}
	}

	public int getAddWarLimit() {
		return AddWarLimit;
	}

	public void updateAddWarLimit(boolean isRemove, int addWarLimit) {
		if (!isRemove) {
			this.AddWarLimit += addWarLimit;
		} else {
			this.AddWarLimit -= addWarLimit;
		}
	}

	public int getAddSoldNum() {
		return AddSoldNum;
	}

	public void updateAddSoldNum(boolean isRemove, int addSoldNum) {
		if (!isRemove) {
			this.AddSoldNum += addSoldNum;
		} else {
			this.AddSoldNum -= addSoldNum;
		}
	}

	public float getAddPowerProd() {
		return AddPowerProd;
	}

	public void updateAddPowerProd(boolean isRemove, float addPowerProd) {
		if (!isRemove) {
			this.AddPowerProd += addPowerProd;
		} else {
			this.AddPowerProd -= addPowerProd;
		}
	}

	public int getAddHospCapa() {
		return AddHospCapa;
	}

	public void updateAddHospCapa(boolean isRemove, int addHospCapa) {
		if (isRemove) {
			AddHospCapa -= addHospCapa;
		} else {
			AddHospCapa += addHospCapa;
		}
	}

	public int getAddRepaCapa() {
		return AddRepaCapa;
	}

	public void updateAddRepaCapa(boolean isRemove, int addHospCapa) {
		if (isRemove) {
			AddRepaCapa -= addHospCapa;
		} else {
			AddRepaCapa += addHospCapa;
		}
	}

	public float getReduRepaTime() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TREAT_SOLDIER_TIME) / 100.0f;
		return ReduRepaTime + newServerBuff;
	}

	public void updateReduRepaTime(boolean isRemove, float reduRepaTime) {
		if (isRemove) {
			this.ReduRepaTime -= reduRepaTime;
		} else {
			this.ReduRepaTime += reduRepaTime;
		}
	}

	public float getReduHospTime() {
		float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TREAT_SOLDIER_TIME) / 100.0f;
		return ReduHospTime + newServerBuff;
	}

	public void updateReduHospTime(boolean isRemove, float ReduHospTime) {
		if (isRemove) {
			this.ReduHospTime -= ReduHospTime;
		} else {
			this.ReduHospTime += ReduHospTime;
		}
	}

	public float getReduRepaRes_1() {
		return ReduRepaRes_1;
	}

	public float getReduRepaRes_2() {
		return ReduRepaRes_2;
	}

	public void updateReduRepaRes(boolean isRemove, float ReduRepaRes, ExtendInfo info) {
		if (info.getType() != ExtendsType.EXTEND_BIO) {
			return;
		}
		switch (info.getId()) {
		case 1:
			if (isRemove) {
				this.ReduRepaRes_1 -= ReduRepaRes;
			} else {
				this.ReduRepaRes_1 += ReduRepaRes;
			}
			break;
		case 2:
			if (isRemove) {
				this.ReduRepaRes_2 -= ReduRepaRes;
			} else {
				this.ReduRepaRes_2 += ReduRepaRes;
			}
			break;
		}
	}

}
