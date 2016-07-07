package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;

public class HttpLogin extends HttpHandler {
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account = request.getParameter("account");  
		String pwd     = request.getParameter("pwd");
        UserCharacter user = CTRL.search(account);
        if (user != null){
        	if (user.getPwd().equals(pwd)){
        		if (user.getPermission().admin()){
        			response.put("result","ok");
        		}else{
        			response.put("result","非管理员账号登录");
        		}
        	}else{
        		response.put("result","密码错误");
        	}
        }else{
        	response.put("result",account + "未注册");
        }
	}
}
