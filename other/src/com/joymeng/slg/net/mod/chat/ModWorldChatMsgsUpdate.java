package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModWorldChatMsgsUpdate extends AbstractModChat{
	ChatMsg[] msgs;
	
	public ModWorldChatMsgsUpdate(ChatMsg msgs){
		this.msgs = new ChatMsg[]{msgs};
	}
	
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msgs);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_WORLD_CHAT_MSGS_UPDATE;
	}

}
