package com.keyking.admin.data.user;

import com.keyking.admin.StringUtil;

public class Forbid {
	String reason;//nullδ����ţ���Ϊnull����ԭ��
	long endTime = 0;//-1���÷�ţ�0����״̬,>0��ʾ��Ž�ֹʱ�䡣
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
