package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformDealData;
import com.keyking.coin.util.JsonUtil;

public class HttpLookDeal extends HttpHandler {
	//http://139.196.30.53:32104/HttpLookDeal?did=1&oid=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long dealId  = Long.parseLong(request.getParameter("did"));//交易编号
		long orderId = Long.parseLong(request.getParameter("oid"));//交易编号
		Deal deal   = CTRL.tryToSearch(dealId);
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order.checkRevoke()){
				message(request,response,"成交数据已撤销");
			}else{
				TransformDealData hd = new TransformDealData();
				hd.copy(deal,order);
				Map<String,Object> datas = new HashMap<String,Object>();
				datas.put("result","ok");
				datas.put("deal",hd);
				String result = formatJosn(request,JsonUtil.ObjectToJsonString(datas));
				response.appendBody(result);
			}
		}else{
			message(request,response,"找不到指定的交易");
		}
	}

}
