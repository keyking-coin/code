package com.joymeng.gm2.net.message.request;

import com.joymeng.gm2.net.ProtocolList;
import com.joymeng.gm2.net.message.GMRequest;
import com.joymeng.services.core.buffer.JoyBuffer;

public class MessageRequest extends GMRequest{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte msgType; //消息类型	
	private byte chatType; //频道类型
	private String content; //
	public MessageRequest() {
		super(ProtocolList.REQ_MESSAGE);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void _deserialize(JoyBuffer in) {
		// TODO Auto-generated method stub
		this.content = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
		this.setMsgType(in.get());
		this.setChatType(in.get());
		
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public byte getChatType() {
		return chatType;
	}

	public void setChatType(byte chatType) {
		this.chatType = chatType;
	}

}
