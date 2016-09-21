package com.joymeng.slg.domain.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 用户邮箱
 * @author houshanping
 *
 */
public class RoleChatMail {
	
	Long roleChatMailId = new Long(1L);	
	
	Map<Long, ChatMsg> roleChatMails = new HashMap<Long, ChatMsg>();

	public Map<Long, ChatMsg> getRoleChatMails() {
		return roleChatMails;
	}

	public void setRoleChatMails(Map<Long, ChatMsg> roleChatMails) {
		this.roleChatMails = roleChatMails;
	}
	
	public Long getRoleChatMailId() {
		return roleChatMailId;
	}

	public void setRoleChatMailId(Long roleChatMailId) {
		this.roleChatMailId = roleChatMailId;
	}

	/**
	 *启动服务器是时候第一次加载邮件
	 * @param msg
	 */
	public synchronized void firstAddMail(ChatMsg msg){
		roleChatMails.put(msg.getId(), msg);
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
