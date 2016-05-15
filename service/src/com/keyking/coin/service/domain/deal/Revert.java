package com.keyking.coin.service.domain.deal;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;


public class Revert implements Instances , SerializeEntity ,Comparable<Revert>{
	
	long id;
	
	long dependentId;
	
	long uid;
	
	long tar;
	
	String context;
	
	String createTime;
	
	boolean revoke;//是否撤销了
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getDependentId() {
		return dependentId;
	}

	public void setDependentId(long dependentId) {
		this.dependentId = dependentId;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getTar() {
		return tar;
	}

	public void setTar(long tar) {
		this.tar = tar;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	
	public boolean isRevoke() {
		return revoke;
	}

	public void setRevoke(boolean revoke) {
		this.revoke = revoke;
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.putLong(dependentId);
		buffer.putLong(uid);
		UserCharacter user = CTRL.search(uid);
		buffer.putUTF(user.getNikeName());
		buffer.putUTF(user.getFace());
		buffer.putUTF(createTime);
		buffer.putUTF(context);
		if (tar > 0){
			user = CTRL.search(tar);
			buffer.putUTF(user.getNikeName());
		}else{
			buffer.putUTF("null");
		}
	}

	@Override
	public int compareTo(Revert o) {
		DateTime time1 = TimeUtils.getTime(createTime);
		DateTime time2 = TimeUtils.getTime(o.createTime);
		if (time1.isBefore(time2)){
			return -1;
		}else{
			return 1;
		}
	}

	public void save() {
		DB.getRevertDao().save(this);
	}
}
