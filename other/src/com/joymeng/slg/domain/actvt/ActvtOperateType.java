package com.joymeng.slg.domain.actvt;

public enum ActvtOperateType
{
	NEW("new"), UPDATE("update"), DELETE("delete");
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public boolean equals(String type) {
		return name.equals(type);
	}
	
	private ActvtOperateType(String name) {
		this.name = name;
	}
}