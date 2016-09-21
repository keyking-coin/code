package com.joymeng.slg.domain.chat;

public enum MsgType {
	// 聊天类型 0：普通文字聊天 1:语音消息 2:系统消息 3:公告消息 4:联盟邮件 5:公告联盟邀请 6:系统聊天 7:红包
	TYPE_COMMON((byte) 0), 
	TYPE_VOICE((byte) 1), 
	TYPE_SYSTEM((byte) 2), 
	TYPE_HORN((byte) 3), 
	TYPE_UNIONNOTICE((byte) 4), 
	TYPE_UNIONINVITE((byte) 5), 
	TYPE_SYSTEM_MSG((byte) 6), 
	TYPE_REDPACKET((byte) 7);

	private byte key;

	private MsgType(byte key) {
		this.key = key;
	}

	public Byte getKey() {
		return key;
	}

	public void setKey(Byte key) {
		this.key = key;
	}

	public static MsgType search(byte key) {
		MsgType[] datas = values();
		for (int i = 0; i < datas.length; i++) {
			MsgType tempKey = datas[i];
			if (tempKey.key == key) {
				return tempKey;
			}
		}
		return null;
	}
}
