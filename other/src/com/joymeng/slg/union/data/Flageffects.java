package com.joymeng.slg.union.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Flageffects implements DataKey{

	String id;
	String uID;
	String description;
	String foldername;
	String effectsName;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getuID() {
		return uID;
	}

	public void setuID(String uID) {
		this.uID = uID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFoldername() {
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public String getEffectsName() {
		return effectsName;
	}

	public void setEffectsName(String effectsName) {
		this.effectsName = effectsName;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
