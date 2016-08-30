package com.joymeng.slg.domain.map.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 大地图NPC固化表
 * @author tanyong
 *
 */
public class Npc implements DataKey {
	String id; 
	String name; 
	int level; 
	String droplist; 
	int rank;
	String rewardtype; 
	
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDroplist() {
		return droplist;
	}

	public void setDroplist(String droplist) {
		this.droplist = droplist;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getRewardtype() {
		return rewardtype;
	}

	public void setRewardtype(String rewardtype) {
		this.rewardtype = rewardtype;
	}

	@Override
	public Object key() {
		return id;
	}
}
