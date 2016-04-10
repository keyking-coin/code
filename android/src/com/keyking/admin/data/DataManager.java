package com.keyking.admin.data;

import java.util.List;

import com.keyking.admin.data.deal.Deal;
import com.keyking.admin.data.user.UserData;

public class DataManager {
	
	private static DataManager instance = new DataManager();
	UserData user;
	List<Deal> deals;
	List<UserData> sellers;
	
	public static DataManager getInstance(){
		return instance;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public List<Deal> getDeals() {
		return deals;
	}

	public void setDeals(List<Deal> deals) {
		this.deals = deals;
	}

	public List<UserData> getSellers() {
		return sellers;
	}

	public void setSellers(List<UserData> sellers) {
		this.sellers = sellers;
	}
}
