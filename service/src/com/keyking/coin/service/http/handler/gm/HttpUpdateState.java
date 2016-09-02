package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpUpdateState extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long did = Long.parseLong(request.getParameter("did"));
		long oid = Long.parseLong(request.getParameter("oid"));
		byte state = Byte.parseByte(request.getParameter("state"));
		Deal deal = CTRL.tryToSearch(did);
		DealOrder order = null;
		if (deal != null){
			order = deal.searchOrder(oid);
			if (order != null){
				if (order.getState() != state -1){
					response.put("result","状态不对,请刷下页面");
					return;
				}else{
					order.addTimes(deal,state);
					order.save();
				}
			}
		}
		if (order != null){
			response.put("result","ok");
		}else{
			response.put("result","找不到数据");
		}
	}
}
