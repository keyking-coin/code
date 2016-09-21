package com.joymeng.slg.domain.chat;

import java.util.ArrayList;
import java.util.List;

import com.joymeng.common.util.TimeUtils;
import com.joymeng.slg.domain.object.bag.BriefItem;

/**
 * 聊天信息封装
 * @author houshanping
 *
 */
public class ChatMsg implements Cloneable {
	private long id; // 消息编号
	private ChannelType channelType;// 聊天类型
	private String msgTitle = "no_title";//消息的标题
	private String msg = "";// 聊天内容   头标志(一个字符):0:普通消息/公告消息  1:战报分享  2:侦查报告分享 3:需要读StringContent的公告消息
	private List<BriefItem> msgAnnex = new ArrayList<>();//内容的附件
	private String msgColor = "000000";
	private byte msgType;// 聊天类型 0：普通文字聊天 1:语音消息 2:系统消息 3:公告消息 4:联盟邮件 5:公告联盟邀请 6:系统聊天 7:红包
	private byte reportType; //2联盟全体邮件 3 联盟邀请 //4 侦查报告//5 战斗报告//6 迁城邀请//7 系统邮件//8 敌人或资源采集
	private ChatRole sender = new ChatRole();// 发送者,若为 null,则表示系统信息
	private ChatRole receiver = new ChatRole();// 接受者
	private long sendDate = TimeUtils.nowLong() / 1000;// 发送时间
	private long groupId = 0;//

	public ChatMsg() {

	}

	public ChatMsg(String msg, String msgColor, ChannelType channel, MsgType msgType, ReportType reportType, ChatRole sender,
			ChatRole receiver) {
		this.msg = msg;
		this.msgColor = msgColor;
		this.channelType = channel;
		this.msgType = msgType.getKey();
		this.reportType = reportType.getKey();
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public ChatMsg(String msgTitle, String msg, String msgColor, ChannelType channel, MsgType msgType, ReportType reportType,
			ChatRole sender, ChatRole receiver) {
		this.msgTitle = msgTitle;
		this.msg = msg;
		this.msgColor = msgColor;
		this.channelType = channel;
		this.msgType = msgType.getKey();
		this.reportType = reportType.getKey();
		this.sender = sender;
		this.receiver = receiver;
	}

	public ChatMsg(long id, ChannelType channelType, String msg, String msgColor, MsgType msgType, ReportType reportType,
			ChatRole sender, ChatRole receiver, long sendDate, long groupId) {
		this.id = id;
		this.channelType = channelType;
		this.msg = msg;
		this.msgColor = msgColor;
		this.msgType = msgType.getKey();
		this.reportType = reportType.getKey();
		this.sender = sender;
		this.receiver = receiver;
		this.sendDate = sendDate;
		this.groupId = groupId;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

	public String getMsgTitle() {
		return msgTitle;
	}

	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}

	public byte getReportType() {
		return reportType;
	}

	public void setReportType(byte reportType) {
		this.reportType = reportType;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public List<BriefItem> getMsgAnnex() {
		return msgAnnex;
	}

	public void setMsgAnnex(List<BriefItem> msgAnnex) {
		this.msgAnnex = msgAnnex;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ChatRole getSender() {
		return sender;
	}

	public void setSender(ChatRole sender) {
		this.sender = sender;
	}

	public ChatRole getReceiver() {
		return receiver;
	}

	public void setReceiver(ChatRole receiver) {
		this.receiver = receiver;
	}

	/**
	 * 是否系统类型
	 * 
	 * @return
	 */
	public boolean isSys() {
		return sender == null;
	}

	public String getToClientMsg() {
		return msg;
	}

	public String getMsgColor() {
		return msgColor;
	}

	public void setMsgColor(String msgColor) {
		this.msgColor = msgColor;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSendDate() {
		return sendDate;
	}

	public void setSendDate(long sendDate) {
		this.sendDate = sendDate;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public ChatMsg copy() {
		ChatMsg msg = new ChatMsg();
		msg.setChannelType(channelType);
		msg.setMsgType(msgType);
		msg.setGroupId(groupId);
		msg.setId(id);
		msg.setMsg(this.msg);
		msg.setMsgColor(msgColor);
		msg.setReportType(reportType);
		msg.setReceiver(receiver);
		msg.setSendDate(sendDate);
		msg.setSender(sender);
		return msg;
	}

	/**
	 * 增加一个附件
	 * @param briefItem
	 */
	public void addMsgAnnex(BriefItem briefItem) {
		msgAnnex.add(briefItem);
	}
}
