package com.joymeng.slg.domain.shop.data;

import java.util.List;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Shop implements DataKey {
	String id; 
	String itemid; 
	int labelType; 
	int goodsStatus; 
	String salesLabels; 
	int normalPrice; 
	int saleSprice; 
	List<Integer> salesWeekDay; 
	int personLimitNum; 
	int serverLimitNum; 
	int rank; 
	int vipLevel; 
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public int getLabelType() {
		return labelType;
	}

	public void setLabelType(int labelType) {
		this.labelType = labelType;
	}

	public int getGoodsStatus() {
		return goodsStatus;
	}

	public void setGoodsStatus(int goodsStatus) {
		this.goodsStatus = goodsStatus;
	}

	public String getSalesLabels() {
		return salesLabels;
	}

	public void setSalesLabels(String salesLabels) {
		this.salesLabels = salesLabels;
	}

	public int getNormalPrice() {
		return normalPrice;
	}

	public void setNormalPrice(int normalPrice) {
		this.normalPrice = normalPrice;
	}

	public int getSaleSprice() {
		return saleSprice;
	}

	public void setSaleSprice(int saleSprice) {
		this.saleSprice = saleSprice;
	}

	public List<Integer> getSalesWeekDay() {
		return salesWeekDay;
	}

	public void setSalesWeekDay(List<Integer> salesWeekDay) {
		this.salesWeekDay = salesWeekDay;
	}

	public int getPersonLimitNum() {
		return personLimitNum;
	}

	public void setPersonLimitNum(int personLimitNum) {
		this.personLimitNum = personLimitNum;
	}

	public int getServerLimitNum() {
		return serverLimitNum;
	}

	public void setServerLimitNum(int serverLimitNum) {
		this.serverLimitNum = serverLimitNum;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
	}

	@Override
	public Object key() {
		return id;
	}

	
	public boolean checkWeekDay(){
		if (salesWeekDay.size() > 0){
			int week = TimeUtils.now().getDayOfWeek();
			return salesWeekDay.contains(week);
		}
		return true;
	}
	
	public int sellPrice(){
		if (goodsStatus == 2){//促销价
			return saleSprice;
		}
		return normalPrice;
	}
}
