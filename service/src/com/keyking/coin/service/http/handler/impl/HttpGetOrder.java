package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;
import com.keyking.coin.util.JsonUtil;

public class HttpGetOrder extends HttpHandler {
	//http://139.196.30.53:32104/HttpGetOrder?did=x&oid=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long dealId    = Long.parseLong(request.getParameter("did"));//帖子编号
		long orderId   = Long.parseLong(request.getParameter("oid"));//订单编号
		Deal deal = CTRL.tryToSearch(dealId);
		TransformOrderDetail tod = null;
		if (deal != null){
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				tod = new TransformOrderDetail();
				tod.copy(deal,order);
			}
		}
		if (tod != null){
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","ok");
			datas.put("order",tod);
			String reBack = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,reBack));
		}else{
			message(request,response,"找不成交数据");
		}
	}

}
