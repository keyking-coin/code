package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.ClientModule;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

public class RoleEvent extends AbstractGameEvent{

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role  = get(trigger);
		short code = get(params[0]);
		switch(code){
			case GameEvent.ROLE_CREATE://角色创建
			{
				role.getSkillAgent().setUid(role.getId());
				role.getVipInfo().setUid(role.getId());
				role.getRoleStamina().initStamina(role);
				role.getSevenSignIn().initRewardLst(role);
				role.getThirtySignIn().initRewardLst(role);
				role.getDailyAgent().setUid(role.getId());
				role.getDailyAgent().init();
				role.initRoleRank();
				role.getEffectAgent().setUid(role.getId());
				role.getRoleStatisticInfo().initRoleFight(role);
				role.getTurntableBody().setUid(role.getId());
				role.getTurntableBody().updateTurntableId(role);
				role.getBlackMarketAgent().createInit(role);
				break;
			}
			case GameEvent.LOAD_FROM_DB:
			{
//				role.getSkillAgent().setUid(role.getId());
//				role.getSkillAgent().loadData(role);
				role.getSevenSignIn().initRewardLst(role);
				role.getThirtySignIn().initRewardLst(role);
				role.getDailyAgent().setUid(role.getId());
				role.getDailyAgent().init();
				role.getBlackMarketAgent().loadOver(role);
				
				//TODO 
				break;
			}
			case GameEvent.RANK_ROLE_FIGHT_CHANGE:{
				if (role.getUnionId() != 0) {
					UnionBody unionBody = unionManager.search(role.getUnionId());
					if (unionBody != null) {
						UnionMember member = unionBody.searchMember(role.getId());
						if (member != null){
							member.setFight(role.getFightPower());
							unionBody.sendMemberToAllMembers(member,ClientModule.DATA_TRANS_TYPE_UPDATE);
						}
					}
				}
//				role.sendFrequentVariables();//下发战斗力变化
				break;
			}
		}
	}
}
