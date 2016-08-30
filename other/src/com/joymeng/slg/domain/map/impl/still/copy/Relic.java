package com.joymeng.slg.domain.map.impl.still.copy;

import com.joymeng.Instances;
import com.joymeng.services.core.buffer.JoyBuffer;

/**
 * 副本用户信息体
 * 
 * @author houshanping
 *
 */
public class Relic implements Instances {
	String id;// 副本的Id
	int position;// 位置
	int type;// 副本类型
	int armyState = 0;// 0:不在 1:在
	long troopId;// 部队编号

	public Relic() {
	}

	public Relic(String id, int position, int type, int armyState, long troopId) {
		this.id = id;
		this.position = position;
		this.type = type;
		this.armyState = armyState;
		this.troopId = troopId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getArmyState() {
		return armyState;
	}

	public void setArmyState(int armyState) {
		this.armyState = armyState;
	}

	public long getTroopId() {
		return troopId;
	}

	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}

	public void serialize(JoyBuffer out) {
		out.putPrefixedString(id, JoyBuffer.STRING_TYPE_SHORT);// 副本的Id
		out.putInt(position);// 位置
		out.putInt(type);// 副本类型
		out.putInt(armyState);// 0:不在 1:在
		out.putLong(troopId);// 部队编号
	}

	public void deserialize(JoyBuffer buffer) {
		id = buffer.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);// 副本的Id
		position = buffer.getInt();// 位置
		type = buffer.getInt();// 副本类型
		armyState = buffer.getInt();// 0:不在 1:在
		troopId = buffer.getLong();// 部队编号
	}
}
