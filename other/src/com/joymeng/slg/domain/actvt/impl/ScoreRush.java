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
import com.joymeng.slg.domain.actvt.data.Activity_scorerush;
import com.joymeng.slg.domain.actvt.data.Activity_task;
import com.joymeng.slg.domain.event.impl.ActvtEvent.ActvtEventType;
import com.joymeng.slg.domain.object.role.Role;

public class ScoreRush extends Actvt 
{
	private Map<Long, Integer> scores = new HashMap<Long, Integer>();
	private Map<Long, Long> rewardFlags = new HashMap<Long, Long>();
	
	private List<Long> ranks = new ArrayList<Long>();
	private Activity_scorerush scoreRush;
	private List<List<Activity_reward>> rewards = new ArrayList<List<Activity_reward>>();
	private Map<Integer, List<Activity_reward>> rankRewards = new HashMap<Integer, List<Activity_reward>>();
	private Map<String, Activity_task> tasks = new HashMap<String, Activity_task>();
	private List<Activity_task> taskList;
	private List<Activity_task> taskListShow = new ArrayList<Activity_task>();

	class ScoreComparator implements Comparator<Long> {
		@Override
		public int compare(Long id1, Long id2) {
			if (scores.get(id1) > scores.get(id2)) {
				return -1;
			}
			if (scores.get(id1) < scores.get(id2)) {
				return 1;
			}
			return 0;
		}
	}
	private ScoreComparator scoreComparator = new ScoreComparator();
	
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
			
			
//			if (task1.getType().compareTo(task2.getType()) == 0) 
//			{
//				if (task1.getDestID().compareTo(task2.getDestID()) == 0)
//				{
//					if (task1.getNumber() > task2.getNumber()) {
//						return 1;
//					}
//					if (task1.getNumber() < task2.getNumber()) {
//						return -1;
//					}
//					return 0;
//				}
//				else {
//					return task1.getDestID().compareTo(task2.getDestID());
//				}
//			} 
//			else {
//				return task1.getType().compareTo(task2.getType());
//			}
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
		if (!isRuning()) {
			return;
		}
		
		boolean flag = true;
		for (Map.Entry<String, Activity_task> entry : tasks.entrySet()) 
		{
			Activity_task task = entry.getValue();
			if (task.getType().equals(ActvtEventType.ACCELERATE.getName())) {
				flag = false;
				break;
			}
		}
		if (flag) {
			return;
		}
		
		String[] strs = data.split("#");
		long joyId = Long.parseLong(strs[0]);
		long time = Long.parseLong(strs[1]) / 60;
		
		List<Integer> accTimes = scoreRush.getAccTimes();
		List<Integer> accScores = scoreRush.getAccScores();
		int score = 0;
		for (int i = 0; i < accTimes.size(); i++)
		{
			score += (time/accTimes.get(i)*accScores.get(i));
			time -= (time/accTimes.get(i)*accTimes.get(i));
		}
		
		addScore(joyId, score);
	}

	@Override
	public void makeUpDetailModule(ClientMod module, Role role) {
		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());

		long joyId = role.getId();
		module.add(getScore(joyId));
		module.add(getRank(joyId));
		module.add(getTheScore(0));
		module.add(getTheScore(1));
		module.add(getTheScore(2));

		for (int i = 0; i < 3; i++) {
			module.add(getTheScore(i));
			module.add(Activity_reward.toString(rewards.get(i)));
			module.add(getFlagState(joyId, i));
		}
		
		module.add(scoreRush.getScoreItems());
		if (activity.getTypeId().equals("ScoreRush2") || activity.getTypeId().equals("ScoreRush3")) {
			module.add(scoreRush.getScoreDesc());
		}
		else {
			module.add("");
		}

		module.add(taskListShow.size());
		for (int i = 0; i < taskListShow.size(); i++) 
		{
			Activity_task task = taskListShow.get(i);
			module.add(task.getContent());
			module.add(String.valueOf(task.getNumber()));
		}
//		module.add(tasks.size());
//		for (Map.Entry<String, Activity_task> entry : tasks.entrySet())
//		{
//			Activity_task task = entry.getValue();
//			module.add(task.getContent());
//			module.add(String.valueOf(task.getNumber()));
//		}

		int num = scoreRush.getRankNum();
//		num = num>20?20:num;
		module.add(num);
		for (int i = 1; i <= num; i++)
		{
			List<Activity_reward> rewards = rankRewards.get(i);
			module.add(Activity_reward.toString(rewards));
		}

//		System.out.println(module.getParams().toString());
	}

	@Override
	public boolean receiveReward(Role role, int index) {
		if (getFlagState(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
			return false;
		}
		rewardPlayer(role, Activity_reward.toString(rewards.get(index)));
		setFlag(role.getId(), index, 1);
		// TEST11 积分活动奖励  领取  rewards
		List<Activity_reward> rew = rewards.get(index);
		StringBuffer sb = new StringBuffer();
		sb.append(getActivity().getTypeId()).append(GameLog.SPLIT_CHAR);
		sb.append("receive"+index).append(GameLog.SPLIT_CHAR);
		for(int j=0;j<rew.size();j++){
			Activity_reward reward = rew.get(j);
			sb.append(reward.getsID());
			sb.append(GameLog.SPLIT_CHAR);
			sb.append(reward.getNum());
			sb.append(GameLog.SPLIT_CHAR);
		}
		String newStr = sb.toString().substring(0, sb.toString().length() - 1);
		NewLogManager.activeLog(role, "activity_integration_reward",newStr);
		return true;
	}

	public int getScore(long joyId) {
		if (scores.containsKey(joyId)) {
			return scores.get(joyId);
		}
		return 0;
	}

	public int getRank(long joyId) {
//		refreshRank();
		for (int i = 0; i < ranks.size(); i++) {
			if (ranks.get(i) == joyId) {
				return i + 1;
			}
		}
		return 0;
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
			String taskID = strs[1];
			int num = 1;
			if (strs.length > 2) {
				num = Integer.parseInt(strs[2]);
			}

			for (Map.Entry<String, Activity_task> entry : tasks.entrySet()) 
			{
				Activity_task task = entry.getValue();
				if (task.getTaskID().equals(taskID)) {
					addScore(joyId, num * task.getNumber());
				}
			}
		} catch (Exception e) {
			GameLog.error(e.getMessage());
		}
	}
	
	private int getFlagState(long joyId, int index) {
		if (getFlag(joyId, index, 1) == 1) {
			return ActvtCommonState.RECEIVED.ordinal();
		}
		if (getFlag(joyId, index, 0) == 1) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.NOT_FINISH.ordinal();
	}

	public void addScore(long joyId, int add) {
		int score = 0;
		if (scores.containsKey(joyId)) {
			score = scores.get(joyId);
		}
		int oldScore = score;
		score += add;
		scores.put(joyId, score);

		for (int i = 0; i < 3; i++) {
			int dstScore = getTheScore(i);
			if (oldScore < dstScore && score >= dstScore) {
				setFlag(joyId, i, 0);
//				Notify(scoreTags[i], String.valueOf(joyId));
				if (i == 2) {
					ranks.add(joyId);
				}
			}
		}
	}

	// 获取排行榜列表和最终发奖时，排序
	private void refreshRank() {
		Collections.sort(ranks, scoreComparator);
		
		for (int i = 0; i < ranks.size(); ) 
		{
			long joyId = ranks.get(i);
			Role role = world.getRole(joyId);
			if (role == null) {
				ranks.remove(i);
			}
			else {
				i++;
			}
		}
	}

	private long getFlag(long joyId, int index, int receive) {
		if (!rewardFlags.containsKey(joyId)) {
			return 0;
		} else {
			long flag = rewardFlags.get(joyId);
			return getBit(flag, index * 2 + receive);
		}
	}

	private void setFlag(long joyId, int index, int receive) {
		long flag = 0;
		if (rewardFlags.containsKey(joyId)) {
			flag = rewardFlags.get(joyId);
		}
		flag = setBit(flag, index * 2 + receive, 1);
		rewardFlags.put(joyId, flag);
		
		if (receive == 0) {
			actvtMgr.sendActvtTip(joyId);
		}
	}

	private int getTheScore(int index) {
		if (index == 0) {
			return scoreRush.getScoreI();
		}
		if (index == 1) {
			return scoreRush.getScoreII();
		}
		return scoreRush.getScoreIII();
	}
	
	@Override
	public void hotLoadEnd()
	{
		super.hotLoadEnd();
		rewardScoreRushRank("", "");
	}

	public void rewardScoreRushRank(String value, String data) {
		refreshRank();
		for (int i = 0; i < scoreRush.getRankNum() && i < ranks.size(); i++)
		{
			int rank = i + 1;
			long joyId = ranks.get(i);
			List<Activity_reward> rewards = rankRewards.get(rank);
//			rewardPlayer(joyId, Activity_reward.toString(rewards));
			sendEmail(joyId, String.format("恭喜你在“%s”活动中获得“第%d名”，获得以下奖励。", getActivity().getName(), rank), rewards);
			
			// TEST11 积分活动奖励  排行奖励  rewards
			StringBuffer sb = new StringBuffer();
			sb.append(getActivity().getTypeId()).append(GameLog.SPLIT_CHAR);
			sb.append(i+1).append(GameLog.SPLIT_CHAR);
			for(int j=0;j<rewards.size();j++){
				Activity_reward reward = rewards.get(j);
				sb.append(reward.getsID());
				sb.append(GameLog.SPLIT_CHAR);
				sb.append(reward.getNum());
				sb.append(GameLog.SPLIT_CHAR);
			}
			String newStr = sb.toString().substring(0, sb.toString().length() - 1);
			Role role = world.getRole(joyId);
			NewLogManager.activeLog(role, "activity_integration_reward",newStr);
		}

//		long joyId = ranks.get(0);
//		for (int i = 0; i < scoreRush.getRankNum(); i++)
//		{
//			int rank = i + 1;
//			List<Activity_reward> rewards = rankRewards.get(rank);
//			sendEmail(joyId, String.format("恭喜你在“%s”活动中获得“第%d名”，获得以下奖励。", getActivity().getName(), rank), rewards);
//		}
	}

	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) {
		refreshRank();

		long joyId = role.getId();
		module.add(getScore(joyId));
		module.add(getRank(joyId));
		module.add("当你达到目标3的时候，就可以参与排名，获得排名奖励");

		int num = ranks.size();
		if (ranks.size() > scoreRush.getRankNum()) {
			num = scoreRush.getRankNum();
		}
		module.add(num);
		for (int i = 0; i < num; i++) {
			long id = ranks.get(i);
			String name = world.getRole(id).getName(); // SN 耗时的查询操作
			int score = getScore(id);
			module.add(name);
			module.add(score);
		}
		
		return true;
	}

	@Override
	public String getStateStr() {
		return JSON.toJSONString(scores) + SPCH + JSON.toJSONString(rewardFlags);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		scores = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
		rewardFlags = JSON.parseObject(strs[1], new TypeReference<Map<Long, Long>>(){});
		
		ranks.clear();
		int score3 = getTheScore(2);
		for (Map.Entry<Long, Integer> entry : scores.entrySet())
		{
			long joyId = entry.getKey();
			int score = entry.getValue();
			if (score >= score3) {
				ranks.add(joyId);
			}
		}
		
		Collections.sort(ranks, scoreComparator);
	}

	@Override
	public void load() {
//		scoreRush = actvtMgr.serach(Activity_scorerush.class, "1");
		scoreRush = actvtMgr.serach(Activity_scorerush.class, new SearchFilter<Activity_scorerush>() {
			@Override
			public boolean filter(Activity_scorerush data) {
				return data.getTypeId().equals(getActivity().getTypeId());
			}
		});

		rewards.clear();
		String typeId = getActivity().getTypeId();
		String[] rewardIds = {typeId+"_r1", typeId+"_r2", typeId+"_r3"};
		for (int i = 0; i < rewardIds.length; i++) 
		{
			final String rewardId = rewardIds[i];
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
				@Override
				public boolean filter(Activity_reward data) {
					return data.getrID().equals(rewardId);
				}
			});
			rewards.add(rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
//		rewards.add(actvtMgr.getReward(scoreRush.getReward1()));
//		rewards.add(actvtMgr.getReward(scoreRush.getReward2()));
//		rewards.add(actvtMgr.getReward(scoreRush.getReward3()));
		
		rankRewards.clear();
		for (int i = 0; i < scoreRush.getRankNum(); i++) 
		{
			final int rank = i+1;
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
				@Override
				public boolean filter(Activity_reward data) {
					return data.getrID().equals(getActivity().getTypeId()+"_"+rank);
				}
			});
//			List<Activity_reward> rewardList = actvtMgr.getReward(getActivity().getTypeId()+"_"+(i+1));
			rankRewards.put(rank, rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
		
		taskList = actvtMgr.serachList(Activity_task.class, new SearchFilter<Activity_task>() {
			@Override
			public boolean filter(Activity_task data) {
//				return data.gettID().startsWith(getActivity().getTypeId());
				return data.getActivity().equals(getActivity().getTypeId());
			}
		});
		tasks.clear();
		for (int i = 0; i < taskList.size(); i++)
		{
			Activity_task task = taskList.get(i);
			tasks.put(task.gettID(), task);
		}
		Collections.sort(taskList, taskComparator);
		
		taskListShow.clear();
		for (int i = 0; i < taskList.size(); i++)
		{
			Activity_task task = taskList.get(i);
			if (task.getIsShow() == 1) {
				taskListShow.add(task);
			}
		}
	}

	@Override
	public int getReceiveableNum(long joyId)
	{
		int num = 0;
		for (int i = 0; i < 3; i++)
		{
			if (getFlagState(joyId, i) == ActvtCommonState.FINISH.ordinal()) {
				num++;
			}
		}
		return num;
	}
	
//	public static void main(String[] args)
//	{
//		List<Integer> tlist = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,0));
//		System.out.println(tlist.toString());
//		
//		for (int i = 0; i < tlist.size(); )
//		{
//			if (tlist.get(i)%2 == 1) {
//				tlist.remove(i);
//			}
//			else {
//				i++;
//			}
//		}
//		System.out.println(tlist.toString());
//	}
}
