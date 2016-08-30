package com.joymeng.slg.domain.map.impl.dynamic;

import com.joymeng.services.core.buffer.JoyBuffer;

public class SpeedNode {
	long time;
	float speed;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void serialize(JoyBuffer out) {
		out.putLong(time);
		out.putPrefixedString(String.valueOf(speed),JoyBuffer.STRING_TYPE_SHORT);
	}
}
