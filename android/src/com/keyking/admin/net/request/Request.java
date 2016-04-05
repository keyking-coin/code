package com.keyking.admin.net.request;

import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.ParametersEntity;

public class Request {
	
	public static final String LOGIC_LOGIN = "";
	
	String logicName;
	
	ParametersEntity params = new ParametersEntity();
	
	public Request(String logicName){
		this.logicName = logicName;
	}
	
	public String getLogicName() {
		return logicName;
	}

	public void serialize(DataBuffer buffer) throws Exception {
		buffer.skip(4);
		buffer.putUTF(logicName);
		params.serialize(buffer);
		buffer.putInt(0,buffer.position()-4);
	}
	
	public void add(Object obj){
		params.add(obj);
	}
	
	public void add(int index,Object obj){
		params.add(index,obj);
	}
}
