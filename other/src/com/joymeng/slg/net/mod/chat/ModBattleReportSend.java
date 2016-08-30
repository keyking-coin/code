package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModBattleReportSend extends AbstractModChat{
	ChatMsg[] msgs;
	
	public ModBattleReportSend(ChatMsg[] msgs){
		this.msgs = msgs;
	}
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msgs);
	}

	@Override
	public short getSubModuleType() {		
		return UI_TYPE_REPORT_SEND;
	}
}
