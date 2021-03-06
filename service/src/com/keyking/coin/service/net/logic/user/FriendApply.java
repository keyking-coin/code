package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class FriendApply extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String value  = buffer.getUTF();
		UserCharacter user_me = CTRL.search(uid);
		if (user_me != null){
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
