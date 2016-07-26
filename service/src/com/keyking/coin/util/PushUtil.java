package com.keyking.coin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.DeviceType;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.push.PushType;

public class PushUtil implements Instances{
	private static PushUtil instance = new PushUtil();
	
	JPushClient pushClient;
	
	public static PushUtil getInstance(){
		return instance;
	}
	
	public void init(){
		pushClient = new JPushClient("fe714706cb46fc30db0c4757", "8c6c63ca61aa276290efbbf5");
	}

	public JPushClient getPushClient() {
		return pushClient;
	}
	
	public void push(String title,String alert,String platform,Map<String, String> data ,String... targets){
		try {
			if (StringUtil.isNull(platform)){
				return;
			}
			if (platform.equals(DeviceType.Android.value())){
				pushClient.sendAndroidNotificationWithRegistrationID(title,alert,data,targets);
			}else if (platform.equals(DeviceType.IOS.value())){
				pushClient.sendIosNotificationWithRegistrationID(alert,data,targets);
			}
		} catch (Exception e) {
			ServerLog.error("推送错误",e);
		}
	}
	
	public void pushAll(String title,String alert,Map<String, String> data){
		List<UserCharacter> users    = CTRL.getUsers();
		List<String> ios_targets     = new ArrayList<String>();
		List<String> android_targets = new ArrayList<String>();
		for (int i = 0 ; i < users.size() ; i++){
			UserCharacter user = users.get(i);
			String pushId = user.getPushId();
			String platform = user.getPlatform();
			String type = data.get("type");
			PushType pt = PushType.search(type);
			if (user.couldPush(pt)){
				if (platform.equals(DeviceType.Android.value())){
					android_targets.add(pushId);
				}else if(platform.equals(DeviceType.IOS.value())){
					ios_targets.add(pushId);
				}
			}
		}
		if (android_targets.size() > 0){
			String[] targets = new String[android_targets.size()];
			android_targets.toArray(targets);
			push(title,alert,DeviceType.Android.value(),data,targets);
		}
		if (ios_targets.size() > 0){
			String[] targets = new String[ios_targets.size()];
			ios_targets.toArray(targets);
			push(title,alert,DeviceType.IOS.value(),data,targets);
		}
	}
}
