package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.data.HttpDeal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpDealSearch implements HttpHandler {
	//http://139.196.30.53:32104/HttpDealSearch?condition=null
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		String conditionStr = request.getParameter("condition");
		List<Deal> deals = null;
		if (conditionStr.equals("null")){//普通查询7天内的所有的帖子
			deals = CTRL.getWeekDeals();
		}else{
			SearchCondition condition = JsonUtil.JsonToObject(conditionStr,SearchCondition.class);
			deals = CTRL.getSearchDeals(condition);
		}
		if (deals.size() > 0){
			List<Deal> issues = new ArrayList<Deal>();
			List<Deal> valides = new ArrayList<Deal>();
			List<Deal> normal = new ArrayList<Deal>();
			for (Deal deal : deals){
				if (deal.isIssueRecently()){
					issues.add(deal);
				}else if (deal.checkValidTime()){
					valides.add(deal);
				}else{
					normal.add(deal);
				}
			}
			Collections.sort(issues,new Comparator<Deal>(){
				@Override
				public int compare(Deal o1, Deal o2) {
					DateTime time1 = TimeUtils.getTime(o1.getLastIssue());
					DateTime time2 = TimeUtils.getTime(o2.getLastIssue());
					if (time1.isBefore(time2)){
						return 1;
					}else{
						return -1;
					}
				}
			});
			Collections.sort(valides,new Comparator<Deal>(){
				@Override
				public int compare(Deal o1, Deal o2) {
					DateTime time1 = TimeUtils.getTime(o1.getValidTime());
					DateTime time2 = TimeUtils.getTime(o2.getValidTime());
					if (time1.isBefore(time2)){
						return 1;
					}else{
						return -1;
					}
				}
			});
			Collections.sort(normal);
			deals.clear();
			List<HttpDeal> hDeals = new ArrayList<HttpDeal>();
			for (Deal deal : issues){
				HttpDeal hdeal = new HttpDeal();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			for (Deal deal : valides){
				HttpDeal hdeal = new HttpDeal();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			for (Deal deal : normal){
				HttpDeal hdeal = new HttpDeal();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			String str = JsonUtil.ObjectToJsonString(hDeals);
			response.appendBody(str);
		}
	}
}
