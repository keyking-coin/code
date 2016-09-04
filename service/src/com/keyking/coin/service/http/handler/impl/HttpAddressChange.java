package com.keyking.coin.service.http.handler.impl;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.StringUtil;

public class HttpAddressChange extends HttpHandler {
	//http://139.196.30.53:32104/HttpAddressChange?uid=x&pwd=x&flag=0&address=xxxx
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		long uid       = Long.parseLong(request.getParameter("uid"));//我的编号
		String pwd     = request.getParameter("pwd");//验证密码
		String flag    = request.getParameter("flag");//0添加,1删除
		String address = request.getParameter("address");//地址
		address = address.replaceAll("　", " ");
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
			synchronized (user) {
				if (flag.equals("0")){
					if (StringUtil.isNull(address)){
						message(request,response,"地址不能为空");
						return;
					}
					if (!user.addAddress(address)){
						message(request,response,"此地址已在列表");
					}else{
						message(request,response,"ok");
					}
				}else{
					if (StringUtil.isNull(address)){
						message(request,response,"不能删除空的地址");
						return;
					}
					if (!user.removeAddress(address)){
						user.save();
						message(request,response,"在列表中找不到要删除的地址");
					}else{
						message(request,response,"ok");
					}
				}
				user.save();
			}
		}
	}

}
