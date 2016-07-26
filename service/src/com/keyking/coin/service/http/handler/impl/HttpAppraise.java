package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealAppraise;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.TransformOrderData;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;

public class HttpAppraise extends HttpHandler {
	//http://139.196.30.53:32104/HttpAppraise?uid=x&pwd=x&did=x&oid=x&type=x&star=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd = request.getParameter("pwd");//密码验证
		long dealId = Long.parseLong(request.getParameter("did"));//交易编号
		long orderId = Long.parseLong(request.getParameter("oid"));//订单编号
		byte star = Byte.parseByte(request.getParameter("star"));//评价星级
		String context = request.getParameter("context");//评价内容
		UserCharacter user = CTRL.search(uid);
		if (!user.getPwd().equals(pwd)) {
			message(request, response, "非法的请求");
			return;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null) {
			message(request, response, "您已经被封号原因是:" + forbidStr);
			return;
		}
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal != null) {
			DealOrder order = deal.searchOrder(orderId);
			if (order != null){
				if (!order.over()){
					message(request, response, "未完成交易,无法评价");
					return;
				}
				synchronized (order) {
					if (order.checkRevoke()) {
						message(request, response, "订单已撤销,无法评价");
						return;
					}
					DealAppraise appraise = null;
					if (deal.checkSeller(uid) || order.checkSeller(deal,uid)){//买家
						appraise = order.getSellerAppraise();
					}else if (deal.checkBuyer(uid) || order.checkBuyer(deal,uid)){//卖家
						appraise = order.getBuyerAppraise();
					}
					if (appraise != null){
						if (!appraise.isCompleted()) {
							appraise.appraise(star,context);
							order.save();
							UserCharacter other = CTRL.search(uid == deal.getUid() ? order.getBuyId() : deal.getUid());
							if (other != null) {
								other.getCredit().addNum(star);
								other.save();
							}
							Map<String,Object> datas = new HashMap<String,Object>();
							TransformOrderData hd = new TransformOrderData();
							hd.copy(deal,order);
							datas.put("result","ok");
							datas.put("order",hd);
							ServerLog.info(user.getAccount()+ " appraised : star = " + star + " context = "+ context);
							String result = formatJosn(request,JsonUtil.ObjectToJsonString(datas));
							response.appendBody(result);
						}else{
							message(request, response, "您已评价过了");
						}
					}else{
						message(request, response,"您没有权限评价");
					}
				}
			}else{
				message(request, response, "订单编号错误");
			}
		}
	}

}
