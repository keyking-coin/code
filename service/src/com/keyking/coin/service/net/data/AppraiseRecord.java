package com.keyking.coin.service.net.data;

public class AppraiseRecord {
	int good;//累计好评次数
	int normal;//累计中评次数
	int bad;//累计差评次数
	public int getGood() {
		return good;
	}
	public void setGood(int good) {
		this.good = good;
	}
	public int getNormal() {
		return normal;
	}
	public void setNormal(int normal) {
		this.normal = normal;
	}
	public int getBad() {
		return bad;
	}
	public void setBad(int bad) {
		this.bad = bad;
	}
}
