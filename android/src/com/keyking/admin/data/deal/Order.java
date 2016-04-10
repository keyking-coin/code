package com.keyking.admin.data.deal;

import java.util.List;

public class Order {
	long id;//�������
	long dealId;//�������ױ��
	long buyId;//�����˱��
	int num;//��������
	float price;//������Ǯ
	byte helpFlag;//0��ͨģʽ��1�н�ģʽ
	String buyerName;//����������
	String buyerIcon;//������ͷ��
	byte state;//����״̬
	List<String> times ;//����״̬�޸�ʱ���б�
	Appraise sellerAppraise;//��������
	Appraise buyerAppraise;//�������
	int revoke;//����״̬0������1��ҳ�����2���ҳ�����3˫��������
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
