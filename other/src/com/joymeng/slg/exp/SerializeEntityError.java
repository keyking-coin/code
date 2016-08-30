package com.joymeng.slg.exp;

@SuppressWarnings("serial")
public class SerializeEntityError extends Exception {
	
	public SerializeEntityError(Class<?> clazz){
		super(clazz.getSimpleName() + "is not a senderClass");
	}
}
