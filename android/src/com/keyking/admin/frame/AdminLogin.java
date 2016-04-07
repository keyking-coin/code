package com.keyking.admin.frame;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.keyking.admin.R;
import com.keyking.admin.StringUtil;
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

	@Override
	public void succ(DataBuffer data) {
		dispearLoading();
	}

	@Override
	public void fail(DataBuffer data) {
		dispearLoading();
		String tips = data.getUTF();
		showTips(this,tips);
	}

	@Override
	public void onBackPressed() {
		if (loading != null && loading.isShowing()){
			dispearLoading();
		}else{
			exit(this);
		}
	}
}
