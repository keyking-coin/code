package com.joymeng.slg.union.impl;

public class DonateButton {
	int donateId;
	int weightingfactor;
	int unusedNum;

	public DonateButton() {
		
	}

	public DonateButton(int donateId, int weightingfactor, int unusedNum) {
		this.donateId = donateId;
		this.weightingfactor = weightingfactor;
		this.unusedNum = unusedNum;
	}
	
	public int getDonateId() {
		return donateId;
	}

	public void setDonateId(int donateId) {
		this.donateId = donateId;
	}

	public int getWeightingfactor() {
		return weightingfactor;
	}

	public void setWeightingfactor(int weightingfactor) {
		this.weightingfactor = weightingfactor;
	}

	public int getUnusedNum() {
		return unusedNum;
	}

	public void setUnusedNum(int unusedNum) {
		this.unusedNum = unusedNum;
	}

}
