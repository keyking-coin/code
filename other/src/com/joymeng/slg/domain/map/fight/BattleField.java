
package com.joymeng.slg.domain.map.fight;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.MathUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.fight.obj.FightBuff;
import com.joymeng.slg.domain.map.fight.obj.FightConfig;
import com.joymeng.slg.domain.map.fight.obj.FightTroops;
import com.joymeng.slg.domain.map.fight.obj.PriorityQ;
import com.joymeng.slg.domain.map.fight.obj.enumType.ArmyType;
import com.joymeng.slg.domain.map.fight.obj.enumType.AttackResultType;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.AttackInfo;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.FightReport;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.ExpediteTroops;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.DefenseArmyInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.world.GameConfig;

public class BattleField implements Instances{	 
    int idIndex = 0;
    List<FightTroops> hooks = new ArrayList<FightTroops>();//陷阱列表
    List<FightTroops> allTroopses = new ArrayList<FightTroops>();
    public static boolean loggerFlag = false;
    public static boolean attackWin = false;
    PriorityQ actionQueue = new PriorityQ();//出手队列
    
    public static FightTroops create(Army army,int num,Side side){
    	FightTroops troops = new FightTroops();
    	troops.getAttribute().init(army);
    	troops.getPos().init(army.getLocation());
    	troops.setNumber(num);
    	troops.setSaveNum(num);
    	troops.setSide(side);
    	troops.initSkills(army);
    	return troops;
    }
    
    public void add(TroopsData troopsData ,Side side) {
		for (int i = 0 ; i < troopsData.getArmys().size() ; i++){
			ArmyEntity entity = troopsData.getArmys().get(i);
    		entity.setId(-1);
    		if (entity.getSane() > 0){
    			add(entity,side);
    		}
    	}
	}
    
    public void add(ExpediteTroops expedite){
    	List<TroopsData> attackers = expedite.getTeams();
		for (int i = 0 ; i < attackers.size() ; i++){
			TroopsData troops = attackers.get(i);
			if (troops.couldFight()){
				add(troops,Side.ATTACK);
			}
		}
    }
    
    private void add(ArmyEntity entity,Side side) {
    	Army army = entity.getTemp();
    	if (army == null){
    		army = dataManager.serach(Army.class,entity.getKey());
    	}
    	entity.setId(idIndex);
    	FightTroops troops = create(army,entity.getSane(),side);
		troops.setFightId(idIndex);
    	troops.getPos().init(entity.getPos());
    	allTroopses.add(troops);
    	idIndex ++;
    }
    
	public void add(ArmyInfo armyInfo , Side side){
    	Army army = armyInfo.getArmyBase();
    	FightTroops troops = create(army,armyInfo.getArmyNum(),side);
    	troops.setFightId(idIndex);
    	if (armyInfo instanceof DefenseArmyInfo){
    		DefenseArmyInfo dai = (DefenseArmyInfo)armyInfo;
    		troops.getAttribute().setHp(dai.getHp());
    		troops.setBuildId(dai.getBuildId());
    		byte nRow = (byte)(troops.getPos().getRow() - 5);
    		troops.getPos().setRow(nRow);//防御建筑的位置重新设置
    	}
    	if (army.getArmyType() == ArmyType.HOOK.ordinal()){
    		hooks.add(troops);
		}else{
	    	allTroopses.add(troops);
		}
    	idIndex++;
    }
	
    public boolean IsBattleEnd() {
		boolean flagA = true, flagB = true;
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops fightTroops = allTroopses.get(i);
			if ((fightTroops.getNumber() > 0 && fightTroops.getAttribute().getArmyType() < ArmyType.HOOK.ordinal()) ||
				(fightTroops.getNumber() == 0 && fightTroops.getLastUnitHP() > 0)) {
				if (fightTroops.getSide() == Side.ATTACK){
					flagA = false;
				}
				if (fightTroops.getSide() == Side.DEFENSE){
					flagB = false;
				}
			}
		}
		return flagA | flagB;
	}
	
	private AttackInfo tryToComputeAttack(FightTroops attcker) {
		FightTroops target = selectTarget(attcker);
		return calculateFight(attcker,target);
	}
    
	private AttackInfo calculateFight(FightTroops t1, FightTroops t2) {
		t1.setHaveActed(true);
		if (t2 == null || !t2.isAlive()) {
			return null;
		}
		if (loggerFlag){
			GameLog.info(t1.getSide() +  "_" + t1.getAttribute().getcName() + "(" + t1.getPos().getRow() + ") could attack  {");
			for (int i = 0 ; i < t1.getCouldAttacks().size() ; i++){
				FightTroops t = t1.getCouldAttacks().get(i);
				GameLog.info("    " + t.getSide() + "_" + t.getAttribute().getcName() + "(" + t.getPos().getRow() + ")");
			}
			GameLog.info("}");
		}
		List<FightTroops> troopes1 = getTroopses(t1.getSide());//友方
		List<FightTroops> troopes2 = getTroopses(t1.getSide() == Side.ATTACK ? Side.DEFENSE : Side.ATTACK);//敌方
		AttackInfo info = new AttackInfo(t1,t2);
		t1.effectAttack(info,t2,troopes1,troopes2);
		t2.effectDefend(info,t1,troopes2,troopes1);
		float  ar  = t2.getEvade() * 100.0f / (t1.getHit() + t2.getEvade());//我方命中率
		double iar = Math.ceil(ar);
		int finalDamage = 0;
		int attRate = MathUtils.random(100);
		AttackResultType art = null;
		if (attRate > iar) {//命中目标
			float dpt = t1.computeDamage(t1.getNumber(),t2);
			int cr = t1.getCrit();
			if (MathUtils.random(100) >= cr) {
				finalDamage = (int) (Math.round((dpt)));
				art =  AttackResultType.HIT;
			} else {//暴击
				finalDamage = (int) (Math.round(dpt * FightConfig.criticalDamageRatio));
				art =  AttackResultType.CRIT;
			}
			if (finalDamage < 1) {
				art =  AttackResultType.GRAZ;
			}
		} else {
			art = AttackResultType.MISS;
		}
		t1.damage(info,t2,finalDamage,art);
		if (!t2.isAlive()){//目标已阵亡
			reSort(null,false,false,false);
		}
		if (loggerFlag){
			String str = info.toString().replaceAll("&nbsp;"," ");
			GameLog.info(str);
		}
		return info;
	}

	/**
	 * 选择战斗目标
	 * @param t
	 * @param temp
	 * @param targetType
	 * @return
	 */
	private FightTroops selectTarget(FightTroops attacker) {
		int size = attacker.getCouldAttacks().size();
		if (!attacker.isAlive() ||  size == 0){
			return null;
		}
		int[] rates  = new int[size];
		FightTroops[] targets = new FightTroops[size];
		for (int i = 0 ; i < size ; i++){
			FightTroops target = attacker.getCouldAttacks().get(i);
			byte bt    = target.getAttribute().getBoiType();
			int rate   = attacker.getAttribute().getRadoms().get(bt).intValue();
			targets[i] = target;
			rates[i]   = rate;
		}
		FightTroops target = MathUtils.getRandomObj(targets,rates);
		return target;
	}
	
	public Side GetWinner() {
		List<FightTroops> attackers = getTroopses(Side.ATTACK);
		List<FightTroops> defensers  = getTroopses(Side.DEFENSE);
		boolean flag1 = false , flag2 = false;
		for (int i = 0 ; i < attackers.size() ; i++){
			FightTroops fightTroops = attackers.get(i);
			if (fightTroops.isAlive()){
				flag1 = true;
				break;
			}
		}
		for (int i = 0 ; i < defensers.size() ; i++){
			FightTroops fightTroops = defensers.get(i);
			if (fightTroops.isAlive() && fightTroops.getAttribute().getArmyType() < ArmyType.HOOK.ordinal()){
				flag2 = true;
				break;
			}
		}
		if (attackWin){
			for (int i = 0 ; i < defensers.size() ; i++){
				FightTroops fightTroops = defensers.get(i);
				fightTroops.setNumber(0);
			}
			return Side.ATTACK;
		}
		if (flag1 && !flag2){
			return Side.ATTACK;
		}else if (flag2 && !flag1){
			return Side.DEFENSE;
		}else{
			return null;
		}
	}
	
	private void reSort(BattleRecord record,boolean act,boolean runBuff,boolean runSkill){
		adjustPosition();
		List<FightTroops> attackers = getAliveTroopses(Side.ATTACK);
		List<FightTroops> defenders = getAliveTroopses(Side.DEFENSE);
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops troops = allTroopses.get(i);
			if (!troops.isAlive()){
				continue;
			}
			List<FightTroops> attacks = troops.getCouldAttacks();
			attacks.clear();
			boolean isAttacker = troops.getSide() == Side.ATTACK;
			if (runBuff){
				troops.runBuff();
			}
			if (act){
				troops.setHaveActed(false);
				actionQueue.Push(troops);
			}
			if (runSkill){
				troops.effectRoundStart(record,isAttacker ? attackers : defenders, isAttacker ? defenders : attackers);
			}
			int attackRange = troops.getAttribute().getRange();//我能攻击到的排数
			List<FightTroops> enemys = isAttacker ? defenders : attackers;
			for (int j = 0 ; j < enemys.size() ; j++){
				FightTroops enemy = enemys.get(j);
				if (enemy.getPos().getRow() <= attackRange && troops.getAttribute().checkAttack(enemy)){
					attacks.add(enemy);
				}
			}
		}
	}
	
	private void roundStart(BattleRecord record) throws Exception {
		record.startNewRound();
		reSort(record,true,true,true);
	}
	
	public BattleRecord startFight() throws Exception {
		BattleRecord record = new BattleRecord();
		prepare(record);
		while (!IsBattleEnd() && record.couldContinue()) {
			if (loggerFlag){
				GameLog.info("第" + (record.getRound() + 1) + "回合");
			}
			roundStart(record);
			roundLogic(record);
			roundEnd(record);
		}
		record.SetWinner(GetWinner());
		return record;
	}

	
	/**
	 * 战斗开始前
	 */
	private void prepare(BattleRecord record) {
		List<FightTroops> attackers = getTroopses(Side.ATTACK);
		List<FightTroops> defenders = getTroopses(Side.DEFENSE);
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops troops = allTroopses.get(i);
			boolean isAttacker = troops.getSide() == Side.ATTACK;
			troops.effectPrepareStart(record,isAttacker ? attackers : defenders, isAttacker ? defenders : attackers);
		}
		//陷阱逻辑
		for (int i = 0 ; i < hooks.size() ; i++){
			FightTroops troops = hooks.get(i);
			troops.runHook(troops.getSide() == Side.ATTACK ? defenders : attackers,record);
		}
	}

	private void roundEnd(BattleRecord record) {
		List<FightTroops> attackers = getAliveTroopses(Side.ATTACK);
		List<FightTroops> defenders = getAliveTroopses(Side.DEFENSE);
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops troops = allTroopses.get(i);
			if (troops.getNumber() <= 0){
				continue;
			}
			boolean isAttacker = troops.getSide() == Side.ATTACK;
			troops.effectRoundEnd(record,isAttacker ? attackers : defenders, isAttacker ? defenders : attackers);
		}
		record.round();
	}

	private void roundLogic(BattleRecord record) throws Exception {
		List<AttackInfo> infos = record.getCurInfos();
		actionQueue.sort();
		while (!IsBattleEnd()) {
			FightTroops troops = actionQueue.Pop();
			if (troops == null) {
				break;
			}
			if (troops.IsAttackable()) {
				AttackInfo info = tryToComputeAttack(troops);
				if (info != null) {
					infos.add(info);
				}
			}
		}
	}

	public List<FightTroops> getHooks() {
		return hooks;
	}

	public List<FightTroops> getTroopses(Side side) {
		List<FightTroops> result = new ArrayList<FightTroops>();
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops fightTroops = allTroopses.get(i);
			if (fightTroops.getSide() == side){
				result.add(fightTroops);
			}
		}
		return result;
	}
	
	public List<FightTroops> getAliveTroopses(Side side) {
		List<FightTroops> result = new ArrayList<FightTroops>();
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops fightTroops = allTroopses.get(i);
			if (fightTroops.isAlive() && fightTroops.getSide() == side){
				result.add(fightTroops);
			}
		}
		return result;
	}
	
	public List<FightTroops> getAliveTroopses(Side side,int row) {
		List<FightTroops> result = new ArrayList<FightTroops>();
		for (int i = 0 ; i < allTroopses.size() ; i++){
			FightTroops fightTroops = allTroopses.get(i);
			if (fightTroops.isAlive() && fightTroops.getSide() == side && fightTroops.getPos().getRow() == row){
				result.add(fightTroops);
			}
		}
		return result;
	}
	
	/**
	 * 每回合开始的时候调整各部队的位置
	 */
	public void adjustPosition(){
		for (byte i = 1 ; i <= GameConfig.BATTLE_FIELD_ROW ; i++){
			List<FightTroops> attackers = getAliveTroopses(Side.ATTACK,i);
			if (attackers.size() == 0){//前排的人死光了
				attackers = getAliveTroopses(Side.ATTACK,i+1);
				for (int j = 0 ; j < attackers.size() ; j++){
					FightTroops troops = attackers.get(j);
					troops.getPos().setRow(i);
				}
			}
			List<FightTroops> defenders = getAliveTroopses(Side.DEFENSE,i);
			if (defenders.size() == 0){//前排的人死光了
				defenders = getAliveTroopses(Side.DEFENSE,i+1);
				for (int j = 0 ; j < defenders.size() ; j++){
					FightTroops troops = defenders.get(j);
					troops.getPos().setRow(i);
				}
			}
		}
	}

	public void reportEff(FightReport report , Side side) {
		List<String> lis = report.getTroopsEff1();
		List<FightTroops> troopses = getTroopses(side);
		if (side == Side.DEFENSE){
			troopses.addAll(hooks);
		}
		insert(lis,troopses);
		lis = report.getTroopsEff2();
		troopses = getTroopses(side == Side.ATTACK ? Side.DEFENSE : Side.ATTACK);
		if (side == Side.ATTACK){
			troopses.addAll(hooks);
		}
		insert(lis,troopses);
	}
	
	private void insert(List<String> lis,List<FightTroops> troopses){
		int total = 0;
		for (int i = 0 ; i < troopses.size() ; i++){
			FightTroops troops = troopses.get(i);
			total += troops.getSaveNum() * troops.getAttribute().getSpace();
			for (int j = 0 ; j < troops.getBuffs().size() ; j++){
				FightBuff buff = troops.getBuffs().get(j);
				if (buff.getValue() == 0){
					continue;
				}
				String str = troops.getAttribute().getName() + "|" + buff.getType().toString() + "|" + buff.getValue();
				lis.add(str);
			}
		}
		lis.add("total|" + total);
	}
}
