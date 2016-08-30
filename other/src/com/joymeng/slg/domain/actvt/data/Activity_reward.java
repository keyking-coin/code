package com.joymeng.slg.domain.actvt.data;

import java.util.List;

import com.joymeng.slg.domain.actvt.DTManager.DataKey;

public class Activity_reward implements DataKey 
{
	String id;
	String rID;
	String type;
	String sID;
	int num;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getrID() {
		return rID;
	}

	public void setrID(String rID) {
		this.rID = rID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getsID() {
		return sID;
	}

	public void setsID(String sID) {
		this.sID = sID;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public Object key() {
		return id;
	}
	
	@Override
	public String toString() {
		return type + "#" + sID + "#" + num;
	}
	
	public static String toString(List<Activity_reward> rewardList) 
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rewardList.size(); i++)
		{
			sb.append(rewardList.get(i).toString());
			if (i != rewardList.size()-1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}
