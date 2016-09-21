package com.joymeng.slg.domain.object.redpacket;

public enum RedpacketState {
	NORMAL((byte) 0), //正常
	INVALID((byte) 1), //已返回(失效)
	GOT_OVER((byte) 2); //已被领取完
	byte state;

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

	private RedpacketState(byte state) {
		this.state = state;
	}

}
