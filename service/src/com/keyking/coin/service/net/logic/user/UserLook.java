package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
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
			resp.add(user.getAppFace());
			resp.add(user.getNikeName());
			resp.add(look?user.getSignature():"保密");
			resp.add(user.getTitle());
			resp.add(user.getRegistTime());
			resp.add(look?user.getAddresses():0);
			resp.add(look?user.getName():"保密");
			resp.add(look?user.getAccount():"保密");
			resp.add(look?user.getBankAccount():0);
			resp.add(user.getCredit());
			resp.setSucces();
			ServerLog.info("look for user's info ---> account is " + user.getAccount());
		}else{
			resp.setError("找不到目标用户");
		}
		return resp;
	}
}
