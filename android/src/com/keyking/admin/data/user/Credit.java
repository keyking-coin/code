package com.keyking.admin.data.user;

public class Credit {
	float curValue;//已使用额度
	float maxValue ;//最大信用额度,每个人一开始就10万额度
	float tempMaxValue ;//临时信用额度
	float totalDealValue;//总的成交金额
	int hp;//好评次数
	int zp;//中评次数
	int cp;//差评次数
	public float getCurValue() {
		return curValue;
	}
	public void setCurValue(float curValue) {
		this.curValue = curValue;
	}
	public float getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	public float getTempMaxValue() {
		return tempMaxValue;
	}
	public void setTempMaxValue(float tempMaxValue) {
		this.tempMaxValue = tempMaxValue;
	}
	public float getTotalDealValue() {
		return totalDealValue;
	}
	public void setTotalDealValue(float totalDealValue) {
		this.totalDealValue = totalDealValue;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}
	public int getZp() {
		return zp;
	}
	public void setZp(int zp) {
		this.zp = zp;
	}
	public int getCp() {
		return cp;
	}
	public void setCp(int cp) {
		this.cp = cp;
	}
	
	
}
