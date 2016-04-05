package com.keyking.admin.net.resp;

import com.keyking.admin.net.DataBuffer;

public interface ResultCallBack {
	public void succ(DataBuffer data);
	public void fail(DataBuffer data);
}
