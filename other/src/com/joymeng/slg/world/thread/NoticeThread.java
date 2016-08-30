package com.joymeng.slg.world.thread;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;

public class NoticeThread extends Thread implements Instances {
	public final static int TICKS_PER_SECOND = 10;

	public final static int TIME_PER_TICK = 1000 / TICKS_PER_SECOND;

	@Override
	public void run() {
		while (!ServiceApp.FREEZE)
			try {
				long pre = TimeUtils.nowLong();
				notice.tick(pre);
				long min = TIME_PER_TICK + pre - TimeUtils.nowLong();
				if (min > 0) {
					Thread.sleep(min);
				}
			} catch (Exception e) {
				GameLog.error(e);
			}
	}
}
