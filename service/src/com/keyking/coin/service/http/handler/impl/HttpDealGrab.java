package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.dao.TableName;
import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.Seller;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.tranform.page.deal.TransformOrder;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;

public class HttpDealGrab extends HttpHandler {
	//http://127.0.0.1:32104/HttpDealGrab?did=2&uid=1&num=20&pwd=xxxx
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
			if (deal.getSellFlag() == Deal.DEAL_TYPE_BUY && !user.getPermission().seller()){//这个买帖
				Seller seller = user.getSeller();
				if (seller != null && !seller.isPass()){
					message(request,response,"请等待卖家认证通过");
				}else{
					message(request,response,"请先进行卖家认证");
				}
				return;
			}
			if (!deal.couldGrab(num)){
				message(request,response,"剩余数量不足，抢单失败。");
				return;
			}
			if (uid == deal.getUid()){
				message(request,response,"自己的交易不能抢单");
				return;
			}
			DealOrder order = new DealOrder();
			order.setDealId(id);
			order.setBuyId(uid);
			order.addTimes(deal,(byte)0);
			order.setPrice(deal.getPrice());
			order.setNum(num);
			order.setHelpFlag(deal.getHelpFlag());
			long orderId = PK.key(TableName.TABLE_NAME_ORDER);
			order.setId(orderId);
			deal.addOrder(order);
			order.save();
			Map<String,Object> datas = new HashMap<String,Object>();
			datas.put("result","ok");
			List<TransformOrder> tos = new ArrayList<TransformOrder>();
			for (DealOrder o : deal.getOrders()){
				TransformOrder to = new TransformOrder();
				to.copy(o);
				tos.add(to);
			}
			datas.put("orders",tos);
			datas.put("dealId",deal.getId());
			datas.put("num",deal.getLeftNum());
			datas.put("monad",deal.getMonad());
			String str = JsonUtil.ObjectToJsonString(datas);
			response.appendBody(formatJosn(request,str));
			ServerLog.info(user.getAccount() + " grab deal ok ----> id is " + id);
		}
	}
}
