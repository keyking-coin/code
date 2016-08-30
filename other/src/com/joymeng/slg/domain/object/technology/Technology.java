package com.joymeng.slg.domain.object.technology;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.technology.data.Tech;

public class Technology implements Instances{
	private String techId;
	private int level;
	private long uid;
	private int cityId;
	private String buff;
	
	
	public Technology(long uid, int cityId, String techId, int level){
		this.techId = techId;
		this.level = level;
		this.uid = uid;
		this.cityId = cityId;
	}

	public String getTechId() {
		return techId;
	}
	
	public String getTechName(){
		Tech tech = dataManager.serach(Tech.class, techId);
		if(tech == null){
			return null;
		}
		return tech.getName();
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getBuff() {
		return buff;
	}
	public void setBuff(String buff) {
		this.buff = buff;
	}
}
