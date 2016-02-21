package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class FriendDel extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long uid = buffer.getLong();
		String idStr = buffer.getUTF();
		UserCharacter user_me = CTRL.search(uid);
		if (user_me != null){
			ModuleResp modules = new ModuleResp();
			String[] ids = idStr.split(",");
			for (String id : ids){
				long fid = Long.parseLong(id);
				UserCharacter user_friend = CTRL.search(fid);
				Friend friend = user_me.removeFriend(user_friend);
				friend.clientMessage(modules,Module.DEL_FLAG);
				friend = user_friend.removeFriend(user_me);
				if (friend != null){
					NET.sendMessageToClent(friend.clientMessage(Module.DEL_FLAG),user_friend);
				}
			}
			NET.sendMessageToClent(modules,user_me);
			resp.setSucces("删除成功");
		}else{
			resp.setError("系统错误");
		}
		return resp;
	}

}
