package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;

public class RemoveRoleEvent extends AbstractGameEvent{

	@Override
	public void _handle(IObject trigger, Object[] params) {
//		Role role = get(trigger);
		short code = get(params[0]);
		switch (code) {
		case GameEvent.REMOVE_ROLE:
//			 role.handleEvent(GameEvent.ACTIVITY_EVENTS,ActvtEventType.DELETE_ROLE,uid);
			break;

		default:
			break;
		}
	}

}
