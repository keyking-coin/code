package com.joymeng.slg.domain.object.task.data;

import java.util.List;

import com.joymeng.slg.domain.data.DataManager.DataKey;

public class Honorwall implements DataKey {
	String id;
	List<String> honorName;
	List<String> honorLink;
	List<String> rewardNum;
	List<String> stagetype;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public List<String> getHonorName() {
		return honorName;
	}

	public void setHonorName(List<String> honorName) {
		this.honorName = honorName;
	}

	public List<String> getHonorLink() {
		return honorLink;
	}

	public void setHonorLink(List<String> honorLink) {
		this.honorLink = honorLink;
	}

	public List<String> getRewardNum() {
		return rewardNum;
	}

	public void setRewardNum(List<String> rewardNum) {
		this.rewardNum = rewardNum;
	}
   
	public List<String> getStagetype() {
		return stagetype;
	}

	public void setStagetype(List<String> stagetype) {
		this.stagetype = stagetype;
	}

	@Override
	public Object key() {
		return id;
	}
	

}
