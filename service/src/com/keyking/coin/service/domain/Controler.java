package com.keyking.coin.service.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.joda.time.DateTime;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.SimpleOrderModule;
import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.tranform.TransformTouristOrder;
import com.keyking.coin.service.tranform.TransformUserData;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class Controler implements Instances{
	
	private static Controler instance = new Controler();
	
	Map<String,UserCharacter> characters = new ConcurrentHashMap<String,UserCharacter>();
	
	List<Deal> deals = new ArrayList<Deal>();
	
	public static Controler getInstance() {
		return instance;
	}
	
	public void load(){
		ServerLog.info("load all deals");
		List<Deal> lis = DB.getDealDao().loadAll();
		for (Deal deal : lis){
			deal.read();
			deals.add(deal);
		}
		ServerLog.info("load all users");
		List<UserCharacter> users = DB.getUserDao().loadAll();
		for (UserCharacter user : users){
			characters.put(user.getAccount(),user);
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
			if (user.getAccount().equals(account)){
				return account + "已被注册";
			}
			if (user.getNikeName().equals(nickName)){
				return nickName + "已被使用";
			}
		}
		if (DB.getUserDao().search(account) != null){
			return account + "已被注册";
		}
		if (DB.getUserDao().checkNikeName(nickName) != null){
			return nickName + "已被使用";
		}
		return null;
	}
	
	public boolean checkAccout(String account,String nickName,GeneralResp resp){
		for (UserCharacter user : characters.values()){
			if (user.getAccount().equals(account)){
				if (resp != null){
					resp.setError(account + "已被注册");
				}
				return false;
			}
			if (user.getNikeName().equals(nickName)){
				if (resp != null){
					resp.setError(nickName + "已被使用");
				}
				return false;
			}
		}
		if (DB.getUserDao().search(account) != null){
			if (resp != null){
				resp.setError(account + "已被注册");
			}
			return false;
		}
		if (DB.getUserDao().checkNikeName(nickName) != null){
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
	
	public void tick() {
		for (UserCharacter user : characters.values()){
			user.tick();
		}
	}
	
	public void save(){
		for (UserCharacter user : characters.values()){
			user.save();
		}
		for (Deal deal : deals){
			deal.save();
		}
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
		for (Deal deal : deals){
			if (condition.legal(deal)){
				result.add(deal);
			}
		}
		return result;
	}
	
	public List<Deal> getWeekDeals() {
		List<Deal> result = new ArrayList<Deal>();
		DateTime time = TimeUtils.now();
		int year = time.getYear();
		int month = time.getMonthOfYear();
		int day = time.getDayOfMonth();
		int preDay = day - 7;
		int preMonth = month;
		int preYear = year;
		if (preDay < 1){
			preMonth --;
			if (preMonth <= 0){
				preMonth = 12;
				preYear --;
			}
			int maxDate = 0;
			if (month == 3){
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0){
					maxDate = 29;
				}else{
					maxDate = 28;
				}
			}else{
				Calendar cal = Calendar.getInstance();   
				cal.set(Calendar.YEAR,year);   
				cal.set(Calendar.MONTH,month);
				cal.set(Calendar.DAY_OF_MONTH,1);//把日期设置为当月第一天  
				cal.roll(Calendar.DAY_OF_MONTH,-1);//日期回滚一天，也就是最后一天  
			    maxDate = cal.get(Calendar.DAY_OF_MONTH);  
			}
			preDay += maxDate;
		}
		String start = preYear + "-" + preMonth + "-" + preDay + " 00:00:00";
		long startTime = TimeUtils.getTime(start).getMillis();
		for (Deal deal : deals){//先看有效期
			long dealTime = TimeUtils.getTime(deal.getValidTime()).getMillis();
			if (dealTime >= startTime){
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
	
	public List<RankEntity> rankDeal(){
		List<RankEntity> result = new ArrayList<RankEntity>();
		List<RankEntity> temp = new ArrayList<RankEntity>();
		for (Deal deal : deals){//未撤销的
			Map<Long,RankEntity> map = deal.compute();
			for (RankEntity re : map.values()){
				RankEntity target = null;
				for (RankEntity entity : temp){
					if (entity.getUid() == re.getUid()){
						target = entity;
					}
				}
				if (target != null){
					target.addCount(re.getCount());
				}else{
					temp.add(re);
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
				DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
				if (TimeUtils.isSameDay(time,otime)){
					orders.add(new TransformTouristOrder(deal,order));
				}
			}
		}
		if (orders.size() < 20){
			long pre = time.getMillis() - 24 * 3600 * 1000;
			time = TimeUtils.getTime(pre);
			for (Deal deal : deals){
				for (DealOrder order : deal.getOrders()){
					DateTime otime = TimeUtils.getTime(order.getTimes().get(0));
					if (TimeUtils.isSameDay(time,otime)){
						orders.add(new TransformTouristOrder(deal,order));
					}
					if (orders.size() >= 20){
						break;
					}
				}
				if (orders.size() >= 20){
					break;
				}
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
			if (seller!= null && !seller.isPass()){
				TransformUserData tud = new TransformUserData(user);
				result.add(tud);
			}
		}
		return result;
	}
	
	public List<TransformUserData> getAllUser() {
		List<TransformUserData> result = new ArrayList<TransformUserData>();
		for (UserCharacter user : characters.values()){
			if (!user.getPermission().admin()){
				TransformUserData tud = new TransformUserData(user);
				result.add(tud);
			}
		}
		return result;
	}
	
	public int computeOkOrderNum(long id) {
		int count = 0 ;
		for (Deal deal : deals){
			for (DealOrder order : deal.getOrders()){
				if (order.over()){
					count++;
				}
			}
		}
		return count;
	}

	public List<Deal> getDeals() {
		return deals;
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
}
 
