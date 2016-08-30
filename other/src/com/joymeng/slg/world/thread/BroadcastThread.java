package com.joymeng.slg.world.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.broadcast.BroadcastMessage;

/**
 * 广播线程
 * @author tanyong
 *
 */
public class BroadcastThread extends Thread implements Instances{
	ConcurrentLinkedQueue<BroadcastMessage> messagesQueue = new ConcurrentLinkedQueue<BroadcastMessage>();
	ConcurrentLinkedQueue<BroadcastMessage> delayQueue    = new ConcurrentLinkedQueue<BroadcastMessage>();
	long nextErrorSendTime;
	
	public void addMessage(BroadcastMessage message) {
		if (message == null){
			return;
		}
		if (!messagesQueue.contains(message) && !delayQueue.contains(message)) {
			messagesQueue.add(message);
		}
	}
	
	@Override
	public void run() {
		nextErrorSendTime = TimeUtils.nowLong();
		while (!ServiceApp.FREEZE) {
			try {
				long delay = 1000;
				if (messagesQueue.size() > 0){
					delay = 0;
				}
				if (delay > 0){
					Thread.sleep(delay);
				}
				BroadcastMessage message = null;
				long now = TimeUtils.nowLong();
				if (now > nextErrorSendTime) {
					nextErrorSendTime = now + Const.MINUTE;
					message = delayQueue.poll();
				}
				if (message == null) {
					message = messagesQueue.poll();
				}
				if (message == null) {
					continue;
				} else {
					if (message.send()){
						GameLog.info("send BroadcastMessage SUCCESS : " + message);
					}else{
						GameLog.error("send BroadcastMessage ERROR: " + message.toString());
						delayQueue.add(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				GameLog.error(e);
			}	
		}
	}
}
