package com.joymeng.slg.domain.object.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.event.GameEvent;
import com.joymeng.slg.domain.object.army.ArmyInfo;
import com.joymeng.slg.domain.object.army.ArmyState;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.resource.ResourceTypeConst;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.RoleTaskType.TaskConditionType;
import com.joymeng.slg.domain.object.task.data.Task2;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class MissionManager implements Instances{
	Map<String, List<RoleMission>>  roleMissionMap = new HashMap<String, List<RoleMission>>();
	List<String> completedList = new ArrayList<String>();//已领取任务编号列表
	long uid;
	String curIndex = "1";//已领取任务完成进度奖励的任务的索引
	int schedule=0;//
	
	public Map<String, List<RoleMission>> getRoleMissionMap() {
		return roleMissionMap;
	}

	public List<String> getDeadMap() {
		return completedList;
	}

	public MissionManager(){
	}
	public void setUid(long uid, String taskState){
		this.uid = uid;
		if(taskState == null || taskState.equals("")){
			return;
		}
		String[] strArray = taskState.split(":");
		if(strArray.length == 2){
			curIndex = strArray[0];
			schedule = Integer.parseInt(strArray[1]);
		}
	}
	public String getCurIndex() {
		return curIndex;
	}
	public void setCurIndex(String curIndex) {
		this.curIndex = curIndex;
	}
	public int getSchedule() {
		return schedule;
	}
	public void setSchedule(int shcedule) {
		this.schedule = shcedule;
	}
	
	public RoleMission createMission(String missionId, int branchId, String mainType, List<String> conditions, int conditionId){
		RoleMission mission = new RoleMission(uid);
		mission.setMissionId(missionId);
		mission.setType(mainType);
		mission.setCondition(conditions);
		mission.setBranchId(branchId);
		mission.setAwardStatus((byte)0);
		mission.setSchedule(0);
		mission.setConditionId(conditionId);
		mission.setUpdateTime(TimeUtils.nowLong()/1000);
		return mission;
	}
	
	public void initMissions(Role role){
		if(roleMissionMap.size() > 0){
			return;
		}
		String taskId = "Main_1_1_1";
		Task2 task = dataManager.serach(Task2.class, taskId);
		RoleMission mission = createMission(taskId, task.getBranchID(), task.getMainID(), task.getCompleteConditon(), task.getDesignid());
		long keyId = keyData.key(DaoData.TABLE_RED_ALERT_MISSION);
		mission.setId(keyId);
		mission.checkTaskState(role);
		List<RoleMission> taskList = roleMissionMap.get(task.getMainID());
		if(taskList == null){
			taskList = new ArrayList<RoleMission>();
		}
		taskList.add(mission);
		roleMissionMap.put(task.getMainID(), taskList);
		// 接任务
		LogManager.taskLog(role, (byte)0,task.getMainID(), task.getId(), true);
	}
	
	public void loadMissionsFromDB(SqlData data){
		long id = data.getLong(DaoData.RED_ALERT_GENERAL_ID);
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		String missionId = data.getString(DaoData.RED_ALERT_MISSION_ID);
		int branchId = data.getInt(DaoData.RED_ALERT_MISSION_TYPE);
		int designId = data.getInt(DaoData.RED_ALERT_MISSION_CONDTYPE);
		String mainType = data.getString(DaoData.RED_ALERT_MISSION_UNIONTYPE);
		String conditions = data.getString(DaoData.RED_ALERT_MISSION_CONDITION);
		decodeConditions(conditions);
		int schedule = data.getInt(DaoData.RED_ALERT_MISSION_SCHEDULE);
		byte state = data.getByte(DaoData.RED_ALERT_MISSION_STATE);
		long time = data.getLong(DaoData.RED_ALERT_MISSION_UPDATETIME);
		RoleMission mission = createMission(missionId, branchId, mainType, decodeConditions(conditions), designId);
		mission.setId(id);
		mission.setAwardStatus(state);
		mission.setSchedule(schedule);
		mission.setUpdateTime(time);
		if(state == 2){
			completedList.add(missionId);
		}else{
//			if(state == 0){
//				TaskConditionType type = TaskConditionType.valueof(designId);
//				if(type.getType() == Const.TASK_COND_TYPE_COUNTRY || type.getType() == Const.TASK_COND_TYPE_UNION){
//					mission.checkTaskState();
//					//TODO
//				}
//			}
			List<RoleMission> missionLst = roleMissionMap.get(mainType);
			if(missionLst == null){
				missionLst = new ArrayList<RoleMission>();
			}
			missionLst.add(mission);
			roleMissionMap.put(mainType, missionLst);
		}
	}
	
	public void serialize(SqlData data){
		JoyBuffer out = JoyBuffer.allocate(8192);
		out.putInt(roleMissionMap.size());
		for(List<RoleMission> missions : roleMissionMap.values()){
			out.putInt(missions.size());
			for(RoleMission mission : missions){
				out.putPrefixedString(mission.getMissionId(),JoyBuffer.STRING_TYPE_SHORT);
				out.putInt(mission.getBranchId());
				out.putInt(mission.getConditionId());
				out.putPrefixedString(mission.getType(),JoyBuffer.STRING_TYPE_SHORT);
				String condition = encodeConditions(mission);
				out.putPrefixedString(condition,JoyBuffer.STRING_TYPE_SHORT);
				out.putInt(mission.getSchedule());
				out.put(mission.getAwardStatus());
				out.putLong(mission.getUpdateTime());
			}
		}
		data.put(DaoData.RED_ALERT_ROEL_MISSIONS, out.arrayToPosition());
	}
	
	public void deserialize(Object data){
		if(data == null){
			return;
		}
		JoyBuffer buffer = JoyBuffer.wrap((byte[])data);
		int size = buffer.getInt();
		for(int i = 0; i < size; i++){
			int sizeSt = buffer.getInt();
			for(int j = 0; j < sizeSt; j++){
				String missionId = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				int branchId = buffer.getInt();
				int conditionId = buffer.getInt();
				String type = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				String conditions = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
				decodeConditions(conditions);
				int schedule = buffer.getInt();
				byte state = buffer.get();
				long time = buffer.getLong();
				
				RoleMission mission = createMission(missionId, branchId, type, decodeConditions(conditions), conditionId);
				mission.setUid(uid);
				mission.setAwardStatus(state);
				mission.setSchedule(schedule);
				mission.setUpdateTime(time);
				if(state == 2){
					completedList.add(missionId);
				}else{
//					if(state == 0){
//						TaskConditionType taskType = TaskConditionType.valueof(conditionId);
//						if(taskType.getType() == Const.TASK_COND_TYPE_COUNTRY || taskType.getType() == Const.TASK_COND_TYPE_UNION){
//							mission.checkTaskState();
//						}
//					}
					List<RoleMission> missionLst = roleMissionMap.get(type);
					if(missionLst == null){
						missionLst = new ArrayList<RoleMission>();
					}
					missionLst.add(mission);
					roleMissionMap.put(type, missionLst);
				}
			}
		}
	}
	
	private List<String> decodeConditions(String str){
		if (str == null || str.length() == 0) {
			return null;
		}
		List<String> conditions = new ArrayList<String>();
		String[] values = str.split(";");
		for (int i = 0 ; i < values.length ; i++){
			String value = values[i];
			conditions.add(value);
		}
		return conditions;
	}
	
	private String encodeConditions(RoleMission mission) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < mission.getCondition().size() ; i++){
			String str = mission.getCondition().get(i);
			sb.append(str).append(";");
		}
		if (sb.length() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		return sb.toString();
	}
	
	public void sendAllToClient(RespModuleSet rms){
		sendMissionsToClient(rms, MissionType.MS_ALL);
	}
    /*
    * 检测任务是否完成
    */
	public void checkMission(Role role) {
		for (String str : roleMissionMap.keySet()) {
			List<RoleMission> roleMission = roleMissionMap.get(str);
			for (int i = 0; i < roleMission.size(); i++) {
				RoleMission mission = roleMission.get(i);
				mission.checkTaskState(role);
			}
		}
	}
	/**
	 * 发送所有任务
	 * @param rms
	 */
	public void sendMissionsToClient(RespModuleSet rms, MissionType type){
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ALL_MISSION;
			}
		};
		int size = roleMissionMap.keySet().size();
		module.add(size);
//		module.add("prize"); // 大分支id String
//		module.add(1); // 任务数量 int
//		module.add(curIndex); // 任务id String
//		module.add(type.getKey()); // 子分支id int
//		module.add(schedule); // 进度 int
//		module.add((byte) 0); // 是否完成(0-未完成，1-完成) byte
		for (Map.Entry<String, List<RoleMission>> mapSet : roleMissionMap.entrySet()) {
			module.add(mapSet.getKey()); // 大分支id String
			module.add(getMissionSize(mapSet.getKey()));//任务数量 int
			for (RoleMission roleMission : mapSet.getValue()) {
				if(roleMission.getAwardStatus() == 2){
					continue;
				}
				module.add(roleMission.getMissionId()); // 任务id String
				module.add(roleMission.getBranchId()); // 子分支id int
				module.add(roleMission.getSchedule()); // 进度 int
				module.add(roleMission.getAwardStatus());// 是否完成(0-未完成，1-完成)
			}
		}
		rms.addModule(module);
	}
	
	private int getMissionSize(String mainType){
		int num = 0;
		for (RoleMission task : roleMissionMap.get(mainType)) {
			if(task.getAwardStatus() != 2){
				num ++;
			}
		}
		return num;
	}

	/**
	 * 获取任务奖励
	 * @param role
	 * @param missionId
	 * @return
	 */
	public synchronized boolean getAwordFromMission(Role role, MissionType type, String missionId){
		List<RoleMission> missionLst = roleMissionMap.get(type.getName());
		if(missionLst == null || missionLst.size() == 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_NOT_EXSIT, missionId);
			return false;
		}
		RoleMission roleMission = null;
		for(RoleMission mission : missionLst){
			if(mission.getMissionId().equals(missionId)){
				roleMission = mission;
			}
		}
		if(roleMission == null){
//			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_NOT_EXSIT, missionId);
			return false;
		}
		if(roleMission.awardStatus != 1){
//			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_NOT_OVER, missionId);
			return false;
		}
		//add award information
		Task2 task = dataManager.serach(Task2.class, missionId);
		if(task == null){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_NOT_EXSIT, missionId);
			return false;
		}
		List<String> awardLst = task.getRewardList();
		if(awardLst.size() == 0){
			MessageSendUtil.sendNormalTip(role.getUserInfo(),I18nGreeting.MSG_MISSION_NOT_EXSIT, missionId);
			return false;
		}
		roleMission.setAwardStatus((byte)2);
		completedList.add(roleMission.getMissionId());
		//激活后置任务
		if (!task.getMainID().equals("Glory")){
			openNewTasks(role,task.getOpenTask());
		}
		this.schedule += 1;
		//发送奖励和更新
		List<Object> objects = new ArrayList<Object>();
		RespModuleSet rms = new RespModuleSet();
		for(String strAward : awardLst){
			String[] awardArray = strAward.split(":");
			ResourceTypeConst resType = ResourceTypeConst.search(awardArray[0]);
			if(resType == null){
				GameLog.error("fuck，固化表数据又不对了。");
				continue;
			}
			switch(resType){
			case RESOURCE_TYPE_FOOD:
			case RESOURCE_TYPE_METAL:
			case RESOURCE_TYPE_OIL:
			case RESOURCE_TYPE_ALLOY:
			{
				long num = Long.parseLong(awardArray[1]);
				if(num == 0){
					continue;
				}
				objects.add(resType);
				objects.add(num);
				break;
			}
			case RESOURCE_TYPE_USEREXP:
			case RESOURCE_TYPE_GOLD:
			{
				int num = Integer.parseInt(awardArray[1]);
				if (resType == ResourceTypeConst.RESOURCE_TYPE_USEREXP) {
					role.addExp(num);
				} else {
					if (num > 0) {
						role.addRoleMoney(num);
						LogManager.goldOutputLog(role, num, EventName.getAwordFromMission.getName());
					}
				}
				role.sendRoleToClient(rms);
				break;
			}
			default:
				break;
			}
		}
		getItemArmyRewards(role, rms, task);
		sendMissionsToClient(rms, MissionType.MS_ALL);
		if(objects.size() > 0){
			Object[] temp = objects.toArray();
			role.addResourcesToCity(rms,0,temp);
			for (int i =0 ; i < temp.length ; i += 2){
				ResourceTypeConst restype = (ResourceTypeConst)temp[i];
				long value = Long.parseLong(temp[i + 1].toString());
				if (value <= 0){
					GameLog.error(" add resource value must > 0");
					continue;
				}
				String item  = restype.getKey();
				LogManager.itemOutputLog(role, value, EventName.getAwordFromMission.getName(), item);
				try {
					NewLogManager.baseEventLog(role, "get_task_reward", item, value);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
			}
		}else{
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
		return true;
	}
	
	private void getItemArmyRewards(Role role, RespModuleSet rms, Task2 task){
		List<ItemCell> items = new ArrayList<ItemCell>();
		if(task.getItemReward().size() > 0){
			for(String strAward : task.getItemReward()){
				String[] awardArray = strAward.split(":");
				if(awardArray.length >= 2){
					String itemId = awardArray[0];
					int num = Integer.parseInt(awardArray[1]);
					items.addAll(role.getBagAgent().addGoods(itemId, num));
				}
			}
		}
		if(items.size() > 0){
			ItemCell[] itemArray = items.toArray(new ItemCell[items.size()]);
			role.getBagAgent().sendItemsToClient(rms, itemArray);
		}
		boolean isAdd = false;
		if(task.getArmyReward().size() > 0){
			for(String strAward : task.getArmyReward()){
				String[] awardArray = strAward.split(":");
				if(awardArray.length >= 2){
					String armyId = awardArray[0];
					int num = Integer.parseInt(awardArray[1]);
					ArmyInfo army = role.getCity(0).getArmyAgent().createArmy(armyId, num, ArmyState.ARMY_IN_NORMAL.getValue());
					role.getCity(0).getArmyAgent().addOneClassArmy(army);
					isAdd = true;
					//添加部队战斗力更新
					role.getRoleStatisticInfo().updataRoleArmyFight(role);
				}
			}
		}
		if(isAdd){
			RoleCityAgent city = role.getCity(0);
			city.getArmyAgent().sendToClient(rms,city);
		}
	}
	
	private void openNewTasks(Role role,List<String> taskList) {
		if (taskList == null || taskList.size() == 0) {
			return;
		}
		List<Task2> openList =new ArrayList<Task2>();
		for (int i = 0 ; i < taskList.size() ; i++){
			String taskId = taskList.get(i);
			Task2 task = dataManager.serach(Task2.class, taskId);
			if (task == null) {
				GameLog.error("open task fail,  taskId= " + taskId + "not exsit.");
				continue;
			}
			RoleMission mission = createMission(taskId, task.getBranchID(), task.getMainID(),
					task.getCompleteConditon(), task.getDesignid());
			
			List<RoleMission> missionList = roleMissionMap.get(task.getMainID());
			if (missionList == null) {
				missionList = new ArrayList<RoleMission>();
			}
			boolean bOpend = false;
			for (int j = 0 ; j < missionList.size() ; j++){
				RoleMission rm = missionList.get(j);
				if (rm.getMissionId().equals(mission.getMissionId())) {
					bOpend = true;
					break;
				}
			}
			if (!bOpend) {
				for (int j = 0 ; j < completedList.size() ; j++){
					String missId = completedList.get(j);
					if (missId.equals(mission.getMissionId())) {
						bOpend = true;
						break;
					}
				}
			}
			if (!bOpend) {
				long keyId = keyData.key(DaoData.TABLE_RED_ALERT_MISSION);
				mission.setId(keyId);
				// 任务激活时检查
				mission.checkTaskState(role);
				//TODO 
				if (mission.getType().equals("Glory")) {
					role.handleEvent(GameEvent.TASK_CHECK_STATE_EVENT,
							mission.getMissionId(),
							mission.getAwardStatus(), mission.getSchedule());
				}
				if (mission.awardStatus == 1 && mission.getType().equals("Glory")) {
					Task2 ts = dataManager.serach(Task2.class,mission.getMissionId());
					openList.add(ts);
				}
				missionList.add(mission);
				roleMissionMap.put(task.getMainID(), missionList);
				//接任务
				LogManager.taskLog(role,(byte)0, task.getMainID(), task.getId(),true);
			}
		}
		if (openList != null && openList.size() != 0) {
			for (int i = 0 ; i < openList.size() ; i++){
				Task2 ts = openList.get(i);
				openNewTasks(role,ts.getOpenTask());
			}
		}
	}
	
	/**
	 * 获取当前领取任务的进度任务
	 * @param role
	 * @return
	 */
	public boolean getCurScheduleMission(Role role){
//		Taskreward taskReward = dataManager.serach(Taskreward.class, curIndex); 
//		if(taskReward == null){
//			return false;
//		}
//		if(schedule < taskReward.getCount()){
//			return false;
//		}
//		int index = Integer.parseInt(curIndex) + 1;
//		curIndex = String.valueOf(index);
//		schedule -= taskReward.getCount();
//		role.setTaskState(curIndex + ":" + schedule);
//		//发送奖励
//		List<ItemCell> cells = new ArrayList<ItemCell>();
//		for(String strAward : taskReward.getRewardList()){
//			String[] awardArray = strAward.split(":");
//			if(awardArray.length < 2){
//				continue;
//			}
//			String itemId = awardArray[0];
//			int itemNum = Integer.parseInt(awardArray[1]);
//			role.getBagAgent().addGoods(itemId, itemNum);
//			cells.addAll(role.getBagAgent().addGoods(itemId, itemNum));
//		}
//		RespModuleSet rms = new RespModuleSet();
//		ItemCell[] items = new ItemCell[cells.size()];
//		cells.toArray(items);
//		role.getBagAgent().sendItemsToClient(rms, items);
//		sendMissionsToClient(rms, MissionType.MS_PRIZE);
//		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return false;
	}
	
	private void missionFinish(){
		Role role = world.getObject(Role.class, uid);
		if(role == null){
			return;
		}
		RespModuleSet rms = new RespModuleSet();
		sendMissionsToClient(rms, MissionType.MS_ALL);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
	}
	
	public List<RoleMission> checkTaskConditions(Role role,TaskConditionType type, Object... objects) {
		List<RoleMission> checkLst = new ArrayList<RoleMission>();
		List<Task2> openList =new ArrayList<Task2>();
		for (List<RoleMission> taskLst : roleMissionMap.values()) {
			for (int i = 0 ; i < taskLst.size() ; i++){
				RoleMission mission = taskLst.get(i);
				if (mission.getAwardStatus() == 0 && mission.getConditionId() == type.getKey()) {
					checkLst.add(mission);
					List<String> conditions = mission.getCondition();
					if (conditions == null || conditions.size() == 0) {
						GameLog.error("mission's condition is empty.");
						continue;
					}
					mission.checkTaskState(role,objects);
					if (mission.awardStatus == 1 && mission.getType().equals("Glory")) {
						Task2 task = dataManager.serach(Task2.class,mission.getMissionId());
						openList.add(task);
					}
					if (mission.getType().equals("Glory")) {
						role.handleEvent(GameEvent.TASK_CHECK_STATE_EVENT,
								mission.getMissionId(), mission.getAwardStatus(),mission.getSchedule());
					}
					if (mission.awardStatus == 1) {
						LogManager.taskLog(role,(byte)0, mission.getType(),mission.getMissionId(), false);
						try {
							NewLogManager.baseEventLog(role, "task_complete",mission.getMissionId());
						} catch (Exception e) {
							GameLog.info("埋点错误");
						}
					}
				}
			}
		}
		
		if (openList != null && openList.size() != 0) {
			for (int i = 0 ; i < openList.size() ; i++){
				Task2 task = openList.get(i);
				openNewTasks(role,task.getOpenTask());
			}
		}
		if(checkLst.size() > 0){
			missionFinish();
		}
		role.getDailyTaskAgent().checkTaskConditions(role,type, objects);
		return checkLst;
	}
	
}

