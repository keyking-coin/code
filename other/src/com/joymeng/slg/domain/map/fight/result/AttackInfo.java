package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.AttackResultType;

public class AttackInfo {
	int round;
	FightTroops attacker;
	FightTroops defender;
	AttackResultType type = AttackResultType.HIT;
	int casualty;
	int damage;
	int unitRemain;
	List<SkillInfo> skills = new ArrayList<SkillInfo>();
	
	public AttackInfo(FightTroops attacker,FightTroops defender){
		this.attacker = attacker;
		this.defender = defender;
	}
	
	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public void setType(AttackResultType type) {
		this.type = type;
	}

	public int getCasualty() {
		return casualty;
	}

	public void setCasualty(String key,int casualty) {
		this.casualty = casualty;
		attacker.addKill(key,casualty);
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getUnitRemain() {
		return unitRemain;
	}

	public void setUnitRemain(int unitRemain) {
		this.unitRemain = unitRemain;
	}
	
	public List<SkillInfo> getSkills() {
		return skills;
	}

	public void setSkills(List<SkillInfo> skills) {
		this.skills = skills;
	}

	public FightTroops getAttacker() {
		return attacker;
	}

	public void setAttacker(FightTroops attacker) {
		this.attacker = attacker;
	}

	public FightTroops getDefender() {
		return defender;
	}

	public void setDefender(FightTroops defender) {
		this.defender = defender;
	}

	public int getAttDie(){
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(attacker.getSide().toString() + "_" + attacker.getAttribute().getcName());
		sb.append("(" + attacker.getPos().getRow() + "," + attacker.getPos().getCol()+")");
		sb.append(" 攻击 ");
		sb.append(defender.getSide().toString() + "_" +  defender.getAttribute().getcName());
		sb.append("("+ defender.getPos().getRow()+","+defender.getPos().getCol()+")");
		sb.append("攻击结果:[" + type.getValue());
		sb.append("&nbsp;&nbsp;造成:" + damage + "伤害");
		sb.append("&nbsp;&nbsp;击杀:" + casualty + "单位");
		sb.append("&nbsp;&nbsp;剩余:" + unitRemain + "单位");
		return sb.toString();
    }
}
