package com.joymeng.slg.domain.object.bag.impl;

import com.joymeng.slg.domain.map.impl.dynamic.ExpeditePackageType;
import com.joymeng.slg.domain.object.bag.ItemCell;
import com.joymeng.slg.net.ParametersEntity;

public class OtherItem extends ItemCell {

	public void init(long uid,String key,int num) {
		this.uid=uid;
		this.key=key;
		this.num=num;
	}
	
	@Override
	public byte getType() {
		return ExpeditePackageType.PACKAGE_TYPE_STONE.getType();
	}

	@Override
	public String primaryKey(){
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
