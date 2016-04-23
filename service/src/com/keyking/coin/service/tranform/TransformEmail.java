package com.keyking.coin.service.tranform;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.email.Email;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class TransformEmail implements Instances,Comparable<TransformEmail>{
	byte type = 1;//邮件类型 0系统邮件 1用户邮件
	long id;//邮件编号
	long senderId;//发送者编号
	String senderName;//发送者名称
	long receiverId;//接受者编号
	String receiverName;//接受者名称	
	String time;// 发送时间
	String theme   = "";// 主题
	String content = "";// 内容
	byte status;//状态 0新邮件 1已查看
	
	public TransformEmail(Email email,UserCharacter me){
		type = email.getType();
		id = email.getId();
		senderId = email.getSenderId();
		UserCharacter sender = CTRL.search(senderId);
		senderName   = sender.getNikeName();
		receiverId   = me.getId();
		receiverName = me.getNikeName();
		time = email.getTime();
		theme = email.getTheme();
		content = email.getContent();
		status = email.getStatus();
	}
	
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(long receiverId) {
		this.receiverId = receiverId;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	@Override
	public int compareTo(TransformEmail o) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(o.time);
		if (time1.isBefore(time2)){
			return -1;
		}
		return 1;
	}
}
