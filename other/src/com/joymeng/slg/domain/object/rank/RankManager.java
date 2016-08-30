package com.joymeng.slg.domain.object.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.object.rank.data.Rankinglist;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.AbstractClientModule;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.union.UnionBody;

public class RankManager implements Instances {

	private static RankManager instance = new RankManager();

	public static RankManager getInstance() {
		return instance;
	}

	static Map<Long, RoleRank> ranks = new HashMap<Long, RoleRank>(); // 所有用户的rank
	static Map<Long, UnionRank> unionRanks = new HashMap<Long, UnionRank>(); // 联盟的Rank

	/**
	 * 添加新用户
	 * 
	 * @param roleRank
	 */
	public synchronized void addRoleRank(RoleRank roleRank) {
		if (roleRank != null) {
			ranks.put(roleRank.getUid(), roleRank);
		}
	}

	/**
	 * 根据用户uid 获取对应的RoleRank对象
	 * 
	 * @param uid
	 * @return
	 */
	public RoleRank getRoleRankByRoleUid(long uid) {
		return ranks.get(uid);
	}

	/**
	 * 根据unionID 获取 UnionRank对象
	 * 
	 * @param unionBody
	 * @return
	 */
	public UnionRank getUnionRankByUnionId(long unionId) {
		UnionRank unionRank = unionRanks.get(unionId);
		if (unionRank == null) {
			GameLog.error("get unionRank by unionId is fail");
			return null;
		}
		return unionRank;
	}
	
	public void load() {
		StringBuffer sb = new StringBuffer();
		sb.append("select role.rankInfo as rankInfo from role");
		List<Map<String, Object>> datas = dbMgr.getGameDao().getDatasBySql(sb.toString());
		if (datas != null) {
			for (int i = 0; i < datas.size(); i++) {
				Map<String, Object> data = datas.get(i);
				RoleRank rank = new RoleRank();
				String rankData = String.valueOf(data.get("rankInfo"));
				if (StringUtils.isNull(rankData)) {
					continue;
				}
				rank = JsonUtil.JsonToObject((rankData), RoleRank.class);
				ranks.put(rank.getUid(), rank);
			}
		}
	}
	
	/**
	 * 加载当前联盟的排名的 unionRanks
	 */
	public static void loadUnionRanks() {
		List<UnionBody> allUnionBodies = world.getListObjects(UnionBody.class);
		for (int i = 0; i < allUnionBodies.size(); i++) {
			UnionBody unionBody = allUnionBodies.get(i);
			if (unionBody == null) {
				GameLog.error("load unionBody is fail!");
				continue;
			}
			UnionRank unionRank = unionRanks.get(unionBody.getId());
			if (unionRank == null) {
				unionRank = new UnionRank();
			}
			long unionFight = 0; // 联盟战斗力
			long unionKillEnemy = 0; // 联盟消灭敌军数
			unionRank.setId(unionBody.getId());
			unionRank.setName(unionBody.getName());
			unionRank.setShortName(unionBody.getShortName());
			unionRank.setIcon(unionBody.getIcon());
			unionRank.setLevel(unionBody.getLevel());
			unionRank.setScore(unionBody.getScore());
			unionRank.setUnionLeaderName(unionBody.getLeaderName());
			for (int j = 0; j < unionBody.getMembers().size(); j++) {
				Long tempUid = unionBody.getMembers().get(j).getUid();
				RoleRank tempRoleRank = ranks.get(tempUid);
				if (tempRoleRank == null) {
					continue;
				}
				unionFight += tempRoleRank.getFight();
				unionKillEnemy += tempRoleRank.getRoleKillEnemy();
			}
			unionRank.setUnionFight(unionFight);
			unionRank.setUnionKillEnemy(unionKillEnemy);
			long ocpNum = 0;
			Map<Integer, Integer> ocpCitysMap = unionBody.getUsInfo().getOcpCitysMap();
			for (Integer cityLevel : ocpCitysMap.keySet()) {
				ocpNum += ocpCitysMap.get(cityLevel);
			}
			unionRank.setUnionNPCCityNum(ocpNum);
			unionRanks.put(unionRank.getId(), unionRank);
		}
	}

	/**
	 * 获取对应rankId的排行榜
	 * 
	 * @param role
	 * @param rankId
	 * @return
	 */
	public boolean getRankResultByRankId(Role role, int rankId) {
		// 指挥官系列排行榜
		RespModuleSet rms = new RespModuleSet();
		Rankinglist rankingList = dataManager.serach(Rankinglist.class, rankId);
		if (rankingList == null) {
			GameLog.error("read rankingList is fail!");
			return false;
		}
		int currentRank = 0;
		if (rankId >= 1 && rankId <= 4) {
			List<RoleRank> allRolesRank = new ArrayList<RoleRank>(ranks.values());
			List<RoleRank> needSendRanks = new ArrayList<RoleRank>();

			switch (rankId) {
			case RankType.RANK_TYPE_ROLE_FIGHT: {
				Collections.sort(allRolesRank, new Comparator<RoleRank>() {
					@Override
					public int compare(RoleRank o1, RoleRank o2) {
						return o1.getFight() == o2.getFight() ? 0 : (o1.getFight() > o2.getFight() ? -1 : 1);
					}
				});
			}
				break;

			case RankType.RANK_TYPE_ROLE_KILLENEMY: {
				Collections.sort(allRolesRank, new Comparator<RoleRank>() {
					@Override
					public int compare(RoleRank o1, RoleRank o2) {
						return o1.getRoleKillEnemy() == o2.getRoleKillEnemy() ? 0
								: (o1.getRoleKillEnemy() > o2.getRoleKillEnemy() ? -1 : 1);
					}
				});
			}
				break;

			case RankType.RANK_TYPE_ROLE_CITYLEVEL: {
				Collections.sort(allRolesRank, new Comparator<RoleRank>() {
					@Override
					public int compare(RoleRank o1, RoleRank o2) {
						return o1.getRoleCityLevel() == o2.getRoleCityLevel() ? 0
								: (o1.getRoleCityLevel() > o2.getRoleCityLevel() ? -1 : 1);
					}
				});
			}
				break;

			case RankType.RANK_TYPE_ROLE_HEROLEVEL: {
				Collections.sort(allRolesRank, new Comparator<RoleRank>() {
					@Override
					public int compare(RoleRank o1, RoleRank o2) {
						return o1.getRoleHeroLevel() == o2.getRoleHeroLevel() ? 0
								: (o1.getRoleHeroLevel() > o2.getRoleHeroLevel() ? -1 : 1);
					}
				});
			}
				break;

			default: {
				return false;
			}
			}

			for (int i = 0; i < allRolesRank.size(); i++) {
				RoleRank rank = allRolesRank.get(i);
				if (rank == null) {
					GameLog.error("get role from allRolesRank is fail!");
					return false;
				}
				if (i < rankingList.getListLengthMax()) {
					needSendRanks.add(rank);
				}
				if (rank.getUid() == role.getId()) {
					currentRank = i + 1;
				}
			}
			// 下发排行榜列表
			sendRoleRankToClient(rms, currentRank, needSendRanks, rankId);
			// rms.addModules(ranks);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			return true;

		} else if (rankId >= 9 && rankId <= 11) { // 联盟系列排行榜
			List<UnionRank> unionRanklist = new ArrayList<UnionRank>(unionRanks.values());
			List<UnionRank> needSendUnionRanks = new ArrayList<UnionRank>();
			loadUnionRanks();
			switch (rankId) {
			case RankType.RANK_TYPE_UNION_FIGHT: {
				Collections.sort(unionRanklist, new Comparator<UnionRank>() {
					@Override
					public int compare(UnionRank o1, UnionRank o2) {
						return o1.getUnionFight() == o2.getUnionFight() ? 0
								: (o1.getUnionFight() > o2.getUnionFight() ? -1 : 1);
					}
				});
			}
				break;
			case RankType.RANK_TYPE_UNION_KILLENEMY: {
				Collections.sort(unionRanklist, new Comparator<UnionRank>() {
					@Override
					public int compare(UnionRank o1, UnionRank o2) {
						return o1.getUnionKillEnemy() == o2.getUnionKillEnemy() ? 0
								: (o1.getUnionKillEnemy() > o2.getUnionKillEnemy() ? -1 : 1);
					}
				});
			}
				break;

			case RankType.RANK_TYPE_UNION_NPCCITY_NUM: {
				Collections.sort(unionRanklist, new Comparator<UnionRank>() {
					@Override
					public int compare(UnionRank o1, UnionRank o2) {
						return o1.getUnionNPCCityNum() == o2.getUnionNPCCityNum() ? 0
								: (o1.getUnionNPCCityNum() > o2.getUnionNPCCityNum() ? -1 : 1);
					}
				});
			}
				break;
			default: {
				return false;
			}
			}
			int i = 0;
			for (int j = 0; j < unionRanklist.size(); j++) {
				UnionRank unionRank = unionRanklist.get(j);
				if (unionRank == null) {
					GameLog.error("get unionRank from unionRanks is fail!");
					continue;
				}
				UnionBody unionBody = unionManager.search(unionRank.getId());
				if (unionBody == null) {
					GameLog.error("get  unionBody from unionRanks is fail!");
					continue;
				}
				if (i < rankingList.getListLengthMax()) {
					needSendUnionRanks.add(unionRank);
				}
				for (int k = 0; k < unionBody.getMembers().size(); k++) {
					Long tempUid = unionBody.getMembers().get(k).getUid();
					if (tempUid == role.getId()) {
						currentRank = i + 1;
					}
				}
				i++;
			}
			// 下发排行榜列表
			sendUnionRankToClient(rms, currentRank, needSendUnionRanks, rankId);
			// rms.addModules(ranks);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param rms
	 * @param currentRoleRank
	 *            打包当前指挥官的排名
	 * @param ranks
	 *            排行对应的列表
	 * @param rankType
	 *            排名榜Id
	 * @return
	 */
	public void sendRoleRankToClient(RespModuleSet rms, int currentRoleRank, List<RoleRank> roleRanks, int rankId) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_ROLE_RANK;
			}
		};
		module.add(currentRoleRank); // int 当前用户的排名
		module.add(roleRanks.size()); // int 排名人数
		for (int i = 0; i < roleRanks.size(); i++) {
			RoleRank rank = roleRanks.get(i);
			if (rank == null)
				continue;
			rank.sendClient(module.getParams());
			switch (rankId) {
			case RankType.RANK_TYPE_ROLE_FIGHT:
				module.add(rank.getFight());
				break;

			case RankType.RANK_TYPE_ROLE_KILLENEMY:
				module.add(rank.getRoleKillEnemy());
				break;

			case RankType.RANK_TYPE_ROLE_CITYLEVEL:
				module.add(rank.getRoleCityLevel());
				break;

			case RankType.RANK_TYPE_ROLE_HEROLEVEL:
				module.add(rank.getRoleHeroLevel());
				break;

			}
		}
		rms.addModule(module);
	}

	/**
	 * 打包联盟排行榜模块
	 * 
	 * @param rms
	 * @param currentRoleRank
	 * @param unionRanks
	 * @param rankId
	 */
	public void sendUnionRankToClient(RespModuleSet rms, int currentRoleRank, List<UnionRank> unionRanks, int rankId) {
		AbstractClientModule module = new AbstractClientModule() {
			@Override
			public short getModuleType() {
				return NTC_DTCD_UNION_RANK;
			}
		};
		module.add(currentRoleRank); // int 当前用户所在联盟的排名
		module.add(unionRanks.size()); // int 排名联盟数
		for (int i = 0; i < unionRanks.size(); i++) {
			UnionRank unionRank = unionRanks.get(i);
			unionRank.sendClient(module.getParams());
			switch (rankId) {
			case RankType.RANK_TYPE_UNION_FIGHT:
				module.add(unionRank.getUnionFight() / 1000 + (unionRank.getUnionFight() / 1000.0 > 0 ? 1 : 0));
				break;

			case RankType.RANK_TYPE_UNION_KILLENEMY:
				module.add(unionRank.getUnionKillEnemy());
				break;

			case RankType.RANK_TYPE_UNION_NPCCITY_NUM:
				module.add(unionRank.getUnionNPCCityNum());
				break;

			default:
				module.add(0);
			}
		}
		rms.addModule(module);
	}
}
