package com.joymeng.slg.domain.object.army;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.army.data.Army;

public class ArmyInfo implements Instances, Comparable<ArmyInfo>{
	int armyNum;
	String armyId;
	byte state;//0-驻守，1-出征，2-受伤, 3-集结, 4-被集结, 99-删除中
	long uid;
	int cityId;
	Army armyBase;
	
	public ArmyInfo(String armyId, int num, byte state){
		this.armyId = armyId;
		this.armyNum = num;
		this.state = state;
	}
	
	public void init(long uid, int cityID){
		this.uid = uid;
		this.cityId = cityID;
	}
	
	public String getArmyId() {
		return armyId;
	}

	public void setArmyId(String armyId) {
		this.armyId = armyId;
	}
	
	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public void setArmyNum(int armyNum) {
		this.armyNum = armyNum;
	}

	public int getArmyNum() {
		return armyNum;
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	
	public Army getArmyBase() {
		return armyBase;
	}

	public void setArmyBase(Army armyBase) {
		this.armyBase = armyBase;
	}
	
	public static float getMinSpeed(){
		return 0;
	}

	@Override
	public int compareTo(ArmyInfo o) {
		return Float.compare(o.getArmyBase().getFightingForce(), this.armyBase.getFightingForce());
	}
}
