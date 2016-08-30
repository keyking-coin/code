package com.joymeng.slg.domain.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleChatMail {
	
	Long roleChatMailId = new Long(1L);	
	
	Map<Long, ChatMsg> roleChatMails = new HashMap<Long, ChatMsg>();

	public Map<Long, ChatMsg> getRoleChatMails() {
		return roleChatMails;
	}

	public void setRoleChatMails(Map<Long, ChatMsg> roleChatMails) {
		this.roleChatMails = roleChatMails;
	}
	
	/**
	 * 加入用户邮箱
	 * @param msg
	 */
	public synchronized void addRoleMail(ChatMsg msg) {
		msg.setId(roleChatMailId);
		roleChatMails.put(roleChatMailId, msg);
		roleChatMailId++;
	}
	
	/**
	 * 从个人邮箱中移除msgsIds
	 * @param msgsIds
	 */
	public synchronized void removeRoleMail(List<Long> msgsIds){
		for (int i = 0 ; i < msgsIds.size() ; i++){
			Long msgsId = msgsIds.get(i);
			ChatMsg chatMsg = roleChatMails.get(msgsId);
			if (chatMsg == null || msgsId > roleChatMailId) {
				continue;
			}
			roleChatMails.remove(msgsId);
		}
	}
	
}
