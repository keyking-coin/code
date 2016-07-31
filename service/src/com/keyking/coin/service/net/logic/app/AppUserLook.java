package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.TransformLookData;

public class AppUserLook extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid     = buffer.getLong();
		long mid     = buffer.getLong();
		long dealId  = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			Deal deal = CTRL.tryToSearch(dealId);
			boolean look  = false;
			if (deal != null){
				look = (deal.getUid() == uid && deal.checkBuyerId(mid)) || (deal.getUid() == mid && deal.checkBuyerId(uid));
			}
			if (uid == mid || mid <= 2){
				look = true;
			}
			TransformLookData data = new TransformLookData();
			data.setFace(user.getFace());
			data.setNikeName(user.getNikeName());
			data.setSignature(look ? user.getSignature() : "仅对交易方可见");
			data.setTitle(user.getTitle());
			data.setRegistTime(user.getRegistTime());
			data.setTel(look ? user.getAccount() : "仅对交易方可见");
			data.setName(look ? user.getName() : "仅对交易方可见");
			data.setCouldLook(look);
			UserCharacter me = CTRL.search(mid);
			data.setFriend(me.checkFriend(user));
			if (look){
				data.getAddresses().addAll(user.getAddresses());
			}
			if (look){
				data.getBanks().addAll(user.getBankAccount().getAccounts());
			}
			data.getCredit().copy(user.getCredit());
			data.setDealCount(CTRL.computeOkOrderNum(uid));
			resp.put("info",data);
			resp.setSucces();
		}else{
			resp.setError("找不到用户");
		}
		return resp;
	}

}
