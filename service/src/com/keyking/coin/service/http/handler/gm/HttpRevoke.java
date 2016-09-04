package com.keyking.coin.service.http.handler.gm;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpRevoke extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id = Long.parseLong(request.getParameter("id"));
		String type = request.getParameter("type");
		if (type.equals("deal")){
			Deal deal = CTRL.tryToSearch(id);
			deal.setRevoke(true);
			deal.save();
		    response.put("result","ok");
		}else{
			List<Deal> deals = CTRL.getDeals();
			for (int i = 0 ; i < deals.size() ; i++){
				Deal deal = deals.get(i);
				for (int  j = 0 ; j < deal.getOrders().size(); j++){
					DealOrder order = deal.getOrders().get(j);
					if (order.getId() == id){
						order.setRevoke(3);
						order.save();
						response.put("result","ok");
						return;
					}
				}
			}
			response.put("result","找不到数据");
		}
	}

}
