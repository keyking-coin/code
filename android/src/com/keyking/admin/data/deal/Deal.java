package com.keyking.admin.data.deal;

import java.util.List;

public class Deal {
	long id;//���
	long uid;//�û����
	byte sellFlag;//��������������
	byte type;//����0��⣬1�ֻ�
	byte helpFlag;//����ʹ���н����;0δ������1������
	boolean revoke;//true������falseδ����
	String bourse;//�Ľ�������
	String name;//��Ʒ����
	float price;//��Ʒ����
	String monad;//��λ
	int num;//��Ʒ����
	String validTime = "����";//��Чʱ��
	String createTime;//����ʱ��
	String other;//��������
	List<Revert> reverts;//�ظ������б�
	List<Order> orders;//����
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
