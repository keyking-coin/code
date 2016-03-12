package com.keyking.coin.service.http.data;

import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;

public class HttpOrder implements Instances{
	long id;
	long dealId;
	long buyId;
	int num;
	float price;
	byte helpFlag;
	String buyerName;
	String buyerIcon;
	DealAppraise sellerAppraise = new DealAppraise();
	DealAppraise buyerAppraise = new DealAppraise();
	
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

	public DealAppraise getSellerAppraise() {
		return sellerAppraise;
	}
	
	public void setSellerAppraise(DealAppraise sellerAppraise) {
		this.sellerAppraise = sellerAppraise;
	}
	
	public DealAppraise getBuyerAppraise() {
		return buyerAppraise;
	}
	
	public void setBuyerAppraise(DealAppraise buyerAppraise) {
		this.buyerAppraise = buyerAppraise;
	}
	
	public void copy(DealOrder order) {
		id                 = order.getId();
		dealId             = order.getDealId();
		buyId              = order.getBuyId();
		UserCharacter user = CTRL.search(buyId);
		buyerName          = user.getNikeName();
		buyerIcon          = user.getFace();
		helpFlag           = order.getHelpFlag();
		num                = order.getNum();
		price              = order.getPrice();
		sellerAppraise     = order.getSellerAppraise();
		buyerAppraise      = order.getBuyerAppraise();
	}
}
