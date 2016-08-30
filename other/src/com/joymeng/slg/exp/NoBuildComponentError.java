package com.joymeng.slg.exp;

@SuppressWarnings("serial")
public class NoBuildComponentError extends Exception {
	public NoBuildComponentError(String componentKey){
		super("can not find BuildComponent where buildId is " + componentKey);
	}
}
