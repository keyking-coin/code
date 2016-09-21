package com.joymeng.slg.domain.object.army;

public enum ArmyState {
	// ArmyState
	ARMY_IN_NORMAL((byte)0,"正常状态"),
	ARMY_OUT_BATTLE((byte)1,"出征状态"), 
	ARMY_IN_HOSPITAL((byte)2,"伤兵"), 
	ARMY_IN_ALLY((byte)3,"集结状态"), 
	ARMY_OUT_ALLY((byte)4,"名字重复"),
	ARMY_DIED((byte)5,"战死"),
	ARMY_PROMOT((byte)88,"晋级"),
	ARMY_REMOVE((byte)99,"删除")
	;
	private byte value;
	private String name;
	ArmyState(byte value, String name) {
		this.value = value;
		this.name = name;
	}
	public byte getValue() {
		return value;
	}
	public void setValue(byte value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
