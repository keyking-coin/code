package com.keyking.coin.service.tranform.page.order;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.Instances;

public class TransformOrderDetail implements Instances{
	long dealId;
	long orderId;//订单编号
	long issueId;//发布人编号
	String issueName;//发布人昵称
	long grabId;//抢单人编号
	String grabName;//抢单人姓名
	byte type;
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
	int buyerNum;//买家确认数量
	int sellerNum;//买家发货数量或者入库数量
	byte state;//订单状态
	int revoke;
	List<String> times;//订单状态时间
	DealAppraise buyerAppraise;//买家评价
	DealAppraise sellerAppraise;//卖家评价
	
	public void copy(Deal deal,DealOrder order){
		dealId     = deal.getId();
		orderId    = order.getId();
		issueId    = deal.getUid();
		UserCharacter user = CTRL.search(issueId);
		issueName  = user.getNikeName();
		grabId     = order.getBuyId();
		user       = CTRL.search(grabId);
		grabName   = user.getNikeName();
		type       = deal.getType();
		sellFlag   = deal.getSellFlag();
		bourse     = deal.getBourse();
		name       = deal.getName();
		price      = deal.getPrice();
		monad      = deal.getMonad();
		num        = order.getNum();
		buyerNum   = order.getBuyerNum();
		sellerNum  = order.getSellerNum();
		state      = order.getState();
		issueTime  = deal.getCreateTime();
		validTime  = deal.getValidTime();
		other      = deal.getOther();
		helpFlag   = deal.getHelpFlag();
		times      = order.getTimes();
		revoke     = order.getRevoke();
		buyerAppraise  = order.getBuyerAppraise();
		sellerAppraise = order.getSellerAppraise();
	}
	
	public long getDealId() {
		return dealId;
	}

	public void setDealId(long dealId) {
		this.dealId = dealId;
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
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

	public int getRevoke() {
		return revoke;
	}

	public void setRevoke(int revoke) {
		this.revoke = revoke;
	}
	
	public ModuleResp clientMessage(byte type){
		ModuleResp modules = new ModuleResp();
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_ORDER);
		module.setFlag(type);
		module.add("order",this);
		modules.addModule(module);
		return modules;
	}
}
