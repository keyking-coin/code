package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class TransformRevertData implements Instances,SerializeEntity{
	long id;//用户回复编号
	long dealId;//关联交易编号
	long uid;//回复人编号
	String sayerName;//回复人名称
	String sayerIcon;//回复人头像
	String target;//表示回复目标的名称
	String context;//内容
	String createTime;//回复时间

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getDealId() {
		return dealId;
	}

	public void setDealId(long dealId) {
		this.dealId = dealId;
	}

	public long getUid() {
		return uid;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
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
	
	public String getSayerName() {
		return sayerName;
	}

	public void setSayerName(String sayerName) {
		this.sayerName = sayerName;
	}

	public String getSayerIcon() {
		return sayerIcon;
	}

	public void setSayerIcon(String sayerIcon) {
		this.sayerIcon = sayerIcon;
	}

	public void copy(Revert revert){
		id                 = revert.getId();
		dealId             = revert.getDependentId();
		uid                = revert.getUid();
		UserCharacter user = CTRL.search(uid);
		sayerName          = user.getNikeName();
		sayerIcon          = user.getAppFace();
		user               = CTRL.search(revert.getTar());
		target             = user.getNikeName();
		context            = revert.getContext();
		createTime         = revert.getCreateTime();
	}

	@Override
	public void serialize(DataBuffer out) {
		
	}
}
