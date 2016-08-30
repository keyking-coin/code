package com.joymeng.slg.domain.timer;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.SerializeEntity;

/**
 * 倒计时类
 * @author tanyong
 *
 */
public class TimerLast implements SerializeEntity{
	
	TimerLastType type;
	
	long start;
	
	long last;
	
	List<TimerOver> timerOvers = new ArrayList<TimerOver>();
	
	Object param;
	
	public TimerLast(){
		
	}
	
	public TimerLast(TimerOver timerOver){
		registTimeOver(timerOver);
	}
	
	public TimerLast(TimerLastType type){
		this.type  = type;
	}
	
	public TimerLast(long start,long last,TimerLastType type){
		this.start = start;
		this.type  = type;
		this.last  = last;
	}
	
	public boolean over(){
		return over(TimeUtils.nowLong());
	}
	
	public boolean over(long now){
		long time = now / 1000;
		if (!type.isFlag() || type == TimerLastType.TIME_FOREVER){
			return false;
		}
		return time >= start + last;
	}
	
	public TimerLastType getType() {
		return type;
	}

	public void setType(TimerLastType type) {
		this.type = type;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getLast() {
		return last;
	}

	public void setLast(long last) {
		this.last = last;
	}
	
	public void setParam(Object param){
		this.param = param;
	}
	
	public Object getParam(){
		return param;
	}
	
	public void resetLastAt(long now , long last){
		long passedTime = now - start;
		this.last = last + passedTime;
	}
	
	public void registTimeOver(TimerOver timerOver){
		if (timerOver != null && !timerOvers.contains(timerOver)){
			timerOvers.add(timerOver);
		}
	}
	
	public void die(){
		for (int i = 0 ; i < timerOvers.size() ; i++){
			TimerOver over = timerOvers.get(i);
			over.finish();
		}
	}
	
	public void sendToClient(ParametersEntity params){
		params.put(type.getKey());//倒计时类型 string
		params.put(start);//开始的服务器时间秒 long
		params.put(last);//持续时间秒 long
		long now = TimeUtils.nowLong() / 1000;
		params.put(now);//系统当前时间秒 long
	}
	
	@Override
	public void serialize(JoyBuffer out){
		out.putPrefixedString(type.getKey(),JoyBuffer.STRING_TYPE_SHORT);//倒计时类型 string
		out.putLong(start);//开始的服务器时间秒 long
		out.putLong(last);//持续时间秒 long
		long now = TimeUtils.nowLong() / 1000;
		out.putLong(now);//系统当前时间秒 long
	}

	public void deserialize(JoyBuffer buffer) {
		String key = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		type = TimerLastType.search(key);
		start = buffer.getLong();
		last = buffer.getLong();
		buffer.getLong();
	}

	public void removeTimeOver(TimerOver over) {
		if (timerOvers.contains(over)){
			timerOvers.remove(over);
		}
	}
}
