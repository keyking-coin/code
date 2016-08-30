package com.joymeng.slg.domain.object.effect;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;

public class ArmyEffVal implements Instances {
	TargetType type;// 目标效果类型
	ExtendInfo extendInfo;
	int targetTypeId;
	float value;

	public ArmyEffVal() {

	}

	public ArmyEffVal(TargetType type, ExtendInfo extendInfo, float value, int targetTypeId) {
		this.type = type;
		this.extendInfo = extendInfo;
		this.value = value;
		this.targetTypeId = targetTypeId;
	}

	public boolean checkTargetInfoByType(TargetType type, ExtendsType exType, int typeId,boolean needAll) {
		if (this.type == type) {
			if (exType == extendInfo.getType() && typeId == extendInfo.getId()) {
				return true;
			} else {
				if (needAll) {
					if (extendInfo.getType() == ExtendsType.EXTEND_ALL) {
						if (typeId != 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkTargetInfo(TargetType type, String key) {
		Army army = dataManager.serach(Army.class, key);
		if (this.type == type) {
			if (extendInfo == null) {
				return true;
			} else {
				switch (extendInfo.getType()) {
				case EXTEND_ALL:
					return true;
				case EXTEND_ARMY:
					if (extendInfo.getId() == army.getArmyType()) {
						return true;
					}
					break;
				case EXTEND_ARMOR:
					if (extendInfo.getId() == army.getArmorType()) {
						return true;
					}
					break;
				case EXTEND_BIO:
					if (extendInfo.getId() == army.getBioType()) {
						return true;
					}
					break;
				case EXTEND_SOLDIER:
					if (extendInfo.getId() == army.getSoldiersType()) {
						return true;
					}
					break;
				case EXTEND_WEAPON:
					if (extendInfo.getId() == army.getWeaponType()) {
						return true;
					}
					break;
				default:
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() == obj.getClass()) {
			ArmyEffVal val = (ArmyEffVal) obj;
			if (type == val.getType()) {
				if (extendInfo == null && val.getExtendInfo() == null)
					return true;
				else if (extendInfo.equals(val.getExtendInfo()))
					return true;
			}
		}
		return false;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public TargetType getType() {
		return type;
	}

	public void setType(TargetType type) {
		this.type = type;
	}

	public ExtendInfo getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(ExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}

}
