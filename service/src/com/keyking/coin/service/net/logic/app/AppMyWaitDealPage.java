package com.keyking.coin.service.net.logic.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AppResp;
import com.keyking.coin.service.tranform.page.deal.TransformDealListInfo;

public class AppMyWaitDealPage extends AbstractLogic{
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AppResp resp = new AppResp(logicName);
		long uid  = buffer.getLong();//用户编号
		int page  = buffer.getInt();//页数
		int num   = buffer.getInt();//一页显示的数据条数
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			resp.setError("找不到用户");
		}else{
			List<TransformDealListInfo> src = searchSells(uid);
			List<TransformDealListInfo> dst = new ArrayList<TransformDealListInfo>();
			int left = CTRL.compute(src,dst,page,num);
			resp.put("list",dst);
			resp.put("page",page);
			resp.put("left",left);
		}
		return resp;
	}

	
	private List<TransformDealListInfo> searchSells(long uid){
		List<TransformDealListInfo> result = new ArrayList<TransformDealListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (Deal deal : deals){
			if (!deal.checkValidTime() || deal.getUid() != uid || deal.isRevoke() || deal.getLeftNum() == 0){
				continue;
			}
			TransformDealListInfo hd = new TransformDealListInfo();
			hd.copy(deal);
			result.add(hd);
		}
		Collections.sort(result);
		return result;
	}
}
