package com.keyking.coin.service.net.data;

import java.util.List;

import com.keyking.coin.service.domain.user.Account;
import com.keyking.coin.service.domain.user.Credit;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.StringUtil;

public class LoginData implements Instances{
	long userId;//用户编号
	String tel;//电话
	String face = "null";//头像名称
	String nikeName;//昵称
	String title;//称号
	String registTime;//注册时间
	List<String> w_accouts;//文交所账号列表，格式是 :"华夏　1018300098"
	String name;//姓名
	String identity;//身份验证
	byte push = 1;//推送设置
	String signature;//签名
	List<Account> b_accounts;//银行账户列表
	byte breach;//违约次数
	String other = "";//备注信息
	MyselfNum mn = new MyselfNum();//和有有关的数据
	Credit credit;//信用度
	int dealCount;
	float money;//邮游币
	
	public LoginData (UserCharacter user){
		userId = user.getId();
		tel  = user.getAccount();
		if (!StringUtil.isNull(user.getFace())){
			face = "http://www.521uu.cc:321/uploads/" + user.getFace();
		}
		nikeName = user.getNikeName();
		title = user.getTitle();
		registTime = user.getRegistTime();
		w_accouts = user.getAddresses();
		name = user.getName();
		identity = user.getIdentity();
		push = user.getPush();
		signature = user.getSignature();
		b_accounts = user.getBankAccount().getAccounts();
		breach = user.getBreach();
		other = user.getOther();
		credit = user.getCredit();
		mn.setEmailNum(user.getNewEmailNum());
		mn.setFriendNum(user.getFriends().size());
		dealCount = CTRL.computeOkOrderNum(userId);
		money = user.getRecharge().getCurMoney();
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
	
	public List<String> getW_accouts() {
		return w_accouts;
	}

	public void setW_accouts(List<String> w_accouts) {
		this.w_accouts = w_accouts;
	}

	public List<Account> getB_accounts() {
		return b_accounts;
	}

	public void setB_accounts(List<Account> b_accounts) {
		this.b_accounts = b_accounts;
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

	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public int getDealCount() {
		return dealCount;
	}

	public void setDealCount(int dealCount) {
		this.dealCount = dealCount;
	}

	public float getMoney() {
		return money;
	}

	public void setMoney(float money) {
		this.money = money;
	}
}
