package com.keyking.coin.service.thread;

import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class UserThread extends Thread implements Instances{
	
	public static boolean isRunning = true;
	
	@Override
	public void run() {
		while (isRunning){
			long pre  = TimeUtils.nowLong();
			CTRL.tick(pre);
			long time  = pre + 100 - TimeUtils.nowLong();
			if (time > 0){
				try {
					sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
