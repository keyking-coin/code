package com.joymeng.slg.domain.object.army.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Skill implements DataKey {
	public String id;
	public String name;
	public String buffID;
	public double buffvalue;
	public int skilltype;
	public double triggerProb;
	public int target;
	public int lastround;
	public int buffcover;
	public String skilldesc;
	public String skillicon;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBuffID() {
		return buffID;
	}

	public void setBuffID(String buffID) {
		this.buffID = buffID;
	}

	public double getBuffvalue() {
		return buffvalue;
	}

	public void setBuffvalue(double buffvalue) {
		this.buffvalue = buffvalue;
	}

	public int getSkilltype() {
		return skilltype;
	}

	public void setSkilltype(int skilltype) {
		this.skilltype = skilltype;
	}

	public double getTriggerProb() {
		return triggerProb;
	}

	public void setTriggerProb(double triggerProb) {
		this.triggerProb = triggerProb;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getLastround() {
		return lastround;
	}

	public void setLastround(int lastround) {
		this.lastround = lastround;
	}

	public int getBuffcover() {
		return buffcover;
	}

	public void setBuffcover(int buffcover) {
		this.buffcover = buffcover;
	}

	public String getSkilldesc() {
		return skilldesc;
	}

	public void setSkilldesc(String skilldesc) {
		this.skilldesc = skilldesc;
	}

	public String getSkillicon() {
		return skillicon;
	}

	public void setSkillicon(String skillicon) {
		this.skillicon = skillicon;
	}

	@Override
	public Object key() {
		return id;
	}

}
