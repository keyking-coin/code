package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.role.Role;

public class RoleBagEvent extends AbstractGameEvent{

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.LOAD_FROM_DB://加载支援
			{
//				world.loadBags(role);
				break;
			}
			case GameEvent.ROLE_CREATE://角色创建
			{
				RoleBagAgent agent = role.getBagAgent();
				agent.setUid(role.getId());
				agent.addGoods("war_protect_3D",1);
				agent.useItem(role,"war_protect_3D",1L,(byte)0,-1);
				break;
			}
		}
	}

}
