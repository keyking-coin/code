package com.keyking.coin.service.http.data;

public class HttpTouristDealOrder {
	long dealId;
	long orderId;
	String des;
	String time;
	
	public long getDealId() {
		return dealId;
	}
	
	public void setDealId(long dealId) {
		this.dealId = dealId;
	}
	
	public long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public String getDes() {
		return des;
	}
	
	public void setDes(String des) {
		this.des = des;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
