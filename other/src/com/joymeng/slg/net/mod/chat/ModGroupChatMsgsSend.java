package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModGroupChatMsgsSend extends AbstractModChat{
	ChatMsg[] msgs;
	
	public ModGroupChatMsgsSend(ChatMsg[] msgs){
		this.msgs = msgs;
	}
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msgs);
	}

	@Override
	public short getSubModuleType() {		
		return UI_TYPE_GROUP_CHAT_MSGS_SEND;
	}
}
