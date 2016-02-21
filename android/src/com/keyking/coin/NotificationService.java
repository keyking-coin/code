package com.keyking.coin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
	
	// 获取消息线程
    private MessageThread messageThread = null;

    private PendingIntent messagePendingIntent = null;
    
    // 通知栏消息
    private int messageNotificationID = 1000;
    
    private NotificationManager messageNotificatioManager = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        messagePendingIntent = PendingIntent.getActivity(this,0,new Intent(this,UnityInterfaceActivity.class), 0);
        //开启线程
        messageThread = new MessageThread(this);
        messageThread.isRunning = true;
        messageThread.start();
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 从服务器端获取消息
     * 
     */
    class MessageThread extends Thread {
    	
    	Context context = null;
    	
    	public MessageThread(Context context){
    		this.context = context;
    	}
    	
        //设置是否循环推送
        public boolean isRunning = true;

        public void run() {
            while (isRunning) {
		        try {
		            //间隔时间
		            Thread.sleep(1000);
		            //获取服务器消息
		            String serverMessage = getServerMessage();
		            if (serverMessage != null && !"".equals(serverMessage)) {
		                //更新通知栏
		            	Notification notification = new Notification.Builder(context)    
		                .setAutoCancel(true)    
		                .setContentTitle("title")    
		                .setContentText(serverMessage)   
		                .setContentIntent(messagePendingIntent)    
		                .setSmallIcon(R.drawable.icon)    
		                .setWhen(System.currentTimeMillis())   
		                .build();
		                messageNotificatioManager.notify(messageNotificationID,notification);
		                messageNotificationID++;
		            }
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
            }
        }
    }

    @Override
    public void onDestroy() {
        messageThread.isRunning = false;
        super.onDestroy();
    }

    /**
     * 模拟发送消息
     * @return 返回服务器要推送的消息，否则如果为空的话，不推送
     */
    public String getServerMessage() {
        return "NEWS!";
    }
}
