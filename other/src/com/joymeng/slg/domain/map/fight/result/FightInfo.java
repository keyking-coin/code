package com.joymeng.slg.domain.map.fight.result;

import java.util.List;

import com.joymeng.slg.domain.map.fight.obj.enumType.AttackResultType;

public class FightInfo {
	UnitInfo attacker = new UnitInfo();//攻击者
	UnitInfo defender = new UnitInfo();//防御者
	AttackResultType art;//本次攻击的结果
	int die;//被攻击者被消灭的士兵数
	int dam;//发起攻击者造成伤害值
	int ln;//被攻击剩余数量
	int aDie = 0;//攻击方消耗，这个只在 陷阱攻击的时候才会是>0的
	List<SkillInfo> skills;//这一次攻击的技能
	
	public FightInfo() {
	}
	
	public void copy(AttackInfo info){
		attacker.init(info.attacker);
		defender.init(info.defender);
		art  = info.type;
		die  = info.casualty;
		dam  = info.damage;
		ln   = info.unitRemain;
		aDie = info.getAttDie();
		skills = info.getSkills();
	}
	
	public UnitInfo getAttacker() {
		return attacker;
	}

	public void setAttacker(UnitInfo attacker) {
		this.attacker = attacker;
	}

	public UnitInfo getDefender() {
		return defender;
	}

	public void setDefender(UnitInfo defender) {
		this.defender = defender;
	}
	
	public AttackResultType getArt() {
		return art;
	}
	
	public void setArt(AttackResultType art) {
		this.art = art;
	}
	
	public int getDie() {
		return die;
	}
	
	public void setDie(int die) {
		this.die = die;
	}
	
	public int getDam() {
		return dam;
	}
	
	public void setDam(int dam) {
		this.dam = dam;
	}
	
	public int getLn() {
		return ln;
	}
	
	public void setLn(int ln) {
		this.ln = ln;
	}
	
	public int getaDie() {
		return aDie;
	}
	
	public void setaDie(int aDie) {
		this.aDie = aDie;
	}
	
	public List<SkillInfo> getSkills() {
		return skills;
	}
	
	public void setSkills(List<SkillInfo> skills) {
		this.skills = skills;
	}
}
