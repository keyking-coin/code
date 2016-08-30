package com.joymeng.slg.domain.event.impl;

import com.joymeng.slg.domain.event.AbstractGameEvent;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.IObject;
import com.joymeng.slg.domain.object.rank.RoleRank;
import com.joymeng.slg.domain.object.role.Role;

public class RoleRankEvent extends AbstractGameEvent {

	@Override
	public void _handle(IObject trigger, Object[] params) {
		Role role = get(trigger);
		short code = get(params[0]);
		switch (code) {
		case GameEvent.RANK_ROLE_FIGHT_CHANGE: {
			// 排行榜个人战斗力发生变化
			RoleRank roleRank = rankManager.getRoleRankByRoleUid(role.getId());
			if (roleRank != null) {
				roleRank.setFight(role.getFightPower());
			}
			break;
		}
		case GameEvent.RANK_ROLEKILLENEMY_CHANGE: {
			// 排行榜个人击杀部队数发生变化
			RoleRank roleRank = rankManager.getRoleRankByRoleUid(role.getId());
			if (roleRank != null) {
				roleRank.setRoleKillEnemy(role.getRoleStatisticInfo().getKillSoldsNum());
			}
			break;
		}
		case GameEvent.RANK_ROLECITYLEVEL_CHANGE: {
			// 排行榜个人城市等级发生变化
			RoleRank roleRank = rankManager.getRoleRankByRoleUid(role.getId());
			if (roleRank != null) {
				roleRank.updataRoleCityLevel();
			}
			break;
		}
		case GameEvent.RANK_ROLEHEROLEVEL_CHANGE: {
			// 排行榜个人英雄等级发生变化
			RoleRank roleRank = rankManager.getRoleRankByRoleUid(role.getId());
			if (roleRank != null) {
				roleRank.updataRoleHeroLevel();
			}
			break;
		}
		default:
			break;
		}
	}

}
