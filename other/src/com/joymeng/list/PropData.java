package com.joymeng.list;

public class PropData {
	String type;// 类型
	long num;// 所有元宝数量
	long active_num;// 活跃元宝数量
	long quiet_num;// 沉寂元宝数量(玩家连续15天未登录)
	long uid_num;// 持有元宝人数
	long active_uid_num;// 持有活跃元宝人数
	long quiet_uid_num;// 持有沉寂元宝人数

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public long getActive_num() {
		return active_num;
	}

	public void setActive_num(long active_num) {
		this.active_num = active_num;
	}

	public long getQuiet_num() {
		return quiet_num;
	}

	public void setQuiet_num(long quiet_num) {
		this.quiet_num = quiet_num;
	}

	public long getUid_num() {
		return uid_num;
	}

	public void setUid_num(long uid_num) {
		this.uid_num = uid_num;
	}

	public long getActive_uid_num() {
		return active_uid_num;
	}

	public void setActive_uid_num(long active_uid_num) {
		this.active_uid_num = active_uid_num;
	}

	public long getQuiet_uid_num() {
		return quiet_uid_num;
	}

	public void setQuiet_uid_num(long quiet_uid_num) {
		this.quiet_uid_num = quiet_uid_num;
	}

}
