package com.joymeng.slg.domain.object.bag.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Equip implements DataKey{
	String id;
	String beizhuname;
	String equipName;
	String description;
	int equipType; //部位
	int rarity;//装备稀有度（额外添加的）
	int equipQuality;
	List<String> buffList;
	List<String> effectNumber;	
	int useLimitation;  //等级
	String upgradeEquipID;
	List<String> upgradeMaterial;
	List<String> upgradeCost;
	long upgradeTime;
	List<String> refineMaterial;
	List<String> refineCost;
	List<String> fuseMaterial;
	int fuseNumber;
	String iconId;

	public String getBeizhuname() {
		return beizhuname;
	}


	public void setBeizhuname(String beizhuname) {
		this.beizhuname = beizhuname;
	}


	public int getRarity() {
		return rarity;
	}


	public void setRarity(int rarity) {
		this.rarity = rarity;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getEquipName() {
		return equipName;
	}


	public void setEquipName(String equipName) {
		this.equipName = equipName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getEquipType() {
		return equipType;
	}


	public void setEquipType(int equipType) {
		this.equipType = equipType;
	}


	public int getEquipQuality() {
		return equipQuality;
	}


	public void setEquipQuality(int equipQuality) {
		this.equipQuality = equipQuality;
	}


	public List<String> getBuffList() {
		return buffList;
	}


	public void setBuffList(List<String> buffList) {
		this.buffList = buffList;
	}


	public List<String> getEffectNumber() {
		return effectNumber;
	}


	public void setEffectNumber(List<String> effectNumber) {
		this.effectNumber = effectNumber;
	}


	public int getUseLimitation() {
		return useLimitation;
	}


	public void setUseLimitation(int useLimitation) {
		this.useLimitation = useLimitation;
	}


	public String getUpgradeEquipID() {
		return upgradeEquipID;
	}


	public void setUpgradeEquipID(String upgradeEquipID) {
		this.upgradeEquipID = upgradeEquipID;
	}


	public List<String> getUpgradeMaterial() {
		return upgradeMaterial;
	}


	public void setUpgradeMaterial(List<String> upgradeMaterial) {
		this.upgradeMaterial = upgradeMaterial;
	}


	public List<String> getUpgradeCost() {
		return upgradeCost;
	}


	public void setUpgradeCost(List<String> upgradeCost) {
		this.upgradeCost = upgradeCost;
	}


	public long getUpgradeTime() {
		return upgradeTime;
	}


	public void setUpgradeTime(long upgradeTime) {
		this.upgradeTime = upgradeTime;
	}


	public List<String> getRefineMaterial() {
		return refineMaterial;
	}


	public void setRefineMaterial(List<String> refineMaterial) {
		this.refineMaterial = refineMaterial;
	}


	public List<String> getRefineCost() {
		return refineCost;
	}


	public void setRefineCost(List<String> refineCost) {
		this.refineCost = refineCost;
	}


	public List<String> getFuseMaterial() {
		return fuseMaterial;
	}


	public void setFuseMaterial(List<String> fuseMaterial) {
		this.fuseMaterial = fuseMaterial;
	}


	public int getFuseNumber() {
		return fuseNumber;
	}


	public void setFuseNumber(int fuseNumber) {
		this.fuseNumber = fuseNumber;
	}


	public String getIconId() {
		return iconId;
	}


	public void setIconId(String iconId) {
		this.iconId = iconId;
	}


	@Override
	public Object key() {
		// TODO Auto-generated method stub
		return id;
	}

}
