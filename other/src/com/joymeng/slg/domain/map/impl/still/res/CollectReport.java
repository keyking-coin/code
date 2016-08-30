package com.joymeng.slg.domain.map.impl.still.res;

public class CollectReport {
	String time;// 发生时间
	int position;// 发生的坐标
	float collect;// 采集量
	int level;//等级
	String key;//固化编号

	public CollectReport() {
	}

	public CollectReport(String time, int position, float collect,int level, String  key) {
		this.time = time;
		this.position = position;
		this.collect = collect;
		this.level = level;
		this.key = key;
	}

	public String getTime() {
		return time;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public float getCollect() {
		return collect;
	}

	public void setCollect(float collect) {
		this.collect = collect;
	}

}
