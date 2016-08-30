package com.joymeng.slg.domain.object.army;

import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.army.data.Army;

public class DefenseArmyInfo extends ArmyInfo{
	private int hp;
	private long buildId = 0;
	
	public DefenseArmyInfo(String armyId, int num, byte state, int hp) {
		super(armyId, num, state);
		this.hp = hp;
	}
	
	public static DefenseArmyInfo create(String armyId, int num, int hp){
		DefenseArmyInfo armyInfo = new DefenseArmyInfo(armyId, num, ArmyState.ARMY_IN_NORMAL.getValue(), hp);
		Army armyBase = dataManager.serach(Army.class, armyId);
		if(armyBase == null){
			GameLog.error("cannot create army,no army base information where armyId="+armyId);
			return null;
		}
		armyInfo.setArmyBase(armyBase);
		return armyInfo;
	}
	
	public int getHp() {
		return hp;
	}
	
	public void setHp(int hp) {
		this.hp = hp;
	}

	public long getBuildId() {
		return buildId;
	}

	public void setBuildId(long buildId) {
		this.buildId = buildId;
	}
}
