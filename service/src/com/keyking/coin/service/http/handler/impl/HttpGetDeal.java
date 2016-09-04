package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealDetail;
import com.keyking.coin.util.JsonUtil;

public class HttpGetDeal extends HttpHandler {
	//http://139.196.30.53:32104/HttpGetDeal?did=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long dealId    = Long.parseLong(request.getParameter("did"));//帖子编号
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			TransformDealDetail td = new TransformDealDetail(deal);
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","ok");
			datas.put("deal",td);
			String reBack = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,reBack));
		}else{
			message(request,response,"找不交易数据");
		}
	}
}
