package com.joymeng.slg.domain.chat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import com.joymeng.Instances;
import com.joymeng.common.util.I18nGreeting;
import com.joymeng.common.util.JsonUtil;
import com.joymeng.common.util.MessageSendUtil;
import com.joymeng.common.util.StringUtils;
import com.joymeng.common.util.TimeUtils;
import com.joymeng.log.GameLog;
import com.joymeng.log.LogManager;
import com.joymeng.slg.domain.object.bag.BriefItem;
import com.joymeng.slg.domain.object.role.Role;
import com.joymeng.slg.net.mod.RespModuleSet;
import com.joymeng.slg.net.mod.chat.ModBattleReportSend;
import com.joymeng.slg.net.mod.chat.ModBattleReportUpdate;
import com.joymeng.slg.net.mod.chat.ModGroupChatMsgsSend;
import com.joymeng.slg.net.mod.chat.ModGroupChatMsgsUpdate;
import com.joymeng.slg.net.mod.chat.ModRoleGroupSend;
import com.joymeng.slg.net.mod.chat.ModRoleGroupUpdate;
import com.joymeng.slg.net.mod.chat.ModUnionChatMsgsSend;
import com.joymeng.slg.net.mod.chat.ModUnionChatMsgsUpdate;
import com.joymeng.slg.net.mod.chat.ModWorldChatMsgsSend;
import com.joymeng.slg.net.mod.chat.ModWorldChatMsgsUpdate;
import com.joymeng.slg.net.mod.chat.ModWorldNoticeMsgUpdate;
import com.joymeng.slg.union.UnionBody;
import com.joymeng.slg.union.impl.UnionMember;
import com.joymeng.slg.world.GameConfig;

/**
 * 聊天管理
 * @author houshanping
 *
 */
public class ChatManager implements Instances {	
	private static ChatManager instance = new ChatManager();
	public static ChatManager getInstance() {
		return instance;
	}
	public Map<Long,ChatMsg> worldMsgs = new ConcurrentHashMap<Long,ChatMsg>();//世界聊天内容保存	
	public Map<Long, UnionChatMsg> unionMsgs = new ConcurrentHashMap<Long, UnionChatMsg>(); //联盟聊天内容	
	public Map<Long, ChatGroup> groups = new ConcurrentHashMap<Long, ChatGroup>();//聊天群组的ID <groupId,roleUids>		
	public Map<Long, RoleChatMail> roleMail = new ConcurrentHashMap<Long, RoleChatMail>();//个人的邮箱 <uid ,邮箱列表>
	
	public PriorityQueue<NoticeMsg> worldNotices = new PriorityQueue<NoticeMsg>(11,new Comparator<NoticeMsg>() {
		@Override
		public int compare(NoticeMsg o1, NoticeMsg o2) {
			int numbera = o1.getPriorityLevel();
			int numberb = o2.getPriorityLevel();
			long timea = o1.getTime();
			long timeb = o2.getTime();
			return numberb == numbera ? (timea == timeb ? 0 : (timea < timeb ? -1 : 1)) : (numbera > numberb ? -1 : 1);
		}
	});//世界公告
	long noticeStart = TimeUtils.nowLong(); //世界公告消息的时间
	NoticeMsg priorNotice = new NoticeMsg();//用作记录上次所发生的公告
	
	public Map<Long, RoleChatMail> getRoleMail() {
		return roleMail;
	}
	
	public Map<Long, ChatMsg> getWorldMsgs() {
		return worldMsgs;
	}

	public void setWorldMsgs(Map<Long, ChatMsg> worldMsgs) {
		this.worldMsgs = worldMsgs;
	}
	
	public Map<Long, UnionChatMsg> getUnionMsgs() {
		return unionMsgs;
	}

	public void setUnionMsgs(Map<Long, UnionChatMsg> unionMsgs) {
		this.unionMsgs = unionMsgs;
	}

	public PriorityQueue<NoticeMsg> getWorldNotices() {
		return worldNotices;
	}

	public void setWorldNotices(PriorityQueue<NoticeMsg> worldNotices) {
		this.worldNotices = worldNotices;
	}

	public Map<Long, ChatGroup> getGroups() {
		return groups;
	}

	public void setGroups(Map<Long, ChatGroup> groups) {
		this.groups = groups;
	}

	public void setRoleMail(Map<Long, RoleChatMail> roleMail) {
		this.roleMail = roleMail;
	}

	public long getNoticeStart() {
		return noticeStart;
	}

	public void setNoticeStart(long noticeStart) {
		this.noticeStart = noticeStart;
	}

	/**
	 * 获取聊天组
	 * @param groupId
	 * @return
	 */
	public ChatGroup getChatGroupByGroupId(long groupId){
		return groups.get(groupId);
	}
	
	/**
	 * 创建一个组
	 * @param uids
	 */
	public synchronized void addGroupChat(Role role,List<Long> uids){
		long groupId = chatKeyData.getGroupId();
		List<ChatRole> roles = new ArrayList<ChatRole>();
		for (int i = 0 ; i < uids.size() ; i++){
			long roleUid = uids.get(i).longValue();
			Role temp = world.getRole(roleUid);
			if (temp == null) {
				continue;
			}
			ChatAgent chatAgent = temp.getChatAgent();
			Map<Long, byte[]> roleChatGroup = chatAgent.getChat_groups();			
			roleChatGroup.put(groupId, new byte[]{0,0});
			ChatRole chatRole = new ChatRole(temp);
			roles.add(chatRole);
		}
		ChatGroup group = new ChatGroup(groupId, roles, uids.get(0));
		groups.put(groupId, group);
		SendRoleGroupsUpdate(group);	//下发组更新消息
		String systemChatPara = "";
		for (int i = 0 ; i < roles.size() ; i++){
			ChatRole chatRole= roles.get(i);
			if (role.getId() == chatRole.getUid()) {
				continue;
			}
			systemChatPara += chatRole.getName() + " ";
		}
		systemChatPara.substring(0, systemChatPara.length() - 1);
		ChatSystemContent systemContent = new ChatSystemContent(ChatSystemContentType.CHAT_CONTENT_HAS_JOINED_SESSION, systemChatPara);
		String result = JsonUtil.ObjectToJsonString(systemContent);
		ChatMsg msg = generateOneMsgsToGroup(group,result);	
		sendGroupChatMsgsUpdate(group, msg);//发送一条提示消息
	}
	
	/**
	 * 创建一个组
	 * @param role
	 * @param uids
	 * @return
	 */
	public synchronized boolean newGroupChat(Role role, List<Long> uids){
		uids = StringUtils.removeDuplicateWithOrder(uids);//移除重复项
		if (uids.size() < 2) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_CREATE_FAIL_LAST_NEED_ONE);
			return false;
		}
		addGroupChat(role,uids);
		return true;
	}
	
	/**
	 * 创建一条组创建成功系统提示消息
	 * 
	 * @param currentGroupId
	 * @param uids
	 * @return ChatMsg
	 */
	public synchronized ChatMsg generateOneMsgsToGroup(ChatGroup group, String chatContent) {
		ChannelType channelType = ChannelType.GROUP; // 频道为聊天组频道
		ChatMsg msg = new ChatMsg(chatContent, MsgTextColorType.COLOR_BLACK, channelType, MsgType.TYPE_SYSTEM, (byte) 0,
				null, null);
		msg.setGroupId(group.getId()); // 设置消息的组的Id
		return msg;
	}
	
	/**
	 * 创建并发送一条联盟系统消息
	 * @param unionId
	 * @param chatContent
	 */
	public synchronized void generateOneMsgsToUnionAndSend(long unionId, String chatContent) {
		ChannelType channelType = ChannelType.GUILD; // 频道为聊天组频道
		ChatMsg msg = new ChatMsg(chatContent,MsgTextColorType.COLOR_BLACK,  channelType, MsgType.TYPE_SYSTEM, (byte) 0, null, null);
		addUnionChat(unionId, msg);
		UnionBody unionBody = world.getObject(UnionBody.class, unionId);
		if (unionBody != null) {
			sendUnionChatMsgsUpdate(unionBody, msg);
		}
	}
	
	/**
	 * 添加世界聊天消息
	 * @param msg
	 */
	public synchronized void addWorldChat(ChatMsg msg){
		long worldMsgsId = chatKeyData.getWorldMsgsId();
		msg.setId(worldMsgsId);
		if (worldMsgsNum() >= GameConfig.WORLD_CHAT_MES_MAX_NUM) {
			long removeId = chatKeyData.worldMsgHeadIncrement();
			worldMsgs.remove(removeId);
		}
		worldMsgs.put(worldMsgsId, msg);
	}

	/**
	 * 获取当前世界聊天记录个数
	 * @return
	 */
	public int worldMsgsNum() {
		return worldMsgs.size();
	}

	/**
	 * 获取当前世界聊天的  最大的ID
	 * @return
	 */
	public long worldMsgsIdMax() {
		return chatKeyData.getWorldMsgsIdMax();
	}
	
	/**
	 * 打包min-max获取区间内的世界聊天记录列表
	 * @param worldMsgsIdMin
	 * @param worldMsgsIdMax
	 * @return rms
	 */
	public RespModuleSet getWorldChat(long worldMsgsIdMin,long worldMsgsIdMax){
		List<ChatMsg> tempChatMsgs = new ArrayList<ChatMsg>();
		for (long index = worldMsgsIdMax; index >= worldMsgsIdMin; index--) {
			tempChatMsgs.add(worldMsgs.get(index));
		}		
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(new ModWorldChatMsgsSend((ChatMsg[])tempChatMsgs.toArray(new ChatMsg[tempChatMsgs.size()])));		
		return rms;
	}
	
	/**
	 * 打包min-max获取区间内的联盟聊天记录列表
	 * @param unionMsgsIdMin
	 * @param unionMsgsIdMax
	 * @return rms
	 */
	public RespModuleSet getUnionChat(Long unionId,long unionMsgsIdMin,long unionMsgsIdMax){
		List<ChatMsg> tempChatMsgs = new ArrayList<ChatMsg>();
		UnionChatMsg unionChatMsg = unionMsgs.get(unionId);	
		RespModuleSet rms = new RespModuleSet();
		if (unionChatMsg == null) {
			GameLog.error("read unionMsg is fail! ");
			unionMsgsIdMax = -1;
		}
		for (long index = unionMsgsIdMax; index >= unionMsgsIdMin; index--) {			
			tempChatMsgs.add(unionChatMsg.getUnionMsgs().get(index));
		}		
		rms.addModule(new ModUnionChatMsgsSend((ChatMsg[])tempChatMsgs.toArray(new ChatMsg[tempChatMsgs.size()])));
		return rms;
	}
	
	/**
	 * 首次登陆发送的消息
	 * @return
	 */
	public boolean firstSendMsgs(Role role) {
		SendOneSuitWorldMsgs(role,worldMsgsIdMax(),10);
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody != null) {
			SendOneSuitUnionMsgs(role,role.getUnionId(),chatKeyData.getUnionMsgsIdMax(role.getUnionId()),10);			
		}
		SendRoleGroupToClient(role);
		SendRoleMailMsgs(role);
		return true;
	}

	/**
	 * 发送一组世界的消息
	 * @param role
	 * @param worldMsgsIdMax
	 * @param msgsCount
	 * @return
	 */
	public boolean SendOneSuitWorldMsgs(Role role,long worldMsgsIdMax ,int msgsCount) {
		long worldMsgsIdMin = worldMsgsIdMax > msgsCount ? worldMsgsIdMax - msgsCount : 1;
		if(worldMsgsIdMax > worldMsgsIdMin){
			RespModuleSet rms = getWorldChat(worldMsgsIdMin, worldMsgsIdMax);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			return true;
		}
		return false;
	}

	/**
	 * 发送一组联盟的消息
	 * @param role
	 * @param unionId
	 * @param worldMsgsIdMax
	 * @param msgsCount
	 * @return
	 */
	public boolean SendOneSuitUnionMsgs(Role role,long unionId,long worldMsgsIdMax ,int msgsCount) {
		long worldMsgsIdMin = worldMsgsIdMax > msgsCount ? worldMsgsIdMax - msgsCount : 1;
		if(worldMsgsIdMax > worldMsgsIdMin){
			RespModuleSet rms = getUnionChat(unionId, worldMsgsIdMin, worldMsgsIdMax);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
			return true;
		}
		return false;
	}
	
	/**
	 * 发送讨论组消息到客户端
	 * @param role
	 */
	public void SendRoleGroupToClient(Role role){
		ChatAgent chatAgent = role.getChatAgent();
		List<ChatGroup> roleGroups = new ArrayList<ChatGroup>();
		RespModuleSet rms = new RespModuleSet();		
		if (chatAgent != null) {
			Map<Long, byte[]> groupsMap = chatAgent.getChat_groups();
			for (Long roleGroupId : groupsMap.keySet()) {
				ChatGroup tempRoleGroup = getChatGroupByGroupId(roleGroupId);
				if (tempRoleGroup != null) {
					roleGroups.add(tempRoleGroup);
				}
			}
			rms.addModule(new ModRoleGroupSend((ChatGroup[])roleGroups.toArray(new ChatGroup[roleGroups.size()])));
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}

	/**
	 * 发送role邮箱的所有消息
	 * @param role
	 * @param worldMsgsIdMax
	 * @return
	 */
	public boolean SendRoleMailMsgs(Role role) {
		RoleChatMail roleChatMail =  roleMail.get(role.getId());
		List<ChatMsg> groupMsgLists = new ArrayList<>();
		List<ChatMsg> reportMsgLists = new ArrayList<>();
		if (roleChatMail != null) {
			for (ChatMsg temp : roleMail.get(role.getId()).getRoleChatMails().values()) {
				if (temp.getReportType() >= 0 && temp.getReportType() <= 2) {
					groupMsgLists.add(temp);
				} else {
					reportMsgLists.add(temp);
				}
			}
			ChatMsg[] groupMsgList =(ChatMsg[]) groupMsgLists.toArray(new ChatMsg[groupMsgLists.size()]);			
 			ChatMsg[] reportMsgList = (ChatMsg[]) reportMsgLists.toArray(new ChatMsg[reportMsgLists.size()]);	
			if (groupMsgList != null || reportMsgList != null) {
				RespModuleSet rms = new RespModuleSet();
				if (groupMsgList.length > 0) {
					rms.addModule(new ModGroupChatMsgsSend(groupMsgList));					
				}
				if (reportMsgList.length > 0) {
					rms.addModule(new ModBattleReportSend(reportMsgList));
				}
				MessageSendUtil.sendModule(rms, role.getUserInfo());
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 发送世界更新消息
	 * @param msg
	 */
	public void sendWorldChatMsgsUpdate(ChatMsg msg){
		List<Role> roles = world.getOnlineRoles();
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(new ModWorldChatMsgsUpdate(msg));
		for (int i = 0 ; i < roles.size() ; i++){
			Role role = roles.get(i);
			MessageSendUtil.sendModule(rms, role.getUserInfo());
		}
	}
	
	/**
	 * 发送联盟更新消息
	 * @param msg
	 */
	public void sendUnionChatMsgsUpdate(UnionBody unionBody,ChatMsg msg){
		List<UnionMember> unionMembers = unionBody.getMembers();		
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(new ModUnionChatMsgsUpdate(msg));
		for (int i = 0 ; i < unionMembers.size() ; i++){
			UnionMember unionMember = unionMembers.get(i);
			Role role = world.getOnlineRole(unionMember.getUid());
			if (role != null) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());				
			}
		}
	}
	
	/**
	 * 将报告发送给指定用户
	 * @param battleReportContent
	 * @param role
	 */
	public void creatBattleReportAndSend(String reportContent, byte reportType, Role role, Role otherRole) {
		RespModuleSet rms = new RespModuleSet();
		ChannelType channelType = ChannelType.SYSTEM_REPORT;
		ChatMsg msg = new ChatMsg();
		if (role == null) {
			msg = new ChatMsg(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,reportContent,MsgTextColorType.COLOR_BLACK,channelType, MsgType.TYPE_SYSTEM, reportType, null, null);
		} else {
			ChatRole sender = new ChatRole(role);
			msg = new ChatMsg(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,reportContent,MsgTextColorType.COLOR_BLACK, channelType, MsgType.TYPE_SYSTEM, reportType, sender, null);
		}
		RoleChatMail roleMails = roleMail.get(otherRole.getId());
		if (roleMails == null) {
			roleMails = new RoleChatMail();
			roleMail.put(otherRole.getId(), roleMails);
		}
		roleMails.addRoleMail(msg);
		rms.addModule(new ModBattleReportUpdate(msg));
		if (otherRole.isOnline()) { // 在线,直接发给用户
			MessageSendUtil.sendModule(rms, otherRole.getUserInfo());
		}
		String mailType = "";
		String sender = "";
		if(role==null){
			mailType="系统邮件";
		    sender ="system";
		}else{
			mailType="联盟群体邮件";
			UnionBody unionBody = unionManager.search(role.getUnionId());
			sender =unionBody.getShortName();
		}
		LogManager.mailLog(otherRole, mailType, "收",sender,String.valueOf(otherRole.getId()));
	}
	
	/**
	 * 发送系统邮件
	 * @param reportContent
	 * @param uid
	 */
	public void creatSystemEmail(String reportContent , long uid) {
		RespModuleSet rms = new RespModuleSet();
		ChannelType channelType = ChannelType.SYSTEM_REPORT;
		ChatMsg msg = new ChatMsg(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,reportContent,MsgTextColorType.COLOR_BLACK,channelType, MsgType.TYPE_SYSTEM,ReportType.TYPE_SYSTEM_MAIL,null,null);
		RoleChatMail roleMails = roleMail.get(uid);
		if (roleMails == null) {
			roleMails = new RoleChatMail();
			roleMail.put(uid, roleMails);
		}
		roleMails.addRoleMail(msg);
		rms.addModule(new ModBattleReportUpdate(msg));
		Role role = world.getOnlineRole(uid);
		if (role != null) { // 在线,直接发给用户
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
	}
	
	/**
	 * 发送系统邮件
	 * @param reportContent
	 * @param annex
	 * @param uid
	 */
	public void creatSystemEmail(String reportContent ,List<BriefItem> annex, long uid) {
		creatSystemEmail(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM,reportContent,annex,uid);
	}
	
	/**
	 * 发送带标题的系统奖励邮件
	 * @param title
	 * @param reportContent
	 * @param annex
	 * @param uid
	 */
	public void creatSystemEmail(String title,String reportContent ,List<BriefItem> annex, long uid) {
		RespModuleSet rms = new RespModuleSet();
		ChannelType channelType = ChannelType.SYSTEM_REPORT;
		ChatMsg msg = new ChatMsg(title,reportContent,MsgTextColorType.COLOR_BLACK,channelType, MsgType.TYPE_SYSTEM,ReportType.TYPE_SYSTEM_MAIL,null,null);
		msg.setMsgAnnex(annex);
		RoleChatMail roleMails = roleMail.get(uid);
		if (roleMails == null) {
			roleMails = new RoleChatMail();
			roleMail.put(uid, roleMails);
		}
		roleMails.addRoleMail(msg);
		rms.addModule(new ModBattleReportUpdate(msg));
		Role role = world.getOnlineRole(uid);
		if (role != null) { 
			MessageSendUtil.sendModule(rms,role.getUserInfo());
		}
	}
	
	/**
	 * 发送一个条组更新消息
	 * @param groupId
	 * @param msg
	 */
	public void sendGroupChatMsgsUpdate(ChatGroup group,ChatMsg tempMsg){
		if (group == null) {
			return;
		}
		List<ChatRole> roles = group.getRoles();
		for (int i = 0 ; i < roles.size() ; i++){
			ChatRole chatRole = roles.get(i);
			ChatMsg msg = tempMsg.copy();
			Role role = world.getRole(chatRole.getUid());
			RoleChatMail roleMails = roleMail.get(role.getId());
			if (roleMails == null) {
				roleMails = new RoleChatMail();
				roleMail.put(role.getId(),roleMails);
			}
			roleMails.addRoleMail(msg);
			RespModuleSet rms = new RespModuleSet();
			rms.addModule(new ModGroupChatMsgsUpdate(msg));
			if(role.isOnline()){	//在线,直接发给用户
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}
	
	/**
	 * 从组中删除UId的用户
	 * @param group
	 * @param uid
	 * @return
	 */
	public synchronized boolean removeRoleFromGroup(ChatGroup group,long uid){
		List<ChatRole> roles = group.getRoles();
		for (int index = 0; index < roles.size(); ++index) {
			ChatRole chatRole = roles.get(index);
			if (chatRole.getUid() == uid) {
				roles.remove(index);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 退出讨论组
	 * @param role
	 * @param groupId
	 * @return
	 */
	public synchronized boolean exitChatGroup(Role role , long groupId){
		ChatGroup group = getChatGroupByGroupId(groupId);	
		if (group == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NOT_EXIST, groupId);
			return false;
		}
		List<ChatRole> roles = group.getRoles();
		if (roles.size() <= 1) { // 如果群人数小于1时.直接移除讨论组
			ChatAgent chatAgent = role.getChatAgent();
			if (chatAgent != null) {
				Map<Long, byte[]> roleGroups = chatAgent.getChat_groups();
				roleGroups.remove(groupId);
				SendRoleGroupToClient(role);
			}
			groups.remove(groupId);
			return true;
		} else {
			if (!removeRoleFromGroup(group, role.getId())) {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NOT_EXIST);
				return false;
			}
			ChatAgent chatAgent = role.getChatAgent();
			if (chatAgent != null) {
				Map<Long, byte[]> roleGroups = chatAgent.getChat_groups();
				roleGroups.remove(groupId);
				String systemChatPara = role.getName();
				ChatSystemContent systemContent = new ChatSystemContent(ChatSystemContentType.CHAT_CONTENT_EXIT_GROUP, systemChatPara);
				String result = JsonUtil.ObjectToJsonString(systemContent);
				ChatMsg msg = generateOneMsgsToGroup(group,result);	
				SendRoleGroupsUpdate(group);	//下发组更新消息
				sendGroupChatMsgsUpdate(group, msg);//发送一条提示消息
				return true;
			} else {
				MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NOT_EXIST);
				return false;
			}
		}
	}
	
	/**
	 * 修改讨论组的名称
	 * @param role
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public synchronized boolean changeGroupName(Role role, long groupId, String groupName) {
		ChatGroup chatGroup = groups.get(groupId);
		if (chatGroup == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NOT_EXIST);
			return false;
		}
		if (chatGroup.getRoles() == null || chatGroup.getRoles().size() < 3) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NO_CHANGE_NAME);
			return false;
		}
		if (!isRoleInGroup(role.getId(), chatGroup)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_EXCLUDE_ROLE);
			return false;
		}
		chatGroup.setName(groupName); // 修改组名称
		SendRoleGroupsUpdate(chatGroup);
		String systemChatPara = role.getName() + "|" + groupName;
		ChatSystemContent systemContent = new ChatSystemContent(ChatSystemContentType.CONTENT_TYPE_GROUP_CHANGE_NAME, systemChatPara);
		String result = JsonUtil.ObjectToJsonString(systemContent);
		ChatMsg msg = generateOneMsgsToGroup(chatGroup,result);	
		sendGroupChatMsgsUpdate(chatGroup, msg);//发送一条提示消息
		return true;
	}

	/**
	 * 用户在聊天组中
	 * @param uid
	 * @param chatGroup
	 * @return
	 */
	public boolean isRoleInGroup(long uid,ChatGroup chatGroup) {
		if (chatGroup == null) {
			return false;
		}
		List<ChatRole> roles = chatGroup.getRoles();
		if (roles == null) {
			return false;
		}
		for (int i = 0 ; i < roles.size() ; i++){
			ChatRole chatRole = roles.get(i);
			if (chatRole.getUid() == uid) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 邀请加入组
	 * @param role
	 * @param groupId
	 * @param rolesList
	 * @return
	 */
	@SuppressWarnings("static-access")
	public synchronized boolean inviteRolesTojoinGroup(Role role,long groupId ,List<Long> roles) {
		if (roles.size() < 1) {
			return false;
		}
		ChatGroup chatGroup = chatMgr.getChatGroupByGroupId(groupId);
		if (chatGroup == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_NOT_EXIST);
			return false;
		}
		if (!isRoleInGroup(role.getId(), chatGroup)) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_EXCLUDE_ROLE);
			return false;
		}
		List<ChatRole> currentRoles = chatGroup.getRoles();
		if (currentRoles.size() + roles.size() > chatGroup.MAX_NUM) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.CHAT_GROUP_BEYOND_ROLE_NUM_LIMIT);
			return false;
		}
		if (currentRoles.size() == 2) { //两人聊天邀请人加入时创建新的讨论组
			List<Long> uids = new ArrayList<>();
			for (int i = 0 ; i < currentRoles.size() ; i++){
				ChatRole chatRole = currentRoles.get(i);
				uids.add(chatRole.getUid());
			}
			uids.addAll(roles);
			newGroupChat(role, uids);
		} else {	//大于两人，直接加入
			String chatContent = "";
			for (int i = 0 ; i < roles.size() ; i++){
				Long tempUid = roles.get(i).longValue();
				Role tempRole = world.getRole(tempUid);
				if (tempRole == null) {
					continue;
				}
				ChatRole temp = getChatRoleFromChatGroupByUid(currentRoles, tempUid);
				if (temp != null) {
					continue;
				}
				if (role.getId() != tempUid) {
					chatContent += tempRole.getName() + " ";
				}
				ChatAgent chatAgent = tempRole.getChatAgent();
				Map<Long, byte[]> roleChatGroup = chatAgent.getChat_groups();
				roleChatGroup.put(groupId, new byte[] { 0, 0 });
				ChatRole chatRole = new ChatRole(tempRole);
				currentRoles.add(chatRole);
			}
			SendRoleGroupsUpdate(chatGroup); // 发送组消息的更新
			chatContent.subSequence(0, chatContent.length() - 1);
			String systemChatPara = chatContent;
			ChatSystemContent systemContent = new ChatSystemContent(
					ChatSystemContentType.CHAT_CONTENT_HAS_JOINED_SESSION, systemChatPara);
			String result = JsonUtil.ObjectToJsonString(systemContent);
			ChatMsg msg = generateOneMsgsToGroup(chatGroup, result);
			sendGroupChatMsgsUpdate(chatGroup, msg);// 发送一条提示消息
		}
		return true;
	}

	/**
	 * 根据Uid获取群组中Uid=Uid 的用户
	 * @param currentRoles
	 * @param tempUid
	 * @return 
	 */
	private ChatRole getChatRoleFromChatGroupByUid(List<ChatRole> currentRoles, Long tempUid) {
		for (int i = 0 ; i < currentRoles.size() ; i++){
			ChatRole chatRole = currentRoles.get(i);
			if (chatRole.getUid() == tempUid) {
				return chatRole;
			}
		}
		return null;
	}

	/**
	 * 发送组消息更新
	 * @param chatGroup
	 */
	public void SendRoleGroupsUpdate(ChatGroup chatGroup) {
		if (chatGroup == null) {
			return;
		}
		RespModuleSet rms = new RespModuleSet();
		rms.addModule(new ModRoleGroupUpdate(chatGroup));
		List<ChatRole> roles = chatGroup.getRoles();
		for (int i = 0 ; i < roles.size() ; i++){
			ChatRole chatRole = roles.get(i);
			Role role  = world.getRole(chatRole.getUid());
			if (role == null) {
				continue;
			}
			if (role.isOnline()) {
				MessageSendUtil.sendModule(rms, role.getUserInfo());
			}
		}
	}
	
	/**
	 * 移除本地缓存的数据
	 * @param role
	 * @param uids
	 * @return
	 */
	public synchronized boolean removeRoleMailMsgs(Role role,List<Long> roleMailMsgIds) {
		RoleChatMail roleChatMail = roleMail.get(role.getId());
		if (roleChatMail == null) {
			GameLog.error("get role mail mags is fail from roleMail");
			return false;
		}
		Map<Long, ChatMsg> roleMailMsgsMap = roleChatMail.getRoleChatMails();
		if (roleMailMsgsMap == null || roleMailMsgsMap.size() < 1) {
			GameLog.error("get role roleMailMsgsMap is fail from roleChatMail");
			return false;
		}
		List<Long> result = new ArrayList<>();
		for (int i = 0 ; i < roleMailMsgIds.size() ; i++){
			Long tempRoleMailMsgIds = roleMailMsgIds.get(i);
			if (roleMailMsgsMap.size() > 0) {
				for (Long tempId : roleMailMsgsMap.keySet()) {
					ChatMsg tempChatMsg = roleMailMsgsMap.get(tempId);
					if (tempChatMsg == null) {
						continue;
					}
					if (tempChatMsg.getId() == tempRoleMailMsgIds) {
						result.add(tempId);
					}
				}
			}
		}
		for (int i = 0 ; i < result.size() ; i++){
			Long tempId = result.get(i);
			roleMailMsgsMap.remove(tempId);
		}
		chatDataManager.updataRoleMail(role.getId());
		return true;
	}

	/**
	 * 添加联盟消息
	 * @param unionId
	 * @param msg
	 */
	public synchronized void addUnionChat(long unionId, ChatMsg msg) {
		UnionChatMsg unionChatMsg = unionMsgs.get(unionId);
		if (unionChatMsg == null) {
			unionChatMsg = new UnionChatMsg();
			unionChatMsg.setUnionId(unionId);
			unionMsgs.put(unionId, unionChatMsg);
		}
		unionChatMsg.addUnionChat(unionId, msg);
		//System.out.println("Add After:" + unionMsgs.get(unionId).getUnionMsgs().size());
	}
	
	/**
	 * 添加组消息
	 * @param unionId
	 * @param msg
	 */
	public synchronized void addGroup(long groupId, ChatGroup chatGroup) {
		groups.put(groupId, chatGroup);
	}
	
	/**
	 * 添加用户邮箱
	 * @param unionId
	 * @param msg
	 */
	public synchronized void addRoleMail(long uid, RoleChatMail resultRoleChatMail) {
		roleMail.put(uid, resultRoleChatMail);
	}

	/**
	 * 更新世界 联盟的用户名称
	 * @param role
	 */
	public synchronized void updateRoleName(Role role) {
		//更新世界的消息集
		for (ChatMsg tempMsg : worldMsgs.values()) {
			if (tempMsg == null) {
				continue;
			}
			ChatRole sender = tempMsg.getSender();
			if (sender != null && sender.getUid() == role.getId()){
				sender.update(role);
			}
		}
		// 更新联盟的消息集
		if (role.getUnionId() != 0) {
			UnionChatMsg unionChatMsgs = unionMsgs.get(role.getUnionId());
			if (unionChatMsgs != null && unionChatMsgs.getUnionMsgs() != null
					&& unionChatMsgs.getUnionMsgs().size() > 0) {
				for (ChatMsg tempMsg : unionChatMsgs.getUnionMsgs().values()) {
					if (tempMsg == null) {
						continue;
					}
					ChatRole sender = tempMsg.getSender();
					if (sender != null && sender.getUid() == role.getId()) {
						sender.update(role);
					}
				}
			}
		}
		//下发数据通知客户端更新相关数据
	}

	/**
	 * 给联盟发送全体邮件
	 * @param role
	 * @param msgText
	 */
	public void sendUnionNotice(Role role, String msgText) {
		UnionBody unionBody = unionManager.search(role.getUnionId());
		if (unionBody == null) {
			MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_NO_JOIN_UNION);
			return;
		}
		List<UnionMember> allUnionMember = unionBody.getMembers();
		for (int i = 0 ; i < allUnionMember.size() ; i++){
			UnionMember unionMember = allUnionMember.get(i);
			if (unionMember == null) {
				continue;
			}
			Role tempRole = world.getRole(unionMember.getUid());
			if (tempRole == null) {
				continue;
			}
			creatBattleReportAndSend(msgText, ReportType.TYPE_UNION_NOTICE, role, tempRole);
		}
		MessageSendUtil.sendNormalTip(role.getUserInfo(), I18nGreeting.MSG_SEND_ALL_MEMBERS_MSG_SUC);
		LogManager.mailLog(role, "联盟群体邮件", "发",String.valueOf(role.getId()),unionBody.getShortName());
	}
	
	/**
	 * 加入一条普通公告消息(公告的优先级为最低)
	 * @param msg
	 */
	public void addWorldNotice(ChatMsg msg) {
		synchronized (worldNotices) {
			NoticeMsg noticeMsg = new NoticeMsg(0, TimeUtils.nowLong(), msg);
			worldNotices.add(noticeMsg);
		}
	}
	
	/**
	 * 添加公告消息
	 * @param noticeMsg
	 */
	public void addWorldNotice(NoticeMsg noticeMsg) {
		synchronized (worldNotices) {
			worldNotices.add(noticeMsg);
		}
	}
	/**
	 * 发送一条系统公告消息(优先展示)
	 * @param msg
	 */
	public void addSystemNotice(ChatMsg msg,int priorityLevel){
		synchronized (worldNotices) {
			NoticeMsg noticeMsg = new NoticeMsg(priorityLevel, TimeUtils.nowLong(), msg);
			worldNotices.add(noticeMsg);
			if (priorNotice != null && noticeMsg.getPriorityLevel() > priorNotice.getPriorityLevel()) {
				worldNotices.add(priorNotice);
				noticeStart = 0; // 让头数据立刻下发
			}
		}
	}
	
	/**
	 * 开服的时候下发一条公告消息
	 * @param rms
	 * @param msg
	 */
	public boolean sendHeadWorldNotice(RespModuleSet rms) {
		synchronized (worldNotices) {
			NoticeMsg noticeMsg = worldNotices.poll();
			if (noticeMsg == null || noticeMsg.getMsg() == null) {
				return false;
			}
			priorNotice = noticeMsg;
			rms.addModule(new ModWorldNoticeMsgUpdate(noticeMsg.getMsg()));
			return true;
		}
	}
	
	/**
	 * 定期向所有在线的用户 发送公告消息
	 */
	public boolean sendHeadWorldNoticeToAllOnlineRoles() {
		// 刷新公告信息
		synchronized (worldNotices) {
			if (worldNotices.size() > 0
					&& TimeUtils.nowLong() - noticeStart >= GameConfig.WORLD_NOTICE_REFRESH_TIME * 1000) {
				RespModuleSet rms = new RespModuleSet();
				if (!sendHeadWorldNotice(rms)) {
					return false;
				}
				List<Role> allRoles = world.getOnlineRoles();
				for (int i = 0 ; i < allRoles.size() ; i++){
					Role role = allRoles.get(i);
					if (role == null || !role.isOnline()) {
						continue;
					}
					MessageSendUtil.sendModule(rms, role);
				}
				noticeStart = TimeUtils.nowLong();
				return true;
			}
			if (worldNotices.size() <= 0 && priorNotice != null
					&& TimeUtils.nowLong() - noticeStart >= GameConfig.WORLD_NOTICE_REFRESH_TIME * 1000) {
				priorNotice = null;
			}
			return false;
		}
	}
	
	/**
	 * 发送一条系统聊天消息
	 * @param reportContent
	 * @param aimRole
	 */
	public synchronized void generateSystemMsgsToRole(String reportContent, String msgColor, Role aimRole) {
		RespModuleSet rms = new RespModuleSet();
		ChannelType channelType = ChannelType.WORLD;
		ChatMsg msg = new ChatMsg(reportContent, msgColor, channelType, MsgType.TYPE_SYSTEM_MSG, (byte) 0, null, null);
		RoleChatMail roleMails = roleMail.get(aimRole.getId());
		if (roleMails == null) {
			roleMails = new RoleChatMail();
			roleMail.put(aimRole.getId(), roleMails);
		}
		roleMails.addRoleMail(msg);
		rms.addModule(new ModBattleReportUpdate(msg));
		if (aimRole.isOnline()) { // 在线,直接发给用户
			MessageSendUtil.sendModule(rms, aimRole.getUserInfo());
		}
	}
	
	/**
	 * 发送StringContent的公告
	 * @param content
	 * @param params
	 */
	public void addStringContentNotice(int priorityLevel,boolean needSendWorld, String stringContent, Object... params) {
		String systemChatPara = "";
		for (int i = 0; i < params.length; i++) {
			systemChatPara += params[i] + "|";
		}
		if (systemChatPara.length() > 1) {
			systemChatPara = systemChatPara.substring(0, systemChatPara.length() - 1);
		}
		ChatSystemContent chatSystemContent = new ChatSystemContent(stringContent, systemChatPara);
		String content = JsonUtil.ObjectToJsonString(chatSystemContent);
		ChatRole sys = new ChatRole();
		sys.setUid(-1L);
		sys.setName("系统公告");
		ChatMsg msg = new ChatMsg(("5" + content), MsgTextColorType.COLOR_BLACK, ChannelType.MAIL_SYSTEM, (byte) 3,
				(byte) 0, sys, null);
		// 添加世界公告
		addSystemNotice(msg, priorityLevel);
		// 发送世界聊天频道
		if (needSendWorld) {
			addWorldChat(msg);
			sendWorldChatMsgsUpdate(msg);
		}
	}
}

