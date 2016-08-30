package com.joymeng.slg.domain.object.skill;

public enum TechTreeType {
	TECH_TREE("techTree"),
	SKILL_TREE("skillTree"),
	ALLI_TREE("allianceTech"),
	;
	private String name;
	private TechTreeType(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
