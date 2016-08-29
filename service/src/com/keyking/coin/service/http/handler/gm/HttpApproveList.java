package com.keyking.coin.service.http.handler.gm;

import java.util.ArrayList;
import java.util.List;

import com.keyking.coin.service.domain.user.PermissionType;
import com.keyking.coin.service.domain.user.SellerApprove;
import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.ServerLog;

public class HttpApproveList extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		try {
			List<UserCharacter> users = CTRL.getUsers();
			List<SellerApprove> sellers = new ArrayList<SellerApprove>();
			for (int i = 0 ; i < users.size() ; i++){
				UserCharacter user = users.get(i);
				if (user.getPermission().getType().ordinal() >= PermissionType.buyer.ordinal()){
					if (user.getSeller() != null && !user.getSeller().isPass()){
						SellerApprove sa = new SellerApprove(user);
						sellers.add(sa);
					}
				}
			}
			response.put("list",sellers);
			response.put("result","ok");
		} catch (Exception e) {
			response.put("result","fail");
			ServerLog.error("获取用户列表异常",e);
		}
	}
}
