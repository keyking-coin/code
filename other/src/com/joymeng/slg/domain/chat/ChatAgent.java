package com.joymeng.slg.domain.chat;

import java.util.HashMap;
import java.util.Map;

import com.joymeng.Instances;
import com.joymeng.common.util.StringUtils;


public class ChatAgent implements Instances {
	public static final byte[] DEFAULT_INSTALLATIONS = {0,0};
	private long lastWorldChatMsgDate;
	long uid;
	Map<Long,byte[]> chatGroups = new HashMap<>();//置顶，屏蔽
	
	public ChatAgent() {
	}
	
	public ChatAgent(long uid) {
		this.uid = uid;
	}
	
	public void setUid(long uid){
		this.uid = uid;
	}

	public Map<Long, byte[]> getChat_groups() {
		return chatGroups;
	}

	public void setChat_groups(Map<Long, byte[]> chatGroups) {
		this.chatGroups = chatGroups;
	}

	public long getUid() {
		return uid;
	}

	public long getLastWorldChatMsgDate() {
		return lastWorldChatMsgDate;
	}

	public void setLastWorldChatMsgDate(long lastWorldChatMsgDate) {
		this.lastWorldChatMsgDate = lastWorldChatMsgDate;
	}

	public void deserialize(String data) {
		if (!StringUtils.isNull(data)) {
			String[] strs = data.split(":");
			for (int i = 0 ; i < strs.length ; i++){
				String string = strs[i];
				String[] dataStrings = string.split(",");
				long groupId = Long.valueOf(dataStrings[0]);
				byte b1 = Byte.valueOf(dataStrings[1]);
				byte b2 = Byte.valueOf(dataStrings[2]);
				byte[] installations = { b1, b2 };
				chatGroups.put(groupId, installations);
			}
		}
	}

	public String serialize() {
		String val = "";
		for (Long groupId : chatGroups.keySet()) {
			val += String.valueOf(groupId) + ",";
			byte b1 = chatGroups.get(groupId)[0];
			byte b2 = chatGroups.get(groupId)[1];
			val += String.valueOf(b1) + ",";
			val += String.valueOf(b2) + ":";
		}
		if (val.length() > 1) {
			val = val.substring(0, val.length() - 1);			
		}
		return val;
	}
}

