package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class FriendDel extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String idStr = buffer.getUTF();
		UserCharacter user_me = CTRL.search(uid);
		if (user_me != null){
			String[] ids = idStr.split(",");
			for (String id : ids){
				long fid = Long.parseLong(id);
				UserCharacter user_friend = CTRL.search(fid);
				user_me.removeFriend(user_friend);
				user_friend.removeFriend(user_me);
			}
			resp.setSucces("删除成功");
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
