package com.joymeng.slg.union.impl;

import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.union.UnionBody;

public class UnionTechFinish implements TimerOver {

	String techId;
	UnionBody unionBody = new UnionBody();

	public UnionTechFinish(UnionBody unionBody, String techId) {
		this.unionBody = unionBody;
		this.techId = techId;
	}

	@Override
	public void finish() {
		unionBody.techTechFinish(techId);
	}

}
