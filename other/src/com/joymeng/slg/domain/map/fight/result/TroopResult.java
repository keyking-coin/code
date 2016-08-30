package com.joymeng.slg.domain.map.fight.result;

public class TroopResult {
	public int hitTotal;
    public int missTotal;
    public int crititalTotal;
    public int grazingTotal;
    public int unitKilled;
    public int unitLost;
    public int damageTotal;
    public int beHitTotal;
    public int missedTotal;

    public String ToString()
    {
        return " hitTotal  = " + hitTotal + "\n missTotal  = " + missTotal + 
            "\n crititalTotal  = " + crititalTotal + "\n grazingTotal  = " +
            grazingTotal + "\n unitKilled  = " + unitKilled + "\n unitLost  = " + 
            unitLost + "\n damageTotal  = " + damageTotal +
            "\n BeHitTotal  = " + beHitTotal + "\n missedTotal = " +missedTotal;
    }
}
