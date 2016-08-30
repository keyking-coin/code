package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.timer.TimerOver;

public class CityTradeCDFinish implements TimerOver{
	RoleBuild build;
	public CityTradeCDFinish(RoleBuild build){
		this.build = build;
	}
	@Override
	public void finish() {
		build.tradeCDFinish();
	}

}
