package com.keyking.admin.net.resp;

import com.keyking.admin.net.DataBuffer;

public interface ResultCallBack {
	public void succ(String logicName,DataBuffer data);
	public void fail(String logicName,DataBuffer data);
}
