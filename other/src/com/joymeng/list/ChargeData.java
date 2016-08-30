package com.joymeng.list;

public class ChargeData {

	int rcount; // 新增UID数
	int rjcount;// 新增UUID数
	int mcount;// UUID充值人数
	int ncount;// UUID新用户充值人数
	int money;// UUID用户充值金额
	int nmoney;// UUID新用户充值金额
	String channelId; // 渠道Id

	public int getRcount() {
		return rcount;
	}

	public void setRcount(int rcount) {
		this.rcount = rcount;
	}

	public int getRjcount() {
		return rjcount;
	}

	public void setRjcount(int rjcount) {
		this.rjcount = rjcount;
	}

	public int getMcount() {
		return mcount;
	}

	public void setMcount(int mcount) {
		this.mcount = mcount;
	}

	public int getNcount() {
		return ncount;
	}

	public void setNcount(int ncount) {
		this.ncount = ncount;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getNmoney() {
		return nmoney;
	}

	public void setNmoney(int nmoney) {
		this.nmoney = nmoney;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

}
