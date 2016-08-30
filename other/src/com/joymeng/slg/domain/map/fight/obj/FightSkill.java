package com.joymeng.slg.domain.map.fight.obj;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffTargetType;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffType;
import com.joymeng.slg.domain.map.fight.result.SkillInfo;
import com.joymeng.slg.domain.object.army.data.Skill;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.object.effect.data.Buff;


public class FightSkill implements Instances{
	String key;
	String buffId;
	float value;
	int type;
	float rate;
	int target;
	int lastRound;
	boolean cover;//false叠加,true覆盖
	
	public FightSkill (Skill skill){
		key    = skill.getId();
		buffId = skill.getBuffID();
		value  = (float)skill.getBuffvalue();
		type   = skill.getSkilltype();
		rate   = (float)skill.getTriggerProb();
		target = skill.getTarget();
		lastRound = skill.getLastround();
		cover  = skill.getBuffcover() == 1;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBuffId() {
		return buffId;
	}
	
	public void setBuffId(String buffId) {
		this.buffId = buffId;
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	public float getRate() {
		return rate;
	}
	public void setRate(float rate) {
		this.rate = rate;
	}
	
	public int getTarget() {
		return target;
	}
	
	public void setTarget(int target) {
		this.target = target;
	}
	
	public int getLastRound() {
		return lastRound;
	}
	
	public void setLastRound(int lastRound) {
		this.lastRound = lastRound;
	}
	
	public boolean isCover() {
		return cover;
	}
	
	public void setCover(boolean cover) {
		this.cover = cover;
	}
	
	public boolean isHappen(){
		int ran = MathUtils.random(100);
		if (ran <= rate * 100){
			return true;
		}
		return false;
	}

	public void effect(List<SkillInfo> sis, FightTroops myself,FightTroops tar,List<FightTroops> team,List<FightTroops> emenys) {
		Buff buffData = dataManager.serach(Buff.class, buffId);
		if (buffData == null) {
			GameLog.error("add effect buff params error.");
			return;
		}
		String[] tParams = buffData.getBuffTarget().split(":");
		TargetType type = TargetType.search(tParams[1]);
		if (type == null){
			GameLog.error("buff固化表又错了 >>>>" + buffId);
			return;
		}
		FightBuff buff = null;
		switch (type){
			case C_A_RED_BDMG_ALL://提升部队减伤
			case C_A_RED_BDMG://提升部队减伤
			{
				buff = new FightBuff(FightBuffType.MITIGATION_EFF,value);
				break;
			}
			case T_A_IMP_SD://防御提升
			{
				buff = new FightBuff(FightBuffType.DEFENDE_EFF,value);
				break;
			}
			case C_A_RED_DEF://防御降低
			{
				buff = new FightBuff(FightBuffType.DEFENDE_EFF,-value);
				break;
			}
			case T_A_IMP_SA://攻击提升
			{
				buff = new FightBuff(FightBuffType.ATTACK_EFF,value);
				break;
			}
			case C_A_RED_ATK://攻击降低
			{
				buff = new FightBuff(FightBuffType.ATTACK_EFF,-value);
				break;
			}
			case T_A_IMP_DMG://提升伤害加成
			{
				buff = new FightBuff(FightBuffType.VULNERABILITY_EFF,value);
				break;
			}
			case C_A_RED_DMG://降低伤害加成
			{
				buff = new FightBuff(FightBuffType.VULNERABILITY_EFF,-value);
				break;
			}
			case T_A_IMP_AHP://max生命值提升
			{
				buff = new FightBuff(FightBuffType.HP_EFF,value);
				break;
			}
			case C_A_RED_HP://max生命值降低
			{
				buff = new FightBuff(FightBuffType.HP_EFF,-value);
				break;
			}
			case T_A_IMP_ICR://暴击提升
			{
				buff = new FightBuff(FightBuffType.CRIT_EFF,value);
				break;
			}
			case C_A_RED_CRT://暴击降低
			{
				buff = new FightBuff(FightBuffType.CRIT_EFF,-value);
				break;
			}
			case T_A_IMP_IAR://命中提升
			{
				buff = new FightBuff(FightBuffType.HIT_EFF,value);
				break;
			}
			case C_A_RED_ATR://命中提升
			{
				buff = new FightBuff(FightBuffType.HIT_EFF,-value);
				break;
			}
			case T_A_IMP_IER://闪避提升
			{
				buff = new FightBuff(FightBuffType.EVADE_EFF,value);
				break;
			}
			case C_A_RED_EDR://闪避值降低
			{
				buff = new FightBuff(FightBuffType.EVADE_EFF,-value);
				break;
			}
			case T_A_IMP_SS://机动力提升	
			{
				buff = new FightBuff(FightBuffType.SPEED_EFF,value);
				break;
			}
			case C_A_RED_MB://机动力提升	
			{
				buff = new FightBuff(FightBuffType.SPEED_EFF,-value);
				break;
			}
			default:{
				break;
			}
		}
		if (buff == null){
			GameLog.error("fight skill effect error : " + buffId);
			return;
		}
		buff.setLastRound(lastRound);
		buff.setBuffId(buffId);
		SkillInfo si = null;
		if (target == FightBuffTargetType.ONLY_MYSELF.ordinal()){
			si = new SkillInfo(key,myself,myself);
			myself.addFightBuff(buff,cover);
		}else if (target == FightBuffTargetType.ONLY_ENEMY.ordinal()){
			if (tar == null){
				GameLog.error("策划配置错误: " + key);
				return;
			}
			si = new SkillInfo(key,myself,tar);
			tar.addFightBuff(buff,cover);
		}else if (target == FightBuffTargetType.MY_TEAM_SAME_TYPE.ordinal()){
			si = new SkillInfo(key,myself,true);
			for (int i = 0 ; i < team.size() ; i++){
				FightTroops ft = team.get(i);
				if (myself.getAttribute().getArmyType() == ft.getAttribute().getArmyType()){
					ft.addFightBuff(buff.copy(),cover);
				}
			}
		}else if (target == FightBuffTargetType.EMENY_TEAM_SAME_TYPE.ordinal()){
			si = new SkillInfo(key,myself,true);
			for (int i = 0 ; i < emenys.size() ; i++){
				FightTroops ft = emenys.get(i);
				if (tar.getAttribute().getArmyType() == ft.getAttribute().getArmyType()){
					ft.addFightBuff(buff.copy(),cover);
				}
			}
		}else if (target == FightBuffTargetType.EMENY_TEAM_ALL.ordinal()){
			si = new SkillInfo(key,myself,true);
			for (int i = 0 ; i < emenys.size() ; i++){
				FightTroops ft = emenys.get(i);
				ft.addFightBuff(buff.copy(),cover);
			}
		}else if (target == FightBuffTargetType.MY_TEAM_ALL.ordinal()){
			si = new SkillInfo(key,myself,true);
			for (int i = 0 ; i < team.size() ; i++){
				FightTroops ft = team.get(i);
				ft.addFightBuff(buff.copy(),cover);
			}
		}
		if (si.getTargets().size() > 0 || si.isMore()){
			//没有目标的技能释放不了
			sis.add(si);
		}
	}
}
