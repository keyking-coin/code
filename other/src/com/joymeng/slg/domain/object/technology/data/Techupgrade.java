package com.joymeng.slg.domain.object.technology.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Techupgrade implements DataKey {
	String id;
	String techID;
	String name;
	String description;
	int level;
	String iconId;
	List<String> buffList;
	List<String> researchCostList;
	long researchTime;
	List<String> buffTarget;
	int attackForce;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getAttackForce() {
		return attackForce;
	}

	public void setAttackForce(int attackForce) {
		this.attackForce = attackForce;
	}

	public String getTechID() {
		return techID;
	}

	public void setTechID(String techID) {
		this.techID = techID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public long getResearchTime() {
		return researchTime;
	}

	public void setResearchTime(long researchTime) {
		this.researchTime = researchTime;
	}

	public List<String> getBuffList() {
		return buffList;
	}

	public void setBuffList(List<String> buffList) {
		this.buffList = buffList;
	}

	public List<String> getResearchCostList() {
		return researchCostList;
	}

	public void setResearchCostList(List<String> researchCostList) {
		this.researchCostList = researchCostList;
	}

	public List<String> getBuffTarget() {
		return buffTarget;
	}

	public void setBuffTarget(List<String> buffTarget) {
		this.buffTarget = buffTarget;
	}

	@Override
	public Object key() {
		return id;
	}

}
