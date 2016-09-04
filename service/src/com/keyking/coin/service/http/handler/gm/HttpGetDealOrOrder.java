package com.keyking.coin.service.http.handler.gm;

import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealDetail;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;

public class HttpGetDealOrOrder extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id = Long.parseLong(request.getParameter("id"));
		String type = request.getParameter("type");
		if (type.equals("deal")){
			Deal deal = CTRL.tryToSearch(id);
			if (deal != null){
				TransformDealDetail tdd = new TransformDealDetail(deal);
			    response.put("result","ok");
			    response.put("deal",tdd);
			}else{
				response.put("result","找不到数据");
			}
		}else{
			List<Deal> deals = CTRL.getDeals();
			for (int i = 0 ; i < deals.size() ; i++){
				Deal deal = deals.get(i);
				for (int  j = 0 ; j < deal.getOrders().size(); j++){
					DealOrder order = deal.getOrders().get(j);
					if (order.getId() == id){
						TransformOrderDetail tod = new TransformOrderDetail(deal,order);
						response.put("result","ok");
						response.put("order",tod);
						return;
					}
				}
			}
			response.put("result","找不到数据");
		}
	}

}
