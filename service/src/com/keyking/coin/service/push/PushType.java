package com.keyking.coin.service.push;

public enum PushType {
	PUSH_TYPE_KICK,//被挤下线了
	PUSH_TYPE_DEAL,//交易推送
	PUSH_TYPE_ORDER,//交易订单改变
	PUSH_TYPE_REVERT,//交易回复
	PUSH_TYPE_TIME,//时间轴变化
	PUSH_TYPE_FRIEND,//好友信息
	PUSH_TYPE_EMAIL//站内信
	;
}
