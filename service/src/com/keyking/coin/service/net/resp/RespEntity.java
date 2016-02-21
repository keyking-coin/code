package com.keyking.coin.service.net.resp;

import com.keyking.coin.service.net.buffer.DataBuffer;

public abstract class RespEntity {
	
	
	public static final int RESP_RESULT_SUCC = 0;
	
	public static final int RESP_RESULT_FAIL = 1;
	
	int result = RESP_RESULT_FAIL;
	
	String logicName;
	
	String error = "";
	
	String succes = null;
	
	public RespEntity(String logicName){
		this.logicName = logicName;
	}
	
	public void serialize(DataBuffer buffer){
		buffer.skip(4);
		buffer.putUTF(logicName);
		buffer.putInt(result);
		if (result == RESP_RESULT_SUCC){
			_serialize_ok(buffer);
		}else{
			_serialize_fail(buffer);
		}
		buffer.putInt(0,buffer.position()-4);
	}
	
	public abstract void _serialize_ok(DataBuffer buffer);
	
	public void _serialize_fail(DataBuffer buffer){
		buffer.putUTF(error);
	}
	
	public int getResult() {
		return result;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		result = RESP_RESULT_FAIL;
		this.error = error;
	}

	public String getSucces() {
		return succes;
	}

	public void setSucces(String succes) {
		setSucces();
		this.succes = succes;
	}
	
	public void setSucces() {
		result = RESP_RESULT_SUCC;
	}
}
 
 
 
