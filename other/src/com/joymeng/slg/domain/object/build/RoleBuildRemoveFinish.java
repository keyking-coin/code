package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.timer.TimerOver;

public class RoleBuildRemoveFinish implements TimerOver {
	RoleBuild build;
	public RoleBuildRemoveFinish(RoleBuild build){
		this.build = build;
	}
	
	@Override
	public void finish() {
		build.removeFinish();
	}
}
