package com.joymeng.slg.domain.event;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.object.IObject;

public abstract class AbstractGameEvent implements GameEvent {
	
	List<Object[]> cache;
	
	@Override
	public void tick() {
		if (cache != null){
			for (int i = 0 ; i < cache.size() ;){
				Object[] objs = cache.get(i);
				boolean addFlag = true;
				if (objs != null){
					IObject trigger = (IObject)objs[0];
					if (objs[2] != null && objs[2] instanceof DelayEffect){
						DelayEffect dely = (DelayEffect)objs[2];
						if (!dely.dely(this)){
							int len = 1 + (objs.length > 3 ? objs.length - 3 : 0);
							Object[] params = new Object[len];
							params[0] = objs[1];
							if (objs.length > 3){
								System.arraycopy(objs,3,params,1,params.length-1);
							}
							_handle(trigger,params);
							cache.remove(i);
							addFlag = false;
						}
					}
				}
				if (addFlag){
					i++;
				}
			}
		}
	}

	@Override
	public void handle(IObject trigger,Object[] params) {
		if (params.length > 1 && params[1] instanceof DelayEffect){
			DelayEffect dely = (DelayEffect)params[1];
			if (dely.dely(this)){
				if (cache == null){
					cache = new ArrayList<Object[]>();
				}
				int len  = params.length + 1;
				Object[] temps = new Object[len];
				temps[0] = trigger;
				System.arraycopy(params,0,temps,1,params.length);
				cache.add(temps);
				return;
			} else {
				for (int i = 1 ; i < params.length ; i++){
					if (i < params.length-1){
						params[i] = params[i+1];
					}
				}
			}
		}
		_handle(trigger,params);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Object obj){
		return obj == null ? null : (T)obj;
	}
	
	public abstract void _handle(IObject trigger,Object[] params);
}
