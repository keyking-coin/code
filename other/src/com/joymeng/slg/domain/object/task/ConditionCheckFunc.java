package com.joymeng.slg.domain.object.task;

import java.util.List;

import com.joymeng.Instances;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.build.RoleBuild;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;

public class ConditionCheckFunc implements Instances{
	
	public static boolean checkBuildLvlup(RoleMission mission, String buildId, int level) {
		List<String> conditions = mission.getCondition();
		int condLevel = Integer.parseInt(conditions.get(1));
		
		if (buildId.equals(conditions.get(0))) {
			if(mission.getSchedule() >= level){
				return false;
			}
			mission.setSchedule(level);
			if (level >= condLevel) {
				mission.setAwardStatus((byte) 1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkBuildsLvlNum(RoleMission mission, String buildId, List<RoleBuild> builds) {
		List<String> conditions = mission.getCondition();
		int condLevel = Integer.parseInt(conditions.get(1));
		int condNum = Integer.parseInt(conditions.get(2));
		int count = 0;
		if (buildId.equals(conditions.get(0))) {
			for(RoleBuild build : builds){
				if(build.getLevel() >= condLevel){
					count ++;
				}
			}
			mission.setSchedule(count);
			if (mission.getSchedule() >= condNum) {
				mission.setAwardStatus((byte) 1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkBuildsNum(RoleMission mission, List<RoleBuild> builds) {
		List<String> conditions = mission.getCondition();
		int condLevel = Integer.parseInt(conditions.get(0));
		int condNum = Integer.parseInt(conditions.get(1));
		int count = 0;
		for(RoleBuild build : builds){
			if(build.getLevel() >= condLevel){
				count ++;
			}
		}
		mission.setSchedule(count);
		if (mission.getSchedule() >= condNum) {
			mission.setAwardStatus((byte) 1);
			return true;
		}
		return false;
	}
	
	public static boolean checkTechLvlup(RoleMission mission, String techId, int level){
		List<String> conditions = mission.getCondition();
		int condLevel = Integer.parseInt(conditions.get(1));
		
		if(techId.equals(conditions.get(0))){
			mission.setSchedule(level);
			if(level >= condLevel){
				mission.setAwardStatus((byte)1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkTechMaxNum(RoleMission mission) {
		List<String> conditions = mission.getCondition();
		int condNum = Integer.parseInt(conditions.get(0));

		mission.setSchedule(mission.getSchedule() + 1);
		if (mission.getSchedule() >= condNum) {
			mission.setAwardStatus((byte) 1);
			return true;
		}
		return false;
	}
	
	public static boolean checkSkillLvlup(RoleMission mission, String techId) {
		List<String> conditions = mission.getCondition();
		
		if(techId.equals(conditions.get(0))){
			mission.setAwardStatus((byte) 1);
			return true;
		}
		return false;
	}
	
	public static boolean checkArmyTrainType(RoleMission mission, String armyId, int num){
		List<String> conditions = mission.getCondition();
		int condNum = Integer.parseInt(conditions.get(1));
		Army army = dataManager.serach(Army.class, armyId);
		String[] types = conditions.get(0).split(":");
		if(types.length < 2){
			return false;
		}
		int typeId = Integer.parseInt(types[1]);
		byte armyTypeId = 0; 
		switch(types[0]){
		case "soldiersType":
			armyTypeId = army.getSoldiersType();
			break;
		case "armyType":
			armyTypeId = army.getArmyType();
			break;
		default:
			return false;
		}
		if(armyTypeId == typeId){
			mission.setSchedule(mission.getSchedule() + num);
			if(mission.getSchedule() >= condNum){
				mission.setAwardStatus((byte) 1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkTaskVIP(RoleMission mission, int level){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condLevel = Integer.parseInt(condArray[1]);
		mission.setSchedule(level);
		if(level >= condLevel){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}
	
	public static boolean checkTaskResProduction(RoleMission mission, ResourceTypeConst resType, int num){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condNum = Integer.parseInt(condArray[2]);
		if(resType.getKey().equals(condArray[1])){
			mission.setSchedule(num);
			if(mission.getSchedule()/* + num*/ >= condNum){
				mission.setAwardStatus((byte)1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkTaskRoleLevel(RoleMission mission, int level){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condLevel = Integer.parseInt(condArray[1]);
		mission.setSchedule(level);
		if(level >= condLevel){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}

	public static boolean checkTaskHealSold(RoleMission mission, List<ArmyInfo> curArmys){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condNum = Integer.parseInt(condArray[2]);
		
		for(ArmyInfo army : curArmys){
			if(army.getArmyId().equals(condArray[1])){
				if(mission.getSchedule() + army.getArmyNum() >= condNum){
					mission.setAwardStatus((byte)1);
					mission.setSchedule(condNum);
					return true;
				}else{
					mission.setSchedule(mission.getSchedule() + army.getArmyNum());
				}
			}
		}
		return false;
	}
	
	public static boolean checkTaskKillMonster(RoleMission mission, String monsterId){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condNum = Integer.parseInt(condArray[2]);
		if(monsterId.equals(condArray[1])){
			mission.setSchedule(mission.getSchedule() + 1);
			if(mission.getSchedule() + 1 >= condNum){
				mission.setAwardStatus((byte)1);
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkTaskAttackOthers(RoleMission mission){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condNum = Integer.parseInt(condArray[1]);
		mission.setSchedule(mission.getSchedule() + 1);
		if(mission.getSchedule() + 1 >= condNum){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}
	
	public static boolean checkTaskCompleteTimes(RoleMission mission){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condNum = Integer.parseInt(condArray[1]);
		mission.setSchedule(mission.getSchedule() + 1);
		if(mission.getSchedule() + 1 >= condNum){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}

	public static boolean checkTaskAccomplishNum(RoleMission mission, int size){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condLevel = Integer.parseInt(condArray[1]);
		mission.setSchedule(size);
		if(size >= condLevel){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}
	
	public static boolean checkTaskOnline(){
		
		return false;
	}
	
	public static boolean checkTaskAllianceHelp(){
		
		return false;
	}
	
	public static boolean checkAllianceLevel(RoleMission mission, int level){
		List<String> conditions = mission.getCondition();
		if(conditions == null || conditions.size() == 0){
			GameLog.error("the task " + mission.getMissionId() + " conditions not found!");
			return false;
		}
		String condition = mission.getCondition().get(0);
		String newStrCondition = condition.replaceAll("'", "");
		newStrCondition = newStrCondition.replace(")", "");
		String[] condArray = newStrCondition.split(",");
		int condLevel = Integer.parseInt(condArray[1]);
		mission.setSchedule(level);
		if(level >= condLevel){
			mission.setAwardStatus((byte)1);
			return true;
		}
		return false;
	}

}
