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
		long dealId = buffer.getLong();
		long uid    = buffer.getLong();
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			UserCharacter user = CTRL.search(uid);
			if (user != null && !user.getFavorites().contains(dealId)){
				user.getFavorites().add(dealId);
				resp.setSucces();
				ServerLog.info(user.getAccount() + " favorite deal ok ----> id is " + dealId);
			}else if(user == null){
				resp.setError("系统错误");
			}else {
				resp.setError("您已经收藏过了");
			}
		}else{
			resp.setError("交易帖子不存在");
		}
		return resp;
	}

}
