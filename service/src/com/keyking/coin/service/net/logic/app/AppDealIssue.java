package com.keyking.coin.service.net.logic.app;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class AppDealIssue extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid  = buffer.getLong();
		long id   = buffer.getLong();
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
		Deal deal = CTRL.tryToSearch(id);
		if (deal == null){
			resp.setError("找不到交易数据");
			return resp;
		}
		if (!deal.checkValidTime()){
			resp.setError("此贴已过期");
			return resp;
		}
		if (user.getRecharge().getCurMoney() < 10){
			resp.setError("您的邮游币不足请先去充值");
		}else{
			deal.setLastIssue(TimeUtils.nowChStr());
			NET.sendMessageToAllClent(deal.pushMessage(),user.getSessionAddress());
			resp.setSucces();
			ServerLog.info(user.getAccount() + " issue deal ok ----> id is " + id);
		}
		return resp;
	}

}
