package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.order.TransformOrderListInfo;
import com.keyking.coin.util.JsonUtil;

public class HttpAgencyPage extends HttpDealPage {
	//http://127.0.0.1:32104/HttpAgencyPage?page=2&num=30
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		//页码
		int page = Integer.parseInt(request.getParameter("page"));
		//每一页数量
		int num  = Integer.parseInt(request.getParameter("num"));
		SearchCondition condition = getCondition(request);
		condition.setAgency(true);
		Map<String,Object> datas = new HashMap<String,Object>();
		List<Deal> temp = CTRL.getSearchDeals(condition);
		if (temp.size() > 0){
			List<TransformOrderListInfo> src = new ArrayList<TransformOrderListInfo>();
			List<TransformOrderListInfo> dst = new ArrayList<TransformOrderListInfo>();
			for (Deal deal : temp){
				for (DealOrder order : deal.getOrders()){
					if (order.checkRevoke()){
						continue;
					}
					TransformOrderListInfo tol = new TransformOrderListInfo();
					tol.copy(deal,order);
					src.add(tol);
				}
			}
			Collections.sort(src);
			int left = CTRL.compute(src,dst,page,num);
			datas.put("result","ok");
			datas.put("list",dst);
			datas.put("page",page);
			datas.put("left",left);
		}else{
			datas.put("result","ok");
			datas.put("list","[]");
			datas.put("page",page);
			datas.put("left",0);
		}
		String reBack = JsonUtil.ObjectToJsonString(datas);
		response.appendBody(formatJosn(request,reBack));
	}
}
