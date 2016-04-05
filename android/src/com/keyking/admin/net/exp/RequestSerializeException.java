package com.keyking.admin.net.exp;

@SuppressWarnings("serial")
public class RequestSerializeException extends Exception {
	public RequestSerializeException(Object obj){
		super(obj.getClass().getSimpleName() + "is not a SerializeEntity class");
	}
}
