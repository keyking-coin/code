package com.keyking.coin.service.http.data;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.user.Account;
import com.keyking.coin.service.domain.user.Credit;
import com.keyking.coin.service.domain.user.UserCharacter;

public class HttpUserCharacter {
	long id;
	String account;
	String face = "face1";
	String nikeName="";//昵称
	String title = "普通营销员";//称号
	List<String> addresses = new ArrayList<String>();//地址
	String name ="";//姓名
	int age = 18;//年龄
	String identity = "";//身份验证
	byte push = 1;//推送设置
	String signature = "大家好";//签名
	List<Account> banks = new ArrayList<Account>();//绑定银行账户
	Credit credit = new Credit();//信用度
	List<Long> favorites = new ArrayList<Long>();//收藏夹
	byte breach;//违约次数
	
	public HttpUserCharacter(UserCharacter user){
		id = user.getId();
		account = user.getAccount();
		face = user.getFace();
		nikeName = user.getNikeName();
		title = user.getTitle();
		addresses.addAll(user.getAddresses());
		name = user.getName();
		age = user.getAge();
		identity = user.getIdentity();
		push = user.getPush();
		signature = user.getSignature();
		banks.addAll(user.getBankAccount().getAccounts());
		credit.copy(user.getCredit());
		breach = user.getBreach();
		favorites.addAll(user.getFavorites());
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getAccount() {
		return account;
	}
	
	public void setAccount(String account) {
		this.account = account;
	}
	
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
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public byte getPush() {
		return push;
	}
	
	public void setPush(byte push) {
		this.push = push;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public void setSignature(String signature) {
		this.signature = signature;
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
	
	public byte getBreach() {
		return breach;
	}
	
	public void setBreach(byte breach) {
		this.breach = breach;
	}
}
