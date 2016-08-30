package com.joymeng.slg.world.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.push.PushModuleData;
import com.joymeng.slg.ServiceApp;

public class PushThread extends Thread{
	
	List<PushModuleData> pushDatas = new CopyOnWriteArrayList<PushModuleData>();
	List<PushModuleData> sendQueue = new ArrayList<PushModuleData>();
	
	public void add(PushModuleData data){
		pushDatas.add(data);
	}
	
	@Override
	public void run() {
		while (!ServiceApp.FREEZE) {
			try {
				long pre = TimeUtils.nowLong();
				for (int i = 0 ; i < pushDatas.size() ;){
					PushModuleData data = pushDatas.get(i);
					if (data.push(pre / 1000)){
						if (data.checkOnline()){
							int cursor = sendQueue.size() -1;
							while (cursor >= 0){
								PushModuleData cursorData = sendQueue.get(cursor);
								if (cursorData.getSendTime() <= data.getSendTime()){
									sendQueue.add(cursor,data);
									break;
								}
								cursor--;
							}
							int size = sendQueue.size();
							if (size > 0){
								if (size > 0 || sendQueue.get(size-1).getSendTime() < pre / 1000 + 20){
									
								}
							}
						}
						pushDatas.remove(i);
					}else{
						i++;
					}
				}
				long sleep = WorldThread.TIME_PER_TICK + pre - TimeUtils.nowLong();
				if (sleep > 0){
					Thread.sleep(sleep);
				}
			} catch (Exception e) {
				GameLog.error("push error",e);
				e.printStackTrace();
			}
		}
	}
}
