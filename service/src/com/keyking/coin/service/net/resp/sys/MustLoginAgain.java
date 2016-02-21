package com.keyking.coin.service.net.resp.sys;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.RespEntity;

public class MustLoginAgain extends RespEntity {
	
	public MustLoginAgain() {
		super("LoginAgain");
		setSucces();
	}

	@Override
	public void _serialize_ok(DataBuffer buffer) {
		//buffer.putUTF("您的账号在别处登录了");
	}
}
 
