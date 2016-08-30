package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatGroup;

public class ModRoleGroupSend extends AbstractModChat{
	ChatGroup[] groups;
	
	public ModRoleGroupSend(ChatGroup[] groups) {
		this.groups = groups;
	}
	
	@Override
	public void subserialize(JoyBuffer out) {
		sendRoleGroup(out,groups);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_ROLE_GROUP_SEND;
	}

}
