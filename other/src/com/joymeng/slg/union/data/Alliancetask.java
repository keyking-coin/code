package com.joymeng.slg.union.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Alliancetask implements DataKey {
	long id;
	//等级区间
	List<String> level;
	//随机类型
	int randomtype;
	//任务类型
	int tasktype;
	//刷新时间
	String readytime;
	//持续时间
	int lasttime;
	//完成条件
	List<String> taskcondition;
	List<String> taskreward;
	String taskicon;

	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public List<String> getLevel() {
		return level;
	}


	public void setLevel(List<String> level) {
		this.level = level;
	}


	public int getRandomtype() {
		return randomtype;
	}


	public void setRandomtype(int randomtype) {
		this.randomtype = randomtype;
	}


	public int getTasktype() {
		return tasktype;
	}


	public void setTasktype(int tasktype) {
		this.tasktype = tasktype;
	}


	public String getReadytime() {
		return readytime;
	}


	public void setReadytime(String readytime) {
		this.readytime = readytime;
	}


	public int getLasttime() {
		return lasttime;
	}


	public void setLasttime(int lasttime) {
		this.lasttime = lasttime;
	}


	public List<String> getTaskcondition() {
		return taskcondition;
	}


	public void setTaskcondition(List<String> taskcondition) {
		this.taskcondition = taskcondition;
	}


	public List<String> getTaskreward() {
		return taskreward;
	}


	public void setTaskreward(List<String> taskreward) {
		this.taskreward = taskreward;
	}


	public String getTaskicon() {
		return taskicon;
	}


	public void setTaskicon(String taskicon) {
		this.taskicon = taskicon;
	}


	@Override
	public Object key() {
		return id;
	}

}
