package com.keyking.admin.data.user;

import java.util.ArrayList;
import java.util.List;

public class Recharge {
	float curMoney = 100;//��ǰʣ��
	float historyMoney;//��ʷ�ۼƳ�ֵ
	List<RechargeOrder> orders = new ArrayList<RechargeOrder>();//��ֵ�����б�
	public float getCurMoney() {
		return curMoney;
	}
	public void setCurMoney(float curMoney) {
		this.curMoney = curMoney;
	}
	public float getHistoryMoney() {
		return historyMoney;
	}
	public void setHistoryMoney(float historyMoney) {
		this.historyMoney = historyMoney;
	}
	public List<RechargeOrder> getOrders() {
		return orders;
	}
	public void setOrders(List<RechargeOrder> orders) {
		this.orders = orders;
	}
	
}
