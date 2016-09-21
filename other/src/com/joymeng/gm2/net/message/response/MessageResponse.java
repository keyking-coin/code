package com.joymeng.gm2.net.message.response;

import com.joymeng.gm2.net.ProtocolList;
import com.joymeng.gm2.net.message.GMResponse;
import com.joymeng.services.core.buffer.JoyBuffer;

public class MessageResponse extends GMResponse{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;
	private byte chatType;
	private byte msgType;
	private long uid;
	private long unionId;
	private String name;

	public MessageResponse() {
		super(ProtocolList.RESP_MESSAGE);
		// TODO Auto-generated constructor stub
	}

	public MessageResponse(long id, long unionId2, String name2, String msg, byte chatType2, byte msgTypeKey) {
		// TODO Auto-generated constructor stub
		this();
		this.instanceId = 0x3015;
		this.uid = id;
		this.unionId = unionId2;
		this.name = name2;
		this.content = msg;
		this.chatType = chatType2;
		this.msgType = msgTypeKey;
	}

	@Override
	protected void _serialize(JoyBuffer out) {
		// TODO Auto-generated method stub
		out.putPrefixedString(content, JoyBuffer.STRING_TYPE_SHORT);
		out.put(chatType);
		out.put(msgType);
		out.putLong(uid);
		out.putLong(unionId);
		out.putPrefixedString(name, JoyBuffer.STRING_TYPE_SHORT);
		out.putInt(instanceId);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte getChatType() {
		return chatType;
	}

	public void setChatType(byte chatType) {
		this.chatType = chatType;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getUnionId() {
		return unionId;
	}

	public void setUnionId(long unionId) {
		this.unionId = unionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
