package com.joymeng.slg.domain.map.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;
/**
 * 资源田产出固化表
 * @author tanyong
 *
 */
public class Resourcefield implements DataKey {
	public String id;
	public String type;
	public String name;
	public int level;
	public int total;
	public String description;
	public String icon;
	public String monster;
	public String droplist;
	public int freetime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getMonster() {
		return monster;
	}

	public void setMonster(String monster) {
		this.monster = monster;
	}

	public String getDroplist() {
		return droplist;
	}

	public void setDroplist(String droplist) {
		this.droplist = droplist;
	}

	public int getFreetime() {
		return freetime;
	}

	public void setFreetime(int freetime) {
		this.freetime = freetime;
	}

	@Override
	public Object key() {
		return id;
	}
}
