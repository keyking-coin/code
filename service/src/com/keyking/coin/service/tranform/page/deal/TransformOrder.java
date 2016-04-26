package com.keyking.coin.service.tranform.page.deal;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;


public class TransformOrder implements Instances , Comparable<TransformOrder>{
	long grabId;//抢单人编号
	String grabName;//抢单人姓名
	int num;//抢单数量
	float price;//抢单价钱
	byte helpFlag;//0普通模式,1中介模式
	byte state;//订单状态
	String time;//订单状态的时间
	
	public void copy(DealOrder order) {
		grabId             = order.getBuyId();
		UserCharacter user = CTRL.search(grabId);
		grabName           = user.getNikeName();
		helpFlag           = order.getHelpFlag();
		num                = order.getNum();
		price              = order.getPrice();
		state              = order.getState();
		time               = order.getTimes().get(0);
	}
	
	public long getGrabId() {
		return grabId;
	}

	public void setGrabId(long grabId) {
		this.grabId = grabId;
	}

	public String getGrabName() {
		return grabName;
	}

	public void setGrabName(String grabName) {
		this.grabName = grabName;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public byte getHelpFlag() {
		return helpFlag;
	}

	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public int compareTo(TransformOrder o) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(o.time);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}
	
}
