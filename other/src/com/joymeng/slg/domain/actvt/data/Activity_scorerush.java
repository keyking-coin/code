package com.joymeng.slg.domain.actvt.data;

import java.util.List;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_scorerush implements DataKey
{
	String id;
	String typeId;
	int scoreI;
	int scoreII;
	int scoreIII;
	int rankNum;
	String reward1;
	String reward2;
	String reward3;
	String scoreItems;
	String scoreDesc;
	List<Integer> accTimes;
	List<Integer> accScores;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public int getScoreI() {
		return scoreI;
	}

	public void setScoreI(int scoreI) {
		this.scoreI = scoreI;
	}

	public int getScoreII() {
		return scoreII;
	}

	public void setScoreII(int scoreII) {
		this.scoreII = scoreII;
	}

	public int getScoreIII() {
		return scoreIII;
	}

	public void setScoreIII(int scoreIII) {
		this.scoreIII = scoreIII;
	}

	public int getRankNum() {
		return rankNum;
	}

	public void setRankNum(int rankNum) {
		this.rankNum = rankNum;
	}

	public String getReward1() {
		return reward1;
	}

	public void setReward1(String reward1) {
		this.reward1 = reward1;
	}

	public String getReward2() {
		return reward2;
	}

	public void setReward2(String reward2) {
		this.reward2 = reward2;
	}

	public String getReward3() {
		return reward3;
	}

	public void setReward3(String reward3) {
		this.reward3 = reward3;
	}

	public String getScoreItems() {
		return scoreItems;
	}

	public void setScoreItems(String scoreItems) {
		this.scoreItems = scoreItems;
	}

	public String getScoreDesc() {
		return scoreDesc;
	}

	public void setScoreDesc(String scoreDesc) {
		this.scoreDesc = scoreDesc;
	}

	public List<Integer> getAccTimes() {
		return accTimes;
	}

	public void setAccTimes(List<Integer> accTimes) {
		this.accTimes = accTimes;
	}

	public List<Integer> getAccScores() {
		return accScores;
	}

	public void setAccScores(List<Integer> accScores) {
		this.accScores = accScores;
	}

	@Override
	public Object key() {
		return id;
	}
}
