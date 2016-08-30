package com.joymeng.slg.domain.chat;

public class ChatSystemContent {

	String systemChatContent;
	String systemChatPara;

	public ChatSystemContent(String systemChatContent, String systemChatPara) {
		this.systemChatContent = systemChatContent;
		this.systemChatPara = systemChatPara;
	}

	public String getSystemChatContent() {
		return systemChatContent;
	}

	public void setSystemChatContent(String systemChatContent) {
		this.systemChatContent = systemChatContent;
	}

	public String getSystemChatPara() {
		return systemChatPara;
	}

	public void setSystemChatPara(String systemChatPara) {
		this.systemChatPara = systemChatPara;
	}

}
