package com.keyking.admin.frame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.keyking.admin.net.NetUtil;

public class BaseActiivity extends Activity {
	NetUtil net = null;
	public static ProgressDialog loading;
	public static BaseActiivity base = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		base = this;
		net = NetUtil.getInstance();
	}

	public void showTips(Activity activity,String str){
		new  AlertDialog.Builder(activity)    
		.setTitle("错误") 
		.setMessage(str)  
		.setPositiveButton("确定" ,null )  
		.show();  
	}
	
	public void exit(final Activity activity){
		new AlertDialog.Builder(activity)    
		.setTitle("系统提示") 
		.setMessage("确定退出应用?")  
		.setNegativeButton("确定",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				activity.finish();
			}
		}).setPositiveButton("取消",null)
		.show();
	}
	
	public static void showLoading(Activity activity,String title){
		if (loading == null){
			loading = ProgressDialog.show(activity,title,"通讯中,请稍候...",true,true); 
		}else{
			loading.show();
		}
	}
	
	public static void dispearLoading(){
		if (loading != null){
			loading.hide();
		}
	}
}
