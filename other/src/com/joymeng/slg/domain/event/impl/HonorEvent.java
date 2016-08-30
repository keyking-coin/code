package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.HonorMissionAgent;

public class HonorEvent extends AbstractGameEvent {

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role = get(trigger);
		short code = get(params[0]);
		switch (code) {

		case GameEvent.ROLE_CREATE:// 角色创建
		{
			role.getHonorAgent().initHonors(role); // 初始化
			break;
		}

		case GameEvent.LOAD_FROM_DB:// 加载荣誉任务表
		{
//			world.loadHonor(role);
			break;
		}
		case GameEvent.TASK_CHECK_STATE_EVENT: {
			String id = get(params[1]);
			byte awardStatus = get(params[2]);
			int schedule = get(params[3]);
			HonorMissionAgent honorAgent = role.getHonorAgent();
			honorAgent.sendMsTohonor(id, awardStatus, schedule); // 荣誉任务Id,是否完成,进度
			break;
		}

		default:
			break;
		}
	}
}
