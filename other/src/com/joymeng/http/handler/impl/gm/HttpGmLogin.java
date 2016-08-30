package com.joymeng.http.handler.impl.gm;

import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.slg.ServiceApp;

public class HttpGmLogin extends HttpHandler {
	
	@Override
	public boolean handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		if (ServiceApp.FREEZE){
			message(response,"服务器已关闭");
			return false;
		}
		String account = request.getParameter("account");
		String pwd     = request.getParameter("pwd");
		if (account.equals("admin") && pwd.equals("admin.nb")){
			response.appendBody("{\"result\":\"ok\"}");
			return true;
		}else{
			response.appendBody("{\"result\":\"用户名或密码不对\"}");
			return false;
		}
	}
}
