package com.keyking.coin.service.push;

public enum PushType {
	PUSH_TYPE_KICK,//被挤下线了
	PUSH_TYPE_DEAL,//买卖盘推送
	PUSH_TYPE_ORDER,//成交盘创建
	PUSH_TYPE_REVERT,//买卖盘回复
	PUSH_TYPE_FRIEND,//好友申请信息
	PUSH_TYPE_EMAIL,//收到新站内信
	PUSH_TYPE_ORDER_CHANGE//成交盘变化了。
	;
	
	public static PushType search(String name){
		PushType[] datas = values();
		for (int i = 0 ; i < datas.length ; i++){
			PushType pt = datas[i];
			if (pt.name().equals(name)){
				return pt;
			}
		}
		return null;
	}
}
