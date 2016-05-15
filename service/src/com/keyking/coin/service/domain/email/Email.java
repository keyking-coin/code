package com.keyking.coin.service.domain.email;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class Email implements Instances,SerializeEntity,Comparable<Email>{
	byte type = 1;//邮件类型 0系统邮件 1用户邮件
	byte status;//状态 0新邮件 1已查看
	long id;//邮件编号
	long senderId;//发送者编号
	long userId;//接受者编号
	String time;// 发送时间
	String theme   = "";// 主题
	String content = "";// 内容
	
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
	
	public long getSenderId() {
		return senderId;
	}
	
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
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
	
	public void save() {
		DB.getEmailDao().save(this);
	}

	public void serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.put(type);
		buffer.put(status);
		buffer.putLong(senderId);
		UserCharacter sender = CTRL.search(senderId);
		buffer.putUTF(time);
		buffer.putUTF(theme);
		buffer.putUTF(content);
		buffer.putUTF(sender.getNikeName());
		buffer.putUTF(sender.getFace());
	}

	@Override
	public int compareTo(Email email) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(email.time);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}

	public void delete() {
		DB.getEmailDao().delete(this);
	}
}
