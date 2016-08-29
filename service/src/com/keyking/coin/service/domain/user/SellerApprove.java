package com.keyking.coin.service.domain.user;

public class SellerApprove {
	String name;
	long uid;
	Seller seller;
	String account;
	
	public SellerApprove(UserCharacter user){
		uid    = user.getId();
		account = user.getAccount();
		name   = user.getNikeName();
		seller = user.getSeller();
	}
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public Seller getSeller() {
		return seller;
	}
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
}
