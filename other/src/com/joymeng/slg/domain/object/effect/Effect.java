package com.joymeng.slg.domain.object.effect;

import com.joymeng.Instances;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.SourceType;
import com.joymeng.slg.domain.object.effect.BuffTypeConst.TargetType;
import com.joymeng.slg.domain.timer.TimerLast;

public class Effect implements Instances {
	TargetType type;// 目标效果类型
	int targetTypeId;// 0:仅自己生效1：己方同类兵生效2：攻击目标生效3：敌方同类型生效4：敌方全体生效5：己方全体生效
	ExtendInfo extendInfo;
	SourceType sType;
	float rate;
	int num;
	boolean isPercent;
	String targetId;
	String sourceId;
	TimerLast timer;
	boolean runByAgent = false;//true倒计时交给EffectAgent来统一管理,false由各个模块自己管理
	
	public Effect() {
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public boolean isPercent() {
		return isPercent;
	}

	public void setPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}

	public TargetType getType() {
		return type;
	}

	public void setType(TargetType type) {
		this.type = type;
	}

	public ExtendInfo getExtendInfo() {
		return extendInfo;
	}

	public void setExtendInfo(ExtendInfo extendInfo) {
		this.extendInfo = extendInfo;
	}

	public SourceType getsType() {
		return sType;
	}

	public void setsType(SourceType sType) {
		this.sType = sType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public TimerLast getTimer() {
		return timer;
	}

	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}

	public int getTargetTypeId() {
		return targetTypeId;
	}

	public void setTargetTypeId(int targetTypeId) {
		this.targetTypeId = targetTypeId;
	}

	public boolean isRunByAgent() {
		return runByAgent;
	}

	public void setRunByAgent(boolean runByAgent) {
		this.runByAgent = runByAgent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() == obj.getClass()) {
			ArmyEffVal val = (ArmyEffVal) obj;
			if (type == val.getType()) {
				if (extendInfo == null && val.getExtendInfo() == null)
					return true;
				else if (extendInfo.equals(val.getExtendInfo()))
					return true;
			}
		}
		return false;
	}
}
