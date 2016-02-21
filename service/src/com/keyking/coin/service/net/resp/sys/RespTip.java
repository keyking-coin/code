package com.keyking.coin.service.net.resp.sys;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.RespEntity;

public class RespTip extends RespEntity {
	
	String context;
	
	public RespTip(String context) {
		super("RespTip");
		this.context = context;
		setSucces();
	}

	@Override
	public void _serialize_ok(DataBuffer buffer) {
		buffer.putUTF(context);
	}
}
 
