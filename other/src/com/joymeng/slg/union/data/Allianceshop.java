package com.joymeng.slg.union.data;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Allianceshop implements DataKey {

	String id;
	String itemName;
	int persContr;
	int allianceContr;
	int num;
	int allianceLv;
	
	
	public int getAllianceLv() {
		return allianceLv;
	}

	public void setAllianceLv(int allianceLv) {
		this.allianceLv = allianceLv;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getPersContr() {
		return persContr;
	}

	public void setPersContr(int persContr) {
		this.persContr = persContr;
	}

	public int getAllianceContr() {
		return allianceContr;
	}

	public void setAllianceContr(int allianceContr) {
		this.allianceContr = allianceContr;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
