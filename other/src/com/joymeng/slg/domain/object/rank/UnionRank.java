package com.joymeng.slg.domain.object.rank;

import com.joymeng.slg.net.ParametersEntity;

public class UnionRank {
	long id;// 编号
	String name;// 名称
	String shortName;// 简称
	String icon = "allianceFlag_red";// 图标
	int level = 1;// 等级
	long score = 0;// 积分
	String unionLeaderName; // 联盟盟主的Uid
	long unionFight = 0; // 联盟战斗力
	long unionKillEnemy = 0; // 联盟消灭敌军数
	long unionNPCCityNum = 0;// 联盟占领城市数量

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getUnionNPCCityNum() {
		return unionNPCCityNum;
	}

	public void setUnionNPCCityNum(long unionNPCCityNum) {
		this.unionNPCCityNum = unionNPCCityNum;
	}

	public String getUnionLeaderName() {
		return unionLeaderName;
	}

	public void setUnionLeaderName(String unionLeaderName) {
		this.unionLeaderName = unionLeaderName;
	}

	public long getUnionFight() {
		return unionFight;
	}

	public void setUnionFight(long unionFight) {
		this.unionFight = unionFight;
	}

	public long getUnionKillEnemy() {
		return unionKillEnemy;
	}

	public void setUnionKillEnemy(long unionKillEnemy) {
		this.unionKillEnemy = unionKillEnemy;
	}

	public void sendClient(ParametersEntity param) {
		param.put(id);
		param.put(name);
		param.put(shortName);
		param.put(icon);
		param.put(unionLeaderName);
	}
}
