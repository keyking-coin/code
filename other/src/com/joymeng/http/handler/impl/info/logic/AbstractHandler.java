package com.joymeng.http.handler.impl.info.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.list.ServerManager;
import com.joymeng.list.ServiceState;

public abstract class AbstractHandler implements LogicHandler{
	Map<Long, InterruptClass> counts = new HashMap<Long,InterruptClass>();
	Map<Long, Map<String, Object>> objs = new HashMap<Long, Map<String, Object>>();
	List<Integer> serList =new ArrayList<Integer>();
	
	public List<Integer> newList(){
		Collection<ServiceState> states = ServerManager.getInstance().getStateMap().values();
		List<Integer> serList = new ArrayList<Integer>();
		for(ServiceState state : states){
			serList.add(state.getServiceId());
		}
		return serList;
	}
	
	public List<String> serverList(){
		Collection<ServiceState> states = ServerManager.getInstance().getStateMap().values();
		List<String> serList = new ArrayList<String>();
		for(ServiceState state : states){
			serList.add(String.valueOf(state.getServiceId()));
		}
		return serList;
	}
	
	public boolean isLegal(String playId) {
		String[] pplayId = playId.split(",");
		boolean isNum;
		for (int i = 0 ; i < pplayId.length ; i++){
			String id = pplayId[i];
			isNum = id.matches("[0-9]+");
			if (!isNum) {
				return false;
			}
		}
		return true;
	}
	
	class InterruptClass{
		List<String> targets = new ArrayList<String>();
		long start;
		public InterruptClass(){
			start = TimeUtils.nowLong();
		}
		
		public long getTime(){
			if (targets.size() == 0){
				return 0;
			}
			return start;
		}
		
		public void add(int target){
			targets.add(String.valueOf(target));
		}
		
		public void remove(int target){
			targets.remove(String.valueOf(target));
		}
	}
	
	public void insert(long uid,int target){
		InterruptClass ic = counts.get(uid);
		if (ic == null){
			ic = new InterruptClass();
			counts.put(uid,ic);
		}
		ic.add(target);
	}
	
	public void interrupt(long uid){
		InterruptClass ic = counts.get(uid);
		if (ic != null){
			while (true) {
				long pre = ic.getTime();
				if (pre == 0) {
					break;
				}
				long time = TimeUtils.nowLong() - pre;
				if (time > 30 * 1000) {
					break;
				}
			}
		}
		counts.remove(uid);
	}
	
	public void remove(long uid,int target){
		InterruptClass ic = counts.get(uid);
		if (ic != null){
			ic.remove(target);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(long uid,String key){
		if (objs.containsKey(uid)){
			Object obj = objs.get(uid).get(key);
			if (obj != null){
				return (T)obj;
			}
		}
		return null;
	}
}
