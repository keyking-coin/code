package com.keyking.coin.service.http.handler.um;

import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpToken extends HttpHandler {
	//http://139.196.30.53:32104/um/HttpToken?tel=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String key    = request.getParameter("tel");
		String code = TOKEN.check(key);
		if (code == null){
			code = TOKEN.create(key);
		}
		if (!SMS.couldSend(key)){
			message(request,response,"近期已发送您验证码,请稍候");
			return;
		}
		if (SMS.sendToken(key,code)){
			message(request,response,"ok");
		}else{
			message(request,response,"系统错误 ");
		}
	}

}
