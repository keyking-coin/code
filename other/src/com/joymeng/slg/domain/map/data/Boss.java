package com.joymeng.slg.domain.map.data;

import java.util.List;
import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 大地图Boss固化表
 * @author tanyong
 *
 */
public class Boss implements DataKey {
	String id; 
	String name; 
	int level; 
	List<String> monster; 
	String droplist; 
	int rank; 
	int fightingcapacity; 
	int physicalStrength; 
	String rewardtype; 
	int reduDeathRate; 
	int refreshCd; 
	String killType; 
	int size; 
	List<String> porecondition;
	
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

	public List<String> getMonster() {
		return monster;
	}

	public void setMonster(List<String> monster) {
		this.monster = monster;
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

	public int getFightingcapacity() {
		return fightingcapacity;
	}

	public void setFightingcapacity(int fightingcapacity) {
		this.fightingcapacity = fightingcapacity;
	}

	public int getPhysicalStrength() {
		return physicalStrength;
	}

	public void setPhysicalStrength(int physicalStrength) {
		this.physicalStrength = physicalStrength;
	}

	public String getRewardtype() {
		return rewardtype;
	}

	public void setRewardtype(String rewardtype) {
		this.rewardtype = rewardtype;
	}

	public int getReduDeathRate() {
		return reduDeathRate;
	}

	public void setReduDeathRate(int reduDeathRate) {
		this.reduDeathRate = reduDeathRate;
	}

	public int getRefreshCd() {
		return refreshCd;
	}

	public void setRefreshCd(int refreshCd) {
		this.refreshCd = refreshCd;
	}

	public String getKillType() {
		return killType;
	}

	public void setKillType(String killType) {
		this.killType = killType;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<String> getPorecondition() {
		return porecondition;
	}

	public void setPorecondition(List<String> porecondition) {
		this.porecondition = porecondition;
	}

	@Override
	public Object key() {
		return id;
	}
}
