package com.keyking.coin.service.http.handler.gm;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealListInfo;
import com.keyking.coin.service.tranform.page.order.TransformOrderListInfo;
import com.keyking.coin.util.StringUtil;

public class HttpDealSearchByKey extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String type = request.getParameter("type");
		String key  = request.getParameter("key");
		response.put("type",type);
		if (type.equals("deal")){
			List<TransformDealListInfo> list = new ArrayList<TransformDealListInfo>();
			if (StringUtil.isInteger(key)){
				long id = Long.parseLong(key);
				Deal deal = CTRL.tryToSearch(id);
				if (deal != null){
					TransformDealListInfo tdi = new TransformDealListInfo(deal);
					list.add(tdi);
				}
			}else{
				CTRL.trySearchDeals(key,list);
			}
			response.put("result","ok");
			response.put("list",list);
		}else{
			List<TransformOrderListInfo> list = new ArrayList<TransformOrderListInfo>();
			CTRL.trySearchOrders(key,list);
			response.put("result","ok");
			response.put("list",list);
		}
	}
}
