package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;

public class SkillInfo {
	String si;//技能编号
	UnitInfo caster = new UnitInfo();//技能释放者
	List<UnitInfo> targets = new ArrayList<UnitInfo>();//目标对象
	boolean more;//多个目标
	
	public SkillInfo(){
		
	}
	
	public SkillInfo(String si){
		this.si = si;
	}
	
	public SkillInfo(String si,FightTroops c,boolean more){
		this.si = si;
		caster.init(c);
		this.more = more;
	}
	
	public SkillInfo(String si,FightTroops c,FightTroops t){
		this.si = si;
		caster.init(c);
		addTarget(t);
	}
	
	public void addTarget(FightTroops t){
		UnitInfo ui = new UnitInfo();
		ui.init(t);
		targets.add(ui);
	}
	
	public String getSi() {
		return si;
	}
	
	public void setSi(String si) {
		this.si = si;
	}
	
	public UnitInfo getCaster() {
		return caster;
	}
	
	public void setCaster(UnitInfo caster) {
		this.caster = caster;
	}
	
	public List<UnitInfo> getTargets() {
		return targets;
	}
	
	public void setTargets(List<UnitInfo> targets) {
		this.targets = targets;
	}

	public boolean isMore() {
		return more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}

	@Override
	public String toString() {
		return caster.toString() + " cast " + si;
	}
}
