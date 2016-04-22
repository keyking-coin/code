package com.keyking.coin.service.tranform.page.order;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class TransformOrderListInfo implements Instances ,Comparable<TransformOrderListInfo>{
	
	long id;//订单编号
	long issueId;//发布人编号
	byte type;//类型0入库，1现货
	String issueName;//发布人昵称
	long grabId;//抢单人编号
	String grabName;//抢单人姓名
	byte sellFlag;//出售帖还是求购帖
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	int revoke;//状态
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	String time;//抢单时间
	int num;//抢单数量
	byte state;//订单状态

	public void copy(Deal deal,DealOrder order){
		id         = order.getId();
		issueId    = deal.getUid();
		type       = deal.getType();
		helpFlag   = deal.getHelpFlag();
		revoke     = order.getRevoke();
		UserCharacter user = CTRL.search(issueId);
		issueName  = user.getNikeName();
		grabId     = order.getBuyId();
		user       = CTRL.search(grabId);
		grabName   = user.getNikeName();
		sellFlag   = deal.getSellFlag();
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = order.getNum();
		state      = order.getState();
		time       = order.getTimes().get(0);
	}
	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public long getIssueId() {
		return issueId;
	}


	public void setIssueId(long issueId) {
		this.issueId = issueId;
	}


	public String getIssueName() {
		return issueName;
	}


	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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


	public byte getSellFlag() {
		return sellFlag;
	}


	public void setSellFlag(byte sellFlag) {
		this.sellFlag = sellFlag;
	}


	public String getBourse() {
		return bourse;
	}


	public void setBourse(String bourse) {
		this.bourse = bourse;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public float getPrice() {
		return price;
	}


	public void setPrice(float price) {
		this.price = price;
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


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}


	public byte getState() {
		return state;
	}


	public void setState(byte state) {
		this.state = state;
	}


	public byte getHelpFlag() {
		return helpFlag;
	}

	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
	}

	public int getRevoke() {
		return revoke;
	}

	public void setRevoke(int revoke) {
		this.revoke = revoke;
	}

	@Override
	public int compareTo(TransformOrderListInfo o) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(o.time);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}
	
	
}
