package com.joymeng.slg.domain.object.task.data;

import com.joymeng.Instances;

public class RoleHonor implements Instances {
	String id;  //荣誉任务Id
	int starNum; //子任务完成个数=星星点亮个数 只要任务完成了，对应星星就会亮
	int reNum;   //已领取（星星）金币奖励个数
	int schedule; //任务进度
	long uid;      //角色Id

	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getSchedule() {
		return schedule;
	}
	public void setSchedule(int schedule) {
		this.schedule = schedule;
	}
	public int getReNum() {
		return reNum;
	}
	public void setReNum(int reNum) {
		this.reNum = reNum;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getStarNum() {
		return starNum;
	}
	public void setStarNum(int starNum) {
		this.starNum = starNum;
	}
}
