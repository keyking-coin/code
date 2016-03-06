package com.keyking.coin.service.net.logic;

import java.util.List;

import com.keyking.coin.service.domain.user.RankEntity;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.resp.impl.GeneralResp;
import com.keyking.coin.util.ServerLog;

public class RankEvent extends AbstractLogic {

	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		GeneralResp resp = new GeneralResp(logicName);
		List<RankEntity> ranks = CTRL.rankDeal();
		resp.add(ranks);
		resp.setSucces();
		ServerLog.info("ranks size " + ranks.size());
		return resp;
	}
}
