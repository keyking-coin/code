package com.joymeng.slg.domain.object.army;

import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.timer.TimerLast;

public class ArmyInfo implements Instances, Comparable<ArmyInfo>{
	int armyNum;
	String armyId;
	byte state;// 0-驻守，1-出征，2-受伤, 3-集结, 4-被集结, 88-晋级中, 99-删除中
	long uid;
	int cityId;
	Army armyBase;
	TimerLast time;
	String promotId; //晋级后的兵种Id
	
	Map<String,Integer> myKills;//我击杀的对象
	
	public ArmyInfo(String armyId, int num, byte state) {
		this.armyId = armyId;
		this.armyNum = num;
		this.state = state;
	}
	
	public ArmyInfo(String armyId, int num, byte state, TimerLast time, String promotId) {
		this.armyId = armyId;
		this.armyNum = num;
		this.state = state;
		this.time = time;
		this.promotId = promotId;
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
	
	public TimerLast getTime() {
		return time;
	}

	public void setTime(TimerLast time) {
		this.time = time;
	}

	public String getPromotId() {
		return promotId;
	}

	public void setPromotId(String promotId) {
		this.promotId = promotId;
	}
	

	public Map<String, Integer> getMyKills() {
		return myKills;
	}

	public void setMyKills(Map<String, Integer> myKills) {
		this.myKills = myKills;
	}

	@Override
	public int compareTo(ArmyInfo o) {
		return Float.compare(o.getArmyBase().getFightingForce(), this.armyBase.getFightingForce());
	}
}
