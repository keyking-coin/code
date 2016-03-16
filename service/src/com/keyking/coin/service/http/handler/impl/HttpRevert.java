package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpRevert extends HttpHandler {
	//http://139.196.30.53:32104/HttpRevert?uid=xxx&tid=xxx&did=xxx&content=xxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		String uid_str  = request.getParameter("uid");//我的编号
		String pwd      = request.getParameter("pwd");//我的编号
		String tid_str  = request.getParameter("tid");//回复目标
		String deal_str = request.getParameter("did");//在那个帖子回复
		String content  = request.getParameter("content");//回复的内容
		long uid        = Long.parseLong(uid_str);
		long target     = Long.parseLong(tid_str);
		long dealId     = Long.parseLong(deal_str);
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal == null){
			error(request,response,"此帖已撤销");
			return;
		}
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			error(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		if (user.getPwd().equals(pwd)){
			error(request,response,"非法请求格式");
			return;
		}
		if (StringUtil.isNull(content)){
			error(request,response,"不能回复空的内容");
			return;
		}
		synchronized (deal) {
			Revert revrt = new Revert();
			revrt.setUid(uid);
			revrt.setTar(target);
			revrt.setDependentId(dealId);
			revrt.setContext(content);
			revrt.setCreateTime(TimeUtils.formatYear(TimeUtils.now()));
			long rid = PK.key("deal_revert");
			revrt.setId(rid);
			deal.addRevert(revrt);
			ServerLog.info(user.getAccount() + " revert deal ok ----> deal-id is " + deal.getId() + " revert-id is " + revrt.getId() + "revert-context is " + content);
		}
		NET.sendMessageToAllClent(deal.clientMessage(Module.UPDATE_FLAG),null);
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String str = formatJosn(request,JsonUtil.ObjectToJsonString(deal));
		response.appendBody(str);
	}
}
