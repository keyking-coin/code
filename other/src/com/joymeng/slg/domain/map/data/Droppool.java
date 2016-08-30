package com.joymeng.slg.domain.map.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

/**
 * 掉落固化表
 * @author tanyong
 *
 */
public class Droppool implements DataKey {
	String id;
	List<String> numberWeight; 
	List<String> itemWeight; 
	List<String> firstkillreward; 
	List<String> fixedreward; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getNumberWeight() {
		return numberWeight;
	}

	public void setNumberWeight(List<String> numberWeight) {
		this.numberWeight = numberWeight;
	}

	public List<String> getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(List<String> itemWeight) {
		this.itemWeight = itemWeight;
	}

	public List<String> getFirstkillreward() {
		return firstkillreward;
	}

	public void setFirstkillreward(List<String> firstkillreward) {
		this.firstkillreward = firstkillreward;
	}

	public List<String> getFixedreward() {
		return fixedreward;
	}

	public void setFixedreward(List<String> fixedreward) {
		this.fixedreward = fixedreward;
	}

	@Override
	public Object key() {
		return id;
	}

}
