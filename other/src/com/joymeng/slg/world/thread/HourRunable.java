
package com.joymeng.slg.world.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.RealtimeData;
import com.joymeng.log.GameLog;
import com.joymeng.slg.ServiceApp;
import com.joymeng.slg.world.GameConfig;

public class HourRunable implements Runnable, Instances{
	static Map<String, Map<String, Long>> map = new HashMap<String, Map<String, Long>>();
    static int appid = 1001;
	static int serverid = ServiceApp.instanceId - GameConfig.SERVER_LIST_ID;
	@Override
	public void run() {
		GameLog.info("Do things at fifty-ninth hours per hour");
		if(GameConfig.SEND_REALTIME_DATA){
			 RealtimeData.liveData();   //实时数据（玩家充值）接口
			 liveProduce();             //实时道具产出接口
		}
	}
	
	public static void liveProduce() {
		synchronized (map) {
			List<Object> list = new ArrayList<Object>();
			for (String str : map.keySet()) {
				for (String s : map.get(str).keySet()) {
					long number = map.get(str).get(s);
					if (number == 0) {
						continue;
					}
					Map<String, Object> so = new HashMap<String, Object>();
					String[] ss = s.split("_");
					so.put("time", TimeUtils.getIntegral());
					so.put("appid", appid);
					so.put("serverid", serverid);
					so.put("channelid", str);
					so.put("prop_type", ss[1]);
					so.put("outputpoint", ss[0]);
					so.put("num", number);
					list.add(so);
				}
			}
			if (list.size() == 0) {
				return;
			}
			RealtimeData.getConnection(list, "live_produce");
			map.clear();
		}
	}
	
	
	/*
	 * 1小时道具实时产出记录
	 */
	
	public static  void recordProduce(String channelid,String event,int num,byte type){
		if (map.get(channelid) == null) {
			Map<String, Long> produceMap = new HashMap<String, Long>();
			map.put(channelid, produceMap);
		}
		Map<String, Long> data = map.get(channelid);
		String str = event+"_"+String.valueOf(type);
		if(data.get(str)==null){
			data.put(str, (long)num);
		}else{
			data.put(str, data.get(str)+num);
		}
	}
	
}
