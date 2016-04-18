package com.keyking.admin.frame;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.keyking.admin.net.DataBuffer;
import com.keyking.admin.net.NetUtil;

public class BaseActiivity extends Activity {
	NetUtil net = null;
	ProgressDialog loading;
	static List<Activity> exitList  = new ArrayList<Activity>();
	Handler uiHandler;
	Handler handler;
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		exitList.add(this);
		handler = new Handler(){
			@Override
            public void handleMessage(Message msg) {
                switch(msg.what){
	                case 0:
	                	showTips(BaseActiivity.this,msg.obj.toString());
	                	break;
	                case 1:
	                	Toast.makeText(BaseActiivity.this,msg.obj.toString(),Toast.LENGTH_SHORT).show();
	                	break;
	                case 2:
	                	AdminAgency agency = (AdminAgency)BaseActiivity.this;
	                	agency.refresh();
	                	break;
                }
            }
		};
		net = NetUtil.getInstance();
		net.setActivity(this);
	}

	
	public Handler getHandler() {
		return handler;
	}

	public void showTips(Activity activity,String str){
		new AlertDialog.Builder(activity)    
		.setTitle("错误") 
		.setMessage(str)  
		.setPositiveButton("确定",null )  
		.show();  
	}
	
	private void over(){
		try { 
            for (Activity activity : exitList) { 
                if (activity != null){
                	activity.finish();
                }   
            }
            System.exit(0);
        } catch (Exception e) { 
            e.printStackTrace(); 
        } finally { 
            System.exit(0); 
        } 
	}
	
	public void exit(final Activity activity){
		new AlertDialog.Builder(activity)    
		.setTitle("系统提示") 
		.setMessage("确定退出应用?")  
		.setNegativeButton("确定",new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,int which) {
				over();
			}
		}).setPositiveButton("取消",null).show();
	}
	
	public void showLoading(Activity activity,String title){
		if (loading == null){
			loading = ProgressDialog.show(activity,title,"通讯中,请稍候...",true,true); 
		}else{
			loading.show();
		}
	}
	
	public void dispearLoading(){
		if (loading != null){
			loading.dismiss();
			loading = null;
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
	
	public void _fail(DataBuffer data){
		String tips = data.getUTF();
		Message message = new Message();
        message.what = 0;
        message.obj = tips;
        handler.sendMessage(message);
	}
	
	public void showToast(String str){
		Message message = new Message();
        message.what = 1;
        message.obj  = str;
        handler.sendMessage(message);
	}
}
