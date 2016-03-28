package com.keyking.coin.service.http.handler.impl;

import java.util.HashMap;
import java.util.Map;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.tranform.TransformOrderData;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;

public class HttpDealOrderUpdate extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealOrderUpdate?uid=x&pwd=x&did=x&oid=x&state=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd = request.getParameter("pwd");//密码验证
		long dealId = Long.parseLong(request.getParameter("did"));//交易编号
		long orderId = Long.parseLong(request.getParameter("oid"));//订单编号
		byte state = Byte.parseByte(request.getParameter("state"));//状态
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
				synchronized (order) {
					if (order.checkRevoke(3)){
						message(request, response, "订单已撤销,无法操作");
						return;
					}
					boolean couldUpdate = false;
					if (state == order.getState() + 1){
						if (order.getHelpFlag() == 0) {
							if ((state == 1 || state == 3) && (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
								couldUpdate = true;
							}
							if (state == 2 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))){
								couldUpdate = true;
							}
						}else{
							if ((state == 1 || state == 4)&& (deal.checkBuyer(uid) || order.checkBuyer(deal, uid))) {
								couldUpdate = true;
							}
							if (state == 3 && (deal.checkSeller(uid) || order.checkSeller(deal, uid))) {
								couldUpdate = true;
							}
					    }
					}
					if (couldUpdate) {
						byte pre = order.getState();
						order.addTimes(state);
						NET.sendMessageToAllClent(order.clientMessage(Module.UPDATE_FLAG), null);
						if (order.getHelpFlag() == 1) {
							if (order.getState() == 1 || order.getState() == 4){
								NET.sendMessageToAdmin(order.clientAdminMessage(Module.ADD_FLAG,new AdminModuleResp()));
							}else if(pre == 1 || pre == 4){
								NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
							}
						}
						Map<String,Object> datas = new HashMap<String,Object>();
						TransformOrderData hd = new TransformOrderData();
						hd.copy(order);
						datas.put("result","ok");
						datas.put("order",hd);
						String str = JsonUtil.ObjectToJsonString(datas);
						ServerLog.info(CTRL.search(uid).getAccount()+ " update deal-order state from " + pre + " to " + order.getState() + " ----> id is " + orderId);
						String result = formatJosn(request,str);
						response.appendBody(result);
					}else{
						message(request, response, "您没有权限那么做");
					}
				}
			}else{
				message(request, response, "订单编号错误");
			}
		}
	}

}
