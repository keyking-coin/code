package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppFriendDel extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		String idStr = buffer.getUTF();
		UserCharacter user_me = CTRL.search(uid);
		if (user_me != null){
			String forbidStr = user_me.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			String[] ids = idStr.split(",");
			for (String id : ids){
				long fid = Long.parseLong(id);
				UserCharacter user_friend = CTRL.search(fid);
				user_me.removeFriend(user_friend);
				user_friend.removeFriend(user_me);
			}
			resp.setSucces();
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
