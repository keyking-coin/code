package com.joymeng.list;

import java.util.concurrent.TimeUnit;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.TimeUtils;

public class WriteLogManager implements Instances {
	private static WriteLogManager instance = new WriteLogManager();
	
	public static WriteLogManager getInstance() {
		return instance;
	}

	long delay = MathUtils.getSecondsToClock(23, 59) + 61;// 第二天的第一秒
	public static long SECONDS_PER_DAY = TimeUnit.DAYS.toSeconds(1);

	public void start() {
		taskPool.scheduleAtFixedRate(null, new Runnable() {
			@Override
			public void run() {
				System.out.println("测试:"+TimeUtils.chDate(TimeUtils.nowLong()));
			}
		}, delay,SECONDS_PER_DAY, TimeUnit.SECONDS);
	}

}
