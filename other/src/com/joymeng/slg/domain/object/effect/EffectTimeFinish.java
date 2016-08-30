package com.joymeng.slg.domain.object.effect;

import com.joymeng.slg.domain.object.effect.BuffTypeConst.SourceType;
import com.joymeng.slg.domain.timer.TimerOver;

public class EffectTimeFinish implements TimerOver {
	EffectAgent agent;
	SourceType type;
	String id;

	public EffectTimeFinish(EffectAgent agent, SourceType type, String id) {
		this.agent = agent;
		this.type = type;
		this.id = id;
	}

	@Override
	public void finish() {
		switch (type) {
		case EFF_ITEM:
			agent.removeItemBuff(id);
			break;
		case EFF_SKILL:
			agent.removeSkillBuff(id);
		case EFF_VIP:
			agent.removeVipBuffs();
		default:
			break;
		}
	}

}
