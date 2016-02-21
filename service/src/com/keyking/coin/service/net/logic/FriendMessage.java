package com.keyking.coin.service.net.logic;

import java.util.List;

import com.keyking.coin.service.domain.friend.Message;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.TimeUtils;

public class FriendMessage extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid   = buffer.getLong();
		long fid   = buffer.getLong();
		byte type  = buffer.get();
		String value = buffer.getUTF();
		UserCharacter user_me     = CTRL.search(uid);
		UserCharacter user_friend = CTRL.search(fid);
		if (user_me != null && user_friend != null){
			Message message = new Message();
			message.setActors("[" + uid + "," + fid + "]");
			long id = PK.key("message");
			message.setId(id);
			message.setSendId(uid);
			message.setTime(TimeUtils.nowChStr());
			message.setType(type);
			message.setContent(value);
			List<Message> temps = user_me.getFriendMessages(fid);
			byte flag = 0;
			if (temps.size() > 0){//最近的一次聊天超过了5分钟
				long timeNum = TimeUtils.getTime(temps.get(0).getTime()).getMillis() + Message.MESSAGE_PRE_TIME;
				if (timeNum > TimeUtils.nowLong()){
					flag = 1;
				}
			}
			message.setShowTime(flag);
			user_me.addMessage(message,fid);
			user_friend.addMessage(message,uid);
			resp.setSucces();
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
