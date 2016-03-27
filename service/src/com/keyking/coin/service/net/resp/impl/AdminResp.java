package com.keyking.coin.service.net.resp.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;

public class AdminResp extends GeneralResp {
	
	List<String> keys = new ArrayList<String>();
	
	public AdminResp(String logicName) {
		super(logicName);
	}

	@Override
	public void _serialize_ok(DataBuffer buffer) {
		Map<String,Object> datas = new HashMap<String, Object>();
		for (int i = 0 ; i < keys.size() ; i++){
			String key = keys.get(i);
			Object obj = get(i);
			datas.put(key,obj);
		}
		String str = JsonUtil.ObjectToJsonString(datas);
		buffer.putUTF(str);
	}

	public void addKey(int index,String key){
		keys.add(index,key);
	}
	
	public void addKey(String key){
		keys.add(keys.size(),key);
	}
	
	public void addKey(String key,Object value){
		keys.add(keys.size(),key);
		add(value);
	}
}
