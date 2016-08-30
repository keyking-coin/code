package com.joymeng.slg.domain.object.bag;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerOver;

public class UseItemFinish implements Instances,TimerOver{
	String itemId;
	long uid;
	public UseItemFinish(long uid, String itemId){
		this.itemId = itemId;
		this.uid = uid;
	}
	@Override
	public void finish() {
		Role role = world.getOnlineRole(uid);
		if(role == null){
			GameLog.error("useItemFinish error,role not online.");
			return;
		}
//		role.getBagAgent().useItemFinish(role, itemId);
	}

}
