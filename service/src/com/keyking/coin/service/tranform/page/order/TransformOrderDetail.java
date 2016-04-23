package com.keyking.coin.service.tranform.page.order;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;

public class TransformOrderDetail implements Instances{
	long id;//订单编号
	long issueId;//发布人编号
	String issueName;//发布人昵称
	long grabId;//抢单人编号
	String grabName;//抢单人姓名
	byte sellFlag;//出售帖还是求购帖
	byte helpFlag;//是否中介
	String bourse;//文交所名称
	String name;//藏品名称
	float price;//藏品单价
	String monad;//单位
	String issueTime;//发布时间
	String validTime;//有效时间
	String other;//描述
	int num;//抢单数量
	byte state;//订单状态
	List<String> times;//订单状态时间
	DealAppraise buyerAppraise;//买家评价
	DealAppraise sellerAppraise;//卖家评价
	
	public void copy(Deal deal,DealOrder order){
		id         = order.getId();
		issueId    = deal.getUid();
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
		issueTime  = deal.getCreateTime();
		validTime  = deal.getValidTime();
		other      = deal.getOther();
		helpFlag   = deal.getHelpFlag();
		times      = order.getTimes();
		buyerAppraise  = order.getBuyerAppraise();
		sellerAppraise = order.getSellerAppraise();
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
	
	public String getIssueTime() {
		return issueTime;
	}
	
	public void setIssueTime(String issueTime) {
		this.issueTime = issueTime;
	}
	
	public String getValidTime() {
		return validTime;
	}
	
	public void setValidTime(String validTime) {
		this.validTime = validTime;
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
	
	public List<String> getTimes() {
		return times;
	}
	
	public void setTimes(List<String> times) {
		this.times = times;
	}
	
	public DealAppraise getBuyerAppraise() {
		return buyerAppraise;
	}
	
	public void setBuyerAppraise(DealAppraise buyerAppraise) {
		this.buyerAppraise = buyerAppraise;
	}
	
	public DealAppraise getSellerAppraise() {
		return sellerAppraise;
	}
	
	public void setSellerAppraise(DealAppraise sellerAppraise) {
		this.sellerAppraise = sellerAppraise;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
}