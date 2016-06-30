package com.keyking.coin.service.domain.user;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class RankEntity implements Comparable<RankEntity> , Instances , SerializeEntity{
	
	long uid;
	String name;//昵称
	String face;//头像名称
	String title;//头衔
	int num;//额度/数量/好评
	

	public RankEntity(long uid){
		UserCharacter user = CTRL.search(uid);
		init(user);
	}
	
	public RankEntity(UserCharacter user){
		init(user);
	}
	
	public void init(UserCharacter user){
		uid   = user.getId();
		name  = user.getNikeName();
		face  = user.getFace();
		title = user.getTitle();
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
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

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void addNum(int num) {
		this.num += num;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	public int compareTo(RankEntity re) {
		return re.num - num;
	}
}
