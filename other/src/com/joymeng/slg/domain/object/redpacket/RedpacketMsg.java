package com.joymeng.slg.domain.object.redpacket;

public class RedpacketMsg {
	String itemId;
	long redpacketId;

	public RedpacketMsg() {

	}

	public RedpacketMsg(String itemId, long redpacketId) {
		this.itemId = itemId;
		this.redpacketId = redpacketId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public long getRedpacketId() {
		return redpacketId;
	}

	public void setRedpacketId(long redpacketId) {
		this.redpacketId = redpacketId;
	}

}
