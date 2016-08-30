package com.joymeng.slg.domain.object.task.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Task2 implements DataKey{
	String id;
	String mainID;
	int branchID;
	int designid;
	List<String> completeConditon;
	List<String> openTask;
	List<String> rewardList;
	String taskName;
	String taskDescription;
	String iconId;
	int	stage;
	String jumpto;
	int rank;
	List<String> itemReward;
	List<String> armyReward;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMainID() {
		return mainID;
	}
	public void setMainID(String mainID) {
		this.mainID = mainID;
	}
	public int getBranchID() {
		return branchID;
	}
	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}
	public int getDesignid() {
		return designid;
	}
	public void setDesignid(int designid) {
		this.designid = designid;
	}
	public List<String> getCompleteConditon() {
		return completeConditon;
	}
	public void setCompleteConditon(List<String> completeConditon) {
		this.completeConditon = completeConditon;
	}
	public List<String> getOpenTask() {
		return openTask;
	}
	public void setOpenTask(List<String> openTask) {
		this.openTask = openTask;
	}
	public List<String> getRewardList() {
		return rewardList;
	}
	public void setRewardList(List<String> rewardList) {
		this.rewardList = rewardList;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getIconId() {
		return iconId;
	}
	public void setIconId(String iconId) {
		this.iconId = iconId;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getJumpto() {
		return jumpto;
	}
	public void setJumpto(String jumpto) {
		this.jumpto = jumpto;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public List<String> getItemReward() {
		return itemReward;
	}
	public void setItemReward(List<String> itemReward) {
		this.itemReward = itemReward;
	}
	public List<String> getArmyReward() {
		return armyReward;
	}
	public void setArmyReward(List<String> armyReward) {
		this.armyReward = armyReward;
	}
	@Override
	public Object key() {
		return id;
	}

	
}
