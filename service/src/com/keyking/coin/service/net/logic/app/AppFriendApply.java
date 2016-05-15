package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppFriendApply extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		String value  = buffer.getUTF();
		UserCharacter user_me = CTRL.search(uid);
		if (user_me != null){
			String forbidStr = user_me.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			UserCharacter user_friend = CTRL.searchByAccountOrNickName(value);
			if (user_friend != null){
				if (user_friend.getId() == uid){
					resp.setError("不能加自己");
				}else {
					Friend friend = user_friend.findFriend(uid);
					if (friend != null){
						if(friend.getPass() == 1){
							resp.setError("他已经是你好友了");
						}else{
							resp.setError("已向他申请过了");
						}
					}else {
						user_friend.applyFriend(uid);
						resp.setSucces("已向对方发送申请");
					}
				}
			}else{
				resp.setError("找不到注册用户");
			}
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
