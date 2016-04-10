package com.keyking.admin.frame;

import android.os.Bundle;

import com.keyking.admin.R;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.resp.ResultCallBack;

public class AdminUser extends BaseActiivity implements ResultCallBack {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_user);
	}

	@Override
	public void succ(DataBuffer data) {
		
	}

	@Override
	public void fail(DataBuffer data) {
		
	}
}
