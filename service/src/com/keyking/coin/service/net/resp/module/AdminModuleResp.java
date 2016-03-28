package com.keyking.coin.service.net.resp.module;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;

public class AdminModuleResp extends ModuleResp {
	
	@Override
	public void _serialize_ok(DataBuffer buffer) {
		String str = JsonUtil.ObjectToJsonString(modules);
		buffer.putUTF(str);
	}
}
