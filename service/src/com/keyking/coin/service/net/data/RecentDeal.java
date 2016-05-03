package com.keyking.coin.service.net.data;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;

public class RecentDeal {
	String bource;//文交所或者是成交城市
	String type;//交易类型
	String dealName;//交易藏品名称
	float price;//价钱
	long dealId;//交易编号
	long orderId;//订单编号
	String time;//成交时间
	int num;//数量
	String monad;//单位
	
	public RecentDeal() {
		
	}
	
	public RecentDeal(Deal deal, DealOrder order) {
		String[] ss = deal.getBourse().split(",");
		dealId = deal.getId();
		orderId = order.getId();
		bource = ss[1];
		type = deal.getType() == 0 ? "入库" : "现货";
		dealName = deal.getName();
		time = order.getTimes().get(0);
		price = order.getPrice();
		num = order.getNum();
		monad = deal.getMonad();
	}
	
	public String getBource() {
		return bource;
	}
	public void setBource(String bource) {
		this.bource = bource;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDealName() {
		return dealName;
	}
	public void setDealName(String dealName) {
		this.dealName = dealName;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
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
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getMonad() {
		return monad;
	}
	public void setMonad(String monad) {
		this.monad = monad;
	}
}
