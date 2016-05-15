package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppFriendPass extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();
		long fid = buffer.getLong();
		byte pass = buffer.get();
		UserCharacter user_me   = CTRL.search(uid);
		UserCharacter user_friend = CTRL.search(fid);
		if (user_me != null && user_friend != null){
			String forbidStr = user_me.getForbid().getReason();
			if (forbidStr != null){
				resp.setError("您已经被封号原因是:" + forbidStr);
				return resp;
			}
			Friend friend = user_me.findFriend(fid);
			if (friend != null){
				if (pass == 1){//同意
					if (friend.getPass() == 1){
						resp.setError("你们已经是好友了");
					}else{
						friend.setPass(pass);
						user_me.sendFriendChange();
						user_friend.addFriend(uid);//把我加入对方的好友列表
						friend.save();
						resp.setSucces();
					}
				}else{//拒绝
					user_me.removeFriend(user_friend);
					user_friend.removeFriend(user_me);
					resp.setSucces();
				}
			}else{
				resp.setError("操作错误");
			}
		}
		return resp;
	}

}
