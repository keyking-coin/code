package com.keyking.coin.service.domain.time;


public class TimeLine {
	long id;
	byte type;//1 申购;2托管预约;3托管入库;4重要提示
	String title;//标题
	String startTime;//开始时间
	String endTime;//结束时间
	String url;//地址
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
