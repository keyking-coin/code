package com.joymeng.slg.union.impl;

import java.util.ArrayList;
import java.util.List;

public class UnionMemberTechProgress {
	String techId;
	List<DonateButton> techProgresses = new ArrayList<DonateButton>();

	public UnionMemberTechProgress() {

	}

	public UnionMemberTechProgress(String techId, List<DonateButton> techProgresses) {
		this.techId = techId;
		this.techProgresses = techProgresses;
	}

	public String getTechId() {
		return techId;
	}

	public void setTechId(String techId) {
		this.techId = techId;
	}

	public List<DonateButton> getTechProgresses() {
		return techProgresses;
	}

	public void setTechProgresses(List<DonateButton> techProgresses) {
		this.techProgresses = techProgresses;
	}

}
