package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;

public class FriendPass extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		long fid = buffer.getLong();
		byte pass = buffer.get();
		UserCharacter user_me   = CTRL.search(uid);
		UserCharacter user_friend = CTRL.search(fid);
		if (user_me != null && user_friend != null){
			Friend friend = user_me.findFriend(fid);
			if (friend != null){
				if (pass == 1){//同意
					if (friend.getPass() == 1){
						resp.setError("你们已经是好友了");
						return resp;
					}else{
						friend.setPass(pass);
						friend.save();
						user_me.sendFriendChange();
						user_friend.addFriend(uid);//把我加入对方的好友列表
						resp.setSucces();
					}
				}else{//拒绝
					user_me.removeFriend(user_friend);
					user_friend.removeFriend(user_me);
					resp.setSucces();
				}
			}
		}
		return resp;
	}

}
