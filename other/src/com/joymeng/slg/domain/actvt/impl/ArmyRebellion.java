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
import com.joymeng.slg.domain.map.MapUtil;
import com.joymeng.slg.domain.map.impl.still.res.MapEctype;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.UnionManager;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.World;

public class ArmyRebellion extends Actvt  
{
	enum RebellInfoState
	{
		CAN_START, // 是盟主且可以开启
		CANOT_START, // 是盟主，但不可以开启
		NOT_SHOW // 不是盟主，隐藏
	}
	
	private static final int SCORE_STAGE_NUM = 3;
	private Map<Integer, ActvtReward> roleRewards = new HashMap<Integer, ActvtReward>();
	private Map<Integer, ActvtReward> unionRewards = new HashMap<Integer, ActvtReward>();
	List<ActvtReward> roleRewardsShow = new ArrayList<ActvtReward>();
	List<ActvtReward> unionRewardsShow = new ArrayList<ActvtReward>();
	
	private List<ActvtTask> taskListShow = new ArrayList<ActvtTask>();
	private List<ActvtReward> rewardStages = new ArrayList<ActvtReward>();
	private List<Long> removeList = new ArrayList<Long>();
	private int roleRankMax = 1;
	private int unionRankMax = 1;
	
	private String monsterPreStr;
	private int monsterNum;
	private List<Integer> scoreStages = new ArrayList<Integer>();
	private String scoreStageDesc;
	private int attackInterval;
	private int failNum;
	
	private Map<Long, Integer> tickSeconds = new HashMap<Long, Integer>();
	private Map<Long, Integer> doneUnionIds =  new HashMap<Long, Integer>();
	private Map<Long, Integer> roleFailNum = new HashMap<Long, Integer>();
	private Map<Long, Integer> unionScores = new HashMap<Long, Integer>();
	private Map<Long, Integer> roleScores = new HashMap<Long, Integer>();
	private Map<Long, List<Integer>> roleFlags = new HashMap<Long, List<Integer>>();
	private Map<Long, Integer> unionMonsterNum = new HashMap<Long, Integer>();
	
	private List<Long> unionRanks = new ArrayList<Long>();
	private List<Long> unionRanksLast = new ArrayList<Long>();
	private List<Long> roleRanks = new ArrayList<Long>();
	private List<Long> roleRanksLast = new ArrayList<Long>();
	
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
	
	private int getRoleScore(long joyId) {
		Role role = world.getRole(joyId);
		if (role == null) {
			return 0;
		}
		if (!roleScores.containsKey(joyId)) {
			return 0;
		}
		return roleScores.get(joyId);
	}
	
	private int getUnionScore(long unionId) {
		UnionBody union = unionManager.search(unionId);
		if (union == null) {
			return 0;
		}
		if (!unionScores.containsKey(unionId)) {
			return 0;
		}
		return unionScores.get(unionId);
	}
	
	private int getRoleRank(long joyId) {
		for (int i = 0; i < roleRanks.size(); i++) {
			if (roleRanks.get(i) == joyId) {
				return i+1;
			}
		}
		return 0;
	}
	
	private int getUnionRank(long unionId) {
		for (int i = 0; i < unionRanks.size(); i++) {
			if (unionRanks.get(i) == unionId) {
				return i+1;
			}
		}
		return 0;
	}
	
	@Override
	public void load(Element element) throws Exception
	{
		super.load(element);

		Element eleSpecial = XmlUtils.getChildByName(element, "Special");
		monsterPreStr = eleSpecial.getAttribute("monsterPreStr");
		monsterNum = Integer.parseInt(eleSpecial.getAttribute("monsterNum"));
		attackInterval = Integer.parseInt(eleSpecial.getAttribute("attackInterval"));
		failNum = Integer.parseInt(eleSpecial.getAttribute("failNum"));
		
		String str = eleSpecial.getAttribute("scoreStages");
		String[] strs = str.split(",");
		if (strs.length != SCORE_STAGE_NUM) {
			throw new Exception("id="+getId()+" scores num is not 3");
		}
		
		scoreStages.clear();
		for (int i = 0; i < strs.length; i++) {
			scoreStages.add(Integer.parseInt(strs[i]));
		}
		scoreStageDesc = eleSpecial.getAttribute("scoreStageDesc");
		
		str = eleSpecial.getAttribute("rewardStages");
		strs = str.split(",");
		if (strs.length != SCORE_STAGE_NUM) {
			throw new Exception("id="+getId()+" scores num is not 3");
		}
		rewardStages.clear();
		for (int i = 0; i < strs.length; i++) {
			rewardStages.add(getReward(strs[i]));
		}
		
		roleRankMax = 1;
		unionRankMax = 1;
		unionRewards.clear();
		roleRewards.clear();
		List<ActvtReward> rewardList = getRewardList();
		for (int i = 0; i < rewardList.size(); i++) {
			ActvtReward reward = rewardList.get(i);
			String id = reward.getId();
			if (id.equals(strs[0]) || id.equals(strs[1]) || id.equals(strs[2])) {
				continue;
			}
			String[] ids = id.split("-");
			if (ids.length == 1) {
				if (id.startsWith("role")) {
					int rank = Integer.parseInt(id.substring(4));
					roleRewards.put(rank, reward);
					if (roleRankMax < rank) {
						roleRankMax = rank;
					}
					roleRewardsShow.add(reward);
				}
				else {
					int rank = Integer.parseInt(id.substring(5));
					unionRewards.put(rank, reward);
					if (unionRankMax < rank) {
						unionRankMax = rank;
					}
					unionRewardsShow.add(reward);
				}
			}
			else {
				if (id.startsWith("role")) {
					id = id.substring(4);
					ids = id.split("-");
					int min = Integer.parseInt(ids[0]);
					int max = Integer.parseInt(ids[1]);
					for (int r = min; r <= max; r++) {
						roleRewards.put(r, reward);
						if (roleRankMax < r) {
							roleRankMax = r;
						}
					}
					roleRewardsShow.add(reward);
				}
				else {
					id = id.substring(5);
					ids = id.split("-");
					int min = Integer.parseInt(ids[0]);
					int max = Integer.parseInt(ids[1]);
					for (int r = min; r <= max; r++) {
						unionRewards.put(r, reward);
						if (unionRankMax < r) {
							unionRankMax = r;
						}
					}
					unionRewardsShow.add(reward);
				}
			}
		}
		
		for (int r = 1; r < unionRankMax; r++) {
			if (!unionRewards.containsKey(r)) {
				throw new Exception("id="+getId()+" union reward not continuos");
			}
		}
		for (int r = 1; r < roleRankMax; r++) {
			if (!roleRewards.containsKey(r)) {
				throw new Exception("id="+getId()+" role reward not continuos");
			}
		}
		
		taskListShow.clear();
		List<ActvtTask> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++) {
			ActvtTask task = taskList.get(i);
			if (task.isShow()) {
				taskListShow.add(task);
			}
		}
	}
	
	@Override
	public void start() {
		super.start();
		
		EvntManager.getInstance().Remove(this);
//		EvntManager.getInstance().Listen("taskEvent", this);
		EvntManager.getInstance().Listen("rebellAttackOver", this);
		
//		stopInnerTimer("attack");
//		startInnerTimer("0 0/3 * * * ?", "attack");
		stopInnerTimer("tick");
		startInnerTimer("* * * * * ?", "tick");
		
		stopInnerTimer("reset");
		startInnerTimer("0 0/16 * * * ?", "reset");
	}
	
	@Override
	public void innerTimerCB(String tag)
	{
		if (tag.equals("reset")) {
			reset();
		}
		else if (tag.equals("tick")) {
			tick();
		}
	}
	
	@Override
	public void execute(String event, Object... datas)
	{
		if (!isRuning()) {
			return;
		}
		
		if (event.equals("rebellAttackOver")) {
			rebellAttackOver(datas);
		}
	}
	
	@Override
	public void end() {
		super.end();
		rewardRank();
		stopInnerTimer("tick");
		stopInnerTimer("reset");
	}
	
	private boolean isUnionLeader(Role role) {
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
		
		return true;
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
	public boolean manualStart(Role role) {
		if (!canManualStart(role)) {
			return false;
		}

		tickSeconds.put(role.getUnionId(), 0);
		unionMonsterNum.put(role.getUnionId(), 0);
		return true;
	}
	
	private void rewardRank() {
		// 邮件分发联盟奖励与个人奖励
		for (int i = 0; i < roleRanks.size(); i++) {
			long joyId = roleRanks.get(i);
			ActvtReward reward = roleRewards.get(i+1);
			sendEmail(joyId, String.format("恭喜您在本次暴君叛乱的活动中获得个人积分第%d名", i+1), reward);
		}
		
		for (int i = 0; i < unionRanks.size(); i++) {
			long unionId = unionRanks.get(i);
			UnionBody union = UnionManager.getInstance().search(unionId);
			if (union != null) {
				ActvtReward reward = unionRewards.get(i+1);
				List<UnionMember> members = union.getMembers();
				for (int j = 0; j < members.size(); j++) {
					sendEmail(members.get(j).getUid(), String.format("恭喜您在本次暴君叛乱的活动中获得联盟积分第%d名", i+1), reward);
				}
			}
		}
	}
	
	public void reset() {
		rewardRank();
		unionRanksLast.clear();
		unionRanksLast.addAll(unionRanks);
		unionRanks.clear();
		roleRanksLast.clear();
		roleRanksLast.addAll(roleRanks);
		roleRanks.clear();
		
		tickSeconds.clear();
		doneUnionIds.clear();
		roleFailNum.clear();
		unionScores.clear();
		roleScores.clear();
		roleFlags.clear();
		unionMonsterNum.clear();
	}
	
	private boolean rebellAttack(long unionId)
	{
		GameLog.info("rebellAttack unionId="+unionId);
		UnionBody union = unionManager.search(unionId);
		if (union == null) {
			GameLog.error("rebell attack unionId="+unionId+" not exist");
			return false;
		}
		
		boolean flag = false;
		
		List<UnionMember> members = union.getMembers();
		for (int i = 0; i < members.size(); i++)
		{
			UnionMember member = members.get(i);
			long joyId = member.getUid();
			if (!roleFailNum.containsKey(joyId) || roleFailNum.get(joyId) < failNum) {
				flag = true;
				// send rebellion attack
				Role role = World.getInstance().getRole(joyId);
				MapEctype ectype = role.getNearestEctype();
				
				int num = 0;
				if (unionMonsterNum.containsKey(unionId)) {
					num = unionMonsterNum.get(unionId);
				}
				unionMonsterNum.put(unionId, ++num);
				
				if (num < 1) {
					num = 1;
					unionMonsterNum.put(unionId, num);
					GameLog.error("rebell attack unionId="+unionId+" monster num is " + num);
				}
				else if (num > monsterNum) {
					num = monsterNum;
				}
				String monsterId = monsterPreStr + num;
				MapUtil.monsterAttackRoleCity(ectype.getPosition(), monsterId, joyId, 0);
			}
		}
		
		return flag;
	}
	
	@Override
	public void tick() {
		if (!isRuning()) {
			return;
		}
		
//		PointVector pos = MapUtil.getPointVector(687079);
		
		for (Map.Entry<Long, Integer> entry : tickSeconds.entrySet()) {
			long unionId = entry.getKey();
			int seconds = entry.getValue();
			if (++seconds >= attackInterval) {
				tickSeconds.put(unionId, 0);
				if (!rebellAttack(unionId)) {
					removeList.add(unionId);
				}
			}
			else {
				tickSeconds.put(unionId, seconds);
			}
		}
		
		for (int i = 0; i < removeList.size(); i++) {
			long unionId = removeList.get(i);
			tickSeconds.remove(unionId);
			doneUnionIds.put(unionId, 1);
		}
		if (!removeList.isEmpty()) {
			removeList.clear();
		}
	}
	
	private int getRoleFlag(long joyId, int index) {
		if (!roleFlags.containsKey(joyId)) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		List<Integer> flags = roleFlags.get(joyId);
		if (index >= flags.size()) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		int flag = flags.get(index);
		if (flag < 0 || flag > ActvtCommonState.RECEIVED.ordinal()) {
			GameLog.error("ArmyRebellion getRoleFlag exception, index > role flags size, id="+joyId);
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		return flag;
	}
	
	private void setRoleFlag(long joyId, int index, int flag) {
		if (!roleFlags.containsKey(joyId)) {
			List<Integer> flags = new ArrayList<Integer>();
			for (int i = 0; i < SCORE_STAGE_NUM; i++) {
				flags.add(ActvtCommonState.NOT_FINISH.ordinal());
			}
			roleFlags.put(joyId, flags);
		}
		List<Integer> flags = roleFlags.get(joyId);
		flags.set(index, flag);
	}
	
	public void makeUpInfoModule(ClientMod module, Role role) 
	{
		ActvtCommon commonData = getCommonData();
		module.add(getId());
		module.add(commonData.getDetailDesc());
		module.add(getStateOdinal());
		if (canManualStart(role)) {
			module.add(RebellInfoState.CAN_START.ordinal());
		}
		else {
			if (isUnionLeader(role)) {
				module.add(RebellInfoState.CANOT_START.ordinal());
			}
			else {
				module.add(RebellInfoState.NOT_SHOW.ordinal());
			}
		}
		GameLog.info(module.getParams().toString());
	}
	
	@Override
	public void makeUpDetailModule(ClientMod module, Role role) 
	{
		ActvtCommon commonData = getCommonData();
		module.add(commonData.getType());
		module.add(commonData.getName());
		module.add(commonData.getDetailDesc());
		
		module.add(getRoleScore(role.getId()));
		module.add(getRoleRank(role.getId()));
		for (int i = 0; i < SCORE_STAGE_NUM; i++) {
			module.add(scoreStages.get(i));
		}

		for (int i = 0; i < SCORE_STAGE_NUM; i++) {
			module.add(scoreStages.get(i));
			module.add(rewardStages.get(i).getItems());
			module.add(getRoleFlag(role.getId(),i));
		}
		
		module.add(taskListShow.size());
		for (int i = 0; i < taskListShow.size(); i++) {
			ActvtTask task = taskListShow.get(i);
			module.add(task.getDesc());
			module.add(String.valueOf(task.getNum()));
		}
		
		module.add(roleRewardsShow.size());
		for (int i = 0; i < roleRewardsShow.size(); i++) {
			ActvtReward reward = roleRewardsShow.get(i);
			module.add("第"+reward.getId().substring(4)+"名");
			module.add(reward.getItems());
		}
		
		module.add(unionRewardsShow.size());
		for (int i = 0; i < unionRewardsShow.size(); i++) {
			ActvtReward reward = unionRewardsShow.get(i);
			module.add("第"+reward.getId().substring(5)+"名");
			module.add(reward.getItems());
		}
	}
	
	private void refreshRoleRanks() {
		for (int i = 0; i < roleRanks.size(); ) {
			long joyId = roleRanks.get(i);
			Role r = world.getRole(joyId);
			if (r == null) {
				roleRanks.remove(i);
			}
			else {
				i++;
			}
		}
		Collections.sort(roleRanks, roleComparator);
	}
	
	private void refreshUnionRanks() {
		for (int i = 0; i < unionRanks.size(); ) {
			long unionId = unionRanks.get(i);
			UnionBody u = unionManager.search(unionId);
			if (u == null || getUnionScore(unionId) <= 0) {
				unionRanks.remove(i);
			}
			else {
				i++;
			}
		}
		Collections.sort(unionRanks, unionComparator);
	}
	
	@Override
	public boolean receiveReward(Role role, int index) {
		if (getRoleFlag(role.getId(), index) != ActvtCommonState.FINISH.ordinal()) {
			return false;
		}
		if (index < 0 || index >= rewardStages.size()) {
			return false;
		}
		
		ActvtReward reward = rewardStages.get(index);
		rewardPlayer(role, reward.getItems());
		setRoleFlag(role.getId(), index, ActvtCommonState.RECEIVED.ordinal());
		return true;
	}
	
	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) 
	{
		refreshRoleRanks();
		refreshUnionRanks();
		
		int rankNum = 2;
		module.add(rankNum);
		
		module.add(getRoleScore(role.getId()));
		module.add(getRoleRank(role.getId()));
		module.add(scoreStageDesc);
		module.add(roleRanks.size());
		for (int i = 0; i < roleRanks.size(); i++) 
		{
			long joyId = roleRanks.get(i);
			Role r = world.getRole(joyId);
			module.add(r.getName());
			module.add(getRoleScore(r.getId()));
		}
		
		UnionBody union = unionManager.search(role.getUnionId());
		module.add(union==null?0:getUnionScore(union.getId()));
		module.add(union==null?0:getUnionRank(union.getId()));
		module.add("");
		module.add(unionRanks.size());
		for (int i = 0; i < unionRanks.size(); i++) 
		{
			long unionId = unionRanks.get(i);
			UnionBody u = unionManager.search(unionId);
			module.add(u.getName());
			module.add(getUnionScore(u.getId()));
		}
		
		return true;
	}
	
	private int getScoreNum(String armyId) {
		List<ActvtTask> taskList = getTaskList();
		for (int i = 0; i < taskList.size(); i++) {
			ActvtTask task = taskList.get(i);
			if (task.check(ActvtEventType.KILL_SOLDIER.getName(), armyId)) {
				return task.getNum();
			}
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public void rebellAttackOver(Object... args)
	{
		long joyId = Long.parseLong(args[0].toString());
		Role role = world.getOnlineRole(joyId);
		if (role == null) {
			return;
		}
		if (!tickSeconds.containsKey(role.getUnionId())) {
			return;
		}
		
		// 计算积分 soldierScores
		Map<String,Integer> kills = (Map<String,Integer>)args[2];
		int addScore = 0;
		for (Map.Entry<String, Integer> entry : kills.entrySet()) {
			String armyId = entry.getKey();
			int num = entry.getValue();
			int score = getScoreNum(armyId);
			addScore += score*num;
		}

		int score = 0;
		if (roleScores.containsKey(joyId)) {
			score = roleScores.get(joyId);
		}
		int newScore = score + addScore;
		roleScores.put(joyId, newScore);
		if (newScore >= scoreStages.get(2) && !roleRanks.contains(joyId)) {
			roleRanks.add(joyId);
		}
		for (int i = 0; i < SCORE_STAGE_NUM; i++) {
			if (newScore >= scoreStages.get(i) && getRoleFlag(joyId, i) == ActvtCommonState.NOT_FINISH.ordinal()) {
				setRoleFlag(joyId, i, ActvtCommonState.FINISH.ordinal());
			}
		}

		long unionId = role.getUnionId();
		UnionBody union = unionManager.search(unionId);
		if (union != null) {
			score = 0;
			if (unionScores.containsKey(unionId)) {
				score = unionScores.get(unionId);
			}
			
			newScore = score + addScore;
			unionScores.put(unionId, newScore);
			if (!unionRanks.contains(unionId) && newScore > 0) {
				unionRanks.add(unionId);
			}
		}
		else {
			GameLog.error("ArmyRebellion armyRebellAttackOver joyId="+joyId+" unionId="+unionId+" not exist");
		}
		
		refreshRoleRanks();
		refreshUnionRanks();
		
		boolean success = (!Boolean.parseBoolean(args[1].toString()));
		if (!success) {
			int num = 0;
			if (roleFailNum.containsKey(joyId)) {
				num = roleFailNum.get(joyId);
			}
			roleFailNum.put(joyId, ++num);
		}
	}

	@Override
	public String getStateStr() {
		return JSON.toJSONString(tickSeconds) + STATE_STR_SPLIT_CH + JSON.toJSONString(unionMonsterNum)
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(roleFlags)
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(doneUnionIds)  
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(roleFailNum)
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(unionScores) 
		 + STATE_STR_SPLIT_CH + JSON.toJSONString(roleScores);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);
		
		tickSeconds = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>(){});
		unionMonsterNum = JSON.parseObject(strs[1], new TypeReference<Map<Long, Integer>>(){});
		roleFlags = JSON.parseObject(strs[1], new TypeReference<Map<Long, List<Integer>>>(){});
		doneUnionIds = JSON.parseObject(strs[2], new TypeReference<Map<Long, Integer>>(){});
		roleFailNum = JSON.parseObject(strs[3], new TypeReference<Map<Long, Integer>>(){});
		unionScores = JSON.parseObject(strs[4], new TypeReference<Map<Long, Integer>>(){});
		roleScores = JSON.parseObject(strs[5], new TypeReference<Map<Long, Integer>>(){});
	}

}
