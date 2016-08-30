package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.object.build.impl.BuildComponentWall;
import com.joymeng.slg.domain.timer.TimerOver;

public class CityFireFinish implements TimerOver{
	BuildComponentWall com;
	public CityFireFinish(BuildComponentWall wall){
		com = wall;
	}
	@Override
	public void finish() {
		com.cancelFireState();
	}
}
