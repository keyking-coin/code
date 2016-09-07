package com.keyking.coin.service.domain.user;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;

public class Recharge {
	
	float curMoney     = 10;//当前剩余
	
	
	float historyMoney = 10;//历史累计充值
	
	List<RechargeOrder> orders = new ArrayList<RechargeOrder>();//充值订单列表
	
	public float getCurMoney() {
		return curMoney;
	}

	public void setCurMoney(float curMoney) {
		this.curMoney = curMoney;
	}
	
	public void changeMoney(float curMoney) {
		this.curMoney += curMoney;
		if (this.curMoney < 0){
			this.curMoney = 0;
		}
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

	public String serialize(){
		return JsonUtil.ObjectToJsonString(this);
	}
	
	public void deserialize(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		Recharge recharge = JsonUtil.JsonToObject(str,Recharge.class);
		curMoney = recharge.curMoney;
		historyMoney = recharge.historyMoney;
		orders.clear();
		orders.addAll(recharge.orders);
	}
	
	public void _serialize(DataBuffer buffer) {
		buffer.putUTF("" + curMoney);
		buffer.putUTF("" + historyMoney);
		buffer.putInt(orders.size());
		for (RechargeOrder order : orders){
			order._serialize(buffer);
		}
	}

	public void copy(Recharge recharge) {
		if (recharge == null){
			return;
		}
		curMoney     = recharge.curMoney;
		historyMoney = recharge.historyMoney;
	}
}
