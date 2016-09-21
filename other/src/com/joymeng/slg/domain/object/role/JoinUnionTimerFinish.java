package com.joymeng.slg.domain.object.role;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;

public class JoinUnionTimerFinish implements TimerOver, Instances {

	long uid;

	public JoinUnionTimerFinish(long uid) {
		this.uid = uid;
	}

	@Override
	public void finish() {
		Role role = world.getRole(uid);
		if (role == null) {
			GameLog.error("JoinUnionTimerFinish world getRole is null");
			return;
		}
		role.setJoinTimer(null);
		RespModuleSet rms = new RespModuleSet();
		role.sendRoleToClient(rms);
		MessageSendUtil.sendModule(rms, role);
	}

}
