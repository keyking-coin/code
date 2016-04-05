package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformLookData;
import com.keyking.coin.util.JsonUtil;

public class HttpLook extends HttpHandler {
	//http://139.196.30.53:32104/HttpLook?uid=xxx&mid=xxx&did=xxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
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
			if (uid == mid || mid == 2){
				look = true;
			}
			TransformLookData data = new TransformLookData();
			data.setFace(user.getFace());
			data.setNikeName(user.getNikeName());
			data.setSignature(look ? user.getSignature() : "仅对交易方可见");
			data.setTitle(user.getTitle());
			String[] ss = user.getRegistTime().split(" ");
			data.setRegistTime(ss[0]);
			data.setTel(look ? user.getAccount() : "仅对交易方可见");
			data.setName(look ? user.getName() : "仅对交易方可见");
			if (look){
				data.getAddresses().addAll(user.getAddresses());
			}
			if (look){
				data.getBanks().addAll(user.getBankAccount().getAccounts());
			}
			data.getCredit().copy(user.getCredit());
			response.appendBody(formatJosn(request,JsonUtil.ObjectToJsonString(data)));
		}else{
			message(request, response,"找不到用户");
		}
	}
}
