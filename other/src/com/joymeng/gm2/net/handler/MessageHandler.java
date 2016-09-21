package com.joymeng.gm2.net.handler;

import org.apache.mina.core.session.IoSession;

import com.joymeng.gm2.net.message.request.MessageRequest;
import com.joymeng.log.GameLog;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgType;
import com.joymeng.slg.domain.chat.ReportType;

public class MessageHandler extends AbstractProtocolHandler<MessageRequest>{

	@Override
	protected void _handle(IoSession session, MessageRequest message) {
		// TODO Auto-generated method stub
		ChannelType channelType = ChannelType.valueOf(message.getChatType());
		MsgType msgType = MsgType.search(message.getMsgType());
		if (msgType == null) {
			GameLog.error("MsgType.search is fail , msgTypeKey = " + message.getMsgType());
			//resp.fail();
			//return resp;
		}
		ChatMsg msg = new ChatMsg(message.getContent(), MsgTextColorType.COLOR_BLACK, channelType, msgType, ReportType.TYPE_DEFAULT, null,
				null);
		
		chatMgr.addWorldChat(msg);
		chatMgr.sendWorldChatMsgsUpdate(msg);
		//LogManager.chatLog(role, groupType, msg.getMsgType(), msg.getMsg());
		//role.getChatAgent().setLastWorldChatMsgDate(TimeUtils.nowLong());
	}
	
}
