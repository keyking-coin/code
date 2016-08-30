package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.role.TurntableBody;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.union.UnionBody;

public class RoleBuildEvent extends AbstractGameEvent {

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.LOAD_FROM_DB:
			{
				world.loadCitys(role);//加载城市数据
				world.loadRoleStaticInfo(role);
				if(role.getTurntableBody() == null){
					role.setTurntableBody(new TurntableBody(role.getId()));
					role.getTurntableBody().updateTurntableId(role);
				}
				break;
			}
			case GameEvent.ROLE_CREATE://角色创建
			{
				//创建城市
				RoleCityAgent agent = new RoleCityAgent(0);
				role.addCity(agent);
				agent.init(role);
				break;
			}
			case GameEvent.ROLE_BUILD_TIME_ROVER:{
				RoleBuild build = get(params[1]);
				TimerLast timer = get(params[2]);
				if (timer.getType().ordinal() == TimerLastType.TIME_CREATE.ordinal() ||
					timer.getType().ordinal() == TimerLastType.TIME_LEVEL_UP.ordinal() ||
					timer.getType().ordinal() == TimerLastType.TIME_RESEARCH.ordinal() ||
					timer.getType().ordinal() == TimerLastType.TIME_CURE.ordinal()){
					UnionBody union = unionManager.search(role.getUnionId());
					if (union != null){
						union.abolishAssistance(role,build,timer);
					}
				}
				break;
			}
			case GameEvent.ARMY_FACT_CREATE:
			{
				break;
			}
			case GameEvent.ARMY_FACT_LEVEL_UP:
			{
				break;
			}
		}
	}
}
