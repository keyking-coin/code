package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.timer.TimerOver;

public class RoleBuildLevelUpFinish implements TimerOver {
	
	RoleBuild build;
	
	public RoleBuildLevelUpFinish(RoleBuild build){
		this.build = build;
	}
	
	@Override
	public void finish() {
		build.leveupFinish(true);
	}
}
