package com.joymeng.list;

public class NoticeInfo {
	int noticeType;//公告类型
	int serverId; // 游戏区服
	String noticeContent; // 公告内容
	String startTime; // 开始时间
	String endTime; // 结束时间
	int timeDelay; // 时间间隔

	public NoticeInfo(int noticeType, int serverId, String noticeContent, String startTime, String endTime,
			int timeDelay) {
		this.noticeType = noticeType;
		this.serverId = serverId;
		this.noticeContent = noticeContent;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeDelay = timeDelay;
	}

	public int getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(int noticeType) {
		this.noticeType = noticeType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(int timeDelay) {
		this.timeDelay = timeDelay;
	}

}
