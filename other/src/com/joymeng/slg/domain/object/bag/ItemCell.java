package com.joymeng.slg.domain.object.bag;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.object.bag.impl.EquipItem;
import com.joymeng.slg.domain.object.bag.impl.GoodsItem;
import com.joymeng.slg.domain.object.bag.impl.OtherItem;
import com.joymeng.slg.net.ParametersEntity;

public abstract class ItemCell implements Instances{
	protected long id;//数据库主键
	protected long uid;//玩家编号
	protected String key;//固化表编号
	protected long num = 1;//数量
	protected byte state=0;
	
	public static ItemCell create(byte type){
		ItemCell item = null;
		switch (type){
		case 0:
			item = new GoodsItem();
			break;
		case 1:
			item = new EquipItem();
			break;
		case 2:
			item = new OtherItem();
			break;
		}
		item.setState((byte)0);
		return item;
	}
	
	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	public void serializeEntiy(JoyBuffer out){
		out.putLong(id);
		out.putPrefixedString(key,JoyBuffer.STRING_TYPE_SHORT);
		out.putLong(num);
		out.put(getType());
		out.putPrefixedString(serialize(),JoyBuffer.STRING_TYPE_SHORT);
	}
	
	public void deserializeEntiy(long id, String key, long num, String str){
		this.id = id;
		this.key = key;
		this.num = num;
		deserialize(str);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public void adjustNum(){
		if (num > Long.MAX_VALUE){
			num = Long.MAX_VALUE;
		}
		if (num < 0){
			num = 0;
		}
	}
	
	public void addNum(long num){
		if (num < 0){
			return;
		}
		this.num += num;
		adjustNum();
	}
	
	public void redNum(long num){
		if (num < 0){
			return;
		}
		this.num -= num;
		adjustNum();
	}

	public abstract byte getType();
	
	public abstract String primaryKey();
	
	public abstract void deserialize(String str);
	
	public abstract String serialize();
	
	public void sendClient(ParametersEntity param){
		param.put(id);//数据库Id long
		param.put(key);//物品Id String
		param.put(num);//物品数量 long
		param.put(getType());//物品-0，装备-1，other-2
		_sendClient(param);
	}
	
	public abstract void _sendClient(ParametersEntity param);
}
