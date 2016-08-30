package com.joymeng.slg.domain.object.build.queue;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerLast;

public class TimeQueue {
	
	TimerLast timer;//可使用时间
	
	long build = 0;//被那个建筑使用
	
	public TimerLast getTimer() {
		return timer;
	}
	
	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}
	
	public long getBuild() {
		return build;
	}
	
	public void setBuild(long build) {
		this.build = build;
	}
	
	public boolean empty(){
		return build <= 0;
	}
	
	public boolean couldUse(long time){
		if (!empty()){
			return false;
		}
		if (timer.getType() == TimerLastType.TIME_FOREVER){
			return true;
		}
		long now  = TimeUtils.nowLong() / 1000;
		long have = timer.getLast() + timer.getStart() - now;
		return have > time;
	}
}
