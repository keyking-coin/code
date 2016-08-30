package com.joymeng.slg.domain.object.build;

public enum BuildState {
	BUILD_NORMAL((byte)0, "正常状态"),
	BUILD_CREATE((byte)1, "建造状态"),
	BUILD_LEVELUP((byte)2, "升级状态"),
	BUILD_FREE((byte)3, "免费提示状态"),
	BUILD_TRAINARMY((byte)4, "训练状态"),
	BUILD_COLLECT((byte)5, "资源可收取状态"),
	BUILD_CUREARMY((byte)6, "治疗状态"),
	BUILD_RESMAX((byte)7, "资源满状态"),
	BUILD_RESEARCH((byte)8, "研究状态"),
	;
	private byte value;
	private String name;
	BuildState(byte value, String name) {
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
