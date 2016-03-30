package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.DealOrder;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.AdminModuleResp;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.net.resp.module.ModuleResp;

public class HttpDealOrderRevoke extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealOrderRevoke?uid=x&pwd=x&did=x&oid=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid       = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd     = request.getParameter("pwd");//验证密码
		long dealId    = Long.parseLong(request.getParameter("did"));//帖子编号
		long orderId    = Long.parseLong(request.getParameter("oid"));//订单编号
		UserCharacter user = CTRL.search(uid);
		if (user != null){
			String forbidStr = user.getForbid().getReason();
			if (forbidStr != null){
				message(request,response,"您已经被封号原因是:" + forbidStr);
				return;
			}
			if (!user.getPwd().equals(pwd)){
				message(request,response,"非法的请求");
				return;
			}
			Deal deal = CTRL.tryToSearch(dealId);
			if (deal != null){
				DealOrder order = deal.searchOrder(orderId);
				synchronized (order) {
					if (order.getBuyId() == uid || deal.getUid() == uid){
						int flag = ((deal.getSellFlag() == 1 && order.getBuyId() == uid) || (deal.getSellFlag() == 0 && deal.getUid() == uid)) ? DealOrder.ORDER_REVOKE_BUYER : DealOrder.ORDER_REVOKE_SELLER;
						if (!order.checkRevoke(flag)){//撤销成功
							order.addRevoke(flag);
							order.setNeedSave(true);
							ModuleResp modules = new ModuleResp();
							order.clientMessage(Module.UPDATE_FLAG,modules);
							NET.sendMessageToAllClent(modules,null);
							if (order.checkRevoke()){//双方都同意了
								NET.sendMessageToAdmin(order.clientAdminMessage(Module.DEL_FLAG,new AdminModuleResp()));
							}
							message(request,response,"ok");
						}else{
							message(request,response,"已申请等待对方处理");
						}
					}else{
						message(request,response,"您没有权限撤销");
					}
				}
			}else{
				message(request,response,"找不到交易");
			}
		}else{
			message(request,response,"找不到用户");
		}
	}

}
