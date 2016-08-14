package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpCharge extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account  = request.getParameter("account");
		String nikeName = request.getParameter("nikeName");
		String token    = request.getParameter("token");
		String num      = request.getParameter("num");
		if (!token.equals("521uu.sys")){
			response.put("result","口令错误");
			return;
		}
		UserCharacter target = CTRL.search(account);
		if (target == null){
			response.put("result","不存在的用户");
			return;
		}
		if (!target.getNikeName().equals(nikeName)){
			response.put("result","用户昵称不对");
			return;
		}
		int charge = Integer.parseInt(num);
		target.getRecharge().changeMoney(charge);
		target.save();
		response.put("result","充值成功");
	}

}
