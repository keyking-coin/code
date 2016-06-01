package com.keyking.coin.service.http.handler.um;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyking.coin.service.domain.broker.Broker;
import com.keyking.coin.service.domain.broker.UserBroker;
import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.JsonUtil;
import com.keyking.coin.util.ServerLog;

public class HttpLogin extends HttpHandler {
	//http://139.196.30.53:32104/um/HttpLogin?account=x&pwd=x
	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		String account   = request.getParameter("account");
		String pwd       = request.getParameter("pwd");
		UserCharacter user = CTRL.search(account);
		if (user != null){
			if (user.getPwd().equals(pwd)){
				Map<String,Object> datas = new HashMap<String,Object>();
				datas.put("result","ok");
				datas.put("ut",user.getPermission().getType());
				if (user.getPermission().getType() == PermissionType.umadmin){
					List<UserBroker> ubs = CTRL.searchUserBrokers(user);
					datas.put("ubs",ubs);
				}else if (user.getPermission().getType() == PermissionType.umuer){
					List<Broker> bs = CTRL.searchBrokers(user);
					datas.put("bs",bs);
				}
				String str = JsonUtil.ObjectToJsonString(datas);
				response.appendBody(formatJosn(request,str));
				ServerLog.info(account + " login um");
			}else{
				message(request,response,"密码错误");
			}
		}else{
			message(request, response,"账号不存在");
		}
	}
}
