package com.joymeng.slg.domain.object.build.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Baseland implements DataKey{
	
	String id;
	int unlockCondition;
	int unlockPrice;
	boolean initUnlock;
	List<String> containSlots;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getUnlockCondition() {
		return unlockCondition;
	}

	public void setUnlockCondition(int unlockCondition) {
		this.unlockCondition = unlockCondition;
	}

	public int getUnlockPrice() {
		return unlockPrice;
	}

	public void setUnlockPrice(int unlockPrice) {
		this.unlockPrice = unlockPrice;
	}

	public boolean isInitUnlock() {
		return initUnlock;
	}

	public void setInitUnlock(boolean initUnlock) {
		this.initUnlock = initUnlock;
	}

	public List<String> getContainSlots() {
		return containSlots;
	}

	public void setContainSlots(List<String> containSlots) {
		this.containSlots = containSlots;
	}

	@Override
	public Object key() {
		return id;
	}

	@Override
	public String toString() {
		return id + "," + unlockCondition;
	}
}
