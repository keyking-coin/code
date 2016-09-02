package com.keyking.coin.service.tranform.page.deal;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class TransformDealListInfo implements Instances,Comparable<TransformDealListInfo>{
	long id;//编号
	long uid;//用户编号
	byte type;//类型0入库，1现货
	String issueName;//发布人昵称
	byte sellFlag;//出售帖还是求购帖
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	boolean revoke;//true撤销，false未撤销
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	int num;//藏品数量
	int orderNums;//已抢单的数量
	String validTime = "永久";//有效时间
	String createTime;//创建时间
	
	public TransformDealListInfo(Deal deal){
		id         = deal.getId();
		uid        = deal.getUid();
		type       = deal.getType();
		sellFlag   = deal.getSellFlag();
		helpFlag   = deal.getHelpFlag();
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = deal.getLeftNum();
		orderNums  = deal.orderNum();
		validTime  = deal.getValidTime();
		createTime = deal.getCreateTime();
		revoke     = deal.isRevoke();
		UserCharacter user = CTRL.search(uid);
		issueName  = user.getNikeName();
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
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

	public byte getSellFlag() {
		return sellFlag;
	}
	
	public void setSellFlag(byte sellFlag) {
		this.sellFlag = sellFlag;
	}
	
	public byte getHelpFlag() {
		return helpFlag;
	}
	
	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
	}
	
	public boolean isRevoke() {
		return revoke;
	}
	
	public void setRevoke(boolean revoke) {
		this.revoke = revoke;
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
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	
	public int getOrderNums() {
		return orderNums;
	}

	public void setOrderNums(int orderNums) {
		this.orderNums = orderNums;
	}

	public String getValidTime() {
		return validTime;
	}
	
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public int compareTo(TransformDealListInfo o) {
		DateTime time1 = TimeUtils.getTime(createTime);
		DateTime time2 = TimeUtils.getTime(o.createTime);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}
}
