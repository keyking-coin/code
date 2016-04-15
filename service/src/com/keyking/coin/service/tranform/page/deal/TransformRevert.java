package com.keyking.coin.service.tranform.page.deal;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class TransformRevert implements Instances , Comparable<TransformRevert>{
	long   sayerId;//回复人编号
	String sayerName;//回复人名称
	String time;//回复时间
	long   sayToId;//表示回复目标的比编号
	String sayToName;//表示回复目标的名称
	String context;//内容
	
	public void copy(Revert revert){
		sayerId            = revert.getUid();
		UserCharacter user = CTRL.search(sayerId);
		sayerName          = user.getNikeName();
		sayToId            = revert.getTar();
		user               = CTRL.search(revert.getTar());
		sayToName          = user.getNikeName();
		context            = revert.getContext();
		time               = revert.getCreateTime();
	}
	
	public long getSayerId() {
		return sayerId;
	}


	public void setSayerId(long sayerId) {
		this.sayerId = sayerId;
	}


	public String getSayerName() {
		return sayerName;
	}


	public void setSayerName(String sayerName) {
		this.sayerName = sayerName;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}

	public long getSayToId() {
		return sayToId;
	}

	public void setSayToId(long sayToId) {
		this.sayToId = sayToId;
	}

	public String getSayToName() {
		return sayToName;
	}

	public void setSayToName(String sayToName) {
		this.sayToName = sayToName;
	}

	public String getContext() {
		return context;
	}


	public void setContext(String context) {
		this.context = context;
	}


	@Override
	public int compareTo(TransformRevert o) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(o.time);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}

}
