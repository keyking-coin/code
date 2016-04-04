package com.keyking.coin.service.net.logic.admin;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.net.buffer.DataBuffer;
import com.keyking.coin.service.net.logic.AbstractLogic;
import com.keyking.coin.service.net.resp.impl.AdminResp;
import com.keyking.coin.util.TimeUtils;

public class AdminStat extends AbstractLogic{
	
	@Override
	public Object doLogic(DataBuffer buffer, String logicName) throws Exception {
		AdminResp resp = new AdminResp(logicName);
		long start = buffer.getLong();//开始时间
		long end   = buffer.getLong();//结束时间
		if (end < start){
			resp.setError("结束时间不能小于开始时间");
		}else{
			float dealNum   = 0;
			float agencyNum = 0;
			for (Deal deal : CTRL.getDeals()){
				for (DealOrder order : deal.getOrders()){
					long time = TimeUtils.getTimes(order.getTimes().get(0));
					if (time >= start && time <= end){
						float value = order.getPrice() * order.getNum();
						dealNum += value;
						if (order.getHelpFlag() == 1){
							agencyNum += value;
						}
					}
				}
			}
			resp.addKey("dealNum",dealNum);
			resp.addKey("agencyNum",agencyNum);
			resp.setSucces();
		}
		return resp;
	}

}
