package com.joymeng.http.handler.impl.gm;

import com.joymeng.http.handler.HttpHandler;
import com.joymeng.http.request.HttpRequestMessage;
import com.joymeng.http.response.HttpResponseMessage;
import com.joymeng.slg.ServiceApp;

public class HttpDrawMap extends HttpHandler {
	@Override
	public boolean handle(HttpRequestMessage request, HttpResponseMessage response) {
		response.setResponseCode(HttpResponseMessage.HTTP_STATUS_SUCCESS);
		if (ServiceApp.FREEZE){
			message(response,"服务器已关闭");
			return false;
		}
		if (mapWorld.drawMapImage(request.getParameters())){
			message(response,"ok");
		}
		return false;
	}
}
