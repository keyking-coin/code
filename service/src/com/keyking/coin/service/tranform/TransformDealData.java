package com.keyking.coin.service.tranform;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class TransformDealData implements Instances,Comparable<TransformDealData>,SerializeEntity{
	long id;//编号
	long uid;//用户编号
	byte sellFlag;//出售帖还是求购帖
	byte type;//类型0入库，1现货
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	boolean revoke;//true撤销，false未撤销
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	int num;//藏品数量
	String validTime = "永久";//有效时间
	String createTime;//创建时间
	String other;//其他描述
	List<TransformRevertData> reverts    = new ArrayList<TransformRevertData>();//回复内容列表
	List<TransformOrderData> orders      = new ArrayList<TransformOrderData>();//订单
	String issueName;
	
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
	
	public byte getSellFlag() {
		return sellFlag;
	}
	
	public void setSellFlag(byte sellFlag) {
		this.sellFlag = sellFlag;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public byte getHelpFlag() {
		return helpFlag;
	}
	
	public void setHelpFlag(byte helpFlag) {
		this.helpFlag = helpFlag;
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
	
	public String getOther() {
		return other;
	}
	
	public void setOther(String other) {
		this.other = other;
	}
	
	public List<TransformRevertData> getReverts() {
		return reverts;
	}

	public void setReverts(List<TransformRevertData> reverts) {
		this.reverts = reverts;
	}

	public List<TransformOrderData> getOrders() {
		return orders;
	}

	public void setOrders(List<TransformOrderData> orders) {
		this.orders = orders;
	}

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public boolean isRevoke() {
		return revoke;
	}

	public void setRevoke(boolean revoke) {
		this.revoke = revoke;
	}

	public void copy(Deal deal) {
		id         = deal.getId();
		uid        = deal.getUid();
		sellFlag   = deal.getSellFlag();
		type       = deal.getType();
		helpFlag   = deal.getHelpFlag();
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = deal.getLeftNum();
		validTime  = deal.getValidTime();
		createTime = deal.getCreateTime();
		other      = deal.getOther();
		revoke     = deal.isRevoke();
		UserCharacter user = CTRL.search(uid);
		issueName  = user.getNikeName();
		for (Revert revert : deal.getReverts()){
			if (revert.isRevoke()){
				continue;
			}
			TransformRevertData hr = new TransformRevertData();
			hr.copy(revert);
			reverts.add(hr);
		}
		for (DealOrder order : deal.getOrders()){
			if (order.checkRevoke()){
				continue;
			}
			TransformOrderData ho = new TransformOrderData();
			ho.copy(deal,order);
			orders.add(ho);
		}
	}
	
	public void copy(Deal deal,DealOrder order) {
		id         = deal.getId();
		uid        = deal.getUid();
		sellFlag   = deal.getSellFlag();
		type       = deal.getType();
		helpFlag   = deal.getHelpFlag();
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = deal.getLeftNum();
		validTime  = deal.getValidTime();
		createTime = deal.getCreateTime();
		other      = deal.getOther();
		revoke     = deal.isRevoke();
		UserCharacter user = CTRL.search(uid);
		issueName  = user.getNikeName();
		for (Revert revert : deal.getReverts()){
			if (revert.isRevoke()){
				continue;
			}
			TransformRevertData hr = new TransformRevertData();
			hr.copy(revert);
			reverts.add(hr);
		}
		TransformOrderData ho = new TransformOrderData();
		ho.copy(deal,order);
		orders.add(ho);
	}
	
	@Override
	public int compareTo(TransformDealData o) {
		DateTime time1 = TimeUtils.getTime(createTime);
		DateTime time2 = TimeUtils.getTime(o.createTime);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}

	@Override
	public void serialize(DataBuffer out) {
		// TODO Auto-generated method stub
		
	}
}
