package com.joymeng.slg.domain.map.fight.result;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.Position;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;

public class UnitInfo {
	String k;//名称
	Side s;//阵营
	Position p;//位置
	String c;
	
	public UnitInfo() {
		
	}
	
	public void init(FightTroops troops){
		k = troops.getAttribute().getName();
		c = troops.getAttribute().getcName();
		s = troops.getSide();
		p = troops.getPos();
	}
	
	public String getK() {
		return k;
	}
	public void setK(String k) {
		this.k = k;
	}
	public Side getS() {
		return s;
	}
	public void setS(Side s) {
		this.s = s;
	}
	public Position getP() {
		return p;
	}
	public void setP(Position p) {
		this.p = p;
	}
	
	@Override
	public String toString() {
		return s.toString() + "_" + c;
	}
}
