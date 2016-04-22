package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.ServerLog;

public class HttpDealDel extends HttpHandler {
	//http://139.196.30.53:32104/HttpDealDel?uid=x&pwd=x&did=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid       = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd     = request.getParameter("pwd");//验证密码
		long dealId    = Long.parseLong(request.getParameter("did"));//帖子编号
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
			synchronized (deal) {
				if (deal.getUid() == uid){
					deal.setNum(0);
					deal.setRevoke(true);
					deal.setNeedSave(true);
					deal.save();
					message(request,response,"ok");
					NET.sendMessageToAllClent(deal.clientMessage(Module.DEL_FLAG),null);
					ServerLog.info(user.getAccount() + " revoke deal ----> id is " + dealId);
				}else{
					message(request,response,"您不是发帖人");
				}
			}
		}else{
			message(request,response,"找不到用户");
		}
	}
}
