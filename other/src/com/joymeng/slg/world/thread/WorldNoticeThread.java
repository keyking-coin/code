package com.joymeng.slg.world.thread;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;

public class WorldNoticeThread extends Thread implements Instances{
	@Override
	public void run() {
		while (!ServiceApp.FREEZE){
			try {
				long pre = TimeUtils.nowLong();
				chatMgr.sendHeadWorldNoticeToAllOnlineRoles();
				long min = WorldThread.TIME_PER_TICK + pre - TimeUtils.nowLong();
				if (min > 0){
					Thread.sleep(min); 
				}
			} catch (Exception e) {
				GameLog.error(e);
			}
		}
	}
}
