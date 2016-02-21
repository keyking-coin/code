package com.keyking.coin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service {
	
	// ��ȡ��Ϣ�߳�
    private MessageThread messageThread = null;

    private PendingIntent messagePendingIntent = null;
    
    // ֪ͨ����Ϣ
    private int messageNotificationID = 1000;
    
    private NotificationManager messageNotificatioManager = null;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        messageNotificatioManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        messagePendingIntent = PendingIntent.getActivity(this,0,new Intent(this,UnityInterfaceActivity.class), 0);
        //�����߳�
        messageThread = new MessageThread(this);
        messageThread.isRunning = true;
        messageThread.start();
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * �ӷ������˻�ȡ��Ϣ
     * 
     */
    class MessageThread extends Thread {
    	
    	Context context = null;
    	
    	public MessageThread(Context context){
    		this.context = context;
    	}
    	
        //�����Ƿ�ѭ������
        public boolean isRunning = true;

        public void run() {
            while (isRunning) {
		        try {
		            //���ʱ��
		            Thread.sleep(1000);
		            //��ȡ��������Ϣ
		            String serverMessage = getServerMessage();
		            if (serverMessage != null && !"".equals(serverMessage)) {
		                //����֪ͨ��
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
     * ģ�ⷢ����Ϣ
     * @return ���ط�����Ҫ���͵���Ϣ���������Ϊ�յĻ���������
     */
    public String getServerMessage() {
        return "NEWS!";
    }
}
