package com.joymeng.slg.domain.object.effect;

import com.joymeng.slg.domain.object.effect.BuffTypeConst.ExtendsType;

public class ExtendInfo {
	ExtendsType type;
	int id;

	public ExtendInfo() {

	}

	public ExtendInfo(ExtendsType type, int Id) {
		this.type = type;
		this.id = Id;
	}

	public ExtendsType getType() {
		return type;
	}

	public void setType(ExtendsType type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() == obj.getClass()) {
			ExtendInfo val = (ExtendInfo) obj;
			if (type == val.getType() && id == val.getId()) {
				return true;
			}
		}
		return false;
	}
}
