package com.keyking.admin.frame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.keyking.admin.net.NetUtil;

public class BaseActiivity extends Activity {
	NetUtil net = null;
	public static ProgressDialog loading;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		net = NetUtil.getInstance();
	}

	public void showTips(Activity activity,String str){
		new  AlertDialog.Builder(activity)    
		.setTitle("错误") 
		.setMessage(str)  
		.setPositiveButton("确定" ,null )  
		.show();  
	}
	
	public static void showLoading(Activity activity,String title){
		if (loading == null){
			loading = ProgressDialog.show(activity,title,"通讯中,请稍候...",true,true); 
		}
	}
	
	public static void dispearLoading(){
		if (loading != null){
			loading.hide();
			loading = null;
		}
	}
}
