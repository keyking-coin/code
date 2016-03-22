package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.data.HttpDealData;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;

public class HttpAgency extends HttpHandler {
	//http://139.196.30.53:32104/HttpAgency?uid=1
	//http://127.0.0.1:32104/HttpAgency?uid=1
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid = Long.parseLong(request.getParameter("uid"));
		//null、入库、现货 ---> 全部类型的 、入库类型、现货类型
		String type    = request.getParameter("type");
		//null、xxx ---> 全部文交所 、其他选择的文交所
		String bourse  = request.getParameter("bourse");
		//null、xxx ---> 藏品名称不限 、输入的的藏品名称
		String title   = request.getParameter("title");
		//null、xxx ---> 成交盘中出售人名字是：不限、输入名称
		String seller  = request.getParameter("title");
		//null、xxx ---> 成交盘中购买人名字是：不限、输入名称
		String buyer   = request.getParameter("buyer");
		//null、xxx ---> 不限有效期、其他选择的字符串(到目前无效，到目前有效)
		String valid   = request.getParameter("valid");
		List<Deal> deals = null;
		UserCharacter user = CTRL.search(uid);
		SearchCondition condition = new SearchCondition();
		condition.setAgency(true);
		if (!StringUtil.isNull(type)){
			condition.setType(type);
		}
		if (!StringUtil.isNull(title)){
			condition.setTitle(title);
		}
		if (!StringUtil.isNull(bourse)){
			condition.setBourse(bourse);
		}
		if (!StringUtil.isNull(seller)){
			condition.setSeller(seller);
		}
		if (!StringUtil.isNull(buyer)){
			condition.setBuyer(buyer);
		}
		if (!StringUtil.isNull(valid)){
			condition.setValid(valid);
		}
		deals = CTRL.getSearchDeals(condition);
		if (deals.size() > 0){
			List<HttpDealData> hDeals = new ArrayList<HttpDealData>();
			for (Deal deal : deals){
				HttpDealData hdeal = new HttpDealData();
				hdeal.copy(deal,user);
				hDeals.add(hdeal);
			}
			String str = formatJosn(request,JsonUtil.ObjectToJsonString(hDeals));
			response.appendBody(str);
		}else{
			String str = formatJosn(request,"[]");
			response.appendBody(str);
		}
	}

}
