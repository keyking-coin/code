package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;

public class FightReport implements Instances{
	byte type;//0战报,1未战战报
	String time;//发生时间
	int position;//发生的坐标
	int result;//结果 0失败,1成功
	ReportTitleType title;//标题
	boolean mass;//是否是集结部队
	Side peopleSide;//我的阵营
	FightVersus people1;
	FightVersus people2;
	List<String> earnings  = new ArrayList<String>();//收益报告
	List<String> fightResult1;//people1的战果
	List<String> fightResult2;//people2的战果
	List<String> troopsEff1 = new ArrayList<String>();//people1的部队属性
	List<String> troopsEff2 = new ArrayList<String>();//people2的部队属性
	List<SkillInfo> starts;//战吼技能
	List<RoundData> rounds;//战斗详细过程
	List<String> armyInfo1;//people1的兵种战果
	List<String> armyInfo2;//people2的兵种战果
	String noBattleTip;//未开战的提示
	private RoleCityAgent city;
	
	public FightReport() {
		
	}
	
	public FightReport(RoleCityAgent city) {
		this.city = city;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public ReportTitleType getTitle() {
		return title;
	}

	public void setTitle(ReportTitleType title) {
		this.title = title;
	}

	public boolean isMass() {
		return mass;
	}

	public void setMass(boolean mass) {
		this.mass = mass;
	}

	public Side getPeopleSide() {
		return peopleSide;
	}

	public void setPeopleSide(Side peopleSide) {
		this.peopleSide = peopleSide;
	}

	public FightVersus getPeople1() {
		return people1;
	}
	
	public void setPeople1(FightVersus people1) {
		this.people1 = people1;
	}
	
	public FightVersus getPeople2() {
		return people2;
	}
	
	public void setPeople2(FightVersus people2) {
		this.people2 = people2;
	}
	
	public List<String> getEarnings() {
		return earnings;
	}
	
	public void setEarnings(List<String> earnings) {
		this.earnings = earnings;
	}
	
	public List<String> getFightResult1() {
		return fightResult1;
	}
	
	public void setFightResult1(List<String> fightResult1) {
		this.fightResult1 = fightResult1;
	}
	
	public List<String> getFightResult2() {
		return fightResult2;
	}
	
	public void setFightResult2(List<String> fightResult2) {
		this.fightResult2 = fightResult2;
	}
	
	public List<String> getTroopsEff1() {
		return troopsEff1;
	}

	public void setTroopsEff1(List<String> troopsEff1) {
		this.troopsEff1 = troopsEff1;
	}

	public List<String> getTroopsEff2() {
		return troopsEff2;
	}

	public void setTroopsEff2(List<String> troopsEff2) {
		this.troopsEff2 = troopsEff2;
	}

	public List<String> getArmyInfo1() {
		return armyInfo1;
	}

	public void setArmyInfo1(List<String> armyInfo1) {
		this.armyInfo1 = armyInfo1;
	}

	public List<String> getArmyInfo2() {
		return armyInfo2;
	}

	public void setArmyInfo2(List<String> armyInfo2) {
		this.armyInfo2 = armyInfo2;
	}

	public List<RoundData> getRounds() {
		return rounds;
	}

	public void setRounds(List<RoundData> rounds) {
		this.rounds = rounds;
	}

	public List<SkillInfo> getStarts() {
		return starts;
	}

	public void setStarts(List<SkillInfo> starts) {
		this.starts = starts;
	}

	public String getNoBattleTip() {
		return noBattleTip;
	}

	public void setNoBattleTip(String noBattleTip) {
		this.noBattleTip = noBattleTip;
	}

	public void send(ExpediteTroops expediteTroops) {
		long uid = people1.getInfo().getUid();
		if (uid == 0){
			return;
		}
		for (int i = 0 ; i < expediteTroops.getTeams().size() ; i++){
			TroopsData troops = expediteTroops.getTeams().get(i);
			if (troops.getInfo().getUid() == uid || city != null){
				troops.reportEarnings(earnings,city);
			}
		}
		String battleReport = JsonUtil.ObjectToJsonString(this);
		Role role = world.getRole(uid);
		chatMgr.creatBattleReportAndSend(battleReport,ReportType.TYPE_BATTLE_REPORT,null,role);
	}
}
