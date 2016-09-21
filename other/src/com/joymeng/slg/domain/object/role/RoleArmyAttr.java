package com.joymeng.slg.domain.object.role;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff;
import com.joymeng.slg.domain.actvt.impl.NewServerBuff.BuffTag;
import com.joymeng.slg.domain.object.effect.ArmyEffVal;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.Effect;

public class RoleArmyAttr implements Instances{
//	Map<Integer, List<ArmyEffVal>> effMaps = new HashMap<Integer, List<ArmyEffVal>>();

	public RoleArmyAttr(){
		
	}
	
	/**
	 * 
	* @Title: getEffMaps 
	* @Description: 得到对应的buffer效果
	* 
	* @return List<ArmyEffVal>
	* @param role
	* @param type
	* @return
	 */
	public static List<ArmyEffVal> getEffMaps(Role role,TargetType type){
		List<ArmyEffVal> effMaps = new ArrayList<ArmyEffVal>();
		
		List<Effect> effects = role.effectAgent.searchBuffByTargetType(type);
		for(Effect eff: effects){
			ArmyEffVal val = new ArmyEffVal(eff.getType(), eff.getExtendInfo(), eff.getRate(), eff.getTargetTypeId());
			effMaps.add(val);
		}
		return effMaps;
	}
	
//	private void addEffect(ArmyEffVal val,List<ArmyEffVal> ArmyEffValList){
//		boolean isOver = false;
//		for (int i = 0 ; i < ArmyEffValList.size() ; i++){
//			ArmyEffVal eff = ArmyEffValList.get(i);
//			if (eff.equals(val)){
//				isOver = true;
//				eff.setValue(eff.getValue() + val.getValue());
//			}
//		}
//		if(!isOver){
//			ArmyEffValList.add(val);
//		}
//	}
	
//	public void removeEffect(ArmyEffVal val){
//		List<ArmyEffVal> effs = effMaps.get(val.getType().getValue());
//		if(effs == null){
//			return;
//		}
//		for(int i=0; i < effs.size();i++){
//			if(val.equals(effs.get(i))){
//				effs.get(i).setValue(effs.get(i).getValue() - val.getValue());
//				break;
//			}
//		}
//	}
	
	public static float getEffVal(Role role,TargetType type, String armyId){
		float value = 0;
		List<ArmyEffVal> effs = getEffMaps(role, type);
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
		GameLog.info("[getEffVal]uid="+role.getJoy_id()+"|type="+type.getName()+"|armyId="+armyId+"|value="+value);
		return value ;
	}
	
	/**
	 * 获取根据目标获取buff的值(包含全兵种的buff)
	 * @param type
	 * @param exType
	 * @param typeId
	 * @return
	 */
	public static float getEffValV2(Role role,TargetType type, ExtendsType exType, int typeId){
		float value = 0;
		List<ArmyEffVal> effs = getEffMaps(role, type);
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
		GameLog.info("[getEffValV2]uid="+role.getJoy_id()+"|type="+type.getName()+"|ExtendsType="+exType.getName()+"|typeId="+typeId+"|value="+value);
		return value;
	}
	
	/**
	 * 获取根据目标获取buff的值(不包含全兵种的buff)
	 * @param type
	 * @param exType
	 * @param typeId
	 * @return
	 */
	public static float getEffValV2NoAll(Role role,TargetType type, ExtendsType exType, int typeId) {
		float value = 0;
		List<ArmyEffVal> effs = getEffMaps(role, type);
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
		
		GameLog.info("[getEffValV2NoAll]uid="+role.getJoy_id()+"|type="+type.getName()+"|ExtendsType="+exType.getName()+"|typeId="+typeId+"|value="+value);
		return value;
	}

}
