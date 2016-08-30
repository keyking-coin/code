package com.joymeng.http.handler.impl.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.http.dao.WebDB;
import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.slg.domain.map.fight.BattleField;
import com.joymeng.slg.domain.map.fight.obj.enumType.Side;
import com.joymeng.slg.domain.map.fight.result.AttackInfo;
import com.joymeng.slg.domain.map.fight.result.BattleRecord;
import com.joymeng.slg.domain.map.fight.result.SkillInfo;
import com.joymeng.slg.domain.map.impl.dynamic.ArmyEntity;
import com.joymeng.slg.domain.map.impl.dynamic.TroopsData;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.army.DefenseArmyInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.world.GameConfig;

public class HttpStartFight extends HttpHandler {
	
	@Override
	public boolean handle(HttpRequestMessage request,HttpResponseMessage response) {
		//System.out.println(1);
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		//获取攻击方数据
		String[][] attacker_str = new String[4][3];
		String[][] defender_str = new String[4][3];
		for (int i = 0 ; i < attacker_str.length ; i++){
			int index= i + 1;
			attacker_str[i][0] = request.getParameter("a_row" + index);
			attacker_str[i][1] = request.getParameter("a_level" + index);
			attacker_str[i][2] = request.getParameter("a_num" + index);
			defender_str[i][0] = request.getParameter("d_row" + index);
			defender_str[i][1] = request.getParameter("d_level" + index);
			defender_str[i][2] = request.getParameter("d_num" + index);
		}
		List<DefenseArmyInfo> buildInfos = new ArrayList<DefenseArmyInfo>();
		String buildFlag1 = request.getParameter("build1");
		String buildFlag2 = request.getParameter("build2");
		String buildFlag3 = request.getParameter("build3");
		if (buildFlag1.equals("true")){
			String buildKey = request.getParameter("b_army1");
			if (!StringUtils.isNull(buildKey)){
				String buildLevel = request.getParameter("b_level1");
				String buildHP = request.getParameter("b_hp1");
				String armyId = buildKey + "_" + buildLevel;
				Army army = getArmy(armyId);
				if (army == null){
					return false;
				}
				int hp = Integer.parseInt(buildHP);
				DefenseArmyInfo ai = new DefenseArmyInfo(armyId,1,ArmyState.ARMY_IN_NORMAL.getValue(),hp);
				ai.setArmyBase(army);
				buildInfos.add(ai);
			}
		}
		if (buildFlag2.equals("true")){
			String buildKey = request.getParameter("b_army2");
			if (!StringUtils.isNull(buildKey)){
				String buildLevel = request.getParameter("b_level2");
				String buildHP = request.getParameter("b_hp2");
				String armyId = buildKey + "_" + buildLevel;
				Army army = getArmy(armyId);
				if (army == null){
					return false;
				}
				int hp = Integer.parseInt(buildHP);
				DefenseArmyInfo ai = new DefenseArmyInfo(armyId,1,ArmyState.ARMY_IN_NORMAL.getValue(),hp);
				ai.setArmyBase(army);
				buildInfos.add(ai);
			}
		}
		if (buildFlag3.equals("true")){
			String buildKey = request.getParameter("b_army3");
			if (!StringUtils.isNull(buildKey)){
				String buildLevel = request.getParameter("b_level3");
				String buildHP = request.getParameter("b_hp3");
				String armyId = buildKey + "_" + buildLevel;
				Army army = getArmy(armyId);
				if (army == null){
					return false;
				}
				int hp = Integer.parseInt(buildHP);
				DefenseArmyInfo ai = new DefenseArmyInfo(armyId,1,ArmyState.ARMY_IN_NORMAL.getValue(),hp);
				ai.setArmyBase(army);
				buildInfos.add(ai);
			}
		}
		int hookCount = Integer.parseInt(request.getParameter("hookCount"));
		String hookFlag = request.getParameter("hooks");
		List<ArmyInfo> has = new ArrayList<ArmyInfo>();
		if (hookFlag.equals("true")){
			int count = 1;
			while (count <= hookCount){
				String buildKey = request.getParameter("h_army" + count);
				if (!StringUtils.isNull(buildKey)){
					String buildLevel = request.getParameter("h_level" + count);
					String buildNum = request.getParameter("h_num" + count);
					String armyId = buildKey + "_" + buildLevel;
					Army army = getArmy(armyId);
					if (army == null){
						return false;
					}
					int num = Integer.parseInt(buildNum);
					ArmyInfo ai = new ArmyInfo(armyId,num,ArmyState.ARMY_IN_NORMAL.getValue());
					ai.setArmyBase(army);
					has.add(ai);
				}
				count++;
			}
		}
		TroopsData attacker = new TroopsData();
		TroopsData defender = new TroopsData();
		for (int i = 0 ; i < attacker_str.length ; i++){
			if (!attacker_str[i][0].equals("none")){
				String attacker_army_key = attacker_str[i][0] + "_" + attacker_str[i][1];
				ArmyEntity entity_a = create(response,attacker_army_key,attacker_str[i][2]);
				if (entity_a != null){
					int row = i + 1;
					entity_a.setPos(row + ",1");
					attacker.getArmys().add(entity_a);
				}else{
					return false;
				}
			}
			if (!defender_str[i][0].equals("none")){
				String defender_army_key = defender_str[i][0] + "_" + defender_str[i][1];
				ArmyEntity entity_d = create(response,defender_army_key,defender_str[i][2]);
				if (entity_d != null){
					int row = i + 1;
					entity_d.setPos(row + ",1");
					defender.getArmys().add(entity_d);
				}else{
					return false;
				}
			}
		}
		String fightStr = request.getParameter("fightNum");
		int fightNum = 1;
		if (!StringUtils.isNull(fightStr)){
			fightNum = Integer.parseInt(fightStr);
		}
		List<BattleRecord> records = new ArrayList<BattleRecord>();
		while (fightNum > 0){
			BattleField battle = new BattleField();
			battle.add(attacker,Side.ATTACK);
			battle.add(defender,Side.DEFENSE);
			for (int i = 0 ; i < buildInfos.size() ; i++){//城防单位
				DefenseArmyInfo daf = buildInfos.get(i);
				battle.add(daf,Side.DEFENSE);
			}
			for (int i = 0 ; i < has.size() ; i++){//陷阱单位
				ArmyInfo ai = has.get(i);
				battle.add(ai,Side.DEFENSE);
			}
			try {
				BattleRecord record = battle.startFight();
				records.add(record);
			} catch (Exception e) {
				response.appendBody("{\"result\":\"战斗发生异常\"}");
				e.printStackTrace();
				return false;
			}
			fightNum --;
		}
		if (records.size() > 0){
			Map<String,Object> results = new HashMap<String,Object>();
			results.put("result","ok");
			StringBuffer sb = new StringBuffer();
			if (records.size() > 1){
				int aNum = 0,dNum=0,pNum=0;
				for (int i = 0 ; i < records.size() ; i++){
					BattleRecord record = records.get(i);
					Side winner = record.GetWinner();
					if (winner == null){
						pNum ++;
					}else if (winner.ordinal() == Side.ATTACK.ordinal()){
						aNum ++;
					}else{
						dNum ++;
					}
				}
				sb.append("平局发生了  : " + pNum + "次<br>");
				sb.append("攻击方胜利了  : " + aNum + "次<br>");
				sb.append("防御方胜利了  : " + dNum + "次<br>");
			}else{
				BattleRecord record = records.get(0);
				String winName = record.GetWinner() == null ? Side.DEFENSE.toString() : record.GetWinner().toString();
				sb.append("胜利者是  : " + winName + "<br>");
				//陷阱回合
				List<AttackInfo> his = record.getHookInfos();
				if (his.size() > 0){
					sb.append("陷阱回合开始<br>");
					for (int i = 0 ; i < his.size() ; i++){
						AttackInfo ai = his.get(i);
						sb.append(ai.toString() + "<br>");
					}
					sb.append("陷阱回合结束<br>");
				}
				
				List<List<AttackInfo>> roundDatas = record.getRoundList();
				for (int i = 0 ; i < roundDatas.size() ; i++){
					int round = i + 1;
					sb.append("第" + round + "回合<br>");
					List<SkillInfo> skills = record.getCurStartSkills(i);
					if (skills.size() > 0){
						sb.append("回合开始技能开始:<br>");
						for (int j = 0 ; j < skills.size() ; j++){
							SkillInfo skill = skills.get(j);
							sb.append(skill.toString() + "<br>");
						}
						sb.append("回合开始技能结束:<br>");
					}
					List<AttackInfo> infos = roundDatas.get(i);
					for (int j = 0 ; j < infos.size() ; j++){
						AttackInfo info = infos.get(j);
						for (int k = 0 ; k < info.getSkills().size() ; k++){
							SkillInfo skill = info.getSkills().get(k);
							sb.append(skill.toString() + "<br>");
						}
						sb.append(info.toString() + "<br>");
					}
					skills = record.getCurEndSkills(i);
					if (skills.size() > 0){
						sb.append("回合结束技能开始:<br>");
						for (int j = 0 ; j < skills.size() ; j++){
							SkillInfo skill = skills.get(j);
							sb.append(skill.toString() + "<br>");
						}
						sb.append("回合结束技能结束:<br>");
					}
				}
			}
			results.put("infos",sb.toString());
			String str = JsonUtil.ObjectToJsonString(results);
			response.appendBody(str);
		}
		return false;
	}
	
	private Army getArmy(String key){
		Army army = null;
		try {
			if (GameConfig.USE_DATA_EDIT_DB){
				army = WebDB.getInstance().getArmyDao().search(key);
			}else{
				army = dataManager.serach(Army.class,key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return army;
	}
	
	private ArmyEntity create(HttpResponseMessage response,String key,String num){
		ArmyEntity entity = new ArmyEntity();
		Army army = getArmy(key);
		if (army == null){
			response.appendBody("{\"result\":\"未找到army是：" + key + "的固化数据\"}");
			return null;
		}
		entity.setPos(army.getLocation());
		entity.setKey(key);
		entity.setSane(Integer.parseInt(num));
		entity.setTemp(army);
		return entity;
	}
}
