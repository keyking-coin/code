package com.joymeng.slg.domain.actvt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.log.GameLog;
import com.joymeng.log.NewLogManager;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ActvtCommonState;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.DTManager.SearchFilter;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.data.Activity_reward;
import com.joymeng.slg.domain.actvt.data.Activity_task;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.role.Role;

public class RushSevenDay extends Actvt 
{	
	private static final int SEVEN = 7;
	public static final int ONE_DAY_SECONDS = 86400; // 86400
	
	private List<Long> doneJoyIds = new ArrayList<Long>();
	private Map<Long, Integer> tickSeconds = new HashMap<Long, Integer>();
	private Map<Long, List<Integer>> taskNumbers = new HashMap<Long, List<Integer>>();
	private Map<Long, Long> dayRewardFlags = new HashMap<Long, Long>();
	private Map<Long, Long> dayTaskFlags = new HashMap<Long, Long>();
	
	private List<Long> removeList = new ArrayList<Long>();
	private Map<Integer, List<Activity_reward>> sevenDayRewards = new HashMap<Integer, List<Activity_reward>>();
	private Map<Integer, List<Activity_task>> dayTasks = new HashMap<Integer, List<Activity_task>>();
	private Map<Integer, List<List<Activity_reward>>> dayRewards = new HashMap<Integer, List<List<Activity_reward>>>();
	
//	private Activity_rushsevenday rushSevenDay;
	class TaskComparator implements Comparator<Activity_task> 
	{
		@Override
		public int compare(Activity_task task1, Activity_task task2) 
		{
			if (task1.getRank() > task2.getRank()) {
				return 1;
			}
			if (task1.getRank() < task2.getRank()) {
				return -1;
			}
			return 0;
		}
	}
	private TaskComparator taskComparator = new TaskComparator();
	
	@Override
	public boolean init(Activity actvt) {
		if (!super.init(actvt)) {
			return false;
		}
		
		load();
		return true;
	}
	
	public void accelerate(String value, String data)
	{
//		String[] strs = data.split("#");
//		long joyId = Long.parseLong(strs[0]);
//		long time = Long.parseLong(strs[1]);
//		
//		List<Integer> accTimes = rushSevenDay.getAccTimes();
//		List<Integer> accScores = rushSevenDay.getAccScores();
//		int score = 0;
//		for (int i = 0; i < accTimes.size(); i++)
//		{
//			score += (time/accTimes.get(i)*accScores.get(i));
//		}
//		
//		addScore(joyId, score);
	}
	
	@Override
	public void taskEvent(String value, String data)
	{
		if (!isRuning()) {
			return;
		}
		
		try {
			String[] strs = data.split("#");
			long joyId = Long.parseLong(strs[0]);
			if (!isRun(joyId)) {
				return;
			}
			
			String taskID = strs[1];
			int num = 1;
			if (strs.length > 2) {
				num = Integer.parseInt(strs[2]);
			}

			boolean flag = false;
			List<Activity_task> tasks = getDayTasks(joyId);
			for (int i = 0; i < tasks.size(); i++)
			{
				if (getDayTaskFlag(joyId, i, 0) == 1) {
					continue;
				}
				
				Activity_task task = tasks.get(i);
				String id = task.getType();
				if (!task.getDestID().isEmpty()) {
					id = id + "_" + task.getDestID();
				}
				
				if (id.equals(taskID) && id.startsWith(ActvtEventType.UPGRADE_BUILD.getName())) 
				{
					if (num >= task.getNumber()) {
						setDayTaskFlag(joyId, i, 0);
						flag = true;
					}
				}
				else 
				{
					if (id.equals(taskID)) {  
						int newNum = addTaskNumber(joyId, i, num);
						if (newNum >= task.getNumber()) {
							setDayTaskFlag(joyId, i, 0);
							flag = true;
						}
					}
				}
			}   
			
			if (flag) 
			{
				boolean allFinish = true;
				for (int i = 0; i < tasks.size(); i++)
				{
					if (getDayTaskFlag(joyId, i, 0) == 0) {
						allFinish = false;   
						break;
					}
				}
				
				if (allFinish) {
					setDayRewardFlag(joyId, getDay(joyId)-1, 0);
				}
			}
		} catch (Exception e) {
			GameLog.error("RushSevenData taskEvent exception: value="+value+" data="+data);
		}
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) {
		long joyId = role.getId();
		int day = getDay(joyId);
		
		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());
		module.add(day);
		module.add(SEVEN);
		for (int i = 0; i < SEVEN; i++)
		{
			module.add(getDayRewardFlagState(joyId, i));
			module.add(Activity_reward.toString(sevenDayRewards.get(i+1)));
		}
		
		List<Activity_task> dTasks = dayTasks.get(day);
		module.add(dTasks.size());
		for (int i = 0; i < dTasks.size(); i++)
		{
			Activity_task task = dTasks.get(i);
			module.add(task.getContent());
			module.add(getFlagState(joyId,i));
			int curNum = getTaskNumber(role.getId(), i);
			int maxNum = task.getNumber();
			if (task.getType().equals(ActvtEventType.UPGRADE_BUILD.getName())) {
				maxNum = 1;
			}
			curNum = (curNum > maxNum)?maxNum:curNum;
			module.add(maxNum);
			module.add(curNum);
			List<Activity_reward> iRewards = getRewardList(day, i);
			module.add(Activity_reward.toString(iRewards));
		}
		
		module.add(activity.getTypeId());
		module.add(0L);
		module.add(isRun(joyId)?(long)ONE_DAY_SECONDS:0L);
		module.add(isRun(joyId)?(long)(getSeconds(joyId)%ONE_DAY_SECONDS):0L);
		
//		System.out.println(module.getParams().toString());
	}
	
	public boolean isRun(long joyId)
	{
		if (!isRuning()) {
			return false;
		}
		if (!tickSeconds.containsKey(joyId)) {
			return false;
		}
		if (getRealDay(joyId) > SEVEN) {
			return false;
		}
		return true;
	}
	
	private List<Activity_reward> getRewardList(int day, int index)
	{
		if (!dayRewards.containsKey(day)) {
			return null;
		}
		List<List<Activity_reward>> dRewards = dayRewards.get(day); 
		if (index >= dRewards.size()) {
			return null;
		}
		return dRewards.get(index);
	}
	
	private int getDay(long joyId)
	{
		if (!tickSeconds.containsKey(joyId)) {
			return SEVEN;
		}
		int seconds = tickSeconds.get(joyId);
		int day = seconds/ONE_DAY_SECONDS + 1;
		return day>SEVEN?SEVEN:day;
	}
	
	private int getRealDay(long joyId) {
		if (!tickSeconds.containsKey(joyId)) {
			return SEVEN+1;
		}
		int seconds = tickSeconds.get(joyId);
		int day = seconds/ONE_DAY_SECONDS + 1;
		return day;
	}
	
	public int getSeconds(long joyId) {
		if (!tickSeconds.containsKey(joyId)) {
			return 0;
		}
		return tickSeconds.get(joyId);
	}
	
	private List<Activity_task> getDayTasks(long joyId)
	{
		int day = getDay(joyId);
		return dayTasks.get(day);
	}
	
	@Override
	public void tick()
	{
		if (!isRuning()) {
			return;
		}
		
		for (Map.Entry<Long, Integer> entry : tickSeconds.entrySet())
		{
			long joyId = entry.getKey();
			int seconds = entry.getValue();
			tickSeconds.put(joyId, ++seconds);
			
//			if (joyId == 1668383) {
//				tickSeconds.put(joyId, ONE_DAY_SECONDS*8-10);
//			}
//			else {
//				tickSeconds.put(joyId, ONE_DAY_SECONDS*4+1);
//			}
			if (seconds%ONE_DAY_SECONDS == 0)
			{
				int day = seconds/ONE_DAY_SECONDS+1;
				if (day > SEVEN+1)
				{
					// personal rushSevenDay is over
					removeList.add(joyId);
				}
				else 
				{
					change2Day(joyId, day);
				}
			}
		}
		
		for (int i = 0; i < removeList.size(); i++)
		{
			tickSeconds.remove(removeList.get(i));
			doneJoyIds.add(removeList.get(i));
		}
		removeList.clear();
	}
	
	@Override
	public void start()
	{
		super.start();
		
		List<Role> roles = world.getOnlineRoles();
		for (int i = 0; i < roles.size(); i++)
		{
			Role role = roles.get(i);
			long joyId = role.getId();
			if (!tickSeconds.containsKey(joyId) && !doneJoyIds.contains(joyId)) {
				tickSeconds.put(joyId, 0);
				change2Day(joyId, 1);
			}
		}
	}
	
	public void startRushSevenDay(String value, String data)
	{
		try {
			long joyId = Long.parseLong(data);
			if (!tickSeconds.containsKey(joyId) && !doneJoyIds.contains(joyId)) {
				tickSeconds.put(joyId, 0);
				change2Day(joyId, 1);
			}
		}
		catch (Exception e) {
			//SN 错误处理
		}
	}
	
	
	
	private void change2Day(long joyId, int day)
	{
		if (day > SEVEN) {
			return;
		}
		
		List<Integer> numbers = new ArrayList<Integer>();
		int num = dayTasks.get(day).size();
		for (int i = 0; i < num; i++) {
			numbers.add(0);
		}
		taskNumbers.put(joyId, numbers);
		dayTaskFlags.put(joyId, 0L);
		
		Role role = world.getRole(joyId); // world.getOnlineRole(joyId);
		if (role == null) {
			return;
		}

		List<Activity_task> tasks = getDayTasks(joyId);
		for (int i = 0; i < tasks.size(); i++)
		{
			Activity_task task = tasks.get(i);
			if (task.getType().equals(ActvtEventType.UPGRADE_BUILD.getName()))
			{
				String buildId = task.getDestID();
				int level = role.getCity(0).checkBuildLevelByBuildId(buildId);
				if (level >= task.getNumber())
				{
					setDayTaskFlag(joyId, i, 0);
				}
			}
			else if (task.getType().equals(ActvtEventType.PUTON_EQUIP.getName())) 
			{
				int equipedNum = role.getBagAgent().getEquipedNum();
				if (equipedNum >= Integer.parseInt(task.getDestID()))
				{
					setDayTaskFlag(joyId, i, 0);
				}
			}
		}
		
		boolean allFinish = true;
		for (int i = 0; i < tasks.size(); i++)
		{
			if (getDayTaskFlag(joyId, i, 0) == 0) {
				allFinish = false;
				break;
			}
		}
		
		if (allFinish) {
			setDayRewardFlag(joyId, getDay(joyId)-1, 0);
		}
		
		if (day > 1) {
			actvtMgr.sendActvtTip(joyId);
		}
	}
	
	@Override
	public boolean receiveReward(Role role, int index) 
	{
		if (index < SEVEN) {
			if (getDayRewardFlagState(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
				return false;
			}
			
			List<Activity_reward> rewards = sevenDayRewards.get(index+1);
			rewardPlayer(role, Activity_reward.toString(rewards));
			setDayRewardFlag(role.getId(), index, 1);
			
			// TEST11 七日冲级活动奖励  领取日奖励  rewards
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<rewards.size();j++){
				Activity_reward reward = rewards.get(j);
				sb.append(reward.getsID());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(reward.getNum());
				sb.append(GameLog.SPLIT_CHAR);
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.activeLog(role, "activity_growing_reward",newStr);
			
		}
		else {
			index -= SEVEN;
			if (getFlagState(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
				return false;
			}
			
			int day = getDay(role.getId());
			List<Activity_reward> rewards = dayRewards.get(day).get(index);
			rewardPlayer(role, Activity_reward.toString(rewards));
			setDayTaskFlag(role.getId(), index, 1);
			
			// TEST11 七日冲级活动奖励  领取任务奖励  rewards
			StringBuffer sb = new StringBuffer();
			for(int j=0;j<rewards.size();j++){
				Activity_reward reward = rewards.get(j);
				sb.append(reward.getsID());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(reward.getNum());
				sb.append(GameLog.SPLIT_CHAR);
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			NewLogManager.activeLog(role, "activity_growing_reward",newStr);
		}
		return true;
	}
	
	int getTaskNumber(long joyId, int index)
	{
		if (!taskNumbers.containsKey(joyId)) {
			return 0;
		}
		List<Integer> numbers = taskNumbers.get(joyId);
		if (index >= numbers.size()) {
			return 0;
		}
		return numbers.get(index);
	}
	
	private int addTaskNumber(long joyId, int index, int num) 
	{
		if (!taskNumbers.containsKey(joyId)) {
			return 0;
		}
		List<Integer> numbers = taskNumbers.get(joyId);
		if (index >= numbers.size()) {
			return 0;
		}
		int newNum = numbers.get(index)+num;
		numbers.set(index, newNum);
		return newNum;
	}
	
	private void setDayRewardFlag(long joyId, int index, int receive) 
	{
		long flag = 0;
		if (dayRewardFlags.containsKey(joyId)) {
			flag = dayRewardFlags.get(joyId);
		}
		flag = setBit(flag, index*2 + receive, 1);
		dayRewardFlags.put(joyId, flag);
		
		if (receive == 0) {
			actvtMgr.sendActvtTip(joyId);
		}
	}
	
	public long getDayRewardFlag(long joyId, int index, int receive) 
	{
		if (!dayRewardFlags.containsKey(joyId))
		{
			return 0;
		}
		else 
		{
			long flag = dayRewardFlags.get(joyId);
			return getBit(flag, index*2 + receive);
		}
	}
	
	private int getFlagState(long joyId, int index) {
		if (state == ActvtState.PREPARE) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getDayTaskFlag(joyId, index, 1) == 1) {
			return ActvtCommonState.RECEIVED.ordinal();
		}
		if (getDayTaskFlag(joyId, index, 0) == 1) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.NOT_FINISH.ordinal();
	}
	
	private int getDayRewardFlagState(long joyId, int index) {
		if (state == ActvtState.PREPARE) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getDayRewardFlag(joyId, index, 1) == 1) {
			return ActvtCommonState.RECEIVED.ordinal();
		}
		if (getDayRewardFlag(joyId, index, 0) == 1) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.NOT_FINISH.ordinal();
	}
	
	@Override
	public boolean canShow(long joyId) {
		if (!super.canShow(joyId)) {
			return false;
		}
		return !doneJoyIds.contains(joyId);
//		int day = getRealDay(joyId);
//		return !doneJoyIds.contains(joyId) && day < SEVEN+2;
	}
	
	private long getDayTaskFlag(long joyId, int index, int receive) 
	{
		if (!dayTaskFlags.containsKey(joyId))
		{
			return 0;
		}
		else 
		{
			long flag = dayTaskFlags.get(joyId);
			return getBit(flag, index*2 + receive);
		}
	}
	
	private void setDayTaskFlag(long joyId, int index, int receive) 
	{
		long flag = 0;
		if (dayTaskFlags.containsKey(joyId)) {
			flag = dayTaskFlags.get(joyId);
		}
		flag = setBit(flag, index*2 + receive, 1);
		dayTaskFlags.put(joyId, flag);
		
		if (receive == 0) {
			actvtMgr.sendActvtTip(joyId);
		}
	}
 
	@Override
	public String getStateStr() {
		return JSON.toJSONString(doneJoyIds) + SPCH + JSON.toJSONString(tickSeconds) + SPCH + JSON.toJSONString(taskNumbers)
		 + SPCH + JSON.toJSONString(dayRewardFlags) + SPCH + JSON.toJSONString(dayTaskFlags);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		doneJoyIds = JSON.parseObject(strs[0], new TypeReference<List<Long>>(){});
		tickSeconds = JSON.parseObject(strs[1], new TypeReference<Map<Long, Integer>>(){});
		taskNumbers = JSON.parseObject(strs[2], new TypeReference<Map<Long, List<Integer>>>(){});
		dayRewardFlags = JSON.parseObject(strs[3], new TypeReference<Map<Long, Long>>(){});
		dayTaskFlags = JSON.parseObject(strs[4], new TypeReference<Map<Long, Long>>(){});
	}

	@Override
	public void load() {
		sevenDayRewards.clear();
		for (int i = 0; i < SEVEN; i++)
		{
			final int rank = i+ 1;
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
				@Override
				public boolean filter(Activity_reward data) {
					return data.getrID().equals(getActivity().getTypeId()+"_"+rank);
				}
			});
//			List<Activity_reward> rewardList = actvtMgr.getReward(getActivity().getTypeId()+"_"+(i+1));
			sevenDayRewards.put(rank, rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
		
		dayRewards.clear();
		for (int i = 0; i < SEVEN; i++)
		{
			List<List<Activity_reward>> dRewards = new ArrayList<List<Activity_reward>>();
			dayRewards.put(i+1, dRewards);
			
			int j = 1;
			while(true)
			{
				final int day = i+ 1;
				final int index = j++;
				List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
					@Override
					public boolean filter(Activity_reward data) {
						return data.getrID().equals(getActivity().getTypeId()+"_"+day+"_"+index);
					}
				});
//				List<Activity_reward> rewardList = actvtMgr.getReward(getActivity().getTypeId()+"_"+(i+1)+"_"+j++);
				if (rewardList == null || rewardList.isEmpty()) {
					break;
				}
				dRewards.add(rewardList);
				Collections.sort(rewardList, rewardComparator);
			}
		}
		
		dayTasks.clear();
		List<Activity_task> tasks = actvtMgr.serachList(Activity_task.class,
			new SearchFilter<Activity_task>() {
				@Override
				public boolean filter(Activity_task data) {
					return data.gettID().matches(getActivity().getTypeId() + "_[0-9]+_[0-9]+$");
				}
			}
		);
		for (int i = 0; i < tasks.size(); i++)
		{
			Activity_task task = tasks.get(i);
			int day = Integer.parseInt(task.gettID().split("_")[1]);
//			int index = Integer.parseInt(task.gettID().split("_")[2]);
			
			if (!dayTasks.containsKey(day)) {
				List<Activity_task> dTasks = new ArrayList<Activity_task>();
				dayTasks.put(day, dTasks);
			}
			List<Activity_task> dTasks = dayTasks.get(day);
			dTasks.add(task);
			Collections.sort(dTasks, taskComparator);
		}
	}
	
	@Override
	public int getReceiveableNum(long joyId)
	{
		int num = 0;
		int day = getDay(joyId);
		if (getDayRewardFlag(joyId, day, 0) == 1 && getDayRewardFlag(joyId, day, 1) == 0) {
			num++;
		}
		
		List<Activity_task> taskList = getDayTasks(joyId);
		for (int i = 0; i < taskList.size(); i++)
		{
			if (getDayTaskFlag(joyId, day, 0) == 1 && getDayTaskFlag(joyId, day, 1) == 0) {
				num++;
			}
		}
		return num;
	}
	
	@Override
	public int getStateOdinal(long joyId) {
		if (getRealDay(joyId) > SEVEN) {
			return ActvtState.END.ordinal();
		}
		return super.getStateOdinal(joyId);
	}
	
	@Override
	public long getStartSeconds() {
		if (isRuning()) {
			return 0L;
		}
		return super.getStartSeconds();
	}

	@Override
	public long getLastSeconds() {
		if (isRuning()) {
			return ONE_DAY_SECONDS*SEVEN;
		}
		return super.getLastSeconds();
	}

	@Override
	public long getNowSeconds(long joyId) {
		if (isRuning()) {
			long nowSecs = 0L;
			if (tickSeconds.containsKey(joyId)) {
				nowSecs = tickSeconds.get(joyId);
			}
			long maxSecs = getLastSeconds();
			return nowSecs>maxSecs?maxSecs:nowSecs;
		}
		return super.getNowSeconds(joyId);
	}
}
