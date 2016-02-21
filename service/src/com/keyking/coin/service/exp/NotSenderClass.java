package com.keyking.coin.service.exp;

@SuppressWarnings("serial")
public class NotSenderClass extends Exception {
	
	public NotSenderClass(String className){
		super(className + "is not a senderClass");
	}
}
 
 
 
