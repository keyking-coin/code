package com.joymeng.slg.domain.object.bag.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Item implements DataKey{
	String id;
	String beizhuname;
	String itemName;
	byte itemType;
	String itemDescription;
	byte itemLevel;
	String effectAfterUse;
	String iconId;
	List<String> useLimitation;
	int pileMax;
	byte useType;
	byte numberSign;
	long holdTime;
	int bindType;
	int storagePrice;
	int removePrice;
	String buffList;
	String goldPrice;
	int ranknumber;
	int sell;
	int synthesis;
	int materialType;
	String upgradeMaterialID;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBeizhuname() {
		return beizhuname;
	}

	public void setBeizhuname(String beizhuname) {
		this.beizhuname = beizhuname;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public int getRanknumber() {
		return ranknumber;
	}

	public void setRanknumber(int ranknumber) {
		this.ranknumber = ranknumber;
	}

	public String getGoldPrice() {
		return goldPrice;
	}

	public void setGoldPrice(String goldPrice) {
		this.goldPrice = goldPrice;
	}

	public int getSell() {
		return sell;
	}

	public void setSell(int sell) {
		this.sell = sell;
	}

	public int getSynthesis() {
		return synthesis;
	}

	public void setSynthesis(int synthesis) {
		this.synthesis = synthesis;
	}

	public byte getItemType() {
		return itemType;
	}

	public void setItemType(byte itemType) {
		this.itemType = itemType;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public byte getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(byte itemLevel) {
		this.itemLevel = itemLevel;
	}

	public String getEffectAfterUse() {
		return effectAfterUse;
	}

	public void setEffectAfterUse(String effectAfterUse) {
		this.effectAfterUse = effectAfterUse;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public List<String> getUseLimitation() {
		return useLimitation;
	}

	public void setUseLimitation(List<String> useLimitation) {
		this.useLimitation = useLimitation;
	}
	
	public int getPileMax() {
		return pileMax;
	}

	public void setPileMax(int pileMax) {
		this.pileMax = pileMax;
	}

	public int getMaterialType() {
		return materialType;
	}

	public void setMaterialType(int materialType) {
		this.materialType = materialType;
	}

	public String getUpgradeMaterialID() {
		return upgradeMaterialID;
	}

	public void setUpgradeMaterialID(String upgradeMaterialID) {
		this.upgradeMaterialID = upgradeMaterialID;
	}

	public byte getUseType() {
		return useType;
	}

	public void setUseType(byte useType) {
		this.useType = useType;
	}

	public byte getNumberSign() {
		return numberSign;
	}

	public void setNumberSign(byte numberSign) {
		this.numberSign = numberSign;
	}

	public long getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(long holdTime) {
		this.holdTime = holdTime;
	}
	
	public int getBindType() {
		return bindType;
	}

	public void setBindType(int bindType) {
		this.bindType = bindType;
	}

	public int getStoragePrice() {
		return storagePrice;
	}

	public void setStoragePrice(int storagePrice) {
		this.storagePrice = storagePrice;
	}

	public int getRemovePrice() {
		return removePrice;
	}

	public void setRemovePrice(int removePrice) {
		this.removePrice = removePrice;
	}

	public String getBuffList() {
		return buffList;
	}

	public void setBuffList(String buffList) {
		this.buffList = buffList;
	}

	@Override
	public Object key() {
		return id;
	}
}
