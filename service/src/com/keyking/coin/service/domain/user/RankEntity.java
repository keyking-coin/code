package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class RankEntity implements Comparable<RankEntity> , Instances , SerializeEntity{
	long uid;
	String name;
	String face;
	int count;
	float worth;//价值
	
	public RankEntity(){
		
	}
	
	public RankEntity(long uid){
		this.uid = uid;
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void addCount(int count) {
		this.count += count;
	}
	
	public float getWorth() {
		return worth;
	}

	public void setWorth(float worth) {
		this.worth = worth;
	}

	public void addWorth(float worth) {
		this.worth += worth;
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
		buffer.putInt(count);
		buffer.putUTF(String.valueOf(worth));
	}

	@Override
	public int compareTo(RankEntity arg0) {
		return worth > arg0.worth ? -1 : 1;
	}
}
