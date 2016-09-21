package com.joymeng.slg.domain.actvt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class ScoreRush extends Actvt 
{
	private static final int SCORE_STAGE_NUM = 3;
	
	private Map<Long, Integer> scores = new HashMap<Long, Integer>();
	private Map<Long, Long> rewardFlags = new HashMap<Long, Long>();
	
	private List<Long> ranks = new ArrayList<Long>();
	
	private List<ActvtReward> rewards = new ArrayList<ActvtReward>();
	private List<ActvtReward> rankRewards = new ArrayList<ActvtReward>();
	private List<Integer> scoreStages = new ArrayList<Integer>();
	private List<Integer> accTimes = new ArrayList<Integer>();
	private List<Integer> accScores = new ArrayList<Integer>();
	private String scoreItemsDesc;
	
	private List<ActvtTask> taskListShow = new ArrayList<ActvtTask>();
	private List<String> eventArgs = new ArrayList<String>();

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
	
	@Override
	public void start()
	{
		super.start();
		
		EvntManager.getInstance().Remove(this);
		List<ActvtTask> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++)
		{
			ActvtTask task = taskList.get(i);
			EvntManager.getInstance().Listen(task.getType(), this);
		}
		EvntManager.getInstance().Listen("taskEvent", this);
	}
	
	@Override
	public void end()
	{
		super.end();
		rewardScoreRushRank();
	}
	
	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		String str = eleSpecial.getAttribute("scoreStages");
		String[] strs = str.split(",");
		if (strs.length != SCORE_STAGE_NUM) {
			throw new Exception("id="+getId()+" scores num is not 3");
		}
		
		scoreStages.clear();
		for (int i = 0; i < strs.length; i++) {
			scoreStages.add(Integer.parseInt(strs[i]));
		}
		scoreItemsDesc = eleSpecial.getAttribute("scoreItemsDesc");
		
		accTimes.clear();
		str = eleSpecial.getAttribute("accTimes");
		if (!str.isEmpty()) 
		{
			strs = str.split(",");
			for (int i = 0; i < strs.length; i++) {
				accTimes.add(Integer.parseInt(strs[i]));
			}
		}
		
		accScores.clear();
		str = eleSpecial.getAttribute("accScores");
		if (!str.isEmpty())
		{
			strs = str.split(",");
			for (int i = 0; i < strs.length; i++) {
				accScores.add(Integer.parseInt(strs[i]));
			}
		}
		
		if (accTimes.size() != accScores.size()) {
			throw new Exception("id="+getId()+" accTimes and accScores is not same");
		}
		
		String str1 = eleSpecial.getAttribute("reward1");
		String str2 = eleSpecial.getAttribute("reward2");
		String str3 = eleSpecial.getAttribute("reward3");
		rewards.clear();
		rewards.add(getReward(str1));
		rewards.add(getReward(str2));
		rewards.add(getReward(str3));
		
		int rank = 1;
		rankRewards.clear();
		List<ActvtReward> rewardList = getRewardList();
		for (int i = 0; i < rewardList.size(); i++)
		{
			ActvtReward reward = rewardList.get(i);
			String id = reward.getId();
			if (id.equals(str1) || id.equals(str2) || id.equals(str3)) {
				continue;
			}
			String[] ids = id.split("-");
			if (ids.length == 1) {
				if (rank == Integer.parseInt(ids[0])) {
					rankRewards.add(reward);
					rank++;
				}
				else {
					throw new Exception("id="+getId()+" rank rewards not continuous");
				}
			}
			else {
				int min = Integer.parseInt(ids[0]);
				int max = Integer.parseInt(ids[1]);
				if (rank != min) {
					throw new Exception("id="+getId()+" rank rewards not continuous");
				}
				for (int r = min; r <= max; r++) {
					rankRewards.add(reward);
				}
				rank = max + 1;
			}
		}
		
		taskListShow.clear();
		List<ActvtTask> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++)
		{
			ActvtTask task = taskList.get(i);
			if (task.isShow()) {
				taskListShow.add(task);
			}
		}
	}
	
	public void accelerate(Object... datas)
	{
		boolean flag = true;
		List<ActvtTask> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++)
		{
			ActvtTask task = taskList.get(i);
			if (task.getType().equals(ActvtEventType.ACCELERATE.getName())) {
				flag = false;
				break;
			}
		}
		if (flag) {
			return;
		}
		
		long joyId = Long.parseLong(datas[0].toString());
		long time = Long.parseLong(datas[1].toString()) / 60;
		
		int score = 0;
		for (int i = 0; i < accTimes.size(); i++)
		{
			score += (time/accTimes.get(i)*accScores.get(i));
			time -= (time/accTimes.get(i)*accTimes.get(i));
		}
		
		addScore(joyId, score);
	}
	
	@Override
	public void execute(String event, Object... datas)
	{
		if (!isRuning()) { 
			return;
		}
		
		if (event.equals("accelerate")) {
			accelerate(datas);
		}
		else if (event.equals("taskEvent")) {
			taskEvent(datas);
		}
	}

	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());

		long joyId = role.getId();
		module.add(getScore(joyId));
		module.add(getRank(joyId));
		module.add(getTheScore(0));
		module.add(getTheScore(1));
		module.add(getTheScore(2));

		for (int i = 0; i < SCORE_STAGE_NUM; i++) {
			module.add(getTheScore(i));
			module.add(rewards.get(i).getItems());
			module.add(getFlagState(joyId, i));
		}
		
		module.add("");
		module.add(scoreItemsDesc);
		module.add(taskListShow.size());
		for (int i = 0; i < taskListShow.size(); i++) 
		{
			ActvtTask task = taskListShow.get(i);
			module.add(task.getDesc());
			module.add(String.valueOf(task.getNum()));
		}

		module.add(rankRewards.size());
		for (int i = 0; i < rankRewards.size(); i++)
		{
			ActvtReward reward = rankRewards.get(i);
			module.add(reward.getItems());
		}
//		System.out.println(module.getParams().toString());
	}

	@Override
	public boolean receiveReward(Role role, int index) 
	{
		if (state == ActvtState.PREPARE) {
			return false;
		}
		
		if (getFlagState(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
			return false;
		}
		
		rewardPlayer(role, rewards.get(index).getItems());
		setFlag(role.getId(), index, 1);
		return true;
	}

	public int getScore(long joyId) 
	{
		if (scores.containsKey(joyId)) {
			return scores.get(joyId);
		}
		return 0;
	}

	public int getRank(long joyId) 
	{
//		refreshRank();
		for (int i = 0; i < ranks.size(); i++) {
			if (ranks.get(i) == joyId) {
				return i + 1;
			}
		}
		return 0;
	}
	
	@Override
	public void taskEvent(Object... datas)
	{
		try {
			long joyId = Long.parseLong(datas[0].toString());
			String taskID = datas[1].toString();
			int num = Integer.parseInt(datas[2].toString());
			
			eventArgs.clear();
			for (int i = 3; i < datas.length; i++) {
				eventArgs.add(datas[i].toString());
			}
			
			List<ActvtTask> taskList = getTaskList();
			for (int i = 0; i < taskList.size(); i++) 
			{
				ActvtTask task = taskList.get(i);
				if (task.check(taskID, eventArgs)) {
					addScore(joyId, num * task.getNum());
				}
			}
		} catch (Exception e) {
			GameLog.error(e.getMessage());
		}
	}
	
	private int getFlagState(long joyId, int index) {
		if (!isRuning()) {
			ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getFlag(joyId, index, 1) == 1) {
			return ActvtCommonState.RECEIVED.ordinal();
		}
		if (getFlag(joyId, index, 0) == 1) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.NOT_FINISH.ordinal();
	}

	public void addScore(long joyId, int add) 
	{
		int score = 0;
		if (scores.containsKey(joyId)) {
			score = scores.get(joyId);
		}
		int oldScore = score;
		score += add;
		scores.put(joyId, score);

		for (int i = 0; i < 3; i++) 
		{
			int dstScore = getTheScore(i);
			if (oldScore < dstScore && score >= dstScore) 
			{
				setFlag(joyId, i, 0);
				if (i == 2) {
					ranks.add(joyId);
				}
			}
		}
	}

	// 获取排行榜列表和最终发奖时，排序
	private void refreshRank() {
		Collections.sort(ranks, scoreComparator);
	}

	private long getFlag(long joyId, int index, int receive) 
	{
		if (!rewardFlags.containsKey(joyId)) {
			return 0;
		} else {
			long flag = rewardFlags.get(joyId);
			return getBit(flag, index * 2 + receive);
		}
	}

	private void setFlag(long joyId, int index, int receive) 
	{
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

	private int getTheScore(int index) 
	{
		if (index >= scoreStages.size()) {
			GameLog.error("ScoreRush id="+getId()+" getTheScore index="+index+" out of bounds");
			return 0;
		}
		return scoreStages.get(index);
	}

	public void rewardScoreRushRank() 
	{
		refreshRank();
		ActvtCommon commonData = getCommonData();
		for (int i = 0; i < rankRewards.size() && i < ranks.size(); i++)
		{
			int rank = i + 1;
			long joyId = ranks.get(i);
			ActvtReward reward = rankRewards.get(rank);
			sendEmail(joyId, String.format("恭喜你在“%s”活动中获得“第%d名”，获得以下奖励。", commonData.getName(), rank), reward);		
		}
	}

	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) 
	{
		refreshRank();

		long joyId = role.getId();
		int rankNum = 1;
		module.add(rankNum);
		
		module.add(getScore(joyId));
		module.add(getRank(joyId));
		module.add("当你达到目标3的时候，就可以参与排名，获得排名奖励");

		int num = ranks.size();
		if (ranks.size() > rankRewards.size()) {
			num = rankRewards.size();
		}
		module.add(num);
		for (int j = 0; j < num; j++) {
			long id = ranks.get(j);
			String name = world.getRole(id).getName(); // SN 耗时的查询操作
			int score = getScore(id);
			module.add(name);
			module.add(score);
		}
		
		return true;
	}

	@Override
	public String getStateStr() {
		return JSON.toJSONString(scores) + STATE_STR_SPLIT_CH + JSON.toJSONString(rewardFlags);
	}

	@Override
	public void loadFromData(SqlData data) 
	{
		String[] strs = getStateStrs(data);
		
		scores = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
		rewardFlags = JSON.parseObject(strs[1], new TypeReference<Map<Long, Long>>(){});
		
		int score3 = getTheScore(2);
		for (Map.Entry<Long, Integer> entry : scores.entrySet())
		{
			long joyId = entry.getKey();
			int score = entry.getValue();
			if (score > score3) {
				ranks.add(joyId);
			}
		}
		refreshRank();
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
}
