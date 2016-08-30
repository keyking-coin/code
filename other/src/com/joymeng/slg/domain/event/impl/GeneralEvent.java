package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;

public class GeneralEvent extends AbstractGameEvent {
	long pretime;
	@Override
	public void _handle(IObject trigger, Object[] params) {
		short code = get(params[0]);
		switch(code){
			case GameEvent.ADD_LIST:{
				world.addObject(trigger.getClass(),trigger);
				break;
			}
			case GameEvent.REMOVE_LIST:{
				world.remove(trigger);
				break;
			}
			case GameEvent.SAVE_MYSELF:{
				taskPool.saveThread.addSaveData(trigger);
				break;
			}
		}
	}
	
	public long getPretime() {
		return pretime;
	}
	
	public void setPretime(long pretime) {
		this.pretime = pretime;
	}
}
