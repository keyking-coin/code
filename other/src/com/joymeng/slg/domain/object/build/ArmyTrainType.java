package com.joymeng.slg.domain.object.build;

public enum ArmyTrainType {
	ARMY_SOLDIER((byte)0),
	ARMY_TANK((byte)1),
	ARMY_PLANE((byte)2),
	ARMY_WARFACT((byte)3),
	ARMY_DEFENSE((byte)4),;
	
	private byte key;
	
	ArmyTrainType(byte state){
		this.setKey(state);
	}
	
	public static ArmyTrainType getState(int index){
		for(ArmyTrainType s : ArmyTrainType.values()){
			if(index == s.ordinal()){
				return s;
			}
		}
		return null;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}
}
