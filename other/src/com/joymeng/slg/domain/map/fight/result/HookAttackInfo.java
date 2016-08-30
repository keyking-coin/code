package com.joymeng.slg.domain.map.fight.result;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;

public class HookAttackInfo extends AttackInfo {
	int attDie;
	
	public HookAttackInfo(FightTroops attacker, FightTroops defender){
		super(attacker, defender);
	}
	
	public void setAttDie(int attDie) {
		this.attDie = attDie;
	}

	@Override
	public int getAttDie() {
		return attDie;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("陷阱:" + attacker.getAttribute().getcName());
		sb.append(" 攻击 ");
		sb.append(defender.getAttribute().getcName() + "("+ defender.getPos().getRow() + "," + defender.getPos().getCol()+")");
		sb.append("结果:[击杀:" + casualty + "单位;");
		sb.append("&nbsp;&nbsp;造成:" + damage + "伤害");
		sb.append("&nbsp;&nbsp;剩余:" + unitRemain + "单位");
		sb.append("&nbsp;&nbsp;消耗:" + attDie + "单位]");
		return sb.toString();
	}
}
