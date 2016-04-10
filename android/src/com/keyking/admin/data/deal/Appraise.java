package com.keyking.admin.data.deal;

public class Appraise {
	boolean isCompleted = false;//是否完成评价
	byte star;//星级评价 3好评;2中评;1差评
	String detail = "null";//详细描述
	String time = "null";//时间
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
