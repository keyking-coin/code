package com.keyking.admin.data.deal;

public class Appraise {
	boolean isCompleted = false;//�Ƿ��������
	byte star;//�Ǽ����� 3����;2����;1����
	String detail = "null";//��ϸ����
	String time = "null";//ʱ��
	public boolean isCompleted() {
		return isCompleted;
	}
	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}
	public byte getStar() {
		return star;
	}
	public void setStar(byte star) {
		this.star = star;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
