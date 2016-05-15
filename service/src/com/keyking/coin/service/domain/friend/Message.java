package com.keyking.coin.service.domain.friend;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.Instances;
import com.keyking.coin.util.TimeUtils;

public class Message implements Instances,SerializeEntity,Comparable<Message>{
	long id;//主键
	String actors;//参与者
	long sendId;//发送者编号
	String time;//发送时间
	String content;//发送内容
	byte type;//内容的类型0文字,1图片(暂时只支持两类后面可能要扩展)
	byte look;//是否看过了0未看,1看过了
	byte showTime;//显示时间
	public static long MESSAGE_PRE_TIME = 5 * 60 * 1000;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getActors() {
		return actors;
	}
	
	public void setActors(String actors) {
		this.actors = actors;
	}
	
	public long getSendId() {
		return sendId;
	}
	
	public void setSendId(long sendId) {
		this.sendId = sendId;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public byte getLook() {
		return look;
	}
	
	public void setLook(byte look) {
		this.look = look;
	}
	
	public byte isShowTime() {
		return showTime;
	}
	
	public void setShowTime(byte showTime) {
		this.showTime = showTime;
	}
	
	@Override
	public void serialize(DataBuffer out) {
		out.putLong(id);
		UserCharacter user = CTRL.search(sendId);
		out.putLong(sendId);
		out.putUTF(user.getFace());
		out.put(type);
		out.put(look);
		out.put(showTime);
		out.putUTF(time == null ? "" : time);
		out.putUTF(content == null ? "" : content);
	}
	
	public ModuleResp clientMessage(byte type){
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_MESSAGE);
		module.setFlag(type);
		module.add("message",this);
		ModuleResp modules = new ModuleResp();
		modules.addModule(module);
		return modules;
	}
	
	public ModuleResp clientMessage(ModuleResp modules , byte type){
		Module module = new Module();
		module.setCode(Module.MODULE_CODE_MESSAGE);
		module.setFlag(type);
		module.add("message",this);
		modules.addModule(module);
		return modules;
	}
	
	public void save() {
		DB.getMessageDao().save(this);
	}
	
	public void del() {
		DB.getMessageDao().delete(this);
	}
	
	@Override
	public int compareTo(Message o) {
		DateTime time1 = TimeUtils.getTime(time);
		DateTime time2 = TimeUtils.getTime(o.time);
		if (time1.isBefore(time2)){
			return 1;
		}else{
			return -1;
		}
	}
}
