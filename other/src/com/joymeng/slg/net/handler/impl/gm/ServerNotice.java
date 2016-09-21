package com.joymeng.slg.net.handler.impl.gm;

import com.joymeng.http.HtppOprateType;
import com.joymeng.services.core.buffer.JoyBuffer;
import com.joymeng.services.core.message.JoyNormalMessage.UserInfo;
import com.joymeng.services.core.message.JoyProtocol;
import com.joymeng.slg.domain.chat.ChannelType;
import com.joymeng.slg.domain.chat.ChatMsg;
import com.joymeng.slg.domain.chat.ChatRole;
import com.joymeng.slg.domain.chat.MsgTextColorType;
import com.joymeng.slg.domain.chat.MsgTitleType;
import com.joymeng.slg.domain.chat.MsgType;
import com.joymeng.slg.domain.chat.ReportType;
import com.joymeng.slg.net.ParametersEntity;
import com.joymeng.slg.net.handler.NeedContinueDoSomthing;
import com.joymeng.slg.net.handler.ServiceHandler;
import com.joymeng.slg.net.resp.TransmissionResp;

public class ServerNotice extends ServiceHandler{
	@Override
	public void _deserialize(JoyBuffer in, ParametersEntity params) {
		int type = in.getInt();
		params.put(type);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {
			params.put(in.getInt());// 从哪个服务器来的请求
			long uid = in.getLong();
			int serverId = in.getInt();
			String noticeContent = in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT);
			params.put(uid);
			params.put(serverId);
			params.put(noticeContent);
		} else {
			params.put(in.get());// 判断结果
			params.put(in.getInt()); // 从哪个服务器来的
			params.put(in.getPrefixedString(JoyBuffer.STRING_TYPE_SHORT)); // 拼接的字符串
		}
	}

	@Override
	public JoyProtocol handle(final UserInfo info, final ParametersEntity params)
			throws Exception {
		int type = params.get(0);
		if (type == HtppOprateType.HTPP_OPRATE_REQUEST.ordinal()) {// 请求
			int fromId = params.get(1);// 从哪个服务器来的请求,回到哪去
			int serverId = params.get(3);
			String content = params.get(4);
			UserInfo targetInfo = new UserInfo();
			targetInfo.setUid(info.getUid());
			targetInfo.setEid(serverId);
			targetInfo.setCid(fromId);// 回到来的服务器
			int protocolId = 0x0000009D;
			TransmissionResp resp = newTransmissionResp(targetInfo);	
			resp.getParams().put(protocolId);// 指令编号
			resp.getParams().put(HtppOprateType.HTPP_OPRATE_RESPONSE.ordinal());
			resp.getParams().put(TransmissionResp.JOY_RESP_SUCC);
			resp.getParams().put(serverId);
			
			content = "0" + content;
			ChatRole sys = new ChatRole();
			sys.setUid(-1L);
			sys.setName("系统公告");
			ChatMsg msg = new ChatMsg(MsgTitleType.MSG_TITLE_GENERAL_SYSTEM, content, MsgTextColorType.COLOR_BLACK,
					ChannelType.MAIL_SYSTEM, MsgType.TYPE_HORN, ReportType.TYPE_DEFAULT, sys, null);	
			//添加世界公告
			chatMgr.addSystemNotice(msg, 11);
			//加入世界聊天
			chatMgr.addWorldChat(msg);
			chatMgr.sendWorldChatMsgsUpdate(msg);
			resp.getParams().put("success");
			return resp;
		} else {
			byte result = params.get(1);
			int sid = params.get(2);
			NeedContinueDoSomthing next = search(info.getUid(), sid);
			if (next != null) {
				if (result == TransmissionResp.JOY_RESP_SUCC) {
					next.succeed(info, params);
				} else {
					next.fail(info, params);
				}
				removeNextDo(info.getUid(), next);
			}
			return null;
		}
	}
}
