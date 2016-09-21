package com.joymeng.slg.domain.object.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.EventName;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.dao.DaoData;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.domain.object.bag.RoleBagAgent;
import com.joymeng.slg.domain.object.bag.data.Item;
import com.joymeng.slg.domain.data.SearchFilter;
import com.joymeng.slg.domain.object.build.RoleCityAgent;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.object.task.RoleTaskType.TaskConditionType;
import com.joymeng.slg.domain.object.task.data.Activeextrainfo;
import com.joymeng.slg.domain.object.task.data.Activereward;
import com.joymeng.slg.domain.object.task.data.Task2;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;

public class DailyTaskAgent implements DaoData, Instances {
	long uid;
	long timer;
	Vector<Byte> rewardState = new Vector<Byte>();
	int schedule;
	Map<String, RoleMission> dailyTaskMap = new HashMap<String, RoleMission>();
	boolean savIng = false;
	
	public DailyTaskAgent() {

	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getTimer() {
		return timer;
	}

	public void setTimer(long timer) {
		this.timer = timer;
	}

	public Map<String, RoleMission> getDailyTaskMap() {
		return dailyTaskMap;
	}

	public void setDailyTaskMap(Map<String, RoleMission> dailyTaskMap) {
		this.dailyTaskMap = dailyTaskMap;
	}

	public int getSchedule() {
		return schedule;
	}

	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}

	public void initDailyTask(Role role) {
		timer = TimeUtils.nowLong();
		schedule = 0;
		List<Activereward> aRewardLst = dataManager.serachList(Activereward.class);
		if (aRewardLst != null) {
			for (int i = 0; i < aRewardLst.size(); i++) {
				rewardState.add(i, (byte) 0);
			}
		}
		byte level = 0;
		for (int i = 0 ; i < role.getCityAgents().size() ; i++){
			RoleCityAgent city = role.getCityAgents().get(i);
			if (level < city.getCityCenterLevel()) {
				level = city.getCityCenterLevel();
			}
		}
		final byte newLevel = level;
		List<Activeextrainfo> taskData = dataManager.serachList(Activeextrainfo.class,
				new SearchFilter<Activeextrainfo>() {
					@Override
					public boolean filter(Activeextrainfo data) {
						if (newLevel >= data.getBuildinglevel()) {
							return true;
						}
						return false;
					}
				});
		if (taskData != null && taskData.size() > 0) {
			for (int i = 0 ; i < taskData.size() ; i++){
				Activeextrainfo info = taskData.get(i);
				String taskId = info.getId();
				Task2 task = dataManager.serach(Task2.class, taskId);
				if (task != null) {
					RoleMission mission = createMission(taskId, task.getBranchID(), task.getMainID(),
							task.getCompleteConditon(), task.getDesignid());
					long keyId = keyData.key(DaoData.TABLE_RED_ALERT_MISSION);
					mission.setId(keyId);
					if(mission.getMissionId().equals("active39")){
						mission.checkTaskState(role);
						schedule += mission.getSchedule();
					}
					dailyTaskMap.put(taskId, mission);
					LogManager.taskLog(role, (byte)1,task.getMainID(), task.getId(), true);
				}
			}
		}
	}

	public void refreshDailyTask(Role role) {
		schedule = 0;
		rewardState.clear();
		List<Activereward> aRewardLst = dataManager.serachList(Activereward.class);
		if (aRewardLst != null) {
			for (int i = 0; i < aRewardLst.size(); i++) {
				rewardState.add(i, (byte) 0);
			}
		}
		timer = TimeUtils.nowLong() + 5000;//增加5秒时间
		for (RoleMission mission : dailyTaskMap.values()) {
			mission.setAwardStatus((byte) 0);
			mission.setSchedule(0);
			if(mission.getMissionId().equals("active39")){
				mission.checkTaskState(role);
				schedule += mission.getSchedule();
			}
		}
	}

	public RoleMission createMission(String missionId, int branchId, String mainType, List<String> conditions,
			int conditionId) {
		RoleMission mission = new RoleMission(uid);
		mission.setMissionId(missionId);
		mission.setType(mainType);
		mission.setCondition(conditions);
		mission.setBranchId(branchId);
		mission.setAwardStatus((byte) 0);
		mission.setSchedule(0);
		mission.setConditionId(conditionId);
		mission.setUpdateTime(TimeUtils.nowLong() / 1000);
		return mission;
	}

	private boolean updateRewardIndex(int index) {
		if (rewardState.get(index - 1) != null) {
			byte state = rewardState.get(index - 1);
			if (state == 1) {
				rewardState.add(index - 1, (byte) 2);
				rewardState.remove(index);
				return true;
			}
		}
		return false;
	}

	private void updateScheduleIndex(Role role) {
		List<Activereward> datas = dataManager.serachList(Activereward.class);
		for (int i = 0 ; i < datas.size() ; i++){
			Activereward data = datas.get(i);
			int index = Integer.parseInt(data.getId()) - 1;
			if (schedule >= data.getPoints() && rewardState.get(index) == 0) {
				rewardState.add(index, (byte) 1);
				rewardState.remove(index + 1);
				try {
					NewLogManager.baseEventLog(role, "daily_task_stage_complete",index);
				} catch (Exception e) {
					GameLog.info("埋点错误");
				}
			}
		}
	}

	/**
	 * 获取任务奖励
	 * 
	 * @param role
	 * @param missionId
	 * @return
	 */
	public synchronized boolean getAwordFromTaskSchedule(Role role, String index) {
		Activereward rewardData = dataManager.serach(Activereward.class, index);
		if (rewardData == null) {
			GameLog.error("getAwordFromTaskSchedule rewardData error where index=" + index);
			return false;
		}
		if (this.schedule < rewardData.getPoints()) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MISSION_NOT_EXSIT, index);
			return false;
		}

		List<String> awardLst = rewardData.getReward();
		if (awardLst.size() == 0) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MISSION_REWARD_NOT_EXSIT, index);
			return false;
		}
		if (!updateRewardIndex(Integer.parseInt(index))) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MISSION_NOT_OVER, index);
			return false;
		}
		// 发送奖励和更新
		RoleBagAgent bagAgent = role.getBagAgent();
		List<ItemCell> items = new ArrayList<ItemCell>();
		for (int i = 0 ; i < awardLst.size() ; i++){
			String strAward = awardLst.get(i);
			String[] awardArray = strAward.split(":");
			if (awardArray.length < 2) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_MISSION_REWARD_NOT_EXSIT, index);
				return false;
			}
			String itemId = awardArray[0];
			Item item = dataManager.serach(Item.class, itemId);
			if (item == null) {
				GameLog.error("getAwordFromTaskSchedule add item error, itemId=" + itemId);
				continue;
			}
			int num = Integer.parseInt(awardArray[1]);
			List<ItemCell> newItems = bagAgent.addGoods(itemId, num);
			LogManager.itemOutputLog(role, num, EventName.getAwardDailyTask.getName(), itemId);
			try {
				NewLogManager.baseEventLog(role, "get_daily_task_reward",itemId,num);
			} catch (Exception e) {
				GameLog.info("埋点错误");
			}
			if (newItems.size() > 0) {
				items.addAll(newItems);
			}
		}
		// 下发
		RespModuleSet rms = new RespModuleSet();
		if (items.size() > 0) {
			bagAgent.sendItemsToClient(rms,items);
		} else {
			GameLog.error("getAwordFromTaskSchedule add item error, rewardIndex= " + index);
		}
		sendMissionsToClient(rms);
		MessageSendUtil.sendModule(rms, role.getUserInfo());
		return true;
	}

	public void sendMissionsToClient(RespModuleSet rms) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_DAILY_TASK;
			}
		};
		module.add(schedule);// 当前总进度 int
		module.add(rewardState.size());// int
		for (int i = 0; i < rewardState.size(); i++) {
			module.add(String.valueOf(i + 1));// 奖励Id String
			module.add(rewardState.get(i));// 是否已领取 byte 0-未领取，1-已领取
		}
		module.add(dailyTaskMap.size());// 任务数量 int
		for (Map.Entry<String, RoleMission> mapSet : dailyTaskMap.entrySet()) {
			RoleMission task = mapSet.getValue();
			module.add(task.getMissionId()); // 任务id String
			module.add(task.getBranchId()); // 子分支id int
			module.add(task.getSchedule()); // 进度 int
			module.add(task.getAwardStatus());// 是否完成(0-未完成，1-完成)
		}
		rms.addModule(module);
	}

	public void sendUpdateToClient(RespModuleSet rms, List<RoleMission> updateList) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_DAILY_TASK;
			}
		};
		module.add(schedule);// 当前总进度 int
		module.add(rewardState.size());// int
		for (int i = 0; i < rewardState.size(); i++) {
			module.add(String.valueOf(i + 1));// 奖励Id String
			module.add(rewardState.get(i));// 是否已领取 byte 0-未领取，1-已领取
		}
		module.add(updateList.size());// 任务数量 int
		for (int i = 0 ; i < updateList.size() ; i++){
			RoleMission task = updateList.get(i);
			module.add(task.getMissionId()); // 任务id String
			module.add(task.getBranchId()); // 子分支id int
			module.add(task.getSchedule()); // 进度 int
			module.add(task.getAwardStatus());// 是否完成(0-未完成，1-完成)
		}
		rms.addModule(module);
	}

	private void missionFinish(List<RoleMission> updateList) {
		Role role = world.getObject(Role.class, uid);
		if (role == null) {
			return;
		}
		if (updateList.size() > 0) {
			RespModuleSet rms = new RespModuleSet();
			sendUpdateToClient(rms, updateList);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	public List<RoleMission> checkTaskConditions(Role role,TaskConditionType type, Object... objects) {
		List<RoleMission> checkLst = new ArrayList<RoleMission>();
		for (RoleMission mission : dailyTaskMap.values()) {
			if (mission.getAwardStatus() == 0 && mission.getConditionId() == type.getKey()) {
				checkLst.add(mission);
				List<String> conditions = mission.getCondition();
				if (conditions == null || conditions.size() == 0) {
					GameLog.error("mission's condition is empty.");
					continue;
				}
				int oldSchedule = mission.getDailySchedule();
				mission.checkTaskState(role,objects);
				if (mission.awardStatus == 1) { // awardStatus -1,任务完成
					LogManager.taskLog(role, (byte)1,mission.getType(), mission.getMissionId(), false);
					try {
						NewLogManager.baseEventLog(role, "task_complete",mission.getMissionId());
					} catch (Exception e) {
						GameLog.info("埋点错误");
					}
				}
				schedule += (mission.getDailySchedule() - oldSchedule);
				updateScheduleIndex(role);
			}
		}
		missionFinish(checkLst);
		return checkLst;
	}

	public void openNewDailyTask(Role role,final byte buildLevel) {
		List<RoleMission> checkLst = new ArrayList<RoleMission>();
		List<Activeextrainfo> taskData = dataManager.serachList(Activeextrainfo.class,
				new SearchFilter<Activeextrainfo>() {
					@Override
					public boolean filter(Activeextrainfo data) {
						if (buildLevel == data.getBuildinglevel()) {
							return true;
						}
						return false;
					}
				});
		if (taskData != null && taskData.size() > 0) {
			for (int i = 0 ; i < taskData.size() ; i++){
				Activeextrainfo info = taskData.get(i);
				String taskId = info.getId();
				Task2 task = dataManager.serach(Task2.class, taskId);
				if (task != null) {
					RoleMission mission = createMission(taskId, task.getBranchID(), task.getMainID(),
							task.getCompleteConditon(), task.getDesignid());
					long keyId = keyData.key(DaoData.TABLE_RED_ALERT_MISSION);
					mission.setId(keyId);
//					mission.checkTaskState(role);
					dailyTaskMap.put(taskId, mission);
					LogManager.taskLog(role, (byte)1,task.getMainID(), task.getId(), true);
					checkLst.add(mission);
				}
			}
		}
		missionFinish(checkLst);
	}

	public boolean checkDailyTime(Role role, SqlData data) {
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		timer = data.getLong(DaoData.RED_ALERT_TASK_UPDATE_TIMER);
		if (!TimeUtils.isSameDay(timer, TimeUtils.nowLong())) {
			initDailyTask(role);
//			checkMission(role);
			return false;
		}
		return true;
	}

	@Override
	public String table() {
		return DaoData.TABLE_RED_ALERT_DAILYTASK;
	}

	@Override
	public String[] wheres() {
		return new String[] { DaoData.RED_ALERT_GENERAL_UID };
	}

	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public void insertData(SqlData data) {
		saveToData(data);
	}

	@Override
	public void save() {
		if (savIng){
			return;
		}
		savIng = true;
		taskPool.saveThread.addSaveData(this);
	}

	@Override
	public void loadFromData(SqlData data) {
		uid = data.getLong(DaoData.RED_ALERT_GENERAL_UID);
		timer = data.getLong(DaoData.RED_ALERT_TASK_UPDATE_TIMER);
		schedule = data.getInt(DaoData.RED_ALERT_TASK_SCHEDULE);
		String states = data.getString(DaoData.RED_ALERT_TASK_GET_INDEX);
		String[] arr = states.split(":");
		for (int i = 0; i < arr.length; i += 2) {
			int index = Integer.parseInt(arr[i]);
			byte state = Byte.parseByte(arr[i + 1]);
			rewardState.add(index, state);
		}
		String mapStr = data.getString(DaoData.RED_ALERT_TASK_MAP);
		dailyTaskMap = JSON.parseObject(mapStr, new TypeReference<Map<String, RoleMission>>() {});
	}

	@Override
	public void saveToData(SqlData data) {
		if (uid == 0) {
			return;
		}
		data.put(DaoData.RED_ALERT_GENERAL_UID, uid);
		data.put(DaoData.RED_ALERT_TASK_SCHEDULE, schedule);
		StringBuffer strState = new StringBuffer();
		for (int i = 0; i < rewardState.size(); i++) {
			strState.append(i).append(":").append(rewardState.get(i)).append(":");
		}
		if (strState.length() > 0) {
			strState.delete(strState.length() - 1, strState.length());
		}
		data.put(DaoData.RED_ALERT_TASK_GET_INDEX, strState.toString());// 领取奖励状态索引
		data.put(DaoData.RED_ALERT_TASK_UPDATE_TIMER, timer);
		data.put(DaoData.RED_ALERT_TASK_MAP, JsonUtil.ObjectToJsonString(dailyTaskMap));
	}

	@Override
	public void over() {
		savIng = false;
	}

	@Override
	public boolean saving() {
		return savIng ;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "_" + uid;
	}

	public void checkMission(Role role) {
		for (RoleMission  mission : dailyTaskMap.values()){
			mission.checkTaskState(role);
			schedule += mission.getDailySchedule();
			updateScheduleIndex(role);
		}
	}
}
