package com.keyking.coin.service.tranform;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.user.Account;
import com.keyking.coin.service.domain.user.Credit;
import com.keyking.coin.service.domain.user.Forbid;
import com.keyking.coin.service.domain.user.Recharge;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.domain.user.UserPermission;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;

public class TransformUserData implements Instances,SerializeEntity{
	long id;
	String account;
	String face = "face1";
	String nikeName= "";//昵称
	String title = "普通营销员";//称号
	String registTime;
	List<String> addresses = new ArrayList<String>();//地址
	String name = "";//姓名
	int age = 18 ;//年龄
	String identity = "";//身份验证
	byte push = 1;//推送设置
	String signature = "大家好";//签名
	List<Account> banks = new ArrayList<Account>();//绑定银行账户
	Credit credit = new Credit();//信用度
	byte breach;//违约次数
	UserPermission perission;//用户权限
	Seller seller;
	Recharge recharge;
	int completeDealNum;
	Forbid forbid;//封号
	
	public TransformUserData(UserCharacter user){
		id = user.getId();
		account = user.getAccount();
		face = user.getFace();
		nikeName = user.getNikeName();
		title = user.getTitle();
		registTime  = user.getRegistTime();
		addresses.addAll(user.getAddresses());
		name = user.getName();
		age = user.getAge();
		identity = user.getIdentity();
		push = user.getPush();
		signature = user.getSignature();
		banks.addAll(user.getBankAccount().getAccounts());
		credit = user.getCredit();
		breach = user.getBreach();
		seller = user.getSeller();
		perission = user.getPermission();
		recharge = user.getRecharge();
		forbid = user.getForbid();
		completeDealNum = CTRL.computeOkOrderNum(id);
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

	public Forbid getForbid() {
		return forbid;
	}

	public void setForbid(Forbid forbid) {
		this.forbid = forbid;
	}

	public String getRegistTime() {
		return registTime;
	}

	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Recharge getRecharge() {
		return recharge;
	}

	public void setRecharge(Recharge recharge) {
		this.recharge = recharge;
	}

	public int getCompleteDealNum() {
		return completeDealNum;
	}

	public void setCompleteDealNum(int completeDealNum) {
		this.completeDealNum = completeDealNum;
	}

	public UserPermission getPerission() {
		return perission;
	}

	public void setPerission(UserPermission perission) {
		this.perission = perission;
	}

	@Override
	public void serialize(DataBuffer out) {
		// TODO Auto-generated method stub
		
	}
	
}
