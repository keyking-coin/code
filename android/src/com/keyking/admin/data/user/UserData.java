package com.keyking.admin.data.user;

import java.util.ArrayList;
import java.util.List;

public class UserData {
	long id;
	String account;
	String face = "face1";
	String nikeName= "";//�ǳ�
	String title = "��ͨӪ��Ա";//�ƺ�
	String registTime;
	List<String> addresses = new ArrayList<String>();//��ַ
	String name = "";//����
	int age = 18 ;//����
	String identity = "";//�����֤
	byte push = 1;//��������
	String signature = "��Һ�";//ǩ��
	List<Account> banks = new ArrayList<Account>();//�������˻�
	Credit credit = new Credit();//���ö�
	byte breach;//ΥԼ����
	UserPermission perission = new UserPermission();//�û�Ȩ��
	Seller seller;
	Recharge recharge;
	int completeDealNum;
	Forbid forbid;//���
	
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
	public UserPermission getPerission() {
		return perission;
	}
	public void setPerission(UserPermission perission) {
		this.perission = perission;
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
	public Forbid getForbid() {
		return forbid;
	}
	public void setForbid(Forbid forbid) {
		this.forbid = forbid;
	}
	
	public void copy(UserData target) {
		id = target.id;
		account = target.account;
		face = target.face;
		nikeName = new String(target.nikeName);
		title = new String(target.title);
		registTime = target.registTime;
		addresses = target.addresses;
		name = target.name;
		age = target.age;
		identity = target.identity;
		push = target.push;
		signature = target.signature;
		banks = target.banks;
		credit = target.credit;
		breach = target.breach;
		perission.copy(target.perission);
		seller = target.seller;
		recharge = target.recharge;
		completeDealNum = target.completeDealNum;
		forbid = target.forbid;
	}
}
