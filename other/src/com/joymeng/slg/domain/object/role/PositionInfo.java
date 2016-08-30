package com.joymeng.slg.domain.object.role;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.net.SerializeEntity;

public class PositionInfo implements SerializeEntity{
	int pos;
	String name;
	String iconId;
	byte type;

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconId() {
		return iconId;
	}

	public void setIconId(String iconId) {
		this.iconId = iconId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	@Override
	public void serialize(JoyBuffer out) {
		out.putInt(pos);
		out.put(type);
		out.putPrefixedString(name,JoyBuffer.STRING_TYPE_SHORT);
		out.putPrefixedString(iconId,JoyBuffer.STRING_TYPE_SHORT);
	}
}
