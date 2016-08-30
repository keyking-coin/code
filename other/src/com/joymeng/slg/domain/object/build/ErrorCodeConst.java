package com.joymeng.slg.domain.object.build;

public enum ErrorCodeConst {
	SUC_RETURN((byte)0),//
	ERR_NORMAL((byte)1),//
	ERR_QUEUE_LMT((byte)2),//
	;
	private byte key;
	
	ErrorCodeConst(byte key){
		this.key = key;
	}

	public byte getKey() {
		return key;
	}

	public void setKey(byte key) {
		this.key = key;
	}
}
