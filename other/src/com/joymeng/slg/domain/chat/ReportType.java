package com.joymeng.slg.domain.chat;

public enum ReportType {
	TYPE_DEFAULT((byte)0),//默认(不做使用)
	TYPE_UNION_NOTICE((byte)2),//联盟全体邮件
	TYPE_UNION_INVITE((byte)3),//联盟邀请
	TYPE_SPY_REPORT((byte)4),//侦查报告
	TYPE_BATTLE_REPORT((byte)5),//战斗报告
	TYPE_MOVECITY_INVITE((byte)6),//迁城邀请
	TYPE_SYSTEM_MAIL((byte)7),//系统邮件
	TYPE_COLLECTION((byte)8),//资源采集
	TYPE_UPDATA_ROLE_NAME((byte)9);//更新用户名称
	
	private byte key;

	private ReportType(byte key) {
		this.key = key;
	}

	public Byte getKey() {
		return key;
	}

	public void setKey(Byte key) {
		this.key = key;
	}
	public static ReportType search(byte key) {
		ReportType[] datas = values();
		for (int i = 0; i < datas.length; i++) {
			ReportType tempKey = datas[i];
			if (tempKey.key == key) {
				return tempKey;
			}
		}
		return null;
	}
}
