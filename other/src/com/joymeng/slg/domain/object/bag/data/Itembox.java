package com.joymeng.slg.domain.object.bag.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Itembox implements DataKey{
	String id;
	byte boxtype;
	List<String> weightlist;
	List<String> itemlist;
	List<String> numberlist;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getBoxtype() {
		return boxtype;
	}

	public void setBoxtype(byte boxtype) {
		this.boxtype = boxtype;
	}

	public List<String> getWeightlist() {
		return weightlist;
	}

	public void setWeightlist(List<String> weightlist) {
		this.weightlist = weightlist;
	}

	public List<String> getItemlist() {
		return itemlist;
	}

	public void setItemlist(List<String> itemlist) {
		this.itemlist = itemlist;
	}

	public List<String> getNumberlist() {
		return numberlist;
	}

	public void setNumberlist(List<String> numberlist) {
		this.numberlist = numberlist;
	}

	@Override
	public Object key() {
		return id;
	}

}
