package com.joymeng.slg.exp;

import com.joymeng.slg.domain.object.IObject;

@SuppressWarnings("serial")
public class GameEventParamError extends Exception {
	
	public GameEventParamError(IObject obj){
		super(obj + " do event parameter must have event code");
	}
}
