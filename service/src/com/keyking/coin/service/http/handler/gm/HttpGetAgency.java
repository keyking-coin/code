package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;

public class HttpGetAgency extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long did = Long.parseLong(request.getParameter("did"));
		long oid = Long.parseLong(request.getParameter("oid"));
		Deal deal = CTRL.tryToSearch(did);
		TransformOrderDetail tod = null;
		if (deal != null){
			DealOrder order = deal.searchOrder(oid);
			if (order != null){
				tod = new TransformOrderDetail();
				tod.copy(deal,order);
			}
		}
		if (tod != null){
			response.put("result","ok");
			response.put("order",tod);
		}else{
			response.put("result","找不到数据");
		}
	}

}
