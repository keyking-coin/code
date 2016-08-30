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
import com.joymeng.slg.domain.actvt.data.Activity_unionoccupycity;
import com.joymeng.slg.domain.map.impl.still.union.MapUnionCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.UnionManager;
import com.joymeng.slg.union.impl.UnionMember;

public class UnionOccupyCity extends Actvt {
	private Activity_unionoccupycity unionOccupyCity;
	private List<List<Activity_reward>> rewards = new ArrayList<List<Activity_reward>>();
	private Map<Integer, List<Activity_reward>> rankRewards = new HashMap<Integer, List<Activity_reward>>();
	private List<UnionBody> unions = new ArrayList<UnionBody>(); // 改成unionId?
	private List<UnionBody> unionsStage = new ArrayList<UnionBody>(); // 改成unionId?

	private Map<Long, Integer> unionScores = new HashMap<Long, Integer>();
	private Map<Long, Long> unionFlags = new HashMap<Long, Long>();
	private Map<Long, Long> roleFlags = new HashMap<Long, Long>();

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
	public void start() {
		super.start();

		updateUnions("", "");
		// unions.clear();
		// unions.addAll(world.getListObjects(UnionBody.class));
	}

	@Override
	public void end() {
		super.end();
		rewardRankUnions();
	}

	private void rewardRankUnions() {
		for (int i = 0; i < unionOccupyCity.getRankNum() && i < 6; i++) {
			if (i >= unionsStage.size()) {
				break;
			}

			UnionBody union = unionsStage.get(i);
			List<UnionMember> members = union.getMembers();
			int index = i + 1;
			int[] indexs = {1,2,3,4,7,11};
			index = indexs[i];
			List<Activity_reward> rewards = rankRewards.get(index);
			
			for (int j = 0; j < members.size(); j++) {
				long joyId = members.get(j).getUid();
				// rewardPlayer(joyId, Activity_reward.toString(rewards));
				sendEmail(joyId, String.format("恭喜你的联盟在“%s”活动中获得“第%d名”，获得以下奖励。", getActivity().getName(), i + 1),
						rewards);
				// TEST11 联盟攻城活动奖励 排行奖励 rewards
				StringBuffer sb = new StringBuffer();
				sb.append(getActivity().getTypeId()).append(GameLog.SPLIT_CHAR);
				sb.append(i + 1).append(GameLog.SPLIT_CHAR);
				for (int n = 0; n < rewards.size(); n++) {
					Activity_reward reward = rewards.get(n);
					sb.append(reward.getsID());
					sb.append(GameLog.SPLIT_CHAR);
					sb.append(reward.getNum());
					sb.append(GameLog.SPLIT_CHAR);
				}
				String newStr = sb.toString().substring(0, sb.toString().length() - 1);
				Role role = world.getRole(joyId);
				NewLogManager.activeLog(role, "unionOccupyCity", newStr);
			}
		}
	}

	private int getUnionScore(long uid) {
		if (unionScores.containsKey(uid)) {
			return unionScores.get(uid);
		}
		return 0;
	}

	private int getRank(long uid) {
		for (int i = 0; i < unionsStage.size(); i++) {
			if (unionsStage.get(i).getId() == uid) {
				return i + 1;
			}
		}
		return 0;
	}

	@Override
	public boolean init(Activity actvt) {
		if (!super.init(actvt)) {
			return false;
		}
		load();

		return true;
	}

	private int getState(long unionId, long joyId, int index) {
		if (getUnionFlag(unionId, index) == 0) {
			return ActvtCommonState.NOT_FINISH.ordinal();
		}
		if (getRoleFlag(joyId, index) == 0) {
			return ActvtCommonState.FINISH.ordinal();
		}
		return ActvtCommonState.RECEIVED.ordinal();
	}

	@Override
	public void makeUpDetailModule(ClientMod module, Role role) {
		// unionScores.clear();
		// unionFlags.clear();
		// roleFlags.clear();
		// updateUnions("", "");

		long unionId = role.getUnionId();
		long joyId = role.getId();

 		Activity activity = getActivity();
		module.add(activity.getType());
		module.add(activity.getName());
		module.add(activity.getDetailDesc());

		module.add(getUnionScore(unionId));
		module.add(getRank(unionId));
		module.add(getOccupyCityMaxLevel(unionId));

		List<Integer> cityLevels = unionOccupyCity.getCityLevels();
		module.add(cityLevels.size());
		for (int i = 0; i < cityLevels.size(); i++) {
			module.add(cityLevels.get(i)); // String.format("占领一座%d级城市",
											// cityLevels.get(i))
			module.add(Activity_reward.toString(rewards.get(i)));
			module.add(getState(unionId, joyId, i));
		}

		List<Integer> cityScores = unionOccupyCity.getCityScores();
		module.add(cityScores.size());
		for (int i = 0; i < cityScores.size(); i++) {
			module.add(String.format("占领%d级城市一天", i + 1));
			module.add(String.valueOf(cityScores.get(i)));
		}

		int num = unionOccupyCity.getRankNum();
		// num = num>20?20:num;
		module.add(num);
		for (int i = 0; i < num; i++) {
			module.add(Activity_reward.toString(rankRewards.get(i + 1)));
		}
		// System.out.println(module.getParams().toString());
	}

	@Override
	public boolean makeUpActvtRankListModule(ClientMod module, Role role) {
		long unionId = role.getUnionId();
		module.add(getUnionScore(unionId));
		module.add(getRank(unionId));
		module.add("当你达到目标1的时候，就可以参与排名，获得排名奖励");

		module.add(unionsStage.size());
		for (int i = 0; i < unionsStage.size(); i++) {
			UnionBody union = unionsStage.get(i);
			module.add(union.getName());
			module.add(getUnionScore(union.getId()));
		}

		return true;
	}

	private int getOccupyCityMaxLevel(long unionId) {
		int maxLevel = 0;
		List<Integer> cityLevels = unionOccupyCity.getCityLevels();
		for (int i = 0; i < cityLevels.size(); i++) {
			if (getUnionFlag(unionId, i) == 1) {
				maxLevel = cityLevels.get(i);
			}
		}
		return maxLevel;
	}

	public void unionOccupyCity(String value, String data) {
		String[] strs = data.split("#");
		long unionId = Long.parseLong(strs[0]);
		int level = Integer.parseInt(strs[1]);

		List<Integer> cityLevels = unionOccupyCity.getCityLevels();
		for (int i = 0; i < cityLevels.size(); i++) {
			if (cityLevels.get(i) <= level) {
				if (getUnionFlag(unionId, i) == 0) {
					setUnionFlag(unionId, i);
				}
			}
		}
	}

	public void updateUnions(String value, String data) {
		unions.clear();
		unions.addAll(world.getListObjects(UnionBody.class));
		Collections.sort(unions, scoreComparator);

		unionsStage.clear();
		int destScore = unionOccupyCity.getCityScores().get(0);
		for (int i = 0; i < unions.size(); i++) {
			UnionBody ub = unions.get(i);
			int score = getUnionScore(ub.getId());
			if (score >= destScore) {
				unionsStage.add(ub);
			}
		}
	}

	@Override
	public void taskEvent(String value, String data) {
		if (!isRuning()) {
			return;
		}

		try {
			String[] strs = data.split("#");

			//String taskID = strs[1];
			// if (!taskID.equals("unionOccupyCity createUnion dissmissUnion"))
			// { // dissmissUnion
			// return;
			// }
			
//			if (!taskID.equals("createUnion") || !taskID.equals("dissmissUnion")) {
//				return;
//			}

			int level = Integer.parseInt(strs[2]);
			long unionId = Long.parseLong(strs[0]);

			List<Integer> cityLevels = unionOccupyCity.getCityLevels();
			for (int i = 0; i < cityLevels.size(); i++) {
				if (cityLevels.get(i) == level) {
					if (getUnionFlag(unionId, i) == 0) {
						setUnionFlag(unionId, i);
					}
					break;
				}
			}
		} catch (Exception e) {
			GameLog.error(e.getMessage());
		}
	}

	public void statsUnionScores(String value, String data) {
		if (!isRuning()) {
			return;
		}
		
		unions.clear();
		unions.addAll(world.getListObjects(UnionBody.class));
		Collections.sort(unions, scoreComparator);
		
//		List<Integer> cityScores = unionOccupyCity.getCityScores();
		for (int i = 0; i < unions.size(); i++) {
			UnionBody union = unions.get(i);
			long uid = union.getId();
			List<MapUnionCity> citys = mapWorld.searchUnionCity(uid);
			int score = 0;
			if (unionScores.containsKey(uid)) {
				score = unionScores.get(uid);
			}
			for (int j = 0; j < citys.size(); j++) {
				score += getScore(citys.get(j).getLevel()); //cityScores.get(citys.get(j).getLevel() - 1);
			}
			unionScores.put(uid, score);
		}

		updateUnions("", "");
	}
	
	private int getScore(int level) {
		if (level == 0) {
			GameLog.error("UnionOccupyCity getScore level="+level);
			level = 1;
		}
		
		List<Integer> cityScores = unionOccupyCity.getCityScores();
		if (level > cityScores.size()) {
			level = cityScores.size();
		}
		return cityScores.get(level-1);
	}

	@Override
	public boolean receiveReward(Role role, int index) {
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

		rewardPlayer(role, Activity_reward.toString(rewards.get(index)));
		setRoleFlag(role.getId(), index);
		// TEST11 联盟攻城活动奖励 领取奖励 rewards
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
		if (union != null) {
			List<UnionMember> members = union.getMembers();
			for (int i = 0; i < members.size(); i++) {
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
		return JSON.toJSONString(unionScores) + SPCH + JSON.toJSONString(unionFlags) + SPCH
				+ JSON.toJSONString(roleFlags);
	}

	@Override
	public void loadFromData(SqlData data) {
		String[] strs = getStateStrs(data);

		unionScores = JSON.parseObject(strs[0], new TypeReference<Map<Long, Integer>>() {
		});
		unionFlags = JSON.parseObject(strs[1], new TypeReference<Map<Long, Long>>() {
		});
		roleFlags = JSON.parseObject(strs[2], new TypeReference<Map<Long, Long>>() {
		});
		updateUnions("", "");
	}

	@Override
	public void load() {
		unionOccupyCity = actvtMgr.serach(Activity_unionoccupycity.class, "1");

		rewards.clear();
		List<String> rewardIds = unionOccupyCity.getRewards();
		for (int i = 0; i < rewardIds.size(); i++) {
			final String rewardId = rewardIds.get(i);
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class,
					new SearchFilter<Activity_reward>() {
						@Override
						public boolean filter(Activity_reward data) {
							return data.getrID().equals(rewardId);
						}
					});
			rewards.add(rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
		// rewards.add(actvtMgr.getReward(rewardIds.get(0)));
		// rewards.add(actvtMgr.getReward(rewardIds.get(1)));
		// rewards.add(actvtMgr.getReward(rewardIds.get(2)));

		rankRewards.clear();
		for (int i = 0; i < unionOccupyCity.getRankNum(); i++) {
			final int rank = i + 1;
			List<Activity_reward> rewardList = actvtMgr.serachList(Activity_reward.class,
					new SearchFilter<Activity_reward>() {
						@Override
						public boolean filter(Activity_reward data) {
							return data.getrID().equals(getActivity().getTypeId() + "_" + rank);
						}
					});
			// List<Activity_reward> rewardList =
			// actvtMgr.getReward(getActivity().getTypeId()+"_"+(i+1));
			rankRewards.put(rank, rewardList);
			Collections.sort(rewardList, rewardComparator);
		}
	}

	@Override
	public void hotLoadEnd() {
		super.hotLoadEnd();
		rewardRankUnions();
	}

	@Override
	public int getReceiveableNum(long joyId) {
		Role role = world.getOnlineRole(joyId);
		if (role == null) {
			return 0;
		}
		UnionBody union = UnionManager.getInstance().search(role.getUnionId());
		if (union == null) {
			return 0;
		}

		int num = 0;
		List<Integer> cityLevels = unionOccupyCity.getCityLevels();
		for (int i = 0; i < cityLevels.size(); i++) {
			if (getState(union.getId(), joyId, i) == ActvtCommonState.FINISH.ordinal()) {
				num++;
			}
		}
		return num;
	}
}
