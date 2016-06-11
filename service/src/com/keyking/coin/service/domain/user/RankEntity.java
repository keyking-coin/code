package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class RankEntity implements Comparable<RankEntity> , Instances , SerializeEntity{
	long uid;
	String name;//昵称
	String face;//头像名称
	int num;//额度/数量/好评
	
	public RankEntity(){
		
	}
	
	public RankEntity(long uid){
		this.uid = uid;
		UserCharacter user = CTRL.search(uid);
		name = user.getNikeName();
		face = user.getFace();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void addNum(int num) {
		this.num += num;
	}
	
	public boolean equals(RankEntity re) {
		return uid == re.uid;
	}

	@Override
	public void serialize(DataBuffer buffer) {
		buffer.putLong(uid);
		UserCharacter user = CTRL.search(uid);
		buffer.putUTF(user.getNikeName());
		buffer.putUTF(user.getFace());
		buffer.putInt(num);
		buffer.putUTF(String.valueOf(num));
	}

	@Override
	public int compareTo(RankEntity arg0) {
		return num > arg0.num ? -1 : 1;
	}
}
