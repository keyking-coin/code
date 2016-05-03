package com.keyking.coin.service.net.resp.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.RespEntity;
import com.keyking.coin.util.JsonUtil;

public class AppResp extends RespEntity {
	
	Map<String,Object> datas = new HashMap<String, Object>();
	
	public AppResp(String logicName) {
		super(logicName);
	}

	@Override
	public void _serialize_ok(DataBuffer buffer) {
		String str = JsonUtil.ObjectToJsonString(datas);
		buffer.putUTF(str);
	}

	public void put(String key,Object value) {
		datas.put(key,value);
	}
}
