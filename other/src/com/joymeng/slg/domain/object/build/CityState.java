package com.joymeng.slg.domain.object.build;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.Instances;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.impl.still.role.MapCity;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;
import com.joymeng.slg.net.mod.RespModuleSet;

public class CityState implements Instances{
	//城池状态
	boolean isNowar = false; //战争保护状态
	boolean isNospy = false; //侦查保护状态
	boolean isDbspy = false; //侦查伪装状态，显示双倍的侦查结果
	boolean isResprotect = false; //资源保护状态,资源不能被掠夺
	boolean isFire = false;//燃烧状态
	int cityViewBuff = 0; //城市视野buff
	int fortViewBuff = 0; //要塞视野buff
	List<TimerLast> timers = new ArrayList<TimerLast>();
	MapCity mc = null;
	
	public CityState(MapCity mc){
		this.mc = mc;
	}
	
	public boolean isNowar() {
		return isNowar;
	}

	public void setNowar(boolean isNowar) {
		this.isNowar = isNowar;
	}

	public boolean isNospy() {
		return isNospy;
	}

	public void setNospy(boolean isNospy) {
		this.isNospy = isNospy;
	}

	public boolean isDbspy() {
		return isDbspy;
	}

	public void setDbspy(boolean isDbspy) {
		this.isDbspy = isDbspy;
	}

	public boolean isResprotect() {
		return isResprotect;
	}

	public void setResprotect(boolean isResprotect) {
		this.isResprotect = isResprotect;
	}

	public boolean isFire() {
		return isFire;
	}

	public void setFire(boolean isFire) {
		this.isFire = isFire;
	}
	
	public int getCityViewBuff() {
		return cityViewBuff;
	}

	public void setCityViewBuff(int cityViewBuff) {
		this.cityViewBuff = cityViewBuff;
	}

	public int getFortViewBuff() {
		return fortViewBuff;
	}

	public void setFortViewBuff(int fortViewBuff) {
		this.fortViewBuff = fortViewBuff;
	}
	
	public void updateCityViewBuff(boolean isRemove, int cityViewBuff){
		if(isRemove){
			this.cityViewBuff -= cityViewBuff;
		}else{
			this.cityViewBuff += cityViewBuff;
		}
	}
	
	public void updateFortViewBuff(boolean isRemove, int fortViewBuff){
		if(isRemove){
			this.fortViewBuff -= fortViewBuff;
		}else{
			this.fortViewBuff += fortViewBuff;
		}
	}

	public MapCity getMc() {
		return mc;
	}

	public void setMc(MapCity mc) {
		this.mc = mc;
	}

	public void setTimers(List<TimerLast> timers) {
		this.timers = timers;
	}
	
	public List<TimerLast> getTimers() {
		return timers;
	}
	
	public TimerLast addTimer(long last,TimerLastType type, Object param){
		TimerLast timer = searchTimer(type);
		if(timer == null){
			timer = new TimerLast(TimeUtils.nowLong()/1000,last,type);
			timer.setParam(param);
			timers.add(timer);
			timer.registTimeOver(new CityStateFinish(this, type));
			taskPool.mapTread.addObj(mc,timer);
		} else {
			timer.setLast(last);
		}
		return timer;
	}
	
	public TimerLast searchTimer(TimerLastType type){
		for (int i = 0 ; i < timers.size() ; i++){
			TimerLast timer = timers.get(i);
			if (timer.getType().equals(type)){
				return timer;
			}
		}
		return null;
	}
	
	public List<TimerLast> searchItemTimer(){
		List<TimerLast> itemTimers = new ArrayList<TimerLast>();
		for (TimerLast timer : timers){
			if(timer.getType() == TimerLastType.TIME_CITY_NOWAR ||
				timer.getType() == TimerLastType.TIME_CITY_NOSPY ||
				timer.getType() == TimerLastType.TIME_CITY_DBSPY ||
				timer.getType() == TimerLastType.TIME_CITY_FIRE){
				itemTimers.add(timer);
			}
		}
		return itemTimers;
	}
	
	public void updateTimer(TimerLastType type){
		for (int i = 0; i < timers.size();){
			TimerLast timer = timers.get(i);
			if (timer.getType() == type){
				timers.remove(timer);
				cityStateChange(type, false);
			}else{
				i++;
			}
		}
	}

	public void cityStateChange(TimerLastType type, boolean isActive) {
		switch (type) {
		case TIME_CITY_NOWAR:
			setNowar(isActive);
			break;
		case TIME_CITY_NOSPY:
			setNospy(isActive);
			break;
		case TIME_CITY_DBSPY:
			setDbspy(isActive);
			break;
		case TIME_CITY_FIRE:
			setFire(isActive);
			break;
		default:
			break;
		}
	}
	
	public void _tick(){
		//城池状态倒计时
		if(timers.size() > 0){
			for(int i=0;i < timers.size();){
				TimerLast timer = timers.get(i);
				if(timer != null && timer.over()){
					timer.die();
					timers.remove(i);
					if (mc != null){
						Role role = world.getOnlineRole(mc.getInfo().getUid());
						if (role != null){
							RespModuleSet rms = new RespModuleSet();
							role.getCity(0).sendCityStateToClient(role, rms);
							MessageSendUtil.sendModule(rms, role.getUserInfo());
						}
					}
				}else{
					i++;
				}
			}
		}
	}
	
	public String serialize(){
		if(timers.size() == 0){
			return "";
		}
		String str = JsonUtil.ObjectToJsonString(timers);
		return str;
	}
	
	public void deserialize(String str) {
		if (StringUtils.isNull(str)) {
			return;
		}
		timers = JsonUtil.JsonToObjectList(str, TimerLast.class);
		long now = TimeUtils.nowLong() / 1000;
		for (int i=0 ; i < timers.size() ; ){
			TimerLast timer = timers.get(i);
			if(timer.getStart() + timer.getLast() <= now){
				timers.remove(i);
			}else{
				cityStateChange(timer.getType(),true);
				timer.registTimeOver(new CityStateFinish(this,timer.getType()));
				taskPool.mapTread.addObj(mc,timer);
				i++;
			}
		}
	}

	public void serialize(JoyBuffer out) {
		out.put((byte)(isNowar ? 1 : 0));
		out.put((byte)(isNospy ? 1 : 0));
		out.put((byte)(isDbspy ? 1 : 0));
		out.put((byte)(isResprotect ? 1 : 0));
		out.put((byte)(isFire ? 1 : 0));
	}
}
