package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.timer.TimerOver;

public class RoleBuildCreateFinish implements TimerOver {
	
	RoleBuild build;
	
	public RoleBuildCreateFinish(RoleBuild build){
		this.build = build;
	}
	
	@Override
	public void finish() {
		build.createFinish(false);
	}

}
