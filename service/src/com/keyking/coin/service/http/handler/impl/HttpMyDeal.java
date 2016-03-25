package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.data.HttpDealData;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.data.SearchCondition;
import com.keyking.coin.util.JsonUtil;

public class HttpMyDeal extends HttpHandler {
	//http://139.196.30.53:32104/HttpMyDeal?index=x&uid=1
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		int index = Integer.parseInt(request.getParameter("index"));
		long uid  = Long.parseLong(request.getParameter("uid"));
		UserCharacter user = CTRL.search(uid);
		if (user == null){
			String str = formatJosn(request,"[]");
			response.appendBody(str);
			return;
		}
		List<Deal> deals = null;
		switch(index){
		case 1:
			deals = searchSells(user);//ddjy 我所发的所有在有效期的帖子
			break;
		case 2:
			deals = searchDealing(user);//zzjy 有两人参与的帖子，未到评分这一步的
			break;
		case 3:
			deals = searchConfirmOrders(user);//ddpj 已经完成收货确认,但没有互评的（这一步未完成也应该释放信用额度）
			break;
		case 4:
			deals = searchDealOver(user);//ywcjy 已经完成评分的，包括交割失败的
			break;
		case 5:
			deals = searchDealRevert(user);//wdct 所有我发布的到期系统自动撤销的帖子或者我自己撤销的帖子
			break;
		case 6:
			deals = searchFavorite(user);//我的收藏夹
			break;
		}
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
			message(request,response,"[]");
		}
	}
	
	private List<Deal> searchSells(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setSeller(user.getNikeName());
		condition.setValid("到目前有效");
		List<Deal> deals = CTRL.getSearchDeals(condition);
		Iterator<Deal> iter = deals.iterator();
		while (iter.hasNext()){
			Deal deal = iter.next();
			if (deal.isRevoke()){
				iter.remove();
			}
		}
		return deals;
	}
	
	private List<Deal> searchDealing(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setDealing(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchConfirmOrders(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setConfirming(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchDealOver(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setOver(true);
		List<Deal> deals = CTRL.getSearchDeals(condition);
		long uid = user.getId();
		for (int i = 0  ; i < deals.size() ;){
			Deal deal = deals.get(i);
			if (!deal.checkJoin(uid)){
				deals.remove(i);
			}else{
				i++;
			}
		}
		return deals;
	}
	
	private List<Deal> searchDealRevert(UserCharacter user){
		SearchCondition condition = new SearchCondition();
		condition.setSeller(user.getNikeName());
		condition.setValid("到目前无效");
		return CTRL.getSearchDeals(condition);
	}
	
	private List<Deal> searchFavorite(UserCharacter user){
		List<Deal> result = new ArrayList<Deal>();
		List<Long> favorites = user.getFavorites();
		for (Long favorite : favorites){
			Deal deal = CTRL.tryToSearch(favorite.longValue());
			if (deal != null){
				result.add(deal);
			}
		}
		return result;
	}

}
