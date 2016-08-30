package com.joymeng.slg.domain.actvt.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.DTManager.SearchFilter;
import com.joymeng.slg.domain.actvt.data.Activity;
import com.joymeng.slg.domain.actvt.data.Activity_armyrebellion;
import com.joymeng.slg.domain.actvt.data.Activity_reward;
import com.joymeng.slg.domain.actvt.data.Activity_soldierscore;
import com.joymeng.slg.domain.actvt.data.Activity_task;
import com.joymeng.slg.domain.object.army.data.Army;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.UnionManager;
import com.joymeng.slg.union.impl.UnionMember;

public class ArmyRebellion  extends Actvt  
{
	private Activity_armyrebellion armyRebellion;
	private Map<String,Integer> soldierScores = new HashMap<String, Integer>();
	private Map<Integer, List<Activity_reward>> roleRankRewards = new HashMap<Integer, List<Activity_reward>>();
	private Map<Integer, List<Activity_reward>> unionRankRewards = new HashMap<Integer, List<Activity_reward>>();
	private Map<String, Activity_task> tasks = new HashMap<String, Activity_task>();
	private List<Long> removeList = new ArrayList<Long>();
	
	private Map<Long, Integer> tickSeconds = new HashMap<Long, Integer>();
	private Map<Long, Integer> doneUnionIds =  new HashMap<Long, Integer>();
	private Map<Long, Integer> roleFailNum = new HashMap<Long, Integer>();
	private Map<Long,Integer> unionScores = new HashMap<Long,Integer>();
	private Map<Long,Integer> roleScores = new HashMap<Long,Integer>();
	
	private List<Long> unionRanks = new ArrayList<Long>();
	private List<Long> roleRanks = new ArrayList<Long>();
	
	private int REBELL_ATTACK_INTERVAL = 1800;
	
	class UnionComparator implements Comparator<Long> {
		@Override
		public int compare(Long uid1, Long uid2) {
			
			if (getUnionScore(uid1) > getUnionScore(uid2)) {
				return -1;
			}
			if (getUnionScore(uid1) < getUnionScore(uid2)) {
				return 1;
			}
			return 0;
		}
	}
	private UnionComparator unionComparator = new UnionComparator();
	
	class RoleComparator implements Comparator<Long> {
		@Override
		public int compare(Long joyId1, Long joyId2) {
			
			if (getRoleScore(joyId1) > getRoleScore(joyId2)) {
				return -1;
			}
			if (getRoleScore(joyId1) < getRoleScore(joyId2)) {
				return 1;
			}
			return 0;
		}
	}
	private RoleComparator roleComparator = new RoleComparator();
	
	private int getRoleScore(long joyId)
	{
		Role role = world.getRole(joyId);
		if (role == null) {
			return 0;
		}
		if (!roleScores.containsKey(joyId)) {
			return 0;
		}
		return roleScores.get(joyId);
	}
	
	private int getUnionScore(long unionId)
	{
		UnionBody union = unionManager.search(unionId);
		if (union == null) {
			return 0;
		}
		if (!unionScores.containsKey(unionId)) {
			return 0;
		}
		return unionScores.get(unionId);
	}
	
	@Override
	public boolean init(Activity actvt) 
	{
		if (!super.init(actvt)) {
			return false;
		}
		load();
		return true;
	}
	
	private boolean rebellAttack(long unionId)
	{
		UnionBody union = unionManager.search(unionId);
		if (union == null) {
			return false;
		}
		
		boolean flag = false;
		List<UnionMember> members = union.getMembers();
		for (int i = 0; i < members.size(); i++)
		{
			UnionMember member = members.get(i);
			long joyId = member.getUid();
			if (!roleFailNum.containsKey(joyId) || roleFailNum.get(joyId) < armyRebellion.getFailNum()) {
				flag = true;
				// send rebellion attack
			}
		}
		
		return flag;
	}
	
	@Override
	public void tick()
	{
		for (Map.Entry<Long, Integer> entry : tickSeconds.entrySet())
		{
			long unionId = entry.getKey();
			int seconds = entry.getValue();
			if (++seconds >= REBELL_ATTACK_INTERVAL) {
				tickSeconds.put(unionId, 0);
				if (!rebellAttack(unionId)) {
					removeList.add(unionId);
				}
			}
			else {
				tickSeconds.put(unionId, seconds);
			}
		}
		
		for (int i = 0; i < removeList.size(); i++)
		{
			long unionId = removeList.get(i);
			tickSeconds.remove(unionId);
			doneUnionIds.put(unionId, 1);
		}
		if (!removeList.isEmpty()) {
			removeList.clear();
		}
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		refreshRanks();
		
//		long joyId = role.getId();
//		long unionId = role.getUnionId();
		
		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());
		module.add(activity.getDetailDesc());
		
		module.add(soldierScores.size());
		for (Map.Entry<String, Integer> entry : soldierScores.entrySet())
		{
			String armyId = entry.getKey();
			int score = entry.getValue();
			Army armyBase = dataManager.serach(Army.class, armyId);
			module.add(String.format("杀死一个", armyBase.getArmyName()));
			module.add(score);
		}
		
		module.add(roleRankRewards.size());
		for (int i = 0; i < roleRankRewards.size(); i++)
		{
			List<Activity_reward> rewards = roleRankRewards.get(i+1);
			module.add(Activity_reward.toString(rewards));
		}
		
		module.add(unionRankRewards.size());
		for (int i = 0; i < unionRankRewards.size(); i++)
		{
			List<Activity_reward> rewards = unionRankRewards.get(i+1);
			module.add(Activity_reward.toString(rewards));
		}

//		module.add(getRoleScore(joyId));
//		module.add(getRoleRank(joyId));
//		
//		module.add(getUnionScore(unionId));
//		module.add(getUnionRank(unionId));
		
//		module.add(canManualStart(role)?1:0);
	}
	
//	private int getUnionRank(long unionId)
//	{
//		for (int i = 0; i < unionRanks.size(); i++) {
//			if (unionRanks.get(i) == unionId) {
//				return i+1;
//			}
//		}
//		return 0;
//	}
//	
//	private int getRoleRank(long joyId)
//	{
//		for (int i = 0; i < roleRanks.size(); i++) {
//			if (roleRanks.get(i) == joyId) {
//				return i+1;
//			}
//		}
//		return 0;
//	}
	
	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) 
	{
//		long joyId = role.getId();
//		module.add(getScore(joyId));
//		module.add(getRank(joyId));
//		module.add("当你达到目标3的时候，就可以参与排名，获得排名奖励");
		
//		module.add(num);
//		for (int i = 0; i < num; i++) {
//			long id = ranks.get(i);
//			String name = world.getRole(id).getName(); // SN 耗时的查询操作
//			int score = getScore(id);
//			module.add(name);
//			module.add(score);
//		}
		
		return false;
	}
	
	private void sendEmailReward(long joyId, String content, List<Activity_reward> rewards)
	{
		List<BriefItem> annex = new ArrayList<BriefItem>();
		for (int i = 0; i < rewards.size(); i++)
		{
			Activity_reward reward = rewards.get(i);
			BriefItem bri = new BriefItem(reward.getType(), reward.getsID(), reward.getNum());
			annex.add(bri);
		}
		chatMgr.creatSystemEmail(content, annex, joyId);
	}
	
	public void reset(String value, String data)
	{
		refreshRanks();
		
		// 邮件分发联盟奖励与个人奖励
		for (int i = 0; i < roleRanks.size(); i++)
		{
			long joyId = roleRanks.get(i);
			List<Activity_reward> rewards = roleRankRewards.get(i+1);
			sendEmailReward(joyId, String.format("恭喜您在本次暴君叛乱的活动中获得个人积分第%d名", i+1), rewards);
		}
		
		for (int i = 0; i < unionRanks.size(); i++)
		{
			long unionId = unionRanks.get(i);
			UnionBody union = UnionManager.getInstance().search(unionId);
			if (union != null)
			{
				List<Activity_reward> rewards = roleRankRewards.get(i+1);
				List<UnionMember> members = union.getMembers();
				for (int j = 0; j < members.size(); j++)
				{
					sendEmailReward(members.get(j).getUid(), String.format("恭喜您在本次暴君叛乱的活动中获得联盟积分第%d名", i+1), rewards);
				}
			}
		}
		
		doneUnionIds.clear();
		tickSeconds.clear();
		roleFailNum.clear();
		roleScores.clear();
		unionScores.clear();
	}
	
	private void refreshRanks()
	{
		unionRanks.clear();
		unionRanks.addAll(unionScores.keySet());
		Collections.sort(unionRanks, unionComparator);
		
		roleRanks.clear();
		roleRanks.addAll(roleScores.keySet());
		Collections.sort(roleRanks, roleComparator);
	}
	
	public void armyRebellOver(String value, String data)
	{
		String[] strs = data.split("#");
		long joyId = Long.parseLong(strs[0]);
		Role role = world.getOnlineRole(joyId);
		if (role == null) {
			return;
		}
		
		boolean success = strs[1].equals("1");
		if (success)
		{
			// 计算积分 soldierScores
			int addScore = 0;
			String[] values = strs[2].split("_");
			for (int i = 0; i < values.length; i+=2)
			{
				String armyId = values[0];
				int num = Integer.parseInt(values[1]);
				int score = 1;
				if (soldierScores.containsKey(armyId)) {
					score = soldierScores.get(armyId);
				}
				addScore += score*num;
			}
			
			int curScore = 0;
			if (roleScores.containsKey(joyId)) {
				curScore = roleScores.get(joyId);
			}
			roleScores.put(joyId, curScore+addScore);

			long unionId = role.getUnionId();
			UnionBody union = unionManager.search(unionId);
			if (union != null)
			{
				curScore = 0;
				if (unionScores.containsKey(unionId)) {
					curScore = unionScores.get(unionId);
				}
				unionScores.put(unionId, curScore+addScore);
			}
		}
		else 
		{
			int num = 0;
			if (roleFailNum.containsKey(joyId)) {
				num = roleFailNum.get(joyId);
			}
			roleFailNum.put(joyId, num+1);
		}
	}
	
	private boolean canManualStart(Role role)
	{
		// check running
		if (!isRuning()) {
			return false;
		}
		
		// check if join union
		long unionId = role.getUnionId();
		UnionBody union = unionManager.search(unionId);
		if (union == null) {
			return false;
		}
		
		// check if leader
		UnionMember um = union.getUnionMemberById(role.getId());
		if (um == null || !um.isLeader()) {
			return false;
		}
		
		// check if already start
		if (tickSeconds.containsKey(unionId)) {
			return false;
		}
		if (doneUnionIds.containsKey(unionId)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean manualStart(Role role) 
	{
		if (!canManualStart(role)) {
			return false;
		}

		tickSeconds.put(role.getUnionId(), 0);
		return true;
	}

	@Override
	public String getStateStr() {
		return JSON.toJSONString(tickSeconds) + SPCH + JSON.toJSONString(doneUnionIds) + SPCH + JSON.toJSONString(roleFailNum)
		 + SPCH + JSON.toJSONString(unionScores) + SPCH + JSON.toJSONString(roleScores);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		tickSeconds = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
		doneUnionIds = JSON.parseObject(strs[1], new TypeReference<Map<Long, Integer>>(){});
		roleFailNum = JSON.parseObject(strs[2], new TypeReference<Map<Long, Integer>>(){});
		unionScores = JSON.parseObject(strs[3], new TypeReference<Map<Long, Integer>>(){});
		roleScores = JSON.parseObject(strs[4], new TypeReference<Map<Long, Integer>>(){});
	}

	@Override
	public void load() {
		armyRebellion = actvtMgr.serach(Activity_armyrebellion.class, "1");
		REBELL_ATTACK_INTERVAL = armyRebellion.getTime() * 60;
		
		List<Activity_soldierscore> ssList = actvtMgr.serachList(Activity_soldierscore.class, new SearchFilter<Activity_soldierscore>() {
			@Override
			public boolean filter(Activity_soldierscore data) {
				return true;
			}
		});
		for (int i = 0; i < ssList.size(); i++) 
		{
			Activity_soldierscore ss = ssList.get(i);
			soldierScores.put(ss.getId(), ss.getScore());
		}
		
		for (int i = 0; i < armyRebellion.getRoleRankNum(); i++)
		{
			final int rank = i+ 1;
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
				@Override
				public boolean filter(Activity_reward data) {
					return data.getrID().equals(getActivity().getTypeId()+"_"+rank);
				}
			});
//			List<Activity_reward> rewardList = actvtMgr.getReward(getActivity().getTypeId()+"_"+(i+1));
			roleRankRewards.put(rank, rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
		
		for (int i = 0; i < armyRebellion.getUnionRankNum(); i++)
		{
			final int rank = i+ 1;
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class, new SearchFilter<Activity_reward>() {
				@Override
				public boolean filter(Activity_reward data) {
					return data.getrID().equals(getActivity().getTypeId()+"_r"+rank);
				}
			});
//			List<Activity_reward> rewardList = actvtMgr.getReward(getActivity().getTypeId()+"_r"+(i+1));
			unionRankRewards.put(i+1, rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
		
		List<Activity_task> taskList = actvtMgr.serachList(Activity_task.class, new SearchFilter<Activity_task>() {
			@Override
			public boolean filter(Activity_task data) {
				return data.getActivity().equals(getActivity().getTypeId()); 
			}
		});
		for (int i = 0; i < taskList.size(); i++)
		{
			Activity_task task = taskList.get(i);
			tasks.put(task.gettID(), task);
		}
	}

	
}
