package com.keyking.admin.data.deal;

import java.util.List;

public class Order {
	long id;//订单编号
	long dealId;//关联交易编号
	long buyId;//抢单人编号
	int num;//抢单数量
	float price;//抢单价钱
	byte helpFlag;//0普通模式，1中介模式
	String buyerName;//抢单人姓名
	String buyerIcon;//抢单人头像
	byte state;//订单状态
	List<String> times ;//订单状态修改时间列表
	Appraise sellerAppraise;//卖家评价
	Appraise buyerAppraise;//买家评价
	int revoke;//撤销状态0正常，1买家撤销，2卖家撤销，3双方都撤销
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDealId() {
		return dealId;
	}
	public void setDealId(long dealId) {
		this.dealId = dealId;
	}
	public long getBuyId() {
		return buyId;
	}
	public void setBuyId(long buyId) {
		this.buyId = buyId;
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
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getBuyerIcon() {
		return buyerIcon;
	}
	public void setBuyerIcon(String buyerIcon) {
		this.buyerIcon = buyerIcon;
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
	public Appraise getSellerAppraise() {
		return sellerAppraise;
	}
	public void setSellerAppraise(Appraise sellerAppraise) {
		this.sellerAppraise = sellerAppraise;
	}
	public Appraise getBuyerAppraise() {
		return buyerAppraise;
	}
	public void setBuyerAppraise(Appraise buyerAppraise) {
		this.buyerAppraise = buyerAppraise;
	}
	public int getRevoke() {
		return revoke;
	}
	public void setRevoke(int revoke) {
		this.revoke = revoke;
	}
}
