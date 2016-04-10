package com.keyking.admin.data.user;

import com.keyking.admin.StringUtil;

public class Forbid {
	String reason;//null未被封号，不为null就是原因
	long endTime = 0;//-1永久封号，0正常状态,>0表示封号截止时间。
	public String getReason() {
		return StringUtil.isNull(reason) ? null : reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}
