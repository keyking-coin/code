package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.List;

public class RoundData {
	int round;
	List<SkillInfo> starts = new ArrayList<SkillInfo>();
	List<FightInfo> fightInfos = new ArrayList<FightInfo>();
	List<SkillInfo> ends = new ArrayList<SkillInfo>();
	
	public RoundData() {
		// TODO Auto-generated constructor stub
	}
	
	public int getRound() {
		return round;
	}
	
	public void setRound(int round) {
		this.round = round;
	}
	
	public List<FightInfo> getFightInfos() {
		return fightInfos;
	}
	
	public void setFightInfos(List<FightInfo> fightInfos) {
		this.fightInfos = fightInfos;
	}

	public List<SkillInfo> getStarts() {
		return starts;
	}

	public void setStarts(List<SkillInfo> starts) {
		this.starts = starts;
	}

	public List<SkillInfo> getEnds() {
		return ends;
	}

	public void setEnds(List<SkillInfo> ends) {
		this.ends = ends;
	}
	
	public FightInfo search(AttackInfo info){
		for (int i = 0 ; i < fightInfos.size() ; i++){
			FightInfo fi = fightInfos.get(i);
			if (fi.getAttacker().getK().equals(info.getAttacker().getAttribute().getName()) && 
				fi.getDefender().getK().equals(info.getDefender().getAttribute().getName())){
				return fi;
			}
		}
		return null;
	}
}
