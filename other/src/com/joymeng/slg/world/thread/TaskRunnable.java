/**
 * 
 */
package com.joymeng.slg.world.thread;

import com.joymeng.log.GameLog;


/**
 * @author Dream
 *
 */
public class TaskRunnable implements Runnable {
	private Runnable runnable;
	public TaskRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	
	@Override
	public void run() {
		try {
			this.runnable.run();
		} catch (Exception e) {
			GameLog.error(e);
		}
	}
}
