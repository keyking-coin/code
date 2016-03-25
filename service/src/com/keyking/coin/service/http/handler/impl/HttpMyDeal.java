package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.data.HttpDealData;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
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
		List<HttpDealData> deals = null;
		switch(index){
		case 1:
			deals = searchSells(user);//等待交易 我所发的所有在有效期的帖子
			break;
		case 2:
			deals = searchDealing(user);//正在交易 有两人参与的帖子，未到评分这一步的
			break;
		case 3:
			deals = searchConfirmOrders(user);//已经完成收货确认,但没有互评的（这一步未完成也应该释放信用额度）
			break;
		case 4:
			deals = searchDealOver(user);//已经完成评分的
			break;
		case 5:
			deals = searchDealHelp(user);//正在中介交易
			break;
		case 6:
			deals = searchFavorite(user);//我的收藏夹
			break;
		}
		String str = formatJosn(request,JsonUtil.ObjectToJsonString(deals));
		response.appendBody(str);
	}
	
	private List<HttpDealData> searchSells(UserCharacter user){
		List<HttpDealData> result = new ArrayList<HttpDealData>();
		List<Deal> deals = CTRL.getDeals();
		for (Deal deal : deals){
			if (!deal.checkValidTime() || deal.getUid() != user.getId() || deal.isRevoke() || deal.getLeftNum() == 0){
				continue;
			}
			HttpDealData hd = new HttpDealData();
			hd.copy(deal,user);
			result.add(hd);
		}
		return result;
	}
	
	private List<HttpDealData> searchDealing(UserCharacter user){
		List<HttpDealData> result = new ArrayList<HttpDealData>();
		List<Deal> deals = CTRL.getDeals();
		long uid = user.getId();
		for (Deal deal : deals){
			if (!deal.checkJoin(uid)){
				continue;
			}
			HttpDealData hd = null;
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isDealing()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isDealing()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}
			if (hd != null){
				result.add(hd);
			}
		}
		return result;
	}
	
	private List<HttpDealData> searchConfirmOrders(UserCharacter user){
		List<HttpDealData> result = new ArrayList<HttpDealData>();
		List<Deal> deals = CTRL.getDeals();
		long uid = user.getId();
		for (Deal deal : deals){
			if (!deal.checkJoin(uid)){
				continue;
			}
			HttpDealData hd = null;
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isConfirming()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isConfirming()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}
			if (hd != null){
				result.add(hd);
			}
		}
		return result;
	}
	
	private List<HttpDealData> searchDealOver(UserCharacter user){
		List<HttpDealData> result = new ArrayList<HttpDealData>();
		List<Deal> deals = CTRL.getDeals();
		long uid = user.getId();
		for (Deal deal : deals){
			if (!deal.checkJoin(uid)){
				continue;
			}
			HttpDealData hd = null;
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isCompleted()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isCompleted()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}
			if (hd != null){
				result.add(hd);
			}
		}
		return result;
	}
	
	private List<HttpDealData> searchDealHelp(UserCharacter user){
		List<HttpDealData> result = new ArrayList<HttpDealData>();
		List<Deal> deals = CTRL.getDeals();
		long uid = user.getId();
		for (Deal deal : deals){
			if (deal.getHelpFlag() == 0 || !deal.checkJoin(uid)){
				continue;
			}
			HttpDealData hd = null;
			if (deal.getUid() == uid){
				for (DealOrder order : deal.getOrders()){
					if (order.isDealing()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}else{
				for (DealOrder order : deal.getOrders()){
					if (order.getBuyId() == uid && order.isDealing()){
						if (hd == null){
							hd = new HttpDealData();
							hd.copy(deal);
						}
						hd.add(order);
					}
				}
			}
			if (hd != null){
				result.add(hd);
			}
		}
		return result;
	}
	
	private List<HttpDealData> searchFavorite(UserCharacter user){
		List<Deal> result = new ArrayList<Deal>();
		List<Long> favorites = user.getFavorites();
		for (Long favorite : favorites){
			Deal deal = CTRL.tryToSearch(favorite.longValue());
			if (deal != null){
				result.add(deal);
			}
		}
		return null;
	}

}
