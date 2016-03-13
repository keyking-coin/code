package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.data.HttpLookData;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;

public class HttpLook extends HttpHandler {
	//http://139.196.30.53:32104/HttpLook?uid=xxx&mid=xxx&did=xxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		String uid_str  = request.getParameter("uid");//要看的人的uid
		String mid_str  = request.getParameter("mid");//我的uid
		String deal_str = request.getParameter("did");//交易帖的编号
		long uid        = Long.parseLong(uid_str);
		long mid        = Long.parseLong(mid_str);
		long dealId     = Long.parseLong(deal_str);
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			Deal deal = CTRL.tryToSearch(dealId);
			boolean look = (deal.getUid() == uid && deal.checkBuyerId(mid)) || (deal.getUid() == mid && deal.checkBuyerId(uid));
			HttpLookData data = new HttpLookData();
			data.setFace(user.getFace());
			data.setNikeName(user.getNikeName());
			data.setSignature(look ? user.getSignature() : "保密");
			data.setTitle(user.getTitle());
			data.setRegistTime(user.getRegistTime());
			data.setTel(look ? user.getAccount() : "保密");
			if (look){
				data.getAddresses().addAll(user.getAddresses());
			}
			data.setName(look ? user.getName() : "保密");
			if (look){
				data.getBanks().addAll(user.getBankAccount().getAccounts());
			}
			if (look){
				data.getCredit().copy(user.getCredit());
			}
			response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
			response.appendBody(formatJosn(request,JsonUtil.ObjectToJsonString(data)));
		}
	}
}
