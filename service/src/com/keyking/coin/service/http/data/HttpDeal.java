package com.keyking.coin.service.http.data;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;

public class HttpDeal implements Instances{
	long id;//编号
	long uid;//用户编号
	byte sellFlag;//出售帖还是求购帖
	byte type;//类型0入库，1现货
	byte helpFlag;//可以使用中介服务;0未开启，1开启。
	byte favorite;
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	int num;//藏品数量
	String validTime = "永久";//有效时间
	String createTime;//创建时间
	String other;//其他描述
	List<HttpRevert> reverts    = new ArrayList<HttpRevert>();//回复内容列表
	List<HttpOrder> orders  = new ArrayList<HttpOrder>();//订单
	String issueName;
	String issueIcon;
	
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
	
	public List<HttpRevert> getReverts() {
		return reverts;
	}

	public void setReverts(List<HttpRevert> reverts) {
		this.reverts = reverts;
	}

	public List<HttpOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<HttpOrder> orders) {
		this.orders = orders;
	}

	public String getIssueName() {
		return issueName;
	}

	public void setIssueName(String issueName) {
		this.issueName = issueName;
	}

	public String getIssueIcon() {
		return issueIcon;
	}

	public void setIssueIcon(String issueIcon) {
		this.issueIcon = issueIcon;
	}

	public byte getFavorite() {
		return favorite;
	}

	public void setFavorite(byte favorite) {
		this.favorite = favorite;
	}

	public void copy(Deal deal,UserCharacter look) {
		id         = deal.getId();
		uid        = deal.getUid();
		sellFlag   = deal.getSellFlag();
		type       = deal.getType();
		helpFlag   = deal.getHelpFlag();
		if (look != null && look.getFavorites().contains(id)){
			favorite = 1;
		}else{
			favorite = 0;
		}
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = deal.getNum();
		validTime  = deal.getValidTime();
		createTime = deal.getCreateTime();
		other      = deal.getOther();
		UserCharacter user = CTRL.search(uid);
		issueName  = user.getNikeName();
		issueIcon  = user.getFace();
		for (Revert revert : deal.getReverts()){
			HttpRevert hr = new HttpRevert();
			hr.copy(revert);
			reverts.add(hr);
		}
		for (DealOrder order : deal.getOrders()){
			HttpOrder ho = new HttpOrder();
			ho.copy(order);
			orders.add(ho);
		}
	}
	
}
