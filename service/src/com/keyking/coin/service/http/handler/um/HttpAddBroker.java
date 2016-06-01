package com.keyking.coin.service.http.handler.um;

import com.keyking.coin.service.domain.broker.Broker;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.StringUtil;

public class HttpAddBroker extends HttpHandler {
	//http://139.196.30.53:32104/um/HttpAddBroker?uid=x&pwd=x&name=x&des=x&adminAccount=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String ustr    = request.getParameter("uid");
		String pwd     = request.getParameter("pwd");
		String name    = request.getParameter("name");
		String des     = request.getParameter("des");
		String adminAccount    = request.getParameter("adminAccount");
		long uid = Long.parseLong(ustr);
		UserCharacter user = CTRL.search(uid);
		if (!user.getPwd().equals(pwd)) {
			message(request, response, "非法的请求");
			return;
		}
		if (StringUtil.isNull(adminAccount)){
			message(request, response, "请输入券商的二级管理员账号");
			return;
		}
		UserCharacter target = CTRL.searchByAccountOrNickName(adminAccount);
		if (target == null){
			message(request, response, "二级管理员账号不存在");
			return;
		}
		if (StringUtil.isNull(name)){
			message(request, response, "请输入券商名称");
			return;
		}
		if (CTRL.checkBroker(name)){
			message(request,response, name + "已添过了");
			return;
		}
		Broker broker = new Broker();
		long bid = PK.key("broker");
		broker.setId(bid);
		broker.setName(name);
		if (!StringUtil.isNull(des)){
			broker.setDes(des);
		}
		CTRL.addBroker(broker);
		broker.save();
		message(request,response,"ok");
	}
}
