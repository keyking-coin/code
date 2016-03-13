package com.keyking.coin.service.domain.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.keyking.coin.service.domain.data.EntitySaver;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.email.EmailModule;
import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.friend.Message;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;


public class UserCharacter extends EntitySaver{
	
	long id;
	
	String account;
	
	String pwd ="666666";
	
	String face = "face1";
	
	String nikeName="";//昵称
	
	String title = "普通营销员";//称号
	
	String registTime = "";//注册时间
	
	List<String> addresses = new ArrayList<String>();//地址
	
	String name ="";//姓名
	
	int age = 18;//年龄
	
	String sessionAddress;//回话
	
	String identity = "";//身份验证
	
	byte push = 1;//推送设置
	
	String signature = "大家好";//签名
	
	Recharge recharge = new Recharge();//充值系统
	
	BankAccount bankAccount = new BankAccount();//绑定银行账户
	
	Credit credit = new Credit();//信用度
	
	Seller seller = null;
	
	Forbid forbid = new Forbid();
	
	byte breach;//违约次数
	
	UserPermission permission = new UserPermission();
	
	List<Email> emails = new ArrayList<Email>();//邮件列表
	
	List<Email> delEmails = new ArrayList<Email>();//邮件列表
	
	List<Long> favorites = new ArrayList<Long>();//收藏夹
	
	List<Friend> friends = new ArrayList<Friend>();//好友列表
	
	List<Friend> delFriends = new ArrayList<Friend>();//需要删除的列表
	
	List<Message> messages = new ArrayList<Message>();//好友发我的消息
	
	List<Message> delMessages = new ArrayList<Message>();//需要删除聊天记录

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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public Credit getCredit() {
		return credit;
	}

	public void setCredit(Credit credit) {
		this.credit = credit;
	}

	public String getNikeName() {
		return nikeName;
	}

	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}

	public List<String> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<String> addresses) {
		this.addresses = addresses;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Recharge getRecharge() {
		return recharge;
	}

	public void setRecharge(Recharge recharge) {
		this.recharge = recharge;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getSessionAddress() {
		return sessionAddress;
	}

	public void setSessionAddress(String sessionAddress) {
		this.sessionAddress = sessionAddress;
	}

	
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Forbid getForbid() {
		return forbid;
	}

	public void setForbid(Forbid forbid) {
		this.forbid = forbid;
	}

	public byte getBreach() {
		return breach;
	}

	public void setBreach(byte breach) {
		this.breach = breach;
	}
	
	public List<Long> getFavorites() {
		return favorites;
	}

	
	public UserPermission getPermission() {
		return permission;
	}

	public void setPermission(UserPermission permission) {
		this.permission = permission;
	}

	public void deserializeFavorites(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		favorites = JsonUtil.JsonToObjectList(str,Long.class);
	}
	
	public String serializeFavorites(){
		if (favorites != null){
			return JsonUtil.ObjectToJsonString(favorites);
		}
		return null;
	}
	
	public void tick(){
		forbid.tick();
	}

	public void deserializeUser(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		seller = JsonUtil.JsonToObject(str,Seller.class);
	}
	
	public String serializeUser(){
		if (seller != null){
			return JsonUtil.ObjectToJsonString(seller);
		}
		return null;
	}
	
	public void load(){
		loadEmails();
		loadFriends();
	}
	
	public void loadEmails(){
		List<Email> temp = DB.getEmailDao().load(id);
		if (temp != null && temp.size() > 0){
			emails.addAll(temp);
			Collections.sort(emails);
		}
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.putUTF(account);
		buffer.putUTF(face);
		buffer.putUTF(nikeName);
		if (permission.isAdmin()){
			buffer.putInt(emails.size());
			for (Email email : emails){
				email.serialize(buffer);
			}
		}else{
			buffer.putUTF(name);
			buffer.putUTF(title);
			buffer.putUTF(registTime);
			buffer.putUTF(identity);
			buffer.putUTF(signature);
			buffer.put(push);
			buffer.putInt(addresses.size());
			for (String address : addresses){
				buffer.putUTF(address);
			}
			recharge._serialize(buffer);
			bankAccount.serialize(buffer);
			if (seller == null){
				buffer.put((byte)0);
			}else{
				buffer.put((byte)1);
				seller.serialize(buffer);
			}
			buffer.putInt(emails.size());
			for (Email email : emails){
				email.serialize(buffer);
			}
			buffer.putInt(friends.size());
			for (Friend friend : friends){
				friend.serialize(buffer);
			}
			buffer.putInt(messages.size());
			for (Message message : messages){
				message.serialize(buffer);
			}
			buffer.putInt(favorites.size());
			for (Long favorite : favorites){
				buffer.putLong(favorite.longValue());
			}
		}
	}

	public void save() {
		if (needSave){
			DB.getUserDao().save(this);
			needSave = false;
		}
		for (Email email : emails){
			email.save();
		}
		for (Email email : delEmails){
			email.delete();
		}
		for (Friend friend : friends){
			friend.save();
		}
		for (Friend friend : delFriends){
			friend.del();
		}
		for (Message message : messages){
			message.save();
		}
		for (Message message : delMessages){
			message.del();
		}
	}

	public void addEmail(Email email) {
		emails.add(email);
	}
	
	public Email searchEmail(long id){
		for (Email email : emails){
			if (email.getId() == id){
				return email;
			}
		}
		return null;
	}
	
	public boolean removeEmail(String ids) {
		String[] ss = ids.split(",");
		if (ss.length > 0){
			ModuleResp modules = new ModuleResp();
			for (String s : ss){
				long id = Long.parseLong(s);
				for (Email email : emails){
					if (email.getId() == id){
						EmailModule module = new EmailModule();
						module.add(email);
						module.setFlag(Module.DEL_FLAG);
						modules.addModule(module);
						emails.remove(email);
						delEmails.add(email);
						break;
					}
				}
			}
			NET.sendMessageToClent(modules,sessionAddress);
		}
		return true;
	}
	
	public void loadFriends(){
		List<Friend> lis = DB.getFriendDao().search(id);
		if (lis != null && lis.size() > 0){
			StringBuffer sb = new StringBuffer();
			for (Friend friend : lis){
				sb.append("\'[" + friend.getFid() + "," + id + "]\',\'[" + id + "," + friend.getFid() + "]\',");
				friends.add(friend);
			}
			sb.deleteCharAt(sb.length()-1);
			messages = DB.getMessageDao().search(sb.toString());
		}
	}
	
	/***
	 * 已用的信用
	 * @return
	 */
	public float computeUsedCredit(){
		List<Deal> deals = CTRL.tryToSearchDeals(id);
		float result = 0;
		long nowTime = TimeUtils.nowLong();
		for (Deal deal : deals){
			if (deal.isRevoke()){
				continue;
			}
			if (deal.getUid() == id){//卖家
				float a = deal.notCompleteDeposit();
				float b = deal.completeDeposit();
				long dealTime = TimeUtils.getTime(deal.getValidTime()).getMillis();
				if (dealTime < nowTime){//已过期
					result += a;
				}else{
					result += deal.getNeedDeposit() - b;
				}
			}else {//买家
				float a = deal.notCompleteDeposit(id);
				float b = deal.completeDeposit(id);
				long dealTime = TimeUtils.getTime(deal.getValidTime()).getMillis();
				if (dealTime < nowTime){//已过期
					result += a;
				}else{
					result += deal.getNeedDeposit() - b;
				}
			}
		}
		return result;
	}
	
	public float computeMaxCredit(){
		float result = credit.getMaxValue();
		if (seller != null && seller.isPass()){
			result += seller.getDeposit();
		}
		return result;
	}
	
	public float computeTempCredit(){
		float result = credit.getTempMaxValue();
		if (seller != null && seller.isPass()){
			result += seller.getDeposit();
		}
		return result;
	}
	
	public Friend removeFriend(UserCharacter user_friend){
		for (Friend friend : friends){
			if (friend.getFid() == user_friend.id){
				String str1 = "[" + id + "," + user_friend.id + "]";
				String str2 = "[" + user_friend.id + "," + id + "]";
				ModuleResp modules = new ModuleResp();
				for (int i = 0 ; i < messages.size() ;) {//删除聊天记录
					Message message = messages.get(i);
					if (message.getActors().equals(str1) || message.getActors().equals(str2)){
						delMessages.add(message);
						messages.remove(i);
						message.clientMessage(modules,Module.DEL_FLAG);
					}else{
						i++;
					}
				}
				friends.remove(friend);
				delFriends.add(friend);
				NET.sendMessageToClent(modules,this);
				return friend;
			}
		}
		return null;
	}
	
	public void applyFriend(long fid){
		Friend friend = new Friend();
		String createTime = TimeUtils.nowChStr();
		friend.setTime(createTime);
		friend.setFid(fid);
		friend.setUid(id);
		friends.add(friend);
		NET.sendMessageToClent(friend.clientMessage(Module.ADD_FLAG),sessionAddress);
	}
	
	public void addFriend(long fid){
		Friend friend = findFriend(fid);
		if (friend == null){
			friend = new Friend();
			friends.add(friend);
			String createTime = TimeUtils.nowChStr();
			friend.setTime(createTime);
		}
		friend.setFid(fid);
		friend.setUid(id);
		friend.setPass((byte)1);
		NET.sendMessageToClent(friend.clientMessage(Module.ADD_FLAG),sessionAddress);
	}
	
	public Friend findFriend(long fid){
		for (Friend friend : friends){
			if (friend.getFid() == fid){
				return friend;
			}
		}
		return null;
	}
	
	public List<Message> getFriendMessages(long fid){
		String str1 = "[" + id + "," + fid + "]";
		String str2 = "[" + fid + "," + id + "]";
		List<Message> temps = new ArrayList<Message>();
		for (Message me : messages){
			if (me.getActors().equals(str1) || me.getActors().equals(str2)){
				temps.add(me);
			}
		}
		Collections.sort(temps);
		return temps;
	}
	
	public void addMessage(Message message,long fid){
		messages.add(message);
		NET.sendMessageToClent(message.clientMessage(Module.ADD_FLAG),sessionAddress);
	}

	public void deserializeAddresses(String str) {
		if (StringUtil.isNull(str)){
			return;
		}
		addresses = JsonUtil.JsonToObjectList(str,String.class);
	}
	
	public String serializeAddresses(){
		if (addresses != null){
			return JsonUtil.ObjectToJsonString(addresses);
		}
		return "null";
	}

	public boolean addAddress(String address) {
		if (StringUtil.isNull(address) || addresses.contains(address)){
			return false;
		}
		addresses.add(address);
		return true;
	}
	
	public boolean removeAddress(String address) {
		if (!addresses.contains(address)){
			return false;
		}
		addresses.remove(address);
		return true;
	}
	
}
 
