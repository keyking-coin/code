package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModGroupChatMsgsUpdate extends AbstractModChat{
	ChatMsg[] msgs;
	
	public ModGroupChatMsgsUpdate(ChatMsg msgs){
		this.msgs = new ChatMsg[]{msgs};
	}
	
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msgs);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_GROUP_CHAT_MSGS_UPDATE;
	}

}
