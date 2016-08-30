package com.joymeng.slg.domain.actvt.data;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_armyrebellion  implements DataKey 
{
	String id;
	int time;
	int failNum;
	int weekDay;
	int roleRankNum;
	int unionRankNum;
	String monsterPrefix;
	int monsterNum;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public int getFailNum() {
		return failNum;
	}

	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}

	public int getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(int weekDay) {
		this.weekDay = weekDay;
	}

	public int getRoleRankNum() {
		return roleRankNum;
	}

	public void setRoleRankNum(int roleRankNum) {
		this.roleRankNum = roleRankNum;
	}

	public int getUnionRankNum() {
		return unionRankNum;
	}

	public void setUnionRankNum(int unionRankNum) {
		this.unionRankNum = unionRankNum;
	}

	public String getMonsterPrefix() {
		return monsterPrefix;
	}

	public void setMonsterPrefix(String monsterPrefix) {
		this.monsterPrefix = monsterPrefix;
	}

	public int getMonsterNum() {
		return monsterNum;
	}

	public void setMonsterNum(int monsterNum) {
		this.monsterNum = monsterNum;
	}

	@Override
	public Object key() {
		return id;
	}
}
