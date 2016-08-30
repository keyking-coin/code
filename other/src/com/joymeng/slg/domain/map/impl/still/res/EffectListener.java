package com.joymeng.slg.domain.map.impl.still.res;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.timer.TimerLast;
import com.joymeng.slg.domain.timer.TimerLastType;

public class EffectListener{
	float value;
	TimerLast timer = new TimerLast();
	
	public EffectListener(float value , TimerLast timer){
		this.value = value;
		this.timer.setType(timer.getType());
		this.timer.setStart(timer.getStart());
		this.timer.setLast(timer.getLast());
	}
	
	protected EffectListener(){
		
	}
	
	public float getValue() {
		return value;
	}
	
	public void setValue(float value) {
		this.value = value;
	}
	
	public TimerLast getTimer() {
		return timer;
	}
	
	public void setTimer(TimerLast timer) {
		this.timer = timer;
	}
	
	public void serialize(JoyBuffer buffer){
		buffer.putPrefixedString(String.valueOf(value),JoyBuffer.STRING_TYPE_SHORT);
		timer.serialize(buffer);
	}
	
	public void deserialize(JoyBuffer buffer){
		String str = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		value  = Float.parseFloat(str);
		String tKey = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		TimerLastType tt = TimerLastType.search(tKey);
		timer.setType(tt);
		timer.setStart(buffer.getLong());
		timer.setLast(buffer.getLong());
		buffer.getLong();
	}
}
