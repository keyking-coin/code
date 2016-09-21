package com.joymeng.slg.domain.object.bag.impl;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.MathUtils;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.domain.object.bag.data.Equip;
import com.joymeng.slg.domain.object.role.Role;

/**
 * 装备类
 * 
 * @author tanyong
 *
 */
public class EquipItem extends ItemCell {
	byte equipState = 0; // 装备的状态 0:空闲 1:装备人身上 2:正在升级中
	List<String> equipBuffIdLists = new ArrayList<String>(); // 装备的属性列表
																// eg:{"buffId","buffValue"}
	List<String> forgeAfterBuffIdLists = new ArrayList<String>(); // 锻造之后的Buff属性列表
																	// eg:{"buffId","buffValue"}
	List<String> upgradeMaterialLists = new ArrayList<String>();// 升级消耗的材料列表
																// eg:{"materialId|num","materialId|num"}

	public byte getEquipState() {
		return equipState;
	}

	public void setEquipState(byte equipState) {
		this.equipState = equipState;
	}

	public List<String> getEquipBuffIdLists() {
		return equipBuffIdLists;
	}

	public void setEquipBuffIdLists(List<String> equipBuffIdLists) {
		this.equipBuffIdLists = equipBuffIdLists;
	}

	public List<String> getForgeAfterBuffIdLists() {
		return forgeAfterBuffIdLists;
	}

	public void setForgeAfterBuffIdLists(List<String> forgeAfterBuffIdLists) {
		this.forgeAfterBuffIdLists = forgeAfterBuffIdLists;
	}

	public List<String> getUpgradeMaterialLists() {
		return upgradeMaterialLists;
	}

	public void setUpgradeMaterialLists(List<String> upgradeMaterialLists) {
		this.upgradeMaterialLists = upgradeMaterialLists;
	}

	public EquipItem clone(){
		EquipItem equipItem = new EquipItem();
		equipItem.setId(this.getId());
		equipItem.setKey(this.getKey());
		equipItem.setNum(this.getNum());
		equipItem.setState(this.getState());
		equipItem.setUid(this.getUid());
		equipItem.setEquipState(this.getEquipState());
		equipItem.setEquipBuffIdLists(this.getEquipBuffIdLists());
		equipItem.setForgeAfterBuffIdLists(this.getForgeAfterBuffIdLists());
		equipItem.setUpgradeMaterialLists(this.getUpgradeMaterialLists());
		return equipItem;
	}
	
	@Override
	public byte getType() {
		return ExpeditePackageType.PACKAGE_TYPE_EQUIP.getType();
	}

	@Override
	public String primaryKey() {
		return String.valueOf(id);
	}

	@Override
	public void deserialize(String str) {
		if (str == null) {
			return;
		}
		String[] strText = str.split(":");
		if (strText.length == 0) {
			return;
		}
		equipState = Byte.parseByte(strText[0]);
		String strTextString = strText[1];
		if (strTextString == null) {
			GameLog.error("deserialize---equip buff list is not null!");
			return;
		}
		String[] buffStrings = strTextString.split(",");
		String tempString = "";
		for (int i = 0; i < buffStrings.length; i++) {
			tempString = buffStrings[i];
			equipBuffIdLists.add(tempString);
		}

		strTextString = strText[2];
		if (strTextString != null && !strTextString.equals("null") && strTextString.length() > 0) {
			String[] forgeAfterBuffStrings = strTextString.split(",");
			for (int i = 0; i < forgeAfterBuffStrings.length; i++) {
				tempString = forgeAfterBuffStrings[i];
				forgeAfterBuffIdLists.add(tempString);
			}
		}
		String strumTextString = strText[3];
		if (strumTextString != null && !strumTextString.equals("null") && strumTextString.length() > 0) {
			String[] upgradeMaterilsStrings = strumTextString.split(",");
			String tempumStr = "";
			for (int i = 0; i < upgradeMaterilsStrings.length; i++) {
				tempumStr = upgradeMaterilsStrings[i];
				upgradeMaterialLists.add(tempumStr);
			}
		}
		// add buff
		// if(equipState == 1){
		// addEquipBuffList();
		// }
	}

	// add equip buff
	public void addEquipBuffList(Role role) {
		for (int i = 0; i < equipBuffIdLists.size(); i++) {
			role.getEffectAgent().addEquipBuff(role,equipBuffIdLists.get(i++), equipBuffIdLists.get(i), id);
		}
	}

	@Override
	public String serialize() {
		String val = "";
		val = String.valueOf(equipState) + ":";
		if (equipBuffIdLists == null) {
			GameLog.error("serialize----equip buff list is not null!");
			return null;
		}
		if (equipBuffIdLists != null && equipBuffIdLists.size() > 1) {
			for (int i = 0 ; i < equipBuffIdLists.size() ; i++){
				String tempBuffId = equipBuffIdLists.get(i);
				val += tempBuffId + ",";
			}
			val = val.substring(0, val.length() - 1);
		} else {
			val += "null";
		}
		val += ":";
		if (forgeAfterBuffIdLists != null && forgeAfterBuffIdLists.size() > 1) {
			for (int i = 0 ; i < forgeAfterBuffIdLists.size() ; i++){
				String tempBuffId = forgeAfterBuffIdLists.get(i);
				val += tempBuffId + ",";
			}
			val = val.substring(0, val.length() - 1);
		} else {
			val += "null";
		}
		val += ":";
		if (upgradeMaterialLists != null && upgradeMaterialLists.size() > 1) {
			for (int i = 0 ; i < upgradeMaterialLists.size() ; i++){
				String tempBuffId = upgradeMaterialLists.get(i);
				val += tempBuffId + ",";
			}
			val = val.substring(0, val.length() - 1);
		} else {
			val += "null";
		}
		return val;
	}

	@Override
	public void _sendClient(ParametersEntity param) {
		// TODO Auto-generated method stub
		param.put(equipState); // 装备的类型 0:空闲 1:装备在身上 byte 2:升级中
		if (equipBuffIdLists == null) {
			GameLog.error("_sendClient----equip buff list is not null!");
			return;
		}
		if (equipBuffIdLists != null && equipBuffIdLists.size() >= 2) {
			param.put(equipBuffIdLists.size() / 2); // 当前装备的BuffListId的大小
			for (int i = 0 ; i < equipBuffIdLists.size() ; i++){
				String tempEquipBuff = equipBuffIdLists.get(i);
				param.put(tempEquipBuff);
			}
		}
		int forgeAfterBuffIdListsize = forgeAfterBuffIdLists == null ? 0 : forgeAfterBuffIdLists.size();
		param.put(forgeAfterBuffIdListsize / 2); // 炼化之后的BuffListId大小
		if (forgeAfterBuffIdLists != null && forgeAfterBuffIdLists.size() >= 2) {
			for (int i = 0 ; i < forgeAfterBuffIdLists.size() ; i++){
				String tempForgeAfterBuff = forgeAfterBuffIdLists.get(i);
				param.put(tempForgeAfterBuff);
			}
		}
		if (equipState == 2) { // 升级中 下发升级所用的材料
			int upgradeMaterialSize = upgradeMaterialLists == null ? 0 : upgradeMaterialLists.size();
			param.put(upgradeMaterialSize);
			if (upgradeMaterialLists != null ) {
				for (int i = 0 ; i < upgradeMaterialLists.size() ; i++){
					String tempUpgradeMaterial = upgradeMaterialLists.get(i);
					param.put(tempUpgradeMaterial);
				}
			}
		}
	}

	/**
	 * @param 随机出一组BuffIdList
	 * @param key--equipId即装备固化表Id
	 */
	public List<String> randomBuffIdList(String keyId) {
		Equip equip = dataManager.serach(Equip.class, keyId);
		if (equip == null) {
			GameLog.error("equip base data is null! equipKeyId = " + keyId);
			return null;
		}
		List<String> buffList = equip.getBuffList();
		List<String> allBuffIdList = new ArrayList<String>();
		for (int index = 0; index < buffList.size(); index += 3) {
			String string = buffList.get(index);
			if (!string.isEmpty()) {
				allBuffIdList.add(string);
			}
		}
		List<String> effectNumberList = equip.getEffectNumber();
		List<String> buffIdLists = new ArrayList<String>(); // 作为最终的范围值buff的Id和对应的值
		List<String> effectNumber = new ArrayList<String>(); // buffId的个数列表
		int buffNumber = 0; // buff的个数
		List<String> buffIdList = new ArrayList<String>(); // buffId的列表
		List<String> buffIdAndValueList = new ArrayList<String>(); // 构造临时的buff列表(此时对应的值为0)
		if (effectNumberList.size() < 0) {
			return null;
		}
		effectNumber = MathUtils.randoms(effectNumberList, 1);
		if (effectNumber == null) {
			return null;
		}
		buffNumber = Integer.parseInt(effectNumber.get(0));
		buffIdList = MathUtils.randoms(allBuffIdList, buffNumber);
		if (buffIdList == null) {
			return null;
		}
		if (buffIdList.size() != buffNumber) {
			return null;
		}
		for (int i = 0; i < buffIdList.size(); i++) {
			buffIdAndValueList.add(buffIdList.get(i));
			buffIdAndValueList.add("0");
		}
		buffIdLists = randomBuffValueList(buffIdAndValueList, keyId);
		return buffIdLists;
	}

	/**
	 * @param 根据buffId列表获取其对应的值
	 * @param buffIdList
	 * @return
	 */
	public List<String> randomBuffValueList(List<String> buffIdList, String keyId) {
		Equip equip = dataManager.serach(Equip.class, keyId);
		if (equip == null) {
			GameLog.error("equip base data is null!");
			return null;
		}
		List<String> buffList = equip.getBuffList();
		List<String> buffIdAndValueList = new ArrayList<String>();// buffIdAnd值的列表
		float buffValueMax = 0; // buff的最大值
		float buffValueMin = 0; // buff的最小值
		float buffValue = 0; // buff的值
		int dataType = 0; // buff的类型 0-浮点 1-整数
		String currentBuffId = "";
		String tempBuffId = "";
		for (int index = 0; index < buffIdList.size(); index += 2) {
			for (int i = 0; i < buffList.size(); i += 3) {
				currentBuffId = buffIdList.get(index);
				tempBuffId = buffList.get(i);
				if (currentBuffId == null || tempBuffId == null || (!currentBuffId.equals(tempBuffId))) {
					continue;
				}
				dataType = Float.parseFloat(buffList.get(i + 1)) >= 1 ? 1 : 0;
				buffValueMin = Float.parseFloat(buffList.get(i + 1));
				buffValueMax = Float.parseFloat(buffList.get(i + 2));
				if (dataType == 0) {
					buffValue = (float) MathUtils.random((int) (buffValueMin * 1000), (int) (buffValueMax * 1000));
					buffValue /= 1000.0f;
				} else if (dataType == 1) {
					buffValue = (float) MathUtils.random((int) buffValueMin, (int) buffValueMax);
				}
				buffIdAndValueList.add(buffIdList.get(index));
				buffIdAndValueList.add(String.valueOf(buffValue));
				break;
			}
		}
		return buffIdAndValueList;
	}

	/**
	 * @param 随机出对应equipId的分解材料列表
	 * @param key--equipId即装备固化表Id
	 * @return
	 */
	public List<String> randomMaterialList(String keyId) {
		Equip equip = dataManager.serach(Equip.class, keyId);
		List<String> allFuseMateriaList = equip.getFuseMaterial();
		List<String> result = new ArrayList<String>();
		if (allFuseMateriaList == null) {
			GameLog.error("fuseMaterial base data is null where equipId=" + keyId);
			return null;
		}
		int fuseMaterialNumber = equip.getFuseNumber();
		String randomMaterialKeyId = "";
		String[] keyStrings = new String[allFuseMateriaList.size()];
		int[] rate = new int[allFuseMateriaList.size()];
		for (int i = 0; i < allFuseMateriaList.size(); i++) {
			String[] textString = allFuseMateriaList.get(i).split(":");
			if (textString.length < 2) {
				GameLog.error("fuseMaterial base data is null where equipId=" + keyId);
				return null;
			}
			keyStrings[i] = textString[0];
			rate[i] = Integer.parseInt(textString[1]);
		}
		for (int i = 0; i < fuseMaterialNumber; i++) {
			randomMaterialKeyId = MathUtils.getRandomObj(keyStrings,rate).toString();
			if (randomMaterialKeyId == null) {
				GameLog.error("random list<String> from MathUtils.random(List<String>,num) is fail!");
				continue;
			}
			result.add(randomMaterialKeyId);
		}
		return result;
	}
}
