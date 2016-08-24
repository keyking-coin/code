package com.keyking.coin.service.domain.other;


public class NoticeEntity implements Comparable<NoticeEntity>{
	long _time;//时间戳
	String time;//时间
	String title;//标题
	String body;//html内容
	byte type;//0公告,1规则,2其他
	
	public long get_time() {
		return _time;
	}

	public void set_time(long _time) {
		this._time = _time;
	}

	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	@Override
	public int compareTo(NoticeEntity o) {
		return Long.compare(o._time,_time);
	}
}
