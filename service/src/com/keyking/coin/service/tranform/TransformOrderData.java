package com.keyking.coin.service.tranform;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class TransformOrderData implements Instances,SerializeEntity{
	long id;//订单编号
	long dealId;//关联交易编号
	long buyId;//抢单人编号
	byte type;
	int num;//抢单数量
	int buyerNum;
	int sellerNum;
	float price;//抢单价钱
	byte helpFlag;//0普通模式，1中介模式
	String buyerName;//抢单人姓名
	String buyerIcon;//抢单人头像
	byte state;//订单状态
	List<String> times = new ArrayList<String>();//订单状态修改时间列表
	DealAppraise sellerAppraise = new DealAppraise();//卖家评价
	DealAppraise buyerAppraise = new DealAppraise();//买家评价
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
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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

	public int getRevoke() {
		return revoke;
	}

	public void setRevoke(int revoke) {
		this.revoke = revoke;
	}

	public int getBuyerNum() {
		return buyerNum;
	}

	public void setBuyerNum(int buyerNum) {
		this.buyerNum = buyerNum;
	}

	public int getSellerNum() {
		return sellerNum;
	}

	public void setSellerNum(int sellerNum) {
		this.sellerNum = sellerNum;
	}

	public void copy(Deal deal,DealOrder order) {
		id                 = order.getId();
		dealId             = order.getDealId();
		buyId              = order.getBuyId();
		type               = deal.getType();
		UserCharacter user = CTRL.search(buyId);
		buyerName          = user.getNikeName();
		buyerIcon          = user.getFace();
		helpFlag           = order.getHelpFlag();
		num                = order.getNum();
		buyerNum           = order.getBuyerNum();
		sellerNum          = order.getSellerNum();
		price              = order.getPrice();
		sellerAppraise     = order.getSellerAppraise();
		buyerAppraise      = order.getBuyerAppraise();
		state              = order.getState();
		times.addAll(order.getTimes());
		revoke             = order.getRevoke();
	}

	@Override
	public void serialize(DataBuffer out) {
		// TODO Auto-generated method stub
		
	}
}
