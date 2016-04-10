package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformDealData;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpDealSearch extends HttpHandler {
	//http://127.0.0.1:32104/HttpDealSearch?search=null
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		//null就是查询最近7天交易记录，默认点击交易区就传null，其他值都标示是条件查询
		String search  = request.getParameter("search");
		//null、入库、现货 ---> 全部类型的 、入库类型、现货类型
		String type    = request.getParameter("type");
		//null、xxx ---> 全部文交所 、其他选择的文交所
		String bourse  = request.getParameter("bourse");
		//null、xxx ---> 藏品名称不限 、输入的的藏品名称
		String title   = request.getParameter("title");
		//null、xxx ---> 成交盘中出售人名字是：不限、输入名称
		String seller  = request.getParameter("seller");
		//null、xxx ---> 成交盘中购买人名字是：不限、输入名称
		String buyer   = request.getParameter("buyer");
		//null、xxx ---> 不限有效期、其他选择的字符串(到目前无效，到目前有效)
		String valid   = request.getParameter("valid");
		List<Deal> deals = null;
		if (search != null && search.equals("null")){//普通查询7天内的所有的帖子
			deals = CTRL.getWeekDeals();
		}else{
			SearchCondition condition = new SearchCondition();
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
		}
		if (deals.size() > 0){
			List<Deal> issues    = new ArrayList<Deal>();
			List<Deal> valides   = new ArrayList<Deal>();
			List<Deal> normals   = new ArrayList<Deal>();
			List<Deal> tails     = new ArrayList<Deal>();
			for (Deal deal : deals){
				if (deal.getLeftNum() == 0){
					tails.add(deal);
					continue;
				}
				if (deal.isIssueRecently()){
					issues.add(deal);
				}else if (deal.checkValidTime()){
					valides.add(deal);
				}else{
					normals.add(deal);
				}
			}
			if (issues.size() > 0){
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
			}
			if (valides.size() > 0){
				Collections.sort(valides);
			}
			if (normals.size() > 0){
				Collections.sort(normals);
			}
			if (tails.size() > 0){
				Collections.sort(tails);
			}
			List<TransformDealData> hDeals = new ArrayList<TransformDealData>();
			for (Deal deal : issues){
				TransformDealData hdeal = new TransformDealData();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			for (Deal deal : valides){
				TransformDealData hdeal = new TransformDealData();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			for (Deal deal : normals){
				TransformDealData hdeal = new TransformDealData();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			for (Deal deal : tails){
				TransformDealData hdeal = new TransformDealData();
				hdeal.copy(deal);
				hDeals.add(hdeal);
			}
			//ServerLog.info(hDeals.get(hDeals.size()-1).getCreateTime());
			String str = formatJosn(request,JsonUtil.ObjectToJsonString(hDeals));
			response.appendBody(str);
		}else{
			String str = formatJosn(request,"[]");
			response.appendBody(str);
		}
	}
}
