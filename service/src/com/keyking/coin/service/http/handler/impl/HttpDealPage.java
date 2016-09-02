package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.domain.condition.SearchCondition;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealListInfo;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.StringUtil;

public class HttpDealPage extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealPage?type=x&bourse=x&title=x&seller=x&buyer=x&valid=x&page=x&num=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		//页码
		int page = Integer.parseInt(request.getParameter("page"));
		//每一页数量
		int num  = Integer.parseInt(request.getParameter("num"));
		SearchCondition condition = getCondition(request);
		Map<String,Object> datas = new HashMap<String,Object>();
		List<Deal> temp = CTRL.getSearchDeals(condition);
		if (temp.size() > 0){
			List<TransformDealListInfo> src = new ArrayList<TransformDealListInfo>();
			List<TransformDealListInfo> dst = new ArrayList<TransformDealListInfo>();
			for (Deal deal : temp){
				TransformDealListInfo tdl = new TransformDealListInfo(deal);
				src.add(tdl);
			}
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

	public SearchCondition getCondition(HttpRequestMessage request){
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
		return condition;
	}
}
