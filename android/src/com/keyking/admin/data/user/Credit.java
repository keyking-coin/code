package com.keyking.admin.data.user;

public class Credit {
	float curValue;//��ʹ�ö��
	float maxValue ;//������ö��,ÿ����һ��ʼ��10����
	float tempMaxValue ;//��ʱ���ö��
	float totalDealValue;//�ܵĳɽ����
	int hp;//��������
	int zp;//��������
	int cp;//��������
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
