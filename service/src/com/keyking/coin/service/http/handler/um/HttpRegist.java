package com.keyking.coin.service.http.handler.um;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;
import com.keyking.coin.util.TimeUtils;

public class HttpRegist extends HttpHandler {
	//http://139.196.30.53:32104/um/HttpRegist?account=x&pwd=x&code=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("account");
		String pwd     = request.getParameter("pwd");
		String code    = request.getParameter("code");
		int codeResult = TOKEN.check(account,code);
		if (codeResult == 1){
			message(request,response,"验证码错误");
			return;
		}else if (codeResult == 2){
			message(request,response,"验证码已失效");
			return;
		}
		String result = CTRL.checkHttpAccout(account,null);
		if (result == null){
			UserCharacter user = new UserCharacter();
			user.setAccount(account);
			user.setPwd(pwd);
			CTRL.register(user);
			user.setNikeName("um_" + user.getId());
			user.save();
			message(request,response,"ok");
			ServerLog.info(account + " regist at " + TimeUtils.nowChStr());
		}else{
			message(request,response,result);
		}
		
	}

}
