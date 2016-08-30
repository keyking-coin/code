package com.joymeng.slg.domain.object.task;

import com.joymeng.slg.domain.event.DelayEffect;

public class TaskEventDelay implements DelayEffect{
	int count = 0;
	@Override
	public boolean dely(Object obj) {
//		if(count > 1){
//			return false;
//		}
//		count ++;
//		return true;
		return false;
	}

}
