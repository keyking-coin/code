package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.deal.SimpleOrderModule;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;
import com.keyking.coin.util.ServerLog;

public class HttpGrab extends HttpHandler{
	//http://139.196.30.53:32104/HttpGrab?did=2&uid=1&num=20&pwd=xxxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
        response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long id    = Long.parseLong(request.getParameter("did"));
		long uid   = Long.parseLong(request.getParameter("uid"));
		int num    = Integer.parseInt(request.getParameter("num"));
		String pwd = request.getParameter("pwd");
		Deal deal = CTRL.tryToSearch(id);
		if (deal == null){
			message(request,response,"卖家已撤销:");
			return;
		}
		if (!deal.checkValidTime()){
			message(request,response,"此贴已过期");
			return;
		}
		UserCharacter user = CTRL.search(uid);
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		synchronized (deal) {
			float need = num * deal.getPrice();
			float maxCredit = Math.max(user.computeMaxCredit(),user.computeTempCredit());
			if (user.computeUsedCredit() + need > maxCredit){
				message(request,response,"你的信用不足");
				return;
			}
			if (!deal.couldGrab(num)){
				message(request,response,"剩余数量不足,抢单失败。");
				return;
			}
			DealOrder order = new DealOrder();
			order.setDealId(id);
			order.setBuyId(uid);
			order.addTimes((byte)0);
			order.setPrice(deal.getPrice());
			order.setNum(num);
			order.setHelpFlag(deal.getHelpFlag());
			long orderId = PK.key("deal_order");
			order.setId(orderId);
			CTRL.addRecents(order);
			deal.addOrder(order);
			SimpleOrderModule module = new SimpleOrderModule();
			order.simpleDes(module);
			ModuleResp modules = new ModuleResp();
			modules.addModule(module);
			order.clientMessage(Module.ADD_FLAG,modules);
			deal.clientMessage(Module.UPDATE_FLAG,modules);
			NET.sendMessageToAllClent(modules,null);
			StringBuffer sb = new StringBuffer();
			int left = Math.max(0,deal.getNum()-deal.orderNum());
			sb.append("{\"result\":\"ok\",\"num\":" + left + "}");
			response.appendBody(formatJosn(request,sb.toString()));
			ServerLog.info(user.getAccount() + " grab deal ok ----> id is " + id);
		}
	}
}
