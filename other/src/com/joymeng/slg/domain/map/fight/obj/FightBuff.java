package com.joymeng.slg.domain.map.fight.obj;

import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffType;


public class FightBuff {
	String skillId;
	String buffId;
    FightBuffType type;
    float value;
    int lastRound = -1;//持续回合数：-1影响全局,0移除
    
    public FightBuff(FightBuffType type,float value) {
    	this.type   = type;
    	this.value  = value;
	}

	public FightBuffType getType() {
		return type;
	}

	public void setType(FightBuffType type) {
		this.type = type;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public int getLastRound() {
		return lastRound;
	}

	public void setLastRound(int lastRound) {
		this.lastRound = lastRound;
	}

	
	public String getBuffId() {
		return buffId;
	}

	public void setBuffId(String buffId) {
		this.buffId = buffId;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public FightBuff copy() {
		FightBuff buff = new FightBuff(type,value);
		buff.lastRound = lastRound;
		buff.buffId = buffId;
		buff.skillId = skillId;
		return buff;
	}

	public void addValue(float f) {
		value += f;
	}
}
