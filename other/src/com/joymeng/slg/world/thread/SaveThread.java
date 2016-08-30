/**
 * 
 */
package com.joymeng.slg.world.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.dao.DaoData;


public class SaveThread extends Thread implements Instances{
	
	ConcurrentLinkedQueue<DaoData> saveQueue = new ConcurrentLinkedQueue<DaoData>();
	ConcurrentLinkedQueue<DaoData> delaySaveQueue = new ConcurrentLinkedQueue<DaoData>();
	long nextErrorSaveTime;
	Map<String, Object> saveTemp = new HashMap<String, Object>();
	
	public void addSaveData(DaoData data) {
		if (data == null){
			return;
		}
		saveQueue.add(data);
	}

	@Override
	public void run() {
		nextErrorSaveTime = TimeUtils.nowLong();
		while (!ServiceApp.FREEZE) {
			try {
				long delay = 1000;
				if (saveQueue.size() > 0){
					delay = 0;
				}
				if (delay > 0){
					Thread.sleep(delay);
				}
				long now = TimeUtils.nowLong();
				DaoData data = null;
				if (now > nextErrorSaveTime) {
					nextErrorSaveTime = now + Const.MINUTE;
					data = delaySaveQueue.poll();
				}
				if (data == null) {
					data = saveQueue.poll();
				}
				if (data == null) {
					continue;
				} else {
					try {
						dbMgr.getGameDao().saveDaoData(data,saveTemp);
						GameLog.info("SAVE SUCCESS >> " + data);
					} catch (Exception e) {
						GameLog.error("SAVE ERROR >> " + data,e);
						delaySaveQueue.add(data);
					}
				}
			} catch (Exception e) {
				GameLog.error(e);
			}
		}
	}
	
	public void gameShutDown() {
		Map<String, Object> map = new HashMap<String, Object>();
		do {
			try {
				DaoData data = saveQueue.poll();
				if (data == null){
					break;
				}
				dbMgr.getGameDao().saveDaoData(data,map);
				GameLog.info("SAVE SUCCESS >> " + data);
			} catch (Exception e) {
				GameLog.error("SAVE ERROR >> ", e);
			}
		}while(true);
		do {
			try {
				DaoData data = delaySaveQueue.poll();
				if (data == null){
					break;
				}
				dbMgr.getGameDao().saveDaoData(data,map);
				GameLog.info("SAVE SUCCESS >> " + data);
			} catch (Exception e) {
				GameLog.error("SAVE ERROR >> ", e);
			}
		}while(true);
	}
}
