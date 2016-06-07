package com.keyking.coin.service.net.logic.app;

import java.util.List;

import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;

public class AppRank extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		List<RankEntity> ranks = CTRL.rankDeal();
		resp.put("ranks",ranks);
		resp.setSucces();
		return resp;
	}
}
