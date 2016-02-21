package com.keyking.coin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OneShotAlarm extends BroadcastReceiver{

	@Override
	public void onReceive(Context context,Intent intent) {
		//UnityInterfaceActivity unity = (UnityInterfaceActivity)context;
		//unity.testNotification();
		Toast.makeText(context, "Alarm do ok",Toast.LENGTH_SHORT).show();
		//UnityPlayer.UnitySendMessage("button-time","aasdadadasste","Alarm do ok");
	}
}
