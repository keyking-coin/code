package com.keyking.um.exp;

@SuppressWarnings("serial")
public class NotSenderClass extends Exception {
	
	public NotSenderClass(String className){
		super(className + "is not a senderClass");
	}
}
 
 
 
