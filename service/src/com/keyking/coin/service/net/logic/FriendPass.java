package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;

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
						friend.setNeedSave(true);
						NET.sendMessageToClent(friend.clientMessage(Module.UPDATE_FLAG),user_me);
						user_friend.addFriend(uid);//把我加入对方的好友列表
						resp.setSucces();
					}
				}else{//拒绝
					friend = user_me.removeFriend(user_friend);
					NET.sendMessageToClent(friend.clientMessage(Module.DEL_FLAG),user_me);
					friend = user_friend.removeFriend(user_me);
					if (friend != null){
						NET.sendMessageToClent(friend.clientMessage(Module.DEL_FLAG),user_friend);
					}
					resp.setSucces();
				}
			}
		}
		return resp;
	}

}
