package com.joymeng.slg.domain.map.fight.result;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.map.impl.MapRoleInfo;

public class FightVersus implements Instances{
	public static final byte FIGHT_TARGET_TYPE_PLAYER = 0;
	public static final byte Fight_TARGET_TYPE_CITY   = 1;
	byte type;//0玩家,1城市
	MapRoleInfo info = new MapRoleInfo();
	
	public FightVersus() {
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public MapRoleInfo getInfo() {
		return info;
	}

	public void setInfo(MapRoleInfo info) {
		this.info = info;
	}

	public void copy(MapRoleInfo info) {
		this.info.copy(info);
	}
	
	public void serialize(JoyBuffer out) {
		out.put(type);//byte //0玩家,1城市
		info.serialize(out);
	}
}
