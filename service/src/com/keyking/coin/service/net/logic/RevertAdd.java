package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class RevertAdd extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long id = buffer.getLong();
		long uid = buffer.getLong();
		long tar = buffer.getLong();
		String context = buffer.getUTF();
		Deal deal = CTRL.tryToSearch(id);
		if (deal == null){
			resp.setError("此帖已撤销");
			return resp;
		}
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		synchronized (deal) {
			Revert revrt = new Revert();
			revrt.setUid(uid);
			revrt.setTar(tar);
			revrt.setDependentId(id);
			revrt.setContext(context);
			revrt.setCreateTime(TimeUtils.formatYear(TimeUtils.now()));
			long rid = PK.key("deal_revert");
			revrt.setId(rid);
			deal.addRevert(revrt);
			ServerLog.info(user.getAccount() + " revert deal ok ----> deal-id is " + deal.getId() + " revert-id is " + revrt.getId() + "revert-context is " + context);
		}
		NET.sendMessageToAllClent(deal.clientMessage(Module.UPDATE_FLAG),null);
		resp.setSucces();
		return resp;
	}

}
