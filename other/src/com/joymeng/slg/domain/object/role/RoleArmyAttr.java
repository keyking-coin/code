package com.joymeng.slg.domain.object.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.object.effect.ArmyEffVal;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;

public class RoleArmyAttr implements Instances{
	Map<Integer, List<ArmyEffVal>> effMaps = new HashMap<Integer, List<ArmyEffVal>>();

	public RoleArmyAttr(){
	}
	
	public void addEffect(ArmyEffVal val){
		List<ArmyEffVal> effs = effMaps.get(val.getType().getValue());
		if(effs == null){
			effs = new ArrayList<ArmyEffVal>();
		}
		boolean isOver = false;
		for (int i = 0 ; i < effs.size() ; i++){
			ArmyEffVal eff = effs.get(i);
			if (eff.equals(val)){
				isOver = true;
				eff.setValue(eff.getValue() + val.getValue());
			}
		}
		if(!isOver){
			effs.add(val);
		}
		effMaps.put(val.getType().getValue(), effs);
	}
	
	public void removeEffect(ArmyEffVal val){
		List<ArmyEffVal> effs = effMaps.get(val.getType().getValue());
		if(effs == null){
			return;
		}
		for(int i=0; i < effs.size();i++){
			if(val.equals(effs.get(i))){
				effs.get(i).setValue(effs.get(i).getValue() - val.getValue());
				break;
			}
		}
	}
	
	public float getEffVal(TargetType type, String armyId){
		float value = 0;
		List<ArmyEffVal> effs = effMaps.get(type.getValue());
		if(effs != null){
			for (int i = 0 ; i < effs.size() ; i++){
				ArmyEffVal eff = effs.get(i);
				if (eff.checkTargetInfo(type, armyId)){
					value += eff.getValue();
				}
			}
		}
		if (type == TargetType.T_A_RED_DR) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_HURT_SOLDIE_RRATE) / 100.0f;
			value -= newServerBuff;
		} else if (type == TargetType.T_A_IMP_SS) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_MOVE_SPEED) / 100.0f;
			value += newServerBuff;
		}else if (type == TargetType.T_A_RED_SPT){
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TRAIN_SOLDIER_TIME)/100.0f;
			value += newServerBuff;
		}
		return value ;
	}
	
	/**
	 * 获取根据目标获取buff的值(包含全兵种的buff)
	 * @param type
	 * @param exType
	 * @param typeId
	 * @return
	 */
	public float getEffValV2(TargetType type, ExtendsType exType, int typeId){
		float value = 0;
		List<ArmyEffVal> effs = effMaps.get(type.getValue());
		if (effs != null)
		{
			for (int i = 0 ; i < effs.size() ; i++){
				ArmyEffVal eff = effs.get(i);
				if(eff.checkTargetInfoByType(type, exType, typeId,true)){
					value += eff.getValue();
				}
			}
		}
		if (type == TargetType.T_A_RED_DR) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_HURT_SOLDIE_RRATE) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_IMP_SS) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_MOVE_SPEED) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_ADD_IC) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_GATHER_SPEED) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_RED_SPT) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TRAIN_SOLDIER_TIME) / 100.0f;
			value += newServerBuff;
		}
		return value;
	}
	
	/**
	 * 获取根据目标获取buff的值(不包含全兵种的buff)
	 * @param type
	 * @param exType
	 * @param typeId
	 * @return
	 */
	public float getEffValV2NoAll(TargetType type, ExtendsType exType, int typeId) {
		float value = 0;
		List<ArmyEffVal> effs = effMaps.get(type.getValue());
		if (effs != null) {
			for (int i = 0; i < effs.size(); i++) {
				ArmyEffVal eff = effs.get(i);
				if (eff.checkTargetInfoByType(type, exType, typeId, false)) {
					value += eff.getValue();
				}
			}
		}
		if (type == TargetType.T_A_RED_DR) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_HURT_SOLDIE_RRATE) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_IMP_SS) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_MOVE_SPEED) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_ADD_IC) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.ADD_TROOP_GATHER_SPEED) / 100.0f;
			value += newServerBuff;
		} else if (type == TargetType.T_A_RED_SPT) {
			float newServerBuff = NewServerBuff.iGetBuff(BuffTag.REDUCE_TRAIN_SOLDIER_TIME) / 100.0f;
			value += newServerBuff;
		}
		return value;
	}

}
