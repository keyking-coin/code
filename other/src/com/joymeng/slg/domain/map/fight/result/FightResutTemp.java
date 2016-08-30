package com.joymeng.slg.domain.map.fight.result;

import java.util.Map;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;

public class FightResutTemp {
	int alive;
	int injurie;
	int die;
	Map<String,Integer> kills;
	
	public FightResutTemp(FightTroops troops,int alive,int injurie,int die){
		this.alive = alive;
		this.injurie = injurie;
		this.die = die;
		this.kills = troops.getKills();
	}

	public int getAlive() {
		return alive;
	}

	public int getInjurie() {
		return injurie;
	}

	public void setInjurie(int injurie) {
		this.injurie = injurie;
	}

	public int getDie() {
		return die;
	}

	public void setDie(int die) {
		this.die = die;
	}

	public Map<String, Integer> getKills() {
		return kills;
	}

	public void setKills(Map<String, Integer> kills) {
		this.kills = kills;
	}

	public int getKill() {
		int num = 0;
		for (Integer kill : kills.values()){
			num += kill.intValue();
		}
		return num;
	}
}
