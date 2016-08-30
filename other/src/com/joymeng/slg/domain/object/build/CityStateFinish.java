package com.joymeng.slg.domain.object.build;

import com.joymeng.slg.domain.object.build.CityState;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;

public class CityStateFinish implements TimerOver{
	CityState cityState;
	TimerLastType type;
	public CityStateFinish(CityState cityState, TimerLastType type){
		this.cityState = cityState;
		this.type = type;
	}
	@Override
	public void finish() {
		cityState.cityStateChange(type, false);
	}

}
