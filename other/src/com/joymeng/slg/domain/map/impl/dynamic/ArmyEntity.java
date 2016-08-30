package com.joymeng.slg.domain.map.impl.dynamic;

import com.joymeng.slg.domain.object.army.data.Army;

public class ArmyEntity {
	int id = -1;//战斗编号，临时属性
	String key;
	int sane;
	int injurie;
	int died;
	String pos;
	Army temp;
	
	public ArmyEntity() {
		
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public int getSane() {
		return sane;
	}
	
	public void setSane(int sane) {
		this.sane = sane;
	}
	
	public int getInjurie() {
		return injurie;
	}
	
	public void setInjurie(int injurie) {
		this.injurie = injurie;
	}
	
	public int getDied() {
		return died;
	}
	
	public void setDied(int died) {
		this.died = died;
	}
	
	public String getPos() {
		return pos;
	}
	
	public void setPos(String pos) {
		this.pos = pos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Army getTemp() {
		return temp;
	}

	public void setTemp(Army temp) {
		this.temp = temp;
	}

	public void reset(){
		sane    = sane + injurie + died;
		injurie = 0;
		died    = 0;
	}

	public void copy(ArmyEntity ae) {
		id = ae.id;
		key = ae.key;
		sane = ae.sane;
		injurie = ae.injurie;
		died = ae.died;
		pos = ae.pos;
	}
	
	
}
