package com.keyking.coin.service.domain.time;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.net.SerializeEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;

public class TimeLine implements SerializeEntity{
	long id;
	byte type;
	String title;//标题
	String time;//发生时间
	List<TimeContent> contents = new ArrayList<TimeContent>();
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public byte getType() {
		return type;
	}
	
	public void setType(byte type) {
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public List<TimeContent> getContents() {
		return contents;
	}
	
	public void setContents(List<TimeContent> content) {
		this.contents = content;
	}
	
	public String contentToStr(){
		return JsonUtil.ObjectToJsonString(contents);
	}
	
	public void strToContents(String str){
		if (StringUtil.isNull(str)){
			return;
		}
		contents = JsonUtil.JsonToObjectList(str,TimeContent.class);
	}
	
	public void serialize(DataBuffer buffer) {
		buffer.putLong(id);
		buffer.put(type);
		buffer.putUTF(title);
		buffer.putUTF(time);
		buffer.put((byte) contents.size());
		for (TimeContent content : contents){
			content.serialize(buffer);
		}
	}
}
