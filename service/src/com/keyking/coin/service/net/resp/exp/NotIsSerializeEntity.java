package com.keyking.coin.service.net.resp.exp;

@SuppressWarnings("serial")
public class NotIsSerializeEntity extends Exception {
	
	public NotIsSerializeEntity(Class<?> clazz){
		super(clazz.getSimpleName() + "is not a senderClass");
	}
	
}
