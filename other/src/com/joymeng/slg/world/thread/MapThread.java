package com.joymeng.slg.world.thread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.domain.map.MapObject;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.timer.TimerLast;

public class MapThread extends Thread implements Instances{
	
	class TimerObject implements Comparable<TimerObject>{
		
		long time;
		
		Class<? extends IObject> type;
		
		long id;
		
		public TimerObject(IObject obj){
			type = obj.getClass();
			id = obj.getId();
		}
		
		public IObject getObject(){
			if (type != null && id > 0){
				return world.getObject(type,id);
			}
			return null;
		}

		@Override
		public int compareTo(TimerObject arg0) {
			return Long.compare(time, arg0.time);
		}

		public boolean equals(TimerObject other) {
			return type == other.type && id == other.id;
		}
		
	}
	
	List<TimerObject> timerObjs = new ArrayList<TimerObject>(2048);//需要倒计时的对象
	List<TimerObject> addLists = new CopyOnWriteArrayList<TimerObject>();//加入列表
	
	public void addObj(IObject obj,TimerLast timer){
		//if (!timer.getType().isFlag()){
		//	return;
		//}
		if (obj instanceof MapObject){
			MapObject mapObj = (MapObject)obj;
			mapObj.setMapThreadFlag(false);
		}
		TimerObject tObj = new TimerObject(obj);
		tObj.time = timer.getStart() + timer.getLast();
		addLists.add(tObj);
	}
	
	public long tick(){
		long pre = TimeUtils.nowLong();
		if (addLists.size() > 0){
			for (int i = 0 ; i < addLists.size() ; i++){
				TimerObject obj = addLists.get(i);
				TimerObject src = null;
				for (int j = 0 ; j < timerObjs.size() ; j++){
					TimerObject tobj = timerObjs.get(j);
					if (tobj.equals(obj)){
						src = tobj;
						break;
					}
				}
				if (src != null){
					if (obj.time >= src.time){
						continue;
					}else{//新的加入的倒计时更找结束
						src.time = obj.time;
					}
				}else{
					timerObjs.add(obj);
				}
			}
			addLists.clear();
			Collections.sort(timerObjs);
		}
		if (timerObjs.size() > 0){
			for (int i = 0 ; i < timerObjs.size() ;){
				TimerObject obj = timerObjs.get(i);
				IObject iObj = obj.getObject();
				if (iObj == null || iObj.needRemoveAtMapThread()){
					timerObjs.remove(i);
					continue;
				}
				try {
					iObj.tick(pre);
				}catch (Exception e) {
					GameLog.error("mapObj timer error",e);
					timerObjs.remove(i);
					continue;
				}
				i++;
			}
		}
		return WorldThread.TIME_PER_TICK + pre - TimeUtils.nowLong();
	}
	
	@Override
	public void run() {
		while (!ServiceApp.FREEZE){
			long delay = tick();
			try {
				if (delay > 0){
					Thread.sleep(delay);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
