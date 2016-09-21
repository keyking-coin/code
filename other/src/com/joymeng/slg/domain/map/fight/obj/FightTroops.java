package com.joymeng.slg.domain.map.fight.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.map.fight.obj.enumType.AttackResultType;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightBuffType;
import com.joymeng.slg.domain.map.fight.obj.enumType.FightSkillType;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.AttackInfo;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.HookAttackInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.army.data.Skill;

public class FightTroops implements Instances,Comparable<FightTroops>{
	int fightId;
	FightTroopAttribute attribute = new FightTroopAttribute();
	Side side;
	Map<String,Integer> kills = new HashMap<String,Integer>();//杀敌数
	int number;//部队数量
	int saveNum;
	int lastUnitHP;//最后剩余的血量
	boolean haveActed;//本轮是否更新过
	Position pos = new Position();//在战场中的位置
	List<FightTroops> couldAttacks = new ArrayList<FightTroops>();//我能攻击到的目标，每回合开始的时候结算一次
	List<FightBuff> buffs = new ArrayList<FightBuff>();//玩家的buff加成
	List<FightBuff> fightBuffs = new ArrayList<FightBuff>();//战斗加成，兵种自己的技能
	List<FightSkill> skills = new ArrayList<FightSkill>();
	long buildId = 0;
	List<String> records = new ArrayList<String>();
	
	public int getFightId() {
		return fightId;
	}

	public void setFightId(int fightId) {
		this.fightId = fightId;
	}

	public FightTroopAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(FightTroopAttribute attribute) {
		this.attribute = attribute;
	}

	public Side getSide() {
		return side;
	}

	public void setSide(Side side) {
		this.side = side;
	}

	public Map<String, Integer> getKills() {
		return kills;
	}

	public void addKill(String key , int kill) {
		int num = kill;
		if (kills.containsKey(key)){
			num += kills.get(key).intValue();
		}
		kills.put(key,num);
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getSaveNum() {
		return saveNum;
	}

	public void setSaveNum(int saveNum) {
		this.saveNum = saveNum;
	}

	public int getLastUnitHP() {
		return lastUnitHP;
	}

	public void setLastUnitHP(int lastUnitHP) {
		this.lastUnitHP = lastUnitHP;
	}

	public boolean isHaveActed() {
		return haveActed;
	}

	public void setHaveActed(boolean haveActed) {
		this.haveActed = haveActed;
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}

	public List<FightTroops> getCouldAttacks() {
		return couldAttacks;
	}
	
	public List<FightBuff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<FightBuff> buffs) {
		this.buffs = buffs;
	}

	public List<FightSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<FightSkill> skills) {
		this.skills = skills;
	}

	public long getBuildId() {
		return buildId;
	}

	public void setBuildId(long buildId) {
		this.buildId = buildId;
	}

	public boolean IsAttackable() {
		if (isAlive() && !haveActed) {
			return true;
		}
		return false;
	}

	public int getDieNum() {
		return saveNum - number;
	}
	
	public void initSkills(Army armyBase) {
		List<String> skillIds = armyBase.getSkill();
		for (int i = 0 ; i < skillIds.size() ; i++){
			String id = skillIds.get(i);
			if (id.equals("null")){
				continue;
			}
			Skill skill = dataManager.serach(Skill.class,id);
			if (skill == null){
				continue;
			}
			skills.add(new FightSkill(skill));
		}
	}
	
	public float getAttack(){
		float eff = getBuffEff(FightBuffType.ATTACK_EFF);
		float value = attribute.atk * (1 + eff);
		return value;
	}
	
	public float getDefence(){
		float eff = getBuffEff(FightBuffType.DEFENDE_EFF);
		float value = attribute.def * (1 + eff);
		return value;
	}
	
	public int getHit(){
		float eff = getBuffEff(FightBuffType.HIT_EFF);
		float value = attribute.acc * (1 + eff);
		return (int)value;
	}
	
	public int getCrit(){
		float eff = getBuffEff(FightBuffType.CRIT_EFF);
		float value = attribute.crit * (1 + eff);
		return (int)value;
	}
	
	public int getEvade(){
		float eff = getBuffEff(FightBuffType.EVADE_EFF);
		float value = attribute.evade * (1 + eff);
		return (int)value;
	}
	
	public float getVulnerability(){
		float eff = getBuffEff(FightBuffType.VULNERABILITY_EFF);
		return eff;
	}
	
	public float getMitigation(){
		float eff = getBuffEff(FightBuffType.MITIGATION_EFF);
		return eff;
	}
	
	public int getSpeed(){
		int base = attribute.spd;
		float eff = getBuffEff(FightBuffType.SPEED_EFF);
		int result = (int)(base + base * eff);
		return result;
	}
	
	public float getBuffEff(FightBuffType type){
		float result = 0;
		for (int i = 0 ; i < buffs.size() ; i++){
			FightBuff buff = buffs.get(i);
			if (buff.getType() == type){
				result += buff.getValue();
			}
		}
		for (int i = 0 ; i < fightBuffs.size() ; i++){
			FightBuff buff = fightBuffs.get(i);
			if (buff.getType() == type){
				result += buff.getValue();
			}
		}
		return result;
	}
	
	public FightBuff find(FightBuffType type) {
		for (int i = 0 ; i < buffs.size() ; i++){
			FightBuff buff =  buffs.get(i);
			if (buff != null && buff.getType() == type){
				return buff;
			}
		}
		return null;
	}
	
	public void addBuff(FightBuffType type ,float value){
		if (type == FightBuffType.HP_EFF){
			float max = attribute.getbHp() * value;
			int nHp = (int)Math.max(0,attribute.getHp() + max);
			attribute.setHp(nHp);
			int cHp = (int)Math.max(0,attribute.getcHp() + max);
			attribute.setcHp(cHp);
		}
		FightBuff buff = find(type);
		if (buff == null){
			buff = new FightBuff(type,value);
			buffs.add(buff);
		}else{
			buff.addValue(value);
		}
	}
	
	public void addFightBuff(FightBuff buff,boolean cover){
		FightBuff target = null;
		if (cover){//覆盖的话移除旧的buff
			for (int i = 0 ; i < fightBuffs.size();i++){
				FightBuff fb = fightBuffs.get(i);
				if (fb.getBuffId().equals(buff.getBuffId()) && fb.getSkillId().equals(buff.getSkillId())){
					target = fb;
					break;
				}
			}
		}
		if (target != null){
			target.setLastRound(buff.getLastRound());
		}else{
			fightBuffs.add(buff);
		}
		if (buff.getType() == FightBuffType.HP_EFF){//血量buff
			float max = attribute.getbHp() * buff.getValue();
			int nHp = (int)Math.max(0,attribute.getHp() + max);
			attribute.setHp(nHp);
			int cHp = (int)Math.max(0,attribute.getcHp() + max);
			attribute.setcHp(cHp);
		}
	}
	
	private boolean _remove(FightBuff buff){
		if (buff.lastRound > 0){
			buff.lastRound --;
		}
		return buff.lastRound == 0;
	}
	
	public void runBuff(){
		for (int i = 0 ; i < fightBuffs.size();){
			FightBuff buff = fightBuffs.get(i);
			if (_remove(buff)){
				fightBuffs.remove(i);
			}else{
				i++;
			}
		}
	}
	
	private List<FightSkill> getSkillsByType(FightSkillType type){
		List<FightSkill> result = new ArrayList<FightSkill>();
		for (int i = 0 ; i < skills.size() ; i++){
			FightSkill skill = skills.get(i);
			if (skill.type == type.ordinal()){
				result.add(skill);
			}
		}
		return result;
	}
	
	/**
	 * 战吼效果
	 * @param record
	 * @param team
	 * @param emenys
	 */
	public void effectPrepareStart(BattleRecord record,List<FightTroops> team , List<FightTroops> emenys){
		List<FightSkill> skills = getSkillsByType(FightSkillType.FIGHT_START);
		if (skills.size() > 0){
			for (int i = 0 ; i < skills.size() ; i++){
				FightSkill skill = skills.get(i);
				if (skill.isHappen()){
					skill.effect(record.getPrepares(),this,null,team,emenys);
				}
			}
		}
	}
	
	/**
	 * 回合开始时
	 * @param record
	 * @param team
	 * @param emenys
	 */
	public void effectRoundStart(BattleRecord record,List<FightTroops> team , List<FightTroops> emenys){
		List<FightSkill> skills = getSkillsByType(FightSkillType.ROUND_START);
		if (skills.size() > 0){
			for (int i = 0 ; i < skills.size() ; i++){
				FightSkill skill = skills.get(i);
				if (skill.isHappen()){
					skill.effect(record.getCurStartSkills(),this,null,team,emenys);
				}
			}
		}
	}
	
	/**
	 * 当我攻击时
	 * @param info
	 * @param defender
	 * @param team
	 * @param emenys
	 */
	public void effectAttack(AttackInfo info,FightTroops defender,List<FightTroops> team , List<FightTroops> emenys){
		List<FightSkill> skills = getSkillsByType(FightSkillType.ME_ATTACK);
		if (skills.size() > 0){
			for (int i = 0 ; i < skills.size() ; i++){
				FightSkill skill = skills.get(i);
				if (skill.isHappen()){
					skill.effect(info.getSkills(),this,defender,team,emenys);
				}
			}
		}
	}
	
	
	/***
	 * 当我防御时
	 * @param info
	 * @param attacker
	 * @param team
	 * @param emenys
	 */
	public void effectDefend(AttackInfo info,FightTroops attacker,List<FightTroops> team , List<FightTroops> emenys){
		List<FightSkill> skills = getSkillsByType(FightSkillType.ME_DEFEND);
		if (skills.size() > 0){
			for (int i = 0 ; i < skills.size() ; i++){
				FightSkill skill = skills.get(i);
				if (skill.isHappen()){
					skill.effect(info.getSkills(),this,attacker,team,emenys);
				}
			}
		}
	}
	
	/**
	 * 回合开始时
	 * @param record
	 * @param team
	 * @param emenys
	 */
	public void effectRoundEnd(BattleRecord record,List<FightTroops> team , List<FightTroops> emenys){
		List<FightSkill> skills = getSkillsByType(FightSkillType.ROUND_END);
		if (skills.size() > 0){
			for (int i = 0 ; i < skills.size() ; i++){
				FightSkill skill = skills.get(i);
				if (skill.isHappen()){
					skill.effect(record.getCurEndSkills(),this,null,team,emenys);
				}
			}
		}
	}

	public void damage(AttackInfo info,FightTroops traget,int finalDamage,AttackResultType type){
		int preNum = traget.getNumber();
		if (type != AttackResultType.MISS) {
			if (traget.getAttribute().getArmyType() == ArmyType.TOWNER.ordinal()){//防御建筑
				int hp = traget.getAttribute().getHp() - finalDamage;
				if (hp > 0){
					traget.getAttribute().setHp(hp);
				}else{
					traget.setNumber(0);
					traget.getAttribute().setHp(0);
				}
			}else{
				int remainHp = traget.getNumber() * traget.getAttribute().getHp() + traget.getLastUnitHP() - finalDamage;
				if (remainHp > 0) {
					int newNum = remainHp / traget.getAttribute().getHp();
					int left   = remainHp % traget.getAttribute().getHp();
					if (newNum == 0 && left > 0){
						traget.setNumber(1);
						traget.setLastUnitHP(left - traget.getAttribute().getHp());
					}else{
						traget.setNumber(newNum);
						traget.setLastUnitHP(left);
					}
				} else {
					traget.setNumber(0);
					traget.setLastUnitHP(0);
				}
			}
		}
		info.setType(type);
		int nowNum = traget.getNumber();
		info.setCasualty(traget.getAttribute().getName(),preNum - nowNum);
		info.setDamage(finalDamage);
		info.setUnitRemain(nowNum);
	}
	
	/**
	 * 部队踩陷阱逻辑
	 * @param list
	 * @param record
	 * 
	 */
	public void runHook(List<FightTroops> list,BattleRecord record) {
		int num  = number;
		if (number > 10){//陷阱数量小于10个就全部踩掉
			num  = (int)(number * FightConfig.hookUseRate);
		}
		List<FightTroops> copyList = new ArrayList<FightTroops>();
		Iterator<FightTroops> iter = list.iterator();
		while (iter.hasNext()){
			FightTroops troops = iter.next();
			if (attribute.checkAttack(troops)){
				copyList.add(troops);
			}
		}
		int round = FightConfig.hookMaxCount;//陷阱回合最多踩20次
		do{
			if (copyList.size() == 0){
				break;
			}
			int index = MathUtils.random(copyList.size());
			FightTroops troops = copyList.get(index);
			if (!troops.isAlive()){
				copyList.remove(index);
				continue;
			}
			HookAttackInfo info = new HookAttackInfo(this,troops);
			record.getHookInfos().add(info);
			int utn = Math.max(1, troops.number  > 1 ? MathUtils.random(troops.number / 2) : 1);
			int hp  = troops.attribute.getHp() * utn + troops.lastUnitHP;//目标部队还有多少hp
			float damage = computeDamage(1,troops);
			int needHook = (int)Math.min(num,hp/damage);//这个部队踩了多少个陷阱
			needHook = Math.max(1,needHook);//最少就要消耗一个陷阱
			number -= needHook;
			num    -= needHook;
			info.setAttDie(needHook);
			damage(info,troops,(int)damage * needHook,AttackResultType.HIT);
			round --;
		}while(num > 0 && round > 0 && copyList.size() > 0);
	}
	
	public float computeDamage(int num , FightTroops target){
		float atk  = getAttack() * num;
		float def  = target.getDefence();//护甲
		float immr = 1 - FightConfig.damageParam * def / (1 + FightConfig.damageParam * def);//护甲减伤比
		float att_rate = attribute.damageRates.get(target.attribute.unitType).floatValue();//伤害系数
		float att_eff = 1 + getVulnerability() - target.getMitigation();//攻击者的伤害加成 - 防守者的减伤
		float damage = Math.max(0,atk * att_eff * immr * att_rate);
		return damage;
	}
	
	public boolean isAlive(){
		return number > 0;
	}

	@Override
	public int compareTo(FightTroops o) {
		float a = o.attribute.getPower() * o.number;
		float b = attribute.getPower() * number;
		return Float.compare(a,b);
	}

	public void  clearCastRecord(){
		records.clear();
	}
	
	public void  addCastRecord(String key){
		records.add(key);
	}
	
	public boolean couldCastInThisTurn(String key) {
		for (int i = 0 ; i < records.size() ; i++){
			String sk = records.get(i);
			if (sk.equals(key)){
				return false;
			}
		}
		return true;
	}
}
