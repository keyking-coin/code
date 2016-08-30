package com.joymeng.slg.domain.map.fight.obj.enumType;

public enum AttackResultType {
	MISS(1, "未命中"), HIT(2, "命中"), CRIT(3, "暴击"), GRAZ(4, "未破防");

	int key;
	String value;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private AttackResultType(int key, String value) {
		this.key = key;
		this.value = value;
	}
}
