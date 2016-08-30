package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModWorldNoticeMsgUpdate extends AbstractModChat {
	ChatMsg[] msg;
	
	public ModWorldNoticeMsgUpdate(ChatMsg msg){
		this.msg = new ChatMsg[]{msg};
	}
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msg);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_WORLD_NOTICE_MSG_SEND;
	}

}
