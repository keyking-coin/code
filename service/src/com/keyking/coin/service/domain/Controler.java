package com.keyking.coin.service.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.core.session.IoSession;
import org.joda.time.DateTime;

import com.keyking.coin.service.domain.broker.Broker;
import com.keyking.coin.service.domain.broker.UserBroker;
import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.SimpleOrderModule;
import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.email.EmailModule;
import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.data.RecentDeal;
import com.keyking.coin.service.net.resp.RespEntity;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.service.tranform.TransformDealData;
import com.keyking.coin.service.tranform.TransformTouristOrder;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class Controler implements Instances{
	
	private static Controler instance = new Controler();
	Map<String,UserCharacter> characters = new ConcurrentHashMap<String,UserCharacter>();
	List<Deal> deals = new CopyOnWriteArrayList<Deal>();
	List<Broker> brokers = new CopyOnWriteArrayList<Broker>();
	List<UserBroker> userBrokers = new CopyOnWriteArrayList<UserBroker>();
	
	public static Controler getInstance() {
		return instance;
	}
	
	public void load(){
		ServerLog.info("load all users");
		List<UserCharacter> users = DB.getUserDao().loadAll();
		if (users != null){
			for (UserCharacter user : users){
				user.load();
				characters.put(user.getAccount(),user);
			}
		}
		ServerLog.info("load all deals");
		List<Deal> ds = DB.getDealDao().loadAll();
		if (ds != null){
			for (Deal deal : ds){
				deal.read();
				if (deal.couldInsert()){
					deals.add(deal);
				}
			}
		}
		ServerLog.info("load all brokers");
		List<Broker> bs = DB.getBrokerDao().loadAll();
		if (bs != null){
			for (Broker broker : bs){
				brokers.add(broker);
			}
		}
		ServerLog.info("load all userBrokers");
		List<UserBroker> ubs = DB.getUserBrokerDao().loadAll();
		if (ubs != null){
			for (UserBroker ub : ubs){
				userBrokers.add(ub);
			}
		}
	}
	
	public UserCharacter adminLogin(String account,String pwd,AdminResp resp){
		UserCharacter user = search(account);
		if (user == null){//不存在账号是account
			resp.setError("账号:" + account + "不存在");
		}else{
			if (user.getPwd().equals(pwd)){
				TransformUserData tud = new TransformUserData(user);
				resp.addKey("user",tud);
				resp.setSucces();
				return user;
			}else{//密码错误
				resp.setError("密码错误");
			}
		}
		return null;
	}
	
	public UserCharacter login(String account,String pwd,GeneralResp resp){
		UserCharacter user = search(account);
		if (user == null){//不存在账号是account
			resp.setError("账号:" + account + "不存在");
		}else{
			if (user.getPwd().equals(pwd)){
				//TransformUserData tud = new TransformUserData(user);
				resp.add(user);
				resp.setSucces();
				return user;
			}else{//密码错误
				resp.setError("密码错误");
			}
		}
		return null;
	}
	
	public UserCharacter search(String accout){
		UserCharacter user = characters.get(accout);
		if (user == null){
			user = DB.getUserDao().search(accout);
			if (user != null){
				user.load();
				characters.put(accout,user);
			}
		}
		return user;
	}
	
	public UserCharacter searchByAccountOrNickName(String value){
		for (UserCharacter user : characters.values()){
			if (user.getAccount().equals(value)){
				return user;
			}
			if (user.getNikeName().equals(value)){
				return user;
			}
		}
		UserCharacter user = DB.getUserDao().search(value);
		if (user != null){
			return user;
		}
		user = DB.getUserDao().checkNikeName(value);
		if (user != null){
			return user;
		}
		return null;
	}
	
	public String checkHttpAccout(String account,String nickName){
		for (UserCharacter user : characters.values()){
			if (account != null){
				if (user.getAccount().equals(account)){
					return account + "已被注册";
				}else if(account.length() < 11){
					return account + "少于11位";
				}
			}
			if (nickName != null && user.getNikeName().equals(nickName)){
				return nickName + "已被使用";
			}
		}
		if (account != null){
			if (DB.getUserDao().search(account) != null){
				return account + "已被注册";
			}else if(account.length() < 11){
				return account + "少于11位";
			}
		}
		if (nickName != null && DB.getUserDao().checkNikeName(nickName) != null){
			return nickName + "已被使用";
		}
		return null;
	}
	
	public boolean checkAccout(String account,String nickName,RespEntity resp){
		for (UserCharacter user : characters.values()){
			if (account != null && user.getAccount().equals(account)){
				if (resp != null){
					resp.setError(account + "已被注册");
				}
				return false;
			}
			if (nickName != null && user.getNikeName().equals(nickName)){
				if (resp != null){
					resp.setError(nickName + "已被使用");
				}
				return false;
			}
		}
		if (account != null && DB.getUserDao().search(account) != null){
			if (resp != null){
				resp.setError(account + "已被注册");
			}
			return false;
		}
		if (nickName != null && DB.getUserDao().checkNikeName(nickName) != null){
			if (resp != null){
				resp.setError(nickName + "已被使用");
			}
			return false;
		}
		return true;
	}
	
	public UserCharacter search(long id){
		for (UserCharacter user : characters.values()){
			if (user.getId() == id){
				return user;
			}
		}
		UserCharacter user = DB.getUserDao().search(id);
		if (user != null){
			user.load();
			characters.put(user.getAccount(),user);
		}
		return user;
	}
	
	public synchronized boolean register(UserCharacter user){
		long id = PK.key("users");
		user.setId(id);
		characters.put(user.getAccount(),user);
		return true;
	}
	
	
	public void haveUserOutNet(IoSession session){
		for (UserCharacter user : characters.values()){
			String addressKey = user.getSessionAddress();
			if (addressKey != null && addressKey.equals(session.getRemoteAddress().toString())){
				user.setSessionAddress(null);
				ServerLog.info(user.getAccount() + " closed connect");
				break;
			}
		}
	}
	
	public List<Deal> getSearchDeals(SearchCondition condition) {
		List<Deal> result = new ArrayList<Deal>();
		List<Deal> issues    = new ArrayList<Deal>();
		List<Deal> valides   = new ArrayList<Deal>();
		List<Deal> normals   = new ArrayList<Deal>();
		List<Deal> tails     = new ArrayList<Deal>();
		for (Deal deal : deals){
			if (condition.legal(deal)){
				if (deal.getLeftNum() == 0){
					tails.add(deal);
					continue;
				}
				if (deal.isIssueRecently()){
					issues.add(deal);
				}else if (deal.checkValidTime()){
					valides.add(deal);
				}else{
					normals.add(deal);
				}
			}
		}
		if (issues.size() > 0){
			Collections.sort(issues,new Comparator<Deal>(){
				@Override
				public int compare(Deal o1, Deal o2) {
					DateTime time1 = TimeUtils.getTime(o1.getLastIssue());
					DateTime time2 = TimeUtils.getTime(o2.getLastIssue());
					if (time1.isBefore(time2)){
						return 1;
					}else{
						return -1;
					}
				}
			});
			result.addAll(issues);
		}
		if (valides.size() > 0){
			Collections.sort(valides);
			result.addAll(valides);
		}
		if (normals.size() > 0){
			Collections.sort(normals);
			result.addAll(normals);
		}
		if (tails.size() > 0){
			Collections.sort(tails);
			result.addAll(tails);
		}
		return result;
	}
	
	public <T> int compute(List<T> src , List<T> dst , int page , int num){
		if (num <= 0){
			return 0;
		}
		int start = (page -1) * num;
		int end   = start + num;
		int count = 0;
		for (int i = start ; i < src.size() ; i++){
			T info = src.get(i);
			if (i < end){
				dst.add(info);
			}else{
				count ++;
			}
		}
		int left = count / num;
		return count % num == 0 ? left : left + 1;
	}
	
	public List<Deal> getWeekDeals() {
		DateTime now = TimeUtils.now();
		int hour     = now.getHourOfDay();
		int minue    = now.getMinuteOfHour();
		int second   = now.getSecondOfMinute();
		int off = hour * 3600 + minue * 60 + second;
		long start = now.getMillis() - (1 * 24 * 3600 + off) * 1000;
		List<Deal> result = new ArrayList<Deal>();
		for (Deal deal : deals){
			long dealTime = TimeUtils.getTime(deal.getCreateTime()).getMillis();
			if (dealTime >= start){
				result.add(deal);
			}
		}
		return result;
	}
	
	public Deal tryToSearch(long id) {
		for (Deal deal : deals){
			if (deal.getId() == id){
				return deal;
			}
		}
		return DB.getDealDao().search(id);
	}
	
	public TransformDealData tryToSearchOrder(long id) {
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				if (order.getId() == id){
					TransformDealData tdd = new TransformDealData();
					tdd.copy(deal,order);
					return tdd;
				}
			}
		}
		return null;
	}
	
	public synchronized boolean tryToInsert(Deal deal) {
		deals.add(0,deal);
		return true;
	}

	public List<Deal> tryToSearchDeals(long uid){
		List<Deal> result = new ArrayList<Deal>();
		for (Deal deal : deals){//未撤销的
			if (deal.getUid() == uid || deal.checkBuyerId(uid)){//是卖家或者有购买
				result.add(deal);
			}
		}
		return result;
	}
	
	public List<RankEntity> rankDeal(int type){
		List<RankEntity> result = new ArrayList<RankEntity>();
		List<RankEntity> temp = new ArrayList<RankEntity>();
		if (type <= 1){
			for (Deal deal : deals){//未撤销的
				Map<Long,RankEntity> map = deal.compute(type);
				for (RankEntity re : map.values()){
					RankEntity target = null;
					for (RankEntity entity : temp){
						if (entity.equals(re)){
							target = entity;
						}
					}
					if (target != null){
						target.addNum(re.getNum());
					}else{
						temp.add(re);
					}
				}
			}
		}else if (type == 2){
			for (UserCharacter user : characters.values()){//未撤销的
				if (user.getPermission().coin_user()){
					int num = user.getCredit().getHp();
					if (num > 0){
						RankEntity re = new RankEntity(user);
						re.addNum(num);
						temp.add(re);
					}
				}
			}
		}
		Collections.sort(temp);
		int max = Math.min(100,temp.size());
		for (int i = 0 ; i < max ; i++){
			RankEntity entity = temp.get(i);
			result.add(entity);
		}
		return result;
	}

	public List<TransformTouristOrder> trySearchHttpRecentOrder(){
		List<TransformTouristOrder> orders = new ArrayList<TransformTouristOrder>();
		DateTime time = TimeUtils.now();
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				if (order.checkRevoke()){
					continue;
				}
				DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
				if (TimeUtils.isSameDay(time,otime)){
					orders.add(new TransformTouristOrder(deal,order));
				}
			}
		}
		int count = 1;
		while (orders.size() < 20 && count < 8){
			long pre = time.getMillis() - count * 24 * 3600 * 1000;
			time = TimeUtils.getTime(pre);
			for (Deal deal : deals){
				for (DealOrder order : deal.getOrders()){
					if (order.checkRevoke()){
						continue;
					}
					DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
					if (TimeUtils.isSameDay(time,otime)){
						orders.add(new TransformTouristOrder(deal,order));
					}
				}
			}
			count ++;
			if (orders.size() >= 20){
				break;
			}
		}
		Collections.sort(orders);
		if (count > 1){
			count = 0;
			Iterator<TransformTouristOrder> iter = orders.iterator();
			while (iter.hasNext()){
				iter.next();
				if (count >= 20){
					iter.remove();
				}
				count++;
			}
		}
		return orders;
	}
	
	
	public List<UserCharacter> searchFuzzyUser(String key) {
		List<UserCharacter> result = new ArrayList<UserCharacter>();
		for (UserCharacter user : characters.values()){
			if (user.getAccount().contains(key) ||
			    user.getNikeName().contains(key) ||
			    user.getName().contains(key)){
				result.add(user);
			}
		}
		DB.getUserDao().searchFuzzy(key,result);
		return result;
	}

	public List<TransformUserData> getSearchRZ() {
		List<TransformUserData> result = new ArrayList<TransformUserData>();
		for (UserCharacter user : characters.values()){
			Seller seller = user.getSeller();
			if (seller != null && !seller.isPass()){
				TransformUserData tud = new TransformUserData(user);
				result.add(tud);
			}
		}
		return result;
	}
	
	public List<UserCharacter> getUsers() {
		List<UserCharacter> result = new ArrayList<UserCharacter>();
		for (UserCharacter user : characters.values()){
			if (!user.getPermission().admin()){
				result.add(user);
			}
		}
		return result;
	}
	
	public List<TransformUserData> getCoinUsers() {
		List<TransformUserData> result = new ArrayList<TransformUserData>();
		for (UserCharacter user : characters.values()){
			if (user.getPermission().buyer() || user.getPermission().seller()){
				TransformUserData tud = new TransformUserData(user);
				result.add(tud);
			}
		}
		return result;
	}
	
	public int computeOkOrderNum(long id) {
		int count = 0 ;
		for (Deal deal : deals){
			if (deal.getUid() == id){
				for (DealOrder order : deal.getOrders()){
					if (order.over()){
						count++;
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == id && order.over()){
						count++;
					}
				}
			}
		}
		return count;
	}

	public List<Deal> getDeals() {
		return deals;
	}
	
	public List<RecentDeal> getRecentOrders() {
		List<RecentDeal> result = new ArrayList<RecentDeal>();
		DateTime time = TimeUtils.now();
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
				if (TimeUtils.isSameDay(time,otime)){
					RecentDeal rd = new RecentDeal(deal,order);
					result.add(rd);
				}
			}
		}
		if (result.size() < 20){
			long pre = time.getMillis() - 24 * 3600 * 1000;
			time = TimeUtils.getTime(pre);
			for (Deal deal : deals){
				for (DealOrder order : deal.getOrders()){
					DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
					if (TimeUtils.isSameDay(time,otime)){
						RecentDeal rd = new RecentDeal(deal,order);
						result.add(rd);
					}
					if (result.size() >= 20){
						break;
					}
				}
				if (result.size() >= 20){
					break;
				}
			}
		}
		return result;
	}
	
	public List<SimpleOrderModule> trySearchRecentOrder() {
		List<SimpleOrderModule> result = new ArrayList<SimpleOrderModule>();
		DateTime time = TimeUtils.now();
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
				if (TimeUtils.isSameDay(time,otime)){
					SimpleOrderModule module = new SimpleOrderModule();
					order.simpleDes(module);
					result.add(module);
				}
			}
		}
		if (result.size() < 20){
			long pre = time.getMillis() - 24 * 3600 * 1000;
			time = TimeUtils.getTime(pre);
			for (Deal deal : deals){
				for (DealOrder order : deal.getOrders()){
					DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
					if (TimeUtils.isSameDay(time,otime)){
						SimpleOrderModule module = new SimpleOrderModule();
						order.simpleDes(module);
						result.add(module);
					}
					if (result.size() >= 20){
						break;
					}
				}
				if (result.size() >= 20){
					break;
				}
			}
		}
		return result;
	}

	public String insertUser(String num,String nikeName) {
		String result = CTRL.checkHttpAccout(num,nikeName);
		if (result == null){
			UserCharacter user = new UserCharacter();
			user.setAccount(num);
			user.setPwd("123456789");
			user.setNikeName(nikeName);
			user.setRegistTime(TimeUtils.nowChStr());
			CTRL.register(user);
			return "ok";
		}
		return result;
	}
	
	public boolean tryToSendEmailToUser(long sendId,String time,String theme,String content,UserCharacter user){
		if (user == null){
			return false;
		}
		synchronized (user) {
			Email email = new Email();
			email.setSenderId(sendId);
			email.setUserId(user.getId());
			email.setTime(time);
			email.setTheme(theme);
			email.setContent(content);
			long id = PK.key("email");
			email.setId(id);
			user.addEmail(email);
			email.save();
			EmailModule module = new EmailModule();
			module.add("num",user.getNewEmailNum());
			ModuleResp modules = new ModuleResp();
			modules.addModule(module);
			NET.sendMessageToClent(modules,user);
			return true;
		}
	}

	public boolean checkBroker(String name) {
		for (Broker broker : brokers){
			if (broker.getName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	public synchronized void addBroker(Broker broker){
		if (!brokers.contains(broker)){
			brokers.add(broker);
		}
	}

	public List<UserBroker> searchUserBrokers(UserCharacter user) {
		List<UserBroker> result = new ArrayList<UserBroker>();
		for (UserBroker ub : userBrokers){
			if (ub.getBid() == user.getBroker()){
				result.add(ub);
			}
		}
		return result;
	}
	
	public List<Broker> searchBrokers(UserCharacter user) {
		List<Broker> result = new ArrayList<Broker>();
		for (UserBroker ub : userBrokers){
			if (ub.getUid() == user.getId()){
				result.add(searchBroker(ub.getBid()));
			}
		}
		return result;
	}
	
	public Broker searchBroker(long brokerId) {
		for (Broker broker : brokers){
			if (broker.getId() == brokerId){
				return broker;
			}
		}
		return null;
	}

	public boolean removeUser(String account) {
		UserCharacter user = characters.get(account);
		if (user != null){
			DB.getUserDao().delete("user",user.getId());
			characters.remove(account);
			return true;
		}
		return false;
	}
}
 
