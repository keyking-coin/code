package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatMsg;

public class ModBattleReportUpdate extends AbstractModChat{
	ChatMsg[] msgs;
	
	public ModBattleReportUpdate(ChatMsg msgs){
		this.msgs = new ChatMsg[]{msgs};
	}
	
	@Override
	public void subserialize(JoyBuffer out) {
		sendChatMsgs(out,msgs);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_REPORT_UPDATE;
	}
}
