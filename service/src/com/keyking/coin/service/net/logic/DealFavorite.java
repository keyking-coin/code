package com.keyking.coin.service.net.logic;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class DealFavorite extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		int type    = buffer.getInt();
		long dealId = buffer.getLong();
		long uid    = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			UserCharacter user = CTRL.search(uid);
			if (user != null){
				String forbidStr = user.getForbid().getReason();
				if (forbidStr != null){
					resp.setError("您已经被封号原因是:" + forbidStr);
					return resp;
				}
				if (type == 0){
					if (!user.getFavorites().contains(dealId)){
						user.getFavorites().add(dealId);
					}
				}else{
					if (user.getFavorites().contains(dealId)){
						user.getFavorites().remove(dealId);
					}
				}
				resp.setSucces();
				resp.add(type);
				resp.add(user.getFavorites());
				ServerLog.info(user.getAccount() + " favorite deal ok ----> id is " + dealId);
			}else{
				resp.setError("系统错误");
			}
		}else{
			resp.setError("交易帖子不存在");
		}
		return resp;
	}

}
