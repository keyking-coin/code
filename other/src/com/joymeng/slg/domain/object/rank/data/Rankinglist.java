package com.joymeng.slg.domain.object.rank.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Rankinglist implements DataKey {
	public int id;
	public int type;
	public String rankName;
	public String title;
	public int listLengthDefault;
	public int listLengthMax;
	public String typeName;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getListLengthDefault() {
		return listLengthDefault;
	}

	public void setListLengthDefault(int listLengthDefault) {
		this.listLengthDefault = listLengthDefault;
	}

	public int getListLengthMax() {
		return listLengthMax;
	}

	public void setListLengthMax(int listLengthMax) {
		this.listLengthMax = listLengthMax;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
