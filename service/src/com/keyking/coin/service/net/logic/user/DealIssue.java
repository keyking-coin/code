package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class DealIssue extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp  = new GeneralResp(logicName);
		long id   = buffer.getLong();
		long uid   = buffer.getLong();
		Deal deal = CTRL.tryToSearch(id);
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		if (user != null && deal != null){
			if (user.getRecharge().getCurMoney() < 10){
				resp.setError("您的邮游币不足请先去充值");
			}else{
				deal.issue();
				resp.setSucces("推送成功");
				ServerLog.info(user.getAccount() + " issue deal ok ----> id is " + id);
			}
		}else{
			resp.setError("数据异常");
		}
		return resp;
	}

}
