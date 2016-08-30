package com.joymeng.slg.domain.chat;

/**
 * 公告消息体
 * @author houshanping
 */
public class NoticeMsg {
	int priorityLevel;
	long time;
	ChatMsg msg;

	public NoticeMsg() {
	}

	public NoticeMsg(int priorityLevel, Long time, ChatMsg msg) {
		this.priorityLevel = priorityLevel;
		this.time = time;
		this.msg = msg;
	}

	public int getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public ChatMsg getMsg() {
		return msg;
	}

	public void setMsg(ChatMsg msg) {
		this.msg = msg;
	}

}
