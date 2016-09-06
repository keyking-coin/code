package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.friend.Friend;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.FriendListInfo;

public class AppFriendList extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid = buffer.getLong();//我的编号
		UserCharacter user = CTRL.search(uid);
		if (user == null){//不存在账号是account
			resp.setError("找不到用户");
			return resp;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		List<FriendListInfo> fis = new ArrayList<FriendListInfo>();
		List<Friend> friends = user.getFriends();
		for (Friend friend : friends){
			FriendListInfo fi = new FriendListInfo(friend);
			fis.add(fi);
		}
		resp.put("list",fis);
		resp.setSucces();
		return resp;
	}

}
