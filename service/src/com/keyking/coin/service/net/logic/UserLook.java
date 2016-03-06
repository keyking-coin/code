package com.keyking.coin.service.net.logic;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class UserLook extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long dealId = buffer.getLong();
		long uid = buffer.getLong();
		long mid = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			Deal deal = CTRL.tryToSearch(dealId);
			boolean look = (deal.getUid() == uid && deal.checkBuyerId(mid)) || (deal.getUid() == mid && deal.checkBuyerId(uid));
			resp.add(user.getFace());
			resp.add(user.getNikeName());
			resp.add(user.getTitle());
			resp.add(user.getRegistTime());
			List<String> las = user.getAddresses();
			String address = (las.size() > 0 ? las.get(0) : "");
			resp.add(look?address:"保密");
			resp.add(look?user.getName():"保密");
			resp.add(look?user.getAccount():"保密");
			resp.add(user.getBankAccount());
			resp.add(user.getCredit());
			resp.setSucces();
			ServerLog.info("look for user's info ---> account is " + user.getAccount());
		}else{
			resp.setError("找不到目标用户");
		}
		return resp;
	}
}
