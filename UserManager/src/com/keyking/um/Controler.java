package com.keyking.um;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.keyking.um.data.Broker;
import com.keyking.um.data.UserBroker;
import com.keyking.um.data.UserCharacter;
import com.keyking.util.Instances;
import com.keyking.util.ServerLog;

public class Controler implements Instances{
	
	private static Controler instance = new Controler();
	Map<String,UserCharacter> users = new ConcurrentHashMap<String,UserCharacter>();
	Map<Long,Broker> brokers = new ConcurrentHashMap<Long,Broker>();
	List<UserBroker> userBrokers = new CopyOnWriteArrayList<UserBroker>();
	
	public static Controler getInstance() {
		return instance;
	}
	
	public void load(){
		ServerLog.info("load all users");
		List<UserCharacter> tus = DB.getUserDao().loadAll();
		for (UserCharacter user : tus){
			users.put(user.getAccount(),user);
		}
		ServerLog.info("load all brokers");
		List<Broker> tbs = DB.getBrokerDao().loadAll();
		for (Broker broker : tbs){
			brokers.put(broker.getId(),broker);
		}
		ServerLog.info("load all userBrokers");
		List<UserBroker> ubs = DB.getUserBrokerDao().loadAll();
		for (UserBroker ub : ubs){
			userBrokers.add(ub);
		}
	}
	
	public UserCharacter search(String accout){
		UserCharacter user = users.get(accout);
		if (user == null){
			user = DB.getUserDao().search(accout);
			if (user != null){
				users.put(accout,user);
			}
		}
		return user;
	}
	
	public UserCharacter searchByAccount(String value){
		for (UserCharacter user : users.values()){
			if (user.getAccount().equals(value)){
				return user;
			}
		}
		UserCharacter user = DB.getUserDao().search(value);
		if (user != null){
			return user;
		}
		return null;
	}
	
	public String checkHttpAccout(String account){
		for (UserCharacter user : users.values()){
			if (account != null){
				if (user.getAccount().equals(account)){
					return account + "已被注册";
				}else if(account.length() < 11){
					return account + "少于11位";
				}
			}
		}
		if (account != null){
			if (DB.getUserDao().search(account) != null){
				return account + "已被注册";
			}else if(account.length() < 11){
				return account + "少于11位";
			}
		}
		return null;
	}
	
	
	public UserCharacter search(long id){
		for (UserCharacter user : users.values()){
			if (user.getId() == id){
				return user;
			}
		}
		UserCharacter user = DB.getUserDao().search(id);
		if (user != null){
			users.put(user.getAccount(),user);
		}
		return user;
	}
	
	public synchronized boolean register(UserCharacter user){
		long id = PK.key("users");
		user.setId(id);
		users.put(user.getAccount(),user);
		return true;
	}
	
		
	public List<UserCharacter> searchFuzzyUser(String key) {
		List<UserCharacter> result = new ArrayList<UserCharacter>();
		for (UserCharacter user : users.values()){
			if (user.getAccount().contains(key) ||
			    user.getName().contains(key)){
				result.add(user);
			}
		}
		return result;
	}
	
	public List<UserCharacter> getUsers() {
		List<UserCharacter> result = new ArrayList<UserCharacter>();
		for (UserCharacter user : users.values()){
			result.add(user);
		}
		return result;
	}
}
 
