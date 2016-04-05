package com.keyking.admin.net.exp;

@SuppressWarnings("serial")
public class TransformDataException extends Exception {
	public TransformDataException(Object obj){
		super(obj.getClass().getSimpleName() + "is not a senderClass");
	}
}
