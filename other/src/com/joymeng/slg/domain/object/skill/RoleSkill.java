package com.joymeng.slg.domain.object.skill;

import com.joymeng.Instances;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.technology.data.Tech;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.domain.timer.TimerOver;
import com.joymeng.slg.net.mod.RespModuleSet;

public class RoleSkill implements Instances, TimerOver {
	private String skillId;
	private int level;
	private long uid;
	boolean isActive;// 是否主动技
	byte state;// 0-未使用状态，1-cd中，2-技能生效中
	byte branchId;// 所属分支id
	TimerLast time;// 主动技能cd时间

	public RoleSkill(long uid, String skillId, int level, byte branchId, byte isActive, byte state) {
		this.skillId = skillId;
		this.level = level;
		this.uid = uid;
		this.isActive = (isActive == 0) ? true : false;
		this.state = state;
		this.branchId = branchId;
	}

	public byte getBranchId() {
		return branchId;
	}

	public void setBranchId(byte branchId) {
		this.branchId = branchId;
	}

	public String getSkillId() {
		return skillId;
	}

	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public TimerLast getTimer() {
		if (isActive) {
			if (time != null && time.getLast() > 0) {
				return time;
			}
		}
		return null;
	}

	public void addTimer(TimerLast timer) {
		this.time = timer;
		time.registTimeOver(this);
	}

	public void createTimer(long lastTime, TimerLastType type) {
		time = new TimerLast(TimeUtils.nowLong() / 1000, lastTime, type);
		time.registTimeOver(this);
	}

	public boolean checkIsCanUse() {
		if (isActive && level > 0) {
			if (state == 0) {
				return true;
			}
		}
		return false;
	}

	public void tick(Role role) {
		if (time != null && time.over()) {
			time.die();
			if (role.getSkillAgent() != null) {
				RespModuleSet rms = new RespModuleSet();
				role.getSkillAgent().sendToClient(rms);
				MessageSendUtil.sendModule(rms, role);
			}
		}
	}

	@Override
	public void finish() {
		if (state == 1) {
			state = 0;
			time = null;
		} else if (state == 2) {//技能持续时间结束
			state = 1;
			Role role = world.getObject(Role.class, uid);
			if(role == null){
				return;
			}
			ActiveSkillType type = ActiveSkillType.search(skillId);
			switch (type) {
			case HIGHEST_ALERT:
			{
				role.getSkillAgent().updateHighestAlert(this.skillId, true);
				break;
			}
			case RES_PROTECT:
			{
				MapCity mapCity = mapWorld.searchMapCity(uid,0);
				if(mapCity != null){
					mapCity.getCityState().setResprotect(true);
				}
				break;
			}
			case FOOD_MATCH:
			case SATEL_NAVI:
			case URGENCY_EXPAN:
			case FULL_OF_VIT:
			case CRAZY_COLLECT:
			{
				role.getEffectAgent().removeSkillBuff(skillId);
				break;
			}
			default:
				break;
			}
			Tech tech = dataManager.serach(Tech.class, skillId);
			createTimer(tech.getCdTime(), TimerLastType.TIME_SKILL_CD);
		}
	}
}
