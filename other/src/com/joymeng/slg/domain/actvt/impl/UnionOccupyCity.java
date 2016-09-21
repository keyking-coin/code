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
import com.joymeng.services.utils.XmlUtils;
import com.joymeng.slg.dao.SqlData;
import com.joymeng.slg.domain.actvt.Actvt;
import com.joymeng.slg.domain.actvt.ActvtCommonState;
import com.joymeng.slg.domain.actvt.ClientMod;
import com.joymeng.slg.domain.actvt.data.ActvtCommon;
import com.joymeng.slg.domain.actvt.data.ActvtReward;
import com.joymeng.slg.domain.evnt.EvntManager;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.UnionManager;
import com.joymeng.slg.union.impl.UnionMember;

public class UnionOccupyCity extends Actvt 
{
	private static final int CITY_DEST_LEVEL_NUM = 3;
	
	private Map<Long, Integer> unionScores = new HashMap<Long, Integer>();
	private Map<Long, Long> unionFlags = new HashMap<Long, Long>();
	private Map<Long, Long> roleFlags = new HashMap<Long, Long>();
	
	private List<UnionBody> unions = new ArrayList<UnionBody>(); // 改成unionId?
	private List<UnionBody> unionsStage = new ArrayList<UnionBody>(); // 改成unionId?
	
	private List<Integer> cityDestLevels = new ArrayList<Integer>();
	private List<Integer> cityLevelScores = new ArrayList<Integer>();
	private List<ActvtReward> rewards = new ArrayList<ActvtReward>();
	private List<ActvtReward> rankRewards = new ArrayList<ActvtReward>();
	List<ActvtReward> rewardShowList = new ArrayList<ActvtReward>();
	

	class ScoreComparator implements Comparator<UnionBody> {
		@Override
		public int compare(UnionBody u1, UnionBody u2) {
			
			if (getUnionScore(u1.getId()) > getUnionScore(u2.getId())) {
				return -1;
			}
			if (getUnionScore(u1.getId()) < getUnionScore(u2.getId())) {
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
		
		stopInnerTimer("statsUnionScores");
		startInnerTimer("0 0/5 * * * ?", "statsUnionScores");
		
		EvntManager.getInstance().Remove(this);
		EvntManager.getInstance().Listen("unionOccupyCity", this);
		EvntManager.getInstance().Listen("createUnion", this);
		EvntManager.getInstance().Listen("dismissUnion", this);
		
		updateUnions();
	}
	
	@Override
	public void end()
	{
		super.end();
		stopInnerTimer("statsUnionScores");
		rewardRankUnions();
	}

	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);
		
		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		String str = eleSpecial.getAttribute("cityDestLevels");
		String[] strs = str.split(",");
		if (strs.length != CITY_DEST_LEVEL_NUM) {
			throw new Exception("id="+getId()+" load special cityDestLevels num is not 3");
		}
		
		cityDestLevels.clear();
		for (int i = 0; i < strs.length; i++) {
			cityDestLevels.add(Integer.parseInt(strs[i]));
		}
		
		cityLevelScores.clear();
		str = eleSpecial.getAttribute("cityLevelScores");
		strs = str.split(",");
		for (int i = 0; i < strs.length; i++) {
			cityLevelScores.add(Integer.parseInt(strs[i]));
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
		rewardShowList.clear();
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
			rewardShowList.add(reward);
		}
	}
	
	@Override
	public void innerTimerCB(String tag)
	{
		if (tag.equals("statsUnionScores")) {
			statsUnionScores();
		}
	}
	
	@Override
	public void execute(String event, Object... datas)
	{
		if (event.equals("createUnion") || event.equals("dismissUnion")) {
			updateUnions();
		}
		else if (event.equals("unionOccupyCity")) {
			unionOccupyCity(datas);
		}
	}
	
	private void rewardRankUnions()
	{
		for (int i = 0; i < rankRewards.size() && i < unionsStage.size(); i++)
		{
			UnionBody union = unionsStage.get(i);
			List<UnionMember> members = union.getMembers();			
			for (int j = 0; j < members.size(); j++)
			{
				long joyId = members.get(j).getUid();
				sendEmail(joyId, String.format("恭喜你的联盟在“%s”活动中获得“第%d名”，获得以下奖励。", getCommonData().getName(), i+1), rankRewards.get(i));
			}
		}
	}
	
	private int getUnionScore(long uid)
	{
		if (unionScores.containsKey(uid)) {
			return unionScores.get(uid);
		}
		return 0;
	}
	
	private int getRank(long uid)
	{
		for (int i = 0; i < unionsStage.size(); i++)
		{
			if (unionsStage.get(i).getId() == uid) {
				return i+1;
			}
		}
		return 0;
	}

	private int getState(long unionId, long joyId, int index)
	{
		if (!isRuning()) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getUnionFlag(unionId,index) == 0) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getRoleFlag(joyId,index) == 0) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.RECEIVED.ordinal();
	}

	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		long unionId = role.getUnionId();
		long joyId = role.getId();
		
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());
		
		module.add(getUnionScore(unionId));
		module.add(getRank(unionId));
		module.add(getOccupyCityMaxLevel(unionId));
		
		module.add(cityDestLevels.size());
		for (int i = 0; i < cityDestLevels.size(); i++)
		{
			module.add(cityDestLevels.get(i));
			module.add(rewards.get(i).getItems());
			module.add(getState(unionId, joyId, i));
		} 
		
		module.add(cityLevelScores.size());
		for (int i = 0; i < cityLevelScores.size(); i++)
		{
			module.add(String.format("占领%d级城市一天", i+1));
			module.add(String.valueOf(cityLevelScores.get(i)));
		}
		
		module.add(rewardShowList.size());
		for (int i = 0; i < rewardShowList.size(); i++)
		{
			ActvtReward reward = rewardShowList.get(i);
			module.add("第"+reward.getId()+"名");
			module.add(reward.getItems());
		}
//		System.out.println(module.getParams().toString());
	}
	
	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) 
	{
		long unionId = role.getUnionId();
		int rankNum = 1;
		module.add(rankNum);
		module.add(getUnionScore(unionId));
		module.add(getRank(unionId));
		module.add("当达到目标1的时候，就可以参与排名，获得排名奖励");

		module.add(unionsStage.size());
		for (int i = 0; i < unionsStage.size(); i++) 
		{
			UnionBody union = unionsStage.get(i);
			module.add(union.getName());
			module.add(getUnionScore(union.getId()));
		}
		
		return true;
	}
	
	private int getOccupyCityMaxLevel(long unionId) 
	{
		int maxLevel = 0;
		for (int i = 0; i < cityDestLevels.size(); i++)
		{
			if (getUnionFlag(unionId, i) == 1) {
				maxLevel = cityDestLevels.get(i);
			}
		}
		return maxLevel;
	}
	
	public void unionOccupyCity(Object... datas) {
		long unionId = Long.parseLong(datas[0].toString());
		int level = Integer.parseInt(datas[1].toString());
		
		for (int i = 0; i < cityDestLevels.size(); i++) {
			if (cityDestLevels.get(i) <= level) {
				if (getUnionFlag(unionId, i) == 0) {
					setUnionFlag(unionId, i);
				}
			}
		}
	}
	
	public void updateUnions()
	{
		unions.clear();
		unions.addAll(world.getListObjects(UnionBody.class));
		Collections.sort(unions, scoreComparator);
		
		unionsStage.clear();
		int destScore = cityLevelScores.get(0);
		for (int i = 0; i < unions.size(); i++) {
			UnionBody ub = unions.get(i);
			int score = getUnionScore(ub.getId());
			if (score >= destScore) {
				unionsStage.add(ub);
			}
		}
	}
	
	public void statsUnionScores()
	{
		if (!isRuning()) {
			return;
		}
		
		for (int i = 0; i < unions.size(); i++)
		{
			UnionBody union = unions.get(i);
			long uid = union.getId();
			List<MapUnionCity> citys = mapWorld.searchUnionCity(uid);
			int score = 0;
			if (unionScores.containsKey(uid)) {
				score = unionScores.get(uid);
			}
			for (int j = 0; j < citys.size(); j++) {
				score += cityLevelScores.get(citys.get(j).getLevel()-1);
			}
			unionScores.put(uid, score);
		}
		
		updateUnions();
	}
	
	@Override
	public boolean receiveReward(Role role, int index) 
	{
		if (!isRuning()) {
			return false;
		}
		
		UnionBody union = unionManager.search(role.getUnionId());
		if (union == null) {
			return false;
		}
		if (getUnionFlag(union.getId(), index) == 0) {
			return false;
		}
		if (getRoleFlag(role.getId(), index) == 1) {
			return false;
		}
		
		rewardPlayer(role, rewards.get(index).getItems());
		setRoleFlag(role.getId(), index);
		return true;
	}
	
	private long getUnionFlag(long unionId, int index) {
		if (!unionFlags.containsKey(unionId)) {
			return 0;
		} else {
			long flag = unionFlags.get(unionId);
			return getBit(flag, index);
		}
	}

	private void setUnionFlag(long unionId, int index) {
		long flag = 0;
		if (unionFlags.containsKey(unionId)) {
			flag = unionFlags.get(unionId);
		}
		flag = setBit(flag, index, 1);
		unionFlags.put(unionId, flag);
		
		UnionBody union = UnionManager.getInstance().search(unionId);
		if (union != null)
		{
			List<UnionMember> members = union.getMembers();
			for (int i = 0 ; i< members.size(); i++)
			{
				actvtMgr.sendActvtTip(members.get(i).getUid());
			}
		}
	}
	
	private long getRoleFlag(long joyId, int index) {
		if (!roleFlags.containsKey(joyId)) {
			return 0;
		} else {
			long flag = roleFlags.get(joyId);
			return getBit(flag, index);
		}
	}

	private void setRoleFlag(long joyId, int index) {
		long flag = 0;
		if (roleFlags.containsKey(joyId)) {
			flag = roleFlags.get(joyId);
		}
		flag = setBit(flag, index, 1);
		roleFlags.put(joyId, flag);
	}

	@Override
	public String getStateStr() {
		return JSON.toJSONString(unionScores) + STATE_STR_SPLIT_CH + JSON.toJSONString(unionFlags) + STATE_STR_SPLIT_CH + JSON.toJSONString(roleFlags);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		unionScores = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
		unionFlags = JSON.parseObject(strs[1], new TypeReference<Map<Long, Long>>(){});
		roleFlags = JSON.parseObject(strs[2], new TypeReference<Map<Long, Long>>(){});
	}

	@Override
	public int getReceiveableNum(long joyId)
	{
		if (!isRuning()) {
			return 0;
		}
		
		Role role = world.getOnlineRole(joyId);
		if (role == null) {
			return 0;
		}
		UnionBody union = UnionManager.getInstance().search(role.getUnionId());
		if (union == null) {
			return 0;
		}
		
		int num = 0;
		for (int i = 0; i < cityDestLevels.size(); i++)
		{
			if (getState(union.getId(), joyId, i) == ActvtCommonState.FINISH.ordinal()) {
				num++;
			}
		}
		return num;
	}
}
