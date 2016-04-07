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
		.setTitle("����") 
		.setMessage(str)  
		.setPositiveButton("ȷ��" ,null )  
		.show();  
	}
	
	public void exit(final Activity activity){
		new AlertDialog.Builder(activity)    
		.setTitle("ϵͳ��ʾ") 
		.setMessage("ȷ���˳�Ӧ��?")  
		.setNegativeButton("ȷ��",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				activity.finish();
			}
		}).setPositiveButton("ȡ��",null)
		.show();
	}
	
	public static void showLoading(Activity activity,String title){
		if (loading == null){
			loading = ProgressDialog.show(activity,title,"ͨѶ��,���Ժ�...",true,true); 
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
