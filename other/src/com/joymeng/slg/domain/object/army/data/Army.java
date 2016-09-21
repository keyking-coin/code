package com.joymeng.slg.domain.object.army.data;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Army implements DataKey, Comparable<Army> {
	String id;
	String armyName;
	String battleName;
	String armyDescription;
	byte armyType;
	byte soldiersType;
	byte bioType;
	byte starLevel;
	byte unitType;
	String location;
	List<String> attackType = new ArrayList<String>();
	byte armorType;
	int damageType;
	byte weaponType;
	float fightingForce;
	float attack;
	float defense;
	float hitPoints;
	float speed;
	float weight;
	float attackRate;
	float evadeRate;
	float critRate;
	int attackRange;
	float harvestSpeed;
	List<String> riceCost;
	List<String> unlockLimitation;
	String unlockDescription;
	String iconId;
	List<String> trainCostList;
	int trainTime;
	List<String> skill;
	int showNum;
	String bottomcolor;
	String bordercolor;
	int space;
	String sort;
	float numberPar;
	List<String> damageRates;
	List<String> randomWeight;
	int armycamp;
	//["1","2"]，前面是个人贡献度，后面是联盟积分
	List<String> alliancescore;
	public Army() {
		// TODO Auto-generated constructor stub
	};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArmyName() {
		return armyName;
	}

	public void setArmyName(String armyName) {
		this.armyName = armyName;
	}

	public String getBattleName() {
		return battleName;
	}

	public void setBattleName(String battleName) {
		this.battleName = battleName;
	}

	public byte getArmyType() {
		return armyType;
	}

	public void setArmyType(byte armyType) {
		this.armyType = armyType;
	}

	public byte getSoldiersType() {
		return soldiersType;
	}

	public void setSoldiersType(byte soldiersType) {
		this.soldiersType = soldiersType;
	}

	public byte getBioType() {
		return bioType;
	}

	public void setBioType(byte bioType) {
		this.bioType = bioType;
	}

	public String getArmyDescription() {
		return armyDescription;
	}

	public void setArmyDescription(String armyDescription) {
		this.armyDescription = armyDescription;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getShowNum() {
		return showNum;
	}

	public void setShowNum(int showNum) {
		this.showNum = showNum;
	}

	public String getBottomcolor() {
		return bottomcolor;
	}

	public void setBottomcolor(String bottomcolor) {
		this.bottomcolor = bottomcolor;
	}

	public String getBordercolor() {
		return bordercolor;
	}

	public void setBordercolor(String bordercolor) {
		this.bordercolor = bordercolor;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public float getFightingForce() {
		return fightingForce;
	}

	public void setFightingForce(float fightingForce) {
		this.fightingForce = fightingForce;
	}

	public void setFightingForce(int fightingForce) {
		this.fightingForce = fightingForce;
	}

	public float getAttack() {
		return attack;
	}

	public void setAttack(float attack) {
		this.attack = attack;
	}

	public float getDefense() {
		return defense;
	}

	public void setDefense(float defense) {
		this.defense = defense;
	}

	public float getHitPoints() {
		return hitPoints;
	}

	public void setHitPoints(float hitPoints) {
		this.hitPoints = hitPoints;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public List<String> getRiceCost() {
		return riceCost;
	}

	public void setRiceCost(List<String> riceCost) {
		this.riceCost = riceCost;
	}

	public List<String> getUnlockLimitation() {
		return unlockLimitation;
	}

	public void setUnlockLimitation(List<String> unlockLimitation) {
		this.unlockLimitation = unlockLimitation;
	}

	public String getUnlockDescription() {
		return unlockDescription;
	}

	public void setUnlockDescription(String unlockDescription) {
		this.unlockDescription = unlockDescription;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public List<String> getTrainCostList() {
		return trainCostList;
	}

	public void setTrainCostList(List<String> trainCostList) {
		this.trainCostList = trainCostList;
	}

	public byte getUnitType() {
		return unitType;
	}

	public void setUnitType(byte unitType) {
		this.unitType = unitType;
	}

	public List<String> getAttackType() {
		return attackType;
	}

	public void setAttackType(List<String> attackType) {
		this.attackType = attackType;
	}

	public byte getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(byte weaponType) {
		this.weaponType = weaponType;
	}

	public int getDamageType() {
		return damageType;
	}

	public void setDamageType(int damageType) {
		this.damageType = damageType;
	}

	public byte getArmorType() {
		return armorType;
	}

	public void setArmorType(byte armorType) {
		this.armorType = armorType;
	}

	public float getHarvestSpeed() {
		return harvestSpeed;
	}

	public void setHarvestSpeed(float harvestSpeed) {
		this.harvestSpeed = harvestSpeed;
	}

	public int getTrainTime() {
		return trainTime;
	}

	public void setTrainTime(int trainTime) {
		this.trainTime = trainTime;
	}

	public List<String> getSkill() {
		return skill;
	}

	public void setSkill(List<String> skill) {
		this.skill = skill;
	}

	public byte getStarLevel() {
		return starLevel;
	}

	public void setStarLevel(byte starLevel) {
		this.starLevel = starLevel;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}

	public float getAttackRate() {
		return attackRate;
	}

	public void setAttackRate(float attackRate) {
		this.attackRate = attackRate;
	}

	public float getEvadeRate() {
		return evadeRate;
	}

	public void setEvadeRate(float evadeRate) {
		this.evadeRate = evadeRate;
	}

	public float getCritRate() {
		return critRate;
	}

	public void setCritRate(float critRate) {
		this.critRate = critRate;
	}

	public float getNumberPar() {
		return numberPar;
	}

	public void setNumberPar(float numberPar) {
		this.numberPar = numberPar;
	}

	public List<String> getDamageRates() {
		return damageRates;
	}

	public void setDamageRates(List<String> damageRates) {
		this.damageRates = damageRates;
	}

	public List<String> getRandomWeight() {
		return randomWeight;
	}

	public void setRandomWeight(List<String> randomWeight) {
		this.randomWeight = randomWeight;
	}

	public int getArmycamp() {
		return armycamp;
	}

	public void setArmycamp(int armycamp) {
		this.armycamp = armycamp;
	}

	public List<String> getAlliancescore() {
		return alliancescore;
	}

	public void setAlliancescore(List<String> alliancescore) {
		this.alliancescore = alliancescore;
	}

	@Override
	public Object key() {
		return id;
	}

	@Override
	public int compareTo(Army arg0) {
		return Float.compare(this.fightingForce, arg0.getFightingForce());
	}
}
