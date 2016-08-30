package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

/**
 * 怪物数据固化表
 * @author tanyong
 *
 */
public class Monster implements DataKey {
	public String id;
	public String name;
	public String animation;
	public int level;
	public int exp;
	public List<String> troops;
	public String droplist;
	public int physical;

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

	public String getAnimation() {
		return animation;
	}

	public void setAnimation(String animation) {
		this.animation = animation;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public List<String> getTroops() {
		return troops;
	}

	public void setTroops(List<String> troops) {
		this.troops = troops;
	}

	public String getDroplist() {
		return droplist;
	}

	public void setDroplist(String droplist) {
		this.droplist = droplist;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	@Override
	public Object key() {
		return id;
	}
}
