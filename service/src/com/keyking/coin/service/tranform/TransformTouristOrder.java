package com.keyking.coin.service.tranform;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.TimeUtils;

public class TransformTouristOrder implements Comparable<TransformTouristOrder>,SerializeEntity{
	long dealId;
	long orderId;
	String type;
	String name;
	String bourse;
	float price;
	int num;
	String monad;
	String time;
	
	public TransformTouristOrder(){
		
	}
	
	public TransformTouristOrder(Deal deal,DealOrder order){
		dealId = deal.getId();
		orderId = order.getId();
		String[] ss = deal.getBourse().split(",");
		bourse = ss[1];
		type = deal.getType() == 0 ? "入库" : "过户";
		name = deal.getName();
		price = order.getPrice();
		num = order.getNum();
		monad = deal.getMonad();
		time = order.getTimes().get(0);
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBourse() {
		return bourse;
	}

	public void setBourse(String bourse) {
		this.bourse = bourse;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int compareTo(TransformTouristOrder o) {
		DateTime d1 = TimeUtils.getTime(time);
		DateTime d2 = TimeUtils.getTime(o.time);
		if (d1.isBefore(d2)){
			return 1;
		}
		return -1;
	}

	@Override
	public void serialize(DataBuffer out) {
		
	}
}
