package com.keyking.coin.service.domain.user;

import org.joda.time.DateTime;

import com.keyking.coin.util.TimeUtils;

public class TransForbid {
	String reason;//null未被封号，不为null就是原因
	String showTime;//
	long endTime;
	
	public void copy(Forbid forbid){
		reason = forbid.reason;
		if (forbid.endTime > 0){
			DateTime time = TimeUtils.getTime(forbid.endTime);
			showTime = TimeUtils.chDate(time.getMillis());
		}
	}
	
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getShowTime() {
		return showTime;
	}

	public void setShowTime(String showTime) {
		this.showTime = showTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
