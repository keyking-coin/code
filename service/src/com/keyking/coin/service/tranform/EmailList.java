package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.email.Email;

public class EmailList {
	byte type = 1;//邮件类型 0系统邮件 1用户邮件
	byte status;//状态 0新邮件 1已查看
	long id;//邮件编号
	String time;// 发送时间
	String theme;// 主题
	
	public EmailList(Email email){
		type = email.getType();
		status = email.getStatus();
		id = email.getId();
		time = email.getTime();
		theme = email.getTheme();
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public byte getStatus() {
		return status;
	}
	
	public void setStatus(byte status) {
		this.status = status;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getTheme() {
		return theme;
	}
	
	public void setTheme(String theme) {
		this.theme = theme;
	}
}
