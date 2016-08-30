package com.joymeng.slg.domain.map.fight.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.object.army.data.Army;

public class FightTroopAttribute {
	String name ;
	String cName;
    int atk;
    int acc;
    int crit;
    int def;
    int hp;
    int bHp;//基础hp
    int cHp;
    int evade;
    int spd;
    int unitType;
    int armyType;
    byte boiType;//选择目标概率用
    int range;
    int space;
    List<Float> damageRates  =  new ArrayList<Float>();
    List<ArmyType> targets   = new ArrayList<ArmyType>();
    Map<Byte,Integer> radoms = new HashMap<Byte,Integer>();
    
	public void init(Army army) {
		name  = army.getId();
		cName = army.getBattleName() == null ? army.getArmyName() : army.getBattleName();
		atk = (int) army.getAttack();
		acc = (int) army.getAttackRate();
		crit = (int) army.getCritRate();
		def = (int) army.getDefense();
		hp = (int) army.getHitPoints();
		bHp = hp;
		evade = (int) army.getEvadeRate();
		spd = (int) army.getSpeed();
		unitType = army.getUnitType();
		armyType = army.getArmyType();
		boiType  = army.getBioType();
		space    = army.getSpace();
		List<String> tempDamage = army.getDamageRates();
		for (int i = 0 ; i < tempDamage.size() ; i++){
			String str = tempDamage.get(i);
			damageRates.add(Float.valueOf(str));
		}
		range = army.getAttackRange();
		List<String> tempAttackTypes = army.getAttackType();
		for (int i = 0 ; i < tempAttackTypes.size() ; i++){
			String str = tempAttackTypes.get(i);
			ArmyType at = ArmyType.search(Integer.parseInt(str));
			if (at != null){
				targets.add(at);
			}
		}
		List<String> rws = army.getRandomWeight();
		for (int i = 0 ; i < rws.size() ; i++){
			String rw = rws.get(i);
			String[] ss = rw.split(":");
			Byte bt = Byte.parseByte(ss[0]);
			Integer rate = Integer.parseInt(ss[1]);
			radoms.put(bt,rate);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getAcc() {
		return acc;
	}

	public void setAcc(int acc) {
		this.acc = acc;
	}

	public int getCrit() {
		return crit;
	}

	public void setCrit(int crit) {
		this.crit = crit;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	
	public int getbHp() {
		return bHp;
	}

	public void setbHp(int bHp) {
		this.bHp = bHp;
	}

	public int getEvade() {
		return evade;
	}

	public void setEvade(int evade) {
		this.evade = evade;
	}

	public int getSpd() {
		return spd;
	}

	public void setSpd(int spd) {
		this.spd = spd;
	}

	public int getRange() {
		return range;
	}

	public int getUnitType() {
		return unitType;
	}

	public void setUnitType(int unitType) {
		this.unitType = unitType;
	}

	public int getArmyType() {
		return armyType;
	}

	public void setArmyType(int armyType) {
		this.armyType = armyType;
	}

	public byte getBoiType() {
		return boiType;
	}

	public void setBoiType(byte boiType) {
		this.boiType = boiType;
	}

	public List<ArmyType> getTargets() {
		return targets;
	}

	public void setTargets(List<ArmyType> targets) {
		this.targets = targets;
	}

	public Map<Byte, Integer> getRadoms() {
		return radoms;
	}

	public void setRadoms(Map<Byte, Integer> radoms) {
		this.radoms = radoms;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public void setDamageRates(List<Float> damageRates) {
		this.damageRates = damageRates;
	}

	public List<Float> getDamageRates() {
		return damageRates;
	}

	
	public int getcHp() {
		return cHp;
	}

	public void setcHp(int cHp) {
		this.cHp = cHp;
	}

	public boolean checkAttack(FightTroops enemy) {
		ArmyType at = ArmyType.search(enemy.getAttribute().getArmyType());
		if (at != null){
			return targets.contains(at);
		}
		return false;
	}
}
