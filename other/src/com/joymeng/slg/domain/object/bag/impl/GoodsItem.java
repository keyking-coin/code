package com.joymeng.slg.domain.object.bag.impl;

import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.net.ParametersEntity;

/**
 * 物品类
 * @author tanyong
 *
 */
public class GoodsItem extends ItemCell {

	@Override
	public byte getType() {
		return ExpeditePackageType.PACKAGE_TYPE_GOODS.getType();
	}

	@Override
	public String primaryKey() {
		return key;
	}

	@Override
	public void deserialize(String str) {
		
	}

	@Override
	public String serialize() {
		return "null";
	}

	@Override
	public void _sendClient(ParametersEntity param) {
		// TODO Auto-generated method stub
		
	}
	
	
}
