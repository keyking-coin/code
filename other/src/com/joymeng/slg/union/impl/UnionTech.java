package com.joymeng.slg.union.impl;

public class UnionTech {
	String techId;	//科技Id
	int techlevel;	//科技的等级
	int currentExp;	//当前经验
	
	public UnionTech() {
	}
	public String getTechId() {
		return techId;
	}
	public void setTechId(String techId) {
		this.techId = techId;
	}
	public int getTechlevel() {
		return techlevel;
	}
	public void setTechlevel(int techlevel) {
		this.techlevel = techlevel;
	}
	public int getCurrentExp() {
		return currentExp;
	}
	public void setCurrentExp(int currentExp) {
		this.currentExp = currentExp;
	}
	
}
