package com.keyking.coin.service.http.data;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.user.Account;
import com.keyking.coin.service.domain.user.Credit;

public class HttpLookData {
	String face = "face1";
	String nikeName="";//昵称
	String signature;
	String title = "普通营销员";//称号
	String registTime = "";//注册时间
	List<String> addresses = new ArrayList<String>();//地址
	String name = "保密";//姓名
	String tel = "保密";
	List<Account> banks = new ArrayList<Account>();//绑定银行账户
	Credit credit = new Credit();//信用度
	
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getNikeName() {
		return nikeName;
	}
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegistTime() {
		return registTime;
	}
	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}
	public List<String> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public List<Account> getBanks() {
		return banks;
	}
	public void setBanks(List<Account> banks) {
		this.banks = banks;
	}
	public Credit getCredit() {
		return credit;
	}
	public void setCredit(Credit credit) {
		this.credit = credit;
	}
}
