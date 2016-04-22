package com.keyking.coin.service;

import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;

public class MainLoop extends Thread implements Instances{
	
	long tickcount = 0;
	
	long preTime = 0;
	
	public boolean isRunning = true;
	
	@Override
	public void run() {
		CTRL.load();
		ServerLog.info("service started success");
		preTime = System.currentTimeMillis();
		while (isRunning) {
			tickcount ++;
			if (tickcount == 10000){
				CTRL.tick();
				tickcount = 0;
			}
		}
	}
}
 
