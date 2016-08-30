package com.joymeng.slg.union.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

/**
 * @author houshanping
 *
 */
public class Alliancetechlevel implements DataKey{
	int id;
	String techid;
	String techName;
	int techLevel;
	int techExp;
	int restime;
	int starnumber;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTechid() {
		return techid;
	}

	public void setTechid(String techid) {
		this.techid = techid;
	}

	public String getTechName() {
		return techName;
	}

	public void setTechName(String techName) {
		this.techName = techName;
	}

	public int getTechLevel() {
		return techLevel;
	}

	public void setTechLevel(int techLevel) {
		this.techLevel = techLevel;
	}

	public int getTechExp() {
		return techExp;
	}

	public void setTechExp(int techExp) {
		this.techExp = techExp;
	}

	public int getRestime() {
		return restime;
	}

	public void setRestime(int restime) {
		this.restime = restime;
	}

	public int getStarnumber() {
		return starnumber;
	}

	public void setStarnumber(int starnumber) {
		this.starnumber = starnumber;
	}

	@Override
	public Object key() {
		return id;
	}
}
