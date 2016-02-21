package com.keyking.coin.service.net.resp.impl;

import com.keyking.coin.service.net.ParametersEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.RespEntity;

public class GeneralResp extends RespEntity {
	
	ParametersEntity params = new ParametersEntity();
	
	public GeneralResp(String logicName) {
		super(logicName);
	}
	
	@Override
	public void _serialize_ok(DataBuffer buffer) {
		if (getSucces() != null){
			buffer.putUTF(getSucces());
		}
		try {
			params.serialize(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(Object obj){
		params.put(obj);
	}
	
	public void add(int cursor,Object obj){
		params.put(cursor,obj);
	}
}
 
