package com.keyking.coin.service.http.handler.gm;

import java.util.List;

import com.keyking.coin.service.domain.user.UserCharacter;
import com.keyking.coin.service.http.handler.HttpHandler;
import com.keyking.coin.service.http.request.HttpRequestMessage;
import com.keyking.coin.service.http.response.HttpResponseMessage;
import com.keyking.coin.util.TimeUtils;

public class HttpGmSendEamil extends HttpHandler {

	@Override
	public void handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setContentType("text/plain");
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		final String type = request.getParameter("type");
		final String target = request.getParameter("target");
		final String title = request.getParameter("title");
		final String content = request.getParameter("content");
		final UserCharacter gm = CTRL.search(1);
		final String time = TimeUtils.nowChStr();
		if (type.equals("2")){
			UserCharacter tar = CTRL.searchByAccountOrNickName(target);
			CTRL.tryToSendEmailToUser(gm, time,title, content,tar);
		}else{
			new Thread(){
				@Override
				public void run() {
					List<UserCharacter> targets = CTRL.getCoinUsers(Integer.parseInt(type));
					for (int i = 0 ; i < targets.size() ; i++){
						UserCharacter tar = targets.get(i);
						CTRL.tryToSendEmailToUser(gm,time,title,content,tar);
					}
				}
			}.start();
		}
		response.put("result","ok");
	}
}
