package com.keyking.coin.service.http.handler.gm;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.SellerApprove;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;

public class HttpGetApprove extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			long id = Long.parseLong(request.getParameter("id"));
			UserCharacter user = CTRL.search(id);
			SellerApprove approve = null;
			if (user != null){
				if (user.getPermission().getType().ordinal() >= PermissionType.buyer.ordinal()){
					if (user.getSeller() != null && !user.getSeller().isPass()){
						approve = new SellerApprove(user);
					}
				}
			}
			if (approve != null){
				response.put("approve",approve);
				response.put("result","ok");
			}else{
				response.put("result","找不到认证数据");
			}
		} catch (Exception e) {
			response.put("result","获取认证数据异常");
			ServerLog.error("获取认证数据异常",e);
		}
	}

}
