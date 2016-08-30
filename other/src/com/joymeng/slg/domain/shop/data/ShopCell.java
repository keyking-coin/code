package com.joymeng.slg.domain.shop.data;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.SerializeEntity;

public class ShopCell implements SerializeEntity{
	String id;//关联shop编号
	int num;//剩余数量:-1标示不限,0已售完;>0可购买限购数量
	
	public ShopCell(String id,int num){
		this.id = id;
		this.num = num;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putPrefixedString(id,JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(num);
	}
}
