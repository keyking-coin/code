package com.keyking.coin.service.net.logic.user;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class DealDel extends AbstractLogic {
	
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		long id   = buffer.getLong();
		long uid  = buffer.getLong();
		Deal deal = CTRL.tryToSearch(id);
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			resp.setError("您已经被封号原因是:" + forbidStr);
			return resp;
		}
		if (deal != null){
			synchronized (deal) {
				String tips = deal.couldDel();
				if (tips != null){
					resp.setError(tips);
				}else if (deal.getUid() == uid){
					deal.setRevoke(true);
					resp.setSucces();
					resp.add(deal.getId());
					deal.setNeedSave(true);
					NET.sendMessageToAllClent(deal.clientMessage(Module.DEL_FLAG),null);
					ServerLog.info(user.getAccount() + " revoke deal ----> id is " + id);
				}else{
					resp.setError("你没有权限这样做");
				}
			}
		}else{
			resp.setError("交易帖子不存在");
		}
		return resp;
	}
}
