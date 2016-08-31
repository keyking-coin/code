package com.keyking.coin.service.http.handler.gm;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.order.TransformOrderDetail;

public class HttpAgencyList extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		List<TransformOrderDetail> orders = new ArrayList<TransformOrderDetail>();
		SearchCondition condition = new SearchCondition();
		condition.setAgency(true);
		List<Deal> temp = CTRL.getSearchDeals(condition);
		for (Deal deal : temp){
			for (DealOrder order : deal.getOrders()){
				if (order.checkRevoke()){
					continue;
				}
				if (order.getState() == 1 || order.getState() == 4){
					//中介可以操作成交盘
					TransformOrderDetail to = new TransformOrderDetail();
					to.copy(deal,order);
					orders.add(to);
				}
			}
		}
		response.put("result","ok");
		response.put("list",orders);
	}

}
