package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpDealFavorite extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealFavorite?type=0&tid=xxx&did=xxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String type     = request.getParameter("type");//0收藏,1取消收藏
		String uid_str  = request.getParameter("uid");//我的编号
		String deal_str = request.getParameter("did");//收藏的帖子编号
		long uid        = Long.parseLong(uid_str);
		long dealId     = Long.parseLong(deal_str);
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null){
			UserCharacter user = CTRL.search(uid);
			if (user != null){
				String forbidStr = user.getForbid().getReason();
				if (forbidStr != null){
					message(request,response,"您已经被封号原因是:" + forbidStr);
					return;
				}
				if (type.equals("0")){
					if (!user.getFavorites().contains(dealId)){
						user.getFavorites().add(dealId);
					}
				}else{
					if (user.getFavorites().contains(dealId)){
						user.getFavorites().remove(dealId);
					}
				}
				message(request,response,"ok");
			}
		}else{
			message(request,response,"未找到知道交易贴");
		}
	}

}
