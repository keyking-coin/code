/**
 * 
 */
package com.joymeng.slg.world;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import com.joymeng.common.util.MathUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.world.thread.BroadcastThread;
import com.joymeng.slg.world.thread.DailyRunable;
import com.joymeng.slg.world.thread.EnterGameThread;
import com.joymeng.slg.world.thread.HourRunable;
import com.joymeng.slg.world.thread.MapThread;
import com.joymeng.slg.world.thread.OnlineRunnable;
import com.joymeng.slg.world.thread.PushThread;
import com.joymeng.slg.world.thread.SaveThread;
import com.joymeng.slg.world.thread.TaskRunnable;
import com.joymeng.slg.world.thread.WorldNoticeThread;
import com.joymeng.slg.world.thread.WorldThread;

/**
 * @author Dream
 * 
 */
public class TaskPool {

	public static long SECONDS_PER_DAY = TimeUnit.DAYS.toSeconds(1);
	public static long SECONDS_PER_HOUR = TimeUnit.HOURS.toSeconds(1);
	public static long SECONDS_PER_MINTUE = TimeUnit.MINUTES.toSeconds(1);
	public static long SECONDS_PER_SECOND = TimeUnit.SECONDS.toSeconds(1);
	private final static ScheduledThreadPoolExecutor taskExcutor = new ScheduledThreadPoolExecutor(5);
	private final static TaskPool instance = new TaskPool();
	public HashMap<String, ScheduledFuture<?>> tasks = new HashMap<String, ScheduledFuture<?>>();

	private TaskPool() {
		taskExcutor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				taskExcutor.purge();
			}
		}, 2, 2, TimeUnit.HOURS);
	}

	public static TaskPool getInstance() {
		return instance;
	}

	public ScheduledFuture<?> schedule(String taskKey, Runnable command,long delay, TimeUnit unit) {
		command = new TaskRunnable(command);
		ScheduledFuture<?> task = taskExcutor.schedule(command, delay, unit);
		if (taskKey != null) {
			tasks.put(taskKey, task);
		}
		return task;
	}

	public ScheduledFuture<?> scheduleAtFixedRate(String taskKey,Runnable command, long initialDelay, long period, TimeUnit unit) {
		command = new TaskRunnable(command);
		ScheduledFuture<?> task = taskExcutor.scheduleAtFixedRate(command, initialDelay, period, unit);
		if (taskKey != null) {
			tasks.put(taskKey, task);
		}
		return task;
	}

	public WorldThread mainThread = new WorldThread();
	
	public WorldNoticeThread worldNoticeThread = new WorldNoticeThread();
	
	public SaveThread saveThread = new SaveThread() ;
	
	public MapThread mapTread = new MapThread();
	
	public BroadcastThread broadcastThread = new BroadcastThread();
	
	public PushThread pushThread = new PushThread();
	
	public EnterGameThread enterThread = new EnterGameThread();
	
	public void start() {
		GameLog.info("start all thread");
		mainThread.start();
		saveThread.start();
		mapTread.start();
		broadcastThread.start();
		worldNoticeThread.start();
		//pushThread.start();
		enterThread.start();
		long delay = MathUtils.getSecondsToClock(23,59) + 61;//第二天的第一秒
		scheduleAtFixedRate(null, new DailyRunable(),delay,SECONDS_PER_DAY,TimeUnit.SECONDS);
		DateTime time = TimeUtils.now();
		long hour = MathUtils.getSecondsToClock(time.getHourOfDay(),59);//每个小时的第59分
		scheduleAtFixedRate(null, new HourRunable(),hour,SECONDS_PER_HOUR,TimeUnit.SECONDS);
		long day = MathUtils.getSecondsToClock(23,59);//每天的23:59
		scheduleAtFixedRate(null, new OnlineRunnable(),day,SECONDS_PER_DAY,TimeUnit.SECONDS);
	}

	public void stop() {
		taskExcutor.shutdown();
	}

	public SaveThread getSaveThread() {
		return saveThread;
	}

	public void remove(String key) {
		if (tasks.containsKey(key)){
			ScheduledFuture<?> sf = tasks.get(key);
			sf.cancel(true);
		}
	}
}
