package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;

public class BattleRecord {
	Side winner;
	int round;
	List<AttackInfo>       hookInfos  = new ArrayList<AttackInfo>();//陷阱回合
	List<SkillInfo>        prepares    = new ArrayList<SkillInfo>();
	List<List<SkillInfo>>  roundStarts = new ArrayList<List<SkillInfo>>();
	List<List<AttackInfo>> roundList  = new ArrayList<List<AttackInfo>>();
	List<List<SkillInfo>>  roundends   = new ArrayList<List<SkillInfo>>();
    public static final int BATTLE_MAX_ROUND_NUM = 8;
    
    public void startNewRound(){
    	List<AttackInfo> infoList = new ArrayList<AttackInfo>();
        roundList.add(infoList);
        List<SkillInfo> skills = new ArrayList<SkillInfo>();
        roundStarts.add(skills);
        skills = new ArrayList<SkillInfo>();
        roundends.add(skills);
    }

    public void SetWinner(Side s) {
        winner = s;
    }

    public Side GetWinner(){
        return winner;
    }

	public void round() {
		round ++;
	}
	
	public boolean couldContinue(){
		return round < BATTLE_MAX_ROUND_NUM;
	}

	public int getRound() {
		return round;
	}

	public List<List<AttackInfo>> getRoundList() {
		return roundList;
	}
	
	public List<RoundData> reportRoundDatas() {
		List<RoundData> rounds = new ArrayList<RoundData>();
		if (hookInfos.size() > 0){
			RoundData round = new RoundData();
			for (int i = 0 ; i < hookInfos.size() ; i++){
				AttackInfo info = hookInfos.get(i);
				FightInfo fi = round.search(info);
				if (fi == null){
					fi = new FightInfo();
					fi.copy(info);
					round.getFightInfos().add(fi);
				}else{
					int nDie  = fi.getDie() + info.casualty;
					int nDam  = fi.getDam() + info.damage;
					int nAdie = fi.getaDie() + info.getAttDie();
					fi.setDie(nDie);
					fi.setDam(nDam);
					fi.setaDie(nAdie);
					fi.setLn(info.unitRemain);
				}
			}
			rounds.add(round);
		}
		for (int i = 0 ; i < roundList.size() ; i++){
			RoundData round = new RoundData();
			round.getStarts().addAll(roundStarts.get(i));
			round.getEnds().addAll(roundends.get(i));
			round.setRound(i + 1);
			List<AttackInfo> infos = roundList.get(i);
			for (int j = 0 ; j < infos.size() ; j++){
				AttackInfo info = infos.get(j);
				FightInfo fi = new FightInfo();
				fi.copy(info);
				round.getFightInfos().add(fi);
			}
			rounds.add(round);
		}
		return rounds;
	}

	public List<SkillInfo> getPrepares() {
		return prepares;
	}

	public List<AttackInfo> getCurInfos() {
		return roundList.get(round);
	}
	
	public List<SkillInfo> getCurStartSkills() {
		return getCurStartSkills(round);
	}
	
	public List<SkillInfo> getCurStartSkills(int index) {
		return roundStarts.get(index);
	}
	
	public List<SkillInfo> getCurEndSkills() {
		return getCurEndSkills(round);
	}
	
	public List<SkillInfo> getCurEndSkills(int index) {
		return roundends.get(index);
	}
	
	public List<AttackInfo> getHookInfos() {
		return hookInfos;
	}
	
	public int getKillsNum(TroopsData att){
		int num = 0;
		for (int i = 0 ; i < roundList.size() ; i++){
			List<AttackInfo> attackList = roundList.get(i);
			for (int j = 0 ; j < attackList.size() ; j++){
				AttackInfo attInfo = attackList.get(j);
				for(ArmyEntity en : att.getArmys()){
					if(attInfo.getAttacker().getFightId() == en.getId()){
						num += attInfo.getCasualty();
					}
				}
			}
		}
		return num;
	}
	
	public AttackInfo searchFromHookInfo(FightTroops attacker,FightTroops target){
		for (int i = 0 ; i < hookInfos.size() ; i++){
			AttackInfo info = hookInfos.get(i);
			if (info.getAttacker().equals(attacker) && info.getDefender().equals(target)){
				return info;
			}
		}
		return null;
	}
}
