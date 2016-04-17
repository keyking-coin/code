package com.keyking.admin.frame;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.keyking.admin.JsonUtil;
import com.keyking.admin.R;
import com.keyking.admin.StringUtil;
import com.keyking.admin.data.DataManager;
import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.data.user.UserData;
import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.request.NetLogicName;
import com.keyking.admin.net.request.Request;
import com.keyking.admin.net.resp.ResultCallBack;

public class AdminLogin extends BaseActiivity implements ResultCallBack{
	EditText account;
	EditText pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_login);
		account = (EditText) findViewById(R.id.login_account);
		pwd = (EditText) findViewById(R.id.login_pwd);
		Button button = (Button) findViewById(R.id.login_action);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String str_account = account.getText().toString();
				if (StringUtil.isNull(str_account)){
					showTips(AdminLogin.this,account.getHint().toString());
					return;
				}
				String str_pwd = pwd.getText().toString();
				if (StringUtil.isNull(str_pwd)){
					showTips(AdminLogin.this,pwd.getHint().toString());
					return;
				}
				Request request = new Request(NetLogicName.admin_login.getKey());
				request.add(str_account);
				request.add(str_pwd);
				if (net.send(request,AdminLogin.this)){
					showLoading(AdminLogin.this,"µÇÂ¼");
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void succ(String logicName,DataBuffer data) {
		String str = data.getUTF();
		Map<String,Object> temp = (Map<String,Object>)JSON.parse(str);
		Object obj = temp.get("user");
		UserData user = JsonUtil.JsonToObject(obj.toString(),UserData.class);
		DataManager.getInstance().setUser(user);
		obj = temp.get("deals");
		List<Deal> deals = JsonUtil.JsonToObjectList(obj.toString(),Deal.class);
		DataManager.getInstance().setDeals(deals);
		/*
		obj = temp.get("sellers");
		List<UserData> sellers = JsonUtil.JsonToObjectList(obj.toString(),UserData.class);
		DataManager.getInstance().setSellers(sellers);
		*/
		Intent intent = new Intent(this,AdminUser.class);
		startActivity(intent);
		dispearLoading();
	}

	@Override
	public void fail(String logicName,DataBuffer data) {
		_fail(data);
	}
}
