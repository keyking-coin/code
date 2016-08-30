package com.joymeng.slg.domain.object.rank;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.union.UnionBody;

public class RoleRank implements Instances {
	long uid; // 用户uid
	int fight = 0; // 用户战斗力
	int roleKillEnemy = 0; // 用户消灭敌军数
	int roleCityLevel = 0; // 用户城市等级
	int roleHeroLevel = 0; // 用户角色等级
	byte comType;//

	public RoleRank() {
	}

	public RoleRank(Role role) {
		uid = role.getId();
		fight = role.getFightPower();
		updataRoleCityLevel();
		updataRoleHeroLevel();
	}

	public synchronized void updataRoleCityLevel() {
		Role role = world.getRole(uid);
		if (role != null) {
			RoleCityAgent cityAgent = role.getCity(0);
			roleCityLevel = cityAgent.getCityCenterLevel();
		}
	}

	public synchronized void updataRoleHeroLevel() {
		Role role = world.getRole(uid);
		if (role != null) {
			roleHeroLevel = role.getLevel();
		}
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public int getFight() {
		return fight;
	}

	public void setFight(int fight) {
		this.fight = fight;
	}

	public int getRoleKillEnemy() {
		return roleKillEnemy;
	}

	public void setRoleKillEnemy(int roleKillEnemy) {
		this.roleKillEnemy = roleKillEnemy;
	}

	public int getRoleCityLevel() {
		return roleCityLevel;
	}

	public void setRoleCityLevel(int roleCityLevel) {
		this.roleCityLevel = roleCityLevel;
	}

	public int getRoleHeroLevel() {
		return roleHeroLevel;
	}

	public void setRoleHeroLevel(int roleHeroLevel) {
		this.roleHeroLevel = roleHeroLevel;
	}

	public void sendClient(ParametersEntity param) {
		Role role = world.getRole(uid);
		if (role == null) {
			GameLog.error("get role is fail from roleRank , setUid=0 client dispose");
			param.put(0);// long uid=0 直接跳过 读下一条
			return;
		}
		param.put(uid);// long uid=0 直接跳过 读下一条
		param.put(role.getName());// string
		param.put(role.getIcon().getIconType());// byte
		param.put(role.getIcon().getIconId());// byte
		param.put(role.getIcon().getIconName());// string
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			param.put(0); // 没有联盟
		} else {
			param.put(1); // 有联盟
			param.put(unionBody.getName());// string
			param.put(unionBody.getShortName());// string
		}
	}
}
