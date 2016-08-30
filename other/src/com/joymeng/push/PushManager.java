package com.joymeng.push;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.push.android.AndroidBroadcast;
import com.joymeng.push.android.AndroidUnicast;
import com.joymeng.push.ios.IOSBroadcast;
import com.joymeng.push.ios.IOSUnicast;

public class PushManager implements Instances{
	private static PushManager instance = new PushManager();
	String appkey;
	String appMasterSecret;
	PushClient client = new PushClient();
	
	public static PushManager getInstance(){
		return instance;
	}
	
	public void load() throws Exception{
		Properties properties = new Properties();
		File file = new File(Const.CONF_PATH + "push.properties");
		properties.load(new FileInputStream(file));
		appkey = properties.getProperty("appkey","57a42d3067e58e5b72002cc8");
		appMasterSecret = properties.getProperty("appkey","pwrrk7hnevm3afartnng1qjr9hgjbluv");
		GameLog.info("push Properties loaded!");
	}
	
	public boolean sendAndroidBroadcast(String title,String content) throws Exception {
		AndroidBroadcast broadcast = new AndroidBroadcast(appkey,appMasterSecret);
		//broadcast.setTicker(ticker);
		broadcast.setTitle(title);
		broadcast.setText(content);
		broadcast.goAppAfterOpen();
		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		broadcast.setProductionMode();
		//broadcast.setExtraField("test","helloworld");
		return client.send(broadcast);
	}
	
	public boolean sendIOSBroadcast(String title,String content) throws Exception {
		IOSBroadcast broadcast = new IOSBroadcast(appkey,appMasterSecret);
		broadcast.setAlert(title);
		broadcast.setBadge(0);
		broadcast.setSound("default");
		broadcast.setProductionMode();
		broadcast.setCustomizedField("content", content);
		return client.send(broadcast);
	}
	
	public boolean sendAndroidUnicast(String targetId,String title,String content) throws Exception {
		AndroidUnicast unicast = new AndroidUnicast(appkey,appMasterSecret);
		unicast.setDeviceToken(targetId);
		//unicast.setTicker( "Android unicast ticker");
		unicast.setTitle(title);
		unicast.setText(content);
		unicast.goAppAfterOpen();
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		unicast.setProductionMode();
		//unicast.setExtraField("test", "helloworld");
		return client.send(unicast);
	}
	
	public boolean sendIOSUnicast(String targetId,String title,String content) throws Exception {
		IOSUnicast unicast = new IOSUnicast(appkey,appMasterSecret);
		unicast.setDeviceToken(targetId);
		unicast.setAlert("IOS 单播测试");
		unicast.setBadge(0);
		unicast.setSound("default");
		unicast.setProductionMode();
		unicast.setCustomizedField("content", content);
		return client.send(unicast);
	}
}
