package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class RevertDel extends AbstractLogic{

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long id   = buffer.getLong();
		long uid  = buffer.getLong();
		long dId  = buffer.getLong();
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		Deal deal = CTRL.tryToSearch(dId);
		if (deal != null){
			Revert revert = deal.searchRevert(id);
			if (revert != null && revert.getUid() == uid){
				revert.setRevoke(true);
				NET.sendMessageToAllClent(deal.clientMessage(Module.UPDATE_FLAG),null);
				resp.setSucces();
				ServerLog.info("rovke deal ok ----> id is " + deal.getId());
			}else{
				resp.setError("您没有权限这么做");
			}
		}else{
			resp.setError("交易帖子不存在");
		}
		return resp;
	}

}
