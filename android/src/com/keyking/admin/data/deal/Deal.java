package com.keyking.admin.data.deal;

import java.util.List;

public class Deal {
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
	List<Revert> reverts;//回复内容列表
	List<Order> orders;//订单
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
	public List<Revert> getReverts() {
		return reverts;
	}
	public void setReverts(List<Revert> reverts) {
		this.reverts = reverts;
	}
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
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
}
