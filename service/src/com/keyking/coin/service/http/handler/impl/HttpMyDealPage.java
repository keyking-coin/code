package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformDealListInfo;
import com.keyking.coin.service.tranform.page.order.TransformOrderListInfo;
import com.keyking.coin.util.JsonUtil;

public class HttpMyDealPage extends HttpDealPage {
	//http://127.0.0.1:32104/HttpMyDealPage?index=3&uid=368&page=1&num=30
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		int index = Integer.parseInt(request.getParameter("index"));
		long uid  = Long.parseLong(request.getParameter("uid"));
		int page  = Integer.parseInt(request.getParameter("page"));
		int num  = Integer.parseInt(request.getParameter("num"));
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			String str = formatJosn(request,"[]");
			response.appendBody(str);
			return;
		}
		Map<String,Object> datas = new HashMap<String,Object>();
		if (index == 1){
			List<TransformDealListInfo> src = searchSells(uid);//等待交易 我所发的所有在有效期的帖子
			if (src != null && src.size() > 0){
				List<TransformDealListInfo> dst = new ArrayList<TransformDealListInfo>();
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
		}else{
			List<TransformOrderListInfo> src = null;
			switch(index){
				case 2:{//正在交易 有两人参与的帖子，未到评分这一步的
					src = searchDealing(uid);
					break;
				}
				case 3:{//已经完成收货确认,但没有互评的
					src = searchConfirmOrders(uid);
					break;
				}
				case 4:{//已经完成评分的
					src = searchDealOver(uid);
					break;
				}
				case 5:{//已经完成评分的
					src = searchDealHelp(uid);
					break;
				}
			}
			if (src != null && src.size() > 0){
				List<TransformOrderListInfo> dst = new ArrayList<TransformOrderListInfo>();
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
		}
		String reBack = JsonUtil.ObjectToJsonString(datas);
		response.appendBody(formatJosn(request,reBack));
	}
	
	private List<TransformDealListInfo> searchSells(long uid){
		List<TransformDealListInfo> result = new ArrayList<TransformDealListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (!deal.checkValidTime() || deal.getUid() != uid || deal.isRevoke() || deal.getLeftNum() == 0){
				continue;
			}
			TransformDealListInfo hd = new TransformDealListInfo(deal);
			result.add(hd);
		}
		Collections.sort(result);
		return result;
	}
	
	private List<TransformOrderListInfo> searchDealing(long uid){
		List<TransformOrderListInfo> result = new ArrayList<TransformOrderListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				continue;
			}
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isDealing() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isDealing() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	private List<TransformOrderListInfo> searchConfirmOrders(long uid){
		List<TransformOrderListInfo> result = new ArrayList<TransformOrderListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				continue;
			}
			for (int j = 0 ; j < deal.getOrders().size() ; j++){
				DealOrder order = deal.getOrders().get(j);
				if (order.isConfirming(deal,uid)){
					TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
					result.add(hd);
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	private List<TransformOrderListInfo> searchDealOver(long uid){
		List<TransformOrderListInfo> result = new ArrayList<TransformOrderListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				continue;
			}
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isCompleted() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isCompleted() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	private List<TransformOrderListInfo> searchDealHelp(long uid){
		List<TransformOrderListInfo> result = new ArrayList<TransformOrderListInfo>();
		List<Deal> deals = CTRL.getDeals();
		for (int i = 0 ; i < deals.size() ; i++){
			Deal deal = deals.get(i);
			if (deal.getHelpFlag() == 0 || !deal.checkJoin(uid)){
				continue;
			}
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isDealing() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isDealing() && !order.checkRevoke()){
						TransformOrderListInfo hd = new TransformOrderListInfo(deal,order);
						result.add(hd);
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
}
