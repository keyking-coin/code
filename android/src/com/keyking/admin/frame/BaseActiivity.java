package com.keyking.admin.frame;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.keyking.admin.net.NetUtil;

public class BaseActiivity extends Activity {
	NetUtil net = null;
	static ProgressDialog loading;
	static List<Activity> exitList  = new ArrayList<Activity>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		net = NetUtil.getInstance();
		exitList.add(this);
	}

	public void showTips(Activity activity,String str){
		new  AlertDialog.Builder(activity)    
		.setTitle("����") 
		.setMessage(str)  
		.setPositiveButton("ȷ��" ,null )  
		.show();  
	}
	
	private void over(){
		try { 
            for (Activity activity : exitList) { 
                if (activity != null){
                	activity.finish();
                }   
            } 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
	}
	
	public void exit(final Activity activity){
		new AlertDialog.Builder(activity)    
		.setTitle("ϵͳ��ʾ") 
		.setMessage("ȷ���˳�Ӧ��?")  
		.setNegativeButton("ȷ��",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				//activity.finish();
				//System.exit(0);
				//android.os.Process.killProcess(android.os.Process.myPid());s
				over();
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
			loading.dismiss();
		}
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
