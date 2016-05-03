package com.keyking.coin.service.net.data;

import java.util.List;

import com.keyking.coin.service.domain.user.Account;
import com.keyking.coin.service.domain.user.UserCharacter;

public class LoginData {
	long userId;//用户编号
	String tel;//电话
	String face;//头像名称
	String nikeName;//昵称
	String title;//称号
	String registTime;//注册时间
	List<String> addresses;//地址
	String name;//姓名
	String identity;//身份验证
	byte push = 1;//推送设置
	String signature;//签名
	List<Account> accounts;//绑定银行账户
	AppraiseRecord ar = new AppraiseRecord();//评价次数记录
	byte breach;//违约次数
	String other = "";//备注信息
	MyselfNum mn = new MyselfNum();//和有有关的数据
	
	public LoginData (UserCharacter user){
		userId = user.getId();
		tel  = user.getAccount();
		face = user.getFace();
		nikeName = user.getNikeName();
		title = user.getTitle();
		registTime = user.getRegistTime();
		addresses = user.getAddresses();
		name = user.getName();
		identity = user.getIdentity();
		push = user.getPush();
		signature = user.getSignature();
		accounts = user.getBankAccount().getAccounts();
		ar.setGood(user.getCredit().getHp());
		ar.setNormal(user.getCredit().getZp());
		ar.setBad(user.getCredit().getCp());
		breach = user.getBreach();
		other = user.getOther();
		mn.setEmailNum(user.getNewEmailNum());
		mn.setFriendNum(user.getFriends().size());
	}
		
	public LoginData (){
		
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
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
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	public AppraiseRecord getAr() {
		return ar;
	}
	public void setAr(AppraiseRecord ar) {
		this.ar = ar;
	}
	public byte getBreach() {
		return breach;
	}
	public void setBreach(byte breach) {
		this.breach = breach;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public MyselfNum getMn() {
		return mn;
	}
	public void setMn(MyselfNum mn) {
		this.mn = mn;
	}
}
