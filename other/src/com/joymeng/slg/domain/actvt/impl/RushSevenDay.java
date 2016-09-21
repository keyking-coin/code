package com.joymeng.slg.domain.actvt.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.log.GameLog;
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ActvtCommonState;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.data.ActvtReward;
import com.joymeng.slg.domain.actvt.data.ActvtTask;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.object.role.Role;

public class RushSevenDay extends Actvt 
{	
	private static final int SEVEN = 7;  
	
	private List<Long> doneJoyIds = new ArrayList<Long>();
	private Map<Long, Integer> tickSeconds = new HashMap<Long, Integer>();
	private Map<Long, List<Integer>> taskNumbers = new HashMap<Long, List<Integer>>();
	private Map<Long, List<Integer>> dayRewardFlags = new HashMap<Long, List<Integer>>();
	private Map<Long, List<Integer>> dayTaskFlags = new HashMap<Long, List<Integer>>();
	
	private List<Long> removeList = new ArrayList<Long>();
	private List<String> eventArgs = new ArrayList<String>();
	
	private String cronExpression;
	private int oneDaySeconds = 86400;
	private List<ActvtReward> sevenDayRewards = new ArrayList<ActvtReward>();
	private Map<Integer, List<ActvtTask>> dayTasks = new HashMap<Integer, List<ActvtTask>>();
	private Map<Integer, List<ActvtReward>> dayRewards = new HashMap<Integer, List<ActvtReward>>();
	
	@Override
	public void end()
	{
		super.end();
		stopInnerTimer("tick");
		EvntManager.getInstance().Remove(this);
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
		
		stopInnerTimer("tick");
		startInnerTimer(cronExpression, "tick");
		
		EvntManager.getInstance().Listen("taskEvent", this);
		EvntManager.getInstance().Listen("roleEnter", this);
	}

	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		oneDaySeconds = Integer.parseInt(eleSpecial.getAttribute("oneDaySeconds"));
		cronExpression = eleSpecial.getAttribute("tickTime");
		
		for (int day = 1; day <= SEVEN; day++)
		{
			int index = 1;
			List<ActvtTask> taskList = new ArrayList<ActvtTask>();
			while(true)
			{
				String id = MessageFormat.format("{0}_{1}", day, index++);
				ActvtTask task = getTask(id);
				if (task == null) {
					break;
				}
				taskList.add(task);
			}
			dayTasks.put(day, taskList);
		}
		
		for (int day = 1; day <= SEVEN; day++)
		{
			ActvtReward reward = getReward(String.valueOf(day));
			if (reward == null) {
				throw new Exception("id="+getId()+" day="+day+" reward not exist");
			}
			sevenDayRewards.add(reward);
			
			int index = 1;
			List<ActvtReward> rewardList = new ArrayList<ActvtReward>();
			while(true)
			{
				String id = MessageFormat.format("{0}_{1}", day, index++);
				ActvtReward rd = getReward(id);
				if (rd == null) {
					break;
				}
				rewardList.add(rd);
			}
			dayRewards.put(day, rewardList);
		}
		
		for (int day = 1; day <= SEVEN; day++)
		{
			List<ActvtTask> taskList = dayTasks.get(day);
			List<ActvtReward> rewardList = dayRewards.get(day);
			
			if (taskList.size() != rewardList.size()) {
				throw new Exception("id="+getId()+" dayRewards and dayTasks not have same num");
			}
		}
	}

	@Override
	public void taskEvent(Object... datas)
	{
		try {
			long joyId = Long.parseLong(datas[0].toString());
			Role role = world.getRole(joyId);
			if (role == null) {
				return;
			}
			
			String taskID = datas[1].toString();
			int num = Integer.parseInt(datas[2].toString());
			
			eventArgs.clear();
			for (int i = 3; i < datas.length; i++) {
				eventArgs.add(datas[i].toString());
			}

			boolean flag = false;
			List<ActvtTask> tasks = getDayTasks(joyId);
			for (int i = 0; i < tasks.size(); i++)
			{
				if (getDayTaskFlag(joyId, i) != ActvtCommonState.NOT_FINISH.ordinal()) {
					continue;
				}
				
				ActvtTask task = tasks.get(i);
				if (task.check(taskID, eventArgs)) {
					int newNum = addTaskNumber(joyId, i, num);
					if (newNum >= task.getNum()) {
						setDayTaskFlag(joyId, i, false);
						flag = true;
					}
				}
			}   
			
			if (flag) 
			{
				boolean allFinish = true;
				for (int i = 0; i < tasks.size(); i++)
				{
					if (getDayTaskFlag(joyId, i) == 0) {
						allFinish = false;   
						break;
					}
				}
				
				if (allFinish) {
					setDayRewardFlag(joyId, getDay(joyId)-1, false);
				}
			}
		} catch (Exception e) {
			GameLog.error("RushSevenData taskEvent exception: data="+Arrays.asList(datas));
		}
	}
	
	@Override
	public void execute(String event, Object... datas)
	{
		if (!isRuning()) {
			return;
		}
		
		if (event.equals("taskEvent")) {
			taskEvent(datas);
		}
		else if (event.equals("roleEnter")) {
			startRushSevenDay(datas);
		}
	}
	
	@Override
	public void innerTimerCB(String tag)
	{
		if (tag.equals("tick")) {
			tick();
		}
	}
	
	public long getOneDaySeconds() {
		return (long)oneDaySeconds;
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		long joyId = role.getId();
		int day = getDay(joyId);
		day = day>SEVEN?SEVEN:day;
		
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(day);
		module.add(SEVEN);
		for (int i = 0; i < SEVEN; i++)
		{
			module.add(getDayRewardFlagState(joyId, i));
			module.add(sevenDayRewards.get(i).getItems());
		}
		
		List<ActvtTask> dTasks = dayTasks.get(day);
		List<ActvtReward> dRewards = dayRewards.get(day);
		module.add(dTasks.size());
		for (int i = 0; i < dTasks.size(); i++)
		{
			ActvtTask task = dTasks.get(i);
			module.add(task.getDesc());
			module.add(getFlagState(joyId,i));
			int curNum = getTaskNumber(role.getId(), i);
			int maxNum = task.getNum();

			curNum = (curNum > maxNum)?maxNum:curNum;
			module.add(maxNum);
			module.add(curNum);
			ActvtReward reward = dRewards.get(i);
			module.add(reward.getItems());
		}
		
		module.add(commonData.getType()+getId());
		module.add(0L);
		module.add(isRun(joyId)?(long)oneDaySeconds:0L);
		module.add(isRun(joyId)?(long)(getSeconds(joyId)%oneDaySeconds):0L);
		
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
		if (getDay(joyId) > SEVEN) {
			return false;
		}
		return true;
	}
	
	private int getDay(long joyId)
	{
		if (doneJoyIds.contains(joyId)) {
			return SEVEN+2;
		}
		if (!tickSeconds.containsKey(joyId)) {
//			tickSeconds.put(joyId, 0);
			return 1;
		}
		int seconds = tickSeconds.get(joyId);
		int day = seconds/oneDaySeconds + 1;
		return day;
	}
	
	public int getSeconds(long joyId) {
		if (!tickSeconds.containsKey(joyId)) {
			return 0;
		}
		return tickSeconds.get(joyId);
	}
	
	private List<ActvtTask> getDayTasks(long joyId)
	{
		int day = getDay(joyId);
		if (dayTasks.containsKey(day)) {
			return dayTasks.get(day);
		}
		return null;
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
			int oldDay = seconds/oneDaySeconds + 1;
			tickSeconds.put(joyId, ++seconds);
			int newDay = seconds/oneDaySeconds + 1;

			if (newDay != oldDay)
			{
				if (newDay >= SEVEN+2) {
					removeList.add(joyId);
				}
				else {
					change2Day(joyId, newDay);
				}
			}
		}
		
		for (int i = 0; i < removeList.size(); i++) {
			tickSeconds.remove(removeList.get(i));
			doneJoyIds.add(removeList.get(i));
		}
		removeList.clear();
	}
	
	public void cheatDay(long joyId, int day)
	{
		int secs = oneDaySeconds*(day-1) - 10;
		secs = secs<0?0:secs;
		tickSeconds.put(joyId, secs);
	}
	
	public void startRushSevenDay(Object... datas)
	{
		try {
			long joyId = Long.parseLong(datas[0].toString());
			if (!tickSeconds.containsKey(joyId) && !doneJoyIds.contains(joyId)) {
				tickSeconds.put(joyId, 0);
				change2Day(joyId, 1);
				
				initDayRewardFlags(joyId);
			}
		}
		catch (Exception e) {
			GameLog.error("startRushSevenDay data="+Arrays.asList(datas)+" is error");
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
		dayTaskFlags.put(joyId, new ArrayList<Integer>(numbers));
		
		Role role = world.getRole(joyId); // world.getOnlineRole(joyId);
		if (role == null) {
			return;
		}

		List<ActvtTask> tasks = getDayTasks(joyId);
		for (int i = 0; i < tasks.size(); i++)
		{
			ActvtTask task = tasks.get(i);
			if (task.getType().equals(ActvtEventType.UPGRADE_BUILD.getName()))
			{
				String buildId = task.getArgs().get(0);
				int dstLevel = Integer.parseInt(task.getArgs().get(1));
				int level = role.getCity(0).checkBuildLevelByBuildId(buildId);
				if (level >= dstLevel) {
					setDayTaskFlag(joyId, i, false);
				}
			}
			else if (task.getType().equals(ActvtEventType.PUTON_EQUIP.getName())) 
			{
				int equipedNum = role.getBagAgent().getEquipedNum();
				int dstNum = Integer.parseInt(task.getArgs().get(0));
				if (equipedNum >= dstNum) {
					setDayTaskFlag(joyId, i, false);
				}
			}
		}
		
		boolean allFinish = true;
		for (int i = 0; i < tasks.size(); i++)
		{
			if (getDayTaskFlag(joyId, i) == 0) {
				allFinish = false;
				break;
			}
		}
		
		if (allFinish) {
			setDayRewardFlag(joyId, SEVEN-1, false);
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
			
			ActvtReward reward = sevenDayRewards.get(index+1);
			rewardPlayer(role, reward.getItems());
			setDayRewardFlag(role.getId(), index, true);
		}
		else {
			index -= SEVEN;
			if (getFlagState(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
				return false;
			}
			
			int day = getDay(role.getId());
			ActvtReward reward = dayRewards.get(day).get(index);
			rewardPlayer(role, reward.getItems());
			setDayTaskFlag(role.getId(), index, true);
		}
		return true;
	}
	
	private int getTaskNumber(long joyId, int index)
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
	
	private void initDayRewardFlags(long joyId)
	{
		List<Integer> numbers = new ArrayList<Integer>();
		for (int i = 0; i < SEVEN; i++) {
			numbers.add(0);
		}
		dayRewardFlags.put(joyId, numbers);
	}
	
	private void setDayRewardFlag(long joyId, int index, boolean receive) 
	{
		if (!dayRewardFlags.containsKey(joyId)) {
			initDayRewardFlags(joyId);
			GameLog.error("setDayRewardFlag but not have joyId="+joyId);
		}
		List<Integer> numbers = dayRewardFlags.get(joyId);
		numbers.set(index, receive?ActvtCommonState.RECEIVED.ordinal():ActvtCommonState.FINISH.ordinal());
	}
	
	public int getDayRewardFlag(long joyId, int index) 
	{
		if (!dayRewardFlags.containsKey(joyId)) {
			return 0;
		}
		
		List<Integer> numbers = dayRewardFlags.get(joyId);
		if (index >= numbers.size()) {
			GameLog.error(MessageFormat.format("getDayRewardFlag index out of bounds joyId={0} index={1}", joyId, index));
			return 0;
		}
		return numbers.get(index);
	}
	
	private int getFlagState(long joyId, int index) 
	{
		if (!isRuning()) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		return getDayTaskFlag(joyId, index);
	}
	
	private int getDayRewardFlagState(long joyId, int index) {
		if (!isRuning()) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		return getDayRewardFlag(joyId, index);
	}
	
	@Override
	public boolean canShow(long joyId) {
		if (!super.canShow(joyId)) {
			return false;
		}
		return !doneJoyIds.contains(joyId);
	}
	
	private int getDayTaskFlag(long joyId, int index) 
	{
		if (!dayTaskFlags.containsKey(joyId)) {
			return 0;
		}
		
		List<Integer> numbers = dayTaskFlags.get(joyId);
		if (index >= numbers.size()) {
			GameLog.error(MessageFormat.format("getDayTaskFlag index out of bounds joyId={0} index={1}", joyId, index));
			return 0;
		}
		return numbers.get(index);
	}
	
	private void initDayTaskFlags(long joyId)
	{
		int day = getDay(joyId);
		List<Integer> numbers = new ArrayList<Integer>();
		int num = dayTasks.get(day).size();
		for (int i = 0; i < num; i++) {
			numbers.add(0);
		}
		taskNumbers.put(joyId, numbers);
		dayTaskFlags.put(joyId, new ArrayList<Integer>(numbers));
	}
	
	private void setDayTaskFlag(long joyId, int index, boolean receive) 
	{
		if (!dayTaskFlags.containsKey(joyId)) {
			initDayTaskFlags(joyId);
			GameLog.error("setDayTaskFlag but not have joyId="+joyId);
		}
		List<Integer> numbers = dayTaskFlags.get(joyId);
		numbers.set(index, receive?ActvtCommonState.RECEIVED.ordinal():ActvtCommonState.FINISH.ordinal());
	}
 
	@Override
	public String getStateStr() {
		return JSON.toJSONString(doneJoyIds) + STATE_STR_SPLIT_CH + JSON.toJSONString(tickSeconds) + STATE_STR_SPLIT_CH + JSON.toJSONString(taskNumbers)
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(dayRewardFlags) + STATE_STR_SPLIT_CH + JSON.toJSONString(dayTaskFlags);
	} 

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		doneJoyIds = JSON.parseObject(strs[0], new TypeReference<List<Long>>(){});
		tickSeconds = JSON.parseObject(strs[1], new TypeReference<Map<Long, Integer>>(){});
		taskNumbers = JSON.parseObject(strs[2], new TypeReference<Map<Long, List<Integer>>>(){});
		dayRewardFlags = JSON.parseObject(strs[3], new TypeReference<Map<Long, List<Integer>>>(){});
		dayTaskFlags = JSON.parseObject(strs[4], new TypeReference<Map<Long, List<Integer>>>(){});
	}

	@Override
	public int getReceiveableNum(long joyId)
	{
		int num = 0;
		int day = getDay(joyId);
		if (getDayRewardFlag(joyId, day) == 1 && getDayRewardFlag(joyId, day) == 0) {
			num++;
		}
		
		List<ActvtTask> taskList = getDayTasks(joyId);
		for (int i = 0; i < taskList.size(); i++)
		{	
			if (getDayTaskFlag(joyId, day) == 1 && getDayTaskFlag(joyId, day) == 0) {
				num++;
			}
		}
		return num;
	}
}
