package com.joymeng.slg.world.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Const;
import com.joymeng.Instances;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.RoleOnlineTime;

public class OnlineRunnable  implements Runnable, Instances{
    static Map<Long,RoleOnlineTime> map = new HashMap<Long,RoleOnlineTime>();
	@Override
	public void run() {
		GameLog.info("Do things at 23:59 every day");
		synchronized (map) {
			List<Role> roleList = world.getOnlineRoles();
			for (int i = 0 ; i < roleList.size() ; i++){
				Role role = roleList.get(i);
				long uid = role.getId();
				long total = map.get(uid).getTotal();
				LogManager.onlineLog(role,total/Const.SECOND);  
			}
			map.clear();
			for (int i = 0 ; i < roleList.size() ; i++){
				Role role = roleList.get(i);
				long uid = role.getId();
				map.put(uid, new RoleOnlineTime());
				RoleOnlineTime  time = map.get(uid);
				time.setLogin(TimeUtils.nowLong());
			}
		}
	}
    
	public static void recordTime(Role role,byte op){ //op 1 登录  2 退出
		long uid = role.getId();
		if(map.get(uid) == null){
			map.put(uid, new RoleOnlineTime());
		}
		RoleOnlineTime  time = map.get(uid);
		long now = TimeUtils.nowLong();
		if(op==1){               //玩家登录
			time.setLogin(now);
		}else{                   //退出游戏
			time.setSignOut(now);
			long online = now - time.getLogin();
			long total = time.getTotal()+online;
			time.setOnline(online);
			time.setTotal(total);
			LogManager.onlineLog(role,online/Const.SECOND);
		}
	}
}
