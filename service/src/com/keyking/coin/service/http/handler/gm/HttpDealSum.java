package com.keyking.coin.service.http.handler.gm;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class HttpDealSum extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			String start = request.getParameter("start") + " 00:00:00";
			String end = request.getParameter("end") + " 23:59:59";
			long time1 = TimeUtils.getTimes(start);
			long time2 = TimeUtils.getTimes(end);
			if (time2 < time1){
				response.put("result","结束时间不能小于开始时间");
			}else{
				float dealNum   = 0;
				float agencyNum = 0;
				List<Deal> deals = CTRL.getDeals();
				for (int i = 0 ; i < deals.size() ; i++){
					Deal deal = deals.get(i);
					for (DealOrder order : deal.getOrders()){
						long time = TimeUtils.getTimes(order.getTimes().get(0));
						if (time >= time1 && time <= time2){
							float value = order.getPrice() * order.getNum();
							dealNum += value;
							if (order.getHelpFlag() == 1){
								agencyNum += value;
							}
						}
					}
				}
				response.put("result","ok");
				StringBuffer sb = new StringBuffer();
				sb.append("<span>总的成交额 :</span>");
				sb.append("<span style=\"color:#ff1000\">" + dealNum + "</span>");
				sb.append("<br><br>");
				sb.append("<span>中介成交额 :</span>");
				sb.append("<span style=\"color:#00ff10\">" + agencyNum + "</span>");
				response.put("content",sb.toString());
			}
		} catch (Exception e) {
			response.put("result","数据异常");
			ServerLog.error("成交额查询异常",e);
		}
	}

}
