package com.keyking.coin.service.tranform;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;

public class EmailList implements Instances{
	byte type = 1;//邮件类型 0系统邮件 1用户邮件
	byte status;//状态 0新邮件 1已查看
	long id;//邮件编号
	String time;// 发送时间
	String theme;// 主题
	long senderId;//发送者编号
	String senderName = "未知目标";//发送者昵称
	String senderFace = "null";//发送者头像
	String content = "";// 内容
	
	public EmailList(Email email){
		type = email.getType();
		status = email.getStatus();
		id = email.getId();
		time = email.getTime();
		theme = email.getTheme();
		senderId = email.getSenderId();
		UserCharacter user = CTRL.search(senderId);
		if (user != null){
			senderName = user.getNikeName();
			senderFace = user.getFace();
		}
		content = email.getContent();
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

	public long getSenderId() {
		return senderId;
	}

	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderFace() {
		return senderFace;
	}

	public void setSenderFace(String senderFace) {
		this.senderFace = senderFace;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
