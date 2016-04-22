package com.keyking.coin.service.http.handler.impl;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.deal.Deal;
import com.keyking.coin.service.domain.deal.Revert;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.service.net.resp.module.Module;
import com.keyking.coin.service.tranform.TransformRevertData;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.StringUtil;
import com.keyking.coin.util.TimeUtils;

public class HttpRevert extends HttpHandler {
	//http://139.196.30.53:32104/HttpRevert?uid=xxx&tid=xxx&did=xxx&content=xxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String uid_str  = request.getParameter("uid");//我的编号
		String pwd      = request.getParameter("pwd");//验证码
		String tid_str  = request.getParameter("tid");//回复目标
		String deal_str = request.getParameter("did");//在那个帖子回复
		String content  = request.getParameter("content");//回复的内容
		long uid        = Long.parseLong(uid_str);
		long target     = Long.parseLong(tid_str);
		long dealId     = Long.parseLong(deal_str);
		Deal deal = CTRL.tryToSearch(dealId);
		if (deal == null){
			message(request,response,"此帖已撤销");
			return;
		}
		UserCharacter user = CTRL.search(uid);
		String forbidStr = user.getForbid().getReason();
		if (forbidStr != null){
			message(request,response,"您已经被封号原因是:" + forbidStr);
			return;
		}
		if (!user.getPwd().equals(pwd)){
			message(request,response,"非法的请求");
			return;
		}
		if (StringUtil.isNull(content)){
			message(request,response,"不能回复空的内容");
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
			deal.save();
			ServerLog.info(user.getAccount() + " revert deal ok ----> deal-id is " + deal.getId() + " revert-id is " + revrt.getId() + "revert-context is " + content);
		}
		NET.sendMessageToAllClent(deal.clientMessage(Module.UPDATE_FLAG),null);
		List<TransformRevertData> reverts = new ArrayList<TransformRevertData>();
		for (Revert revert : deal.getReverts()){
			TransformRevertData hr = new TransformRevertData();
			hr.copy(revert);
			reverts.add(hr);
		}
		String str = formatJosn(request,"{\"reverts\":" + JsonUtil.ObjectToJsonString(reverts) + "}");
		response.appendBody(str);
	}
}
