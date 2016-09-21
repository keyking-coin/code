package com.joymeng.slg.domain.map.fight.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;

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
	List<String> earnings  = new ArrayList<String>();
	List<String> fightResult1;//people1的战果
	List<String> fightResult2;//people2的战果
	List<String> troopsEff1 = new ArrayList<String>();//people1的部队属性
	List<String> troopsEff2 = new ArrayList<String>();//people2的部队属性
	List<SkillInfo> starts;//战吼技能
	List<RoundData> rounds;//战斗详细过程
	List<String> armyInfo1;//people1的兵种战果    （兵种|存活|损失|受伤|消灭  ****  monster_1_1_0|103|147|0|70 ）
	List<String> armyInfo2;//people2的兵种战果   （兵种|存活|损失|受伤|消灭  ****  monster_1_1_0|103|147|0|70 ）
	String noBattleTip;//未开战的提示
	private RoleCityAgent city;
	Map<String,Integer> myKills = new HashMap<String,Integer>();//我击杀的对象
	
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
	
	public void addMyKills(Map<String,Integer> maps){
		if(maps==null)
			return;
		for(String key:maps.keySet()){
			if(myKills.get(key) == null){
				myKills.put(key, maps.get(key));
			}else{
				myKills.put(key, maps.get(key)+myKills.get(key));
			}
		}
	}
	
	
	/**
	 * 计算联盟积分
	* @Title: reportAllianceScore 
	* @return void
	 */
	public int[] reportAllianceScore(){
		//玩家
		long uid = people1.getInfo().getUid();
		Role role = world.getRole(uid);
		if(role == null || role.getUnionId() <=0){
			GameLog.info("<reportAllianceScore>uid="+uid+":玩家不存在或者没有联盟");
			return null;
		}
		UnionBody union = unionManager.search(role.getUnionId());
		if(union == null){
			GameLog.info("<reportAllianceScore>union="+role.getUnionId()+":联盟不存在");
			return null;
		}
		UnionMember member = union.getUnionMemberById(uid);
		if(member == null){
			GameLog.info("<reportAllianceScore>member="+uid+":联盟成员不不存在");
			return null;
		}
		GameLog.info("reportAllianceScore----"+JsonUtil.ObjectToJsonString(myKills));
		int[] allians = new int[]{0,0};
		//计算我的贡献和联盟贡献
		for(String key : myKills.keySet()){
			//找到对应部队和数量
			Army armyBase = dataManager.serach(Army.class, key);
			if(armyBase == null)
				continue;
			List<String> alliancescore = armyBase.getAlliancescore();
			int num = myKills.get(key);
			if(alliancescore!= null && alliancescore.size()>1){
				allians[0] += Integer.parseInt(alliancescore.get(0))*num;
				allians[1] += Integer.parseInt(alliancescore.get(1))*num;
			}
		}
		member.addUnionMemberScore(allians[0]);
		union.setScore(union.getScore()+allians[1]);
		GameLog.info("<reportAllianceScore>myscore="+allians[0]+"|union="+allians[1]);
		earnings.add("union|" + allians[0] + "|" + allians[1] + "|0" );
		return allians;
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
		if(title != ReportTitleType.TITLE_TYPE_D_MONSTER)//去掉怪物攻城
			reportAllianceScore();
		String battleReport = JsonUtil.ObjectToJsonString(this);
		Role role = world.getRole(uid);
		chatMgr.creatBattleReportAndSend(battleReport,ReportType.TYPE_BATTLE_REPORT,null,role);
	}
}
