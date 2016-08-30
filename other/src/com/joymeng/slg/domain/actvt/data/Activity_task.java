package com.joymeng.slg.domain.actvt.data;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_task implements DataKey 
{
	String id;
	String tID;
	String activity;
	String type;
	String destID;
	int number;
	String content;
	int isShow;
	int rank;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String gettID() {
		return tID;
	}

	public void settID(String tID) {
		this.tID = tID;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDestID() {
		return destID;
	}

	public void setDestID(String destID) {
		this.destID = destID;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getTaskID() {
		if (destID.isEmpty()) {
			return type;
		}
		return type+"_"+destID;
	}

	@Override
	public Object key() {
		return id;
	}
}
