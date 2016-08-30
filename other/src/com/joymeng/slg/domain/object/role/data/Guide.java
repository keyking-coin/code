package com.joymeng.slg.domain.object.role.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Guide implements DataKey{
	String Id;
	String guideTrigger;
	String moduleID;
	String description;
	String spawnsFrom;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getGuideTrigger() {
		return guideTrigger;
	}

	public void setGuideTrigger(String guideTrigger) {
		this.guideTrigger = guideTrigger;
	}

	public String getModuleID() {
		return moduleID;
	}

	public void setModuleID(String moduleID) {
		this.moduleID = moduleID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSpawnsFrom() {
		return spawnsFrom;
	}

	public void setSpawnsFrom(String spawnsFrom) {
		this.spawnsFrom = spawnsFrom;
	}

	@Override
	public Object key() {
		return Id;
	}

}
