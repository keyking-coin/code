package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealDetail;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;
import com.keyking.coin.util.JsonUtil;

public class HttpGetDetail extends HttpHandler {
	//http://139.196.30.53:32104/HttpGetDetail?module=buy/sell/order&id=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String module = request.getParameter("module");
		long id = Long.parseLong(request.getParameter("id"));
		if (module.equals("buy") || module.equals("sell")){
			Deal deal = CTRL.tryToSearch(id);
			TransformDealDetail td = new TransformDealDetail(deal);
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","ok");
			datas.put("data",td);
			datas.put("module",module);
			String reBack = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,reBack));
		}else if(module.equals("order")){
			TransformOrderDetail order = CTRL.searchTransOrder(id);
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","ok");
			datas.put("data",order);
			datas.put("module",module);
			String reBack = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,reBack));
		}else{
			message(request,response,"错误的module");
		}
	}

}
