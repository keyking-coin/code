package com.joymeng.slg.net.handler.impl.feedback;

public class FeedBack {
	long uid; // 玩家uid
	String channelId; // 玩家渠道Id
	String time; // 反馈时间
	String come; //来源
	String content; // 反馈内容

	public FeedBack(long uid, String channelId, String time, String come,String content) {
		this.uid = uid;
		this.channelId = channelId;
		this.time = time;
		this.come = come;
		this.content = content;
	}
	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
