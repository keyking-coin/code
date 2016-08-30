package com.joymeng.slg.domain.object.role.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Userlevel implements DataKey,Comparable<Userlevel>{
	String id;
	int level;
	int experience;
	short stamina;
	int equipmentHoles;
	int skillpoints;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public short getStamina() {
		return stamina;
	}

	public void setStamina(short stamina) {
		this.stamina = stamina;
	}

	public int getEquipmentHoles() {
		return equipmentHoles;
	}

	public void setEquipmentHoles(int equipmentHoles) {
		this.equipmentHoles = equipmentHoles;
	}

	public int getSkillpoints() {
		return skillpoints;
	}

	public void setSkillpoints(int skillpoints) {
		this.skillpoints = skillpoints;
	}

	@Override
	public Object key() {
		return level;
	}

	@Override
	public int compareTo(Userlevel o) {
		return o.level - level;
	}
}
