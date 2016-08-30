package com.joymeng.slg.net.mod.chat;

import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.slg.domain.chat.ChatGroup;

public class ModRoleGroupUpdate extends AbstractModChat{
	ChatGroup[] groups;
	
	public ModRoleGroupUpdate(ChatGroup groups) {
		this.groups = new ChatGroup[] { groups };
	}
	
	@Override
	public void subserialize(JoyBuffer out) {
		sendRoleGroup(out,groups);
	}

	@Override
	public short getSubModuleType() {
		return UI_TYPE_ROLE_GROUP_UPDATE;
	}
}
